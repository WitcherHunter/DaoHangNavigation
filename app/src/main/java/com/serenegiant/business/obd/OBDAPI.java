package com.serenegiant.business.obd;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.rscja.deviceapi.Module;
import com.rscja.deviceapi.exception.ConfigurationException;
import com.rscja.utility.StringUtility;
import com.serenegiant.AppConfig;
import com.serenegiant.net.ObdRunningData;
import com.serenegiant.utils.MessageDefine;

/**
 * Created by Administrator on 2016/6/21 0021.
 */
public class OBDAPI {

    String Tag="ODB";
    boolean DEBUG= AppConfig.DEBUG_EN;
    private static Module mInstance;//OBD数据
    private static boolean isHex=false;
    private Handler handler;
    private  static boolean isStop=false;
    ReceiveThread receiveThread;

    public OBDAPI()
    {
        if(mInstance==null)
            try {
                mInstance=Module.getInstance();
               // send("ATROFF");//关闭广播
            } catch (ConfigurationException e) {
                e.printStackTrace();
            }
    }

    public boolean init()
    {
        try
        {
           // return true;
            return  mInstance.init(5, 9600);
        }catch (Exception ex)
        {
            return false;
        }
    }
    public  boolean free()
    {
//         return   true;
       return mInstance.free();
    }
    private boolean send(String strAT) {
        String data =strAT;
        if (StringUtility.isEmpty(data)) {
            return false;
        }
        byte[] sendBytes = null;
        if (isHex) {
            sendBytes = StringUtility.hexString2Bytes(data);
        } else {
            byte[] bdata = data.getBytes();
            sendBytes = new byte[bdata.length + 2];
            for (int i = 0; i < bdata.length; i++) {
                sendBytes[i] = bdata[i];
            }
            sendBytes[sendBytes.length - 2] = '\r';
            sendBytes[sendBytes.length - 1] = '\n';
        }
        if (mInstance.send(sendBytes)) {
             Log.i("OBD", "sendBytes = " + arrayToString(sendBytes));
             return true;
        } else {
             Log.i("OBD", "sendBytes = " + arrayToString(sendBytes));
            return false;
        }
    }

    private String  receive(){
        byte[] data;
        data = mInstance.receive();
        if (data != null && data.length > 0) {
            if (isHex) {
                return StringUtility.bytes2HexString(data, data.length);
            } else {
                return new String(data);
            }
        }
        return "";
    }

    ///行驶速度,返回值
    ///[0]:  当前速度
    ///[1]:  本次行程最大值
    ///[2]   本次行程平均值
    public  String[] getDrivingSpeed()
    {
        try
        {
            String at="AT013";
            String head="$013=";
            if(send(at))
            {
                Thread.sleep(100);
                String data=receive();
                if(data.contains(head))
                {
                    String[] str=data.split(",");
                    String current=(str[0].replace(head,""));
                    String max=str[1];
                    String average=str[2];
                    String[] speed=new String[3];
                    speed[0]=current;
                    speed[1]=max;
                    speed[2]=average;
                    return  speed;
                }
            }
            return null;
        }catch (Exception ex)
        {
            return null;
        }
    }

    /*
    * 得到里程
    *
    *返回值：【0】：本次里程    [1]：累计里程     [2]: 总里程
    *
    */
    public  String[] getDrivingMileage()
    {
        try
        {
            String at="AT300";
            String head="$300=";
            if(send(at))
            {
                Thread.sleep(100);
                String data=receive();
                if(data.contains(head))
                {
                    String[] str=data.split(",");
                    String current=(str[0].replace(head,""));
                    String count=str[1];
                    String total=str[2];
                    String[] mileage=new String[3];
                    mileage[0]=current;
                    mileage[1]=count;
                    mileage[2]=total;
                    return  mileage;
                }
            }
            return null;
        }catch (Exception ex)
        {
            return null;
        }
    }


    ///开始监控
    public void startReceive(Handler handler)
    {

        if(receiveThread==null)
        {
            if(DEBUG)Log.i(Tag,"startReceive");
            this.handler=handler;
            isStop=false;
            receiveThread=new ReceiveThread();
            receiveThread.start();
        }
    }

    public void startReceive()
    {

        if(receiveThread==null)
        {
            if(DEBUG)Log.i(Tag,"startReceive");
            isStop=false;
            receiveThread=new ReceiveThread();
            receiveThread.start();
        }
    }

    //结束监控
    public void  stoptReceive()
    {

         if (receiveThread!=null)
         {
             if(DEBUG)Log.i(Tag,"stoptReceive");
             isStop=true;
             receiveThread.wakeThread();
             receiveThread=null;
         }
    }
    private class ReceiveThread extends Thread {
        Object lock=new Object();
        int waitTime=400;
            Message msg;
            @Override
            public void run() {
                do {
                    try {
//                        if (DEBUG) Log.i(Tag, "ReceiveThread");
                        Log.e("OBDAPI", "run: receiveThread");
                        String data = receive().trim();
                        if (data.length() > 0) {
                            String[] str = data.split("\r\n");
                            for (int k = 0; k < str.length; k++) {
                                proccData(str[k]);
                            }
                        }else{
//                            ObdRunningData.setSpeed(0);//
//                            ObdRunningData.setTotalMiles(0);//
//                            ObdRunningData.setEngineRpm(0);//
//                            ObdRunningData.setCurMiles(0);//
//                            ObdRunningData.setCurUsedFuel(0);
//                            ObdRunningData.setCoolantTemperature(0);
                        }
                        synchronized (lock) {
                            try {
                                lock.wait(waitTime);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } while (!isStop);
            }

        public void wakeThread() {
                synchronized (lock) {
                    try{
                        lock.notify();
                    }catch (Exception ex)
                    {}
                }
            }
        }


    int speed = 0;
    private  void proccData(String data) {
//        if (DEBUG) Log.i(Tag, "proccData:" + data);

//        if (speed > 325) {
//            speed = 0;
//        } else {
//            speed++;
//        }
//        data = String.format("$OBD-RT,13.5,1426,%d,0.00,42.6,82,5.68,8.02,10.42,80.65,0.75,6.15,2,1,3\r\n", speed); ;
        if (data.contains("$OBD-RT")) {
            /*
           [0]: $OBD-RT  [1]:电瓶电压 [2]:发动机转速   rpm
           [3]:行驶车速     Km/h  [4]:节气门开度   %  [5]:发动机负荷   %
           [6]:冷却液温度   ℃ [7]:瞬时油耗 （怠速）：L/h	（行驶）：L/100km     通过车速判断当前是怠速状态或行驶状态
           [8]:平均油耗		L/100km  [9]:本次行驶里程		km [10]:总里程		km  [11]:本次耗油量		L
           [12]:累计耗油量		L  [13]:当前故障码数量  [14]:本次急加速次数		Times  [15]:本次急减速次数		Times
           开机运行
           $EST527,V7.5.5 System running...
           $EST527,Wake up from RESET.
           $EST527,Connecting to ECU 001 times.
           接收（怠速状态）
           >$OBD-RT,13.5,851,0,0.00,42.6,60,1.33,0.00,10.42,80.65,0.75,6.15,2,1,3\r\n
            接收（行驶状态）
            >$OBD-RT,13.5,1426,38,0.00,42.6,82,5.68,8.02,10.42,80.65,0.75,6.15,2,1,3\r\n
            */
            String[] str = data.split(",");
            if (str.length < 10)
            {
                return;
            }
            byte speed;
            int state;

            for(int i = 0; i < str.length; i ++)
                Log.e("obdapi", "proccData: str[i]:" + str[i] );

            ObdRunningData.setSpeed(Float.parseFloat(str[3]));//
            ObdRunningData.setTotalMiles(Float.parseFloat(str[10]));//
            ObdRunningData.setEngineRpm(Integer.parseInt(str[2]));//
            ObdRunningData.setCurMiles(Float.parseFloat(str[9]));//
            ObdRunningData.setCurUsedFuel(Float.parseFloat(str[11]));
            ObdRunningData.setCoolantTemperature(Float.parseFloat(str[6]));
//            speed = (byte) ((Integer.getInteger(str[3], 0)) & 0xFF);
            //CommonInfo.setObdSpeed(speed);
//            state = 0;
            //Log.d("OBD","发动机转速："+Integer.parseInt(str[2])+",行驶速度："+Float.parseFloat(str[3])+",里程："+Float.parseFloat(str[10]));
//                刹车、门边线、左转、右转、远光灯、ACC、喇叭、备用
            //刹车、门边线、左转、右转、远光灯、
//            if (Integer.getInteger(str[2], 0) > 0) {
//                state |= 0x20;
//            }
//            CommonInfo.setObdState((byte) (state & 0xFF));
            Message msg = new Message();
            msg.what = MessageDefine.REFRESH_OBD_DATA;
            msg.arg1 = 1;
            msg.obj = str;
            handler.sendMessage(msg);
        }
        else if(data.contains("$EST527"))//开机启动数据
        {
            String[] str = new String[1];
            Message msg = new Message();
            msg.what = MessageDefine.REFRESH_OBD_DATA;
            msg.arg1 = 0;
            msg.obj = data.split("\r\n");
            handler.sendMessage(msg);
        }
        else if (data.contains("$300"))//里程 no $
        {
            Message msg = new Message();
            msg.what = MessageDefine.REFRESH_OBD_DATA;
            msg.arg1 = 0;
            msg.obj = data.split("\r\n");
            handler.sendMessage(msg);
        }
    }

    public final String arrayToString(byte[] bytes) {
        StringBuffer buff = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            buff.append(bytes[i] + " ");
        }
        return buff.toString();
    }
}
