package com.company.filehandler;

import com.company.pojo.ProjectConfiguration;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

public class MyFileHandler {
    

    byte[] bitField;
    boolean iHaveFile;
    public MyFileHandler(Boolean iHaveFile){
        this.iHaveFile = iHaveFile;
        bitFieldSetUp();
    }


    private void bitFieldSetUp() {
        ProjectConfiguration projectConfiguration = CommonConfigHandler.getInstance().getProjectConfiguration();
        bitField = new byte[projectConfiguration.getNumChunks()];
        if(iHaveFile){
            BitSet set = BitSet.valueOf(bitField);
            for(int i = 0; i < projectConfiguration.getNumChunks(); i++){
                set.set(i,true);
            }
            bitField = set.toByteArray();
        }

    }

    public int numOfPiecesIHave(){
        BitSet set = BitSet.valueOf(bitField);
        int c = 0;
        for(int i =0; i < set.length(); i++){
            if(set.get(i)){
                c++;
            }
        }
        return c;
    }
    // Missing pieces
    public List<Integer> chunksIWant(){
        BitSet myBitSet = BitSet.valueOf(bitField);
        List<Integer> list = new ArrayList<Integer>();
        for(int i = 0; i < CommonConfigHandler.getInstance().getProjectConfiguration().getNumChunks(); i++){
            if(!myBitSet.get(i)){
                list.add(i);
            }
        }
        return list;
    }



    public List<Integer> chunksIAmInterestedInFromPeer(byte[] peerBitField){
        BitSet peerBitSet = BitSet.valueOf(peerBitField);
        BitSet myBitSet = BitSet.valueOf(bitField);
        List<Integer> list = new ArrayList<>();
        for(int i = 0; i < peerBitSet.length(); i++){
            if(peerBitSet.get(i) && (!myBitSet.get(i))){
                list.add(i);
            }
        }
        return list;
    }



    public byte[] getBitField(){
        return bitField;
    }


    public  void splitFile(String fName) throws IOException {
        File f = new File(fName);
        int partCounter = 0;//I like to name parts from 001, 002, 003, ...
        //you can change it to 0 if you want 000, 001, ...

        int sizeOfFiles = CommonConfigHandler.getInstance().getProjectConfiguration().getPieceSize();
        byte[] buffer = new byte[sizeOfFiles];

        String fileName = f.getName();

        //try-with-resources to ensure closing stream
        try (FileInputStream fis = new FileInputStream(f);
             BufferedInputStream bis = new BufferedInputStream(fis)) {

            int bytesAmount = 0;
            while ((bytesAmount = bis.read(buffer)) > 0) {
                //write each chunk of data into separate file with different number in name
                String filePartName = fileName + "." + partCounter;
                File newFile = new File(f.getParent(), filePartName);
                try (FileOutputStream out = new FileOutputStream(newFile)) {
                    out.write(buffer, 0, bytesAmount);
                }
            }
        }
    }


    public  List<File> listOfFilesToMerge(String oneOfFiles) {
        return listOfFilesToMerge(new File(oneOfFiles));
    }

    public  void mergeFiles(String oneOfFiles, String into) throws IOException{
        mergeFiles(new File(oneOfFiles), new File(into));
    }

    public  void mergeFiles(File oneOfFiles, File into)
            throws IOException {
        mergeFiles(listOfFilesToMerge(oneOfFiles), into);
    }
    public static List<File> listOfFilesToMerge(File oneOfFiles) {
        String tmpName = oneOfFiles.getName();//{name}.{number}
        String destFileName = tmpName.substring(0, tmpName.lastIndexOf('.'));//remove .{number}
        File[] files = oneOfFiles.getParentFile().listFiles(
                (File dir, String name) -> name.matches(destFileName + "[.]\\d+"));
        Arrays.sort(files);//ensuring order 001, 002, ..., 010, ...
        return Arrays.asList(files);
    }

    private void mergeFiles(List<File> files, File into)
            throws IOException {
        try (FileOutputStream fos = new FileOutputStream(into);
             BufferedOutputStream mergingStream = new BufferedOutputStream(fos)) {
            for (File f : files) {
                Files.copy(f.toPath(), mergingStream);
            }
        }
    }


    synchronized public void updateMyBitfiled(int pieceIndex){

        //bitfieldLock.lock();
        try {
            // Do bit manipulation here
            BitSet bitSet = BitSet.valueOf(bitField);
            bitSet.set(pieceIndex, true);
            bitField = bitSet.toByteArray();

        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
           // bitfieldLock.unlock();
        }
    }

    public void putChunk(int pieceIndex, byte[] fileData){
        try {
            String path = CommonConfigHandler.getInstance().getProjectConfiguration().getFileName() + "." + pieceIndex;
            File file = new File(path);
            OutputStream
                    os
                    = new FileOutputStream(file);

            // Starts writing the bytes in it
            os.write(fileData);
            System.out.println("Successfully"
                    + " byte inserted");

            // Close the file
            os.close();
            updateMyBitfiled(pieceIndex);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  byte[] getChunk(int pieceIndex){
        byte[] data = null;

        try {
            String fPath = CommonConfigHandler.getInstance().getProjectConfiguration().getFileName() + "." + pieceIndex;
            Path path = Paths.get(fPath);
            data = Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public boolean checkIfFinish(){
        BitSet set = BitSet.valueOf(bitField);
        boolean flag = true;

        for(int i = 0; i < set.length(); i++){
            if(!set.get(i)){
                flag = false;
            }
        }
        return flag;
    }


}
