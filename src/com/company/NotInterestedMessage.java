package com.company;

public class NotInterestedMessage extends Message {
    public NotInterestedMessage(byte[] input) {
        super(input);
    }
    public NotInterestedMessage(){
        super( Constants.NOT_INTERESTED);
    }
}
