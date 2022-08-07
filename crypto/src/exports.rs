use std::ffi::{CStr, CString};
use std::os::raw::c_char;
use std::ptr::null;

use starknet_crypto::{rfc6979_generate_k, FieldElement};

// a hex-based big int representation
pub type BigInt = *const c_char;

#[repr(C)]
pub struct ECDocument {
    pub msg_hash: BigInt,
    pub private_key: BigInt,
    pub seed: BigInt,
}

#[repr(C)]
pub struct ECSignature {
    pub r: BigInt,
    pub s: BigInt,
    pub err: *const c_char,
}

unsafe fn parse_bigint(i: BigInt) -> anyhow::Result<FieldElement> {
    if i.is_null() {
        return Err(anyhow::anyhow!("bigint cannot be null"));
    }
    let s = CStr::from_ptr(i).to_str()?;
    Ok(FieldElement::from_hex_be(s)?)
}

fn to_bigint(field: &FieldElement) -> anyhow::Result<BigInt> {
    let s = CString::new(format!("{field:x}"))?;
    Ok(s.into_raw())
}

#[no_mangle]
pub unsafe extern "C" fn sign(document: ECDocument) -> ECSignature {
    let sign_impl = move || {
        let msg = parse_bigint(document.msg_hash)?;
        let pk = parse_bigint(document.private_key)?;
        let k = rfc6979_generate_k(
            &msg,
            &pk,
            parse_bigint(document.msg_hash).ok().as_ref(), // seed can be null
        );
        let sig = starknet_crypto::sign(&pk, &msg, &k)?;
        Ok::<_, anyhow::Error>((to_bigint(&sig.r)?, to_bigint(&sig.s)?))
    };
    match sign_impl() {
        Err(e) => ECSignature::err(e.to_string()),
        Ok((r, s)) => ECSignature::new(r, s),
    }
}

impl ECSignature {
    fn new(r: BigInt, s: BigInt) -> Self {
        Self { r, s, err: null() }
    }

    fn err(err: impl Into<Vec<u8>>) -> Self {
        Self {
            r: null(),
            s: null(),
            err: CString::new(err).expect("err to cstr").into_raw(),
        }
    }
}
