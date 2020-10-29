package com.company;

public class BitfieldMessage extends Message{
    private byte[] bitfield; // 2 bytes I think

    public BitfieldMessage(byte[] input) {
        super(input);
    }
    public BitfieldMessage(byte[] payLoad, boolean parse){
        super( payLoad, Constants.BITFIELD);
    }
}
