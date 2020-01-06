package com.serenegiant.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import com.iflytek.speech.SynthesizerPlayer;
import com.navigation.timerterminal.R;

import cn.yunzhisheng.tts.offline.basic.ITTSControl;
import cn.yunzhisheng.tts.offline.basic.TTSFactory;
import cn.yunzhisheng.tts.offline.basic.TTSPlayerListener;


/**
 * Created by zhoupin on 2016/6/3 0003.
 */
public class SoundManage {

//    public static void ttsPlaySound(Context c, String speech) {
//        if (AppContext.mEngine != null)
////            AppContext.mEngine.setSaveAudioFileName(Environment.getExternalStorageDirectory() + "/tts/"
////                    + System.currentTimeMillis() + ".wav");
//            AppContext.mEngine.speak(speech, "1024");
//    }

    //    public static void ttsPlaySound(Context c, String speech) {
//        if (AppContext.mEngine != null)
//            AppContext.mEngine.speak(speech, "1024");
//    }

    private static Context applicationContext;
    private static int successID = -1;
    private static int failureID = -1;
    private static SoundPool soundPool = null;
    //这是后台朗读，实例化一个SynthesizerPlayer
    private static final String APPID = "appid=519328ab";
    private static SynthesizerPlayer ttsPlayer = null;
    private static SpeechUtilOffline speechUtilOffline=null;

    public static void initOffline(Context context){
        applicationContext = context;
        if (speechUtilOffline == null)
            speechUtilOffline = new SpeechUtilOffline(context);
    }

    public static void PlaySound(Context c, SoundType type) {
        soundPool = getSoundPool();
        if (soundPool == null)
            return;

        int id = -1;

        if (type == SoundType.SUCCESS) {
            if (successID == -1) {
                successID = soundPool.load(c, R.raw.success, 1);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            id = successID;
        } else if (type == SoundType.FAILURE) {
            if (failureID == -1) {
                //第一个参数为id
                //第二个和第三个参数为左右声道的音量控制
                //第四个参数为优先级，由于只有这一个声音，因此优先级在这里并不重要
                //第五个参数为是否循环播放，0为不循环，-1为循环
                //最后一个参数为播放比率，从0.5到2，一般为1，表示正常播放。
                failureID = soundPool.load(c, R.raw.failue, 1);
            }
            id = failureID;
        }
        if (id != -1)
            soundPool.play(id, 1, 1, 0, 0, 1);

    }

    public static void ttsPlaySound(Context c, String speech) {
        // onLinePlayer(c,speech);//在线播放
        offLinePlayer(c,speech);//离线播放
    }

    private static SoundPool getSoundPool() {
        if (soundPool == null) {
            soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        }
        return soundPool;
    }

    public enum SoundType {
        FAILURE, SUCCESS
    }
    //在线播放
    private static void   onLinePlayer (Context c, String speech) {
        if (ttsPlayer == null) {
            //    AutomaticDetectionActivity c=new AutomaticDetectionActivity();
            ttsPlayer = SynthesizerPlayer.createSynthesizerPlayer(c, APPID);
            ttsPlayer.setVoiceName("vivixiaoyan");//在此设置语音播报的人选例如：vivixiaoyan、vivixiaomei、vivixiaoqi
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //设置语音朗读者，可以根据需要设置男女朗读，具体请看api文档和官方论坛
        if (ttsPlayer != null) {
            if (!speech.equals(""))
                ttsPlayer.playText(speech, "ent=vivi21,bft=5", null);
        }
    }

    //离线播放
    private static void offLinePlayer(Context c, String speech)
    {
        if(speechUtilOffline==null && applicationContext != null) {
            speechUtilOffline = new SpeechUtilOffline(applicationContext);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if(speechUtilOffline!=null)
        {
            if (!speech.equals(""))
                speechUtilOffline.play(speech);
        }
    }
    /**
     * 离线语音解析
     *
     * @author JPH
     * @date 2015-4-14 下午7:20:25
     */
    private static  class SpeechUtilOffline implements TTSPlayerListener {
        public static final String appKey = "_appKey_";
        public static final String secret = "_secret_";
        private ITTSControl mTTSPlayer;
        private Context context;

        public SpeechUtilOffline(Context context) {
            this.context = context;
            init();
        }

        /**
         * 初始化引擎
         *
         * @author JPH
         * @date 2015-4-14 下午7:32:58
         */
        private void init() {
            mTTSPlayer = TTSFactory.createTTSControl(context, appKey);// 初始化语音合成对象
            mTTSPlayer.setTTSListener(this);// 设置回调监听
            mTTSPlayer.setStreamType(AudioManager.STREAM_MUSIC);//设置音频流
            mTTSPlayer.setVoiceSpeed(2.5f);//设置播报语速,播报语速，数值范围 0.1~2.5 默认为 1.0
            mTTSPlayer.setVoicePitch(1.1f);//设置播报音高,调节音高，数值范围 0.9～1.1 默认为 1.0
            mTTSPlayer.init();// 初始化合成引擎
        }

        /**
         * 停止播放
         *
         * @author JPH
         * @date 2015-4-14 下午7:50:35
         */
        public void stop() {
            mTTSPlayer.stop();
        }

        /**
         * 播放
         *
         * @author JPH
         * @date 2015-4-14 下午7:29:24
         */
        public void play(String content) {
            mTTSPlayer.play(content);
        }

        /**
         * 释放资源
         *
         * @author JPH
         * @date 2015-4-14 下午7:27:56
         */
        public void release() {
            // 主动释放离线引擎
            mTTSPlayer.release();
        }

        @Override
        public void onPlayEnd() {
            // 播放完成回调
            Log.i("msg", "onPlayEnd");
        }

        @Override
        public void onPlayBegin() {
            // 开始播放回调
            Log.i("msg", "onPlayBegin");
        }

        @Override
        public void onInitFinish() {
            // 初始化成功回调
            Log.i("msg", "onInitFinish");
        }

        @Override
        public void onError(cn.yunzhisheng.tts.offline.common.USCError arg0) {
            // 语音合成错误回调
            Log.i("msg", "onError");
        }

        @Override
        public void onCancel() {
            // 取消播放回调
            Log.i("msg", "onCancel");
        }

        @Override
        public void onBuffer() {
            // 开始缓冲回调
            Log.i("msg", "onBuffer");

        }
    }
}
