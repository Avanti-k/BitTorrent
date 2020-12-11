package com.company;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;

public class Util {
    public static byte[] convertIntToByte(int i){
        return ByteBuffer.allocate(4).putInt(i).array();
    }

    public static int convertBytetoInt(byte[] array){
        return new BigInteger(array).intValue();
        //return ByteBuffer.wrap(array).getInt(); this is throwing some exception
    }

    public static byte[] convertStringToByte(String str){
        return str.getBytes(StandardCharsets.UTF_8);
    }
    public static String convertByteToString(byte[] input){
        return new String(input, StandardCharsets.UTF_8);
    }


    public static byte[] updateBitFieldWithPiece(byte[] oldBitField, int pieceIndex){
        BitSet set = BitSet.valueOf(oldBitField);
        set.set(pieceIndex,true);
        return set.toByteArray();
    }


   public static int convertNumBitsToNumBytes(int i){
        return (int) Math.ceil((double) i/8d);
   }


}
