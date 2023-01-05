package com.reddio.crypto;

import com.sun.jna.Structure;

@Structure.FieldOrder({"order_id"})
public class CancelOrderMsg extends Structure implements Structure.ByValue {
    /**
     * decimal string, order id
     */
    public String order_id;
}
