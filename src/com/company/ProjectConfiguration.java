package com.company;

public class ProjectConfiguration {
    int NumberOfPreferredNeighbors;
    int UnchokingInterval;
    int OptimisticUnchokingInterval;
    String FileName;
    int FileSize;
    int PieceSize;
    int numChunks;

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public void setFileSize(int fileSize) {
        FileSize = fileSize;
    }

    public void setNumberOfPreferredNeighbors(int numberOfPreferredNeighbors) {
        NumberOfPreferredNeighbors = numberOfPreferredNeighbors;
    }

    public void setOptimisticUnchokingInterval(int optimisticUnchokingInterval) {
        OptimisticUnchokingInterval = optimisticUnchokingInterval;
    }

    public void setPieceSize(int pieceSize) {
        PieceSize = pieceSize;
    }

    public void setUnchokingInterval(int unchokingInterval) {
        UnchokingInterval = unchokingInterval;
    }

    public int getNumberOfPreferredNeighbors() {
        return NumberOfPreferredNeighbors;
    }

    public int getOptimisticUnchokingInterval() {
        return OptimisticUnchokingInterval;
    }

    public int getPieceSize() {
        return PieceSize;
    }

    public int getUnchokingInterval() {
        return UnchokingInterval;
    }

    public String getFileName() {
        return FileName;
    }

    public int getFileSize() {
        return FileSize;
    }

    public int getNumChunks() {
        return numChunks;
    }

    public void setNumChunks(int numChunks) {
        this.numChunks = numChunks;
    }
}
