use std::str::Utf8Error;

use starknet_crypto::{SignError, VerifyError};
use starknet_ff::{FromHexError, FromDecStrError};

#[repr(C)]
pub enum Errno {
    Ok,

    InvalidNullPtr,
    InvalidStr,
    InvalidHex,
    InvalidDecStr,
    InvalidMsg,
    InvalidR,
    InvalidS,

    InternelInvalidK,

    Unknown,
}

pub type Result<T> = std::result::Result<T, Errno>;

impl Errno {
    pub fn static_reason(&self) -> &'static str {
        match self {
            Self::Ok => "ok\0",
            Self::InvalidNullPtr => "pointer cannot be null\0",
            Self::InvalidStr => "not an invalid hex string\0",
            Self::InvalidDecStr => "not an invalid decimal string\0",
            Self::InvalidHex => "not an invalid hex number\0",
            Self::InvalidMsg => "not an invalid message hash\0",
            Self::InvalidR => "not an invalid 'r'\0",
            Self::InvalidS => "not an invalid 's'\0",
            Self::InternelInvalidK => "internal error: k is invalid\0",
            Self::Unknown => "unknown error\0",
        }
    }
}

impl From<Utf8Error> for Errno {
    fn from(_: Utf8Error) -> Self {
        Self::InvalidStr
    }
}

impl From<FromHexError> for Errno {
    fn from(_: FromHexError) -> Self {
        Self::InvalidHex
    }
}

impl From<FromDecStrError> for Errno{
    fn from(_: FromDecStrError) -> Self {
        Self::InvalidDecStr
    }
}

impl From<SignError> for Errno {
    fn from(e: SignError) -> Self {
        match e {
            SignError::InvalidMessageHash => Self::InvalidMsg,
            SignError::InvalidK => Self::InternelInvalidK,
        }
    }
}

impl From<VerifyError> for Errno {
    fn from(e: VerifyError) -> Self {
        match e {
            VerifyError::InvalidMessageHash => Self::InvalidMsg,
            VerifyError::InvalidR => Self::InvalidR,
            VerifyError::InvalidS => Self::InvalidS,
        }
    }
}

default impl<E: std::error::Error> From<E> for Errno {
    fn from(_: E) -> Self {
        Self::Unknown
    }
}
