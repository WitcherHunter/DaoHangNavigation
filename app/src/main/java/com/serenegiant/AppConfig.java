package com.serenegiant;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class AppConfig {
    private final static String APP_CONFIG = "config";
    public final static String CONF_CHECKUP = "perf_checkup";
    public final static String CONF_VOICE = "perf_voice";
    public final static boolean  DEBUG_EN = true;

    public final static String DEFAULT_SAVE_PATH = Environment.getExternalStorageDirectory()
            + File.separator
            + "CWJ"
            + File.separator;
    public static final String RECORD_SAVE_PATH = Environment.getExternalStorageDirectory().toString()+File.separator + "RecordList" + File.separator + "Record";
    //参数保存目录
    public final static String PARAMETER_SAVE_PATH = Environment.getExternalStorageDirectory().toString()+File.separator+"RecordList"+File.separator+"Config";
    //参数保存目录  RSA数据保存路径
    public final static String REGISTER_SAVE_PATH = Environment.getExternalStorageDirectory().toString()+File.separator+"RecordList"+File.separator+"Register";
    //照片保存地址路径
    public final static String PHOTO_SAVA_DERECTORY = Environment.getExternalStorageDirectory().toString()+File.separator+"RecordList"+File.separator+"Photos";
    //视频保存地址路径
    public final static String VIDEO_SAVE_DIRECTORY = Environment.getExternalStorageDirectory().toString()+File.separator+"RecordList"+File.separator+"Videos";
    //学时数据保存地址路径
    public final static String TRAIN_DATA_SAVE_DIRECTORY = Environment.getExternalStorageDirectory().toString()+File.separator+"RecordList"+File.separator+"DB";
    //考试数据保存目录
    public final static String EXAMINE_RECORD_LIST_DIRECTORY = "/storage/sdcard2";//Environment.getExternalStorageDirectory().toString();
    //程序运行日志
    public final static String HAMBOBO_DEBUG_INFON_ADDR = Environment.getExternalStorageDirectory().toString();
    //下载文件保存路径
    public final static String UPDATE_FILE_CACHE_DIRECTORY = Environment.getExternalStorageDirectory().toString()+File.separator+"update";
    //SD卡导出数据路径
//    public final static String EXPORT_FILE_DIRECTORY = Environment.getExternalStorageDirectory().toString()+File.separator+"Export";//"/storage/sdcard2";
    public final static String EXPORT_FILE_DIRECTORY ="/storage/sdcard2/Export";
    // 与服务器交互时间间隔，毫秒
    public final static int UP_TIME = 5000;
    // 数据库名称
    public final static String TRAIN_LIST_FILE_NAME = "TrainRecord.db";
    //远程升级包地址
    public final static String UPDATE_VERSION = Environment.getExternalStorageDirectory().toString()+File.separator + "RecordList" + File.separator + "Update";

    private Context mContext;
    private static AppConfig appConfig;
    public final static String UNLOCK_PWD = "sxdhjs";

    public static AppConfig getAppConfig(Context context) {
        if (appConfig == null) {
            appConfig = new AppConfig();
            appConfig.mContext = context;
        }
        return appConfig;
    }

    /**
     * 获取Preference设置
     */
    public static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }


    public void setSharedPreferences(String key, String value) {
        SharedPreferences preferences =mContext.getSharedPreferences("mac",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getSharedPreferences(String key) {
        SharedPreferences preferences = mContext.getSharedPreferences("mac",
                Context.MODE_PRIVATE);
        return preferences.getString(key, "");
    }

    /**
     * 判断当前版本是否兼容目标版本的方法
     *
     * @param VersionCode
     * @return
     */
    public static boolean isMethodsCompat(int VersionCode) {
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        return currentVersion >= VersionCode;
    }



    public String get(String key) {
        Properties props = get();
        return (props != null) ? props.getProperty(key) : null;
    }

    public Properties get() {
        FileInputStream fis = null;
        Properties props = new Properties();
        try {

            // 读取app_config目录下的config
            File dirConf = mContext.getDir(APP_CONFIG, Context.MODE_PRIVATE);
            fis = new FileInputStream(dirConf.getPath() + File.separator
                    + APP_CONFIG);



            Log.i("AppConfig","********* available ="+fis.available()+"  exists="+dirConf.exists());

            props.load(fis);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return props;
    }

    private void setProps(Properties p) {
        FileOutputStream fos = null;
        try {

            // 把config建在(自定义)app_config的目录下
            File dirConf = mContext.getDir(APP_CONFIG, Context.MODE_PRIVATE);



            File conf = new File(dirConf, APP_CONFIG);

            Log.i("AppConfig","*********setProps() exists="+conf.exists());


            fos = new FileOutputStream(conf);

            p.store(fos, null);
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
            }
        }
    }

    public void set(Properties ps) {
        Properties props = get();
        props.putAll(ps);
        setProps(props);
    }

    public void set(String key, String value) {
        Log.i("MY", "AppConfig.set " + value);

        Properties props = get();
        props.setProperty(key, value);
        setProps(props);
    }

    public void remove(String... key) {
        Properties props = get();
        for (String k : key)
            props.remove(k);
        setProps(props);
    }

}
