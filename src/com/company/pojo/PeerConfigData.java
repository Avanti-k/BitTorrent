package com.company.pojo;

public class PeerConfigData {
    int peerId;
    String  address;
    int portNumber;
    boolean isFileComplete;

    public int getPeerId() {
        return peerId;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public String getAddress() {
        return address;
    }
    public boolean getIsFileComplete(){
        return isFileComplete;
    }

    public void setAddress(String address) {

        this.address = address;
    }

    public void setFileComplete(boolean fileComplete) {
        isFileComplete = fileComplete;
    }

    public void setPeerId(int peerId) {
        this.peerId = peerId;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }
}
