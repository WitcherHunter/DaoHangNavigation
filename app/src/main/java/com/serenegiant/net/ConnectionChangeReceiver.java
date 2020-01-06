package com.serenegiant.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.serenegiant.utils.SoundManage;

/**
 * Created by Administrator on 2017/3/16.
 */



public class ConnectionChangeReceiver extends BroadcastReceiver {
    Handler refreshHand;
    private boolean isNetStop = false;
    @Override
    public void onReceive(final Context context, Intent intent) {
//        ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo mobNetInfo=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//        NetworkInfo  wifiNetInfo=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//
//        if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
//            //Toast.makeText(context, "网络不可以用",Toast.LENGTH_LONG).show();
//            //改变背景或者 处理网络的全局变量
//            Log.e("ConnectionChangeReceiver","网络断开");
//            MessageDefine.isNetwork=false;
//            MessageDefine.lxpxyc = System.currentTimeMillis();
////            MessageDefine.isBlindArea=1;
//            Log.d("TcpClient","网络不可用");
//            play(context,"网络异常",500);
//            isNetStop = true;
//            if (MessageDefine.isTcpStart)
//            {
//                TcpClient.getInstance(context).setHandlerMain(null);
//            }
//        }else {
//                Log.e("ConnectionChangeReceiver","网络连接成功");
//            if (isNetStop && !MessageDefine.isTcpStart)
//            {
//                TcpClient.getInstance(context).start();
//                context.startService(new Intent(context, SendPositionService.class));
//                MessageDefine.isTcpStart=true;
//            } else{
//                TcpClient.getInstance(context).setHandlerMain(refreshHand);
//            }
//
//            MessageDefine.isNetwork = true;
////            MessageDefine.isBlindArea=0;
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    play(context,"网络连接成功",500);
//                }
//            }, 1500);
//
//        }
    }

    long flagPlayTiem;

    private void play(Context context,String txt, long time) {
        if (System.currentTimeMillis() - flagPlayTiem >= time) {
            flagPlayTiem = System.currentTimeMillis();
            SoundManage.ttsPlaySound(context, txt);
        }
    }
}

