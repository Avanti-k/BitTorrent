package com.company;

public class UnchokeMessage extends Message {

    public UnchokeMessage createUnchokeMsg(){
        // this will not have payload, fill msg type and length
        UnchokeMessage unchokeMessage = new UnchokeMessage();
        return unchokeMessage;
    }
}
