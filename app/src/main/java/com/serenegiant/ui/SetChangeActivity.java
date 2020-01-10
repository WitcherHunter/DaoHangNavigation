package com.serenegiant.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.navigation.timerterminal.R;
import com.serenegiant.AppContext;
import com.serenegiant.net.TcpClient;
import com.serenegiant.rfid.CardInfo;
import com.serenegiant.rfid.OnCardCheckedListener;
import com.serenegiant.rfid.RFID;
import com.serenegiant.rfid.RfidThread;
import com.serenegiant.utils.IUtil;
import com.serenegiant.utils.MessageDefine;
import com.serenegiant.utils.SoundManage;

import java.util.Timer;
import java.util.TimerTask;

import static com.serenegiant.ui.SetActivity.imei;

/**
 * A login screen that offers login via email/password.
 */
public class SetChangeActivity extends Activity implements View.OnClickListener, OnCardCheckedListener {
    private String TAG = "SetChangeActivity";
    private Button setThis, setCard;
    private boolean ThisActivity = false; // 是否开启读卡线程
    private long flagPlayTiem;
    private TextView IMEI, NUM;

    private RFID rfid;
    private RfidThread mRfidThread;
    private boolean threadStarted = false;

    private TimerTask speechTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_change);

        rfid = new RFID(this);

        setThis = findViewById(R.id.setThis);
        setCard = findViewById(R.id.setOhter);
        IMEI = findViewById(R.id.tvSetIMEI);
        NUM = findViewById(R.id.tvSetNUM);
        IMEI.setText("IMEI：" + getImei());
        NUM.setText("序列号：" + Build.SERIAL.substring(1, Build.SERIAL.length()));
        setThis.setOnClickListener(this);
        setCard.setOnClickListener(this);
        setThis.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                TcpClient.getInstance(SetChangeActivity.this).handlerState = TcpClient.EnumState.REGCOMPLICT;
                MessageDefine.MSG_TERMINAL_CANCEL = 1;
                return false;
            }
        });
    }

    private String getImei(){
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (manager != null)
            return manager.getDeviceId();
        else
            return "";
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setThis:
                startActivity(new Intent(SetChangeActivity.this, SetSystemActivity.class));
                break;
            case R.id.setOhter:
                AlertDialog.Builder dialog = new AlertDialog.Builder(SetChangeActivity.this);
                final EditText etPassword = new EditText(SetChangeActivity.this);
                dialog.setView(etPassword);
                dialog.setMessage("请输入密码：");
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (etPassword.getText() != null && !etPassword.getText().toString().isEmpty()) {
                            if (etPassword.getText().toString().equals(IUtil.managerCardPassword)) {
                                startActivity(new Intent(SetChangeActivity.this, SetSecketActivity.class));
                                dialog.cancel();
                            } else
                                Toast.makeText(SetChangeActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(SetChangeActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dialog.setCancelable(false);
                dialog.show();
                break;
        }
    }

    private void startCheckCard() {
        if (mRfidThread == null) {
            mRfidThread = new RfidThread(rfid, this);
            mRfidThread.start();
        } else if (!mRfidThread.isAlive()) {
            mRfidThread = null;
            AppContext.Stopped = true;
            mRfidThread = new RfidThread(rfid, this);
            mRfidThread.start();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        flagPlayTiem = System.currentTimeMillis();
    }

    private synchronized void play(String txt) {
        flagPlayTiem = System.currentTimeMillis();
        SoundManage.ttsPlaySound(SetChangeActivity.this, txt);
    }

    @Override
    public void onReadSuccess(long uid) {

    }


    @Override
    protected void onPause() {
        super.onPause();

        AppContext.Stopped = true;

        if (speechTask != null)
            speechTask.cancel();

        if (setCard != null)
            setCard.setClickable(true);

        mRfidThread = null;
    }

    /*IMEI*/
    public String getDeviceId(Context context) {

        String mDeviceId = getTelephonyManager(context).getDeviceId();// String
//        String mDeviceId = getTelephonyManager(context).getImei();// String
//        String mDeviceId = getTelephonyManager(context).getDeviceId(TelephonyManager.PHONE_TYPE_GSM);// String
//        String myIMSI = android.os.SystemProperties.get(android.telephony.TelephonyProperties.PROPERTY_IMSI);
//        return "869235025762255";
//        return "869235025764194";
        return mDeviceId;
    }

    public static TelephonyManager getTelephonyManager(Context context) {
        // 获取telephony系统服务，用于取得SIM卡和网络相关信息
        TelephonyManager mTelephonyManager = null;
        if (mTelephonyManager == null) {
            mTelephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
        }
        return mTelephonyManager;
    }
}

