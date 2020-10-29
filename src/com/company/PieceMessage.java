package com.company;

public class PieceMessage extends Message{
    public PieceMessage(byte[] input) {
        super(input);
    }
    public PieceMessage(byte[] payLoad, boolean parse){
        super( payLoad, Constants.PIECE);
    }

}
