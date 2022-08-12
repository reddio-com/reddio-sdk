use std::ffi::{CStr, CString};
use std::os::raw::c_char;
use std::ptr::{copy, write_bytes};

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
