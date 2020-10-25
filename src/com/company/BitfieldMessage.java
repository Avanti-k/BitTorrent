package com.company;

public class BitfieldMessage extends Message{
    private byte[] bitfield; // 2 bytes I think

    public BitfieldMessage(byte[] bitfield){
        this.bitfield = bitfield;
    }

    public BitfieldMessage createBitfieldMsg(byte[] bitfield){
        // TODO add remaining fields

        BitfieldMessage bitfieldMessage = new BitfieldMessage(bitfield);
        return bitfieldMessage;
    }
}
