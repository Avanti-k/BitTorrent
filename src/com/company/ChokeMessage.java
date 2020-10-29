package com.company;

public class ChokeMessage extends Message{
    public ChokeMessage(byte[] input) {
        super(input);
    }
    public ChokeMessage(byte[] payLoad, boolean parse){
        super( payLoad, Constants.CHOKE);
    }


}
