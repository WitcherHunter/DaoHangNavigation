package com.serenegiant.utils;

import android.text.TextUtils;

import java.util.List;

public class HexUtil {
    public static byte[] vinCommand = new byte[]{0x55, (byte) 0xAA, 0x00,0x03,0x01,0x05,0x07,0x0D,0x0A};
    public static byte[] standardCommand = new byte[]{0x55, (byte)0xAA, 0x00, 0x03, 0x01, 0x03, 0x00, 0x0D, 0x0A};

    public static byte[] int2BytesHighFirst(int value){
        byte[] src = new byte[4];
        src[0] = (byte) ((value>>24) & 0xFF);
        src[1] = (byte) ((value>>16)& 0xFF);
        src[2] = (byte) ((value>>8)&0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }

    public static byte[] int2BytesLowFirst(int value){
        byte[] src = new byte[4];
        src[3] =  (byte) ((value>>24) & 0xFF);
        src[2] =  (byte) ((value>>16) & 0xFF);
        src[1] =  (byte) ((value>>8) & 0xFF);
        src[0] =  (byte) (value & 0xFF);
        return src;
    }

    public static int byteToInt(byte b){
        return b & 0xFF;
    }

    public static int byteArrayToInt(byte[] b) {
        return   b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    public static int byteArrayToInt(List<Byte> b) {
        return   b.get(3) & 0xFF |
                (b.get(2) & 0xFF) << 8 |
                (b.get(1) & 0xFF) << 16 |
                (b.get(0) & 0xFF) << 24;
    }

    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static String bytesToHexString(List<Byte> src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.size() <= 0) {
            return null;
        }
        for (int i = 0; i < src.size(); i++) {
            int v = src.get(i) & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static byte[] short2BytesHighFirst(short value){
        byte[] src = new byte[2];
        src[0] = (byte) ((value>>8) & 0xFF);
        src[1] = (byte) ((value)& 0xFF);

        return src;
    }

    public static byte[] short2BytesLowFirst(short value){
        byte[] src = new byte[4];
        src[1] =  (byte) ((value>>8) & 0xFF);
        src[0] =  (byte) (value & 0xFF);
        return src;
    }

    public static short bytes2ShortWithIndex(byte[] value, int index){
        return (short) (((value[index + 1] << 8) | value[index] & 0xff));
    }

    public static short bytes2Short(byte[] value){
        return (short) ((0xff & value[0]) | (0xff00 & (value[1] << 8)));
    }

    public static short bytes2Short(List<Byte> value){
        return (short) (((value.get(1) << 8) | value.get(0) & 0xff));
    }

    public static byte[] hexStr2Bytes(String src)
    {
        int m=0,n=0;
        int l=src.length()/2;
        System.out.println(l);
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++)
        {
            m=i*2+1;
            n=m+1;
            ret[i] = Byte.decode("0x" + src.substring(i*2, m) + src.substring(m,n));
        }
        return ret;
    }

    public static byte[] toByteArray(String hexString) {
        if (TextUtils.isEmpty(hexString))
            throw new IllegalArgumentException("this hexString must not be empty");

        hexString = hexString.toLowerCase();
        final byte[] byteArray = new byte[hexString.length() / 2];
        int k = 0;
        for (int i = 0; i < byteArray.length; i++) {//因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
            byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
            byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
            byteArray[i] = (byte) (high << 4 | low);
            k += 2;
        }
        return byteArray;
    }

    /**
     * 卡uid转long
     * @param uid
     * @return
     */
    public static long convertUidToLong(byte[] uid){
        if(uid.length != 4){
            return -1;
        }
        long tmpLong = 0;
        for(int i=0;i<4;++i){
            tmpLong <<= 8;
            tmpLong |= (uid[4-i-1] & 0xff);
        }

        return tmpLong;
    }


    /**
     * hex字符串转byte数组
     * @param inHex 待转换的Hex字符串
     * @return  转换后的byte数组结果
     */
    public static byte[] hexToByteArray(String inHex){
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1){
            //奇数
            hexlen++;
            result = new byte[(hexlen/2)];
            inHex="0"+inHex;
        }else {
            //偶数
            result = new byte[(hexlen/2)];
        }
        int j=0;
        for (int i = 0; i < hexlen; i+=2){
            result[j]=hexToByte(inHex.substring(i,i+2));
            j++;
        }
        return result;
    }
    /**
     * Hex字符串转byte
     * @param inHex 待转换的Hex字符串
     * @return  转换后的byte
     */
    public static byte hexToByte(String inHex){
        return (byte)Integer.parseInt(inHex,16);
    }
}
