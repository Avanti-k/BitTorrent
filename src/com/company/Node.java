package com.company;

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

public class Node {
    Peer peer;
    PeerHandler peerhandler;
    private Socket listeningSocket;     // it will keep accepting new connections
    private Peer[] neighbourList;          // neighbours it is connected
    private int[] ChokedPeerList;           // indices of choked peers
    private List<Integer> interestedPeerList;       // indices of interested peers
    private HashSet<Integer> preferredPeers;           // indices of currently active peers

    private int numOfUnchokedPeers;
    // TODO add timers Peerlist update and random optimistic peer update
    private byte[] myBitfield;
    private HashSet<Integer> unchokedPeers;      // contains index of currently unchoked peers
    // TODO Add more data members here
    private int OptimisticallySelectedPeer;
    private List<Integer> InterestedButNotSelected;
    private Boolean HasCompleteFile = false; //set in the beginning based on the PeerInfo file
    int k = 0, m = 0, p = 0; //define in commonconfig file later
    Lock bitfieldLock;
    Node()
    {
        unchokedPeers = new HashSet<>();
        preferredPeers = new HashSet<>();
        interestedPeerList = new ArrayList<>();
        bitfieldLock = new ReentrantLock();
        myBitfield = new byte[2]; // TODO not sure if 2 or 4 bytes
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
            byte[] newBitField = generateByteFromBinaryString(pieceIndex);
            byte[] tempBitField = new byte[2];

            // First Byte
            int result = myBitfield[0] | newBitField[0];
            tempBitField[0] = (byte)(result & 0xff); // byte is signed so use int and mask
            System.out.println("\n MSB | operand MSB = " + (tempBitField[0] & 0xff));

            // Second Byte
            result = myBitfield[1] | newBitField[1];
            tempBitField[1] = (byte)(result & 0xff);
            System.out.println("\n LSB | operand LSB = " + (tempBitField[1] & 0xff));

            myBitfield = tempBitField;
            System.out.println( "\n Updated Bit Field = " + (Util.convertBytetoInt(myBitfield) & 0xffff));


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
