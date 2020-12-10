package com.company;

import java.util.logging.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.logging.SimpleFormatter;

public class LoggerModule {

    /*
    Logger logger = Logger.getLogger(LoggerModule.class.getName());
    FileHandler fileHandler = new FileHandler("./app.log");
    logger.addHandler(fileHandler);
    SimpleFormatter formatter = new SimpleFormatter();
    fileHandler.setFormatter(formatter);
    public LoggerModule() throws IOException {
    }
     */

    public String getcurrtime() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return formatter.format(date);
    }

    public void writelog(int function, int peer1, int peer2, int pieceindex, int numpieces)
    {
        switch(function){
            case 1 :    //makes a TCP Connection
                System.out.println("[" + getcurrtime() + "]: Peer [" + peer1 + "] makes a connection to Peer [" + peer2 +"].");
                //logger.info("Peer [" + peer1 + "] makes a connection to Peer [" + peer2 +"].");
                break;
            case 2 :    //TCP Connection established
                System.out.println("[" + getcurrtime() + "]: Peer [" + peer1 + "] is connected from Peer [" + peer2 +"].");
                //logger.info("Peer [" + peer1 + "] is connected from Peer [" + peer2 +"].");
                break;
            case 3:     //unchoking
                System.out.println("[" + getcurrtime() + "]: Peer [" + peer1 + "] is unchoked by [" + peer2 +"].");
                //logger.info("Peer [" + peer1 + "] is unchoked by [" + peer2 +"].");
                break;
            case 4:     //choking
                System.out.println("[" + getcurrtime() + "]: Peer [" + peer1 + "] is choked by [" + peer2 +"].");
                //logger.info("Peer [" + peer1 + "] is choked by [" + peer2 +"].");
                break;
            case 5:     //receiving ‘have’ message
                System.out.println("[" + getcurrtime() + "]: Peer [" + peer1 + "] received the ‘have’ message from [" + peer2 +"] for the piece [" + pieceindex + "].");
                //logger.info("Peer [" + peer1 + "] received the ‘have’ message from [" + peer2 +"] for the piece [" + pieceindex + "].");
                break;
            case 6:     //receiving ‘interested’ message
                System.out.println("[" + getcurrtime() + "]: Peer [" + peer1 + "] received the ‘interested’ message from [" + peer2 +"].");
                //logger.info("Peer [" + peer1 + "] received the ‘interested’ message from [" + peer2 +"].");
                break;
            case 7:     //receiving ‘not interested’ message
                System.out.println("[" + getcurrtime() + "]: Peer [" + peer1 + "] received the ‘not interested’ message from [" + peer2 +"].");
                //logger.info("Peer [" + peer1 + "] received the ‘not interested’ message from [" + peer2 +"].");
                break;
            case 8:     //downloading a piece
                System.out.println("[" + getcurrtime() + "]: Peer [" + peer1 + "] has downloaded the piece [" + pieceindex + " from [" + peer2 +"]. Now the number of pieces it has is [" + numpieces + "].");
                //logger.info("Peer [" + peer1 + "] has downloaded the piece [" + pieceindex + " from [" + peer2 +"]. Now the number of pieces it has is [" + numpieces + "].");
                break;
            case 9:     //completion of download
                System.out.println("[" + getcurrtime() + "]: Peer [" + peer1 + "] has downloaded the complete file.");
                //logger.info("Peer [" + peer1 + "] has downloaded the complete file.");
                break;
            default:
        }

    }

    public void writeLogForPreferedNeighbors(int peer1, HashSet<Integer> peers){
        System.out.println("[" + getcurrtime() + "]: Peer [" + peer1 + " has the preferred neighbors [");
        //logger.info("Peer [" + peer1 + " has the preferred neighbors [");
        for(int i : peers)
        {
            System.out.println(i + ",");
            //logger.info(i + ",");

        }
        System.out.println("].");
        //logger.info("].");
    }

    public void writeLogForOptNeighbor(int peer1, int peer2){
        System.out.println("[" + getcurrtime() + "]: Peer [" + peer1 + "] has the optimistically unchoked neighbor [" + peer2 +"].");
        //logger.info("Peer [" + peer1 + "] has the optimistically unchoked neighbor [" + peer2 +"].");
    }
}
