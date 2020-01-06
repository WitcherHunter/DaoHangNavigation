package com.serenegiant.ui;

import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.FragmentActivity;
import android.text.Selection;
import android.text.Spannable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.navigation.timerterminal.R;
import com.rscja.utility.StringUtility;
import com.serenegiant.AppContext;
import com.serenegiant.AppManager;
import com.serenegiant.UIHelper;
import com.serenegiant.utils.NetUtils;
import com.umeng.analytics.MobclickAgent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 应用程序Activity的基类
 */
public class BaseActivity extends FragmentActivity{

    private static final String TAG = "BaseActivity";

    public Animation shake;

    private NetwrokBroadcastReceiver mNetworkStateReceiver;

    private HomeKeyEventBroadCastReceiver mHomeKeyEventReceiver;
    /**
     * 是否监测网络
     */

    protected Boolean isRegisterBroadcastReceiver = true;

    protected TextView tv_title;
    protected String strNet = "";

    public AppContext appContext;// 全局Context

    protected boolean networkExist = false;

    // 是否允许全屏
    private boolean allowFullScreen = false;

    // 是否允许销毁
    private boolean allowDestroy = true;

    private View view;

    PowerManager powerManager = null;
    WakeLock wakeLock = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //电源管理器
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock WakeLockmWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP|PowerManager.FULL_WAKE_LOCK,"SimpleTimer");
        WakeLockmWakelock.acquire();//点亮
        // mWakeLock.release();//关闭

        //获取系统服务
        KeyguardManager mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        //初始化键盘锁，可以锁定或解开键盘锁
        KeyguardManager.KeyguardLock mKeyguardLock = mKeyguardManager.newKeyguardLock("unLock");
        //禁用显示键盘锁定
        mKeyguardLock.disableKeyguard();
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);//去掉信息栏

        shake = AnimationUtils.loadAnimation(this, R.anim.shake);

        allowFullScreen = false;
        // 添加Activity到堆栈
        AppManager.getAppManager().addActivity(this);

        appContext = (AppContext) getApplication();

        this.powerManager = (PowerManager) this
                .getSystemService(Context.POWER_SERVICE);
        this.wakeLock = this.powerManager.newWakeLock(
                PowerManager.FULL_WAKE_LOCK, "My Lock");

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            setTranslucentStatus(true);
//        }
        //view state bar
       // getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        getWindow().addFlags(WindowManager.LayoutParams.TYPE_STATUS_BAR);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//禁止屏幕休眠


        mHomeKeyEventReceiver = new HomeKeyEventBroadCastReceiver();
    }

    class HomeKeyEventBroadCastReceiver extends BroadcastReceiver {

        static final String SYSTEM_REASON = "reason";
        static final String SYSTEM_HOME_KEY = "homekey";//home key
        static final String SYSTEM_RECENT_APPS = "recentapps";//long home key

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("com.rscja.android.KEY_DOWN")) {
                int reason = intent.getIntExtra("Keycode",0);
                //getStringExtra
//                boolean long1 = intent.getBooleanExtra("Pressed",false);
                // home key处理点
//                Toast.makeText(getApplicationContext(), "home key="+reason+",long1="+long1, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @TargetApi(19)
    public void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    protected void onPause() {
        if (isRegisterBroadcastReceiver) {
            unregBroadcastReceiver();
        }
        unregisterReceiver(mHomeKeyEventReceiver);
        this.wakeLock.release();

        MobclickAgent.onPause(this);

        super.onPause();

        if (UIHelper.getToast() != null) {
            UIHelper.getToast().cancel();
        }

    }

    @Override
    protected void onResume() {
        registerReceiver(mHomeKeyEventReceiver, new IntentFilter("com.rscja.android.KEY_DOWN"));

        if (isRegisterBroadcastReceiver) {
            regBroadcastReceiver();
        }

        this.wakeLock.acquire();

        MobclickAgent.onResume(this);

        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 结束Activity&从堆栈中移除
        AppManager.getAppManager().finishActivity(this);
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && view != null) {
            view.onKeyDown(keyCode, event);
            if (!allowDestroy) {
                return false;
            }
        } else if (KeyEvent.KEYCODE_HOME == keyCode) {
            Toast.makeText(getApplicationContext(), "HOME 键已被禁用...", Toast.LENGTH_LONG).show();
        }
        return super.onKeyDown(keyCode, event); // 不会回到 home 页面
    }

    protected void regBroadcastReceiver() {

        if (isRegisterBroadcastReceiver) {

            Log.i("BaseActivity", "regBroadcastReceiver()");

            // 网络连接判断
//            if (!appContext.isNetworkConnected())
//                UIHelper.ToastMessage(this, R.string.network_not_connected);

            mNetworkStateReceiver = new NetwrokBroadcastReceiver();

            // 注册网络监听
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            registerReceiver(mNetworkStateReceiver, filter);
//             isRegisterBroadcastReceiver = true;
        }
    }


    protected void unregBroadcastReceiver() {

        if (mNetworkStateReceiver != null && isRegisterBroadcastReceiver) {

            Log.i("BaseActivity", "unregBroadcastReceiver()");

            unregisterReceiver(mNetworkStateReceiver); // 取消监听
            // isRegisterBroadcastReceiver = false;
        }
    }

    protected class NetwrokBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {

            checkNet();

        }

    }

    protected void checkNet() {

        Log.i("BaseActivity", "checkNet()");

        if (!NetUtils.isNetworkConnected(this)) {

            networkExist = false;

            strNet = "(" + getString(R.string.msg_network_unavailable) + ")";

            //UIHelper.ToastMessage(this, R.string.msg_network_none);
        } else {
            networkExist = true;
            strNet = "(" + NetUtils.getNetworkType(this) + ")";
            //UIHelper.ToastMessage(this, R.string.msg_network_ok);
        }
    }
}
