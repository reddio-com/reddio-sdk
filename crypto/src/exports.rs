use std::os::raw::c_char;
use std::ptr::null;

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

#[no_mangle]
pub unsafe extern "C" fn sign(_document: ECDocument) -> ECSignature {
    return ECSignature::err("sign is not implemented".as_ptr());
}

impl ECSignature {
    fn new(r: BigInt, s: BigInt) -> Self {
        Self { r, s, err: null() }
    }

    fn err(err: *const u8) -> Self {
        Self {
            r: null(),
            s: null(),
            err: err as *const c_char,
        }
    }
}
