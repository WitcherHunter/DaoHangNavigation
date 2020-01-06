package com.serenegiant.ui;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.navigation.timerterminal.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rscja.deviceapi.BDNavigation;
import com.serenegiant.AppConfig;
import com.serenegiant.business.obd.OBDAPI;
import com.serenegiant.dataFormat.PhotoPrintInfo;
import com.serenegiant.db.Insert_DB;
import com.serenegiant.db.Select_DB;
import com.serenegiant.db.Update_DB;
import com.serenegiant.entiy.CoachLoginInfo;
import com.serenegiant.entiy.GPSInfo;
import com.serenegiant.entiy.InstructorInfo;
import com.serenegiant.entiy.PhotoHeadInformation;
import com.serenegiant.entiy.StudentLoginInfo;
import com.serenegiant.entiy.TrainingRecord;
import com.serenegiant.finger.FingerprintAPI;
import com.serenegiant.net.CommonInfo;
import com.serenegiant.net.ConnectionChangeReceiver;
import com.serenegiant.net.DeviceParameter;
import com.serenegiant.net.FenceDataStruct;
import com.serenegiant.net.ObdRunningData;
import com.serenegiant.net.TcpClient;
import com.serenegiant.rfid.CardInfo;
import com.serenegiant.rfid.RFID;
import com.serenegiant.utils.CountDistance;
import com.serenegiant.utils.IUtil;
import com.serenegiant.utils.MessageDefine;
import com.serenegiant.utils.MyToast;
import com.serenegiant.utils.SharedPreferencesUtil;
import com.serenegiant.utils.SoundManage;
import com.serenegiant.utils.StringUtils;
import com.serenegiant.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import static android.widget.Toast.LENGTH_LONG;
import static java.lang.Thread.sleep;

//正在培训
public final class RainingActivity extends BaseActivity{//} implements CameraDialog.CameraDialogParent{
//    private USBMonitor mUSBMonitor;                    // 用于监视USB设备接入
//    private UVCCamera mUVCCameraL;                    // 表示左边摄像头设备
//    private OutputStream snapshotOutStreamL;        // 用于左边摄像头拍照
//    private String snapshotFileNameL;
//    private UVCCamera mUVCCameraR;                    // 表示右边摄像头设备
//    private OutputStream snapshotOutStreamR;        // 用于右边摄像头拍照
//    private String snapshotFileNameR;
//    private UVCCameraTextureView mUVCCameraViewR;    // 用于右边摄像头预览
//    private Surface mRightPreviewSurface;
    private static final float[] BANDWIDTH_FACTORS = {0.5f, 0.5f};
//    private int currentWidth = UVCCamera.DEFAULT_PREVIEW_WIDTH;
//    private int currentHeight = UVCCamera.DEFAULT_PREVIEW_HEIGHT;
//    private UVCCameraTextureView mUVCCameraViewL;    // 用于左边摄像头预览
//    private Surface mLeftPreviewSurface;

    private SurfaceView mSurfaceView;
    private Camera mCamera;
    private ConnectionChangeReceiver myReceiver;
    private static String DIR_PHOTO_NAME = "USBCamera";
    private static String DIR_VIDEO_NAME = "USBVideo";
    private final int AVAILABLE_DRIVING_NAME_SHOW = 12;
    public HorizontalScrollView mTouchView;
    private static final boolean DEBUG = true;
    ArrayList<HashMap<String, String>> listTrainData = new ArrayList<HashMap<String, String>>();

    public static int currentFuel = 0;

    long takePhotoTime = System.currentTimeMillis();
    long takePhotoTime1 = System.currentTimeMillis();
    long saveTrainListTimer = System.currentTimeMillis();

    byte[] fingerChar;
    byte[] fingerChar2;
    IntentFilter wifiIntentFilter;
    //    private static boolean isOTG = false;
    String TAG = "RainingActivity";
    boolean Debug = true;
    TextView tvLicensePlate;//车牌
    TextView tvLearnerName;//学员姓名
    TextView tvIdentification;//学员身份证
    TextView mCoaIdentifyNumTv;//教练身份证
    TextView tvInstructorName;//教练员姓名
    TextView tvMileage;//里程
    TextView tvLongitude;//经度
    TextView tvLatitude;//纬度
    TextView tvCountdown;//培训时长
    //    TextView tvsignin;//签到时长
    TextView tvDriving;//驾校
    TextView tvDeviceID;//设备id
    //ImageView imgEnd;//培训结束
    TextView imgEnd;
    //    TextView textViewListTile;
//    TextView textViewTrainMode;
    TextView textViewTrainItem;
    //    TextView tvLine1;
//    TextView tvLine11;
//    TextView tvLine2;
//    TextView tvLine22;
    TextView tvTrainStuSigned;
    TextView tvTrainHeadTime;
    //    TextView tvTrainCurPage;
    TextView tvTrainNetworkType;
    ImageView gpsSignalSem;
    TextView tvTrainLoginStatel;

    TextView tvSendCount;

    LinearLayout btn_jspx;
    LinearLayout btn_jlqt;

    LinearLayout ll_bootomShow;
    LinearLayout ll_textOperation;

    ImageView imgPhoneSem;
    RFID rfid = new RFID(RainingActivity.this);
    //    FingerprintAPI fingerprintAPI = new FingerprintAPI();
    OBDAPI obdApi = new OBDAPI();
    boolean flagRFID = false;  //RFID上电状态
    boolean flagFingerprint = false;  //指纹上电状态
    boolean flagOBD = false;  //OBD是否已经初始化
    boolean flagBDGPS = false;
    int BDGPSIndex = 0;

    private SoundPool sp;//声明一个SoundPool
    private int sp_success;//成功音提示
    private int sp_failure;//失败音提示
    moduleStatus mStatus = moduleStatus.UNKNOWN;  //当前校验哪个模块
    boolean isIniting = false;//正在初始化
    boolean isVerification = false;//是否验证失败
    Handler refreshHand;
    int waitTime = 1000;// 默认1秒钟循环一次
    boolean isEnd = false;
    int countTime = 0;//培训时间
    int totalTime = 0;//总的培训时间
    // ListeningDevice listeningDevice;
    ArrayList<TrainingRecord> list;

    int iCount = 0;//统计线程循环的次数
    int flag_Error = -1;//错误读卡次数
    long flagPlayTiem = System.currentTimeMillis();//最后一次播放声音的时间

    //按键功能选择
    int selectButtonFunc;

    ArrayList<TrainListInfo> myTrainList;

    private boolean cam_R_added = false, cam_L_added = false, cam_opened = false;
    private int recordingcam = 0, mdelay;
    //    DashboardView dashboardView3;
    long startTrainTime;
    Date startTrainDate;
    Date endTrainDate;
    long learnMile;
    boolean isCameraAllowedUse = false;
    boolean isOTG = false;
    long lastRefreshOrderTime;
    String orderNumber;
    int orderDuration;
    int orderLearned;
    String orderAreaID;
    View parent;

    boolean takePhotoPromote = true;

    //培训状态参数
    int stuSignedState;// 记录学员签到状态 1、学员已签到  0、学员签退
    ArrayList<FenceDataStruct> myFenceData;

    boolean continueCheckStudent = true;
    boolean isStudentbool = false;

    enum TaskRunState {HALT, TRAIN, STOP, COACH, FINGER;}

    TaskRunState taskRunState = TaskRunState.COACH;
    public static RainingActivity instance = null;

    TelephonyManager Tel;
    MyPhoneStateListener MyListener;
    //    private ConnectionChangeReceiver myReceiver;
    private static final int MODE_TAKE_PIC = 0;
    private static final int MODE_TAKE_VID = 1;
    BDNavigation mInstance = null;
    //    private static boolean isPosition = true;
    boolean isOBDbool = true;
    long positionTime = System.currentTimeMillis();
    long OBDTime = System.currentTimeMillis();
    private static long TrainClassId = 0;
    boolean suijipaizhao = true;
    SharedPreferences sendMessageCount;
    SharedPreferences.Editor sendMessageEditor;
    boolean lxpx = true;
    CheckStudentLogin checkStudentLogin;
    private boolean isStuExiting = false;//学员是否正在签退
    private String mStuLoginNo;//学员签到时的编号
    private String mStuExitNo;//学员签退时的编号
    private boolean isCoachExiting = false;//教练是否正在签退
    private String mCoachLoginNo;//教练签到时的编号
    private String mCoachExitNo;//教练签退时的编号
    private Dialog mExitStuByCoaDialog = null;
    private Dialog mSignDialog = null;
    private Timer mUploadPicTimer;
    private TimerTask mUploadPicTask;
    private Timer mStuExitTimer;
    private TimerTask mStuExitTask;
    private TextView mNowSpeedTv;
    private boolean switchBdorOBD = true;
    private boolean isExitingByCoa;//学员是否正在被教练员签退
    private Context mContext;
    private boolean isCoachPhoto = true;
    private boolean isHasSecondCamera = false;//是否装了两个摄像头

    private SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            startPreview();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            stopPreview();
        }
    };

    void loadFencePointData() {
        Select_DB selectable = new Select_DB();
        myFenceData = selectable.getAllFencesData();//加载电子围栏列表
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raining);
        mContext = this;
        sendMessageCount = RainingActivity.this.getSharedPreferences("SendMessageCount", MODE_PRIVATE);
        sendMessageEditor = sendMessageCount.edit();

        initCamera();
        initComponent();
        initData();
        addListener();

        //----------------camera_test-------------------
        if (DEBUG) Log.v(TAG, "onCreate:");
        if (savedInstanceState == null) {
            if (DEBUG) Log.i(TAG, "onCreate:new");
        }
        List<HighlightCR> highlight2 = new ArrayList<>();
        highlight2.add(new HighlightCR(130, 110, Color.parseColor("#5a973e")));
        highlight2.add(new HighlightCR(280, 30, Color.parseColor("#d44040")));
        startTrainDate = new Date();
        if (CommonInfo.getTrainMode() != 3) { //非长考模式
            checkStudentLogin = new CheckStudentLogin();
            Thread checkStudentThread = new Thread(checkStudentLogin, "checkStudent1");
            checkStudentThread.start();
        }
        View contentView = getLayoutInflater().inflate(R.layout.popup_window, null);
        parent = this.findViewById(R.id.layout_raining_xml);

        MyListener = new MyPhoneStateListener();

//        Tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        Tel.listen(MyListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        //----------------load camera view -------------------
        //10秒后允许操作摄像头
        registerReceiver();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshHand.sendEmptyMessage(MessageDefine.START_USE_CAMERA);
                // stopUsedCamera();
            }
        }, 10000);
        displayBriefMemory();
//        mUSBMonitor.register();

        mUploadPicTimer = new Timer();
        mStuExitTimer = new Timer();

        initView();
    }

    private void startPreview(){
        mCamera = Camera.open(4);
        try {
            mCamera.setPreviewDisplay(mSurfaceView.getHolder());
            mCamera.setDisplayOrientation(90);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopPreview(){
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    private void initView() {
        mNowSpeedTv = (TextView) findViewById(R.id.now_speed_tv);

        new Thread(new OBD_BD_switchThread()).start();
    }

//    /**
//     * BD OBD 切换执行
//     */
    class OBD_BD_switchThread implements Runnable {

        @Override
        public void run() {
            while (switchBdorOBD) {
                if (obdApi.init()) {

                    obdApi.startReceive(refreshHand);
                }
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                obdApi.stoptReceive();
                obdApi.free();

                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void initCamera() {
        mSurfaceView = findViewById(R.id.camera_view_L);
        mSurfaceView.getHolder().addCallback(mCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Tel.listen(MyListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        Log.d("cwj", "RainingActivity_onResume");
        Utils.saveRunningLog("RainingActivity onResume");
        IUtil.OnClass = true;
        new alarmSystemThread().start();
    }

    @Override
    protected void onPause() {

        Tel.listen(MyListener, PhoneStateListener.LISTEN_NONE);
        if (DEBUG) Log.d(TAG, "onPause:");
        Utils.saveRunningLog("RainingActivity onPause");
        super.onPause();
    }

    private TcpClient tcpThread;

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "RainingActivity_onStop");
        Utils.saveRunningLog("RainingActivity onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "RainingActivity_onDestroy");
        //camera_test
        Utils.saveRunningLog("RainingActivity onDestorying...");
        IUtil.OnClass = false;
        continueCheckStudent = false;
        switchBdorOBD = false;
        obdApi.stoptReceive();
        obdApi.free();
        FingerprintAPI.free();

        if (null != mUploadPicTimer) {
            mUploadPicTimer.cancel();
            mUploadPicTimer = null;
        }
        if (null != mStuExitTimer) {
            mStuExitTimer.cancel();
            mStuExitTimer = null;
        }
        if (null != mExitStuByCoaDialog) {
            mExitStuByCoaDialog = null;
        }

        Utils.saveRunningLog("RainingActivity onDestoryed");
        if (tcpThread == null) {
            tcpThread = TcpClient.getInstance(RainingActivity.this);
            tcpThread.setHandlerMain(null);
        } else {
            tcpThread.setHandlerMain(null);
        }
        refreshHand.removeCallbacksAndMessages(null);
        unregisterReceiver();
        com.rscja.deviceapi.OTG.getInstance().off();//关闭OTG
    }

    void twxtViewSwitch(boolean flag) {
        LinearLayout.LayoutParams params;
        if (flag) {
            params = new LinearLayout.LayoutParams(ll_bootomShow.getLayoutParams());
            params.weight = 1.0f;
            ll_bootomShow.setLayoutParams(params);
            //隐藏视频
            params = new LinearLayout.LayoutParams(ll_textOperation.getLayoutParams());
            params.weight = 0.0f;
            ll_textOperation.setLayoutParams(params);
        }
    }

    boolean cameraShowRecord = false;
    View.OnClickListener mylistener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tvDriving: //显示完成的驾校名称
                    if (DeviceParameter.getDrivingName().length() > AVAILABLE_DRIVING_NAME_SHOW) {
                        Toast toast = Toast.makeText(RainingActivity.this, DeviceParameter.getDrivingName(), LENGTH_LONG);
                        toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 100);
                        toast.show();
                    }
                    break;
                case R.id.btn_jspx:
                    btn_jspx.setVisibility(View.GONE);

                    btn_jlqt.setVisibility(View.VISIBLE);

                    //先刷卡，验证指纹，再执行签退
                    play("请学员刷卡签退", 100);
                    taskRunState = TaskRunState.HALT;
                    isStudentbool = false;
                    isStuExiting = true;//学员签退成功之后需置为false
                    SignNow = true;
                    preWakeupStudentSign = System.currentTimeMillis() - 4000;
                    break;
                case R.id.btn_jlqt:
                    if (isStuExiting) {
                        Toast.makeText(RainingActivity.this, "学员正在签退！请稍后", Toast.LENGTH_SHORT).show();
                    } else {
                        btn_jlqt.setVisibility(View.GONE);

                        isStudentbool = false;
                        isCoachExiting = true;//教练签退成功之后置为false
                        taskRunState = TaskRunState.COACH;
                        play("请教练员刷卡签退", 100);
                    }
                    break;
            }
        }
    };

    int selectTrainListPage = 0;
    final int maxItemsInOnePage = 8;
    int maxPagesQueryed = -1;


    //flag 0、ExaminePhoto   1、培训照片1， 2、培训照片2
    String getTrainingPhotoPath(int flag) {
        String fileDir;
        String dateString;
        String folderName;
        String fileName;
        String sRet;
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
        {
            dateString = sdf.format(new Date(System.currentTimeMillis()));
            folderName = dateString.substring(0, 6);
            fileName = dateString.substring(6);
            fileDir = AppConfig.PHOTO_SAVA_DERECTORY + File.separator + folderName;
            File dirFile = new File(fileDir);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            fileName = fileName + ".jpeg";
            sRet = fileDir + File.separator + fileName;
        }
        return sRet;
    }

    long startTakeVideoTime;

    //********************************更新UI*************************************************************
    private void updateUI() {
        refreshHand = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MessageDefine.REFRESH_SECOND_1:
                        if (GPSInfo.isValid()) {
                            gpsSignalSem.setImageResource(R.drawable.gps_signal_enable);
                        } else {
                            gpsSignalSem.setImageResource(R.drawable.gps_signal_disable);
                        }
                        tvLongitude.setText(String.format("%.6f", GPSInfo.getLongitude()));//精度
                        tvLatitude.setText(String.format("%.6f", GPSInfo.getLatitude()));  //维度
//                        Tv_GPSTime.setText("GT："+GPSInfo.getGPSTime());
                        int phoptCount = sendMessageCount.getInt("PhoptCount", 0);
                        int positionCount = sendMessageCount.getInt("PositionCount", 0);
                        int trainCount = sendMessageCount.getInt("TrainCount", 0);
                        String sendMessageCountStr = "T:" + trainCount + " P:" + phoptCount + " G:" + positionCount;
                        tvSendCount.setText(sendMessageCountStr);
//                        dashboardView3.setRealTimeValue(ObdRunningData.getSpeed(), true);//速度 保留三位精度
                        /*
                         * 距离小于1时 加 0
                         * */
                        if (GPSInfo.getSpeed() < 1) {
                            mNowSpeedTv.setText("0" + new DecimalFormat("#.000").format(GPSInfo.getSpeed()) + " km/h");
                        } else {
                            mNowSpeedTv.setText(new DecimalFormat("#.000").format(GPSInfo.getSpeed()) + " km/h");
                        }

//                        imgWifiSignal.setImageResource(R.drawable.wifi_signal_0);
                        String sTime;
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        sTime = sdf.format(new Date());
                        tvTrainHeadTime.setText(sTime);
                        if (CommonInfo.getDeviceLoginState() == 1) {
                            tvTrainLoginStatel.setText("已登录");
                        } else {
                            tvTrainLoginStatel.setText("未登录");
                        }


                        iLearnTime = System.currentTimeMillis() - startTrainTime;
                        //iLearnTime /= 1000;//seconds
                        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                        format.setTimeZone(TimeZone.getTimeZone("GMT+0"));
                        //String sTrainTime = String.format("%.2f", (float) (iLearnTime + iHistoryLearnTime) / 3600.0);
                        String sTrainTime = format.format(iLearnTime + iHistoryLearnTime);
                        tvCountdown.setText(sTrainTime);
//                        tvsignin.setText(sTrainTime);
                        //String sTrainMile = String.format("%.1f", (float) (ObdRunningData.getTotalMiles() + iHistoryLearnMiles) / 1000);
//                        String sTrainMile = String.format("%.1f", ObdRunningData.getCurMiles());
//                        tvMileage.setText(sTrainMile);
                        /*
                         * 里程转换成km 保留3位小数
                         * */

                        if (IUtil.Distance / 1000 < 1) {
                            tvMileage.setText("0" + new DecimalFormat("#.000").format(IUtil.Distance / 1000) + " km");
                        } else {
                            tvMileage.setText(new DecimalFormat("#.000").format(IUtil.Distance / 1000) + " km");
                        }


                        break;
                    case MessageDefine.MSG_REFRESH_LOC:
                        if (GPSInfo.isValid()) {
                            gpsSignalSem.setImageResource(R.drawable.gps_signal_enable);
                        } else {
                            gpsSignalSem.setImageResource(R.drawable.gps_signal_disable);
                        }
                        tvLongitude.setText(String.format("%.6f", GPSInfo.getLongitude()));//精度
                        tvLatitude.setText(String.format("%.6f", GPSInfo.getLatitude()));  //维度
                        break;
                    case MessageDefine.REFRESH_SECOND_2:
                        if (CommonInfo.getTrainMode() == 2 && CommonInfo.getCurItem() == 2 && myFenceData != null)//判定电子围栏
                        {
                            String fenceID = findValidZone((long) (GPSInfo.getLatitude() * 1000000d), (long) (GPSInfo.getLongitude() * 1000000d));
                            if (fenceID == null) {
                                Log.e(TAG, "超出培训区域");
//                                tvLine11.setText("当前场地:无效");
                                CommonInfo.setGpsWarningFlag(CommonInfo.GPS_WARNING_OVER_ZONE);
                            } else {
                                Log.e(TAG, "在培训区域内");
//                                tvLine11.setText("当前场地:" + fenceID);
                                CommonInfo.clearGpsWarningFlag(CommonInfo.GPS_WARNING_OVER_ZONE);
                            }
                        }
                        break;
                    case MessageDefine.START_USE_CAMERA: //可以使用摄像头
                        isCameraAllowedUse = true;
                        break;
                    case MessageDefine.FORCE_END_TRAINNING:
                        forceEndTrain();
                        break;
                    case MessageDefine.MSG_STU_FINGERCHAR:
                        play("未发现采集指纹", 200);
                        break;
                    case MessageDefine.MSG_STU_IDENTIFY_NO:
                        play("身份认证失败", 200);
                        break;
                    case MessageDefine.MSG_STU_IDENTIFY_OK:
                        play("身份认证成功", 100);
                        break;
                    case MessageDefine.MSG_COACH_CARD_INVALID:
                        play("信息有误", 100);
                        break;
                    case 111111:
                        play("今日不能培训", 100);
                        break;
                    case 222222:
                        play("需要补传", 100);
                        break;
                    case MessageDefine.MSG_TAKE_SIGNOUT_PHOTO:
                        //handleTakeSignoutPhoto(2);
                        break;
                    case MessageDefine.MSG_RESELECT_TRAIN_MODE:
                        if (CommonInfo.getTrainMode() == 3)//长考模式
                        {
                        } else {
                            normalEndTrain();
                        }
                        break;
                    case MessageDefine.SWITCH_COM_MODE:
                        if (msg.arg1 == 1)
                            new InitRFID_Fingerprint(moduleStatus.RFID).execute();
                        else if (msg.arg1 == 2)
                            new InitRFID_Fingerprint(moduleStatus.FINGERPRINT).execute();
                        else if (msg.arg1 == 3)
                            new InitRFID_Fingerprint(moduleStatus.OBD).execute();
                        break;

                    case MessageDefine.MSG_TAKE_PHOTO_PROMPT: {
                        if (Debug)
                            Utils.saveRunningLog("Receive message of remote take photo command immediately");
                        if (CommonInfo.getTrainMode() != 3) {  //长考中不允许远程拍照
                            takePhotoPromote = true;
                        }
                    }
                    break;
                    case MessageDefine.MSG_STUDENT_FINGER_OK:
                        play("指纹验证成功！", 100);
                        break;
                    case MessageDefine.SEND_TTS_STRING:
                        play("指纹验证失败!", 100);//
                        break;
                    case MessageDefine.MSG_TRAIN_SWIPING_CARD: //请学员刷卡
                        tvTrainStuSigned.setText("学员签到，请刷卡...");
                        if (!isStuExiting) {
                            play("请学员刷卡签到", 200);
                            SignTeacher = true;
                            fingerChar = null;
                            fingerChar2 = null;
                        } else {
                            play("请学员刷卡签退", 200);
                        }
                        break;
                    case MessageDefine.MSG_TRAIN_PUT_FINGER: //请学员验证指纹
                        if (!SignTeacher) {
                            fingerNUM = 0;
                        }
                        if (fingerNUM < 5) {
                            fingerNUM++;
                            play("请按下指纹！", 100);
                        } else {

                            play("指纹等待时间过长", 100);
                            fingerNUM = 0;
                            taskRunState = TaskRunState.HALT;
                        }
                        break;

                    case MessageDefine.MSG_TRAIN_PUT_FINGER_NULL: //请学员验证指纹
                        if (!SignTeacher) {
                            fingerNULL = 0;
                        }
                        if (fingerNULL < 5) {
                            fingerNULL++;
                            play("卡内无指纹请设置指纹验证！", 100);
                        } else {

                            play("等待时间过长请重新刷卡", 100);
                            fingerNULL = 0;
                            taskRunState = TaskRunState.HALT;
                        }


                        break;
                    case MessageDefine.MSG_COACH_SWIPING_CARD:
                        tvTrainStuSigned.setText("教练员签到，请刷卡");
                        if (!isCoachExiting) {
                            play("请教练员刷卡", 100);
                        } else {
                            play("请教练员刷卡签退", 200);
                        }
                        break;
                    case MessageDefine.MSG_COACH_LOGINPHOTO:
                        tvInstructorName.setText(CommonInfo.getCoachName());
                        if (CommonInfo.getCoachIdentifyNumber() != null) {
                            mCoaIdentifyNumTv.setBackgroundColor(Color.TRANSPARENT);
                            mCoaIdentifyNumTv.setText(new String(CommonInfo.getCoachIdentifyNumber()));
                        }
                        isCoachPhoto = true;//教练照片标识
                        takePhoto(0, 0x14, new String(CommonInfo.getCoachNumber()));
                        //takePhoto(0,0x14,CommonInfo.getCoachNumber().toString());
                        displayBriefMemory();
                        break;
                    case MessageDefine.MSG_COACH_LOGOUTPHOTO:

                        isCoachPhoto = true;//教练拍照标识
                        takePhoto(0, 0x15, new String(CommonInfo.getCoachNumber()));
                        displayBriefMemory();
//                        WriteCard(false);
                        play("教练员签退成功", 100);
                        SignTeacher = false;
                        tvInstructorName.setText("");
                        mCoaIdentifyNumTv.setBackgroundColor(Color.RED);
                        mCoaIdentifyNumTv.setText("等待教练员签到...");
                        BDGPSIndex = 0;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isStudentbool = false;
                                taskRunState = TaskRunState.COACH;
                                imgEnd.setVisibility(View.VISIBLE);
                            }
                        }, 1000);

                        break;
                    case 100000:
//                        Toast.makeText(RainingActivity.this,"开始拍照调试",Toast.LENGTH_LONG).show();
                        play("学员编号" + new String(CommonInfo.getStuNumber()), 100);
                        break;
                    case 10086:
                        play("照片上传成功", 100);
                        break;
                    case 198802:
                        Toast.makeText(RainingActivity.this, "数据发送完成", Toast.LENGTH_SHORT).show();
                        break;
                    case MessageDefine.MSG_COACH_CARD_TYPE_WRONG:
                        play("不是教练员卡", 100);//
                        tvTrainStuSigned.setText("不是教练员卡!");
                        break;
                    case MessageDefine.MSG_TRAIN_FINGER_OK://指纹验证OK


                        //定时拍照
                        if (null != mUploadPicTask) {
                            mUploadPicTask.cancel();
                        }
                        mUploadPicTask = new TimerTask() {
                            @Override
                            public void run() {
                                if (MessageDefine.isNetwork) {
                                    isCoachPhoto = false;//学员照片标识
                                    takePhoto(0, 5, new String(CommonInfo.getStuNumber()));
                                }
                            }
                        };
                        if (null == mUploadPicTimer)
                            mUploadPicTimer = new Timer();
                        mUploadPicTimer.schedule(mUploadPicTask, DeviceParameter.getTakePhotoInterval() * 60000, DeviceParameter.getTakePhotoInterval() * 60000);

                        //4小时后自动签退学员
                        if (null != mStuExitTask) {
                            mStuExitTask.cancel();
                        }
                        mStuExitTask = new TimerTask() {
                            @Override
                            public void run() {
                                //超过4小时，主动签退学员
                                //需要在主线程执行
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        btn_jspx.setVisibility(View.GONE);
                                        btn_jlqt.setVisibility(View.VISIBLE);
                                        stopTrain();
                                    }
                                });
                            }
                        };
                        if (null == mStuExitTimer)
                            mStuExitTimer = new Timer();
                        mStuExitTimer.schedule(mStuExitTask, 4 * 60 * 60 * 1000);
//                        mStuExitTimer.schedule(mStuExitTask,60*1000);
                        imgEnd.setVisibility(View.GONE);
                        btn_jlqt.setVisibility(View.GONE);
                        btn_jspx.setVisibility(View.VISIBLE);
                        //tvTrainStuSigned.setText("开始培训！");
                        IUtil.Distance = 0;
                        IUtil.mDistance = true;//开始记录距离
                        play("开始培训！", 100);
                        fingerNUM = 0;
                        isCoachPhoto = false;//学员照片标识
                        takePhoto(0, 0x11, new String(CommonInfo.getStuNumber()));
                        displayBriefMemory();
                        takePhotoTime = System.currentTimeMillis();
                        takePhotoTime1 = System.currentTimeMillis();
                        saveTrainListTimer = System.currentTimeMillis();
//                        isPosition = true;
                        startTrain();
                        flagBDGPS = false;
                        /*
                         * 开启围栏 线程
                         * */

                        break;
                    case MessageDefine.MSG_TRAIN_CARD_INVALID://卡片无效
                        tvTrainStuSigned.setText("无效卡！");
                        play("无效卡", 100);
                        break;
                    case MessageDefine.MSG_TRAIN_FINGER_INVALID://培训车型不符
                        tvTrainStuSigned.setText("培训车型不符");
                        play("培训车型不符", 100);
                        break;
                    case MessageDefine.MSG_STUDENT_FINGER_INVALID://指纹无效
                        play("指纹验证失败!", 100);//
                        break;
                    case MessageDefine.MSG_TRAIN_API_DEFAULT://模块出错
                        play("未检测到IC卡！", 500);//
                        tvTrainStuSigned.setText("重复签到无效！");
                        break;
                    case MessageDefine.MSG_LAST_STU_NOT_EXITED:
                        play("上一学员还未签退", 500);
                        break;
                    case MessageDefine.MSG_LAST_COA_NOT_EXITED:
                        play("上一教练员还未签退", 500);
                        break;
                    case MessageDefine.MSG_PHONE_SEM_CHANGED:
                        switch (Tel.getNetworkType()) {
                            case TelephonyManager.NETWORK_TYPE_GPRS:
                                //case TelephonyManager.NETWORK_TYPE_GSM:
                            case TelephonyManager.NETWORK_TYPE_EDGE:
                            case TelephonyManager.NETWORK_TYPE_CDMA:
                            case TelephonyManager.NETWORK_TYPE_1xRTT:
                            case TelephonyManager.NETWORK_TYPE_IDEN:
                                tvTrainNetworkType.setText("2G网络");
                                break;
                            case TelephonyManager.NETWORK_TYPE_UMTS:
                            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                            case TelephonyManager.NETWORK_TYPE_HSDPA:
                            case TelephonyManager.NETWORK_TYPE_HSUPA:
                            case TelephonyManager.NETWORK_TYPE_HSPA:
                            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                            case TelephonyManager.NETWORK_TYPE_EHRPD:
                            case TelephonyManager.NETWORK_TYPE_HSPAP:
                                tvTrainNetworkType.setText("3G网络");
                                break;
                            case TelephonyManager.NETWORK_TYPE_LTE:
                                tvTrainNetworkType.setText("4G网络");
                                break;
                            default:
                                if (isWifi(RainingActivity.this)) {
                                    tvTrainNetworkType.setText("WIFI");
                                } else {
                                    tvTrainNetworkType.setText("未知?");
                                }
                                break;
                        }
                        switch (currentPhoneSem) {
                            case 0:
                                imgPhoneSem.setImageResource(R.drawable.phone_sem0);
                                break;
                            case 1:
                                imgPhoneSem.setImageResource(R.drawable.phone_sem1);
                                break;
                            case 2:
                                imgPhoneSem.setImageResource(R.drawable.phone_sem2);
                                break;
                            case 3:
                                imgPhoneSem.setImageResource(R.drawable.phone_sem3);
                                break;
                            case 4:
                                imgPhoneSem.setImageResource(R.drawable.phone_sem4);
                                break;
                            case 5:
                                imgPhoneSem.setImageResource(R.drawable.phone_sem5);
                                break;
                        }
                        break;
                    case MessageDefine.MSG_NO_NET:
                        play("请检查网络", 800);
                        break;
                }
            }
        };
    }

    boolean isRecord = false;

    //********************************更新时间*************************************************************
    private class UpdateTime extends Thread {
        @Override
        public void run() {
            long startUseCamera = System.currentTimeMillis();
            long sendInterval1 = System.currentTimeMillis();
            long sendInterval2 = System.currentTimeMillis();
//            long takePhotoTime = System.currentTimeMillis();
            long recordingInterval = System.currentTimeMillis();
            long recordingDuration = System.currentTimeMillis();
//            long saveTrainListTimer = System.currentTimeMillis();
            long saveTrainInterval = System.currentTimeMillis();
            boolean isPicMode = false;

            //加载电子围栏列表
            loadFencePointData();//加载电子围栏
            long threadStartTime;
            Message msg;
            long waitMillSeconds;
            for (int i = 1; i <= 4; i++) {
                threadStartTime = System.currentTimeMillis();
                waitMillSeconds = 1000;
                while (continueCheckStudent && System.currentTimeMillis() - threadStartTime < waitMillSeconds) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    msg = new Message();
                    msg.what = MessageDefine.MSG_LOAD_CAMERA_VIEW;
                    msg.arg1 = i;
                    refreshHand.sendMessage(msg);
                }
            }
            try {
                while (continueCheckStudent) {
                    if (!isRecord
//                                || !isTakeSignedPhoto//学员签到
                            && takePhotoPromote//调试
//                                || takeSignoutPhoto//学员签退
//                                || takeCoachLoginPhot//教练签到
//                                || takeCoachOutPhot//教练签退
                            ) {
                        if (isFinishPreview) {
                            if (takePhotoPromote) {
                                takePhotoPromote = false;
                                //refreshHand.sendEmptyMessage(100000);
                                isCoachPhoto = false;
                                takePhoto(0, 0, "0");
                            }
                            displayBriefMemory();
                        } else if (isFinishPreview) {
                            if (Debug) Utils.saveRunningLog("camera is not prepared");
                        } else {
                            //if (Debug)  Utils.saveRunningLog("camera is not prepared with isFinishPreview is false");
                        }
                    }
                    refreshHand.sendEmptyMessage(MessageDefine.MSG_REFRESH_LOC);

                    if (isTrainStart) {
                        if (System.currentTimeMillis() - sendInterval1 >= 1000) {
                            sendInterval1 = System.currentTimeMillis();
                            refreshHand.sendEmptyMessage(MessageDefine.REFRESH_SECOND_1);
                        }
                        if (!isRecord && (System.currentTimeMillis() - takePhotoTime >= DeviceParameter.getTakePhotoInterval() * 60000//定时拍照
                                || takePhotoPromote//调试
                                || ((System.currentTimeMillis() - takePhotoTime1 >= (DeviceParameter.getTakePhotoInterval() + 2) * 60000) && suijipaizhao)//随机拍照
                        )) {
                            if (isFinishPreview) {
                                if (takePhotoPromote) {
                                    takePhotoPromote = false;
                                    isCoachPhoto = false;
                                    takePhoto(0, 0x00, "0");
                                } else if (System.currentTimeMillis() - takePhotoTime >= DeviceParameter.getTakePhotoInterval() * 60000) {
                                    takePhotoTime = System.currentTimeMillis();
                                    isCoachPhoto = false;//学员照片
                                    takePhoto(0, 0x13, new String(CommonInfo.getStuNumber()));//学员培训中拍照
//                                    }
                                } else {
                                    takePhotoTime1 = System.currentTimeMillis();
                                    suijipaizhao = false;
                                    isCoachPhoto = false;
                                    takePhoto(0, 0x13, new String(CommonInfo.getStuNumber()));//学员培训中拍照
                                }
                                displayBriefMemory();
                            } else if (isFinishPreview) {
                                if (Debug) Utils.saveRunningLog("camera is not prepared");
                            }
                        }

                        if (!MessageDefine.isNetwork) {
                            if (lxpx && (System.currentTimeMillis() - MessageDefine.lxpxyc >= 5000)) {
                                lxpx = false;
                                saveTrainListTimer = System.currentTimeMillis();
                                insertTrainingRecord();//
                            } else if (System.currentTimeMillis() - saveTrainListTimer >= 60000)//保存培训记录
                            {
                                saveTrainListTimer = System.currentTimeMillis();
                                insertTrainingRecord();//
                            }
                        } else {
                            if (System.currentTimeMillis() - saveTrainListTimer >= 60000)//保存培训记录
                            {
                                saveTrainListTimer = System.currentTimeMillis();
                                insertTrainingRecord();//
                            }
                        }
                    }
                    Thread.sleep(200);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    PhotoPrintInfo pf;

    //mode 0、计时模式模式  1、长考模式
    //entryType 事件类型
    //    0：中心查询的图片
    //    1：紧急报警主动上传的图片
    //    2：关车门后达到指定车速主动上传的图片
    //    3：侧翻报警主动上传的图片
    //    4：上客
    //    5：定时拍照
    //    6：进区域
    //    7：出区域
    //    8：事故疑点(紧急刹车)
    //    9：开车门
    //    17(0x11)--学员登录拍照
    //    18(0x12)--学员登出拍照
    //    19(0x13)--学员培训过程中拍照
    void takePhoto(int mode, int entryType, String number) {
        pf = null;
        SimpleDateFormat printSdf = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String photoAbsolutePath;
//        for (int i = 0; i < 2; i++) {
        if (mode == 0) {
            photoAbsolutePath = getTrainingPhotoPath(1);
        } else {
            photoAbsolutePath = getTrainingPhotoPath(0);
        }
        pf = new PhotoPrintInfo();
        String sSpeed;
        String sLocation;
        String sDate;
        File file;
        sDate = printSdf.format(new Date());
        sLocation = String.format("%3.5fE-%3.5fN", GPSInfo.getLongitude(), GPSInfo.getLatitude());
        sSpeed = String.format("车速:%dKm/h", (int) ObdRunningData.getSpeed());

        pf.setDatetime(sDate);
        pf.setIdentify(new String(CommonInfo.getStuIdentifyNumber() == null ? "0".getBytes() : CommonInfo.getStuIdentifyNumber()));
        pf.setLocation("经纬：" + sLocation);
        pf.setSpeed(sSpeed);
        pf.setCarNum(DeviceParameter.getLoginPlate() == null ? "" : DeviceParameter.getLoginPlate());
        pf.setCoachName("教练：" + (CommonInfo.getCoachName() == null ? "" : CommonInfo.getCoachName()));
        pf.setSchoolName(DeviceParameter.getDrivingName() == null ? "" : DeviceParameter.getDrivingName());
        if (entryType == 0x14 || entryType == 0x15) {
            pf.setDeviceID(CommonInfo.getCoachDrivingNumber() == null ? "" : new String(CommonInfo.getCoachDrivingNumber()));
            pf.setStudentName("");
        } else {
            pf.setDeviceID(CommonInfo.getStuDrivingNumber() == null ? "" : new String(CommonInfo.getStuDrivingNumber()));
            pf.setStudentName("学员：" + (CommonInfo.getStuName() == null ? "" : CommonInfo.getStuName()));
        }
        captureSnapshot(photoAbsolutePath);
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        savePhotoPath(photoAbsolutePath, (byte) entryType, number);
    }


    //********************************初始化模块*************************************************************  //
    public class InitRFID_Fingerprint extends AsyncTask<String, Integer, Boolean> {
        ProgressDialog mypDialog;
        String msg;
        moduleStatus type;

        public InitRFID_Fingerprint(moduleStatus type) {
            this.type = type;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            mypDialog.cancel();
            if (type == moduleStatus.RFID) {
                if (!result)
                    play("高频模块初始化失败", 100);
            } else if (type == moduleStatus.FINGERPRINT) {
                if (!result)
                    play("指纹初始化失败", 100);
                else {
                    play("请学员验证指纹", 100);
                }
            } else if (type == moduleStatus.OBD) {
                if (!result)
                    play("OBD初始化失败", 100);
            }
            isIniting = false;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            if (type == moduleStatus.RFID) {
                if (rfid.rfid_init()) {
                    flagRFID = true;
                    mStatus = moduleStatus.RFID;
                    return true;
                } else {
                    continueCheckStudent = false;//退出线程
                    flagRFID = false;
                    return false;
                }
            } else if (type == moduleStatus.FINGERPRINT) {
                if (FingerprintAPI.init()) {
                    flagFingerprint = true;
                    mStatus = moduleStatus.FINGERPRINT;
                    return true;
                } else {
                    continueCheckStudent = false;//退出线程
                    flagFingerprint = false;
                    return false;
                }
            } else if (type == moduleStatus.OBD) {
                if (obdApi.init()) {
                    flagOBD = true;
                    mStatus = moduleStatus.OBD;
                    obdApi.startReceive(refreshHand);
                    return true;
                } else {
                    continueCheckStudent = false;//退出线程
                    flagOBD = false;
                    return false;
                }
            } else {
                return false;
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            mypDialog = new ProgressDialog(RainingActivity.this);
            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            if (type == moduleStatus.RFID)
                msg = "RFID正在初始化";
            else if (type == moduleStatus.FINGERPRINT)
                msg = "指纹正在初始化";
            else if (type == moduleStatus.OBD)
                msg = "OBD正在初始化";
            mypDialog.setMessage(msg);
            mypDialog.setCanceledOnTouchOutside(false);
            // mypDialog.show();
        }
    }

    public enum moduleStatus {
        UNKNOWN, RFID, FINGERPRINT, OBD
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME: //不作响应

                break;
            case KeyEvent.KEYCODE_BACK: //签退学员，并退回到科目选择界面
//                if (event.getRepeatCount() == 0) {
//                    normalEndTrain();
//                    return true;
//                }
//                return false;
                break;
            default:
                break;
        }
        //return super.onKeyDown(keyCode, event);
        return true;
    }

    private int trainListSequence = 0;

    //培训记录数据
    private void insertTrainingRecord() {
        if (Debug) Log.i(TAG, "开始插入培训记录数据!");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            String sTime = sdf.format(new Date(System.currentTimeMillis()));
            TrainingRecord t = new TrainingRecord();
            StringBuffer sb = new StringBuffer();
            //sb.append("4167126621176494");
            sb.append(CommonInfo.getTelnetDeviceNumber());
            sb.append(sTime.substring(2, 8));
            SharedPreferences sp = RainingActivity.this.getSharedPreferences("TrainingId", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            int train_id = sp.getInt("TrainId", 1);
            //存入数据
            String ymd = sp.getString("TrainTime", "123456");
            if (ymd.equals(sTime.substring(2, 8))) {

            } else {
                editor.putString("TrainTime", sTime.substring(2, 8));
                train_id = 1;
            }
            //sb.append(String.format("%04d", ++trainListSequence));
            sb.append(String.format("%04d", train_id));
            t.setTraining_id(sb.toString());
            editor.putInt("TrainId", train_id + 1);
            editor.commit();
            String learner_number = new String(CommonInfo.getStuNumber());//StringUtils.bytesToHexString(CommonInfo.getCoachNumber());//学员编号
            t.setTraining_learner_number(learner_number);
            String instructor_number = new String(CommonInfo.getCoachNumber()); //教练员编号
            t.setTraining_instructor_number(instructor_number);
            t.setTraining_end_time(sTime.substring(8));
            //t.setTrainClassId((int)CommonInfo.getClassID());
            t.setTrainClassId((int) TrainClassId);
            t.setTrainCourseName(CommonInfo.getCourseCode());
            t.setState(0);//0、正常  1、异常
            t.setMaxSpeed((int) GPSInfo.getSpeed() * 10); // 1/10KM
            t.setMiles((int) CountDistance.getTotalMile() * 10); // 1/10KM
            t.setGpsDate(CommonInfo.getGpsPackageTrainRecord());

            byte[] gpsDate = t.getGpsDate();
            StringBuffer buf1 = new StringBuffer();
            for (int i = 28; i < gpsDate.length; i++) {
                buf1.append(gpsDate[i]);
            }
            Log.e(TAG, "insertTrainingRecord: package is " + buf1);
            t.setTraining_uploadStatus(0); //上传状态默认未来上传
            boolean result = Insert_DB.getInstance().insertTrainingRecord(t);
            int trainCount = sendMessageCount.getInt("TrainCount", 0);
            sendMessageEditor.putInt("TrainCount", trainCount + 1);
            sendMessageEditor.commit();
        } catch (Exception ex) {
            if (Debug) Log.e(TAG, "培训记录数据异常:" + ex.getMessage());
        }
//        drivingRecord(trainListNo);
    }


    //type 1、签到  2、签退
    private void insertStuLoinData(int type) //learnerQuit() {
    {
        try {
            if (Debug) Log.i(TAG, "开始插入学员退出信息!");
            StudentLoginInfo info = new StudentLoginInfo();

            String loginID;
            if (type == 1) {
                //学员登录
                info.setType(1);
                info.setLogoutDate(new Date());
                info.setMiles((int) (ObdRunningData.getCurMiles() * 10));
                info.setMinutes(0);
                info.setClassID(System.currentTimeMillis() / 1000);
                TrainClassId = info.getClassID();
            } else {
                //学员登出
                info.setLogoutDate(new Date());
                //info.setMiles((int) (CountDistance.getTotalMile() + iHistoryLearnMiles) / 100);
                info.setMiles((int) (ObdRunningData.getCurMiles() * 10));
                info.setMinutes((int) ((iLearnTime + iHistoryLearnTime) / 60000));
                info.setType(2);
                info.setClassID(TrainClassId);
            }
            info.setLoginDate(new Date());
            if (CommonInfo.getCurItem() == 2) {
                CommonInfo.setCourseCode("1212130000");
            } else if (CommonInfo.getCurItem() == 3) {
                CommonInfo.setCourseCode("1213360000");
            }
            info.setStuNumber(new String(CommonInfo.getStuNumber()));
            info.setCoachNumber(new String(CommonInfo.getCoachNumber()));
            info.setCourse(StringUtils.HexStringToBytes(CommonInfo.getCourseCode()));
            info.setGpsDate(CommonInfo.getGpsData());
            Insert_DB.getInstance().insertLearner_Login(info);
        } catch (Exception ex) {
            if (Debug) Log.e(TAG, "学员退出信息插入异常!" + ex.getMessage());
        }
        // TODO: 2018/8/2 此处无用 暂时注释掉
//        if (type == 2 && CommonInfo.getTrainMode() == 1) {
//            Update_DB datebase_update = new Update_DB();
//            int learnedTime;
//
//            iLearnTime = System.currentTimeMillis() - startTrainTime;
//            iLearnTime /= 1000;//seconds
//            learnedTime = (int) (iLearnTime + iHistoryLearnTime) / 60 + orderLearned;
//        }
    }

    //结束培训
    private void forceEndTrain() {
        insertStuLoinData(2);
        endTrainDate = new Date();
//        this.insertCheckRecord();
        CommonInfo.clearStuInfo();//清空学员信息
        play("培训结束", 100);
        Toast.makeText(RainingActivity.this, "培训结束!", Toast.LENGTH_SHORT).show();
        if (CommonInfo.getTrainMode() == 1) {//订单模式
            backToPreviousActivity();
        } else if (CommonInfo.getTrainMode() == 2)//传统模式
        {
            stopTrain();
        }
    }


    void waitForThreadsTerminate() {
        continueCheckStudent = false;//退出线程
        switchBdorOBD = false;
        while (threadUpdateTime.getState() != Thread.State.TERMINATED) {
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    void backToPreviousActivity() {
        waitForThreadsTerminate();
        Intent intent = new Intent(RainingActivity.this, AutomaticDetectionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//刷新
        intent.putExtra("disableBack", true);
        startActivity(intent);
        finish();
    }

    private void normalEndTrain() {
        DialogShow = true;
        Dialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("退出培训")
                .setMessage("是否退出培训？")
                .setCancelable(false)
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if ((isTrainStart || isStudentbool) && isCoachExiting) {
                            stopTrain();
                        }
                        if (tcpThread == null) {
                            tcpThread = TcpClient.getInstance(RainingActivity.this);
                            tcpThread.setHandlerMain(null);
                        } else {
                            tcpThread.setHandlerMain(null);

                        }
                        backToPreviousActivity();
                        DialogShow = false;
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                        DialogShow = false;
                    }
                })
                .create();
        alertDialog.show();


    }

    /**
     * 是否通过刷教练卡来签退学员
     */
    private void isExitStuByCoach() {
        if (mExitStuByCoaDialog != null) {
            mExitStuByCoaDialog.show();
        } else {
            mExitStuByCoaDialog = new AlertDialog.Builder(this)
                    .setTitle("教练员签退学员")
                    .setMessage("是否签退当前学员？")
                    //.setIcon(R.drawable.warning_sign)
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            isExitingByCoa = true;
                            stopTrain();
                        }
                    })
                    .setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            mExitStuByCoaDialog.show();
        }


    }

    /**
     * 是否强制签到
     */
    private void SignNow() {
        if (mSignDialog != null) {
            mSignDialog.show();
        } else {
            mSignDialog = new AlertDialog.Builder(this)
                    .setTitle("是否强制签到")
                    .setMessage("该卡没有正确签退，强制签到会导致上次培训数据丢失")
                    //.setIcon(R.drawable.warning_sign)
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SignNow = true;
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            mSignDialog.show();
        }


    }


    private synchronized void play(String txt, long time) {
        if (System.currentTimeMillis() - flagPlayTiem >= time) {
            flagPlayTiem = System.currentTimeMillis();
            SoundManage.ttsPlaySound(RainingActivity.this, txt);
        }
    }


    protected ImageLoader imageLoader;
    //    private GridView mGridGv;
    private DisplayImageOptions options;    // 设置图片显示相关参数
    private String[] imageUrls;        // 图片路径

    public void initComponent() {
        sp = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
        sp_success = sp.load(this, R.raw.success, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
        sp_failure = sp.load(this, R.raw.failue, 1);
        list = new ArrayList<TrainingRecord>();
        btn_jspx = (LinearLayout) findViewById(R.id.btn_jspx);
        btn_jlqt = (LinearLayout) findViewById(R.id.btn_jlqt);
        btn_jspx.setVisibility(View.GONE);
        btn_jlqt.setVisibility(View.GONE);
        //设置事件监听
        btn_jspx.setOnClickListener(mylistener);
        btn_jlqt.setOnClickListener(mylistener);

        tvSendCount = (TextView) findViewById(R.id.tvSendCount);
        textViewTrainItem = (TextView) findViewById(R.id.textViewTrainItem);
        tvTrainStuSigned = (TextView) findViewById(R.id.tvTrainStuSigned);
        imgPhoneSem = (ImageView) findViewById(R.id.imgPhoneSem);
        tvTrainHeadTime = (TextView) findViewById(R.id.tvTrainHeadTime);
        tvTrainNetworkType = (TextView) findViewById(R.id.tvTrainNetworkType);
        gpsSignalSem = (ImageView) findViewById(R.id.gpsSignalSem);
        gpsSignalSem.setImageResource(R.drawable.gps_signal_disable);
        tvTrainLoginStatel = (TextView) findViewById(R.id.tvTrainLoginStatel);
        ll_bootomShow = (LinearLayout) findViewById(R.id.layout_bottom_text);
        ll_textOperation = (LinearLayout) findViewById(R.id.layout_operation_text);
        tvLicensePlate = (TextView) findViewById(R.id.tvLicensePlate);//车牌
        tvLearnerName = (TextView) findViewById(R.id.tvLearnerName);//学员姓名
        tvIdentification = (TextView) findViewById(R.id.tvIdentification);//学员身份证
        mCoaIdentifyNumTv = (TextView) findViewById(R.id.coach_identfy_num_tv);
        tvInstructorName = (TextView) findViewById(R.id.tvInstructorName);//教练员姓名
        tvMileage = (TextView) findViewById(R.id.tvMileage);//里程
        tvLongitude = (TextView) findViewById(R.id.tvLongitude);//经度
        tvLatitude = (TextView) findViewById(R.id.tvLatitude);//纬度
        tvCountdown = (TextView) findViewById(R.id.tvCountdown);//培训时长
        tvDriving = (TextView) findViewById(R.id.tvDriving);//驾校
        tvDriving.setOnClickListener(mylistener);          //显示完整的驾校名称
        tvDeviceID = (TextView) findViewById(R.id.tvDeviceID);//设备id
        imgEnd = (TextView) findViewById(R.id.imgEnd);//培训结束
        imgEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                normalEndTrain();
            }
        });
        tvLicensePlate.setText(DeviceParameter.getLoginPlate());

        //时速表
//        dashboardView3 = (DashboardView) findViewById(R.id.dashboard_view_3);
        wifiIntentFilter = new IntentFilter();
        wifiIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);

    }

    Thread threadUpdateTime;

    public void initData() {
        updateUI();
        threadUpdateTime = new UpdateTime();
        threadUpdateTime.start();
        lastRefreshOrderTime = 0;
        String name = DeviceParameter.getDrivingName();
        if (name != null && name.length() > AVAILABLE_DRIVING_NAME_SHOW) {
            String abstractName = name.substring(0, AVAILABLE_DRIVING_NAME_SHOW - 2) + "...";
            tvDriving.setText(abstractName);
        } else {
            tvDriving.setText(name);
        }
        tvDeviceID.setText("ID：" + DeviceParameter.getDeviceNumber() + " ");

        if (GPSInfo.getUseCount() > 3) {
            CountDistance.startCount(GPSInfo.getLatitude(), GPSInfo.getLongitude(), GPSInfo.getAngle());
        }

        Bundle bundle = this.getIntent().getExtras();
        int receiveTrainMode;
        if (bundle != null) {
            receiveTrainMode = bundle.getInt("trainMode");
            switch (receiveTrainMode) {
                case 1://订单数据
                    orderNumber = bundle.getString("orderNumber");
                    orderDuration = bundle.getInt("orderDuration");
                    orderAreaID = bundle.getString("orderAreaID");
                    orderLearned = bundle.getInt("orderLearned");
                    CommonInfo.setPictureSerial(orderLearned * 60);
                    break;
                case 2://
                    break;
                case 3://
                    break;
            }
        }
        switch (CommonInfo.getTrainMode()) {
            case 1:
                selectButtonFunc = 11;
                String sTime = new String();
                if (orderDuration / 60 > 0) {
                    sTime += String.format("%d小时 ", orderDuration / 60);
                }
                if (orderDuration % 60 > 0) {
                    sTime += String.format("%d分钟 ", orderDuration % 60);
                }
                if (sTime.length() == 0) {
                    sTime += "无效";
                }
//                tvLine1.setText("预约时长:" + sTime);

                if (orderAreaID != null) {
//                    tvLine11.setText("  场地:" + orderAreaID);
                } else {
//                    tvLine11.setText("  场地:不存在");
                }
                break;
            case 2:
                selectButtonFunc = 1;
                break;
            default:
                break;
        }
        if (CommonInfo.getTrainMode() == 1 || CommonInfo.getTrainMode() == 2) {
            if (CommonInfo.getCurItem() == 2) {
                textViewTrainItem.setText("部分二");
            } else if (CommonInfo.getCurItem() == 3) {
                textViewTrainItem.setText("部分三");
            } else {
                textViewTrainItem.setText("未选择部分");
            }
//            startTrain();
        } else {
            textViewTrainItem.setText("");
        }
        /******初始化加载图片列表******/
        CommonInfo.setCourseCode("1212120000");
        CommonInfo.setClassID(System.currentTimeMillis() / 1000);
        loadFileList();
        stuSignedState = 1;
    }

    void loadFileList() {
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_empty) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.ic_empty) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.cwj_video_ico) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(false) // 设置下载的图片是否缓存在SD卡中
                // .displayer(new RoundedBitmapDisplayer(10)) // 设置成圆角图片
                .build();
//        mGridGv.setAdapter(new ItemGridAdapter());
    }


    private boolean savePhotoPath(String photoPath, byte eventType, String number) {
        if (eventType == 0 && isTrainStart) {
            number = new String(CommonInfo.getStuNumber());
        }
        PhotoHeadInformation ptInfo;
        ptInfo = new PhotoHeadInformation(photoPath);
        ptInfo.setTakeID(CommonInfo.getPictureNumber(true));
        ptInfo.setStuNumber(number);
        ptInfo.setClassID((int) TrainClassId);//CommonInfo.getClassID());
        ptInfo.setEventType(eventType);
        //校验数组
        try {
            if (ptInfo.getSavePath().length() > 0 && ptInfo.getStuNumber().length() > 0
                    && ptInfo.getGpsInfo().length > 0 && ptInfo.getTakeID().length > 0
                    && !ptInfo.getStuNumber().equals("0")) {
                int phoptCount = sendMessageCount.getInt("PhoptCount", 0);
                sendMessageEditor.putInt("PhoptCount", phoptCount + 1);
                sendMessageEditor.commit();
                return Insert_DB.getInstance().insertImgInfo(ptInfo);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }


    boolean isTrainStart;
    boolean isTakeSignedPhoto = false;
    long iLearnTime;
    long iLearnMiles;
    long iHistoryLearnTime;
    long iHistoryLearnMiles;
    long allowCoachWithoutStudentTime;
    long preWakeupStudentSign = System.currentTimeMillis();
    long wakeupInterval = 4000;

    void startTrain()//学员重新签到
    {
        currentFuel = (int) ObdRunningData.getCurUsedFuel();
        iLearnTime = 0;
        iLearnMiles = 0;
        iHistoryLearnTime = 0;
        iHistoryLearnMiles = 0;
        isStudentbool = true;
        isTrainStart = true;
//        }
        isTakeSignedPhoto = false;

        startTrainTime = System.currentTimeMillis();
        if (GPSInfo.getUseCount() > 3) {
            CountDistance.startCount(GPSInfo.getLatitude(), GPSInfo.getLongitude(), GPSInfo.getAngle());
        }
        tvIdentification.setBackgroundColor(Color.TRANSPARENT);
        tvIdentification.setText(new String(CommonInfo.getStuIdentifyNumber()));//学员身份证
        tvLearnerName.setText(CommonInfo.getStuName());
        twxtViewSwitch(true);
        startTrainDate = new Date();
        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String kssj = fm.format(date);
        if (CommonInfo.getTrainMode() == 1)//order mode
        {
            //start
            byte[] orderState = new byte[13];
            byte[] orderID = StringUtils.HexStringToBytes(orderNumber);
            System.arraycopy(orderID, 0, orderState, 0, orderID.length);
            orderState[8] = 0x01;//开始
            orderState[9] = (byte) ((orderDuration >> 8) & 0xFF);
            orderState[10] = (byte) (orderDuration & 0xFF);
            orderState[11] = (byte) ((orderLearned >> 8) & 0xFF);
            orderState[12] = (byte) (orderLearned & 0xFF);
            // TcpClient.getInstance(this).setOrderStateData(orderState);
        }
    }

    void stopTrain()//学员签退
    {
        isStudentbool = true;//暂时设置为true
        if (null != mExitStuByCoaDialog && mExitStuByCoaDialog.isShowing()) {
            mExitStuByCoaDialog.dismiss();
        }
        if (null != mUploadPicTimer) {
            mUploadPicTimer.cancel();
            mUploadPicTimer = null;
        }
        if (null != mStuExitTimer) {
            mStuExitTimer.cancel();
            mStuExitTimer = null;
        }
        twxtViewSwitch(false);
        tvIdentification.setBackgroundColor(Color.RED);
        tvIdentification.setText("等待学员签到...");//学员身份证
        tvLearnerName.setText("");
        allowCoachWithoutStudentTime = System.currentTimeMillis();
        preWakeupStudentSign = System.currentTimeMillis();
        wakeupInterval = 4000;
        isTrainStart = false;
        insertStuLoinData(2);
        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String jssj = fm.format(date);
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        isCoachPhoto = false;//学员照片标识
        takePhoto(0, 0x12, new String(CommonInfo.getStuNumber()));
        displayBriefMemory();
        isStuExiting = false;
        IUtil.mDistance = false;
        if (cardInfo != null) {

            if (cardInfo.getCardType() == RFID.CardType.StudentCard) {
                WriteCard(true);
                democardinfo = null;
            } else {
                sp.play(sp_success, 1, 1, 0, 0, 1);
                play("学员签退成功", 100);
            }
        } else {
            sp.play(sp_success, 1, 1, 0, 0, 1);
            play("学员签退成功", 100);

        }
        cardInfo = null;
        fingerChar = null;
        fingerChar2 = null;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isStudentbool = false;//2s后开始扫描
                isExitingByCoa = false;
            }
        }, 5000);
        taskRunState = TaskRunState.HALT;
    }


    /*
     * 判断点是否在围栏内
     * */
    private String findValidZone(long latitude, long longtitude) {
        for (int i = 0; i < myFenceData.size(); i++) {
            FenceDataStruct fencedata = myFenceData.get(i);
            if (fencedata.getMode() == 0 || fencedata.getMode() == CommonInfo.getCurItem()) {
                if (fencedata.getCnt() >= 3)//只少3个点
                {
                    long[] lat = fencedata.getLatitude();
                    long[] longt = fencedata.getLongtitude();
                    int pointsNumber;
                    pointsNumber = fencedata.getCnt();
                    int sum = 0;
                    long x0;
                    long y0;
                    long x1;
                    long y1;
                    long x;
                    long y;
                    y = latitude;
                    for (int index = 0; index < pointsNumber; index++) {
                        if (index == pointsNumber - 1) {
                            x0 = longt[index];
                            y0 = lat[index];
                            x1 = longt[0];
                            y1 = lat[0];
                        } else {
                            x0 = longt[index];
                            y0 = lat[index];
                            x1 = longt[index + 1];
                            y1 = lat[index + 1];
                        }
                        if (y0 != y1) {
                            if ((y0 > y && y1 < y) || (y0 < y && y1 > y)) {
                                if (longtitude < x0 && longtitude < x1) //相交
                                {
                                    sum++;
                                } else if (longtitude > x0 && longtitude > x1) //不相交
                                {
                                } else //进一步判定
                                {
                                    x = (long) ((float) (x0 - x1) / (float) (y0 - y1) * (y - y0)) + x0;
                                    if (x > longtitude)
                                        sum++;
                                }
                            }
                        }
                    }
                    if ((sum & 0x01) == 0x01) {
                        return fencedata.getId();
                    }
                }
            }
        }
        return null;
    }

    public void addListener() {
        /*mGridGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (imageUrls != null && imageUrls.length > 0) {
                    if (imageUrls[position].endsWith(".mp4")) {
                        photoShow.setVisibility(View.GONE);
                        videoReader.setVisibility(View.VISIBLE);
                        MediaController mc = new MediaController(RainingActivity.this);
                        videoReader.setVideoPath(imageUrls[position]);//指定要播放的视频
                        videoReader.setMediaController(mc);//设置VedioView与MediaController相关联
                        videoReader.requestFocus();//让VedioView获得焦点
                        try {
                            //videoReader.getBackground().setAlpha(0);//将背景图片设为透明
                            videoReader.start();//开始播放视频
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                        //为VideoView添加完成事件监听器
                        videoReader.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                //弹出消息提示框显示播放完毕
                                Toast.makeText(RainingActivity.this, "视频播放完毕", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        videoReader.setVisibility(View.GONE);
                        photoShow.setVisibility(View.VISIBLE);
                        photoShow.setImageURI(Uri.parse(imageUrls[position]));
                    }
                }
            }
        });*/
        MyListener = new MyPhoneStateListener();
        Tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
//        registerReceiver();
    }

    private WifiManager mWifiManager;

    private boolean isWifiOpen() {
        boolean isWifiOpen = mWifiManager.isWifiEnabled();
        return isWifiOpen;
    }

    /* —————————– */

    /* Start the PhoneState listener */

    /* —————————– */

    int currentPhoneSem = -1;
    boolean isConnect = false;

    private class MyPhoneStateListener extends PhoneStateListener {
        /* Get the Signal strength from the provider, each tiome there is an update  从得到的信号强度,每个tiome供应商有更新*/
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            String signalinfo = signalStrength.toString();
            String[] parts = signalinfo.split(" ");
            int ltedbm = Integer.parseInt(parts[9]);
            int asu = signalStrength.getGsmSignalStrength();
            int dbm = -113 + 2 * asu;

            switch (Tel.getNetworkType()) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    //case TelephonyManager.NETWORK_TYPE_GSM:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    if (asu < 0 || asu >= 99) {
                        currentPhoneSem = 0;
                    } else if (asu >= 18) {
                        currentPhoneSem = 5;
                    } else if (asu >= 12) {
                        currentPhoneSem = 4;
                    } else if (asu >= 8) {
                        currentPhoneSem = 3;
                    } else if (asu >= 4) {
                        currentPhoneSem = 2;
                    } else {
                        currentPhoneSem = 1;
                    }
                    break;
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    if (dbm > -75) {
                        currentPhoneSem = 4;
                    } else if (dbm > -85) {
                        currentPhoneSem = 3;
                    } else if (dbm > -95) {
                        currentPhoneSem = 2;
                    } else if (dbm > -100) {
                        currentPhoneSem = 1;
                    } else {
                        currentPhoneSem = 0;
                    }
                    break;
                case TelephonyManager.NETWORK_TYPE_LTE:
                    if (ltedbm > -65) {
                        currentPhoneSem = 5;
                    }
                    if (ltedbm > -75) {
                        currentPhoneSem = 4;
                    } else if (ltedbm > -90) {
                        currentPhoneSem = 3;
                    } else if (ltedbm > -100) {
                        currentPhoneSem = 2;
                    } else if (ltedbm > -120) {
                        currentPhoneSem = 1;
                    } else {
                        currentPhoneSem = 0;
                    }
                    break;
                default:
                    tvTrainNetworkType.setText("未知?");
                    break;
            }
            refreshHand.sendEmptyMessage(MessageDefine.MSG_PHONE_SEM_CHANGED);
        }
    }


    private void displayBriefMemory() {
        final ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(info);
        String memoryInfo = String.format("Memory-> availMem:%d totalMem:%d", info.availMem, info.totalMem);
        Utils.saveRunningLog(memoryInfo);
    }
//
//    private final USBMonitor.OnDeviceConnectListener mOnDeviceConnectListener = new USBMonitor.OnDeviceConnectListener() {
//        @Override
//        public void onAttach(final UsbDevice device) {
//            if (DEBUG) Log.i(TAG, "onAttach:" + device);
//            Utils.saveRunningLog(TAG + ":-----onAttach:" + device);
//            final List<UsbDevice> list = mUSBMonitor.getDeviceList();
//            mUSBMonitor.requestPermission(list.get(0));
//
//            if (list.size() > 1)
//
//                new Handler().postDelayed(new Runnable() {
//                    public void run() {
//                        mUSBMonitor.requestPermission(list.get(1));
//                    }
//                }, 200);
//            Utils.saveRunningLog(TAG + ":----申请摄像权限成功");
//        }
//
//        @Override
//        public void onConnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock, final boolean createNew) {
//            if (DEBUG) Log.i(TAG, "onConnect:" + ctrlBlock.getVenderId());
//            Utils.saveRunningLog("onConnect:" + ctrlBlock.getVenderId());
//            synchronized (this) {
//                if (mUVCCameraL != null && mUVCCameraR != null) { // 如果左右摄像头都打开了就不能再接入设备了
//                    return;
//                }
//                if (ctrlBlock.getVenderId() == 2) {
//                    if (mUVCCameraL != null && mUVCCameraL.getDevice().equals(device)) {
//                        return;
//                    }
//                } else if (ctrlBlock.getVenderId() == 3) {
//                    isHasSecondCamera = true;
//                    if ((mUVCCameraR != null && mUVCCameraR.getDevice().equals(device))) {
//                        return;
//                    }
//                } else {
//                    return;
//                }
//                final UVCCamera camera = new UVCCamera();
//                final int open_camera_nums = (mUVCCameraL != null ? 1 : 0);
//                if (ctrlBlock != null)
//                    camera.open(ctrlBlock);
//                else {
//                    return;
//                }
//                try {
//                    camera.setPreviewSize(currentWidth, currentHeight, UVCCamera.FRAME_FORMAT_MJPEG, 0.5f); // 0.5f是一个重要参数，表示带宽可以平均分配给两个摄像头，如果是一个摄像头则是1.0f，可以参考驱动实现
//                } catch (final IllegalArgumentException e1) {
//                    Log.e("FRAME_FORMAT", "MJPEG Failed");
//                    try {
//                        camera.setPreviewSize(currentWidth, currentHeight, UVCCamera.DEFAULT_PREVIEW_MODE, 0.5f);
//                    } catch (final IllegalArgumentException e2) {
//                        try {
//                            currentWidth = UVCCamera.DEFAULT_PREVIEW_WIDTH;
//                            currentHeight = UVCCamera.DEFAULT_PREVIEW_HEIGHT;
//                            camera.setPreviewSize(currentWidth, currentHeight, UVCCamera.DEFAULT_PREVIEW_MODE, 0.5f);
//                        } catch (final IllegalArgumentException e3) {
//                            camera.destroy();
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    //Toast.makeText(TrainViewActivity.this, "UVC设备错误", Toast.LENGTH_LONG).show();
//                                }
//                            });
//
//                            return;
//                        }
//                    }
//                }
//
//                // 将摄像头进行分配
//                if (ctrlBlock.getVenderId() == 2 && mUVCCameraL == null) {
//                    mUVCCameraL = camera;
//                    try {
//                        if (mLeftPreviewSurface != null) {
//                            mLeftPreviewSurface.release();
//                            mLeftPreviewSurface = null;
//                        }
//
//                        final SurfaceTexture st = mUVCCameraViewL.getSurfaceTexture();
//                        if (st != null) {
//                            Log.d(TAG, "*******mUVCCameraViewL.getSurfaceTexture ok");
//                            Utils.saveRunningLog("*******mUVCCameraViewL.getSurfaceTexture ok");
//                            mLeftPreviewSurface = new Surface(st);
//                            if (mLeftPreviewSurface != null)
//                                Log.d(TAG, "*******mLeftPreviewSurface create ok");
//                            Utils.saveRunningLog("*******mLeftPreviewSurface create ok");
//                        }
//                        mUVCCameraL.setPreviewDisplay(mLeftPreviewSurface);
//
//                        mUVCCameraL.setFrameCallback(mUVCFrameCallbackL, UVCCamera.PIXEL_FORMAT_YUV420SP);
//                        mUVCCameraL.startPreview();
//                    } catch (final Exception e) {
//                        Log.e(TAG, e.getMessage());
//                    }
//                } else if (ctrlBlock.getVenderId() == 3 && mUVCCameraR == null) {
//                    mUVCCameraR = camera;
//                    if (mRightPreviewSurface != null) {
//                        mRightPreviewSurface.release();
//                        mRightPreviewSurface = null;
//                    }
//
//                    final SurfaceTexture st = mUVCCameraViewR.getSurfaceTexture();
//                    if (st != null)
//                        mRightPreviewSurface = new Surface(st);
//                    mUVCCameraR.setPreviewDisplay(mRightPreviewSurface);
//
//                    mUVCCameraR.setFrameCallback(mUVCFrameCallbackR, UVCCamera.PIXEL_FORMAT_YUV420SP);
//                    mUVCCameraR.startPreview();
//                }
//            }
//        }
//
//        @Override
//        public void onDisconnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock) {
//            if (DEBUG) Log.i(TAG, "onDisconnect:" + device);
//        }
//
//        @Override
//        public void onDettach(final UsbDevice device) {
//        }
//
//        @Override
//        public void onCancel(final UsbDevice device) {
//            if (DEBUG) Log.i(TAG, "onCancel:");
//        }
//    };

    // 左边摄像头的NV21视频帧回调
//    private final IFrameCallback mUVCFrameCallbackL = new IFrameCallback() {
//        @Override
//        public void onFrame(final ByteBuffer frame) {
//
//            if (mUVCCameraL == null)
//                return;
//
//            final Size size = mUVCCameraL.getPreviewSize();
//            byte[] buffer = null;
//
//            int FrameSize = frame.remaining();
//            if (buffer == null) {
//                buffer = new byte[FrameSize];
//                frame.get(buffer);
//            }
//            if (snapshotOutStreamL != null) { // 将视频帧压缩成jpeg图片，实现快照捕获
//
//                if (!(FrameSize < size.width * size.height * 3 / 2) && (buffer != null)) {
//                    try {
//                        new YuvImage(buffer, ImageFormat.NV21, size.width, size.height, null).compressToJpeg(new Rect(0, 0, size.width, size.height), 90, snapshotOutStreamL);
//                        snapshotOutStreamL.flush();
//                        Bitmap bmp = BitmapFactory.decodeFile(snapshotFileNameL);
//                        Bitmap map = createBitmap(bmp, pf);
//                        File f = new File(snapshotFileNameL);
//                        if (f.exists()) {
//
//                            f.delete();
//                        }
//                        try {
//                            FileOutputStream out = new FileOutputStream(f);
//                            map.compress(Bitmap.CompressFormat.JPEG, 60, out);
//                            out.flush();
//                            out.close();
//                            Log.i(TAG, "已经保存");
//                        } catch (FileNotFoundException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//                    } catch (Exception ex) {
//                    } finally {
//                        int len = (int) new File(snapshotFileNameL).length();
//                        Log.e(snapshotFileNameL, "" + len);
//                        if (len > 0) {
//                            try {
//                                snapshotOutStreamL.close();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                            snapshotOutStreamL = null;
//                        }
//
//                    }
//                }
//            }
//        }
//    };

//    private final IFrameCallback mUVCFrameCallbackR = new IFrameCallback() {
//        @Override
//        public void onFrame(final ByteBuffer frame) {
//            if (mUVCCameraR == null)
//                return;
//
//            final Size size = mUVCCameraR.getPreviewSize();
//            byte[] buffer = null;
//
//            int FrameSize = frame.remaining();
//            if (buffer == null) {
//                buffer = new byte[FrameSize];
//                frame.get(buffer);
//            }
//            if (snapshotOutStreamR != null) { // 将视频帧压缩成jpeg图片，实现快照捕获
//                if (!(FrameSize < size.width * size.height * 3 / 2) && (buffer != null)) {
//                    try {
//                        new YuvImage(buffer, ImageFormat.NV21, size.width, size.height, null).compressToJpeg(new Rect(0, 0, size.width, size.height), 90, snapshotOutStreamR);
//                        snapshotOutStreamR.flush();
//                        snapshotOutStreamR.close();
//                        Bitmap bmp = BitmapFactory.decodeFile(snapshotFileNameR);
//                        Bitmap map = createBitmap(bmp, pf);
//                        File f = new File(snapshotFileNameR);
//                        if (f.exists()) {
//                            f.delete();
//                        }
//                        try {
//                            FileOutputStream out = new FileOutputStream(f);
//                            map.compress(Bitmap.CompressFormat.JPEG, 60, out);
//                            out.flush();
//                            out.close();
//                            Log.i(TAG, "已经保存");
//                        } catch (FileNotFoundException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//
//                        //开始水印照片
////                                AddTextToImage.drawTextToBitmap(bmp, pf);
////                                bmp.compress(Bitmap.CompressFormat.JPEG, 80, os);
////                                bmp.recycle();
////                        runOnUiThread(new Runnable() {
////                            @Override
////                            public void run() {
////                            }
////                        });
//                    } catch (Exception ex) {
//                    } finally {
//                        buffer = null;
//                        snapshotOutStreamR = null;
//                    }
//                }
//            }
//        }
//    };

//    @Override
//    public USBMonitor getUSBMonitor() {
//        return mUSBMonitor;
//    }
//
//    @Override
//    public void onDialogResult(boolean b) {
//
//    }

    boolean isFinishPreview = false;

    private void saveFile(byte[] bytes, String filePath) {
        File file = new File(filePath);
        try {
            if (file.exists())
                file.delete();
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void captureSnapshot(final String filePath) {

        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                saveFile(data, filePath);
            }
        });

//        if (isHasSecondCamera) {
//            //两个摄像头
//            if (isCoachPhoto) {
//                //教练照片
//                snapshotFileNameR = filePath;
//                File recordFileR = new File(snapshotFileNameR);    // 右边摄像头快照的文件名
//                if (recordFileR.exists()) {
//                    recordFileR.delete();
//                }
//                try {
//                    recordFileR.createNewFile();
//                    snapshotOutStreamR = new FileOutputStream(recordFileR);
//                } catch (Exception e) {
//                }
//            } else {
//                //学员照片
//                snapshotFileNameL = filePath;
//                File recordFileL = new File(snapshotFileNameL);    // 左边摄像头快照的文件名
//                if (recordFileL.exists()) {
//                    recordFileL.delete();
//                }
//                try {
//                    recordFileL.createNewFile();
//                    snapshotOutStreamL = new FileOutputStream(recordFileL);
//                } catch (Exception e) {
//                }
//            }
//        } else {
//            //只有摄像头1
//            snapshotFileNameL = filePath;
//            File recordFileL = new File(snapshotFileNameL);    // 左边摄像头快照的文件名
//            if (recordFileL.exists()) {
//                recordFileL.delete();
//            }
//            try {
//                recordFileL.createNewFile();
//                snapshotOutStreamL = new FileOutputStream(recordFileL);
//            } catch (Exception e) {
//            }
//        }
    }

    /**
     * 　　* 进行添加水印图片和文字
     * 　　*
     * 　　* @param src
     * 　　* @param waterMak
     * 　　* @return
     */
    public static Bitmap createBitmap(Bitmap src, PhotoPrintInfo
            pf) {
        android.graphics.Bitmap.Config bitmapConfig = src.getConfig();


        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        src = src.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(src);
        Paint paint = new Paint(Paint.FAKE_BOLD_TEXT_FLAG);
        paint.setDither(true); // 获取跟清晰的图像采样
        //paint.setFilterBitmap(true);// 过滤一些
        paint.setColor(Color.WHITE);
        paint.setTextSize((int) (21));
        paint.setStrokeWidth(3);                                    //设置描边宽度
        paint.setStyle(Paint.Style.FILL_AND_STROKE);                            //对文字只描边
        Paint paint2 = new Paint(Paint.FAKE_BOLD_TEXT_FLAG);
        paint2.setColor(Color.RED);
        paint2.setTextSize((int) (21));
        paint2.setStyle(Paint.Style.FILL_AND_STROKE);
        paint2.setStrokeWidth(1);                                    //设置描边宽度
        Rect bounds = new Rect();
        int x;
        int y;
        x = 8;
        y = 8;
        paint.getTextBounds(pf.getSchoolName(), 0, pf.getSchoolName().length(), bounds);
        canvas.drawText(pf.getSchoolName(), x, y + 16, paint);
        canvas.drawText(pf.getSchoolName(), x, y + 16, paint2);
        x = 8;
        y = 45;
        paint.getTextBounds(pf.getCarNum(), 0, pf.getCarNum().length(), bounds);
        canvas.drawText(pf.getCarNum(), x, y + 16, paint);
        canvas.drawText(pf.getCarNum(), x, y + 16, paint2);
        x = 160;
        y = 45;
        paint.getTextBounds(pf.getDeviceID(), 0, pf.getDeviceID().length(), bounds);
        canvas.drawText(pf.getDeviceID(), x, y + 16, paint);
        canvas.drawText(pf.getDeviceID(), x, y + 16, paint2);
        x = 8;
        y = 315;
        paint.getTextBounds(pf.getStudentName(), 0, pf.getStudentName().length(), bounds);
        canvas.drawText(pf.getStudentName(), x, y + 16, paint);
        canvas.drawText(pf.getStudentName(), x, y + 16, paint2);
        x = 8;
        y = 345;
        paint.getTextBounds(pf.getCoachName(), 0, pf.getCoachName().length(), bounds);
        canvas.drawText(pf.getCoachName(), x, y + 16, paint);
        canvas.drawText(pf.getCoachName(), x, y + 16, paint2);
        x = 8;
        y = 375;
        paint.getTextBounds(pf.getLocation(), 0, pf.getLocation().length(), bounds);
        canvas.drawText(pf.getLocation(), x, y + 16, paint);
        canvas.drawText(pf.getLocation(), x, y + 16, paint2);
        x = 8;
        y = 405;
        paint.getTextBounds(pf.getSpeed(), 0, pf.getSpeed().length(), bounds);
        canvas.drawText(pf.getSpeed(), x, y + 16, paint);
        canvas.drawText(pf.getSpeed(), x, y + 16, paint2);
        x = 8;
        y = 435;
        paint.getTextBounds(pf.getDatetime(), 0, pf.getDatetime().length(), bounds);
        canvas.drawText(pf.getDatetime(), x, y + 16, paint);
        canvas.drawText(pf.getDatetime(), x, y + 16, paint2);
        Utils.saveRunningLog("执行水印完毕");
        return src;
    }


    CardInfo cardInfo;

    class CheckStudentLogin implements Runnable {
        @Override
        public void run() {
            boolean isCheckStuCard;
            boolean isCheckCoaCard = false;
            int mNowFingerrRole = 0;//1：代表教练在刷指纹，2：代表学员在刷指纹

            while (continueCheckStudent) {
                int isTrain = DeviceParameter.getIsExaminationEn();

                if (!isStudentbool) {
//                    boolean bool = rfid.rfid_init();
//                    if (bool) {
                        isOBDbool = false;
                        //学员 1，教练 2

                        if (DialogShow) {
                            try {
                                sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            continue;
                        }
                        switch (taskRunState) {
                            case HALT://检测学员
                                fingerNUM = 0;
                                if (System.currentTimeMillis() - preWakeupStudentSign > wakeupInterval) {


                                    refreshHand.sendEmptyMessage(MessageDefine.MSG_TRAIN_SWIPING_CARD);
                                    wakeupInterval *= 2;
                                }
                                cardInfo = rfid.getCardInfo();
                                System.out.println(cardInfo.getName());
                                democardinfo = cardInfo;

                                if (cardInfo != null) {
                                    if (!mCarType.equals(cardInfo.getCarType())) {//准教车型判断
                                        refreshHand.sendEmptyMessage(MessageDefine.MSG_TRAIN_FINGER_INVALID);
                                        continue;
                                    }
                                    if (cardInfo.getCardState() == 1 &&
                                            cardInfo.getCardType() == RFID.CardType.StudentCard &&
                                            !SignNow) {//是否签退isStuExiting
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (!isExitingByCoa) {
                                                    SignNow();
                                                } else {
                                                    MyToast.show(mContext, "上次培训没有签退");
                                                }
                                            }
                                        });
                                        continue;
                                    } else {
                                        SignNow = false;
                                    }
//                                    if (cardInfo.getDrivingNumber().equals("")
//                                            || cardInfo.getIdentification().equals("")
//                                            || cardInfo.getName().equals("")
//                                            || cardInfo.getNumberID().equals("")
//                                            ) {
//                                        if (Debug) Utils.saveRunningLog("card invalid ");
//                                        refreshHand.sendEmptyMessage(MessageDefine.MSG_TRAIN_CARD_INVALID);
//                                        continue;
//                                    }

                                    if (cardInfo.getId().equals(new String(CommonInfo.getCoachNumber())) && cardInfo.getCardType() != RFID.CardType.StudentCard) {
                                        //当前刷的是当前学员对应的教练卡 cardInfo.getType() == 2 &&
                                        if (!isStuExiting) {
                                            refreshHand.sendEmptyMessage(MessageDefine.MSG_TRAIN_CARD_INVALID);
                                            continue;
                                        } else {
                                            if (cardInfo.getCardType() == RFID.CardType.PracticeCoachCard || cardInfo.getCardType() == RFID.CardType.CommonCoachCard) {
                                            } else {
                                                refreshHand.sendEmptyMessage(MessageDefine.MSG_TRAIN_CARD_INVALID);
                                                continue;
                                            }
                                            //学员签退时，刷的教练卡
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (!isExitingByCoa) {
                                                        isExitStuByCoach();
                                                    } else {
                                                        MyToast.show(mContext, "正在签退！");
                                                    }
                                                }
                                            });

                                        }
                                        continue;
                                    } else if (cardInfo.getCardType() == RFID.CardType.TheoryCoachCard) {
                                        //当前刷的是其他教练卡
                                        refreshHand.sendEmptyMessage(MessageDefine.MSG_TRAIN_CARD_INVALID);
                                        continue;
                                    }

                                    if (!isStuExiting) {

                                        //学员签到
                                        if (isTrain == 2) {
                                            refreshHand.sendEmptyMessage(111111);
                                            continue;
                                        }
                                        CommonInfo.setStuCardUid(rfid.getCardUid());
                                        CommonInfo.setStuDrivingNumber(cardInfo.getCompany().getBytes()); //驾校编号
                                        CommonInfo.setStuName(cardInfo.getName());                       // 学员姓名
                                        CommonInfo.setStuNumber(cardInfo.getId().getBytes()); //学员编号
//                                        CommonInfo.setStuCardNumber(cardInfo.getCardID().getBytes()); // 学员卡编号
                                        CommonInfo.setStuIdentifyNumnber(cardInfo.getIdentification().getBytes()); //身份证号
                                        CommonInfo.setValidAckSignout(false);
                                        mStuLoginNo = cardInfo.getId();
                                        isCheckStuCard = true;
                                        wakeupInterval = 4000;
                                    } else {
                                        //学员签退
                                        if (cardInfo.getCardType() == RFID.CardType.TheoryCoachCard) {
                                            //学员签退时，刷的教练卡（且不为当前教练）
                                            refreshHand.sendEmptyMessage(MessageDefine.MSG_TRAIN_CARD_INVALID);
                                            continue;
                                        } else {
                                            mStuExitNo = cardInfo.getId();
                                            isCheckStuCard = true;
                                            wakeupInterval = 4000;
                                            if (!mStuLoginNo.equals(mStuExitNo)) {
                                                refreshHand.sendEmptyMessage(MessageDefine.MSG_LAST_STU_NOT_EXITED);
                                                try {
                                                    sleep(2500);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                                continue;
                                            }
                                        }
                                    }
                                    if (isCheckStuCard) {
                                        if ((DeviceParameter.isStudentFingerEn() && !isStuExiting)

                                                || (DeviceParameter.isStudentFingerEnOut() && isStuExiting)) {

                                            if (fingerChar == null || fingerChar.equals("")) {
                                                if (isStuExiting) {

                                                    play("读取指纹，请勿取卡", 100);
                                                }
                                                fingerChar = rfid.readFingerPrint1();
                                                fingerChar2 = rfid.readFingerPrint2();
                                                if (fingerChar2 == null || fingerChar2.length == 0) {
                                                    play("卡内指纹未读取完成", 100);
                                                    wakeupInterval = 4000;
                                                    continue;
                                                }
                                            }

                                            if (fingerChar == null || fingerChar.length == 0) {
                                                play("卡内指纹未读取完成", 100);
                                                wakeupInterval = 4000;
                                                continue;
                                            } else {
                                                sp.play(sp_success, 1, 1, 0, 0, 1);
                                                taskRunState = TaskRunState.FINGER;
                                                mNowFingerrRole = 2;
                                            }
                                        } else {
                                            if (!isStuExiting) {
                                                sp.play(sp_success, 1, 1, 0, 0, 1);
                                                insertStuLoinData(1);
                                                refreshHand.sendEmptyMessageDelayed(MessageDefine.MSG_TRAIN_FINGER_OK, 1000);
                                                taskRunState = TaskRunState.STOP;
                                            } else {
                                                play("数据写入中，请勿取卡", 100);
                                                isStudentbool = true;
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        stopTrain();
                                                    }
                                                });
                                            }
                                        }
                                    }

                                }
                                if (!Write) {
                                    rfid.rfid_free();
                                }
                                break;
                            case FINGER:
                                /*
                                 * 卡内没有指纹语音提示
                                 * */

                                if (fingerChar == null || fingerChar2 == null) {
                                    refreshHand.sendEmptyMessage(MessageDefine.MSG_TRAIN_PUT_FINGER_NULL);
                                    try {
                                        sleep(5000);

                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    rfid.rfid_free();
                                    FingerprintAPI.free();
                                    continue;
                                }

                                if (FingerprintAPI.init()) {
                                    if (System.currentTimeMillis() - preWakeupStudentSign > wakeupInterval) {

                                        refreshHand.sendEmptyMessage(MessageDefine.MSG_TRAIN_PUT_FINGER);
                                        wakeupInterval *= 2;
                                    }
                                    try {
                                        sleep(500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    boolean fingerMatch;
                                    fingerMatch = FingerprintAPI.matchFingerprint(fingerChar) || FingerprintAPI.matchFingerprint(fingerChar2);
                                    if (fingerMatch) {
                                        rfid.rfid_free();
                                        FingerprintAPI.free(); //校验成功后下点退出循环
                                        //                                           isPosition=true;
                                        refreshHand.sendEmptyMessage(MessageDefine.MSG_STUDENT_FINGER_OK);
                                        if (mNowFingerrRole == 2) {
                                            if (!isStuExiting) {
                                                //学员签到指纹
                                                insertStuLoinData(1);
                                                refreshHand.sendEmptyMessageDelayed(MessageDefine.MSG_TRAIN_FINGER_OK, 1000);
                                                taskRunState = TaskRunState.STOP;
                                            } else {
                                                //学员签退指纹正确，可以签退
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        stopTrain();
                                                    }
                                                });
                                            }

                                        } else if (mNowFingerrRole == 1) {
                                            if (!isCoachExiting) {
                                                //教练签到指纹
                                                insertCoachLoginData(1);
                                                refreshHand.sendEmptyMessageDelayed(MessageDefine.MSG_TRAIN_SWIPING_CARD, 1000);
                                                taskRunState = TaskRunState.HALT;
                                                refreshHand.sendEmptyMessageDelayed(MessageDefine.MSG_COACH_LOGINPHOTO, 1000);
                                            } else {
                                                //教练员签退指纹正确，可以签退
                                                isStudentbool = true;
                                                isCoachExiting = false;
                                                insertCoachLoginData(2);
                                                refreshHand.sendEmptyMessageDelayed(MessageDefine.MSG_COACH_LOGOUTPHOTO, 1000);
                                            }
                                        }


                                    }

                                }
                                FingerprintAPI.free();
                                rfid.rfid_free();
                                break;
                            case COACH:
                                fingerNUM = 0;
                                if (System.currentTimeMillis() - preWakeupStudentSign > wakeupInterval) {
                                    refreshHand.sendEmptyMessage(MessageDefine.MSG_COACH_SWIPING_CARD);
                                    wakeupInterval *= 2;
                                }
                                cardInfo = rfid.getCardInfo();
                                if (cardInfo != null) {
                                    if (!isCoachExiting) {
                                        //教练签到
                                        if (isTrain == 2) {
                                            refreshHand.sendEmptyMessage(111111);
                                            continue;
                                        }
                                        mCarType = cardInfo.getCarType();
                                        String SysCartype = getSharedPreferences(SharedPreferencesUtil.REGISTER_INFO_FILE, MODE_PRIVATE)
                                                .getString(SharedPreferencesUtil.REGISTER_TYPE, "");
                                        if (!SysCartype.equals("")) {
                                            if (!SysCartype.equalsIgnoreCase(mCarType)) {
                                                play("教练培训车型不符", 100);
                                                continue;
                                            }
                                        }
                                        /*
                                         * 新卡卡的类型判断  只有实操教练才可以签到
                                         * */
                                        if (cardInfo.getCardType() != RFID.CardType.PracticeCoachCard && cardInfo.getCardType() != RFID.CardType.CommonCoachCard) {
                                            play("不是实操教练卡", 100);
                                            continue;
                                        }


                                        InstructorInfo instructorInfo = new InstructorInfo();
                                        instructorInfo.setInstructor_identification(cardInfo.getIdentification());//身份证
                                        instructorInfo.setInstructor_id(cardInfo.getId());//编号
                                        // TODO: 2018/6/21 这里是存放16进制还是10进制
                                        instructorInfo.setInstructor_cardID(rfid.getCardUid());//卡号
                                        instructorInfo.setInstructor_licensePlate(DeviceParameter.getLoginPlate());//车牌
                                        instructorInfo.setInstructor_phone(""); //电话
                                        instructorInfo.setInstructor_name(cardInfo.getName());//instructorInfo.setInstructor_name(StringUtils.getGB2312_Data(cardInfo.getName()));//转换中文字符
                                        CommonInfo.setCoachCardUid(rfid.getCardUid());
                                        CommonInfo.setCoachName(instructorInfo.getInstructor_name());//教练员姓名
                                        byte[] buffDriving = cardInfo.getCompany().getBytes();
                                        CommonInfo.setCoachDrivingNumber(buffDriving);//驾校编号
                                        byte[] buffCoachN = instructorInfo.getInstructor_id().getBytes();
                                        CommonInfo.setCoachNumber(buffCoachN);//教练员编号
                                        byte[] buffCard = instructorInfo.getInstructor_cardID().getBytes();
                                        CommonInfo.setCoachCardNumber(buffCard);//卡号
                                        byte[] buffId = instructorInfo.getInstructor_identification().getBytes();
                                        CommonInfo.setCoachIdentifyNumber(buffId);//身份证号;
                                        isCheckCoaCard = true;
                                        wakeupInterval = 4000;
                                        mCoachLoginNo = cardInfo.getId();
                                    } else {
                                        //教练签退
                                        isCheckCoaCard = true;
                                        wakeupInterval = 4000;
                                        mCoachExitNo = cardInfo.getId();
                                        if (!mCoachExitNo.equals(mCoachLoginNo)) {
                                            refreshHand.sendEmptyMessage(MessageDefine.MSG_LAST_COA_NOT_EXITED);
                                            try {
                                                sleep(2500);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            continue;
                                        }
                                    }
//                                      读卡
                                    if (isCheckCoaCard) {
                                        if ((DeviceParameter.isCoachFingerEn() && !isCoachExiting) ||
                                                (DeviceParameter.isCoachFingerEnOut() && isCoachExiting)) {
                                            fingerChar = rfid.readFingerPrint1();
                                            fingerChar2 = rfid.readFingerPrint2();
                                            sp.play(sp_success, 1, 1, 0, 0, 1);
                                            if (fingerChar2 == null || fingerChar2.length == 0) {

                                                play("卡内指纹未读取完成", 100);
                                                wakeupInterval = 4000;
                                                continue;
                                            }
                                            if (fingerChar == null || fingerChar.length == 0) {
                                                play("卡内指纹未读取完成", 100);
                                                wakeupInterval = 4000;
                                                continue;
                                            } else {
                                                taskRunState = TaskRunState.FINGER;
                                                mNowFingerrRole = 1;
                                            }
                                        } else {
                                            if (!isCoachExiting) {
                                                //教练员签到
                                                insertCoachLoginData(1);
                                                refreshHand.sendEmptyMessageDelayed(MessageDefine.MSG_COACH_LOGINPHOTO, 1000);
                                                refreshHand.sendEmptyMessageDelayed(MessageDefine.MSG_TRAIN_SWIPING_CARD, 1000);
                                                taskRunState = TaskRunState.HALT;
                                            } else {
                                                isStudentbool = true;
                                                isCoachExiting = false;
                                                insertCoachLoginData(2);
                                                refreshHand.sendEmptyMessageDelayed(MessageDefine.MSG_COACH_LOGOUTPHOTO, 1000);
                                            }
                                        }
                                    } else {
                                        sp.play(sp_success, 1, 1, 0, 0, 1);
                                    }
                                }
                                rfid.rfid_free();
//                                    isPosition=true;
                                break;
                            case STOP:
                                break;
                        }
//                    }
//                    else {
//                        rfid.rfid_free();
////                            isPosition=true;
//                        Log.d("TCP", "关闭高频");
//                        if (Debug) Utils.saveRunningLog("RFID API was initial failure");
//                        refreshHand.sendEmptyMessage(MessageDefine.MSG_TRAIN_API_DEFAULT);
//                        try {
//                            sleep(1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
                }
            }
            //end
//            }
        }
    }


    private static boolean isWifi(Context mContext) {
//        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
//                .getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
//        if (activeNetInfo != null
//                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
//            return true;
//        }
        return false;
    }


    private boolean insertCoachLoginData(int type) {
        CoachLoginInfo loginInfo = new CoachLoginInfo();
        loginInfo.setNumber(new String(CommonInfo.getCoachNumber()));
        loginInfo.setIdentifyNumber(new String(CommonInfo.getCoachIdentifyNumber()));
        loginInfo.setTeachCarType("C1");//TODO 需从教练卡获取
        loginInfo.setType(type);
        loginInfo.setGpsDate(CommonInfo.getGpsData());
        Insert_DB.getInstance().insertInstructorLogin(loginInfo);
        return true;
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        myReceiver = new ConnectionChangeReceiver();
        this.registerReceiver(myReceiver, filter);
    }

    private void unregisterReceiver() {
        this.unregisterReceiver(myReceiver);
    }

    private boolean DialogShow = false;
    public static String mCarType;

    private void WriteCard(boolean Stu) { //学员签退写卡  判断是否是学员卡
        Write = true;
        String stime;
        int time = 0;
        if (Stu) {

            if (democardinfo.getCardState() == 0) {
                play("学员已在其他设备签退", 100);
                return;
            }
            /*
             * 培训时长
             * */
            stime = tvCountdown.getText().toString().trim();
            String[] times = stime.split(":");
            time = Integer.parseInt(times[0]) * 60 + Integer.parseInt(times[1]) + (Integer.parseInt(times[2]) > 20 ? 1 : 0);
        }

        if (!rfid.rfid_init()) {
            return;
        }
        int index = 0;

        data = new byte[48];
        data[index] = (byte) (0 & 0xFF);//状态 0
        if (Stu) {
            index = intToBytes2(democardinfo.getPracticeTime(), data, 1);
            index = shortToBytes2((short) (democardinfo.getCheckInTimes() + 1), data, index);//签到次数加 1
            index = intToBytes2(democardinfo.getSubjectOneLearnedTime(), data, index);//
            if (CommonInfo.curItem == 2) {
                index = intToBytes2(democardinfo.getSubjectTwoLearnedTime() + time, data, index);   //三舍四入 为学员多加1米
                index = intToBytes2(democardinfo.getSubjectTwoLearnedMiles() + IUtil.Distance > (int) (IUtil.Distance) + 0.3 ? (int) (IUtil.Distance) + 1 : (int) (IUtil.Distance), data, index);
                index = intToBytes2(democardinfo.getSubjectThreeLearnedTime(), data, index);
                index = intToBytes2(democardinfo.getSubjectThreeLearnedMiles(), data, index);
            } else {
                index = intToBytes2(democardinfo.getSubjectTwoLearnedTime(), data, index);
                index = intToBytes2(democardinfo.getSubjectTwoLearnedMiles(), data, index);
                index = intToBytes2(democardinfo.getSubjectThreeLearnedTime() + time, data, index);
                index = intToBytes2(democardinfo.getSubjectThreeLearnedMiles() + IUtil.Distance > (int) (IUtil.Distance) + 0.3 ? (int) (IUtil.Distance) + 1 : (int) (IUtil.Distance), data, index);
            }
            index = intToBytes2(democardinfo.getSubjectFourLearnedTime(), data, index);
            System.arraycopy(democardinfo.getVirtualCurrency(), 0, data, index, 4);
        }
        rfid.writeCard(1232, data);
        rfid.rfid_free();
        democardinfo = null;
        Write = false;
        fingerNUM = 0;
        if (Stu) {
            play("学员签退成功，请您取卡", 100);
            try {
                sleep(5 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            sp.play(sp_success, 1, 1, 0, 0, 1);
        }

    }

    /**
     * 将int数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。  和bytesToInt2（）配套使用
     */
    public int intToBytes2(int value, byte[] src, int index) {
        data[index++] = (byte) ((value >> 24) & 0xFF);
        data[index++] = (byte) ((value >> 16) & 0xFF);
        data[index++] = (byte) ((value >> 8) & 0xFF);
        data[index++] = (byte) (value & 0xFF);
        return index;
    }

    /**
     * 将short数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。  和bytesToInt2（）配套使用
     */
    public int shortToBytes2(short value, byte[] src, int index) {
        data[index++] = (byte) ((value >> 8) & 0xFF);
        data[index++] = (byte) (value & 0xFF);
        return index;
    }

    /*
     * 报警线程
     * */
    private class alarmSystemThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (IUtil.OnClass) {
                try {
                    sleep(8 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (!IUtil.OnClass) {
                    break;
                }
                if (IUtil.EnclosureOut) {//围栏报警
//                    play("超出教学区域", 200);
                }
                try {
                    sleep(5 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (IUtil.SpeedOut) {//超速报警
                    play("您已超速", 200);
                }
            }
        }
    }

    private CardInfo democardinfo;
    private byte[] data;
    private boolean Write = false; //是否在写卡
    private boolean SignNow = false; //是否签退
    private int fingerNUM = 0; // 验证指纹超时记录
    private int fingerNULL = 0; // 卡内指纹验证记录
    private boolean SignTeacher = false; // 教练是否签到

}
