package com.serenegiant;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import com.navigation.timerterminal.R;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.serenegiant.business.obd.NewObdService;
import com.serenegiant.constants.CourseCodeGenerator;
import com.serenegiant.entiy.DaoMaster;
import com.serenegiant.entiy.DaoSession;
import com.serenegiant.entiy.WarningFlag;
import com.serenegiant.receiver.GeoFenceReceiver;
import com.serenegiant.services.NewLocationService;
import com.serenegiant.utils.MethodsCompat;
import com.serenegiant.utils.MilesCalculateUtil;
import com.serenegiant.utils.SoundManage;
import com.serenegiant.utils.StringUtils;
import com.serenegiant.utils.Utils;
import com.umeng.commonsdk.UMConfigure;

import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import static com.serenegiant.services.NewLocationService.GEOFENCE_BROADCAST_ACTION;

/**
 * 全局应用程序类：用于保存和调用全局应用配置及访问网络数据
 */
public class AppContext extends Application{

    private static final String TAG = "AppContext";


    public static boolean Stopped = false;
    private static String model = android.os.Build.DISPLAY.toUpperCase();

    public static final int NETTYPE_WIFI = 0x01;
    public static final int NETTYPE_CMWAP = 0x02;
    public static final int NETTYPE_CMNET = 0x03;

    public static int PAGE_SIZE = 20;// 默认分页大小
    private static final int CACHE_TIME = 60 * 60000;// 缓存失效时间

    private String saveImagePath;// 保存图片路径

    private Hashtable<String, Object> memCacheRegion = new Hashtable<String, Object>();

    HashMap<Integer, Integer> soundMap = new HashMap<Integer, Integer>();
    private SoundPool soundPool;
    private float volumnRatio;
    private AudioManager am;

    private static DaoSession daoSession;

    private NewObdService obdService;
    public static boolean isObdOpen = false;
    public static long lastCheckObdTime = System.currentTimeMillis();
    private TimerTask obdConnectionCheckTask = new TimerTask() {
        @Override
        public void run() {
            isObdOpen = System.currentTimeMillis() - lastCheckObdTime < 2000;
            if (isObdOpen)
                WarningFlag.clearWarning(WarningFlag.WARNING_OBD_NOT_CONNECTED);
            else
                WarningFlag.setWarning(WarningFlag.WARNING_OBD_NOT_CONNECTED);
        }
    };

    //围栏变化广播接收器
    public static GeoFenceReceiver mGeoFenceReceiver;
    //定位类
    private NewLocationService mLocationService;

    @Override
    public void onCreate() {

        super.onCreate();
        //数据库文件变成文件夹删除

        UMConfigure.init(this,"5cdfa1e83fc195c2e7000dda","Umeng",UMConfigure.DEVICE_TYPE_PHONE,null);

        deleteFile(Environment.getExternalStorageDirectory().toString() + File.separator + "RecordList");
//        // 注册App异常崩溃处理器
        Thread.setDefaultUncaughtExceptionHandler(AppException.getAppExceptionHandler());
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());

        MilesCalculateUtil.init();
        CourseCodeGenerator.init();
        WarningFlag.initFlagArray();

        initLocation(getImei());

        initObdSerialPort();

        // TODO: 2019-08-09 暂时屏蔽
//        new Timer().schedule(obdConnectionCheckTask,1000,1000);

        init();

        initImageLoader(getApplicationContext());//初始化图片加载列表

        initGreenDao();
    }

    /**
     * 初始化obd相关串口
     */
    private boolean initObdSerialPort(){
//        SWDObdService swdObdService = new SWDObdService();
//        NewObdService newObdService = new NewObdService();
//
//        boolean b2 = newObdService.open("ttyS6",57600);
//        boolean b1 = swdObdService.open("ttyS5",9600);
//
//        return b1 || b2;

        return new NewObdService().open("ttyS6",57600);
    }

    /**
     * 初始化定位
     */
    private void initLocation(String imei){
        IntentFilter filter = new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(GEOFENCE_BROADCAST_ACTION);
        AppContext.mGeoFenceReceiver = new GeoFenceReceiver();
        registerReceiver(AppContext.mGeoFenceReceiver, filter);

        mLocationService = new NewLocationService(this);
        mLocationService.startLocation(imei);
    }

//    private void unregisterGeoReceiverAndStopLocation(){
//        if (mGeoFenceReceiver != null)
//            unregisterReceiver(mGeoFenceReceiver);
//        if (mLocationService != null)
//            mLocationService.stopLocation();
//    }

    private void initGreenDao(){
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "records.db");
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster master = new DaoMaster(db);
        daoSession = master.newSession();
    }

    public static DaoSession getDaoSession(){
        return daoSession;
    }

    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }


    /**
     * 初始化
     */
    private void init() {
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
        soundMap.put(1, soundPool.load(this, R.raw.barcodebeep, 1));
        soundMap.put(2, soundPool.load(this, R.raw.serror, 1));
        soundMap.put(3, soundPool.load(this, R.raw.camera, 1));
        am = (AudioManager) this.getSystemService(AUDIO_SERVICE);// 实例化AudioManager对象


        Utils.createPath(AppConfig.DEFAULT_SAVE_PATH);

        SoundManage.initOffline(this);
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    /**
     * 获取设备id
     *
     * @return
     */
    public String getDeviceId() {
        String id = getImei();

        if (!StringUtils.isEmpty(id)) {
            return id;
        }

        id = getLocalMacAddress();

        if (!StringUtils.isEmpty(id) && !"NULL".equals(id)) {
            return id;
        }

        id = getSerialNum();

        if (!StringUtils.isEmpty(id)) {
            return id;
        }

        return "UNKNOW";

    }

    /**
     * 获取设备序列号
     *
     * @return
     */
    public String getSerialNum() {
        return android.os.Build.SERIAL;
    }

    /**
     * 获取自定义型号
     *
     * @return
     */
    public String getCustModle() {
        return model;
    }

    /**
     * 获取设备mac地址 此方法需要保证WIFI在本次开机以来曾经是打开过的，否则会返回null。
     *
     * @return
     */
    public String getLocalMacAddress() {
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return (info == null) ? "NULL" : info.getMacAddress();
    }

    /**
     * 获取设备的IMEI
     *
     * @return
     */
    public String getImei() {
        return ((TelephonyManager) getSystemService(TELEPHONY_SERVICE))
                .getDeviceId();
    }

    /**
     * 检测当前系统声音是否为正常模式
     *
     * @return
     */
    public boolean isAudioNormal() {
        AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        return mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
    }

    /**
     * 应用程序是否发出提示音
     *
     * @return
     */
    public boolean isAppSound() {
        return isAudioNormal() && isVoice();
    }

    /**
     * 是否发出提示音
     *
     * @return
     */
    public boolean isVoice() {
        String perf_voice = getProperty(AppConfig.CONF_VOICE);
        // 默认是开启提示声音
        if (StringUtils.isEmpty(perf_voice))
            return true;
        else
            return StringUtils.toBool(perf_voice);
    }

    /**
     * 设置是否发出提示音
     *
     * @param b
     */
    public void setConfigVoice(boolean b) {
        setProperty(AppConfig.CONF_VOICE, String.valueOf(b));
    }

    /**
     * 播放提示音
     *
     * @param id 成功1，失败2
     */
    public void playSound(int id) {

        float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC); // 返回当前AudioManager对象的最大音量值
        float audioCurrentVolumn = am.getStreamVolume(AudioManager.STREAM_MUSIC);// 返回当前AudioManager对象的音量值
        volumnRatio = audioCurrentVolumn / audioMaxVolumn;

        if (isAppSound()) {

            try {
                soundPool.play(soundMap.get(id), volumnRatio, // 左声道音量
                        volumnRatio, // 右声道音量
                        1, // 优先级，0为最低
                        0, // 循环次数，0无不循环，-1无永远循环
                        1 // 回放速度 ，该值在0.5-2.0之间，1为正常速度
                );
            } catch (Exception e) {
                e.printStackTrace();

                UIHelper.ToastMessage(this, "playSound error");

            }

        }
    }

    /**
     * 获取当前网络类型
     *
     * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
     */
    public int getNetworkType() {
        int netType = 0;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            String extraInfo = networkInfo.getExtraInfo();
            if (!StringUtils.isEmpty(extraInfo)) {
                if (extraInfo.toLowerCase().equals("cmnet")) {
                    netType = NETTYPE_CMNET;
                } else {
                    netType = NETTYPE_CMWAP;
                }
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = NETTYPE_WIFI;
        }
        return netType;
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

    /**
     * 获取App安装包信息
     *
     * @return
     */
    public PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null)
            info = new PackageInfo();
        return info;
    }


    /**
     * 清除app缓存
     */
    public void clearAppCache() {
        // // 清除webview缓存
        // File file = CacheManager.getCacheFileBaseDir();
        // if (file != null && file.exists() && file.isDirectory()) {
        // for (File item : file.listFiles()) {
        // item.delete();
        // }
        // file.delete();
        // }
        deleteDatabase("webview.db");
        deleteDatabase("webview.db-shm");
        deleteDatabase("webview.db-wal");
        deleteDatabase("webviewCache.db");
        deleteDatabase("webviewCache.db-shm");
        deleteDatabase("webviewCache.db-wal");
        // 清除数据缓存
        clearCacheFolder(getFilesDir(), System.currentTimeMillis());
        clearCacheFolder(getCacheDir(), System.currentTimeMillis());
        // 2.2版本才有将应用缓存转移到sd卡的功能
        if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
            clearCacheFolder(MethodsCompat.getExternalCacheDir(this),
                    System.currentTimeMillis());
        }
        // 清除编辑器保存的临时内容
        Properties props = getProperties();
        for (Object key : props.keySet()) {
            String _key = key.toString();
            if (_key.startsWith("temp"))
                removeProperty(_key);
        }
    }

    /**
     * 清除缓存目录
     *
     * @param dir     目录
     * @param curTime 当前系统时间
     * @return
     */
    private int clearCacheFolder(File dir, long curTime) {
        int deletedFiles = 0;
        if (dir != null && dir.isDirectory()) {
            try {
                for (File child : dir.listFiles()) {
                    if (child.isDirectory()) {
                        deletedFiles += clearCacheFolder(child, curTime);
                    }
                    if (child.lastModified() < curTime) {
                        if (child.delete()) {
                            deletedFiles++;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return deletedFiles;
    }


    /**
     * 获取内存中保存图片的路径
     *
     * @return
     */
    public String getSaveImagePath() {
        return saveImagePath;
    }

    public boolean containsProperty(String key) {
        Properties props = getProperties();
        return props.containsKey(key);
    }

    public void setProperties(Properties ps) {
        AppConfig.getAppConfig(this).set(ps);
    }

    public Properties getProperties() {
        return AppConfig.getAppConfig(this).get();
    }

    public void setProperty(String key, String value) {
        AppConfig.getAppConfig(this).set(key, value);
    }

    public String getProperty(String key) {
        return AppConfig.getAppConfig(this).get(key);
    }

    public String getSharedPreferences(String key) {
        return AppConfig.getAppConfig(this).getSharedPreferences(key);
    }

    public void setSharedPreferences(String key, String value) {
        AppConfig.getAppConfig(this).setSharedPreferences(key, value);
    }

    public void removeProperty(String... key) {
        AppConfig.getAppConfig(this).remove(key);
    }

    public double getDensity(Activity act) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = act.getResources().getDisplayMetrics();
        return dm.density;
    }


    /**
     * 用来判断服务是否运行.
     *
     * @param mContext
     * @param className 判断的服务名字
     * @return true 在运行 false 不在运行
     */
    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(100);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {


            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    public static void initImageLoader(Context context) {
        //缓存文件的目录
        File cacheDir = StorageUtils.getOwnCacheDirectory(context, "imageloader/Cache");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .memoryCacheExtraOptions(480, 800) // max width, max height，即保存的每个缓存文件的最大长宽
                .threadPoolSize(3) //线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator()) //将保存的时候的URI名称用MD5 加密
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现
                .memoryCacheSize(2 * 1024 * 1024) // 内存缓存的最大值
                .diskCacheSize(50 * 1024 * 1024)  // 50 Mb sd卡(本地)缓存的最大值
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                // 由原先的discCache -> diskCache
                .diskCache(new UnlimitedDiscCache(cacheDir))//自定义缓存路径
                .imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
                .writeDebugLogs() // Remove for release app
                .build();
        //全局初始化此配置
        ImageLoader.getInstance().init(config);
    }
}
