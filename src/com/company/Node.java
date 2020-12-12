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
import com.company.pojo.ProjectConfiguration;
import com.company.filehandler.CommonConfigHandler;

// Actual node, will keep listening to msgs and divert  them to peer handlers accordingly.
// one new peer handler per peer.

public class Node extends Thread{
    Peer selfPeer; // Host
    PeerHandler peerhandler;
    PeerInfoHandler peerInfoHandler;    // This will store handler threads against each peerId. Use this to send choke unchoke msg
    private HashMap<Integer, PeerHandler> PeerMap = new HashMap<Integer, PeerHandler>(); // peerId: Handler thread
    private ServerSocket listeningSocket;     // it will keep accepting new connections
    private int sPort = 8000;
    private Peer[] neighbourList;          // neighbours it is connected
    private HashSet<Integer> interestedPeers;       // indices of interested peers
    private HashSet<Integer> preferredPeers;           // indices of currently active peers
    public boolean[] isRequested;
    private int numOfUnchokedPeers;
    // TODO add timers Peerlist update and random optimistic peer update
    // private byte[] myBitfield;

    private HashSet<Integer> unchokedPeers;      // contains index of currently unchoked peers
    private HashSet<Integer> ChokedPeers;           // indices of choked peers
    private HashSet<Integer> donePeers;
    private int OptimisticallySelectedPeer;
    private Boolean HasCompleteFile = false; //set in the beginning based on the PeerInfo file
    private int expectedpeerID;
    ProjectConfiguration projectConfiguration = CommonConfigHandler.getInstance().getProjectConfiguration();
    private int k = projectConfiguration.getNumberOfPreferredNeighbors(); // this should be in seconds. will it be already in sec or should be converted?
    private int m = projectConfiguration.getOptimisticUnchokingInterval();
    private int p = projectConfiguration.getUnchokingInterval();
    private HashMap<Integer, Integer> PeerIDtoDownloadRate = new HashMap<Integer, Integer>();
    Lock bitfieldLock;
    Lock peerMapLock;
    Lock isRequestedLock;
    MyFileHandler myFileHandler;
    int selfId;
    LoggerModule logger = new LoggerModule();

    public Node(int selfId)
    {
        unchokedPeers = new HashSet<>();
        this.selfId = selfId;
        ChokedPeers = new HashSet<>();
        preferredPeers = new HashSet<>();
        interestedPeers = new HashSet<>();
        bitfieldLock = new ReentrantLock();
        isRequestedLock = new ReentrantLock();
        peerInfoHandler = new PeerInfoHandler(selfId);
        myFileHandler = new MyFileHandler(peerInfoHandler.getPeerHashMap().get(selfId).gethaveFileInitially(), selfId);
        peerMapLock = new ReentrantLock();
        sPort = peerInfoHandler.getPeerHashMap().get(selfId).getPortNo();
        selfPeer = peerInfoHandler.getPeerHashMap().get(selfId);
        donePeers = new HashSet<>();

    }

    public void run(){
        int numPieces = projectConfiguration.getNumChunks();
        System.out.println(currentThread().getName() + " : The server is running.... on port = " + sPort +
                " numChunks = " + numPieces + " haveFile = " + peerInfoHandler.getPeerHashMap().get(selfId).gethaveFileInitially());

        isRequested = new boolean[numPieces];
        boolean toConnectPrevious = true;

        if (peerInfoHandler.getPeerHashMap().get(selfId).gethaveFileInitially()) {
            Arrays.fill(isRequested, true);
        }
        else {
            Arrays.fill(isRequested, false);
        }

        try {
            listeningSocket = new ServerSocket(sPort);
            int clientNum = 1;
            boolean diviFlag = true;
            while(true) {
                if(toConnectPrevious){
                    connectToMyPreviousPeers();
                    toConnectPrevious = false;
                }
                new PeerHandler(listeningSocket.accept(),this, false).start();
                System.out.println("Client "  + clientNum + " is connected!");
                clientNum++;


                if(diviFlag){
                    updatePreferedPeerList();
                    updateOptimisticallyUnchokedPeer();
                    diviFlag = false;
                }


                }
        } catch (IOException e) {
            try{
                listeningSocket.close();

            }
            catch (Exception f){
                f.printStackTrace();
            }
        } finally {
        }


    }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//        finally {
//            try {
//                listeningSocket.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
   // }

    public void connectToMyPreviousPeers(){
        HashMap<Integer,Peer> peersNeedToConnect = peerInfoHandler.getPeersBeforeMe();

        Thread connectorThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for(Peer peer : peersNeedToConnect.values()){
                    try {
                        Socket socket = new Socket(peer.getIpAddress(), peer.getPortNo());
                        new PeerHandler(socket,Node.this, true).start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        connectorThread.start();


    }

    //getters & setters can be added if needed later.
    //getters for used variables
    public HashSet<Integer> getinterestedPeerList(){
        return this.interestedPeers;
    }
    public HashSet<Integer> getpreferredPeers(){
        return this.preferredPeers;
    }
    public int getOptimisticallySelectedPeer(){
        return this.OptimisticallySelectedPeer;
    }
    public HashSet<Integer> getunchokedPeers(){
        return this.unchokedPeers;
    }
    public HashSet<Integer> getChokedPeers(){
        return this.ChokedPeers;
    }
    public int getexpectedpeerID() {
        return expectedpeerID;
    }
    public int getk(){
        return k;
    }
    public int getp(){
        return p;
    }
    public int getm(){
        return m;
    }

    public Map<Integer, Integer> getPeerIDtoDownloadRate(){
        return this.PeerIDtoDownloadRate;
    }

    public void updatePeerMap( int peerId, PeerHandler handlerThread){
        peerMapLock.lock();
        PeerMap.put(peerId, handlerThread);
        peerMapLock.unlock();
    }

    synchronized public void updateDoneSet( int peerId){
        donePeers.add(peerId);
        if(donePeers.size() == peerInfoHandler.getPeerHashMap().size()){
            // TODO terminate program here
            System.out.println("\n ******** " + currentThread().getName() + " :  ********** All peers completed ********* \n");
            System.exit(0);
        }
    }
    public void sendHavePieceUpdateToAll(int pieceIndex){
        PeerHandler[] handlersSet = PeerMap.values().toArray(new PeerHandler[0]);
        Command command = new Command(Constants.HAVE, pieceIndex);
        for (PeerHandler peerhandler : handlersSet){
                peerhandler.commandQueue.add(command);
        }
    }
    //setters for used variables
    public void setinterestedPeerList(HashSet<Integer>  interestedPeers) {
        this.interestedPeers = interestedPeers;
    }

    public void setpreferredPeers(HashSet<Integer> preferredPeers) {
        this.preferredPeers = preferredPeers;
    }

    public void setOptimisticallySelectedPeer(int OptimisticallySelectedPeer) {
        this.OptimisticallySelectedPeer = OptimisticallySelectedPeer;
    }

    public void setexpectedpeerID(int expectedpeerID) {
        this.expectedpeerID = expectedpeerID;
    }

    public void setPeerIDtoDownloadRate(int peerId){
        this.PeerIDtoDownloadRate.put(peerId, this.PeerIDtoDownloadRate.get(peerId) + 1);
    }

    public void setHasCompleteFile() {
        this.HasCompleteFile = true;
    }

    public void addtointerestedPeerList(int id)
    {
        this.interestedPeers.add(id);
    }

    public void removefrominterestedPeerList(int id)
    {
        this.interestedPeers.remove(id);
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


    public void updatedownloadingrate(int peerid) {
        if(getPeerIDtoDownloadRate().containsKey(peerid))
            setPeerIDtoDownloadRate(peerid);
        else
            this.PeerIDtoDownloadRate.put(peerid, 1);
    }

    public void updatePreferedPeerList(){
    // to be called after timer p sets in.
        Node node = this;
        int K = getk();
        Thread thread = new Thread() {
            public void run() {
                while(true) {
                    System.out.println("Thread for selecting preferred peer Running");
                    //select preferred neighbors
                    //sort interested peers in dec order of their downloading rate
                    //for those peers, who is not there in the map - did this - check if its correct
                    if (!node.interestedPeers.isEmpty()) {
                        List<Integer> interestedPeerList = new ArrayList<Integer>(node.interestedPeers);
                        if (!node.myFileHandler.checkIfFinish()) {
                            Collections.sort(interestedPeerList, new Comparator<Integer>() {
                                public int compare(Integer i1, Integer i2) {
                                    if (!getPeerIDtoDownloadRate().containsKey(i1))
                                        getPeerIDtoDownloadRate().put(i1, 0);
                                    if (!getPeerIDtoDownloadRate().containsKey(i2))
                                        getPeerIDtoDownloadRate().put(i2, 0);
                                    return getPeerIDtoDownloadRate().get(i2) - getPeerIDtoDownloadRate().get(i1);
                                }
                            });
                        } else {
                            Collections.shuffle(interestedPeerList);
                        }
                        //update preferred peer list
                        if (interestedPeerList.size() < K) {
                            List<Integer> first = new ArrayList<>(interestedPeerList.subList(0, interestedPeerList.size()));
                            HashSet<Integer> tSet = new HashSet<Integer>();
                            tSet.addAll(first);
                            setpreferredPeers(tSet);
                        } else {
                            List<Integer> first1 = new ArrayList<>(interestedPeerList.subList(0, K));
                            HashSet<Integer> tSet1 = new HashSet<Integer>();
                            tSet1.addAll(first1);
                            setpreferredPeers(tSet1);
                        }
                        //choke or unchoke them (only k) based on their current state
                        logger.writeLogForPreferedNeighbors(node.selfPeer.getPeerId(), node.getpreferredPeers());
                        //unchoking selected ones
                        for (int PreferredPeer : node.preferredPeers) {
                            if (!getunchokedPeers().contains(PreferredPeer)) //if previously not choked //opt selected peer selected and choked separately
                            {
                                node.unchokedPeers.add(PreferredPeer);
                                node.ChokedPeers.remove(PreferredPeer);
                                node.PeerMap.get(PreferredPeer).commandQueue.add(new Command(Constants.UNCHOKE, -1));
                            }
                        }

                        //choking not selected connections
                        for (int unchokedPeer : node.unchokedPeers) {
                            if (!node.preferredPeers.contains(unchokedPeer)) {
                                if (unchokedPeer != node.OptimisticallySelectedPeer) {
                                    node.unchokedPeers.remove(unchokedPeer);
                                    node.ChokedPeers.add(unchokedPeer);
                                    node.PeerMap.get(unchokedPeer).commandQueue.add(new Command(Constants.CHOKE, -1));
                                }
                            }
                        }

                        // set the downloading rate map to zero again for calculation for next cycle
                        node.PeerIDtoDownloadRate.clear();
                    }
                    try {
                        Thread.sleep(getp() * 1000); // p = unchoking interval

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
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
                while(true) {
                    System.out.println("Thread for selecting optimistic unchoked peer Running");
                    //select opt unchoked neighbor
                    if (!getChokedPeers().isEmpty()) {
                        HashSet<Integer> temp = getChokedPeers();
                        temp.retainAll(getinterestedPeerList());
                        List<Integer> tempList = new ArrayList<Integer>(temp);
                        Random rand = new Random();
                        int randomElement = tempList.get(rand.nextInt(tempList.size()));
                        setOptimisticallySelectedPeer(randomElement);
                        node.PeerMap.get(randomElement).commandQueue.add(new Command(Constants.UNCHOKE, -1));
                        logger.writeLogForOptNeighbor(node.selfPeer.getPeerId(), randomElement);
                    }
                    try {
                        Thread.sleep(getm() * 1000); // m = optimistic unchoking interval
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
    }

}
