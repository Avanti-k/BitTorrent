package com.company;

public class BitfieldMessage extends Message{
    private byte[] bitfield; // 2 bytes I think

    // reads bytes from socket and form msg
    public BitfieldMessage(byte[] input) {
        super(input);
        parsePayload();
    }
    public BitfieldMessage(byte[] bitfield, byte type){
        super( bitfield, Constants.BITFIELD);
        parsePayload();
    }

    /* Parses the payload into 2 byte Bitfield*/
    void parsePayload(){
        int i = 0;
        int index = 0;
        for(; i < 2; i++){
            this.bitfield[index] = this.messagePayload[i];
            index++;
        }
    }

    public byte[] getBitfield(){
        return this.bitfield;
    }
}
