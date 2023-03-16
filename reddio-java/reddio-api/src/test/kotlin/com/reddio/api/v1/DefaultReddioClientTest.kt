package com.reddio.api.v1

import com.reddio.api.v1.rest.Payment
import com.reddio.crypto.CryptoService
import com.reddio.sign.PaymentSHA3
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger

class DefaultReddioClientTest {
    @Test
    fun testGetSign() {
        val hash = PaymentSHA3.getPaymentHash(Payment.of(Payment.PayInfo.of("123456789"), ""), 2)
        Assert.assertEquals("4110a485d7032449867e24422205a845ca887a03a87a1742d691b35063a6a0a", hash.toString(16))

        val sign = CryptoService.sign(
            BigInteger(
                "0x1a35ffa8bafc5c6656271bcae1f847bb6201705d7e2895c413cfb7d757a3111".replace(
                    "0x", ""
                ).toLowerCase(), 16
            ), hash, null
        )
        Assert.assertEquals("a3791c02e33326210a4105acc0d6a3361e8724e71440ba003bdad504371e6c", sign.r)
        Assert.assertEquals("e898159dbd066df494fe425731dba63eba13d889d0fe0128ca0a42b98abc19", sign.s)
    }
}