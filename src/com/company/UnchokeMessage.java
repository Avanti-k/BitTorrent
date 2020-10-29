package com.company;

public class UnchokeMessage extends Message {

    public UnchokeMessage(byte[] input) {
        super(input);
    }
    public UnchokeMessage(byte[] payLoad, boolean parse){
        super( payLoad, Constants.UNCHOKE);
    }
}
