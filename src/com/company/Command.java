package com.company;

public class Command {

    private byte type;
    private int value;

    public Command(byte type, int value){
        this.type = type;
        this.value = value;
    }

    public byte getType() {
        return type;
    }

    public int getValue() {
        return value;
    }
}
