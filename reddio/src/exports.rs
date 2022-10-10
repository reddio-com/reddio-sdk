use std::ffi::{CStr, CString};
use std::os::raw::c_char;
use std::ptr::{copy, write_bytes};
use std::str::FromStr;

use elliptic_curve::bigint::{Encoding, U256};
use rand::Rng;
use sha2::{Digest, Sha256};
use starknet_crypto::{rfc6979_generate_k, FieldElement};

use crate::errno::{Errno, Result};

// a hex-based big int representation
pub type BigInt = *const c_char;

pub type MutBigInt = *mut c_char;

pub const BIG_INT_SIZE: usize = 65;

#[repr(C)]
pub struct SignDocument {
    pub private_key: BigInt,
    pub msg_hash: BigInt,
    pub seed: BigInt,
}

#[repr(C)]
#[derive(Copy, Clone)]
pub struct SignResult {
    pub r: MutBigInt,
    pub s: MutBigInt,
}

#[repr(C)]
pub struct Signature {
    pub public_key: BigInt,
    pub msg_hash: BigInt,
    pub r: BigInt,
    pub s: BigInt,
}

pub unsafe fn parse_bigint(i: BigInt) -> Result<FieldElement> {
    if i.is_null() {
        return Err(Errno::InvalidNullPtr);
    }
    let s = CStr::from_ptr(i).to_str()?;
    Ok(FieldElement::from_hex_be(s)?)
}

pub unsafe fn write_bigint(field: &FieldElement, i: MutBigInt) {
    write_bytes(i, 0, BIG_INT_SIZE);
    let s = CString::new(format!("{field:x}")).expect("hex string cannot contain nul bytes");
    let len = s.as_bytes().len();
    copy(s.into_raw(), i, len);
}

#[no_mangle]
pub unsafe extern "C" fn sign(document: SignDocument, ret: SignResult) -> Errno {
    let sign_impl = move || {
        let msg = parse_bigint(document.msg_hash)?;
        let pk = parse_bigint(document.private_key)?;
        let k = rfc6979_generate_k(
            &msg,
            &pk,
            parse_bigint(document.seed).ok().as_ref(), // seed can be null
        );
        let sig = starknet_crypto::sign(&pk, &msg, &k)?;
        write_bigint(&sig.r, ret.r);
        write_bigint(&sig.s, ret.s);
        Ok::<_, Errno>(())
    };
    match sign_impl() {
        Err(e) => e,
        Ok(_) => Errno::Ok,
    }
}

#[no_mangle]
pub unsafe extern "C" fn verify(signature: Signature, valid: *mut bool) -> Errno {
    let verify_impl = move || {
        let msg = parse_bigint(signature.msg_hash)?;
        let pk = parse_bigint(signature.public_key)?;
        let r = parse_bigint(signature.r)?;
        let s = parse_bigint(signature.s)?;
        *valid = starknet_crypto::verify(&pk, &msg, &r, &s)?;
        Ok::<_, Errno>(())
    };
    match verify_impl() {
        Err(e) => e,
        Ok(_) => Errno::Ok,
    }
}

#[no_mangle]
pub unsafe extern "C" fn get_public_key(private_key: BigInt, public_key: MutBigInt) -> Errno {
    let get_public_key_impl = move || {
        let private = parse_bigint(private_key)?;
        let public = starknet_crypto::get_public_key(&private);
        write_bigint(&public, public_key);
        Ok::<_, Errno>(())
    };
    match get_public_key_impl() {
        Err(e) => e,
        Ok(_) => Errno::Ok,
    }
}

#[no_mangle]
pub unsafe extern "C" fn explain(errno: Errno) -> *const c_char {
    errno.static_reason().as_ptr() as _
}

#[no_mangle]
pub unsafe extern "C" fn get_private_key_from_eth_signature(
    eth_signature: *const c_char,
    private_key_str: *mut c_char,
) -> Errno {
    let eth_signature = match CStr::from_ptr(eth_signature as *const c_char).to_str() {
        Ok(s) => s,
        Err(_) => return Errno::InvalidStr,
    };
    let eth_signature_fixed = if let Some(eth_signature) = eth_signature.strip_prefix("0x") {
        &eth_signature[0..64]
    } else {
        &eth_signature[0..64]
    };

    match grind_key(eth_signature_fixed) {
        Ok(private_key) => {
            let private_key = format!("{private_key:x}");
            std::ptr::copy(
                private_key.as_ptr() as *const c_char,
                private_key_str,
                private_key.len(),
            );
            *(private_key_str.add(private_key.len())) = 0;

            Errno::Ok
        }
        Err(errno) => errno,
    }
}

#[no_mangle]
pub unsafe extern "C" fn get_random_private_key(private_key: MutBigInt) -> Errno {
    let get_random_private_key_impl = move || {
        let result = get_random_private_key_internal()?;
        write_bigint(&result, private_key);
        anyhow::Ok::<_>(())
    };

    match get_random_private_key_impl() {
        Err(e) => {
            // if e is Errno
            if let Ok(errno) = e.downcast::<Errno>() {
                errno
            } else {
                Errno::Unknown
            }
        }
        Ok(_) => Errno::Ok,
    }
}

/// reference: https://github.com/reddio-com/test-service/blame/main/script/generate_keys.py#L62-L64
fn get_random_private_key_internal() -> anyhow::Result<FieldElement> {
    let ec_order = num_bigint::BigUint::from_str(
        "3618502788666131213697322783095070105526743751716087489154079457884512865583",
    )?;
    let mut rng = rand::thread_rng();
    let mut number = num_bigint::BigUint::default();
    while !(number != num_bigint::BigUint::default() && number < ec_order) {
        number = rng.sample(num_bigint::RandomBits::new(256));
    }
    return Ok(biguint_to_field_element(number)?);
}

fn biguint_to_field_element(value: num_bigint::BigUint) -> anyhow::Result<FieldElement> {
    let mut unaligned_bytes = value.to_bytes_le();
    if unaligned_bytes.len() < 32 {
        for _ in 0..(32 - unaligned_bytes.len()) {
            unaligned_bytes.push(0);
        }
    }
    unaligned_bytes.reverse();
    let aligned_be_bytes = unaligned_bytes;
    let temp: [u8; 32] = aligned_be_bytes.as_slice().try_into().unwrap();
    let result = FieldElement::from_bytes_be(&temp).unwrap();
    return Ok(result);
}

fn grind_key(seed: &str) -> Result<FieldElement> {
    // the `key_val_limit` is hard coded to the `n` of the stark curve
    let key_val_limit =
        U256::from_be_hex("0800000000000010ffffffffffffffffb781126dcae7b2321e66a241adc64d2f");

    // the `max_allowed_val` is calculated through:
    //
    // ```javascript
    //  const sha256EcMaxDigest = new BN(
    //   '1 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000',
    //   16
    // );
    // const maxAllowedVal = sha256EcMaxDigest.sub(
    //   sha256EcMaxDigest.mod(keyValLimit)
    // );
    // ```
    let max_allowed_val =
        U256::from_be_hex("f80000000000020efffffffffffffff738a13b4b920e9411ae6da5f40b0358b1");

    let mut i = 0;
    let mut key = hash_key_with_index(seed, i)?;
    i += 1;
    while key >= max_allowed_val {
        key = hash_key_with_index(seed, i)?;
        i += 1;
    }

    // this option is none iff key_val_limit is zero
    let key = key.reduce(&key_val_limit).unwrap();

    // this result is err iff key is out of range
    // we have kept the key in range by `reduce` above
    Ok(FieldElement::from_bytes_be(&key.to_be_bytes()).unwrap())
}

fn hash_key_with_index(seed: &str, index: usize) -> Result<U256> {
    // seed is a hex string
    let mut tail_str = format!("{:x}", index);
    if tail_str.len() % 2 == 1 {
        tail_str = format!("0{}", tail_str);
    }

    let hash_str = format!("{}{}", seed, tail_str);
    // TODO: handle this error
    let hash_bytes = match hex::decode(hash_str) {
        Ok(bytes) => bytes,
        Err(_) => return Err(Errno::InvalidHex),
    };

    let mut hasher = Sha256::new();
    hasher.update(&hash_bytes);
    let hash_result: [u8; 32] = hasher
        .finalize()
        .as_slice()
        .try_into()
        .expect("hash result is not 32 bytes");

    Ok(U256::from_be_bytes(hash_result))
}
#[cfg(test)]
mod tests {
    use std::ffi::{c_char, CStr, CString};
    use std::ptr::null;
    use std::str::FromStr;

    use super::{
        biguint_to_field_element, get_random_private_key_internal, sign, SignDocument, SignResult,
    };
    use crate::exports::BIG_INT_SIZE;

    #[test]
    fn test_sign_1() -> anyhow::Result<()> {
        let actual = SignResult {
            r: ([0 as c_char; BIG_INT_SIZE]).as_mut_ptr(),
            s: ([0 as c_char; BIG_INT_SIZE]).as_mut_ptr(),
        };
        unsafe {
            let errno = sign(
                SignDocument {
                    msg_hash: CString::new(
                        "0x397e76d1667c4454bfb83514e120583af836f8e32a516765497823eabe16a3f",
                    )
                    .unwrap()
                    .into_raw(),
                    private_key: CString::new(
                        "0x3c1e9550e66958296d11b60f8e8e7a7ad990d07fa65d5f7652c4a6c87d4e3cc",
                    )
                    .unwrap()
                    .into_raw(),
                    seed: null(),
                },
                actual,
            );
            assert_eq!(errno as u8, 0);

            let actual_r = CStr::from_ptr(actual.r).to_str()?;
            let actual_s = CStr::from_ptr(actual.s).to_str()?;
            assert_eq!(
                actual_r,
                "173fd03d8b008ee7432977ac27d1e9d1a1f6c98b1a2f05fa84a21c84c44e882"
            );
            assert_eq!(
                actual_s,
                "4b6d75385aed025aa222f28a0adc6d58db78ff17e51c3f59e259b131cd5a1cc"
            );
        }
        Ok(())
    }

    #[test]
    fn test_sign_2() -> anyhow::Result<()> {
        let actual = SignResult {
            r: ([0 as c_char; BIG_INT_SIZE]).as_mut_ptr(),
            s: ([0 as c_char; BIG_INT_SIZE]).as_mut_ptr(),
        };
        unsafe {
            let errno = sign(
                SignDocument {
                    msg_hash: CString::new(
                        "0x6adb14408452ede28b89f40ca1847eca4de6a2dd6eb2c7d6dc5584f9399586",
                    )
                    .unwrap()
                    .into_raw(),
                    private_key: CString::new(
                        "0x4c1e9550e66958296d11b60f8e8e7a7ad990d07fa65d5f7652c4a6c87d4e3cc",
                    )
                    .unwrap()
                    .into_raw(),
                    seed: null(),
                },
                actual,
            );
            assert_eq!(errno as u8, 0);

            let actual_r = CStr::from_ptr(actual.r).to_str()?;
            let actual_s = CStr::from_ptr(actual.s).to_str()?;
            assert_eq!(
                actual_r,
                "2ee2b8927122f93dd5fc07a11980f0fab4c8358e5d1306bfee5e095355d2ad0"
            );
            assert_eq!(
                actual_s,
                "64d393473af2ebab736c579ad511bf439263e4740f9ad299498bda2e75b0e9"
            );
        }
        Ok(())
    }

    #[test]
    fn test_random_private_key() -> anyhow::Result<()> {
        let random_private_key = get_random_private_key_internal()?;
        let public_key = starknet_crypto::get_public_key(&random_private_key);
        println!(
            "private key: {}, public key: {}",
            random_private_key, public_key
        );
        Ok(())
    }

    #[test]
    fn could_always_align_with_u256() -> anyhow::Result<()> {
        let number = num_bigint::BigUint::from_str("255")?;
        let field_element = biguint_to_field_element(number)?;
        assert_eq!(format!("{}", field_element), "255");
        Ok(())
    }
}
