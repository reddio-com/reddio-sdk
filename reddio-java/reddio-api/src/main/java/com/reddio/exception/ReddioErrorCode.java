package com.reddio.exception;

import lombok.Getter;

public enum ReddioErrorCode {
    Success(0),
    StarkKeyMissing(1),
    AmountInvalid(2),
    TokenIDMissing(3),
    TokenIDParseFailed(4),
    NOSuchAccountID(5),
    NoSuchAssetID(6),
    NoMintableToken(7),
    InsufficientAvailable(8),
    InsufficientFrozen(9),
    AddSequenceFailed(10),
    FailedToGenerateNonce(11),
    OrderFormatError(12),
    DuplicateTransactionError(13),
    FullWithdrawError(14),
    NotSuchContract(15),
    FailedToGenerateVaultID(16),
    CancelOrderWrongOwner(17),
    StarkKeyInvalid(18),
    InvalidParam(19),
    OrderConditionalCanceled(20),
    FOKAndIOCCanceled(21),
    TokenIDInvalid(22),
    MintAmountInvalid(23),
    DuplicateOrderInfoError(24),
    NotSuchToken(25),
    CanceledOrder(26),
    ContractAddressMissing(27),
    InvalidAPIKey(28),
    InvalidOwnerOfContract(29),
    SystemError(500);

    ReddioErrorCode(int code) {
        this.code = code;
    }

    @Getter
    private final int code;

    public static ReddioErrorCode fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ReddioErrorCode errorCode : ReddioErrorCode.values()) {
            if (errorCode.getCode() == code) {
                return errorCode;
            }
        }
        return null;
    }

}
