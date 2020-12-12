package com.company.filehandler;

import com.company.Peer;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class PeerInfoHandler {
    ConcurrentHashMap<Integer, Peer> peerHashMap;


    HashMap<Integer, Peer> peersBeforeMe;
    public static String PEERINFO_PATH = "PeerInfo.cfg";
    private int nodeId;
    public PeerInfoHandler(int nodeId){
        this.nodeId = nodeId;
        initialize();
        fileSetup();
    }

    private void initialize() {

        peerHashMap = new ConcurrentHashMap<>();
        peersBeforeMe = new HashMap<>();
    }

    public HashMap<Integer, Peer> getPeersBeforeMe() {
        return peersBeforeMe;
    }

    public ConcurrentHashMap<Integer, Peer> getPeerHashMap() {
        return peerHashMap;
    }


    private void fileSetup() {
        try {
            FileInputStream fileInputStream = new FileInputStream(PEERINFO_PATH);
            BufferedReader br = new BufferedReader( new InputStreamReader(fileInputStream));
            String string;
            boolean peerAdd = true;
            while((string = br.readLine()) != null){
                String values[] =  string.split("\\s+");
                Peer peer = new Peer();
                peer.setPeerId(Integer.parseInt(values[0]));

                peer.setIpAddress(values[1]);
                peer.setPortNo(Integer.parseInt(values[2]));
                if(Integer.parseInt(values[3]) == 1){
                    peer.setHaveFileInitially(true);

                }
                peerHashMap.put(peer.getPeerId(), peer);
                if(peerAdd){
                    if(peer.getPeerId() != nodeId){
                        peersBeforeMe.put(peer.getPeerId(),peer);
                    }else{
                        peerAdd = false;
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    //getPeerInfo(){}





}
