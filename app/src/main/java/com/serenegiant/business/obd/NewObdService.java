package com.serenegiant.business.obd;

import android.util.Log;

import com.serenegiant.entiy.ObdDataModel;
import com.serenegiant.entiy.WarningFlag;
import com.serenegiant.utils.HexUtil;

import java.util.List;

public class NewObdService extends ObdService {
    private static final String TAG = "NewObdService";

    @Override
    public void onDataReceived(byte[] bytes) {
        super.onDataReceived(bytes);

        Log.e(TAG, "onDataReceived: " + new String(bytes));

        for (byte aByte : bytes) {
            tempDataList.add(aByte);
        }

        int length = tempDataList.size();
        if (length > 2 && tempDataList.get(length - 1) == 0x0A &&
                tempDataList.get(length - 2) == 0x0D){
//            System.out.println("接收: " + HexUtil.bytesToHexString(tempDataList));

            if (tempDataList.size() > 5 && tempDataList.get(4) == 0x41 && tempDataList.get(5) == 0x02){
                List<Byte> data = tempDataList.subList(6,length - 3);
                System.out.println(HexUtil.bytesToHexString(data));

                ObdDataModel.setTotalMiles(HexUtil.byteArrayToInt(data.subList(0,4)));
                ObdDataModel.setTotalFuel(HexUtil.byteArrayToInt(data.subList(4,8)));
                ObdDataModel.setCurrentFuel(HexUtil.byteArrayToInt(data.subList(8,12)));
                ObdDataModel.setVoltage(HexUtil.bytes2Short(data.subList(12,14)));
                ObdDataModel.setSpeed(HexUtil.byteToInt(data.get(16)));

                List<Byte> rpmList = data.subList(14,16);
                byte[] rpm = new byte[]{0x00,0x00,rpmList.get(0),rpmList.get(1)};
                ObdDataModel.setRotatingSpeed(HexUtil.byteArrayToInt(rpm));

                if (ObdDataModel.getSpeed() > 80){
                    WarningFlag.setWarning(WarningFlag.WARNING_OVER_SPEED);
                } else {
                    WarningFlag.clearWarning(WarningFlag.WARNING_OVER_SPEED);
                }

                System.out.println("累计里程：" + ObdDataModel.getTotalMiles());
                System.out.println("累计油量：" + ObdDataModel.getTotalFuel());
                System.out.println("瞬时油耗：" + ObdDataModel.getCurrentFuel());
                System.out.println("电压：" + ObdDataModel.getVoltage());
                System.out.println("速度：" + ObdDataModel.getSpeed());
                System.out.println("转速：" + ObdDataModel.getRotatingSpeed());
            }

            tempDataList.clear();
        }
    }

    @Override
    public void onDataSent(byte[] bytes) {

    }

    @Override
    void processData(String data) {

    }
}
