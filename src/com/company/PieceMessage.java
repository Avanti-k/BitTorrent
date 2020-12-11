package com.company;

public class PieceMessage extends Message{
    private int pieceIndex;
    private byte[] pieceContent;

    /* Bytes to Object creation */
    public PieceMessage(byte[] input) {
        super(input);
        parsePayload();

    }

    /* Parameters to Object formation */
    public PieceMessage(int pieceIndex, byte[] content){
        super(pieceIndex, content, Constants.PIECE);
        parsePayload();
    }

    /* Parses the payload into Piece Id and Piece Content*/
    void parsePayload(){
        byte[] idBytes = new byte[4];
        byte[] piece = new byte[this.messagePayload.length - 4];
        int i = 0;
        int index = 0;
        for(; i < 4; i++){
            idBytes[index] = this.messagePayload[i];
            index++;
        }
        index = 0;
        for(; i < 4+this.messagePayload.length; i++){
            piece[index] = this.messagePayload[i];
            index++;
        }
        this.pieceContent = piece;
        this.pieceIndex = Util.convertBytetoInt(idBytes);
    }

    public int getPieceIndex() {
        return pieceIndex;
    }

    public byte[] getPieceContent() {
        return pieceContent;
    }
}
