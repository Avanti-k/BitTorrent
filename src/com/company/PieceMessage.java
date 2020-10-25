package com.company;

public class PieceMessage extends Message{
    private int pieceIndex;
    private byte[] pieceContent;

    public PieceMessage(int pieceIndex, byte[] content){
        this.pieceContent = content;
        this.pieceIndex = pieceIndex;
    }

    public PieceMessage createPieceMsg(int pieceIndex, byte[] pieceContent){
        // TODO add remaining fields
        PieceMessage pieceMessage = new PieceMessage(pieceIndex, pieceContent);
        return pieceMessage;
    }

}
