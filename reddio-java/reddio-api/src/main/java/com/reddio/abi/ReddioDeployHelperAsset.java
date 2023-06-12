package com.reddio.abi;

public enum ReddioDeployHelperAsset {
    ERC20(0),
    ERC721(1),
    ERC721Mintable(2),
    ERC721MintableCustomURI(3),
    ;

    private int value;

    ReddioDeployHelperAsset(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
