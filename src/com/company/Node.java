package com.company;

import java.net.Socket;
// Actual node, will keep listening to msgs and divert  them to peer handlers accordingly.
// one new peer handler per peer.

public class Node {
    private Socket listeningSocket;     // it will keep accepting new connections
    private Peer[] neighbourList;          // neighbours it is connected to
    private int numOfUnchokedPeers;
    // TODO add timers Peerlist update and random optimistic peer update
    private byte[] myBitfied;
    private int[] unchokedPeerIndices;      // contains index of currently unchoked peers
    // TODO Add more data members here
    //getters & setters can be added if needed later.


    public byte[] getMyBitfied() {
        return myBitfied;
    }

}
