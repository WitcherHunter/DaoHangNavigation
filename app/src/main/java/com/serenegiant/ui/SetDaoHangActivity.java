package com.serenegiant.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.navigation.timerterminal.R;
import com.serenegiant.AppConfig;
import com.serenegiant.net.DeviceParameter;
import com.serenegiant.utils.IUtil;
import com.serenegiant.utils.MyToast;
import com.serenegiant.utils.SharedPreferencesUtil;
import com.serenegiant.utils.Utils;
import com.serenegiant.view.SwitchButton;

public class SetDaoHangActivity extends Activity implements CompoundButton.OnCheckedChangeListener{
    public static final String VERIFY_MODE = "verify_mode";

    public static final int MODE_FINGER = 1;
    public static final int MODE_FACE = 2;
    public static final int MODE_IDENTIFICATION = 3;
    public static final int MODE_NONE = 0;


    private EditText ip,port,sim,carNum,time,speed,SchoolName, etCarType, etTimeOut;
    private Button bt_back,bt_login;
    private SwitchButton sbstin,sbstout,sbcoachin,sbcoachout, faceVerifyButton;
    private SharedPreferencesUtil util;
    private AlertDialog mUnLockDialog;
    private TextView door;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_dao_hang);
        util = SharedPreferencesUtil.getInstance(SetDaoHangActivity.this);
        initView();
    }

    private void initView() {
        etCarType = findViewById(R.id.etCarType);
        ip = findViewById(R.id.etSetIP);
        port = findViewById(R.id.etSetPort);
        sim = findViewById(R.id.etSetTerminalID);
        carNum = findViewById(R.id.etSetPlateNo);
        time = findViewById(R.id.camera_time);
        speed = findViewById(R.id.speed);
        SchoolName = findViewById(R.id.SchoolName);
        bt_login = findViewById(R.id.btn_login);
        bt_back = findViewById(R.id.btn_back);
        ip.setText(""+ DeviceParameter.getLoginIP());
        port.setText(""+DeviceParameter.getLoginPort());
        if(DeviceParameter.getDeviceNumber() != null)
            sim.setText(""+DeviceParameter.getDeviceNumber().substring(5));
        carNum.setText(""+DeviceParameter.getLoginPlate());
        sbstin = findViewById(R.id.sbStudentFingerEn);
        sbcoachin = findViewById(R.id.sbCoachFingerEn);
        sbstout = findViewById(R.id.tvStudentFingerEnOut);
        sbcoachout = findViewById(R.id.tvCoachFingerEnOut);
        faceVerifyButton = findViewById(R.id.faceVerify);
        sbstin.setOnCheckedChangeListener(this);
        sbcoachin.setOnCheckedChangeListener(this);
        sbstout.setOnCheckedChangeListener(this);
        sbcoachout.setOnCheckedChangeListener(this);
        faceVerifyButton.setOnCheckedChangeListener(this);
        door = findViewById(R.id.door);
        etTimeOut = findViewById(R.id.etTimeOut);
        /*
        * 超速阀值为新加参数
        * */
        time.setText(""+DeviceParameter.getTakePhotoInterval());

        /*
        * 超速阀值
        * */
        int st = util.getsharepreferencesInt(IUtil.MAX_SPEED);
        if(st <= 0)
            st = 0;
        speed.setText(""+st);


        SchoolName.setText(""+DeviceParameter.getDrivingName());
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonSure();
                startActivity(new Intent(SetDaoHangActivity.this,AutomaticDetectionActivity.class));
            }
        });
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonSure();
                startActivity(new Intent(SetDaoHangActivity.this,AutomaticDetectionActivity.class));
            }
        });
        door.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ShowDialog();
                return false;
            }
        });
        sbstin.setChecked(DeviceParameter.isStudentFingerEn());
        sbcoachin.setChecked(DeviceParameter.isCoachFingerEn());
        sbstout.setChecked(util.getsharepreferencesBoolean(IUtil.STOUT));
        sbcoachout.setChecked(util.getsharepreferencesBoolean(IUtil.CACHOUT));
//        faceVerifyButton.setChecked(DeviceParameter.getUpPhotoMode() == (byte) 1 ? true : false);

        SharedPreferences sp = getSharedPreferences("VerifyMode", MODE_PRIVATE);
        if (sp.getInt(VERIFY_MODE,MODE_NONE) == MODE_FACE)
            faceVerifyButton.setChecked(true);
        else
            faceVerifyButton.setChecked(false);


        etCarType.setText(getSharedPreferences(SharedPreferencesUtil.REGISTER_INFO_FILE, MODE_PRIVATE)
                .getString(SharedPreferencesUtil.REGISTER_TYPE, ""));

        etTimeOut.setText("" + getSharedPreferences(SharedPreferencesUtil.REGISTER_INFO_FILE, MODE_PRIVATE)
                .getInt(SharedPreferencesUtil.TIME_OUT, 10));
    }

    /*
       * 所有EditText的验证判断
       * */
    private void buttonSure(){
        String sTemp;
        int tempValue;

        tempValue = Integer.valueOf(time.getText().toString());
        if (tempValue < 1)
        {
            Toast.makeText(SetDaoHangActivity.this, "拍照间隔不能小于1分钟！", Toast.LENGTH_SHORT).show();
            return;
        }
        DeviceParameter.setTakePhotoInterval((byte)tempValue);//设置拍照时间间隔

        sTemp = ip.getText().toString();
        if(sTemp.length() == 0)
        {
            Toast.makeText(SetDaoHangActivity.this, "IP地址不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
//        if(!Utils.isIP(sTemp))
//        {
//            Toast.makeText(SetDaoHangActivity.this, "IP地址不正确", Toast.LENGTH_SHORT).show();
//            return;
//        }

        DeviceParameter.setLoginIP(sTemp);
        sTemp = SchoolName.getText().toString();
        if(sTemp.length() == 0)
        {
            Toast.makeText(SetDaoHangActivity.this, "驾校名不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }

        DeviceParameter.setDrivingName(sTemp);

        sTemp = port.getText().toString();
        tempValue = Integer.valueOf(sTemp);
        if(tempValue > 65535)
        {
            Toast.makeText(SetDaoHangActivity.this, "端口号设置无效！", Toast.LENGTH_SHORT).show();
            return;
        }
        DeviceParameter.setLoginPort(tempValue);

        sTemp = carNum.getText().toString();
        if (sTemp.length() == 0)
        {
            Toast.makeText(SetDaoHangActivity.this, "车牌号不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Utils.isCarnumberNO(sTemp) || sTemp.length() != 7)
        {
            Toast.makeText(SetDaoHangActivity.this, "不是教练员车辆", Toast.LENGTH_SHORT).show();
            return;
        }
        DeviceParameter.setLoginPlate(carNum.getText().toString());

        sTemp = "00000"+sim.getText().toString();
        if (sTemp.length() == 0){
            Toast.makeText(SetDaoHangActivity.this, "设备编号不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
//        if (!Utils.isPhoneLegal(sTemp.substring(5))){
//            Toast.makeText(SetDaoHangActivity.this, "手机号不正确！", Toast.LENGTH_SHORT).show();
//            return;
//        }
        IUtil.Num = sim.getText().toString();
        DeviceParameter.setDeviceNumber(sTemp);

        if (etCarType.getText() == null || etCarType.getText().toString().isEmpty()){
            Toast.makeText(SetDaoHangActivity.this, "车型不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }

        if (etTimeOut.getText() == null || etTimeOut.getText().toString().isEmpty()) {
            Toast.makeText(SetDaoHangActivity.this, "关机超时时间不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }


        SharedPreferences sp = getSharedPreferences(SharedPreferencesUtil.REGISTER_INFO_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if(null != editor){
            editor.putString(SharedPreferencesUtil.REGISTER_TYPE, etCarType.getText().toString());
            editor.putInt(SharedPreferencesUtil.TIME_OUT, Integer.valueOf(etTimeOut.getText().toString()));
            editor.commit();
        }

        tempValue = Integer.valueOf(speed.getText().toString());
        if (tempValue <= 0)
        {
            Toast.makeText(SetDaoHangActivity.this, "超速阀值不能小于0", Toast.LENGTH_SHORT).show();
            return;
        }
        DeviceParameter.setmaxSpeed((byte)tempValue);
        util.setsharepreferences(IUtil.MAX_SPEED,tempValue);


        DeviceParameter.createParameterXML();//保存配置文件
    }


    /*
    * 滑动按钮状态变化监听
    * */
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()){
            case R.id.sbStudentFingerEn :
                if (isChecked) {
                    DeviceParameter.setStudentFingerEn(true);
                } else {
                    DeviceParameter.setStudentFingerEn(false);
                }
                break;
            case R.id.sbCoachFingerEn :
                if (isChecked) {
                    DeviceParameter.setCoachFingerEn(true);
                } else {
                    DeviceParameter.setCoachFingerEn(false);
                }
                break;
            case R.id.tvStudentFingerEnOut :
                if (isChecked) {
                    DeviceParameter.setStudentFingerEnOut(true);
                    util.setsharepreferencesBoolean(IUtil.STOUT,true);
                } else {
                    DeviceParameter.setStudentFingerEnOut(false);
                    util.setsharepreferencesBoolean(IUtil.STOUT,false);
                }
                break;
            case R.id.tvCoachFingerEnOut :
                if (isChecked) {
                    DeviceParameter.setCoachFingerEnOut(true);
                    util.setsharepreferencesBoolean(IUtil.CACHOUT,true);
                } else {
                    DeviceParameter.setCoachFingerEnOut(false);
                    util.setsharepreferencesBoolean(IUtil.CACHOUT,false);
                }
                break;
            case R.id.faceVerify:
                SharedPreferences sp = getSharedPreferences("VerifyMode", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                if (isChecked) {
                    editor.putInt(VERIFY_MODE,MODE_FACE);
//                    DeviceParameter.setUpPhotoMode((byte) 1);
                } else {
                    editor.putInt(VERIFY_MODE,MODE_NONE);
//                    DeviceParameter.setUpPhotoMode((byte) 2);
                }
                editor.commit();
                break;
        }
    }

    /*
    * 可以退出应用的后门 需要输入密码
    * */
    private void  ShowDialog(){
        if(null != mUnLockDialog){
            mUnLockDialog.show();
        } else {
            final EditText mPwdEt = new EditText(SetDaoHangActivity.this);
            mUnLockDialog = new AlertDialog.Builder(SetDaoHangActivity.this)
                    .setTitle("请输入管理员密码")
                    .setView(mPwdEt)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String pwd = mPwdEt.getText().toString();
                            if(AppConfig.UNLOCK_PWD.equals(pwd)){
//                                Intent intent = new Intent("android.intent.action.HOME_ENABLE");
//                                sendBroadcast(intent);
//                                Intent intent2 = new Intent("android.intent.action.PANEL_ENABLE");
//                                sendBroadcast(intent2);
//                                Intent intent3 = new Intent("android.intent.action.MENU_ENABLE");
//                                sendBroadcast(intent3);
//                                dialogInterface.dismiss();
//                                MyToast.show(SetDaoHangActivity.this, "解锁成功");
//                                Timer timer = new Timer();
//                                TimerTask task = new TimerTask() {
//                                    @Override
//                                    public void run() {
//                                        Intent intent = new Intent("android.intent.action.HOME_DISABLED");
//                                        sendBroadcast(intent);
//                                        Intent intent2 = new Intent("android.intent.action.PANEL_DISABLED");
//                                        sendBroadcast(intent2);
//                                        Intent intent3 = new Intent("android.intent.action.MENU_DISABLED");
//
//                                        sendBroadcast(intent3);
//                                    }
//                                };
//                                timer.schedule(task,30*1000,30*1000);
//                                MyToast.show(SetDaoHangActivity.this, "解锁成功");
//                                Timer timer = new Timer();
//                                TimerTask task = new TimerTask() {
//                                    @Override
//                                    public void run() {
//                                    }
//                                };
//                                timer.schedule(task, 30 * 1000, 30 * 1000);
                                Intent intent = new Intent(Settings.ACTION_DISPLAY_SETTINGS);
                                startActivity(intent);
                            } else {
                                MyToast.show(SetDaoHangActivity.this, "密码不正确");
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
    }
}
