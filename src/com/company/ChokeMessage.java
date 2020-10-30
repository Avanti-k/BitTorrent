package com.company;

public class ChokeMessage extends Message{
    public ChokeMessage(byte[] input) {
        super(input);
    }
    public ChokeMessage(){
        super(Constants.CHOKE);
    }

}
