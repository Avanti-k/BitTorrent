package com.company;

public class InterestedMessage extends Message {
    // byte to object
    public InterestedMessage(byte[] input) {
        super(input);
    }
    // used to create message object from parameters
    public InterestedMessage(){
        super(Constants.INTERESTED);
    }
}
