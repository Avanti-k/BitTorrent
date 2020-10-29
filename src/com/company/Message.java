package com.company;

public class Message {

    private int messageLength;
    private byte messageType; // TODO make byte sized enum
    private byte[] messagePayload;
    private byte[] message;
    public Message(byte[] input){
        byte[] lenByte = new byte[4];

        int i = 0;
        int index = 0;
        for(; i < 4; i++){
            lenByte[index] = input[i];
            index++;
        }

        index = 0;
        byte typeByte = input[i];
        i++;

        messagePayload = new byte[input.length - (lenByte.length + 1)];
        for(; i < input.length; i++){
            messagePayload[index] = input[i];
        }

        messageLength = Util.convertBytetoInt(lenByte);
        messageType = typeByte;
        this.message = input;
    }

    public Message(byte[] messagePayload, byte type){
        this.messageType = type;
        this.messagePayload = messagePayload;
        this.messageLength = 1 + messagePayload.length;
        byte[] lenByte = Util.convertIntToByte(messageLength);

        message = new byte[5 + messagePayload.length];
        int i = 0;
        int index = 0;
        for(; i < lenByte.length; i++){
            message[i] = lenByte[index];
            index++;
        }
        index = 0;
        message[i] = type;
        i++;

        for(; i < message.length; i++){
            message[i] = messagePayload[index];
            index++;
        }



    }

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

    public byte[] getMessage() {
        return message;
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
