package com.company;

import com.company.filehandler.PeerInfoHandler;
import com.company.filehandler.MyFileHandler;

import java.net.ServerSocket;
import java.net.Socket;
import java.lang.Thread;
import java.lang.InterruptedException;
import java.io.*;
import java.util.*;
import java.lang.*;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
// Actual node, will keep listening to msgs and divert  them to peer handlers accordingly.
// one new peer handler per peer.

public class Node extends Thread{
    Peer peer; // Host
    PeerHandler peerhandler;
    PeerInfoHandler peerInfoHandler;    // This will store handler threads against each peerId. Use this to send choke unchoke msg
    private HashMap<Integer, PeerHandler> PeerMap = new HashMap<Integer, PeerHandler>();
    private ServerSocket listeningSocket;     // it will keep accepting new connections
    private int sPort = 8000;
    private Peer[] neighbourList;          // neighbours it is connected
    private int[] ChokedPeerList;           // indices of choked peers
    private List<Integer> interestedPeerList;       // indices of interested peers
    private HashSet<Integer> preferredPeers;           // indices of currently active peers
    public boolean[] isRequested;
    private int numOfUnchokedPeers;
    // TODO add timers Peerlist update and random optimistic peer update
    private byte[] myBitfield;
    private HashSet<Integer> unchokedPeers;      // contains index of currently unchoked peers
    // TODO Add more data members here
    private int OptimisticallySelectedPeer;
    private List<Integer> InterestedButNotSelected;
    private Boolean HasCompleteFile = false; //set in the beginning based on the PeerInfo file
    private int expectedpeerID;
    int k = 0, m = 0, p = 0; //define in commonconfig file later
    Lock bitfieldLock;
    Lock isRequestedLock;
    MyFileHandler myFileHandler;

    public Node()
    {
        unchokedPeers = new HashSet<>();
        preferredPeers = new HashSet<>();
        interestedPeerList = new ArrayList<>();
        bitfieldLock = new ReentrantLock();
        myBitfield = new byte[2]; // TODO change this DJ utility
        isRequestedLock = new ReentrantLock();
        myFileHandler = new MyFileHandler(true); // TODO DJ read from cfg file
        
    }


    public void run(){
        System.out.println("The server is running....");
        // TODO DJs utility's number of pieces extract
        int numPieces = 5; // TODO user myFilehandler dummy
        boolean iHaveFile = true; // dummy TODO use Myfilehandler
        isRequested = new boolean[numPieces];
        if (iHaveFile)
            Arrays.fill(isRequested, true);
        else
            Arrays.fill(isRequested, false);

        try {
            listeningSocket = new ServerSocket(sPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int clientNum = 1;
        try {
            while(true) {
                new PeerHandler(listeningSocket.accept(),this).start();
                System.out.println("Client "  + clientNum + " is connected!");
                clientNum++;
                //TODO
                // add timers and then timout logic here
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try {
                listeningSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //getters & setters can be added if needed later.
    //getters for used variables
    public List<Integer> getinterestedPeerList(){
        return this.interestedPeerList;
    }
    public HashSet<Integer> getpreferredPeers(){
        return this.preferredPeers;
    }
    public int getOptimisticallySelectedPeer(){
        return this.OptimisticallySelectedPeer;
    }
    public List<Integer> getInterestedButNotSelected(){
        return this.InterestedButNotSelected;
    }
    public HashSet<Integer> getunchokedPeers(){
        return this.unchokedPeers;
    }
    public byte[] getMyBitfield() {
        return myBitfield;
    }
    public int getexpectedpeerID() {
        return expectedpeerID;
    }
    //setters for used variables
    public void setinterestedPeerList(List<Integer>  interestedPeerList) {
        this.interestedPeerList = interestedPeerList;
    }

    public void setpreferredPeers(HashSet<Integer> preferredPeers) {
        this.preferredPeers = preferredPeers;
    }

    public void setOptimisticallySelectedPeer(int OptimisticallySelectedPeer) {
        this.OptimisticallySelectedPeer = OptimisticallySelectedPeer;
    }

    public void setInterestedButNotSelected(List<Integer> InterestedButNotSelected) {
        this.InterestedButNotSelected = InterestedButNotSelected;
    }

    public void setexpectedpeerID(int expectedpeerID) {
        this.expectedpeerID = expectedpeerID;
    }

    public void setHasCompleteFile() {
        this.HasCompleteFile = true;
    }

    public void addtointerestedPeerList(int id)
    {
        this.interestedPeerList.add(id);
    }

    public void removefrominterestedPeerList(int id)
    {
        this.interestedPeerList.remove(id);
    }

    // each peer handler will call from their thread
    public void setIsRequested(int pieceIdIndex, boolean value){
        isRequestedLock.lock();
        isRequested[pieceIdIndex] = value;
        isRequestedLock.unlock();
    }

    // just check
    public boolean checkRequestedStatus(int pieceIdIndex){
        isRequestedLock.lock();
        boolean status = isRequested[pieceIdIndex];
        isRequestedLock.unlock();
        return status;
    }

    /* Takes piece index and creates a byte with '1' at that index */
    public byte[] generateByteFromBinaryString(int pieceIndex){
        StringBuilder binaryString = new StringBuilder();
        byte[] resultingBitField = new byte[2];
        for(int i = 0; i < 8; i ++){
            if((pieceIndex % 8) == i){
                binaryString.append('1');
            }
            else {
                binaryString.append('0');
            }
        }
        //System.out.println("\n Binary String = " + binaryString);
        if(pieceIndex < 7){
            resultingBitField[0] = (byte)(Integer.parseInt(String.valueOf(binaryString), 2));
            resultingBitField[1] = (byte) 0x00;
        }
        else{
            resultingBitField[1] =  (byte)Integer.parseInt(String.valueOf(binaryString), 2);
            resultingBitField[0] = (byte) 0x00;
        }
        System.out.println( "\n ResultingBitField = " + Util.convertBytetoInt(resultingBitField));
        return resultingBitField;
    }

    /* each handler thread will call it from their context
     to update 'myBitfield' */
    public void updateMyBitfiled(int pieceIndex){

        bitfieldLock.lock();
        try {
            // Do bit manipulation here
            BitSet bitSet = BitSet.valueOf(myBitfield);
            bitSet.set(pieceIndex, true);
            myBitfield = bitSet.toByteArray();

//            byte[] newBitField = generateByteFromBinaryString(pieceIndex);
//            byte[] tempBitField = new byte[2];
//
//            // First Byte
//            int result = myBitfield[0] | newBitField[0];
//            tempBitField[0] = (byte)(result & 0xff); // byte is signed so use int and mask
//            System.out.println("\n MSB | operand MSB = " + (tempBitField[0] & 0xff));
//
//            // Second Byte
//            result = myBitfield[1] | newBitField[1];
//            tempBitField[1] = (byte)(result & 0xff);
//            System.out.println("\n LSB | operand LSB = " + (tempBitField[1] & 0xff));
//
//            myBitfield = tempBitField;
//            System.out.println( "\n Updated Bit Field = " + (Util.convertBytetoInt(myBitfield) & 0xffff));


        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            bitfieldLock.unlock();
        }
    }

    public void updatePreferedPeerList(){
    // to be called after timer p sets in.
    // calculate new preferred peers from peerlist
    // and send them unchoke msgs
        Node node = this;
        Thread thread = new Thread() {
            public void run() {
                System.out.println("Thread for selecting preferred peer Running");
                //select preferred neighbors
                //sort interested peers in dec order of their downloading rate
                Collections.sort(node.interestedPeerList, (i1, i2) -> peer.getPeerIDtoDownloadRate().get(i2) - peer.getPeerIDtoDownloadRate().get(i1));
                //update preferred peer list
                if (getinterestedPeerList().size() < k) {
                    List<Integer> first = new ArrayList<>(node.interestedPeerList.subList(0, node.interestedPeerList.size()));
                    HashSet<Integer> tSet = new HashSet<Integer>();
                    tSet.addAll(first);
                    setpreferredPeers(tSet);
                    node.InterestedButNotSelected = null;
                } else {
                    List<Integer> first1 = new ArrayList<>(node.interestedPeerList.subList(0, k));
                    List<Integer> second1 = new ArrayList<>(node.interestedPeerList.subList(k+1, node.interestedPeerList.size()));
                    HashSet<Integer> tSet1 = new HashSet<Integer>();
                    tSet1.addAll(first1);
                    setpreferredPeers(tSet1);
                    setInterestedButNotSelected(second1);
                }
                //choke or unchoke them (only k) based on their current state

                //unchoking selected ones
                for (int PreferredPeer : node.preferredPeers) {
                    if (!getunchokedPeers().contains(PreferredPeer)) //if previously not choked //opt selected peer selected and choked separately
                    {
                        node.unchokedPeers.add(PreferredPeer);
                        peerhandler.sendUnchokedMsg(); //argument to send : PreferredPeer
                    }
                }

                //choking not selected connections
                for (int unchokedPeer : node.unchokedPeers) {
                    if (!node.preferredPeers.contains(unchokedPeer))
                    {
                        if(unchokedPeer != node.OptimisticallySelectedPeer) {
                            node.unchokedPeers.remove(unchokedPeer);
                            peerhandler.sendChokeMsg(); //argument to send : unchokedPeer
                        }
                    }
                }
                //in this p interval itself, calculate downloading rates for next p!!
                try {
                    Thread.sleep(p); // p = unchoking interval (define in config file)
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    public void updateOptimisticallyUnchokedPeer(){
        // after timer m sets in update one optimistically
        //selected neighbour and send 'unchoke msg'
        Node node = this;
        Thread thread = new Thread(){
            public void run() {
                System.out.println("Thread for selecting optimistic unchoked peer Running");
                //select opt unchoked neighbor
                if (getInterestedButNotSelected() == null) {
                    node.OptimisticallySelectedPeer = 0; //or what to keep ?
                } else {
                    Random rand = new Random();
                    int randomElement = InterestedButNotSelected.get(rand.nextInt(InterestedButNotSelected.size()));
                    setOptimisticallySelectedPeer(randomElement);
                    peerhandler.sendUnchokedMsg(); //argument to send : node.OptimisticallySelectedPeer
                }
                try {
                    Thread.sleep(m); // m = optimistic unchoking interval (define in config file)
                    // convert it into milliseconds and is a timer required ?
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

}
