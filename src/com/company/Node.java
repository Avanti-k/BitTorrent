package com.company;

import java.net.Socket;
import java.lang.Thread;
import java.lang.InterruptedException;
// Actual node, will keep listening to msgs and divert  them to peer handlers accordingly.
// one new peer handler per peer.

public class Node {
    Peer peer;
    private Socket listeningSocket;     // it will keep accepting new connections
    private Peer[] neighbourList;          // neighbours it is connected
    private int[] ChokedPeerList;           // indices of choked peers
    private int[] interestedPeerList;       // indices of interested peers
    private int[] preferredPeers;           // indices of currently active peers

    private int numOfUnchokedPeers;
    // TODO add timers Peerlist update and random optimistic peer update
    private byte[] myBitfied;
    private int[] unchokedPeers;      // contains index of currently unchoked peers
    // TODO Add more data members here
    private int OptimisticallySelectedPeer;
    private int[] InterestedButNotSelected;
    private Boolean HasCompleteFile = false; //set in the beginning based on the PeerInfo file

    //getters & setters can be added if needed later.
    //getters for used variables
    public int[] getinterestedPeerList(){
        return this.interestedPeerList;
    }
    public int[] getpreferredPeers(){
        return this.preferredPeers;
    }
    public int getOptimisticallySelectedPeer(){
        return this.OptimisticallySelectedPeer;
    }
    public int[] getInterestedButNotSelected(){
        return this.InterestedButNotSelected;
    }

    public byte[] getMyBitfied() {
        return myBitfied;
    }

    //setters for used variables
    public void setinterestedPeerList(int[] interestedPeerList) {
        this.interestedPeerList = interestedPeerList;
    }

    public void setpreferredPeers(int[] preferredPeers) {
        this.preferredPeers = preferredPeers;
    }

    public void setOptimisticallySelectedPeer(int OptimisticallySelectedPeer) {
        this.OptimisticallySelectedPeer = OptimisticallySelectedPeer;
    }

    public void setInterestedButNotSelected(int[] InterestedButNotSelected) {
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
        Thread thread = new Thread() {
            public void run() {
                System.out.println("Thread for selecting preferred peer Running");
                //select preferred neighbors
                //sort interested peers in dec order of their downloading rate
                Arrays.sort(this.interestedPeerList, (i1, i2) -> peer.getPeerIDtoDownloadRate().get(i2) - peer.getPeerIDtoDownloadRate().get(i1));
                //update preferred peer list
                if (getinterestedPeerList().length < k) {
                    setpreferredPeers(Arrays.copyOfRange(this.interestedPeerList, 0, this.interestedPeerList.length));
                    this.InterestedButNotSelected.clear();
                } else {
                    setpreferredPeers(Arrays.copyOfRange(this.interestedPeerList, 0, k)); //k defined in config file //"in case of tie, choose randomly" is anyway taken care right?
                    setInterestedButNotSelected(Arrays.copyOfRange(this.interestedPeerList, k + 1, this.interestedPeerList.length));
                }
                //choke or unchoke them (only k) based on their current state

                //unchoking selected ones
                for (int PreferredPeer : this.preferredPeers) {
                    if (!this.unchokedPeers.contains(PreferredPeer)) //if previously not choked, //todo : take care of opt selected peer
                    {
                        this.unchokedPeers.add(PreferredPeer);
                        sendUnchokedMsg(PreferredPeer); //change argument passed later
                    }
                }

                //choking not selected connections
                for (int unchokedPeer : this.unchokedPeers) {
                    if (!this.preferredPeers.contains(unchokedPeer)) //todo : take care of opt selected peer
                    {
                        this.unchokedPeers.remove(unchokedPeer);
                        sendChokeMsg(unchokedPeer);
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
        Thread thread = new Thread(){
            public void run() {
                System.out.println("Thread for selecting optimistic unchoked peer Running");
                //select opt unchoked neighbor
                if (getInterestedButNotSelected().empty()) {
                    this.OptimisticallySelectedPeer = 0; //or what to keep ?
                } else {
                    int rnd = new Random().nextInt(this.InterestedButNotSelected.size());
                    setOptimisticallySelectedPeer(InterestedButNotSelected[rnd]);
                    sendUnchokedMsg(this.OptimisticallySelectedPeer); //change argument passed later
                }
                try {
                    Thread.sleep(m); // m = optimistic unchoking interval (define in config file)
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

}
