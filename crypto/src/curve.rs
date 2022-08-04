use elliptic_curve::bigint::U256;

/// brainpoolP256r1 elliptic curve: verifiably pseudo-random variant
#[derive(Copy, Clone, Debug, Default, Eq, PartialEq, PartialOrd, Ord)]
pub struct StarkWareCurve;

impl elliptic_curve::Curve for StarkWareCurve {
    /// 256-bit field modulus
    type UInt = U256;

    /// Curve order
    const ORDER: U256 =
        U256::from_be_hex("800000000000010ffffffffffffffffb781126dcae7b2321e66a241adc64d2f");
}

impl elliptic_curve::PrimeCurve for StarkWareCurve {}

impl elliptic_curve::PointCompression for StarkWareCurve {
    const COMPRESS_POINTS: bool = false;
}

/// StarkWareCurve field element serialized as bytes.
///
/// Byte array containing a serialized field element value (base field or scalar).
pub type FieldBytes = elliptic_curve::FieldBytes<StarkWareCurve>;

/// StarkWareCurve secret key.
pub type SecretKey = elliptic_curve::SecretKey<StarkWareCurve>;
