package com.company;

import java.net.Socket;
import java.util.*;

// Class to maintain each neighbour of a node
// Each peer will be a thread or something
public class Peer {
    private int portNo;
    private String ipAddress;
    private int peerId;
    private Socket connection;
    private byte[] bitfield;

    Boolean unchoked = false; // maintain current status of that Neighbour
    // getters
    public int getPortNo(){
        return this.portNo;
    }

    public int getPeerId(){
        return this.peerId;
    }

    public String getIpAddress(){
        return this.ipAddress;
    }

    public Socket getConnection(){
        return this.connection;
    }

    public byte[] getBitfield(){
        return this.bitfield;
    }

    // setters


    public void setBitfield(byte[] bitfield) {
        this.bitfield = bitfield;
    }

    public void setConnection(Socket connection) {
        this.connection = connection;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setPeerId(int peerId) {
        this.peerId = peerId;
    }

    public void setPortNo(int portNo) {
        this.portNo = portNo;
    }

}
