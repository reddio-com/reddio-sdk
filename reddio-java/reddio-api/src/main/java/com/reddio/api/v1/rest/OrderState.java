package com.reddio.api.v1.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderState {
    Placed(0),
    Canceled(1),
    Filled(2),
    PartiallyFilled(3),
    ConditionallyCanceled(4),
    AllOrderState(5),
    DefaultOrderState(6),
    ;

    OrderState(int value) {
        this.value = value;
    }

    @JsonValue
    public int value;

    public int getValue() {
        return value;
    }

    @JsonCreator
    public static OrderState fromValue(int value) {
        for (OrderState orderState : OrderState.values()) {
            if (orderState.value == value) {
                return orderState;
            }
        }
        return null;
    }
}
