package com.serenegiant.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.view.Display;


public class IUtil {
    private static int displaywidth;
    private static int displayHeight;
    public static double Distance = 0;
    public static boolean mDistance = false;
    public static boolean WhileB = true;
    public static String Num;

    /**
     * 管理卡密码
     */
    public static final String managerCardPassword = "123456";

    public static boolean isFaceOpen = false;

    //指纹模块是否正常
    public static boolean isFingerEnable = false;
    //rfid模块是否正常
    public static boolean isRfidEnable = false;

    /*
    * 是否使用GPS模块定位
    * 现在是GPS 和 北斗模块双模式分开
    * */
    public static boolean IsGPS = false;
    /*
    * 是否第一次登录
    * */
    public static boolean IsFirstIn = true;
    /*
    * sharepreference 最大速度
    * */
    public static String  MAX_SPEED = "MAX_SPEED";
    /*
      * sharepreference 学员签退
      * */
    public static String  STOUT = "STOUT";
    /*
    * sharepreference 教练签退
    * */
    public static String  CACHOUT = "CACHOUT";
    /*
    * sharepreference 首次登录
    * */
    public static String  FirstLogin = "FirstLogin";

    /*
    * 是否超出速度上线
    * */
    public static boolean SpeedOut = false;
    /*
    * 是否超出培训区域
    * */
    public static boolean EnclosureOut = false;
//    /*
//    * 卡密码获取
//    * */
//    public static native String PassWord();
    /*
    * 从数据库查出签到学员数目，主界面上面显示所用
    * */
    public static int signnumStudent;

    /*
    * 从数据库查出签到教练数目，主界面上面显示所用
    * */
    public static int signnumTeacher;
    /*
    * 从数据库查出照片数目，主界面上面显示所用
    * */
    public static int getphotoNun;
    /*
    * 从数据库查出位置轨迹数目，主界面上面显示所用
    * */
    public static int getlocationNun;
    /*
    * 从数据库查出培训学时数目，主界面上面显示所用
    * */
    public static int getclassSignNun;
    public static boolean OnClass = false;

    public static void init(Activity activity) {
        // 获取屏幕的宽度
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        displaywidth = size.x;
        displayHeight = size.y;
    }

    public static int getDisplaywidth() {
        return displaywidth;
    }

    public static int getDisplayHeight() {
        return displayHeight;
    }

    public static void installApk(Context context, String fileName) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + fileName),"application/vnd.android.package-archive");
        context.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
//    static {
//        System.loadLibrary("native-lib");
//    }

    /**
     * 判断是否包含SIM卡
     *
     * @return 状态
     */
    public static boolean hasSimCard( Context context) {
        TelephonyManager telMgr = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telMgr.getSimState();
        boolean result = true;
        switch (simState) {
            case TelephonyManager.SIM_STATE_ABSENT:
                result = false; // 没有SIM卡
                break;
            case TelephonyManager.SIM_STATE_UNKNOWN:
                result = false;
                break;
        }
        return result;
    }
}
