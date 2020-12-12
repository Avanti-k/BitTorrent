package com.company;

import com.company.filehandler.CommonConfigHandler;

public class BitfieldMessage extends Message{
    private byte[] bitfield; // 2 bytes I think

    // reads bytes from socket and form msg
    public BitfieldMessage(byte[] input) {
        super(input);
        bitfield = new byte[Util.convertNumBitsToNumBytes(CommonConfigHandler.getInstance().getProjectConfiguration().getNumChunks())];
        parsePayload();
    }
    public BitfieldMessage(byte[] bitfield, byte type){
        super( bitfield, Constants.BITFIELD);
        this.bitfield = bitfield;
        parsePayload();
    }

    /* Parses the payload into 2 byte Bitfield*/
    void parsePayload(){
        int i = 0;
        int index = 0;
        for(; i < bitfield.length; i++){
            this.bitfield[index] = this.messagePayload[i];
            index++;
        }
    }

    public byte[] getBitfield(){
        return this.bitfield;
    }
}
