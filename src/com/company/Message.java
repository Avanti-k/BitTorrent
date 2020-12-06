package com.company;

public class Message {

    private int messageLength;
    private byte messageType; // TODO make byte sized enum
    protected byte[] messagePayload;
    private byte[] messageBytes; // entire msg packet in bytes


// converts bytes to message object
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
        this.messageBytes = input;
    }

    /* Needed for Piece Message where payload is pieceID and content
     */
    public Message(int pieceID, byte[] messagePayload, byte type){
        byte[] combinedPayload = combineAndFormPayload(pieceID, messagePayload);
        this.messageType = type;
        this.messagePayload = messagePayload;
        this.messageLength = 1 + messagePayload.length;
        byte[] lenByte = Util.convertIntToByte(messageLength);

        messageBytes = new byte[5 + messagePayload.length];
        int i = 0;
        int index = 0;
        for(; i < lenByte.length; i++){
            messageBytes[i] = lenByte[index];
            index++;
        }
        index = 0;
        messageBytes[i] = type;
        i++;

        for(; i < messageBytes.length; i++){
            messageBytes[i] = messagePayload[index];
            index++;
        }

    }

    /* Needed for Msgs with payload as piece index (Have /Request/ Bitfield)
     Have / Receive - payload is the piece Index
     Bitfield - payload is te 4 byte byte
     */
    public Message(int payload, byte type){
        this.messageType = type;
        this.messagePayload = Util.convertIntToByte(payload);
        this.messageLength = 1 + messagePayload.length;
        byte[] lenByte = Util.convertIntToByte(messageLength);

        messageBytes = new byte[5 + messagePayload.length];
        int i = 0;
        int index = 0;
        for(; i < lenByte.length; i++){
            messageBytes[i] = lenByte[index];
            index++;
        }
        index = 0;
        messageBytes[i] = type;
        i++;

        for(; i < messageBytes.length; i++){
            messageBytes[i] = messagePayload[index];
            index++;
        }

    }

    /* Needed for Messages without Payloads (Choke / Unchoke / Interested / Not Interested) */
    public Message(byte type){
        this.messageType = type;
        //this.messagePayload = messagePayload;
        this.messageLength = 1; // length of msg byte
        byte[] lenByte = Util.convertIntToByte(messageLength);

        messageBytes = new byte[5]; // 4 bytes length + 1 byte type
        int i = 0;
        int index = 0;
        for(; i < lenByte.length; i++){
            messageBytes[i] = lenByte[index];
            index++;
        }
        messageBytes[i] = type;

    }

    // Only needed for Piece Message Class
    private byte[] combineAndFormPayload(int pieceIndex, byte[] pieceContent){
        byte[] pieceIDBytes = Util.convertIntToByte(pieceIndex);
        byte[] combinedPayload = new byte[pieceIDBytes.length + pieceContent.length];
        System.arraycopy(pieceIDBytes,0,combinedPayload,0,pieceIDBytes.length);
        System.arraycopy(pieceContent,0,combinedPayload, pieceIDBytes.length, pieceContent.length);
        return  combinedPayload;
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
        return messageBytes;
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
