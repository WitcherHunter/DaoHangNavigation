package com.serenegiant.business.obd;

import android.util.Log;

import com.serenegiant.entiy.ObdDataModel;

/**
 * 速锐得obd
 */
public class SWDObdService extends ObdService {

    private static final String TAG = "SWObdService";

    @Override
    public void onDataReceived(byte[] bytes) {
        super.onDataReceived(bytes);

//        Log.e(TAG, "onDataReceived: " + new String(bytes));
        String str = new String(bytes);
        buffer.append(str);
        if (buffer.indexOf("\r\n") != -1) {
            processData(buffer.toString());
            buffer.setLength(0);
        }
    }

    @Override
    public void onDataSent(byte[] bytes) {

    }

    @Override
    void processData(String data) {
        if (data.contains("$OBD-RT")) {
            String[] strArray = data.split(",");
            if (strArray != null && strArray.length > 12) {
                Log.e(TAG, "电压：" + strArray[1]);
                ObdDataModel.setVoltage((int) (Float.parseFloat(strArray[1]) * 1000));
                Log.e(TAG, "转速：" + strArray[2]);
                ObdDataModel.setRotatingSpeed(Integer.parseInt(strArray[2]));
                Log.e(TAG, "车速：" + strArray[3]);
                ObdDataModel.setSpeed(Integer.parseInt(strArray[3]));
                Log.e(TAG, "瞬时油耗：" + strArray[7]);
                Log.e(TAG, "平均油耗：" + strArray[8]);
                Log.e(TAG, "本次行驶里程：" + strArray[9]);
                ObdDataModel.setTotalMiles((int) (Float.parseFloat(strArray[9]) * 1000));
                Log.e(TAG, "总里程：" + strArray[10]);
                Log.e(TAG, "本次耗油量：" + strArray[11]);
                Log.e(TAG, "累计耗油量：" + strArray[12]);
            }
        }
    }
}
