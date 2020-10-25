package com.company;

public class InterestedMessage extends Message {
    public InterestedMessage createInteresed(){
        // this will also have no payload
        InterestedMessage interestedMessage = new InterestedMessage();
        return interestedMessage;
    }
}
