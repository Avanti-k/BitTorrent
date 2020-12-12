package com.company;


import java.io.InputStream;
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

    private Logger logger;
    private FileHandler fh;
    private SimpleFormatter formatter;

    LoggerModule(int peerid){
        try{
            logger = Logger.getLogger("Peer"+peerid);
            fh = new FileHandler("./log_peer_"+peerid+".log");
            logger.addHandler(fh);
            //formatter = new SimpleFormatter();
            //fh.setFormatter(formatter);

            fh.setFormatter(new SimpleFormatter() {
                private static final String format = "[%1$tF %1$tT] [%2$-7s] %3$s %n";
                @Override
                public String formatMessage(LogRecord record) {
                    return String.format(format,
                            new Date(record.getMillis()),
                            record.getLevel().getLocalizedName(),
                            record.getMessage()
                    );
                }
            });
            logger.setUseParentHandlers(false);
            //logger.addHandler(handler);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /*public String getcurrtime() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return formatter.format(date);
    }*/

    public void writelog(int function, int peerConnected, int peerHost, int pieceindex, int numpieces)
    {
        switch(function){
            case 1 :    //makes a TCP Connection
                //System.out.println("[" + getcurrtime() + "]: Peer [" + peerHost + "] makes a connection to Peer [" + peerConnected +"].");
                this.logger.info("Peer [" + peerHost + "] makes a connection to Peer [" + peerConnected +"].");
                break;

            case 2 :    //TCP Connection established
                //System.out.println("[" + getcurrtime() + "]: Peer [" + peerHost + "] is connected from Peer [" + peerConnected +"].");
                this.logger.info("Peer [" + peerHost + "] is connected from Peer [" + peerConnected +"].");
                break;
            case 3:     //unchoking
                //System.out.println("[" + getcurrtime() + "]: Peer [" + peerHost + "] is unchoked by [" + peerConnected +"].");
                this.logger.info("Peer [" + peerHost + "] is unchoked by [" + peerConnected +"].");
                break;
            case 4:     //choking
                //System.out.println("[" + getcurrtime() + "]: Peer [" + peerHost + "] is choked by [" + peerConnected +"].");
                this.logger.info("Peer [" + peerHost + "] is choked by [" + peerConnected +"].");
                break;
            case 5:     //receiving ‘have’ message
                //System.out.println("[" + getcurrtime() + "]: Peer [" + peerHost + "] received the ‘have’ message from [" + peerConnected +"] for the piece [" + pieceindex + "].");
                this.logger.info("Peer [" + peerHost + "] received the ‘have’ message from [" + peerConnected +"] for the piece [" + pieceindex + "].");
                break;
            case 6:     //receiving ‘interested’ message
                //System.out.println("[" + getcurrtime() + "]: Peer [" + peerHost + "] received the ‘interested’ message from [" + peerConnected +"].");
                this.logger.info("Peer [" + peerHost + "] received the ‘interested’ message from [" + peerConnected +"].");
                break;
            case 7:     //receiving ‘not interested’ message
                //System.out.println("[" + getcurrtime() + "]: Peer [" + peerHost + "] received the ‘not interested’ message from [" + peerConnected +"].");
                this.logger.info("Peer [" + peerHost + "] received the ‘not interested’ message from [" + peerConnected +"].");
                break;
            case 8:     //downloading a piece
                //System.out.println("[" + getcurrtime() + "]: Peer [" + peerHost + "] has downloaded the piece [" + pieceindex + "] from [" + peerConnected +"]. Now the number of pieces it has is [" + numpieces + "].");
                this.logger.info("Peer [" + peerHost + "] has downloaded the piece [" + pieceindex + " from [" + peerConnected +"]. Now the number of pieces it has is [" + numpieces + "].");
                break;
            case 9:     //completion of download
                //System.out.println("[" + getcurrtime() + "]: Peer [" + peerHost + "] has downloaded the complete file.");
                this.logger.info("Peer [" + peerHost + "] has downloaded the complete file.");
                break;
            default:
        }

    }

    public void writeLogForPreferedNeighbors(int peer1, HashSet<Integer> peers){
        //System.out.println("[" + getcurrtime() + "]: Peer [" + peer1 + " has the preferred neighbors [");
        StringBuffer writer = new StringBuffer();
        writer.append("Peer [" + peer1 + "] has the preferred neighbors [");
        for(int i : peers)
        {
            //System.out.println(i + ",");
            writer.append(i + ",");
        }
        //System.out.println("].");
        writer.append("].");
        this.logger.info(writer.toString());
    }

    public void writeLogForOptNeighbor(int peer1, int peer2){
        //System.out.println("[" + getcurrtime() + "]: Peer [" + peer1 + "] has the optimistically unchoked neighbor [" + peer2 +"].");
        this.logger.info("Peer [" + peer1 + "] has the optimistically unchoked neighbor [" + peer2 +"].");
    }
}