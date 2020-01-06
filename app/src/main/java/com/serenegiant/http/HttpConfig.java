package com.serenegiant.http;

/**
 * Created by zxj on 2017/8/25.
 */

public class HttpConfig {
    //武威
//    public static final String code = "wwjp";
    //酒泉
//    public static final String code = "jq";
    //甘南
//    public static final String code = "gnjp";
    //白银
//    public static final String code = "byjp";
    //张掖
//    public static final String code = "zyjp";
    //渭南
//    public static final String code = "wnjp";
    //汕尾
//    public static final String code = "swjp_test";
    //娄底
//    public static final String code = "ldjp";
    //四川
//    public static final String code = "scjp";
    //常德
//    public static final String code = "cdjp";
    //贵阳
    public static final String code = "gyjp";

    //测试
//    public static String SERVER_IP = "114.116.67.213";
//    public static int SERVER_PORT = 4655;

    //白银
//    public static String SERVER_IP = "111.20.168.154";
//    public static int SERVER_PORT = 1655;
    //39
//    public static String SERVER_IP = "111.20.168.154";
//    public static int SERVER_PORT = 3650;
    //贵阳
//    public static String SERVER_IP = "www.gyjponline.com";
//    public static int SERVER_PORT = 8080;

    //汕尾
//    public static String SERVER_IP = "swjs.jpfwpt.com";
//    public static int SERVER_PORT = 5655;

//    public static String SERVER_IP = "192.168.2.41";
//    public static int SERVER_PORT = 7777;


    //四川
//    public static String SERVER_IP = "scjs.jpfwpt.com";
//    public static int SERVER_PORT = 1660;
//    江西 娄底
//    public static String SERVER_IP = "ldjs.jpfwpt.com";
//    public static int SERVER_PORT = 7788;

    //甘肃
//    public static String SERVER_IP = "117.34.70.134";
//    public static int SERVER_PORT = 3003;

    //常德
//    public static String SERVER_IP = "cdjs.jpfwpt.com";
//    public static int SERVER_PORT = 5655;

    public static String SERVER_IP = "www.gyjponline.com";
    public static int SERVER_PORT = 8080;

    //渭南 http://117.34.73.53:2650
//    public static String SERVER_IP = "117.34.73.53";
//    public static int SERVER_PORT = 2650;

//    public static String SERVER_IP = "192.168.2.88";
//    public static int SERVER_PORT = 8899;

    //云南测试环境
//    public static String SERVER_IP = "114.116.89.28";
//    public static int SERVER_PORT = 4655;

    //汕尾测试环境
//    public static String SERVER_IP = "114.116.66.51";
//    public static int SERVER_PORT = 8089;

    public static String URL_FREFIX = "http://" + SERVER_IP + ":" + SERVER_PORT + "/";

    public static String updateVersionUrl = URL_FREFIX + "version/" + code + "/2";
    public static String locations = URL_FREFIX + "getregions/";
    public static String getNewApk = URL_FREFIX + "apk/";
    public static String checkFace = URL_FREFIX + "rest/matchPhoto";
}
