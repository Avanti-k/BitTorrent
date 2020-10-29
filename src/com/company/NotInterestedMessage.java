package com.company;

public class NotInterestedMessage extends Message {
    public NotInterestedMessage(byte[] input) {
        super(input);
    }
    public NotInterestedMessage(byte[] payLoad, boolean parse){
        super( payLoad, Constants.NOT_INTERESTED);
    }
}
