package com.serenegiant.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.navigation.timerterminal.R;
import com.serenegiant.AppContext;
import com.serenegiant.http.HttpConfig;
import com.serenegiant.net.CommonInfo;
import com.serenegiant.net.DeviceParameter;
import com.serenegiant.net.TcpClient;
import com.serenegiant.utils.IUtil;
import com.serenegiant.utils.SharedPreferencesUtil;
import com.serenegiant.utils.Utils;

public class SetSecketActivity extends Activity {

    private EditText ip, port, sim, carNum, time, speed, etCarType;
    private Button bt_back, bt_login;
    boolean isFirst, isConfigFirst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_secket);


        /*
         * 第一次打开应用填写
         * */
        SharedPreferencesUtil sharedPreferences = SharedPreferencesUtil.getInstance(getApplication());
        isFirst = sharedPreferences.getsharepreferencesBoolean(IUtil.FirstLogin);
        isConfigFirst = sharedPreferences.getsharepreferencesBoolean("isConfigFirst");
        /*
         * 加载 是否签退上传照片
         * */
        DeviceParameter.setStudentFingerEnOut(sharedPreferences.getsharepreferencesBoolean(IUtil.STOUT));
        DeviceParameter.setCoachFingerEnOut(sharedPreferences.getsharepreferencesBoolean(IUtil.CACHOUT));
        if (isFirst && isConfigFirst) {
            if (IUtil.IsFirstIn) {
                IUtil.IsFirstIn = false;
                CommonInfo.setDeviceLoginState(0);
                Login();
                startActivity(new Intent(SetSecketActivity.this, AutomaticDetectionActivity.class));
            }
        } else {
            DeviceParameter.setLoginIP(HttpConfig.SERVER_IP);
            sharedPreferences.setsharepreferencesBoolean(IUtil.FirstLogin, true);
            sharedPreferences.setsharepreferencesBoolean("isConfigFirst",true);
        }
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        etCarType = findViewById(R.id.etCarType);
        etCarType.setText(getSharedPreferences(SharedPreferencesUtil.REGISTER_INFO_FILE, MODE_PRIVATE)
                .getString(SharedPreferencesUtil.REGISTER_TYPE, ""));
        ip = findViewById(R.id.etSetIP);
        port = findViewById(R.id.etSetPort);
        sim = findViewById(R.id.etSetTerminalID);
        carNum = findViewById(R.id.etSetPlateNo);
        time = findViewById(R.id.camera_time);
        speed = findViewById(R.id.speed);
        bt_login = findViewById(R.id.btn_login);
        bt_back = findViewById(R.id.btn_back);

        if (isFirst) {
            bt_back.setText("返回");
        }
//        ip.setText("" + DeviceParameter.getLoginIP());
//        port.setText("" + DeviceParameter.getLoginPort());
//        if (DeviceParameter.getDeviceNumber() != null && !DeviceParameter.getDeviceNumber().isEmpty())
//            sim.setText("" + DeviceParameter.getDeviceNumber().substring(5));
//        carNum.setText("" + DeviceParameter.getLoginPlate());
        if (DeviceParameter.getLoginIP() != null && !DeviceParameter.getLoginIP().isEmpty())
            ip.setText(DeviceParameter.getLoginIP());
        else {
//            ip.setText("114.116.74.190");
            ip.setText(HttpConfig.SERVER_IP);
        }

        if (DeviceParameter.getLoginPort() != 0)
            port.setText("" + DeviceParameter.getLoginPort());
        else {
//            port.setText(HttpConfig.SERVER_PORT);
            port.setText("2346");
        }

        if (DeviceParameter.getDeviceNumber() != null && !DeviceParameter.getDeviceNumber().isEmpty())
            sim.setText("" + DeviceParameter.getDeviceNumber().substring(5));
        else
            sim.setText("13745612378");

        if (DeviceParameter.getLoginPlate() != null && !DeviceParameter.getLoginPlate().isEmpty())
            carNum.setText("" + DeviceParameter.getLoginPlate());
        else
            carNum.setText("湘KE609学");

        /*
         * 超速阀值为新加参数
         * */
        int st = SharedPreferencesUtil.getInstance(getApplication()).getsharepreferencesInt(IUtil.MAX_SPEED);
        if (st <= 0)
            st = 0;
        speed.setText("" + st);
        time.setText("" + DeviceParameter.getTakePhotoInterval());
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonSure();

            }
        });
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SetSecketActivity.this, AutomaticDetectionActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    /*
     * 注册
     * */
    private void Login() {
        boolean isHadRegister = getSharedPreferences(SharedPreferencesUtil.REGISTER_INFO_FILE, MODE_PRIVATE)
                .getBoolean(SharedPreferencesUtil.REGISTER_SUCCESS, false);
        if (!isHadRegister) {
            //之前未成功注册状态
            TcpClient.getInstance(SetSecketActivity.this).handlerState = TcpClient.EnumState.UNREGISTER;
//                            byte[] sendBytes = TcpClient.getInstance(getApplication()).getCmdData(0x0100, 0);
//                            TcpClient.getInstance(getApplication()).sendTcpBytes(sendBytes);
        } else {
            //之前已成功注册状态
            TcpClient.getInstance(SetSecketActivity.this).handlerState = TcpClient.EnumState.UNLOGIN;
//                            byte[] sendBytes = getCmdData(GPRS_ID_CMD0003, 0);
        }
    }

    private void buttonSure() {
        String sTemp;
        int tempValue;

        tempValue = Integer.valueOf(time.getText().toString());
        if (tempValue < 1) {
            Toast.makeText(SetSecketActivity.this, "拍照间隔不能小于1分钟！", Toast.LENGTH_SHORT).show();
            return;
        }
        DeviceParameter.setTakePhotoInterval((byte) tempValue);//设置拍照时间间隔

        tempValue = Integer.valueOf(speed.getText().toString());
        if (tempValue < 0) {
            Toast.makeText(SetSecketActivity.this, "超速阀值不能小于0", Toast.LENGTH_SHORT).show();
            return;
        }
        DeviceParameter.setmaxSpeed((byte) tempValue);//设置拍照时间间隔
        SharedPreferencesUtil.getInstance(getApplication()).setsharepreferences(IUtil.MAX_SPEED, tempValue);
        sTemp = ip.getText().toString();
        if (sTemp.length() == 0) {
            Toast.makeText(SetSecketActivity.this, "IP地址不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
//        if (!Utils.isIP(sTemp)) {
//            Toast.makeText(SetSecketActivity.this, "IP地址不正确", Toast.LENGTH_SHORT).show();
//            return;
//        }

        DeviceParameter.setLoginIP(sTemp);

        sTemp = port.getText().toString();
        tempValue = Integer.valueOf(sTemp);
        if (tempValue > 65535) {
            Toast.makeText(SetSecketActivity.this, "端口号设置无效！", Toast.LENGTH_SHORT).show();
            return;
        }
        DeviceParameter.setLoginPort(tempValue);

        sTemp = carNum.getText().toString();
        if (sTemp.length() == 0) {
            Toast.makeText(SetSecketActivity.this, "车牌号不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Utils.isCarnumberNO(sTemp) || sTemp.length() != 7) {
            Toast.makeText(SetSecketActivity.this, "不是教练员车辆", Toast.LENGTH_SHORT).show();
            return;
        }
        DeviceParameter.setLoginPlate(carNum.getText().toString());

        sTemp = "00000" + sim.getText().toString();
        if (sTemp.length() == 0) {
            Toast.makeText(SetSecketActivity.this, "设备编号不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
//        if (!Utils.isPhoneLegal(sTemp.substring(5))) {
//            Toast.makeText(SetSecketActivity.this, "手机号不正确！", Toast.LENGTH_SHORT).show();
//            return;
//        }
        IUtil.Num = sim.getText().toString();
        DeviceParameter.setDeviceNumber(sTemp);


        if (etCarType.getText() == null || etCarType.getText().toString().isEmpty()) {
            Toast.makeText(SetSecketActivity.this, "车型不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sp = getSharedPreferences(SharedPreferencesUtil.REGISTER_INFO_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (null != editor) {
            editor.putString(SharedPreferencesUtil.REGISTER_TYPE, etCarType.getText().toString());
            editor.commit();
        }

        DeviceParameter.createParameterXML();//保存配置文件
        Login();
        startActivity(new Intent(SetSecketActivity.this, AutomaticDetectionActivity.class));
        finish();
    }


}
