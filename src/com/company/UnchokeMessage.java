package com.company;

public class UnchokeMessage extends Message {

    public UnchokeMessage(byte[] input) {
        super(input);
    }
    public UnchokeMessage(){
        super( Constants.UNCHOKE);
    }
}
