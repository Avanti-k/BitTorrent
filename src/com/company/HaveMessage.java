package com.company;

public class HaveMessage  extends  Message{
    private int pieceId;
    public HaveMessage(int pieceId)
    {
        this.pieceId = pieceId;
    }
    public HaveMessage createHaveMsg(int pieceId){
        // TODO fill msg type, mength and piece Id as payload here
        HaveMessage haveMessage = new HaveMessage(pieceId);
        return haveMessage;
    }
}
