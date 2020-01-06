package com.serenegiant.business.fingerprint;

import android.content.Context;
import android.os.Handler;

import com.rscja.deviceapi.Fingerprint;
import com.rscja.deviceapi.exception.ConfigurationException;
import com.serenegiant.utils.MessageDefine;


/**
 * Created by zhoupin on 2016/6/2 0002.
 */
public class FingerprintAPI {
    private static Fingerprint mFingerprint;
    public  FingerprintAPI()
    {
        if(mFingerprint==null)
            try {
                mFingerprint=Fingerprint.getInstance();
            } catch (ConfigurationException e) {
                e.printStackTrace();
            }
    }

    public  boolean free()
    {
        try{
            if (mFingerprint != null) {
                return mFingerprint.free();
            }
        }catch (Exception ex){};

        return   false;
    }
    public  boolean init()
    {
        try{
            if (mFingerprint != null) {
//                boolean reg = mFingerprint.setReg(5, 1);
                return mFingerprint.init();
            }
        }catch (Exception ex){};
        return   false;
    }
   //获取指纹数据
    public FingerprintInfo getFingerprintInfo(Context c)
    {
        if (mFingerprint != null) {
            if (!mFingerprint.getImage()) {
                return null;
            }
            if (mFingerprint.genChar(Fingerprint.BufferEnum.B1)) {
                int[] result = null;
                int exeCount = 0;
                do {
                    exeCount++;
                    result = mFingerprint.search(Fingerprint.BufferEnum.B1, 0, 255);
                } while (result == null && exeCount < 3);

                if (result != null) {
                    FingerprintInfo fInfo=new FingerprintInfo();
                    fInfo.setPage(result[0]);
                    fInfo.setScore(result[1]);
                        // 显示指纹图片
                 //  if (mContext.mFingerprint.upImage(1, mContext.getFilesDir() + "/finger.bmp") != -1) {

                        if (mFingerprint.getImage()) {
                            if (mFingerprint.upImage(1, c.getFilesDir() + "/finger.bmp") != -1) {//FileClass.getFingerprintImgPath()
                                fInfo.setImg(c.getFilesDir() + "/finger.bmp");//(FileClass.getFingerprintImgPath());
                                return  fInfo;
                            }
                        }
                }
            }
        }
         return  null;
    }

    public String getFingerprintChar ()
    {
        if (mFingerprint != null) {
            if (!mFingerprint.getImage()) {
                return null;
            }
            if (mFingerprint.genChar(Fingerprint.BufferEnum.B1)) {
                return mFingerprint.upChar(Fingerprint.BufferEnum.B1);
            }
        }
        return  null;
    }

    private final static int MSG_COACH_FINGER_INVALID = 5;
    public FingerprintInfo getFingerprintInfo(Handler handler) {
        if (mFingerprint == null)
            return null;

        if (!mFingerprint.getImage())
            return null;

        if (!mFingerprint.genChar(Fingerprint.BufferEnum.B1)) {
            handler.sendEmptyMessage(MessageDefine.MSG_STUDENT_FINGER_INVALID);
            return null;
        }

        int[] result = null;
        FingerprintInfo fInfo = new FingerprintInfo();
        result = mFingerprint.search(Fingerprint.BufferEnum.B1, 0, 255);
        if (result != null) {
            fInfo.setPage(result[0]);
            fInfo.setScore(result[1]);
            return fInfo;
        }
        else {
            handler.sendEmptyMessage(MessageDefine.MSG_STUDENT_FINGER_INVALID);
            return null;
        }
    }


    public int matchFingerprint(Handler handler, String fp) {
        if (mFingerprint == null)
            return 0;

        if (!mFingerprint.getImage())
            return 0;

        if (!mFingerprint.genChar(Fingerprint.BufferEnum.B1)) {
            handler.sendEmptyMessage(MessageDefine.MSG_STUDENT_FINGER_INVALID);
            return 0;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(fp);
        if(fp.length() < 1024)
        {
            for(int i = 0; i < 1024-fp.length(); i++)
            {
                sb.append("0");
            }
        }
        if (!mFingerprint.downChar(Fingerprint.BufferEnum.B2, sb.toString())) {
            handler.sendEmptyMessage(MessageDefine.MSG_STUDENT_FINGER_INVALID);
            return 0;
        }
        int result;
        result = mFingerprint.match();
        return result;
    }
    public int matchFingerprint(Handler handler, String fp, String fp2) {
        if (mFingerprint == null)
            return 0;

        if (!mFingerprint.getImage())
            return 0;

        if (!mFingerprint.genChar(Fingerprint.BufferEnum.B1)  ) {
            handler.sendEmptyMessage(MessageDefine.MSG_STUDENT_FINGER_INVALID);
            return 0;
        }
        StringBuffer sb = new StringBuffer();
        StringBuffer sb2 = new StringBuffer();
        sb.append(fp);
        sb2.append(fp2);
        if(fp.length() < 1024)
        {
            for(int i = 0; i < 1024-fp.length(); i++)
            {
                sb.append("0");
            }
        }
        if(fp2.length() < 1024)
        {
            for(int i = 0; i < 1024-fp2.length(); i++)
            {
                sb2.append("0");
            }
        }
        if (!mFingerprint.downChar(Fingerprint.BufferEnum.B2, sb.toString())) {
                handler.sendEmptyMessage(MessageDefine.MSG_STUDENT_FINGER_INVALID);
            return 0;
        }
        int result;
        result = mFingerprint.match();
        if(result < 20 && fp2.length() >1){
            if (!mFingerprint.downChar(Fingerprint.BufferEnum.B2, sb2.toString())) {
                handler.sendEmptyMessage(MessageDefine.MSG_STUDENT_FINGER_INVALID);
                return 0;
            }
            result = mFingerprint.match();
        }else if(fp2.length() == 0 && result == -1){
            handler.sendEmptyMessage(MessageDefine.MSG_STUDENT_FINGER_INVALID);
        }

        return result;
    }
    public class FingerprintInfo {
        private  int page; //指纹id
        private  int score;//指纹得分
        private  String img;//指纹图片

        public void setPage(int page)
        {
            this.page=page;
        }
        public int getPage()
        {
            return page;
        }

        public void setScore(int score)
        {
            this.score=score;
        }
        public int getScore()
        {
            return score;
        }

        public void setImg(String img)
        {
            this.img=img;
        }
        public String getImg()
        {
            return img;
        }
    }

}
