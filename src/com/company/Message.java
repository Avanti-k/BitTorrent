package com.company;

public class Message {

    private int messageLength;
    private byte messageType; // TODO make byte sized enum
    private byte[] messagePayload;

    // getters
    public int getMessageLength(){
        return this.messageLength;
    }
    public int getMessageType(){
        return this.messageType; // byte to int
    }
    public String getMessagePayload(){
        return new String(this.messagePayload);
    }

    // setters
    public void setMessageLength(int messageLength){
        this.messageLength = messageLength;
    }

    public void setMessageType(int messageType){
        this.messageType = (byte)messageType; // TODO use enum later
    }
    public void setMessagePayload(String payload){
        this.messagePayload = payload.getBytes();
    }

}
