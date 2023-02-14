package com.reddio.api.v1.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RecordStatus {
    SubmittedToReddio(0),
    AcceptedByReddio(1),
    FailedOnReddio(2),
    AcceptedOnL2(3),
    RejectedOnL2(4),
    Rolled(5),
    AcceptedOnL1(6),
    ;
    private int value;

    RecordStatus(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    @JsonCreator
    public static RecordStatus fromValue(int value) {
        for (RecordStatus recordStatus : RecordStatus.values()) {
            if (recordStatus.value == value) {
                return recordStatus;
            }
        }
        return null;
    }
}
