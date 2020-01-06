package com.serenegiant.net;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by zxj on 2017/9/19.
 */

public class ConnectService extends Service{
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO 每隔3s，进行一次心跳链接
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("ConnectService", "心跳连接");
                TcpClient.getInstance(getApplicationContext()).connect();
            }
        }).start();
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        int time = 30 * 1000;//
        long triggerAtTime = SystemClock.elapsedRealtime() + time;
        Intent i = new Intent(this, AlarmReceiver.class);
        //获取一个能够执行广播的PendingIntent
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);//定时执行任务
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
