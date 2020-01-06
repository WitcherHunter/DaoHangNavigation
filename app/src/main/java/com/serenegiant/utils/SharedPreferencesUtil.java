package com.serenegiant.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by zxj on 2017/8/21.
 */

public class SharedPreferencesUtil {

    public static final String REGISTER_INFO_FILE = "register_info_file";

    public static final String REGISTER_SUCCESS = "register_success";
    public static final String REGISTER_TYPE = "TYPE";
    public static final String TIME_OUT = "timeout";

    private	static SharedPreferences share;
    private Context content;
    private volatile static SharedPreferencesUtil instance;

    public static SharedPreferencesUtil getInstance(Context content) {
        if (instance == null) {
            synchronized (SharedPreferencesUtil.class) {
                if (instance == null) {
                    instance = new SharedPreferencesUtil(content);
                    share = content.getSharedPreferences("SXDH",0);
                }
            }
        }
        return instance;
    }

    public SharedPreferencesUtil(Context content){
        this.content = content;
    }

    public void setsharepreferences(String key, String value){
        SharedPreferences.Editor editor = share.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void setsharepreferences(String key, int value){
        SharedPreferences.Editor editor = share.edit();
        editor.putInt(key, value);
        editor.apply();
    }
    public void setsharepreferencesBoolean(String key, Boolean value){
        SharedPreferences.Editor editor = share.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
    public void setsharepreferences(String[] key, String[] value){
        SharedPreferences.Editor editor = share.edit();
        for(int i = 0;i<key.length;i++){
            editor.putString(key[i], value[i]);
        }
        editor.apply();
    }

    public String getsharepreferences(String key){
        return share.getString(key, "");
    }
    public int getsharepreferencesInt(String key){
        return share.getInt(key, -1);
    }
    public Boolean getsharepreferencesBoolean(String key){
        return share.getBoolean(key, false);
    }

}
