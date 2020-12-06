package com.company;

import java.awt.desktop.SystemEventListener;
import java.net.Socket;
import java.io.*;

public class PeerHandler extends Thread {

    Peer peer;
    private Socket connection;
    Node parent;            // parent node= actual node
    private ObjectInputStream in;	//stream read from the socket
    private ObjectOutputStream out;
    String message;
    String MESSAGE;
    PeerHandler(Socket clientSocket, Node parent){
        this.parent = parent;
        this.connection = clientSocket;
    }

    //TODO
    // Write State machine and corresponding functions here to handle one connection

    public void run(){
        System.out.println("One peer Handler thread started..");
        try {
            while (true) {
                //receive the message sent from the client
                message = (String)in.readObject();
                //show the message to the user
                System.out.println("Receive message: " + message + " from client " + peer.getPeerId());
                //Capitalize all letters in the message
                MESSAGE = message.toUpperCase();
                //send MESSAGE back to the client
                sendMessage(MESSAGE);
            }
        }
        catch(IOException | ClassNotFoundException ioException){
            ioException.printStackTrace();
        }
    }

    void sendMessage(String msg)
    {
        try{
            //stream write the message
            out.writeObject(msg);
            out.flush();
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
    }

    // STUB functions for state machine.
    public boolean checkIfInterested_bitfield(BitfieldMessage msg){
        // called when bitfield is received from peer
        //if the sender has pieces receiver doesnot have - send interested msg
        //else - send not interested msg
        byte[] tempBitField = new byte[2];
        int res = parent.getMyBitfield()[0] | msg.getBitfield()[0];
        tempBitField[0] = (byte)(res & 0xff);

        res = parent.getMyBitfield()[1] | msg.getBitfield()[1];
        tempBitField[1] = (byte)(res & 0xff);

        if(parent.getMyBitfield() == tempBitField) {
            return false;
        }
        return true;
    }

    // Can be combined with above method later
    public boolean checkIfHave(byte[] havepiece){
        // called after receiving a 'have' msg from peer.
        // check if the piece ID received is needed by you.
        // if so send 'interested' msg else 'not interested'
        byte[] tempBitField = new byte[2];
        int res = parent.getMyBitfield()[0] & havepiece[0];
        tempBitField[0] = (byte)(res & 0xff); // is it & when bitwise AND is used ?

        res = parent.getMyBitfield()[1] & havepiece[1];
        tempBitField[1] = (byte)(res & 0xff);

        if(havepiece != tempBitField) {
            return false;
        }
        return true;
    }

    /* Checks if the Peer is valid one and return bool accordingly */
    public boolean checkHandshakeHeader(HandshakeMessage msg){
        // TODO
        // Check if header is correct string
        //check peer ID is the expected one.
        if(msg.header != "P2PFILESHARINGPROJ" || msg.getPeerId() != parent.getexpectedpeerID())
        {
            return false;
        }
        return true;
    }

    public void receiveHandshakeMsg(){
        // receive HS msg and check the headers
        // to validate the peer
        // TODO receive msg over TCP in bytes
        byte[] rcvHSMessage = new byte[10]; // random assignment for compilation - variable to receive over tcp
        HandshakeMessage handshakemessage = new HandshakeMessage(rcvHSMessage);
        if(checkHandshakeHeader(handshakemessage))
        {
            sendBitfieldMsg();
        }
        else
        {
            //keep listening
        }
    }
    public void sendHandshakeMsg(){
        HandshakeMessage handshakeMessage = new HandshakeMessage(1); // hardcoded peer ID
        byte[] handshakeMessageInBytes = handshakeMessage.getMessage();
        parent.setexpectedpeerID(1); //hardcoded peer ID
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
        byte[] rcvHaveMsg = new byte[10];
        HaveMessage haveMessage = new HaveMessage(rcvHaveMsg);
        // ?? check and update peer's bitfield ??
        byte[] havepiece = parent.generateByteFromBinaryString(haveMessage.getPieceIndex());
        if(checkIfHave(havepiece))
        {
            sendInterestedMsg();
        }
        else
        {
            sendNotInterestedMsg();
        }
    }

    public void receiveBitfield(){
        // TODO
        //send 'interested' msgs to the sender
        byte[] rcvBitfield = new byte[10];
        BitfieldMessage bitfieldMessage = new BitfieldMessage(rcvBitfield);

        // receive bitfield msg here and update Node's bitfield. - ??
        parent.updateMyBitfiled(1);

        if(checkIfInterested_bitfield(bitfieldMessage))
        {
            sendInterestedMsg();
        }
        else
        {
            sendNotInterestedMsg();
        }
    }

    public void receivedInterestedMsg(){
        // receive msg over TCP in bytes
        byte[] rcvInterestedMsg = new byte[10]; // random assignment for compilation - variable to receive over tcp
        InterestedMessage interestedMessage = new InterestedMessage(rcvInterestedMsg);
        // update interested peer array of parent node here
        parent.addtointerestedPeerList(peer.getPeerId());
    }

    public void receivedNotInterested(){
        // receive msg over TCP in bytes
        byte[] rcvNotInterestedMsg = new byte[10]; // random assignment for compilation - variable to receive over tcp
        NotInterestedMessage notInterestedMessage = new NotInterestedMessage(rcvNotInterestedMsg);
        // if this peer was previously in interested array, remove it
        parent.removefrominterestedPeerList(peer.getPeerId());
    }

    public void receiveChokeMsg(){
        // dont transmit
        byte[] rcvChokeMsg = new byte[10];
        ChokeMessage chokeMessage = new ChokeMessage(rcvChokeMsg);
        //any blocking required?
    }

    public void receiveUnchokeMsg(){
        // find out required piece and begin transmitting.
        // send 'request' msg here
        byte[] rcvUnchokeMsg = new byte[10];
        UnchokeMessage unchokeMessage = new UnchokeMessage(rcvUnchokeMsg);
        sendRequestMsg();
    }

    public void receiveRequestMsg(){
        // check the requested piece id
        // if you have it send 'piece msg' with payload
        byte[] rcvRequestMsg = new byte[10];
        RequestMessage requestMessage = new RequestMessage(rcvRequestMsg);
        byte[] requestedpiece = parent.generateByteFromBinaryString(requestMessage.getPieceIndex());
        if(checkIfHave(requestedpiece))
        {
        //    sendPieceMsg(); with content (that piece)
        }
    }

    public void receivePieceMsg(){
        // download the piece
        // TODO check if piece is received in multiple msgs or just one piece msg
        // update parents bit field on completion
        // call 'completePieceReceived'
        // check next piece of interest as well, and if so send 'request' else
        // send 'not interested'
        byte[] rcvRequestMsg = new byte[10];
        PieceMessage pieceMessage = new PieceMessage(rcvRequestMsg);
        parent.updateMyBitfiled(1); //should be parent or peer ? peer right ?
        //if(any pieces needed) - keep a function to find the missing pieces in peer
        //and also it should not be requested if it has already been requested to someone else
        //{
            //sendRequestMsg();
        //}
        //else
        //{
            //sendNotInterestedMsg();
        //}
    }

    public void completePieceReceived(){
        // update parents bitfield and all peers bitfield
        // send 'not interested' msg if needed to any peer from here
        parent.setHasCompleteFile();
        parent.updateMyBitfiled(1);
        // ?? how to know to which all peer send 'not interested'
    }

}
