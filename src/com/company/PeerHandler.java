package com.company;

import java.net.*;
import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class PeerHandler extends Thread {

    private Peer peerConnected; // connected to
    private Socket connection;
    private Node parent;            // parent node= actual node
    private ObjectInputStream in;	//stream read from the socket
    private ObjectOutputStream out;
    private int clientNum;
    private boolean gotChoked = true;
    private boolean HSEstablished = false;
    private boolean amIInitiator;
    Queue<Command> commandQueue;

    String message;
    PeerHandler(Socket clientSocket, Node parent, boolean amIInitiator){
        this.parent = parent;
        this.connection = clientSocket;
        int someTimeout = 1000;
        this.amIInitiator = amIInitiator;
        commandQueue = new LinkedList<>();
    }

    public void run(){
        System.out.println(currentThread().getName() + " : peer Handler thread started.. Initiator =  " + amIInitiator
            + " started by thread = " + parent.selfPeer.getPeerId());
        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            out.flush();
            in = new ObjectInputStream(connection.getInputStream());

            if (amIInitiator) {
                System.out.println(currentThread().getName() + " Sending HS =  " + amIInitiator);

                sendHandshakeMsg(parent.selfPeer.getPeerId());
            }
            System.out.println(currentThread().getName() + " Before whiile =  " + amIInitiator);

            while (true) {
                try {
                    //receive the message sent from the client
                    // check in command queue if any command from node is received


                    if(commandQueue.size() != 0)
                    {
                        // Parent has sent choke/unchoke command
                        Command command = commandQueue.remove();
                        if (command.getType() == Constants.CHOKE)
                        {
                            sendChokeMsg();
                        }
                        else if (command.getType() == Constants.UNCHOKE)
                        {
                            sendUnchokedMsg();
                        }
                        else if(command.getType() == Constants.DONE){
                            sendDoneMsg();
                        }
                        else if (command.getType() == Constants.HAVE){
                            int pieceId = command.getValue();
                            sendHaveMsg(pieceId);
                        }
                    }

                    //System.out.println(currentThread().getName() + " Inside whiile =  " + amIInitiator);


                    if(true) {

                        //System.out.println(" Inside available =  " + currentThread().getName());
                        connection.setSoTimeout(1000);


                        byte[] messageInBytes = (byte[]) in.readObject();
                        //show the message to the user
                        System.out.println(currentThread().getName() + " :Receive message: " + messageInBytes + " from client ");

                        if (!HSEstablished) {
                            boolean isValid = receiveHandshakeMsg(messageInBytes);
                            if (isValid) {
                                if (!amIInitiator) {
                                    sendHandshakeMsg(parent.selfPeer.getPeerId());
                                }else{
                                    sendBitfieldMsg();
                                }
                                HSEstablished = true;
                                // TODO log connection established here
                            }
                        } else {
                            Message message = new Message(messageInBytes);
                            switch (message.getMessageType()) {
                                case Constants.CHOKE:
                                    receiveChokeMsg();
                                    break;
                                case Constants.UNCHOKE:
                                    receiveUnchokeMsg();
                                    break;
                                case Constants.INTERESTED:
                                    receivedInterestedMsg();
                                    break;
                                case Constants.NOT_INTERESTED:
                                    receivedNotInterested();
                                    break;
                                case Constants.HAVE:
                                    receivedHaveMsg(messageInBytes);
                                    break;
                                case Constants.BITFIELD:
                                    receiveBitfield(messageInBytes);
                                    break;
                                case Constants.REQUEST:
                                    receiveRequestMsg(messageInBytes);
                                    break;
                                case Constants.PIECE:
                                    receivePieceMsg(messageInBytes);
                                    break;
                                case Constants.DONE:
                                    receivedDoneMsg(messageInBytes);
                                    break;

                                default:
                                    System.out.println("\n INVALID MESSAGE TYPE RECEIVED");
                            }
                        } // end else
                    } // if available
                } catch (IOException ioException) {

                } catch (ClassNotFoundException e) {
                    //e.printStackTrace();
                }
                catch(Exception e){

                }

            } // end while
        }
        catch (ConnectException e) {
            System.err.println("Connection refused. You need to initiate a server first.");
        }

        catch(UnknownHostException unknownHost){
            System.err.println("You are trying to connect to an unknown host!");
        }
        catch(IOException ioException){
                ioException.printStackTrace();
        }

    }// receiveUnchokeMsg

    public void sendMessage(byte[] msg)
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

    //  functions for state machine.
    public boolean checkIfInterested_bitfield(byte[] peerBitfiled){
        // called when bitfield is received from peer
        List<Integer> interestingPieces = parent.myFileHandler.chunksIAmInterestedInFromPeer(peerBitfiled);
        if(interestingPieces.isEmpty()) {
            return false;
        }
        return true;
    }

    // Can be combined with above method later
    public boolean checkIfHave(int pieceId){
        // called after receiving a 'have' msg from peer.
        List<Integer> missingPiecesList = parent.myFileHandler.chunksIWant();
        if(missingPiecesList.contains(pieceId))
            return false;
        return true;// not in missing pieces so I already have it
    }

    /* Checks if the Peer is valid one and return bool accordingly */
    public boolean checkHandshakeHeader(HandshakeMessage msg){

        //check peer ID is the expected one.
        if(msg.header.compareTo("P2PFILESHARINGPROJ")  != 0)
        {
            return false;
        }
        return true;
    }

    public boolean receiveHandshakeMsg(byte[] msg){
        // receive HS msg and check the headers
        // to validate the peer
        HandshakeMessage handshakemessage = new HandshakeMessage(msg);
        if(checkHandshakeHeader(handshakemessage))
        {
            // if valid get peerId for this connection
            int peerIdReceived = handshakemessage.getPeerId();
            // This is the peer connected to via this handler.
            parent.updatePeerMap(peerIdReceived, this);
            this.peerConnected = parent.peerInfoHandler.getPeerHashMap().get(peerIdReceived);
            if(amIInitiator){
                parent.logger.writelog(1,peerConnected.getPeerId(),parent.selfId,0,0);
            }
            return true;
        }
        else
        {
            // invalid format keep listening
            return false;
        }
    }
    public void sendHandshakeMsg(int peerId){
        System.out.println(currentThread().getName() + " Sending HS =  " + amIInitiator + " to peerID = " + peerId);

        HandshakeMessage handshakeMessage = new HandshakeMessage(peerId); // hardcoded peer ID
        byte[] handshakeMessageInBytes = handshakeMessage.getMessage();
        sendMessage(handshakeMessageInBytes);
        if (!amIInitiator){
            parent.logger.writelog(2,peerConnected.getPeerId(),parent.selfId,0,0);

        }
    }

    /* Sends the nodes bitfield to peer at time of handshake */
    public void sendBitfieldMsg(){
        // Send parents bitfield to connected peer
        BitfieldMessage bitfieldMessage = new BitfieldMessage(parent.myFileHandler.getBitField(), Constants.BITFIELD);
        byte[] bitfieldMessageInBytes = bitfieldMessage.getMessage();
        // send over TCP
        sendMessage(bitfieldMessageInBytes);
    }

    /* Sends the nodes bitfield to peer at time of handshake */
    public void sendDoneMsg(){
        // Send parents bitfield to connected peer
        DoneMessage doneMessage = new DoneMessage();
        byte[] doneMsgInBytes = doneMessage.getMessage();
        // send over TCP
        sendMessage(doneMsgInBytes);
    }

    public void sendInterestedMsg(){
        InterestedMessage interestedMessage = new InterestedMessage();
        byte[] interestedMessageInBytes = interestedMessage.getMessage();
        // SEND over tcp
        sendMessage(interestedMessageInBytes);
    }

    public void sendNotInterestedMsg(){
        NotInterestedMessage notInterestedMessage = new NotInterestedMessage();
        byte[] notInterestedMessageInBytes = notInterestedMessage.getMessage();
        // SEND over tcp
        sendMessage(notInterestedMessageInBytes);
    }

    public void sendHaveMsg(int pieceId){
        HaveMessage haveMessage = new HaveMessage(pieceId);
        byte[] haveMessageInBytes = haveMessage.getMessage();
        // Send over TCP
        sendMessage(haveMessageInBytes);

    }

    private void sendRequestMsg(int pieceId){
        // maybe use a timeout here for corner case
        // A requests a piece but get choked before receiving result
        RequestMessage requestMessage = new RequestMessage(pieceId);
        byte[] requestMessageInBytes = requestMessage.getMessage();
        parent.setIsRequested(pieceId, true);
        // send over TCP
        sendMessage(requestMessageInBytes);
    }

    public void sendChokeMsg(){
        // send choke and stop sending pieces
        ChokeMessage chokeMessage = new ChokeMessage();
        byte[] chokeMessageInBytes = chokeMessage.getMessage();
        sendMessage(chokeMessageInBytes);
    }

    public void sendUnchokedMsg(){
        UnchokeMessage unchokeMessage = new UnchokeMessage();
        byte[] unchokeMessageInBytes = unchokeMessage.getMessage();
        sendMessage(unchokeMessageInBytes);
    }

    // to be called from inside receive request msg
    public void sendPieceMsg(int pieceId){
        byte[] pieceContent = parent.myFileHandler.getChunk(pieceId);
        PieceMessage pieceMessage = new PieceMessage( pieceId, pieceContent);
        sendMessage(pieceMessage.getMessage());
    }

    public void receivedHaveMsg(byte[] msg){
        // check and update peer's bitfield
        // send 'interested' msg to NB
        HaveMessage haveMessage = new HaveMessage(msg);
        //
        int havePieceId = haveMessage.getPieceIndex();
        parent.logger.writelog(5, peerConnected.getPeerId(), parent.selfId, havePieceId,0);
        Util.updateBitFieldWithPiece(peerConnected.getBitfield(), havePieceId);
        if(!checkIfHave(havePieceId)) {
            sendInterestedMsg();
        }
    }

    public void receiveBitfield(byte[]msg){
        BitfieldMessage bitfieldMessage = new BitfieldMessage(msg);
        // Set connected peer's bit field for the first time
        System.out.println(currentThread().getName() + " : got bitfield as " + bitfieldMessage.getBitfield());
        this.peerConnected.setBitfield(bitfieldMessage.getBitfield());
        // send hosts bitfield back
        if(!amIInitiator){
            sendBitfieldMsg();

        }
        // Send interested / Not interested msgs back
        if(checkIfInterested_bitfield(bitfieldMessage.getBitfield())) {
            sendInterestedMsg();
        }
        else {
            sendNotInterestedMsg();
        }
    }

    public void receivedInterestedMsg(){
        // update interested peer set of parent node here
        parent.logger.writelog(6, peerConnected.getPeerId(), parent.selfId,0,0);
        parent.addtointerestedPeerList(peerConnected.getPeerId());
    }

    public void receivedNotInterested(){
        // if this peer was previously in interested set, remove it
        parent.logger.writelog(7, peerConnected.getPeerId(), parent.selfId,0,0);
        parent.removefrominterestedPeerList(peerConnected.getPeerId());
        if(parent.myFileHandler.checkIfFinish()){
            System.out.println("\n ****  Parent " + parent.selfPeer.getPeerId() + "is finised\n");
            //parent.updateDoneSet(peerConnected.getPeerId());
        }
    }

    public void receivedDoneMsg(byte[] message){
        // if this peer was previously in interested set, remove it
       // parent.logger.writelog(7, peerConnected.getPeerId(), parent.selfId,0,0);
        //parent.removefrominterestedPeerList(peerConnected.getPeerId());
        System.out.println("\n ****  Parent " + parent.selfPeer.getPeerId() + "got that peerConnectd is done : " +
                peerConnected.getPeerId());
        parent.updateDoneSet(peerConnected.getPeerId());

    }
    public void receiveChokeMsg(){
        // dont transmit
        byte[] rcvChokeMsg = new byte[10];
        ChokeMessage chokeMessage = new ChokeMessage(rcvChokeMsg);
        gotChoked = true;
        parent.logger.writelog(4, peerConnected.getPeerId(), parent.selfId,0,0);
    }

    public void receiveUnchokeMsg(){
        // find out required piece and begin transmitting.
        // send 'request' msg here
        gotChoked = false;
        parent.logger.writelog(3, peerConnected.getPeerId(), parent.selfId,0,0);
        List<Integer> interestingPieces = parent.myFileHandler.chunksIAmInterestedInFromPeer(peerConnected.getBitfield());
        if(interestingPieces.isEmpty()) {
            // that peer unchoked me but I am no longer interested in any of it's pieces
            sendNotInterestedMsg();
        }
        else {
            boolean gotPiece = false;
            int pieceIdRequested = -1;
            while (!gotPiece) {
                pieceIdRequested = interestingPieces.remove(0); // always first piece
                gotPiece = !(parent.checkRequestedStatus(pieceIdRequested));
            }
            if (pieceIdRequested != -1)
                sendRequestMsg(pieceIdRequested);
            else
                sendNotInterestedMsg();
        }
    }

    public void receiveRequestMsg(byte [] msg){
        // check the requested piece id
        // if you have it send 'piece msg' with payload
        RequestMessage requestMessage = new RequestMessage(msg);
        int requestedPieceIndex = requestMessage.getPieceIndex();
        if(checkIfHave(requestedPieceIndex)) {
            sendPieceMsg(requestedPieceIndex);
        }
    }

    public void receivePieceMsg(byte[] msg){
        // call 'completePieceReceived'
        PieceMessage pieceMessage = new PieceMessage(msg);
        int pieceIndex = pieceMessage.getPieceIndex();
        byte[] chunk = pieceMessage.getPieceContent();
        parent.myFileHandler.putChunk(pieceIndex, chunk);
        parent.myFileHandler.updateMyBitfiled(pieceIndex);
        parent.logger.writelog(8, peerConnected.getPeerId(), parent.selfId, pieceIndex, parent.myFileHandler.numOfChunksIHave());

        parent.sendHavePieceUpdateToAll(pieceIndex);
        if(parent.myFileHandler.checkIfFinish()){
            // write finish log
            System.out.println(" Node " + parent.selfPeer.getPeerId() + " has complete file now");
            parent.sendDoneUpdateToAll();
            parent.logger.writelog(9, peerConnected.getPeerId(), parent.selfId,0,0);

        }
        if(!gotChoked) {
            List<Integer> interestedPieceList = parent.myFileHandler.chunksIAmInterestedInFromPeer(peerConnected.getBitfield());
            if (interestedPieceList.isEmpty()) {
                sendNotInterestedMsg();
            } else {
                boolean gotPiece = false;
                int pieceIdRequested = -1;
                while (!gotPiece) {
                    pieceIdRequested = interestedPieceList.remove(0); // always first piece
                    gotPiece = !(parent.checkRequestedStatus(pieceIdRequested));
                }
                if (pieceIdRequested != -1)
                    sendRequestMsg(pieceIdRequested);
                else
                    sendNotInterestedMsg();
            }
        }
        parent.updatedownloadingrate(peerConnected.getPeerId());
    }

//    public void completePieceReceived(){
//        // update parents bitfield and all peers bitfield
//        // send 'not interested' msg if needed to any peer from here
//        parent.setHasCompleteFile();
//        parent.updateMyBitfiled(1);
//        // ?? how to know to which all peer send 'not interested'
//    }

}
