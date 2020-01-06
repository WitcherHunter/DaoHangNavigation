package com.serenegiant.finger;

import com.yz.lz.modulapi.JNIUtils;

public class FingerprintAPI {

    public static void free(){
        JNIUtils.getInstance().closeFingerDevice();
    }

    public static boolean init(){
        return JNIUtils.getInstance().openFingerDevice();
    }


    public static boolean matchFingerprint(byte[] fp){
        JNIUtils.getInstance().reset();
        if (!JNIUtils.getInstance().upChar(fp))
            return false;
        return JNIUtils.getInstance().matchFingerprint();
    }
}
