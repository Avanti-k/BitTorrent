package com.company;

public class RequestMessage extends Message {
    int pieceIndex;
    public RequestMessage(int pieceIndex) {
        this.pieceIndex = pieceIndex;
    }

    public RequestMessage createRequestMsg(int pieceIndex){
        // TODO add remaining fields
        RequestMessage requestMessage = new RequestMessage(pieceIndex);
        return requestMessage;
    }
}
