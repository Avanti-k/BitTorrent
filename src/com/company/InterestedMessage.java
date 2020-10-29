package com.company;

public class InterestedMessage extends Message {
    public InterestedMessage(byte[] input) {
        super(input);
    }
    public InterestedMessage(byte[] payLoad, boolean parse){
        super( payLoad, Constants.INTERESTED);
    }
}
