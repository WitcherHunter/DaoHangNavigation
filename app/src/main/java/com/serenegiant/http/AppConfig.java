package com.serenegiant.http;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Properties;

public class AppConfig {
	private final static String APP_CONFIG = "config";
	public final static String TEMP_IMAGE = "temp_image";
	public final static String TEMP_NEWSLIST = "temp_newslist";
	public final static String CONF_APP_UNIQUEID = "APP_UNIQUEID";
	public final static String CONF_COOKIE = "cookie";
	public final static String CONF_LOAD_IMAGE = "perf_loadimage";
	public final static String CONF_CHECKUP = "perf_checkup";
	public final static String CONF_VOICE = "perf_voice";
	public final static String CONF_FONT_SIZE = "font_size";
	
	public static final int KEYCODE_SCAN = 139;

	public final static String SAVE_IMAGE_PATH = "save_image_path";
	public final static String DEFAULT_SAVE_IMAGE_PATH = Environment
			.getExternalStorageDirectory()
			+ File.separator
			+ "Parking"
			+ File.separator;

	public final static String DEFAULT_SAVE_PATH = Environment
			.getExternalStorageDirectory()
			+ File.separator
			+ "chainway"
			+ File.separator;

	// 与服务器交互时间间隔，毫??
	public final static int UP_TIME = 5000;
	// 服务器IP
	public static String SERVER_IP = "192.168.2.45";
	// 服务器端??
	public static int SERVER_PORT = 8080;
	// 数据库名??
	public final static String APP_DB_NAME = "ParkDB.db";

	private static Context mContext;
	private static AppConfig appConfig;
	private static SoundPool soundPool;
	private float volumnRatio;
	private static AudioManager am;
	static HashMap<Integer, Integer> soundMap = new HashMap<Integer, Integer>();

	public static AppConfig getAppConfig(Context context) {
		if (appConfig == null) {
			appConfig = new AppConfig();
			appConfig.mContext = context;
			init();
		}
		return appConfig;
	}
	private static void init(){
//		soundPool = new SoundPool(10, AudioManager.STREAM_RING, 5);
//		soundMap.put(1, soundPool.load(mContext, R.raw.check_failure, 1));
//		soundMap.put(2, soundPool.load(mContext, R.raw.check_ok, 1));
//		soundMap.put(3, soundPool.load(mContext, R.raw.barcodebeep,1));
		am = (AudioManager) mContext.getSystemService("audio");// 实例化AudioManager对象
	}
	
	/**
	 * 播放提示??
	 * 
	 * @param id
	 *            成功1，失??
	 */
	public  void playSound(int id) {

		float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_RING); // 返回当前AudioManager对象的最大音量??
		float audioCurrentVolumn = am.getStreamVolume(AudioManager.STREAM_RING);// 返回当前AudioManager对象的音量??
		volumnRatio = audioCurrentVolumn / audioMaxVolumn;


			try {
				soundPool.play(soundMap.get(id), volumnRatio, // 左声道音??
						volumnRatio, // 右声道音??
						1, // 优先级，0为最??
						0, // 循环次数??无不循环??1无永远循??
						1 // 回放速度 ，该值在0.5-2.0之间??为正常????
						);
			} catch (Exception e) {
				e.printStackTrace();
		}
	}


	/**
	 * 获取Preference设置
	 */
	public static SharedPreferences getSharedPreferences(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

	/**
	 * 判断当前版本是否兼容目标版本的方??
	 * 
	 * @param VersionCode
	 * @return
	 */
	public static boolean isMethodsCompat(int VersionCode) {
		int currentVersion = android.os.Build.VERSION.SDK_INT;
		return currentVersion >= VersionCode;
	}

	/**
	 * 是否加载显示文章图片
	 */
	public static boolean isLoadImage(Context context) {
		return getSharedPreferences(context).getBoolean(CONF_LOAD_IMAGE, true);
	}


	public String getCookie() {
		return get(CONF_COOKIE);
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

			props.load(fis);
		} catch (Exception e) {
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return props;
	}

	private void setProps(Properties p) {
		FileOutputStream fos = null;
		try {

			// 把config建在(自定??app_config的目录下
			File dirConf = mContext.getDir(APP_CONFIG, Context.MODE_PRIVATE);
			File conf = new File(dirConf, APP_CONFIG);
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
