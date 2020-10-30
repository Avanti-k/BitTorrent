package com.company;

public class RequestMessage extends Message {
    private int pieceIndex;
    public RequestMessage(byte[] input) {
        super(input);
        parsePayload();
    }
    public RequestMessage(int pieceIndex){
        super( pieceIndex, Constants.REQUEST);
        parsePayload();
    }


    /* Parses the payload into Piece Id */
    void parsePayload(){
        byte[] idBytes = new byte[4];
        int i = 0;
        int index = 0;
        for(; i < 4; i++){
            idBytes[index] = this.messagePayload[i];
            index++;
        }
        this.pieceIndex = Util.convertBytetoInt(idBytes);
    }

    public int getPieceIndex() {
        return pieceIndex;
    }
}
