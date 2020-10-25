package com.company;

public class ChokeMessage extends Message{
    public ChokeMessage createChokeMsg(){
        // this will have no payload
        ChokeMessage chokeMessage = new ChokeMessage();
        // fill message type and length here
        return chokeMessage;
    }

}
