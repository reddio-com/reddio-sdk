use std::ffi::{CStr, CString};
use std::os::raw::c_char;
use std::ptr::{copy, write_bytes};

use elliptic_curve::bigint::{Encoding, SubMod, U256};
use elliptic_curve::Field;
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

unsafe fn parse_bigint(i: BigInt) -> Result<FieldElement> {
    if i.is_null() {
        return Err(Errno::InvalidNullPtr);
    }
    let s = CStr::from_ptr(i).to_str()?;
    Ok(FieldElement::from_hex_be(s)?)
}

unsafe fn write_bigint(field: &FieldElement, i: MutBigInt) {
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
    while (key >= max_allowed_val) {
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
