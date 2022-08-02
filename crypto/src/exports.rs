#[repr(C)]
pub struct BigInt {
    pub value: *mut u8,

    pub len: u32,

    // The number base to parse the value as.
    pub base: u8,
}

#[repr(C)]
pub struct ECDocument {
    pub msg_hash: BigInt,
    pub private_key: BigInt,
    pub seed: *const BigInt,
}

#[repr(C)]
pub struct ECSignature {
    pub r: BigInt,
    pub s: BigInt,
}

#[repr(C)]
pub struct Error {
    pub msg: *mut u8,
    pub len: u32,
}

#[no_mangle]
pub unsafe extern "C" fn sign(
    _document: *const ECDocument,
    _signature: *mut ECSignature,
    _err_msg: *mut Error,
) -> i32 {
    return 0;
}
