package com.serenegiant.net;

/**
 * Created by Administrator on 2016-06-29.
 */
public class PhotoHead {
    byte upMode;
    byte channel;
    byte size;
//    发起图片的事件类型，定义如下：
//            0：中心查询的图片；
//            1：紧急报警主动上传的图片；
//            2：关车门后达到指定车速主动上传的图片；
//            3：侧翻报警主动上传的图片；
//            4：上客；
//            5：定时拍照；
//            6：进区域；
//            7：出区域；
//            8：事故疑点(紧急刹车)；
//            9：开车门；
//            17：学员登录拍照；
//            18：学员登出拍照；
//            19：学员培训过程中拍照；
//            20：教练员登录拍照；
//            21：教练员登出拍照
    byte eventType;
    int totalPackages;
    long fileSize;
    byte[] stuNumber;
    byte[] photoSerial;
    String filePath;
    long classID;
    int isBlindArea;
}
