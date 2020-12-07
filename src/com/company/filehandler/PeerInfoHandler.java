package com.company.filehandler;

import com.company.Peer;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

public class PeerInfoHandler {
    ConcurrentHashMap<Integer, Peer> peerHashMap;
    public String configPath;
    public static String PEERINFO_PATH = "";
    public PeerInfoHandler(){
        initialize();
        fileSetup();
    }

    private void initialize() {
        peerHashMap = new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<Integer, Peer> getPeerHashMap() {
        return peerHashMap;
    }

    private void fileSetup() {
        try {
            FileInputStream fileInputStream = new FileInputStream(PEERINFO_PATH);
            BufferedReader br = new BufferedReader( new InputStreamReader(fileInputStream));
            String string;
            while((string = br.readLine()) != null){
                String values[] =  string.split("\\s+");
                Peer peer = new Peer();
                peer.setPeerId(Integer.parseInt(values[0]));
                peer.setIpAddress(values[1]);
                peer.setPortNo(Integer.parseInt(values[2]));
                peerHashMap.put(peer.getPeerId(), peer);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    //getPeerInfo(){}





}
