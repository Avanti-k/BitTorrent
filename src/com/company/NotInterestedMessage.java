package com.company;

public class NotInterestedMessage extends Message {
    // this will not have payload either
    public NotInterestedMessage createNotInterestedMsg(){
        NotInterestedMessage notInterestedMessage = new NotInterestedMessage();
        return notInterestedMessage;
    }
}
