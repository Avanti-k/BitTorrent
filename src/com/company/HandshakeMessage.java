package com.company;

public class HandshakeMessage {
    private byte[] handshakeHeader; // 18 bytes
    private byte[] zeroBits; // 10 bytes
    private int peerId; // 4 bytes

    // Getters
    public int getPeerId() {
        return this.peerId;
    }
    public String getHandshakeHeader(){
        return new String(this.handshakeHeader);
    }

    public byte[] getZeroBits(){
        return this.zeroBits;
    }

    // setters
    public void  setHandshakeHeader(String handshakeHeader){
        this.handshakeHeader = handshakeHeader.getBytes();
    }

    public void setZeroBits(byte[]zeroBits){
        this.zeroBits = zeroBits;
    }

    public void setPeerId(int peerId){
        this.peerId = peerId;
    }
}
