package com.company;

import java.net.Socket;
// Actual node, will keep listening to msgs and divert  them to peer handlers accordingly.
// one new peer handler per peer.
//try commit
public class Node {
    private Socket listeningSocket;     // it will keep accepting new connections
    private Peer[] neighbourList;          // neighbours it is connected
    private int[] ChokedPeerList;           // indices of choked peers
    private int[] interestedPeerList;       // indices of interested peers
    private int[] preferredPeers;           // indices of currently active peers

    private int numOfUnchokedPeers;
    // TODO add timers Peerlist update and random optimistic peer update
    private byte[] myBitfied;
    private int[] unchokedPeerIndices;      // contains index of currently unchoked peers
    // TODO Add more data members here
    //getters & setters can be added if needed later.

    public byte[] getMyBitfied() {
        return myBitfied;
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
    }

    public void updateOptimisticallyUnchokedPeer(){
        // after timer m sets in update one optimistically
        //selected neighbour and send 'unchoke msg'
    }




}
