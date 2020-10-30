package com.company;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class HandshakeMessage {
   // private byte[] handshakeHeader; // 18 bytes
    private byte[] zeroBits; // 10 bytes
    private int peerId; // 4 bytes
    public String header;
    private byte[] message;
    public HandshakeMessage(Byte[] input){

        byte[] peerByte = new byte[4];
        byte[] headerByte = new byte[18];
        byte[] zeroes = new byte[10];
        int i = 0;
        int index = 0;
        for(; i < headerByte.length; i++){
            headerByte[index] = input[i];
            index++;
        }
        index = 0;
        // BUG:  should be length + i;
        /// index increment
        for(; i < zeroes.length; i++){
            zeroes[index] = input[i];
        }
        index = 0;
        //BUG : Should be length + i
        for(; i < peerByte.length; i++){
            peerByte[index] = input[i];
            index++;
        }

        peerId =  Util.convertBytetoInt(peerByte);
        header = Util.convertByteToString(headerByte);
        zeroBits = zeroes;

    }
    public HandshakeMessage(int peerId){
        this.peerId = peerId;
        header = "P2PFILESHARINGPROJ";
        setMessageFromInt();

    }

    private void setMessageFromInt() {

        byte[] header = Util.convertStringToByte(this.header);
        byte[] zeroes = new byte[10];
        byte[] peerByte = Util.convertIntToByte(peerId);
        message = new byte[header.length + zeroes.length + peerByte.length];

        System.arraycopy(header,0,message,0,header.length);
        System.arraycopy(zeroes,0,message,header.length, zeroes.length);
        System.arraycopy(peerByte, 0, message, header.length + zeroes.length, peerByte.length);

    }




    public void setPeerId(int peerId) {
        this.peerId = peerId;
    }

    public int getPeerId() {
        return peerId;
    }

    public byte[] getMessage() {
        return message;
    }
    //    // Getters
//    public int getPeerId() {
//        return this.peerId;
//    }
//    public String getHandshakeHeader(){
//        return new String(this.handshakeHeader);
//    }
//
//    public byte[] getZeroBits(){
//        return this.zeroBits;
//    }
//
//    // setters
//    public void  setHandshakeHeader(String handshakeHeader){
//        this.handshakeHeader = handshakeHeader.getBytes();
//    }
//
//    public void setZeroBits(byte[]zeroBits){
//        this.zeroBits = zeroBits;
//    }
//
//    public void setPeerId(int peerId){
//        this.peerId = peerId;
//    }
}
