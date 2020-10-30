package com.company;

import java.net.Socket;
import java.lang.Thread;
import java.lang.InterruptedException;
import java.io.*;
import java.util.*;
import java.lang.*;
import java.util.ArrayList;
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
    private byte[] myBitfied;
    private HashSet<Integer> unchokedPeers;      // contains index of currently unchoked peers
    // TODO Add more data members here
    private int OptimisticallySelectedPeer;
    private List<Integer> InterestedButNotSelected;
    private Boolean HasCompleteFile = false; //set in the beginning based on the PeerInfo file
    int k = 0, m = 0, p = 0; //define in commonconfig file later

    Node()
    {
        unchokedPeers = new HashSet<>();
        preferredPeers = new HashSet<>();
        interestedPeerList = new ArrayList<>();
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
        return myBitfied;
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

    public void updateMyBitfiled(){
        // MUST BE CALLED WITH MUTEX
        // each handler thread will call it from their context
        // to update 'myBitfield'
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
