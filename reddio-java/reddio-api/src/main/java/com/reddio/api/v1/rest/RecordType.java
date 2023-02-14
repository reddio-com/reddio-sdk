package com.reddio.api.v1.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RecordType {
    DepositRecordType(1),
    MintRecordType(2),
    TransferFromRecordType(3),
    WithdrawRecordType(4),
    ASKOrderRecordType(7),
    BIDOrderRecordType(8),
    ;
    private int value;

    RecordType(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    @JsonCreator
    public static RecordType fromValue(int value) {
        for (RecordType recordType : RecordType.values()) {
            if (recordType.value == value) {
                return recordType;
            }
        }
        return null;
    }
}
