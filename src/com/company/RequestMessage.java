package com.company;

public class RequestMessage extends Message {
    public RequestMessage(byte[] input) {
        super(input);
    }
    public RequestMessage(byte[] payLoad, boolean parse){
        super( payLoad, Constants.REQUEST);
    }
}
