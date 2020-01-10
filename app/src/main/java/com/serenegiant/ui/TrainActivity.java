package com.serenegiant.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.navigation.timerterminal.R;
import com.serenegiant.AppConfig;
import com.serenegiant.AppContext;
import com.serenegiant.dataFormat.PhotoPrintInfo;
import com.serenegiant.db.Insert_DB;
import com.serenegiant.entiy.CoachInfo;
import com.serenegiant.entiy.CoachLoginInfo;
import com.serenegiant.entiy.FaceVerifyRequest;
import com.serenegiant.entiy.FaceVerifyResponse;
import com.serenegiant.entiy.GPSInfo;
import com.serenegiant.entiy.MinuteRecord;
import com.serenegiant.entiy.MinuteRecordDao;
import com.serenegiant.entiy.ObdDataModel;
import com.serenegiant.entiy.PhotoHeadInformation;
import com.serenegiant.entiy.StudentAndCoachInfoRequest;
import com.serenegiant.entiy.StudentInfo;
import com.serenegiant.entiy.StudentLoginInfo;
import com.serenegiant.entiy.TrainingRecord;
import com.serenegiant.http.HttpConfig;
import com.serenegiant.net.CommonInfo;
import com.serenegiant.net.DeviceParameter;
import com.serenegiant.rfid.CardInfo;
import com.serenegiant.rfid.OnCardCheckedListener;
import com.serenegiant.rfid.RFID;
import com.serenegiant.rfid.RfidThread;
import com.serenegiant.utils.AddTextToImage;
import com.serenegiant.utils.CountDistance;
import com.serenegiant.utils.IUtil;
import com.serenegiant.utils.MilesCalculateUtil;
import com.serenegiant.utils.SharedPreferencesUtil;
import com.serenegiant.utils.SoundManage;
import com.serenegiant.utils.StringUtils;
import com.serenegiant.utils.Utils;
import com.serenegiant.view.CameraPreview;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static com.serenegiant.entiy.PracticeEvent.COACH_EXIT_SUCCESS;
import static com.serenegiant.entiy.PracticeEvent.COACH_LOGIN_SUCCESS;
import static com.serenegiant.entiy.PracticeEvent.ENTER_FINGER;
import static com.serenegiant.entiy.PracticeEvent.FINGER_MATCH_FAIL;
import static com.serenegiant.entiy.PracticeEvent.START_TRAIN;
import static com.serenegiant.entiy.PracticeEvent.STUDENT_EXIT_SUCCESS;
import static com.serenegiant.entiy.PracticeEvent.TIMEOUT;
import static com.serenegiant.ui.SetDaoHangActivity.MODE_NONE;

public class TrainActivity extends BaseActivity implements OnCardCheckedListener {
    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;

    public static final int LOGIN = 1;
    public static final int LOGOUT = 2;
    private int verifyMode = MODE_NONE;

    public static final int COACH_LOGIN_FACE = 1;
    public static final int STUDENT_LOGIN_FACE = 2;
    public static final int STUDENT_LOGOUT_FACE = 3;
    public static final int COACH_LOGOUT_FACE = 4;
    public static final int COACH_EXIT_STUDENT_FACE = 5;

    public static final int FACE_COACH = 1;
    public static final int FACE_STUDENT = 2;

    enum TrainState {
        // 初始化中
        PREPARING,
        // 等待教练刷卡签到
        COACH_LOGIN,
        // 等待学员刷卡签到
        STUDENT_LOGIN,
        // 训练中
        TRAINING,
        // 等待学员刷卡签退,签退成功后进入STUDENT_LOGIN状态
        STUDENT_EXIT {
            @Override
            public String toString() {
                return "学员签退";
            }
        },
        // 等待教练刷卡签退,签退成功后进入READY状态
        COACH_EXIT {
            @Override
            public String toString() {
                return "教练签退";
            }
        }
    }

    private int maxSpeed;

    private SoundPool mSoundPool;
    private int soundSuccess, soundFail;

    private boolean isExit = false;

    private boolean rfidReady = false;
    private boolean fingerPrintReady = false;

    //初始状态为初始化中
    private TrainState mState = TrainState.PREPARING;

    private AppCompatButton mBtnStudentExit, mBtnCoachExit;

    //摄像头surfaceView
    private FrameLayout mSurfaceView;
    private Camera mCamera;

    private Chronometer mTimer;

    private RFID mRfid;

    private CardInfo mCoachCardInfo;
    private CardInfo mStudentCardInfo;

    private AppCompatTextView mTvTrainPart;

    private TextView mTvCoachName, mTvCoachNumber;

    private TextView mTvStudentName, mTvStudentNumber;

    private AppCompatTextView mTvCarNum;

    private AppCompatTextView mTvLatitude, mTvLongitude, mTvSpeed, mTvTrainMiles;

    private AppCompatTextView mTvTotalTime;

    private TextView mTvEnd, mTvDeviceId, mTvSchoolName;

    private long classId;
    private long startTime;

    private byte[] data;

    private PhotoPrintInfo pf;

    SharedPreferences sendMessageCount;
    SharedPreferences.Editor sendMessageEditor;

    private Timer timer;
    private Timer trainRecordTimer;
    //分钟学时距离
    private int distancePerMinute;
    //每分钟最大速度
    private int maxSpeedPerMinute;
    //单次培训总里程
    private int totalMiles;

    long lastPlayTime = System.currentTimeMillis();

    private OkHttpClient mClient;

    //本次培训时长
    private int practiceTime = 0;

    //当日训练时长
    private int todayPracticeTime = 0;


    private int currentLearnedTime = 0;
    private TimerTask mRefreshTask;

    private TimerTask mLocationTask;

    private TimerTask fourHourAlarmTask;

    private TimerTask mTimeOutTask;

    private int alertCount = 0;

    private boolean isStudentExiting = false;

    private MinuteRecordDao minuteRecordDao;

    private MinuteRecord record;

    private boolean shutdownTimeout;

    //本次学习时间（异常关机后记录，重启后恢复）
    private int learnedTimeBeforeShutdown;

    private AlertDialog mTimeOutDialog;

    //培训开始时obd数据中的累积里程
    private int startMilesUsedByMinuteRecord;

    private int startMiles;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case COACH_LOGIN_SUCCESS:
                    if (mCoachCardInfo.getName() != null)
                        mTvCoachName.setText(mCoachCardInfo.getName());
                    if (mCoachCardInfo.getId() != null)
                        mTvCoachNumber.setText(mCoachCardInfo.getId());
                    break;
                case START_TRAIN:
                    mTvEnd.setVisibility(View.INVISIBLE);
                    if (record != null)
                        mTimer.setBase(SystemClock.elapsedRealtime() - learnedTimeBeforeShutdown * 60 * 1000);
                    else
                        mTimer.setBase(SystemClock.elapsedRealtime());
                    mTimer.start();

                    mBtnStudentExit.setVisibility(View.VISIBLE);
                    mBtnCoachExit.setVisibility(View.GONE);

                    if (mStudentCardInfo.getName() != null)
                        mTvStudentName.setText(mStudentCardInfo.getName());
                    if (mStudentCardInfo.getId() != null)
                        mTvStudentNumber.setText(mStudentCardInfo.getId());

                    if (DeviceParameter.getTakePhotoInterval() != 0)
                        scheduleTakePicture();

                    break;
                case STUDENT_EXIT_SUCCESS:
                    mTvEnd.setVisibility(View.VISIBLE);
                    isStudentExiting = false;

                    mTvStudentName.setText("");
                    mTvStudentNumber.setText("");

                    stopScheduleTakePicture();
                    stopMinuteRecord();
                    stopOutOfTimeAlert();

                    learnedTimeBeforeShutdown = 0;
                    clearRecord();
                    record = null;

                    play("请学员刷卡签到", 400);
                    break;
                case COACH_EXIT_SUCCESS:
                    stopMinuteRecord();
                    play("请教练员刷卡", 200);
                    mTvCoachNumber.setText("");
                    mTvCoachName.setText("");
                    break;
                case ENTER_FINGER:
                    play("请刷指纹", 200);
                    break;
                case FINGER_MATCH_FAIL:
                    play("指纹比对失败", 200);
                    break;
                case TIMEOUT:
                    showTimeOutDialog();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);

        minuteRecordDao = AppContext.getDaoSession().getMinuteRecordDao();
        Intent intent = getIntent();
        if (intent.getBooleanExtra("continueLearn", false)) {
            record = minuteRecordDao.loadAll().get(0);
        } else
            record = null;

        AppContext.Stopped = false;

        sendMessageCount = TrainActivity.this.getSharedPreferences("SendMessageCount", MODE_PRIVATE);
        sendMessageEditor = sendMessageCount.edit();

        if (CommonInfo.getCurItem() == 2) {
            CommonInfo.setCourseCode("1212130000");
        } else if (CommonInfo.getCurItem() == 3) {
            CommonInfo.setCourseCode("1213360000");
        }

        // TODO: 2019-07-22 模拟学时
//        if (CommonInfo.getCurItem() == 2) {
//            CommonInfo.setCourseCode("3212130000");
//        } else if (CommonInfo.getCurItem() == 3) {
//            CommonInfo.setCourseCode("3213360000");
//        }

        mClient = new OkHttpClient();
        mClient.setReadTimeout(10000, TimeUnit.MILLISECONDS);
        mClient.setConnectTimeout(10000, TimeUnit.MILLISECONDS);

//        verifyMode = getSharedPreferences("VerifyMode", MODE_PRIVATE).getInt(VERIFY_MODE, MODE_NONE);
//        verifyMode = MODE_NONE;

        initPermissions();

//        registerReceiver();
    }

    private void init() {
        maxSpeed = SharedPreferencesUtil.getInstance(getApplication()).getsharepreferencesInt(IUtil.MAX_SPEED);

        mSoundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        soundSuccess = mSoundPool.load(this, R.raw.success, 1);
        soundFail = mSoundPool.load(this, R.raw.failue, 1);

        timer = new Timer();

        initView();

        initCamera();

        mRfid = new RFID(this);

        new Thread(mAlarmRunnable).start();

        mLocationTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshLocation();
                    }
                });
            }
        };

        new Timer().schedule(mLocationTask, 0, 2000);

        if (record != null) {
            if (record.getStuName() != null) {
                mTvStudentName.setText(record.getStuName());
                CommonInfo.setStuName(record.getStuName());
            }
            if (record.getCoachName() != null) {
                mTvCoachName.setText(record.getCoachName());
                CommonInfo.setCoachName(record.getCoachName());
            }
            if (record.getStudentNumber() != null) {
                mTvStudentNumber.setText(new String(record.getStudentNumber()));
                CommonInfo.setStuNumber(record.getStudentNumber());
            }
            if (record.getCoachNumber() != null) {
                mTvCoachNumber.setText(new String(record.getCoachNumber()));
                CommonInfo.setCoachNumber(record.getCoachNumber());
            }

            mStudentCardInfo = record.generateStudentCardInfo();
            mCoachCardInfo = record.generateCoachCardInfo();

            learnedTimeBeforeShutdown = record.getLearnedTime();
            CommonInfo.setStuIdentifyNumnber(record.getStudentIdentifyNumber());
            CommonInfo.setCoachIdentifyNumber(record.getCoachIdentifyNumber());
            classId = record.getClassId();

            int timeout = getSharedPreferences(SharedPreferencesUtil.REGISTER_INFO_FILE, MODE_PRIVATE)
                    .getInt(SharedPreferencesUtil.TIME_OUT, 10);

            if (System.currentTimeMillis() - record.getLastRecordTime() > timeout * 60 * 1000) {
                new AlertDialog.Builder(this)
                        .setMessage("您关机超过" + timeout + "分钟，系统将以上次关机时间作为签退时间.")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (null != mTimeOutTask)
                                    mTimeOutTask.cancel();

                                shutdownTimeout = true;
                                stopTrain();
                                mHandler.sendEmptyMessage(STUDENT_EXIT_SUCCESS);

                                mState = TrainState.STUDENT_LOGIN;
                                startCheckCard();
                            }
                        })
                        .setOnKeyListener(new DialogInterface.OnKeyListener() {
                            @Override
                            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                return keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0;
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            } else {
                mTvEnd.setVisibility(View.INVISIBLE);
                new AlertDialog.Builder(this)
                        .setMessage("是否继续上次培训？")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                play("开始培训", 200);
                                mState = TrainState.TRAINING;

                                mHandler.sendEmptyMessage(START_TRAIN);

                                startTrain();
                            }
                        })
                        .setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                isStudentExiting = true;
                                studentReadyToExit();
                                mBtnStudentExit.setVisibility(View.GONE);
                                mBtnCoachExit.setVisibility(View.VISIBLE);
                            }
                        })
                        .setCancelable(false)
                        .create().show();
            }
        } else {
            if (IUtil.isRfidEnable && IUtil.isFingerEnable) {
                mState = TrainState.COACH_LOGIN;
                play("请教练员刷卡", 200);
                startCheckCard();
            }
        }
    }

    private void initPermissions() {
        String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE};

        for (int i = 0; i < permissions.length; i++) {
            if (!Utils.checkPermission(this, permissions[i])) {
                requestPermissions(permissions, 1);
                return;
            }
        }

        init();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mBtnStudentExit = findViewById(R.id.btnStudentLogout);
        mBtnStudentExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStudentExiting = true;
                studentReadyToExit();
                mBtnStudentExit.setVisibility(View.GONE);
                mBtnCoachExit.setVisibility(View.VISIBLE);
            }
        });

        mBtnCoachExit = findViewById(R.id.btnCoachLogout);
        mBtnCoachExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isStudentExiting) {
                    coachReadyToExit();
                    mBtnCoachExit.setVisibility(View.GONE);
                    mBtnStudentExit.setVisibility(View.GONE);
                }
            }
        });

        mTvEnd = findViewById(R.id.imgEnd);
        mTvEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(TrainActivity.this)
                        .setTitle("确认退出培训？")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            }
        });

        mTvCoachName = findViewById(R.id.tvCoach);
        mTvCoachNumber = findViewById(R.id.tvCoachNumber);

        mTvStudentName = findViewById(R.id.tvStudent);
        mTvStudentNumber = findViewById(R.id.tvStudentNumber);

        mTimer = findViewById(R.id.timer);

        mTvLatitude = findViewById(R.id.tvLatitude);
        mTvLongitude = findViewById(R.id.tvLongitude);
        mTvSpeed = findViewById(R.id.tvSpeed);
        mTvTrainMiles = findViewById(R.id.tvTrainMiles);

        mTvTotalTime = findViewById(R.id.tvTotalTime);

        mTvCarNum = findViewById(R.id.tvCarNumber);
        if (DeviceParameter.getLoginPlate() != null)
            mTvCarNum.setText("车牌号码: " + DeviceParameter.getLoginPlate());

        mTvTrainPart = findViewById(R.id.tvTrainPart);
        if (CommonInfo.getCurItem() == 2) {
            mTvTrainPart.setText("训练部分：科目二");
        } else if (CommonInfo.getCurItem() == 3) {
            mTvTrainPart.setText("训练部分：科目三");
        }

        mTvDeviceId = findViewById(R.id.tvDeviceID);
        mTvSchoolName = findViewById(R.id.tvDriving);

        mTvDeviceId.setText("ID：" + DeviceParameter.getDeviceNumber());
        mTvSchoolName.setText(DeviceParameter.getDrivingName());

        mTimeOutDialog = new AlertDialog.Builder(this)
                .setMessage("是否继续学习？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (null != mTimeOutTask)
                            mTimeOutTask.cancel();
                        mTimeOutTask = new TimerTask() {
                            @Override
                            public void run() {
                                alertCount++;
                                play("您今天额外学习" + (alertCount * 15) + "分钟", 200);
                            }
                        };

                        new Timer().schedule(mTimeOutTask, 15 * 60 * 1000, 15 * 60 * 1000);

                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (null != mTimeOutTask)
                                    mTimeOutTask.cancel();
                                isStudentExiting = true;
                                studentReadyToExit();
                                mBtnStudentExit.setVisibility(View.GONE);
                                mBtnCoachExit.setVisibility(View.VISIBLE);
                            }
                        });
                        dialog.dismiss();
                    }
                })
                .create();
    }

    /**
     * 初始化相机
     */
    private void initCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
            return;
        }

        mSurfaceView = findViewById(R.id.surfaceView);
        mCamera = Camera.open();
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPictureSize(640, 480);
        }
        mSurfaceView.removeAllViews();
        mSurfaceView.addView(new CameraPreview(this, mCamera));
    }

    @Override
    public void onReadSuccess(long uid) {
        if (mState == TrainState.STUDENT_LOGIN) {
            getStudentInfo(uid);
        } else if (mState == TrainState.COACH_LOGIN) {
            getCoachInfo(uid);
        } else if (mState == TrainState.COACH_EXIT) {
            CardInfo info = new CardInfo();
            info.setUid(uid);
            handleCard(info, RFID.CardType.CommonCoachCard);
        } else if (mState == TrainState.STUDENT_EXIT) {
            CardInfo info = new CardInfo();
            info.setUid(uid);
            handleCard(info, RFID.CardType.StudentCard);
        }
    }

    /**
     * 获取学员信息
     *
     * @param uid 卡内码
     */
    private void getStudentInfo(final long uid) {
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType, new Gson().toJson(new StudentAndCoachInfoRequest(uid)));
        Request request = new Request.Builder()
                .url(HttpConfig.studentLogin)
                .post(body)
                .build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                play("学员信息获取失败，请重试",200);
                reCheckCard();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    if (response != null && response.body() != null) {
                        StudentInfo info;
                        try {
                            info = new Gson().fromJson(response.body().string(), StudentInfo.class);
                        } catch (Exception e) {
                            info = new StudentInfo(false, null);
                        }
                        if (info.isSuccess() && info.getResult() != null && !info.getResult().isEmpty()) {
                            System.out.println("学员信息获取成功");
                            handleCard(CardInfo.studentInfoToCardInfo(info.getResult().get(0), uid), RFID.CardType.StudentCard);
                        } else {
                            System.out.println("学员信息获取失败");
                            play("学员信息获取失败，请重试",200);
                            reCheckCard();
                        }
                    } else {
                        play("学员信息获取失败，请重试",200);
                        reCheckCard();
                    }
                } catch (Exception e){
                    play("学员信息获取失败，请重试",200);
                    reCheckCard();
                }
            }
        });
    }

    /**
     * 获取教练信息
     *
     * @param uid 卡内码
     */
    private void getCoachInfo(final long uid) {
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType, new Gson().toJson(new StudentAndCoachInfoRequest(uid)));
        Request request = new Request.Builder()
                .url(HttpConfig.coachLogin)
                .post(body)
                .build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                play("教练信息获取失败，请重试",200);
                reCheckCard();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    if (response != null && response.body() != null) {
                        CoachInfo info;
                        try {
                            info = new Gson().fromJson(response.body().string(), CoachInfo.class);
                        } catch (Exception e) {
                            info = new CoachInfo(false, null);
                        }
                        if (info.isSuccess() && info.getResult() != null && !info.getResult().isEmpty()) {
                            System.out.println("教练员信息获取成功");
                            handleCard(CardInfo.coachInfoToCardInfo(info.getResult().get(0), uid), RFID.CardType.CommonCoachCard);
                        } else {
                            play("教练信息获取失败，请重试", 200);
                            reCheckCard();
                        }
                    } else {
                        play("教练信息获取失败，请重试", 200);
                        reCheckCard();
                    }
                } catch (Exception e){
                    play("教练信息获取失败，请重试",200);
                    reCheckCard();
                }
            }
        });
    }

    /**
     * 学员准备签退
     */
    private void studentReadyToExit() {
        mState = TrainState.STUDENT_EXIT;
        play("请学员刷卡签退", 200);
        startCheckCard();
    }

    /**
     * 教练准备签退
     */
    private void coachReadyToExit() {
        mState = TrainState.COACH_EXIT;
        play("请教练员刷卡签退", 200);
    }

    /**
     * 开启卡片读取线程
     */
    private void startCheckCard() {
        if (!isExit) {
            new RfidThread(mRfid, this).start();
        }
    }

    private void reCheckCard() {
        try {
            Thread.sleep(3000);
            startCheckCard();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 人脸识别
     */
    private void checkFace(final int actionType, final String photoPath) {
//        retryCount++;
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                compressBitmap(BitmapFactory.decodeByteArray(data, 0, data.length), getTrainingPhotoPath(),
                        (byte) 0x14, mCoachCardInfo.getId(), true);
                FaceVerifyRequest model = new FaceVerifyRequest(HttpConfig.PHOTO_PREFIX + photoPath, Base64.encodeToString(data, Base64.DEFAULT));
                MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(mediaType, new Gson().toJson(model));

                Request request = new Request.Builder()
                        .url(HttpConfig.checkFace)
                        .post(body)
                        .build();

                Log.e("TrainActivity", "接口开始调用：" + new SimpleDateFormat("yyMMdd HH:mm:ss").format(new Date()));
                mClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        Log.e("TrainActivity", "网络链接失败：" + new SimpleDateFormat("yyMMdd HH:mm:ss").format(new Date()));
                        play("人脸识别失败，请确认当前网络状况是否良好", 200);
                        reCheckCard();
                    }

                    @Override
                    public void onResponse(Response response) {
                        try {
                            String result = response.body().string();
                            System.out.println("人脸识别结果： " + result);
                            FaceVerifyResponse data = new Gson().fromJson(result, FaceVerifyResponse.class);
                            if (data.getSuccess()) {
                                Log.e("TrainActivity", "接口调用成功：" + new SimpleDateFormat("yyMMdd HH:mm:ss").format(new Date()));
                                if (actionType == STUDENT_LOGIN_FACE) {
                                    play("学员签到成功，开始培训", 200);
                                    mState = TrainState.TRAINING;

                                    startTrain();

                                    mHandler.sendEmptyMessage(START_TRAIN);
                                } else if (actionType == STUDENT_LOGOUT_FACE) {
                                    mState = TrainState.STUDENT_LOGIN;

                                    stopTrain();

                                    mHandler.sendEmptyMessage(STUDENT_EXIT_SUCCESS);

                                    play("学员签退成功", 200);
                                    reCheckCard();
                                }
                            } else {
                                Log.e("TrainActivity", "接口调用失败：" + new SimpleDateFormat("yyMMdd HH:mm:ss").format(new Date()));
                                play("人脸识别失败，请重新刷卡验证", 200);
                                reCheckCard();
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                            play("人脸识别失败，请重新刷卡验证", 200);
                            reCheckCard();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        isExit = true;
    }

    /**
     * 启动分钟学时
     */
    private void scheduleMinuteRecord() {
        if (trainRecordTimer != null)
            return;
        trainRecordTimer = new Timer();
        trainRecordTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                learnedTimeBeforeShutdown++;
                insertTrainRecord();
            }
        }, 1000 * 50, 1000 * 60);
    }

    /**
     * 停止发送分钟学时
     */
    private void stopMinuteRecord() {
        if (trainRecordTimer != null) {
            trainRecordTimer.cancel();
            trainRecordTimer = null;
        }
    }

    /**
     * 退出超时提醒
     */
    private void stopOutOfTimeAlert() {
        if (mTimeOutTask != null)
            mTimeOutTask.cancel();
    }

    /**
     * 插入分钟学时数据
     */
    private void insertTrainRecord() {
        // TODO: 2019-07-16 OBD
//        distancePerMinute = ObdDataModel.getTotalMiles() - startMilesUsedByMinuteRecord;
//        startMilesUsedByMinuteRecord = ObdDataModel.getTotalMiles();

        // TODO: 2019-07-16 GPS
        distancePerMinute = (int) (IUtil.Distance / 1000 - startMilesUsedByMinuteRecord);
        startMilesUsedByMinuteRecord = (int) IUtil.Distance / 1000;

        maxSpeedPerMinute = ObdDataModel.getSpeed();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            String sTime = sdf.format(new Date(System.currentTimeMillis()));
            Log.e("TrainActivity", "插入分钟学时： " + sTime);
            TrainingRecord t = new TrainingRecord();
            StringBuffer sb = new StringBuffer();
            //sb.append("4167126621176494");
            sb.append(CommonInfo.getTelnetDeviceNumber());
            sb.append(sTime.substring(2, 8));
            SharedPreferences sp = TrainActivity.this.getSharedPreferences("TrainingId", MODE_PRIVATE);
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
            if (mStudentCardInfo == null || mStudentCardInfo.getId() == null)
                return;
            String learner_number = mStudentCardInfo.getId();//StringUtils.bytesToHexString(CommonInfo.getCoachNumber());//学员编号
            t.setTraining_learner_number(learner_number);
            if (mCoachCardInfo == null || mCoachCardInfo.getId() == null) {
                return;
            }
            String instructor_number = mCoachCardInfo.getId(); //教练员编号
            t.setTraining_instructor_number(instructor_number);
            t.setTraining_end_time(sTime.substring(8));
            //t.setTrainClassId((int)CommonInfo.getClassID());
            t.setTrainClassId((int) classId);
            t.setTrainCourseName(CommonInfo.getCourseCode());
            t.setState(0);//0、正常  1、异常
//            t.setMaxSpeed((int) GPSInfo.getSpeed() * 10); // 1/10KM
//            t.setMiles((int) CountDistance.getTotalMile() * 10); // 1/10KM

            int tempDistance = Math.abs(distancePerMinute);
            if (tempDistance > 100)
                t.setMiles(tempDistance / 100);
            else if (tempDistance > 50)
                t.setMiles(1);
            else
                t.setMiles(0);
            t.setMaxSpeed(maxSpeedPerMinute * 10);

            t.setGpsDate(CommonInfo.getGpsPackageTrainRecord());

            byte[] gpsDate = t.getGpsDate();
            StringBuffer buf1 = new StringBuffer();
            for (int i = 28; i < gpsDate.length; i++) {
                buf1.append(gpsDate[i]);
            }
            Log.e("TrainActivity", "insertTrainingRecord: package is " + buf1);
            t.setTraining_uploadStatus(0); //上传状态默认未来上传
            boolean result = Insert_DB.getInstance().insertTrainingRecord(t);
            int trainCount = sendMessageCount.getInt("TrainCount", 0);
            sendMessageEditor.putInt("TrainCount", trainCount + 1);
            sendMessageEditor.commit();
        } catch (Exception ex) {
            Log.e("TrainActivity", "培训记录数据异常:" + ex.getMessage());
        }

        saveRecord();
    }

    /**
     * 启动定时拍照
     */
    private void scheduleTakePicture() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                takePicture(0, 0x05, mStudentCardInfo.getId());
            }
        }, DeviceParameter.getTakePhotoInterval() * 60000, DeviceParameter.getTakePhotoInterval() * 60000);
    }

    /**
     * 结束定时拍照
     */
    private void stopScheduleTakePicture() {
        if (timer != null)
            timer.cancel();
    }

    /**
     * 检查教练车型
     *
     * @return
     */
    private boolean checkCoachCarType(CardInfo cardInfo) {
        if (cardInfo != null && cardInfo.getCarType() != null) {
            String carType = cardInfo.getCarType();
            String localCarType = getSharedPreferences(SharedPreferencesUtil.REGISTER_INFO_FILE, MODE_PRIVATE)
                    .getString(SharedPreferencesUtil.REGISTER_TYPE, "");

            return MilesCalculateUtil.coachCarTypeValid(carType, localCarType);
        } else
            return false;
    }

    /**
     * 处理卡信息
     *
     * @param cardInfo 卡信息
     * @param cardType 卡类型
     */
    private void handleCard(final CardInfo cardInfo, RFID.CardType cardType) {
        switch (mState) {
            case COACH_LOGIN:
                if (!checkCoachCarType(cardInfo)) {
                    play("教练培训车型不符", 200);
                    reCheckCard();
                } else if (cardType != RFID.CardType.PracticeCoachCard
                        && cardType != RFID.CardType.CommonCoachCard) {
                    play("不是实操教练卡", 200);
                    reCheckCard();
                } else {
                    mSoundPool.play(soundSuccess, 1, 1, 0, 0, 1);
                    play("教练员签到成功，请学员刷卡签到", 200);
                    mState = TrainState.STUDENT_LOGIN;
                    mCoachCardInfo = cardInfo;

//                    takePicture(0, 0x14, mCoachCardInfo.getId());
                    insertCoachLoginData(1);

                    mHandler.sendEmptyMessage(COACH_LOGIN_SUCCESS);

                    reCheckCard();
                }

                break;
            case STUDENT_LOGIN:
                String localCarType = getSharedPreferences(SharedPreferencesUtil.REGISTER_INFO_FILE, MODE_PRIVATE)
                        .getString(SharedPreferencesUtil.REGISTER_TYPE, "");
                if (cardType != RFID.CardType.StudentCard) {
                    play("不是学员卡", 200);
                    reCheckCard();
                } else if (!cardInfo.getCarType().equalsIgnoreCase(localCarType)) {
                    play("学员培训车型不符", 200);
                    reCheckCard();
                } else {
                    if (IUtil.isFaceOpen) {
                        mStudentCardInfo = cardInfo;
                        mSoundPool.play(soundSuccess, 1, 1, 0, 0, 1);
                        play("学员刷卡成功，请正对摄像头，开始人脸识别", 200);
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                checkFace(STUDENT_LOGIN_FACE, mStudentCardInfo.getHeadImage());
                            }
                        }, 2000);
                    } else {
                        mStudentCardInfo = cardInfo;
                        mSoundPool.play(soundSuccess, 1, 1, 0, 0, 1);
                        play("学员签到成功，开始培训", 200);
                        mState = TrainState.TRAINING;

//                        takePicture(0, 0x11, mStudentCardInfo.getId());
                        startTrain();

                        mHandler.sendEmptyMessage(START_TRAIN);
                    }
                }
                break;
            case STUDENT_EXIT:
                if (mStudentCardInfo != null && mStudentCardInfo.getUid() == cardInfo.getUid()) {
                    mSoundPool.play(soundSuccess, 1, 1, 0, 0, 1);
                    if (IUtil.isFaceOpen) {
                        play("学员刷卡成功，请正对摄像头，开始人脸识别", 200);
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                checkFace(STUDENT_LOGOUT_FACE, mStudentCardInfo.getHeadImage());
                            }
                        }, 2000);
                    } else {
                        mState = TrainState.STUDENT_LOGIN;

                        stopTrain();

                        mHandler.sendEmptyMessage(STUDENT_EXIT_SUCCESS);

                        play("学员签退成功", 200);
                        reCheckCard();
                    }
                } else {
                    play("学员卡信息有误", 200);
                    reCheckCard();
                }
                break;
            case COACH_EXIT:
                if (mCoachCardInfo != null && mCoachCardInfo.getUid() == cardInfo.getUid()) {
                    mSoundPool.play(soundSuccess, 1, 1, 0, 0, 1);
                    insertCoachLoginData(2);
                    play("教练员签退成功", 200);
                    mState = TrainState.COACH_LOGIN;

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    startCheckCard();

                    mHandler.sendEmptyMessage(COACH_EXIT_SUCCESS);
//                    }
                } else {
                    play("教练卡信息有误", 200);
                }
                break;
            default:
                break;
        }
    }

    private void play(String msg, long time) {
        if (System.currentTimeMillis() - lastPlayTime >= time) {
            lastPlayTime = System.currentTimeMillis();
            SoundManage.ttsPlaySound(this, msg);
        }
    }

    /**
     * 开始培训
     */
    private void startTrain() {
        // TODO: 2019-07-16 OBD
//        startMilesUsedByMinuteRecord = ObdDataModel.getTotalMiles();
//        startMiles = ObdDataModel.getTotalMiles();

        // TODO: 2019-07-16 GPS
        startMilesUsedByMinuteRecord = (int) (IUtil.Distance / 1000);

        CommonInfo.setStuNumber(mStudentCardInfo.getId().getBytes());

        scheduleMinuteRecord();

        clearRecord();

        final String currentDate = new SimpleDateFormat("yyMMdd").format(new Date());
        final String last = mStudentCardInfo.getLastExitDate();

        if (last == null || last.isEmpty() || Integer.parseInt(last) == 0) {
            mStudentCardInfo.setLastExitDate(currentDate);
        } else if (Integer.parseInt(currentDate) > Integer.parseInt(last)) {
            todayPracticeTime = 0;
        } else {
            todayPracticeTime = mStudentCardInfo.getPracticeTime();
        }

        if (todayPracticeTime > 240) {
            play("您今天已累计学满四小时，是否继续学习", 200);
            mHandler.sendEmptyMessage(TIMEOUT);
        } else {
            if (fourHourAlarmTask != null) {
                fourHourAlarmTask.cancel();
                fourHourAlarmTask = null;
            }

            fourHourAlarmTask = new TimerTask() {
                @Override
                public void run() {
                    play("您今天已累计学满四小时，是否继续学习", 200);
                    mHandler.sendEmptyMessage(TIMEOUT);
                }
            };

            new Timer().schedule(fourHourAlarmTask, 4 * 60 * 60 * 1000 - todayPracticeTime * 60 * 1000);
        }


        IUtil.OnClass = true;
        if (record == null) {
            startTime = System.currentTimeMillis();
            insertStudent(1);
            takePicture(0, 0x11, mStudentCardInfo.getId());
        } else {
            startTime = record.getStartTime();
        }

        createNewTimerTask();
        new Timer().schedule(mRefreshTask, 0, 1000);

        if (GPSInfo.getUseCount() > 3) {
            CountDistance.startCount(GPSInfo.getLatitude(), GPSInfo.getLongitude(), GPSInfo.getAngle());
        }

        saveRecord();
    }

    /**
     * 定时保存数据
     */
    private void saveRecord() {
        MinuteRecord record = new MinuteRecord();
        record.setCourseCode(CommonInfo.getCurItem());
        record.setStartTime(startTime);
        record.setLastRecordTime(System.currentTimeMillis());
        record.setLearnedTime(learnedTimeBeforeShutdown);
        record.setClassId(classId);
        record.setCompany(mCoachCardInfo.getCompany());
        // TODO: 2018/10/25 record.setLearnedMiles


        record.setStudentNumber(mStudentCardInfo.getId().getBytes());
        record.setStudentIdentifyNumber(mStudentCardInfo.getIdentification().getBytes());
        record.setStudentCarType(mStudentCardInfo.getCarType());
        record.setStuName(mStudentCardInfo.getName());
        record.setCheckInTimes(mStudentCardInfo.getCheckInTimes());
        record.setSubjectOneLearnedTime(mStudentCardInfo.getSubjectOneLearnedTime());
        record.setSubjectFourLearnedTime(mStudentCardInfo.getSubjectFourLearnedTime());
        record.setSubjectTwoLearnedTime(mStudentCardInfo.getSubjectTwoLearnedTime());
        record.setSubjectTwoLearnedMiles(mStudentCardInfo.getSubjectTwoLearnedMiles());
        record.setSubjectThreeLearnedTime(mStudentCardInfo.getSubjectThreeLearnedTime());
        record.setSubjectThreeLearnedMiles(mStudentCardInfo.getSubjectThreeLearnedMiles());
        record.setPracticeTime(mStudentCardInfo.getPracticeTime());
        record.setVirtualCurrency(mStudentCardInfo.getVirtualCurrency());


        record.setCoachNumber(mCoachCardInfo.getId().getBytes());
        record.setCoachIdentifyNumber(mCoachCardInfo.getIdentification().getBytes());
        record.setCoachName(mCoachCardInfo.getName());
        record.setCoachCarType(mCoachCardInfo.getCarType());

//        record.setLearnedMiles();
        if (minuteRecordDao != null) {
            clearRecord();
            minuteRecordDao.insertOrReplace(record);
        }
    }

    /**
     * 学满四小时提醒弹窗
     */
    private void showTimeOutDialog() {
        if (mTimeOutDialog != null) {
            if (mTimeOutDialog.isShowing())
                mTimeOutDialog.cancel();
            mTimeOutDialog.show();
        }
    }

    private void createNewTimerTask() {
        mRefreshTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mTvTotalTime != null) {
//                            if (CommonInfo.getCurItemLearned() != 0) {
//                                currentLearnedTime = CommonInfo.getCurItemLearned();
//                            } else if (mStudentCardInfo != null) {
//                                if (CommonInfo.getCurItem() == 2 && mStudentCardInfo.getSubjectTwoLearnedTime() != 0)
//                                    currentLearnedTime = mStudentCardInfo.getSubjectTwoLearnedTime();
//                                else if (CommonInfo.getCurItem() == 3 && mStudentCardInfo.getSubjectThreeLearnedTime() != 0)
//                                    currentLearnedTime = mStudentCardInfo.getSubjectThreeLearnedTime();
//                            }
                            mTvTotalTime.setText("累计时长: " + CommonInfo.getCurItemLearned() + "分钟");
                        }
                    }
                });
            }
        };
    }

    private void clearRecord() {
        if (minuteRecordDao != null)
            minuteRecordDao.deleteAll();
    }

    /**
     * 插入教练签到签退信息
     *
     * @param type 1.签到 2.签退
     */
    private void insertCoachLoginData(int type) {
        CoachLoginInfo loginInfo = new CoachLoginInfo();
        loginInfo.setNumber(mCoachCardInfo.getId());
        loginInfo.setIdentifyNumber(mCoachCardInfo.getIdentification());
        loginInfo.setTeachCarType(mCoachCardInfo.getCarType());
        loginInfo.setType(type);
        loginInfo.setGpsDate(CommonInfo.getGpsData());
        Insert_DB.getInstance().insertInstructorLogin(loginInfo);

        if (type == 1)
            takePicture(0, 0x14, mCoachCardInfo.getId());
        else
            takePicture(0, 0x15, mCoachCardInfo.getId());
    }

    /**
     * 停止培训
     */
    private void stopTrain() {
        clearRecord();

        IUtil.OnClass = false;
        mTimer.stop();

        if (mRefreshTask != null)
            mRefreshTask.cancel();
        if (fourHourAlarmTask != null)
            fourHourAlarmTask.cancel();

        insertStudent(2);
        takePicture(0, 0x12, mStudentCardInfo.getId());

        mStudentCardInfo = null;

        CommonInfo.setStuNumber(new byte[0]);
    }

    private void insertStudent(int type) {
        if (mStudentCardInfo == null)
            return;

        StudentLoginInfo info = new StudentLoginInfo();
        int time = 0;

        String loginID;
        if (type == 1) {
            //学员登录
            info.setType(1);
            info.setLogoutDate(new Date());
            info.setMinutes(0);
            info.setClassID(System.currentTimeMillis() / 1000);
            classId = info.getClassID();
        } else {
//            time = (int) ((System.currentTimeMillis() - startTime) / 60000);
            time = learnedTimeBeforeShutdown + 1;
            //学员登出
            if (shutdownTimeout && record != null && record.getLastRecordTime() != 0) {
                info.setLogoutDate(new Date(record.getLastRecordTime()));
                shutdownTimeout = false;
            } else {
                info.setLogoutDate(new Date());
            }
            //info.setMiles((int) (CountDistance.getTotalMile() + iHistoryLearnMiles) / 100);
//            info.setMiles((int) (ObdRunningData.getCurMiles() * 10));
//            if (CommonInfo.getCurItem() == 2)
//                info.setMiles(MilesCalculateUtil.calculateDistance(mStudentCardInfo.getCarType(), (byte) 2, time) / 100);
//                info.setMiles(MilesCalculateUtil.calculateMiles(mStudentCardInfo.getCarType(), mStudentCardInfo.getSubjectTwoLearnedTime(), (byte) 2, mStudentCardInfo.getSubjectTwoLearnedMiles(),
//                        time) / 100);
//            else
            info.setMiles(MilesCalculateUtil.calculateDistance(mStudentCardInfo.getCarType(), (byte) 3, time) / 100);
//                info.setMiles(MilesCalculateUtil.calculateMiles(mStudentCardInfo.getCarType(), mStudentCardInfo.getSubjectThreeLearnedTime(), (byte) 3, mStudentCardInfo.getSubjectThreeLearnedMiles(),
//                        time) / 100);
            info.setMiles(totalMiles / 100);
            totalMiles = 0;
            info.setMinutes(time);
            info.setType(2);
            info.setClassID(classId);
        }
        info.setLoginDate(new Date());
        // TODO: 2019-12-09 实车
        if (CommonInfo.getCurItem() == 2) {
            CommonInfo.setCourseCode("1212130000");
        } else if (CommonInfo.getCurItem() == 3) {
            CommonInfo.setCourseCode("1213360000");
        }
        // TODO: 2019-12-09 模拟
//        CommonInfo.setCourseCode(CourseCodeGenerator.generateDemoCourseCode(CommonInfo.getCurItem(),mStudentCardInfo.getCarType()));
        info.setStuNumber(mStudentCardInfo.getId());
        info.setCoachNumber(mCoachCardInfo.getId());
        info.setCourse(StringUtils.HexStringToBytes(CommonInfo.getCourseCode()));
        info.setGpsDate(CommonInfo.getGpsData());
        Insert_DB.getInstance().insertLearner_Login(info);
    }

    /**
     * 拍照
     */
    private void takePicture(int mode, int entryType, String number) {
        pf = null;
        SimpleDateFormat printSdf = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String photoAbsolutePath;
        photoAbsolutePath = getTrainingPhotoPath();
        pf = new PhotoPrintInfo();
        String sSpeed;
        String sLocation;
        String sDate;
        File file;
        sDate = printSdf.format(new Date());
        sLocation = String.format("%3.5fE-%3.5fN", GPSInfo.getLongitude(), GPSInfo.getLatitude());
        sSpeed = String.format("车速:%dKm/h", (int) ObdDataModel.getSpeed());

        pf.setDatetime(sDate);
        pf.setLocation("经纬：" + sLocation);
        pf.setSpeed(sSpeed);
        pf.setCarNum(DeviceParameter.getLoginPlate() == null ? "" : DeviceParameter.getLoginPlate());
        pf.setCoachName("教练：" + (mCoachCardInfo == null ? "" : mCoachCardInfo.getName()));
        pf.setSchoolName(DeviceParameter.getDrivingName() == null ? "" : DeviceParameter.getDrivingName());
        if (entryType == 0x14 || entryType == 0x15) {
            if (mCoachCardInfo != null && mCoachCardInfo.getIdentification() != null)
                pf.setIdentify(mCoachCardInfo.getIdentification());
            else
                pf.setIdentify("0");
            pf.setDeviceID(CommonInfo.getCoachDrivingNumber() == null ? "" : new String(CommonInfo.getCoachDrivingNumber()));
            pf.setStudentName("");
        } else {
            if (mStudentCardInfo != null && mStudentCardInfo.getIdentification() != null)
                pf.setIdentify(mStudentCardInfo.getIdentification());
            else
                pf.setIdentify("0");
            pf.setDeviceID(CommonInfo.getStuDrivingNumber() == null ? "" : new String(CommonInfo.getStuDrivingNumber()));
            pf.setStudentName("学员：" + (mStudentCardInfo == null ? "" : mStudentCardInfo.getName()));
        }

        captureSnapshot(photoAbsolutePath, (byte) entryType, number);
    }

    private void captureSnapshot(final String filePath, final byte entryType, final String number) {
        if (mCamera != null)
            try {
                mCamera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(final byte[] data, Camera camera) {
                        new Thread() {
                            @Override
                            public void run() {
                                compressBitmap(BitmapFactory.decodeByteArray(data, 0, data.length), filePath,
                                        entryType, number, false);
                            }
                        }.start();
                    }
                });
            } catch (Exception e) {
                Log.e("TrainActivity", "拍照失败");
            }
    }


    private void saveFile(byte[] bytes, String filePath, byte entryType, String number) {
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
                    savePhotoPath(filePath, entryType, number);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void compressBitmap(Bitmap bitmap, String filePath, byte entryType, String number, boolean checkFace) {
        Bitmap mutableBitmap = bitmap.copy(bitmap.getConfig(), true);
        if (!checkFace)
            AddTextToImage.drawTextToBitmap(mutableBitmap, pf);

        int maxFileSize = 30;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options_ = 100;
        mutableBitmap.compress(Bitmap.CompressFormat.JPEG, options_, baos);//质量压缩方法，把压缩后的数据存放到baos中 (100表示不压缩，0表示压缩到最小)

        int baosLength = baos.toByteArray().length;
        while (baosLength / 1024 > maxFileSize) {//循环判断如果压缩后图片是否大于maxMemmorrySize,大于继续压缩
            baos.reset();//重置baos即让下一次的写入覆盖之前的内容
            options_ = Math.max(0, options_ - 10);//图片质量每次减少10
            mutableBitmap.compress(Bitmap.CompressFormat.JPEG, options_, baos);//将压缩后的图片保存到baos中
            baosLength = baos.toByteArray().length;
            if (options_ == 0)//如果图片的质量已降到最低则，不再进行压缩
                break;
        }


        saveFile(baos.toByteArray(), filePath, entryType, number);
    }

    private boolean savePhotoPath(String photoPath, byte eventType, String number) {
        if (eventType == 0) {
            number = new String(CommonInfo.getStuNumber());
        }
        PhotoHeadInformation ptInfo;
        ptInfo = new PhotoHeadInformation(photoPath);
        ptInfo.setTakeID(CommonInfo.getPictureNumber(true));
        ptInfo.setStuNumber(number);
        ptInfo.setClassID((int) classId);//CommonInfo.getClassID());
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

    private String getTrainingPhotoPath() {
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 1) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(permissions, 1);
                    return;
                }
            }

            init();
        } else {
            requestPermissions(permissions, 1);
        }
    }

    //位置更新和超速报警线程
    private Runnable mAlarmRunnable = new Runnable() {
        @Override
        public void run() {
            while (IUtil.OnClass) {
                try {
                    Thread.sleep(2 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (!IUtil.OnClass) {
                    break;
                }
                if (IUtil.EnclosureOut) {//围栏报警
                    play("超出教学区域", 200);
                }
                try {
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if ((ObdDataModel.getSpeed() > maxSpeed) || (GPSInfo.getSpeed() > maxSpeed))
                    play("您已超速", 200);
            }
        }
    };

    /**
     * 更新位置信息
     */
    private void refreshLocation() {
        if (GPSInfo.getLongitude() < 0)
            mTvLongitude.setText("经度: 0");
        else
            mTvLongitude.setText("经度: " + String.format("%.6f", GPSInfo.getLongitude()));
        if (GPSInfo.getLatitude() < 0)
            mTvLatitude.setText("纬度: 0");
        else
            mTvLatitude.setText("纬度: " + String.format("%.6f", GPSInfo.getLatitude()));

        // TODO: 2019-05-27 OBD
//        totalMiles = ObdDataModel.getTotalMiles() - startMiles;
//        if (IUtil.OnClass) {
//            if (totalMiles / 1000 < 1) {
//                mTvTrainMiles.setText("训练里程: 0" + new DecimalFormat("#.00").format(totalMiles / 1000.0) + " km");
//            } else {
//                mTvTrainMiles.setText("训练里程: " + new DecimalFormat("#.00").format(totalMiles / 1000.0) + " km");
//            }
//        }
//
//        mTvSpeed.setText("当前车速：" + ObdDataModel.getSpeed() + "km/h");
//
//        //刷新每分钟最大速度
//        if (maxSpeedPerMinute < ObdDataModel.getSpeed())
//            maxSpeedPerMinute = ObdDataModel.getSpeed();


        // TODO: 2019-05-27 GPS
        if (IUtil.OnClass) {
//            totalMiles = ObdDataModel.getTotalMiles() - startMiles;
            totalMiles = (int) (IUtil.Distance / 1000);
            mTvTrainMiles.setText("训练里程: 0" + new DecimalFormat("#.000").format(IUtil.Distance / 1000) + " km");
        }
        mTvSpeed.setText("当前车速：" + (int) GPSInfo.getSpeed() + "km/h");

        if (maxSpeedPerMinute < GPSInfo.getSpeed())
            maxSpeedPerMinute = (int) GPSInfo.getSpeed();


//        if (speed < 1) {
//            mTvSpeed.setText("当前车速: 0" + new DecimalFormat("#.000").format(speed) + "km/h");
//        } else {
//            mTvSpeed.setText("当前车速: " + new DecimalFormat("#.000").format(speed) + " km/h");
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        AppContext.Stopped = true;

        if (mLocationTask != null)
            mLocationTask.cancel();

        if (mRefreshTask != null)
            mRefreshTask.cancel();

        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }

        stopOutOfTimeAlert();

        stopMinuteRecord();
    }
}
