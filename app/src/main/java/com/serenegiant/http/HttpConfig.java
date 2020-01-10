package com.serenegiant.http;

/**
 * Created by zxj on 2017/8/25.
 */

public class HttpConfig {
    public static final String code = "gyjp";

    public static final String SERVER_IP = "218.77.100.37";
    public static final int SERVER_PORT = 8089;

    public static final String URL_FREFIX = "http://" + SERVER_IP + ":" + SERVER_PORT + "/api/appdev/";

    //人脸识别url前缀
    public static final String PHOTO_PREFIX = "http://" + SERVER_IP + ":" + SERVER_PORT;

    //获取设备信息
    public static final String deviceInfo = URL_FREFIX + "getdeviceinfo";
    //验证人脸识别是否打开
    public static final String faceOpen = URL_FREFIX + "getstudentfacestate";
    //获取学员信息
    public static final String studentLogin = URL_FREFIX + "getstudentinfo";
    //获取教练信息
    public static final String coachLogin = URL_FREFIX + "getcoachinfo";
    //获取电子围栏
    public static final String locations = URL_FREFIX + "getregions";
    //人脸识别
    public static final String checkFace = URL_FREFIX + "getstudentphotoscore";
    //在线升级
    public static String updateVersionUrl = URL_FREFIX + "version/" + code + "/2";
    //下载apk
    public static String getNewApk = URL_FREFIX + "apk/";
}
