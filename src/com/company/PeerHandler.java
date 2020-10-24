package com.company;

import java.net.Socket;

public class PeerHandler extends Thread {

    Peer peer;
    private Socket connection;
    Node parent;            // parent node= actual node
    PeerHandler(Node parent){
        this.parent = parent;
    }

    //TODO
    // Write State machine and corresponding functions here to handle one connection


    // STUB functions

    /* Checks if the Peer is valid one and return bool accordingly */
    public boolean checkHandshakeHeader(HandshakeMessage msg){
        // TODO
        // Check if header is correct string
        // check if peer ID is the correct one

        return true;
    }

    public void sendHandshakeMessage(){
        //TODO send handshake msg here
    }

    /* Sends the nodes bitfield to peer at time of handshake */
    public void sendBitfieldMsg(){
        //TODO
        // wrap this.bitfied in Bitfield msg and send over TCP socket
        // corresponding to the peer.
        // parent.getMyBitfield
    }

    public void receiveBitfield(){
        // TODO
        // receive bitfield msg here and update Node's bitfield.
    }

    public void sendInterestedMsg(){

    }

    public void sendNotInterestedMsg(){

    }

    public void sendHaveMsg(){

    }

    private void sendRequestMsg(){

    }

    public void sendChokeMsg(){

    }

    public void sendUnchokedMsg(){

    }

    public void sendPieceMsg(){

    }

}
