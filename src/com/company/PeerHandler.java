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

    // STUB functions for state machine.

    public boolean checkIfInterested_bitfield(){
        // called when bitfield is received from peer
        // if this peer has needed pieces send "interested msg"
        // else send 'not interested msg
        return true;
    }

    // Can be combined with above method later
    public boolean checkIfInterested_Have(){
        // called after receiving a 'have' msg from peer.
        // check if the piece ID received is needed by you.
        // if so send 'interested' msg else 'not interested'
        return true;
    }

    /* Checks if the Peer is valid one and return bool accordingly */
    public boolean checkHandshakeHeader(HandshakeMessage msg){
        // TODO
        // Check if header is correct string
        // check if peer ID is the correct one

        return true;
    }

    public void receiveHandshakeMsg(){
        // receive HS msg and check the headers
        // to validate the peer
    }
    public void sendHandshakeMsg(){
        HandshakeMessage handshakeMessage = new HandshakeMessage(1); // hardcoded peer ID
        byte[] handshakeMessageInBytes = handshakeMessage.getMessage();
        // TODO send byteMessage over TCP socket here
    }

    /* Sends the nodes bitfield to peer at time of handshake */
    public void sendBitfieldMsg(){
        byte[] bitfield =  parent.getMyBitfield();
        // just for having common menthod converting bitfied to int here
        int bitFieldInt = Util.convertBytetoInt(bitfield);
        BitfieldMessage bitfieldMessage = new BitfieldMessage(bitFieldInt);
        byte[] bitfieldMessageInBytes = bitfieldMessage.getMessage();
        // TODO send over TCP
    }

    public void sendInterestedMsg(){
        InterestedMessage interestedMessage = new InterestedMessage();
        byte[] interestedMessageInBytes = interestedMessage.getMessage();
        // SEND over tcp
    }

    public void sendNotInterestedMsg(){
        NotInterestedMessage notInterestedMessage = new NotInterestedMessage();
        byte[] notInterestedMessageInBytes = notInterestedMessage.getMessage();
        // SEND over tcp
    }

    public void sendHaveMsg(){
        int pieceID = 1; // For now hardcoding.
        HaveMessage haveMessage = new HaveMessage(pieceID);
        byte[] haveMessageInBytes = haveMessage.getMessage();
        // Send over TCP

    }

    private void sendRequestMsg(){
        // maybe use a timeout here for corner case
        // A requests a piece but get choked before receiving result
        int pieceID = 1; // For now hardcoding.
        RequestMessage requestMessage = new RequestMessage(pieceID);
        byte[] requestMessageInBytes = requestMessage.getMessage();
        // send over TCP
    }

    public void sendChokeMsg(){
        // send choke and stop sending pieces
        ChokeMessage chokeMessage = new ChokeMessage();
        byte[] chokeMessageInBytes = chokeMessage.getMessage();
    }

    public void sendUnchokedMsg(){
        UnchokeMessage unchokeMessage = new UnchokeMessage();
        byte[] unchokeMessageInBytes = unchokeMessage.getMessage();

    }

    public void sendPieceMsg(){
        int pieceIdD = 1; // for now hardcoded
        byte[] pieceContent = "DUMMY\tCONTENT".getBytes();
        PieceMessage pieceMessage = new PieceMessage( pieceIdD, pieceContent);
    }

    public void receivedHaveMsg(){
        // check and update peer's bitfield
        // send 'interested' msg to NB
    }

    public void receiveBitfield(){
        // TODO
        // receive bitfield msg here and update Node's bitfield.
        //send 'interested' msgs to the sender
    }

    public void receivedInterestedMsg(){
        // update interested peer array of parent node here

    }

    public void receivedNotInterested(){
        // if this peer was previously in interested array
        // remove it
    }
    public void receiveChokeMsg(){
        // dont transmit
    }

    public void receiveUnchokeMsg(){
        // find out required piece and begin transmitting.
        // send 'request' msg here
    }

    public void receiveRequestMsg(){
        // check the requested piece id
        // if you have it send 'piece msg' with payload
    }

    public void receivePieceMsg(){
        // download the piece
        // TODO check if piece is received in multiple msgs or just one piece msg
        // update parents bit field on completion
        // call 'completePieceReceived'
        // check next piece of interest as well, and if so send 'request' else
        // send 'not interested'
    }
    public void completePieceReceived(){
        // update parents bitfield and all peers bitfield
        // send 'not interested' msg if needed to any peer from here

    }

}
