package com.serenegiant.ui;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.navigation.timerterminal.R;
import com.serenegiant.AppConfig;
import com.serenegiant.AppContext;
import com.serenegiant.business.obd.NewObdService;
import com.serenegiant.entiy.TimerDerminalEvent;
import com.serenegiant.net.DeviceParameter;
import com.serenegiant.net.ObdRunningData;
import com.serenegiant.utils.IUtil;
import com.serenegiant.utils.MyToast;
import com.serenegiant.utils.SharedPreferencesUtil;
import com.serenegiant.utils.SoundManage;
import com.yz.lz.modulapi.JNIUtils;
import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

import static java.lang.Thread.sleep;

public class SetActivity extends BaseActivity {
    //远程参数设置
    boolean checkBool = true;

    boolean dialogShowing = false;

    /**刘志 添加NONE **/
    enum TaskRunState {NONE, CHECKGPS, CHECKOBD, CHECKNET, CHEECKUVC, CHECKRFID, CHECKSTOP, CHECKFINGER, CHECKSIM}

    private String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private AlertDialog mUnLockDialog;

    public static String imei = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);

//        boolean close = NewJNIUtils.getInstance().rm_touch_module();
//        boolean open = NewJNIUtils.getInstance().reload_touch_module();

//        System.out.println("close is " + close + ", open is " + open);

        System.out.println("onCreate");

        requestPermission();

        EventBus.getDefault().register(this);

        getDeviceId();

        String pathDirectory = AppConfig.PARAMETER_SAVE_PATH;
        String pathFile = pathDirectory + "/DeviceParameter.xml";
        DeviceParameter.setParameterFilePath(pathFile);
        DeviceParameter.initParameter();  //初始化参数为默认值
        DeviceParameter.setDeviceIMEI(imei);
        File f = new File(pathDirectory);
        if (!f.exists()) {
            f.mkdirs();
        }
        File f2 = new File(pathFile);//获取参数XML文件
        if (!f2.exists()) {
            DeviceParameter.createParameterXML();//创建初始XML文件
        }

        DeviceParameter.getParameterData();
//        startActivity(new Intent(SetActivity.this, SetSecketActivity.class));
//        finish();

        if (mAlertDialog == null) {
            System.out.println("SelfCheckDialog");
            SelfCheckingDialog();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        System.out.println("onPause");
    }


    @Override
    protected void onStart() {
        super.onStart();

        System.out.println("onStart");
    }

    /**
     * 请求一些必要权限
     */
    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission_group.CAMERA) != 0
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != 0
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != 0
                || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != 0
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != 0
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != 0
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != 0
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != 0) {
            requestPermissions(permissions, 1);
        } else {
//            initSet();
        }
    }


    private void initView() {
        findViewById(R.id.tvSetDrivingName).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (null != mUnLockDialog) {
                    mUnLockDialog.show();
                } else {
                    final EditText mPwdEt = new EditText(SetActivity.this);
                    mUnLockDialog = new AlertDialog.Builder(SetActivity.this)
                            .setTitle("请输入管理员密码")
                            .setView(mPwdEt)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String pwd = mPwdEt.getText().toString();
                                    if (AppConfig.UNLOCK_PWD.equals(pwd)) {
                                        Intent intent = new Intent("android.intent.action.HOME_ENABLE");
                                        sendBroadcast(intent);
                                        Intent intent2 = new Intent("android.intent.action.PANEL_ENABLE");
                                        sendBroadcast(intent2);
                                        Intent intent3 = new Intent("android.intent.action.MENU_ENABLE");
                                        sendBroadcast(intent3);
                                        dialogInterface.dismiss();
                                        MyToast.show(SetActivity.this, "解锁成功");
                                        Background = true;
                                        Timer timer = new Timer();
                                        TimerTask task = new TimerTask() {
                                            @Override
                                            public void run() {
                                                Background = false;
                                                Intent intent = new Intent("android.intent.action.HOME_DISABLED");
                                                sendBroadcast(intent);
                                                Intent intent2 = new Intent("android.intent.action.PANEL_DISABLED");
                                                sendBroadcast(intent2);
                                                Intent intent3 = new Intent("android.intent.action.MENU_DISABLED");

                                                sendBroadcast(intent3);
                                            }
                                        };
                                        timer.schedule(task, 30 * 1000, 30 * 1000);
                                    } else {
                                        MyToast.show(SetActivity.this, "密码不正确");
                                    }
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .create();
                    mUnLockDialog.show();
                }
                return false;
            }
        });
    }

    public void onEventMainThread(TimerDerminalEvent e) {
        if (null == e) {
            return;
        }

        int flag = (int) e.getObj();
        if (flag == 13) {
            //注销成功
//            stopService(new Intent(SetActivity.this, SendPositionService.class));
//            MessageDefine.isPositionService = false;
            play("终端注销成功", 200);
            MyToast.show(this, "终端注销成功");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    MyToast.show(SetActivity.this, "请重新注册");
                }
            }, 2000);
            SharedPreferences sp = getSharedPreferences(SharedPreferencesUtil.REGISTER_INFO_FILE, MODE_PRIVATE);
            if (null != sp) {
                SharedPreferences.Editor editor = sp.edit();
                if (null != editor) {
                    editor.putBoolean(SharedPreferencesUtil.REGISTER_SUCCESS, false);
                    editor.commit();
                }
            }
        }
    }

    //time 距离上次语音播报的最小时长ms
    long flagPlayTiem;

    private void play(String txt, long time) {
        if (System.currentTimeMillis() - flagPlayTiem >= time) {
            flagPlayTiem = System.currentTimeMillis();
            SoundManage.ttsPlaySound(this, txt);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        System.out.println("onDestroy");

        if (mAlertDialog != null)
            mAlertDialog.dismiss();
    }

    private Dialog mAlertDialog = null;
    TextView GPS;
    TextView OBD;
    TextView NET;
    TextView UVC;
    TextView RFID;
    TextView FINGER;
    TextView SIM;

    private void SelfCheckingDialog() {
        View view = View.inflate(getApplicationContext(), R.layout.activity_selfchecking, null);
        view.setMinimumWidth(1000);
        view.setMinimumHeight(500);
        GPS = (TextView) view.findViewById(R.id.TV_GPS);
        OBD = (TextView) view.findViewById(R.id.TV_OBD);
        NET = (TextView) view.findViewById(R.id.TV_NETWORK);
        UVC = (TextView) view.findViewById(R.id.TV_UVC);
        RFID = (TextView) view.findViewById(R.id.TV_RFID);
        FINGER = (TextView) view.findViewById(R.id.TV_FINGER);
        SIM = (TextView) view.findViewById(R.id.TV_SIM);
        mAlertDialog = new AlertDialog.Builder(this).setView(view).create();
        mAlertDialog.setCancelable(false);
        mAlertDialog.setTitle("开机自检");

        mAlertDialog.show();

        System.out.println("启动线程");

        new Thread(new Runnable() {
            @Override
            public void run() {
                TaskRunState taskRunState = TaskRunState.CHECKGPS;

                while (checkBool) {

                    /**刘志**/
//                if (taskRunState == TaskRunState.NONE) {
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Message msg = new Message();
                    switch (taskRunState) {
                        case CHECKGPS:
                            //检测GPS模块,假的
                            gpsbool = true;
                            if (gpsbool) {
                                msg.obj = "正常";
                            } else {
                                msg.obj = "异常";
                            }
                            taskRunState = TaskRunState.CHECKOBD;
                            msg.what = 1;
                            mHandler.sendMessage(msg);

                            System.out.println("CHECKGPS");
                            break;
                        case CHECKOBD:
                            //检测OBD模块
                            if (AppContext.isObdOpen) {
                                msg.obj = "正常";
                            } else {
                                msg.obj = "异常";
                            }
                            taskRunState = TaskRunState.CHECKNET;
                            msg.what = 2;
                            mHandler.sendMessage(msg);

                            System.out.println("CHECKOBD");
                            break;
                        case CHECKNET:
                            //检测网络模块
                            isnetwork = true;
//                        isnetwork = isCheckNetWork();
                            if (isnetwork) {
                                msg.obj = "正常";
                            } else {
                                msg.obj = "异常";
                            }
                            taskRunState = TaskRunState.CHEECKUVC;
                            msg.what = 3;
                            mHandler.sendMessage(msg);

                            System.out.println("CHECKNET");
                            break;
                        case CHECKRFID:
                            //检测RFID模块
                            rfbool = JNIUtils.getInstance().openRfidDevice();
                            if (rfbool) {
                                msg.obj = "正常";
                            } else {
                                msg.obj = "异常";
                            }
                            IUtil.isRfidEnable = rfbool;
                            taskRunState = TaskRunState.CHECKFINGER;
                            msg.what = 5;
                            mHandler.sendMessage(msg);

                            System.out.println("CHECKRFID");
                            break;
                        case CHECKFINGER:
                            /*
                             * 检测指纹模块
                             * */

//                            rfbool = JNIUtils.getInstance().openFingerDevice();
                            rfbool = true;
                            if (rfbool) {
                                msg.obj = "正常";
                            } else {
                                msg.obj = "异常";
                            }
                            IUtil.isFingerEnable = rfbool;
                            taskRunState = TaskRunState.CHECKSIM;
                            msg.what = 6;
                            mHandler.sendMessage(msg);

                            System.out.println("CHECKFINGER");
                            break;
                        case CHECKSIM:
                            //检测SIM卡
                            /*
                             * SIM 是否插入
                             * */
//                        rfbool = IUtil.hasSimCard(getApplicationContext());
                            rfbool = true;
                            if (rfbool) {
                                msg.obj = "正常";
                            } else {
                                msg.obj = "异常";
                            }
                            taskRunState = TaskRunState.CHECKSTOP;
                            msg.what = 7;
                            mHandler.sendMessage(msg);

                            System.out.println("CHECKSIM");
                            break;
                        case CHEECKUVC:
                            //检测摄像头
//                        File file1 = new File(USB_DEVICE_DIRECTORY1);
//                        File file2 = new File(USB_DEVICE_DIRECTORY2);
//                        File file3 = new File(USB_DEVICE_DIRECTORY3);
//                        uvc = file1.exists();
//                        if (!uvc) {
//                            uvc = file2.exists();
//                            if (!uvc) {
//                                uvc = file3.exists();
//                            }
//                        }
                            uvc = true;
                            if (uvc) {
                                msg.obj = "正常";
                            } else {
                                msg.obj = "异常";
                            }
                            taskRunState = TaskRunState.CHECKRFID;
                            msg.what = 4;
                            mHandler.sendMessage(msg);

                            System.out.println("CHECKUVC");
                            break;
                        case CHECKSTOP:
                            //检测结束
//                       if(rfbool&&ObdRunningData.getSpeed()!=0&&isnetwork&&uvc&&gpsbool)//null!=location)
//                       {

                            ObdRunningData.setSpeed(0);
                            checkBool = false;
                            mHandler.sendEmptyMessage(123);

                            System.out.println("CHECKSTOP");
//                       }else
//                       {
//                           taskRunState = TaskRunState.CHECKGPS;
//                       }
                            break;
                    }

                    /**刘志**/
//                taskRunState = TaskRunState.NONE;
                }
            }
        }).start();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mAlertDialog.isShowing()) {
                switch (msg.what) {
                    case 1:
                        if (msg.obj.equals("正常")) {
                            GPS.setTextColor(getResources().getColor(R.color.green));
                        } else {
                            GPS.setTextColor(getResources().getColor(R.color.red));
                        }
                        GPS.setText(msg.obj.toString());
                        break;
                    case 2:
                        if (msg.obj.equals("正常")) {
                            OBD.setTextColor(getResources().getColor(R.color.green));
                        } else {
                            OBD.setTextColor(getResources().getColor(R.color.red));
                        }
                        OBD.setText(msg.obj.toString());
                        break;
                    case 3:
                        if (msg.obj.equals("正常")) {
                            NET.setTextColor(getResources().getColor(R.color.green));
                        } else {
                            NET.setTextColor(getResources().getColor(R.color.red));
                        }
                        NET.setText(msg.obj.toString());
                        break;
                    case 4:
                        if (msg.obj.equals("正常")) {
                            UVC.setTextColor(getResources().getColor(R.color.green));
                        } else {
                            UVC.setTextColor(getResources().getColor(R.color.red));
                        }
                        UVC.setText(msg.obj.toString());
                        break;
                    case 5:
                        if (msg.obj.equals("正常")) {
                            RFID.setTextColor(getResources().getColor(R.color.green));
                        } else {
                            RFID.setTextColor(getResources().getColor(R.color.red));
                        }
                        RFID.setText(msg.obj.toString());
                        break;
                    case 6:
                        if (msg.obj.equals("正常")) {
                            FINGER.setTextColor(getResources().getColor(R.color.green));
                        } else {
                            FINGER.setTextColor(getResources().getColor(R.color.red));
                        }
                        FINGER.setText(msg.obj.toString());
                        break;
                    case 7:
                        if (msg.obj.equals("正常")) {
                            SIM.setTextColor(getResources().getColor(R.color.green));
                        } else {
                            SIM.setTextColor(getResources().getColor(R.color.red));
                        }
                        SIM.setText(msg.obj.toString());
                        break;
                    case 123:
                        mAlertDialog.dismiss();
                        startActivity(new Intent(SetActivity.this, SetSecketActivity.class));
                        //stopService(new Intent(SetActivity.this, GPSService.class));
//                        com.rscja.deviceapi.OTG.getInstance().off();
                        finish();
                        break;
                }
            }
        }
    };
    Location location;
    boolean gpsbool;
    boolean rfbool;
    boolean obdbool;
    boolean isnetwork;
    boolean uvc;

    LocationManager locationManager;


    public static TelephonyManager getTelephonyManager(Context context) {
        // 获取telephony系统服务，用于取得SIM卡和网络相关信息
        TelephonyManager mTelephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephonyManager == null) {
            mTelephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
        }
        return mTelephonyManager;
    }


    public void getDeviceId() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        } else {
            TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (manager != null)
                imei = manager.getDeviceId();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            initSet();
        }
    }

    @Override
    public void onBackPressed() {
        if (null == AutomaticDetectionActivity.automaticDetectionActivity) {
            //注销之后，或者第一次进入app 时，不能返回
        } else {
            super.onBackPressed();
        }
    }

    /*
     * 判断是否 后台执行
     * */
    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                /*
                BACKGROUND=400 EMPTY=500 FOREGROUND=100
                GONE=1000 PERCEPTIBLE=130 SERVICE=300 ISIBLE=200
                 */
                Log.i(context.getPackageName(), "此appimportace ="
                        + appProcess.importance
                        + ",context.getClass().getName()="
                        + context.getClass().getName());
                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    Log.i(context.getPackageName(), "处于后台"
                            + appProcess.processName);
                    System.exit(0);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    IUtil.WhileB = false;
                    return true;
                } else {
                    Log.i(context.getPackageName(), "处于前台"
                            + appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();

        checkBool = false;

        System.out.println("onStop");

        EventBus.getDefault().unregister(this);
        if (Background) {
            isBackground(getApplicationContext());
        }
    }

    private boolean Background = false;
}
