package com.company;

public class HaveMessage  extends  Message{
    public HaveMessage(byte[] input) {
        super(input);
    }
    public HaveMessage(byte[] payLoad, boolean parse){
        super( payLoad, Constants.HAVE);
    }
}
