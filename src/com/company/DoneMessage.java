package com.company;

public class DoneMessage extends  Message{

    public DoneMessage(byte[] input) {
        super(input);
    }
    public DoneMessage(){ super( Constants.DONE);
    }
}
