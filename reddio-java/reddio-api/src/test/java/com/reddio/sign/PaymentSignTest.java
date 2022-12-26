package com.reddio.sign;

import org.junit.Assert;
import org.junit.Test;

public class PaymentSignTest {
    @Test
    public void testVerify() {
        boolean actual = PaymentSign.verify(
                "123456789",
                "",
                6,
                "0x79006ea58e526cadd243657801b3e66097f5e501b746e8c0116d1bbf74f3ee6",
                "0xc74ef49c7713c67c555b9482ab7ebb6e0dc5e1e177e31180e7f01aacaf5201",
                "0x44d7412b1d9910da2293b0825665be3ec41addf94408c0e8e5db3974ef0f099"
        );
        Assert.assertEquals(true, actual);
    }
}