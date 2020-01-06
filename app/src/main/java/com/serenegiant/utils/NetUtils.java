package com.serenegiant.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class NetUtils {
    /**
     * 检测网络是否可用
     *
     * @return
     */
    public static boolean isNetworkConnected(Activity act) {
        ConnectivityManager cm = (ConnectivityManager) act
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    /**
     * 获取当前网络类型
     *
     * @return WiFi或移动网络CDMA 网络类型为CDMAEDGE 网络类型为EDGE EVDO_0 网络类型为EVDO0EVDO_A
     * 网络类型为EVDOA GPRS 网络类型为GPRSHSDPA 网络类型为HSDPA HSPA 网络类型为HSPAHSUPA
     * 网络类型为HSUPA UMTS 网络类型为UMTS
     */
    public static String getNetworkType(Activity act) {
        String netType = "";
        ConnectivityManager connectivityManager = (ConnectivityManager) act
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {

            // Log.i("NetUtils", "SubtypeName=" + networkInfo.getSubtypeName());

            netType = networkInfo.getSubtypeName().toUpperCase();

            // String extraInfo = networkInfo.getExtraInfo();
            // if (extraInfo != null) {
            // if (extraInfo.toLowerCase().equals("cmnet")) {
            // netType = "CMNET";
            // } else {
            // netType = "CMWAP";
            // }
            // }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = "WIFI";
        }
        return netType;
    }

    private static int getNetworkClass(int networkType) {
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return 2;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return 3;
            case TelephonyManager.NETWORK_TYPE_LTE:
                return 4;
            default:
                return -1;
        }
    }

    public static String getNetworkClass(Activity act) {
        String netType = "";
        ConnectivityManager connectivityManager = (ConnectivityManager) act
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {

            // Log.i("NetUtils", "SubtypeName=" + networkInfo.getSubtypeName());

            netType = networkInfo.getSubtypeName().toUpperCase();

            switch (getNetworkClass(networkInfo.getSubtype())) {
                case 2:
                    return "2G";
                case 3:
                    return "3G";
                case 4:
                    return "4G";
            }

            // String extraInfo = networkInfo.getExtraInfo();
            // if (extraInfo != null) {
            // if (extraInfo.toLowerCase().equals("cmnet")) {
            // netType = "CMNET";
            // } else {
            // netType = "CMWAP";
            // }
            // }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = "WIFI";
        }
        return netType;
    }

}
