package com.serenegiant.net;

import android.support.annotation.Nullable;
import android.util.Log;

import com.serenegiant.AppConfig;
import com.serenegiant.entiy.GPSInfo;
import com.serenegiant.entiy.ObdDataModel;
import com.serenegiant.entiy.WarningFlag;
import com.serenegiant.ui.RainingActivity;
import com.serenegiant.utils.IUtil;
import com.serenegiant.utils.MessageDefine;
import com.serenegiant.utils.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * Created by Hambobo on 2016-06-21.
 */
public class CommonInfo {
    public static String TAG = "CommonInfo";

    static public String getGuid() {
        return guid;
    }

    static public void setGuid(String guid) {
        CommonInfo.guid = guid;
    }

    private static  String guid = null;

    public static int trainMode; //教学模式

    public static byte[] getOrderListNumber() {
        return orderListNumber;
    }

    public static void setOrderListNumber(byte[] orderListNumber) {
        CommonInfo.orderListNumber = orderListNumber;
    }

    public static String getTelnetDeviceNumber() {
        return telnetDeviceNumber;
    }

    public static void setTelnetDeviceNumber(String telnetDeviceNumber) {
        CommonInfo.telnetDeviceNumber = telnetDeviceNumber;
    }

    public static String getCertificationPassword() {
        return certificationPassword;
    }

    public static void setCertificationPassword(String certificationPassword) {
        CommonInfo.certificationPassword = certificationPassword;
    }

    private static String telnetDeviceNumber = null;
    private static String certificationPassword = null;


    private static byte[] orderListNumber; //订单编号
    private static byte[] coachLoginNumber; //教练登录编号
    private static byte[] stuLoginNumber; //学员登录编号
    private static byte[] trainListNumber; //培训记录编号
    private static byte[] pictureNumber; //拍照编号
    private static byte[] stuLoginData; //学员登陆数据

    private static int coachSerial; //教练登陆流水号
    private static int stuSerial;   //学员登录流水号
    private static int trainListSerial;//培训记录流水号
    private static int pictureSerial; //照片流水号
    private static byte[] coachLoginData; //教练员登陆数据

    public static long getClassID() {
        return classID;
    }

    public static void setClassID(long classID) {
        CommonInfo.classID = classID;
    }

    private static long classID;//课堂ID

    public static String getCourseCode() {
        return courseCode;
    }

    public static void setCourseCode(String courseCode) {
        CommonInfo.courseCode = courseCode;
    }

    public static String courseCode;




    public static byte curItem = 2;

    private static byte[] coachNumber;
    private static byte[] coachCardNumber;
    private static byte[] coachDrivingNumber;
    private static byte[] coachIdentifyNumber;
    private  static  String coachName="";//教练姓名

    private static byte[] stuNumber; //学员编号
    private static byte[] stuCardNumber;//学员卡号
    private static byte[] stuDrivingNumber; //驾校编号
    private static byte[] stuIdentifyNumnber;
    private static String stuName;//学员姓名

    public static String getStuCardUid() {
        return stuCardUid;
    }

    public static void setStuCardUid(String stuCardUid) {
        CommonInfo.stuCardUid = stuCardUid;
    }

    public static String getCoachCardUid() {
        return coachCardUid;
    }

    public static void setCoachCardUid(String coachCardUid) {
        CommonInfo.coachCardUid = coachCardUid;
    }
    public static String getStuPreCardUid() {
        return stuPreCardUid;
    }

    public static void setStuPreCardUid(String stuPreCardUid) {
        CommonInfo.stuPreCardUid = stuPreCardUid;
    }

    public static int getDeviceLoginState() {
        return deviceLoginState;
    }

    public static void setDeviceLoginState(int deviceLoginState) {
        CommonInfo.deviceLoginState = deviceLoginState;
    }

    public static int deviceLoginState;
    public static String stuCardUid;
    public static String coachCardUid;
    public static String stuPreCardUid;  //上次刷卡ID

    public  static  int trainingMode=1;//1：表示传统模式   2：表示计时模式
    public  static  boolean isVerification=false; // 教练员界面会用到
    public  static  int totalTime=0;
    public  static  String course="";//培训科目

    public static int getTrainMode() {
        return trainMode;
    }

    public static void setTrainMode(int trainMode) {
        CommonInfo.trainMode = trainMode;
    }
    public static byte getCurItem() {
        return curItem;
    }

    public static void setCurItem(byte curItem) {
        CommonInfo.curItem = curItem;
    }

    /*
       获取教练登录编号
    教练登录编号6字节， 4字节BCD时间YYMMDDHH加上两字节流水号（0~65535）
    fresh：false、重复获取教练员登录编号  true、获取教练新登陆编号
    */
    public static byte[] getCoachLoginNumber(boolean fresh) {
        if (fresh) {
            coachLoginNumber = DeviceParameter.getDateTime();
            coachSerial = ((coachLoginNumber[4]&0xFF)<<8) + (coachLoginNumber[5]&0xFF);
            if (((++coachSerial)&0xFFFF) == 0)
            {
                coachSerial++;
            }
            coachLoginNumber[4] = (byte) (coachSerial >> 8);
            coachLoginNumber[5] = (byte) coachSerial;
            return coachLoginNumber;
        }
        else
        {
            return coachLoginNumber;
        }
    }
    /*
       获取学员登录编号
       学员登录编号8字节， 教练员登录编号加上两字节流水号（0~65535）
       fresh：false、重复获取教练员登录编号  true、获取教练新登陆编号
    */
    public static byte[] getStuLoginNumber(boolean fresh) {
        if(trainMode == 1)//订单模式
        {
            return CommonInfo.orderListNumber;
        }
        else {
            if (fresh) {
                stuLoginNumber = new byte[8];
                System.arraycopy(coachLoginNumber, 0, stuLoginNumber, 0, coachLoginNumber.length);
                if (((++stuSerial) & 0xFFFF) == 0) {
                    stuSerial++;
                }
                stuLoginNumber[6] = (byte) (stuSerial >> 8);
                stuLoginNumber[7] = (byte) stuSerial;
                return stuLoginNumber;
            } else {
                return stuLoginNumber;
            }
        }
    }

    /*
      获取培训记录编号
      总共10字节， 学员登录编号（8字节）+ 培训记录流水号（2字节）
      fresh：false、重复获取培训记录编号  true、获取新培训记录编号
   */
    public static byte[] getTrainListNumber(boolean fresh)
    {
        if (fresh)
        {
            trainListNumber = new byte[10];
            byte[] logNumber = getStuLoginNumber(false);
            System.arraycopy(logNumber, 0, trainListNumber, 0, logNumber.length);
            if (((++trainListSerial)&0xFFFF) == 0)
            {
                trainListSerial++;
            }
            trainListNumber[8] = (byte) (trainListSerial >> 8);
            trainListNumber[9] = (byte) trainListSerial;
            return trainListNumber;
        }
        else
        {
            return trainListNumber;
        }
    }

    public static void setPictureSerial(int pictureSerial) {
        CommonInfo.pictureSerial = pictureSerial;
    }

    /*
         获取拍照记录编号
         总共10字节， 学员登录编号（8字节）+ 拍照流水号（2字节）
         fresh：false、重复获取培训记录编号  true、获取新培训记录编号
      */
    public static byte[] getPictureNumber(boolean fresh)
    {
        if (fresh || pictureNumber == null)
        {
            String s = String.format("%010d", System.currentTimeMillis() / 1000);
            Log.e(TAG, "getPictureNumber: " + s);
            pictureNumber = s.getBytes();
            return pictureNumber;
        }
        else
        {
            return pictureNumber;
        }
    }

    public static long gpsWarningFlag;

    public static final long GPS_WARNING_TUCH_SWITCH = 0x00000001;
    public static final long GPS_WARNING_OVER_SPEED = 0x00000002;
    public static final long GPS_WARNING_CAMERA = 0x00000800;
    public static final long GPS_WARNING_OVER_ZONE = 0x00100000;

    public static long getGpsWarningFlag()
    {
        return gpsWarningFlag;
    }
    public static void LhsetGpsWarningFlag(long flag)
    {
        gpsWarningFlag = flag;
    }
    public static void setGpsWarningFlag(long flag)
    {
        gpsWarningFlag = flag;
    }

    public static void clearGpsWarningFlag(long flag)
    {
        gpsWarningFlag &= ~flag;
    }

    public static byte[] getGpsData() {
        byte[] gpsData = new byte[28];
        byte[] temp;
        long warmingFlag;//报警信息
        long state;//状态信息
        long latitude;//维度乘以10的6次方
        long longtitude;//精度，运算同上
        int altitude;//海拔高度，单位为米
        int speed;//GPS速度信息 单位：1/10km/h
        int obdspeed;//obd速度
        int orientition;//方向0~359
        byte[] time; //BCD编码yymmddhhmmss

//        warmingFlag = getGpsWarningFlag();
//        warmingFlag = WarningFlag.getWarningFlag();
        state = 0;
        state=setBit(state,0);
        state=setBit(state,1);
        if (MessageDefine.isNetwork)
        {
            state=clrBit(state,6);
        }else {
            state=setBit(state, 6);
        }
        state=setBit(state,12);
        if(MessageDefine.isGPS)
        {
            time = getDateTime();
        }else{
            time = DeviceParameter.getDateTime();
        }
        latitude = (long) (GPSInfo.getLatitude() * 1000000);
        longtitude =(long)(GPSInfo.getLongitude() * 1000000);
        altitude = (int)GPSInfo.getAltitude();
        /*
        * 速度是以 1 /10 km/h 传输
        * */
        // TODO: 2019-05-27 OBD
//        speed = ObdDataModel.getSpeed() * 10;
//        obdspeed= ObdDataModel.getSpeed() * 10;

        // TODO: 2019-05-27 GPS
        speed = (int) (GPSInfo.getSpeed() * 10);
        obdspeed = speed;
//        Log.d("TCP","obd速度："+obdspeed);
        orientition = GPSInfo.getAngle();
        if (altitude <= 0)//屏蔽负数
        {
            altitude = 0;
        }
//        temp = StringUtils.longToBytes(warmingFlag);
        if (IUtil.OnClass)
            System.arraycopy(WarningFlag.getWarningFlag(), 0, gpsData, 0, 4);
        else
            System.arraycopy(WarningFlag.getNoWarningFlag(), 0, gpsData, 0, 4);
        temp = StringUtils.longToBytes(state);
        System.arraycopy(temp, 0, gpsData, 4, 4);
        temp = StringUtils.longToBytes(latitude);
        System.arraycopy(temp, 0, gpsData, 8, 4);
        temp = StringUtils.longToBytes(longtitude);
        System.arraycopy(temp, 0, gpsData, 12, 4);
        //temp = StringUtils.intToBytes(altitude);
        temp = StringUtils.intToBytes(obdspeed); //行驶速度
        //temp = StringUtils.intToBytes(160);
        System.arraycopy(temp, 0, gpsData, 16, 2);
        temp = StringUtils.intToBytes(speed); //卫星定位速度
        System.arraycopy(temp, 0, gpsData, 18, 2);
        temp = StringUtils.intToBytes(orientition);
        System.arraycopy(temp, 0, gpsData, 20, 2);
        System.arraycopy(time, 0, gpsData, 22, 6);
//        Log.d("Tcp:GNSS  ",StringUtils.bytesToHexString(gpsData));
        return gpsData;
    }
    public static byte[] getGpsAddition() {
        int index = 0;
        byte[] gpsData = new byte[18];
        //附加信息
        gpsData[index++] = 0x01;//车上里程表的读数
        gpsData[index++] = 0x04;
        int miles = (int)(ObdDataModel.getTotalMiles()*10);
        //int miles = 0;
//        Log.d("Tcp","里程："+miles);

        gpsData[index++] = (byte) (miles>>24);
        gpsData[index++] = (byte) (miles>>16);
        gpsData[index++] = (byte) (miles>>8);
        gpsData[index++] = (byte) miles;
        gpsData[index++] = 0x02;//油量，WORD，1/10L，对应车上油量表读书
        gpsData[index++] = 0x02;
        int usedFuel1 = (int)ObdRunningData.getCurUsedFuel();
        int usedFuel=ObdDataModel.getTotalFuel();
//        Log.d("Tcp","油耗："+usedFuel1);
        gpsData[index++] = (byte) (usedFuel>>8);//油量，WORD，1/10L，对应车上油量表读书
        gpsData[index++] = (byte)usedFuel;
        gpsData[index++] = 0x03;//海拔高度，单位为m
        gpsData[index++] = 0x02;
        int altitude = (int)GPSInfo.getAltitude();
        gpsData[index++] = (byte) (altitude>>8);
        gpsData[index++] = (byte)altitude;
        gpsData[index++] = 0x05;//发动机转速，WORD
        gpsData[index++] = 0x02;
//        int rmp = ObdDataModel.getRotatingSpeed();
        // TODO: 2019-07-15 GPS
        int rmp = 1500;
//        Log.d("TCP","发动机转速:"+rmp);
        gpsData[index++] = (byte) (rmp>>8);
        gpsData[index++] = (byte)rmp;
        return gpsData;
    }

    public static byte[] getGpsPackageTrainRecord(){
        byte[] gpsData = new byte[38];
        byte[] temp;

        temp = getGpsData();
        System.arraycopy(temp,0,gpsData,0,28);

        gpsData[28] = 0x01;//车上里程表的读数
        gpsData[29] = 0x04;
        long miles = (long)(ObdDataModel.getTotalMiles() / 100);
        gpsData[30] = (byte) (miles>>24);
        gpsData[31] = (byte) (miles>>16);
        gpsData[32] = (byte) (miles>>8);
        gpsData[33] = (byte) miles;

        gpsData[34] = 0x05;//发动机转速，WORD
        gpsData[35] = 0x02;
//        int rmp = ObdDataModel.getRotatingSpeed();
        // TODO: 2019-07-15 GPS
        int rmp = 1500;
        gpsData[36] = (byte) (rmp>>8);
        gpsData[37] = (byte)rmp;

        return gpsData;
    }

    public static byte[] getGpsPackage() {
        byte[] gpsData = new byte[46];
        byte[] temp;

        temp = getGpsData();
        System.arraycopy(temp, 0, gpsData, 0, 28);
        //附加信息
        gpsData[28] = 0x01;//车上里程表的读数
        gpsData[29] = 0x04;
        long miles = (long)(ObdDataModel.getTotalMiles() / 100);
        gpsData[30] = (byte) (miles>>24);
        gpsData[31] = (byte) (miles>>16);
        gpsData[32] = (byte) (miles>>8);
        gpsData[33] = (byte) miles;
        gpsData[34] = 0x02;//油量，WORD，1/10L，对应车上油量表读书
        gpsData[35] = 0x02;
//        int usedFuel = (int)ObdRunningData.getCurUsedFuel();
        int usedFuel = (int)ObdRunningData.getCurUsedFuel() - RainingActivity.currentFuel;
        gpsData[36] = (byte) (usedFuel>>8);//油量，WORD，1/10L，对应车上油量表读书
        gpsData[37] = (byte)usedFuel;
        gpsData[38] = 0x03;//海拔高度，单位为m
        gpsData[39] = 0x02;
        int altitude = (int)GPSInfo.getAltitude();
        gpsData[40] = (byte) (altitude>>8);
        gpsData[41] = (byte)altitude;
        gpsData[42] = 0x05;//发动机转速，WORD
        gpsData[43] = 0x02;
        int rmp = ObdDataModel.getRotatingSpeed();
        gpsData[44] = (byte) (rmp>>8);
        gpsData[45] = (byte)rmp;
        return gpsData;
    }

    public static byte[] getStuLoginData() {
        return stuLoginData;
    }

    public static void setStuLoginData(byte[] stuLoginData) {
        CommonInfo.stuLoginData = stuLoginData;
    }

    public static byte[] getCoachLoginData() {
        return coachLoginData;
    }

    public static void setCoachLoginData(byte[] coachLoginData) {
        CommonInfo.coachLoginData = coachLoginData;
    }

    public static String getStuName() {
        return stuName;
    }
    public static void setStuName(String stuName) {
        CommonInfo.stuName = stuName;
    }

    public static byte[] getCoachNumber()//教练员编号
    {
        return coachNumber;
    }
    public static void setCoachNumber(byte[] coachNumber) {
        CommonInfo.coachNumber = coachNumber;
    }
    public static byte[] getCoachCardNumber()//教练员IC卡物理卡号
    {
        return coachCardNumber;
    }
    public static void setCoachCardNumber(byte[] card)//教练员IC卡物理卡号
    {
          coachCardNumber=card;
    }
    public static byte[] getCoachDrivingNumber()//教练员驾校编号
    {
        return coachDrivingNumber;
    }
    public static void setCoachDrivingNumber(byte[] coachDrivingNumber) {
        CommonInfo.coachDrivingNumber = coachDrivingNumber;
    }

    public static byte[] getCoachIdentifyNumber()//教练身份证号
    {
        return coachIdentifyNumber;
    }
    public static void setCoachIdentifyNumber(byte[] coachIdentifyNumber) {
        CommonInfo.coachIdentifyNumber = coachIdentifyNumber;
    }

    private static byte obdSpeed;
    private static byte obdState;

    public static void setObdSpeed(byte speed)
    {
        obdSpeed = speed;
    }
    public static byte getObdSpeed()
    {
        return obdSpeed;
    }
    public static void setObdState(byte state)
    {
        obdState = state;
    }
    public static byte getObdState()
    {
        return obdState;
    }

    public static String getCoachName() {//教练姓名
        return coachName;
    }
    public static void setCoachName(String coachName) {
        CommonInfo.coachName = coachName;
    }

    public static byte[] getStuNumber()//学员编号
    {
        return stuNumber;
    }
    public static void setStuNumber(byte[] stuNumber) {
        CommonInfo.stuNumber = stuNumber;
    }
    public static byte[] getStuCardNumber()//学员IC卡物理卡号
    {
        return stuCardNumber;
    }
    public static void setStuCardNumber(byte[] stuCardNumber) {//学员卡号
        CommonInfo.stuCardNumber = stuCardNumber;
    }
    public static byte[] getStuDrivingNumber()//驾校编号
    {
        return stuDrivingNumber;
    }
    public static void setStuDrivingNumber(byte[] stuDrivingNumber) {
        CommonInfo.stuDrivingNumber = stuDrivingNumber;
    }

    public static byte[] getStuIdentifyNumber()//学员身份证编号
    {
        return stuIdentifyNumnber;
    }
    public static void setStuIdentifyNumnber(byte[] stuIdentifyNumnber) {
        CommonInfo.stuIdentifyNumnber = stuIdentifyNumnber;
    }
    public static byte[] getTrainAreaNumber()//获取培训场地编号
    {
        return new byte[6];
    }


    private static boolean validAckSigned;

    public static boolean isValidAckSigned() {
        return validAckSigned;
    }

    public static void setValidAckSignout(boolean validAckSignout) {
        CommonInfo.validAckSigned = validAckSignout;
    }

    public static int getCurItemOutline() {
        return curItemOutline;
    }

    public static void setCurItemOutline(int curItemOutline) {
        CommonInfo.curItemOutline = curItemOutline;
    }

    public static int getCurItemLearned() {
        return curItemLearned;
    }

    public static void setCurItemLearned(int curItemLearned) {
        CommonInfo.curItemLearned = curItemLearned;
    }

    public static int getCurStuValid() {
        return curStuValid;
    }

    public static void setCurStuValid(int curStuValid) {
        CommonInfo.curStuValid = curStuValid;
    }

    private static int curStuValid;
    private static int curItemOutline;
    private static int curItemLearned;

    public static int getCurMilesOutline() {
        return curMilesOutline;
    }

    public static void setCurMilesOutline(int curMilesOutline) {
        CommonInfo.curMilesOutline = curMilesOutline;
    }

    public static int getCurMilesLearned() {
        return curMilesLearned;
    }

    public static void setCurMilesLearned(int curMilesLearned) {
        CommonInfo.curMilesLearned = curMilesLearned;
    }

    private static int curMilesOutline;
    private static int curMilesLearned;

    private static byte[] getCoachPackage(boolean flag)
    {
        byte[] pcData = new byte[86];
        byte[] tempBytes;
        int index = 0;

        tempBytes = getCoachLoginNumber(flag);//教练登录编号
        System.arraycopy(tempBytes, 0, pcData, index, tempBytes.length);
        index += tempBytes.length;
        tempBytes = getCoachCardNumber();//IC卡物理卡号
        System.arraycopy(tempBytes, 0, pcData, index, tempBytes.length);
        index += tempBytes.length;
        tempBytes = getCoachNumber();//教练编号
        System.arraycopy(tempBytes, 0, pcData, index, tempBytes.length);
        index += tempBytes.length;
        tempBytes = getCoachIdentifyNumber();//教练身份证号
        System.arraycopy(tempBytes, 0, pcData, index, tempBytes.length);
        index += tempBytes.length;
        tempBytes = getCoachDrivingNumber();//教练驾校编号
        System.arraycopy(tempBytes, 0, pcData, index, tempBytes.length);
        index += tempBytes.length;
        tempBytes = getGpsPackage();//完整的GPS数据包
        System.arraycopy(tempBytes, 0, pcData, index, tempBytes.length);
        index += tempBytes.length;
        if (index != 86) {
            Log.i(TAG, "教练数据包长度有误");
            return null;
        }
        else {
            return pcData;
        }
    }
    public static byte[] getCoachLoginPackage()
    {
        return getCoachPackage(true);
    }
    public static byte[] getCoachLogoutPackage()
    {
        return getCoachPackage(false);
    }
    @Nullable
    private static byte[] getStudentPackage(boolean flag)
    {
        byte[] pcData = new byte[97];
        byte[] tempBytes;
        int index = 0;

        tempBytes = getStuLoginNumber(flag);//学员登录编号
        System.arraycopy(tempBytes, 0, pcData, index, tempBytes.length);
        index += tempBytes.length;
        tempBytes = getStuCardNumber();//学员IC卡物理卡号
        System.arraycopy(tempBytes, 0, pcData, index, tempBytes.length);
        index += tempBytes.length;
        tempBytes = getStuNumber();//学员编号
        System.arraycopy(tempBytes, 0, pcData, index, tempBytes.length);
        index += tempBytes.length;
        tempBytes = getStuIdentifyNumber();//学员身份证编号
        System.arraycopy(tempBytes, 0, pcData, index, tempBytes.length);
        index += tempBytes.length;
        pcData[index++] = getCurItem();           // 学习科目
        tempBytes = getCoachNumber();//当前教练员编号
        System.arraycopy(tempBytes, 0, pcData, index, tempBytes.length);
        index += tempBytes.length;
        tempBytes = DeviceParameter.getDrivingNo().getBytes();//学员驾校编号
        System.arraycopy(tempBytes, 0, pcData, index, tempBytes.length);
        index += tempBytes.length;
        tempBytes = getGpsPackage();//完整GPS包
        System.arraycopy(tempBytes, 0, pcData, index, tempBytes.length);
        index += tempBytes.length;
        if (index != 97) {
            Log.i(TAG, "学员数据包消息长度有误");
            return null;
        }
        else
        {
            return pcData;
        }
    }

    public static byte[] getStuLoginPackage()
    {
        return getStudentPackage(true);
    }

    public static byte[] getStuLogoutPackage()
    {
        return getStudentPackage(false);
    }

    @Nullable
    public static byte[] getTrainListPackage()
    {
        byte[] pcData = new byte[97];
        byte[] tempBytes;
        int index = 0;

        pcData[index++] = 0x01;
        tempBytes = getTrainListNumber(false);
        System.arraycopy(tempBytes, 0, pcData, index, tempBytes.length);
        index += tempBytes.length;
        tempBytes = DeviceParameter.getDateTime();
        System.arraycopy(tempBytes, 0, pcData, index, tempBytes.length);
        index += tempBytes.length;
        tempBytes = getStuNumber();
        System.arraycopy(tempBytes, 0, pcData, index, tempBytes.length);
        index += tempBytes.length;
        tempBytes = getCoachNumber();
        System.arraycopy(tempBytes, 0, pcData, index, tempBytes.length);
        index += tempBytes.length;
        pcData[index++] = getCurItem();
        pcData[index++] = 0x00;
        long latitude = (long) (GPSInfo.getLatitude() * 1000000);
        long longitude = (long)(GPSInfo.getLongitude() * 1000000);
        tempBytes = StringUtils.longToBytes(latitude);
        System.arraycopy(tempBytes, 0, pcData, index, tempBytes.length);
        index += tempBytes.length;
        tempBytes = StringUtils.longToBytes(longitude);
        System.arraycopy(tempBytes, 0, pcData, index, tempBytes.length);
        index += tempBytes.length;
        tempBytes = getStuCardNumber();
        System.arraycopy(tempBytes, 0, pcData, index, tempBytes.length);
        index += tempBytes.length;
        tempBytes = getCoachCardNumber();
        System.arraycopy(tempBytes, 0, pcData, index, tempBytes.length);
        index += tempBytes.length;
        tempBytes = getTrainAreaNumber();
        System.arraycopy(tempBytes, 0, pcData, index, tempBytes.length);
        index += tempBytes.length;
        return pcData;
//        if (index != 97) {
//            Log.i(TAG, "上报学时记录消息长度有误");
//            return null;
//        }
//        else
//        {
//            return pcData;
//        }
    }

    private static byte startSecond;
    private static byte[] actionBytes = new byte[180];
    private static long actionRefreshTime;
    //记录行驶中的行为数据
    //second 当前存储行为数据的秒数0~59
    //refresh 刷新行为数据，在0秒或者首次调用时为true 其他为false
    public static void setDrivingActions(byte second, boolean refresh)
    {
        if (refresh)
        {
            startSecond = second;
            actionRefreshTime = System.currentTimeMillis();
        }
        else if (System.currentTimeMillis() - actionRefreshTime > 70000)//70秒钟之后自动清空
        {
            startSecond = second;
            actionRefreshTime = System.currentTimeMillis();
        }
        if (second < 0 || second > 59)
        {
            return;
        }
        actionBytes[second*3] = (byte)ObdRunningData.getSpeed();
        actionBytes[second*3+1] = (byte) GPSInfo.getSpeed();
        actionBytes[second*3+2] = getObdState();
    }

    //获取当前这一分钟的行为数据
    public static byte[] getDrivingActions()
    {
//        if (startSecond > 59 && startSecond < 0)
//        {
//            return null;
//        }
//        byte[] temp = new byte[(60-startSecond)*3];
        byte[] temp = new byte[180];
        for (int i = startSecond; i < 60; i++)
        {
            temp[i*3] = actionBytes[i*3];
            temp[i*3+1] = actionBytes[i*3+1];
            temp[i*3+2] = actionBytes[i*3+2];
        }
        return temp;
    }

    //获取驾驶行为记录数据包，在 setDrivingActions(59, false)；之后调用
    public static byte[] getDrivingBehavior()
    {

        byte[] tempBytes;
        int index = 0;

        tempBytes = getDrivingActions();
        if (tempBytes != null) {
            byte[] pcData = new byte[tempBytes.length + 18];
            System.arraycopy(tempBytes, 0, pcData, 18, tempBytes.length);
            pcData[0] = 1;//上报类型： 1自动上报  2被动上报
            tempBytes = getTrainListNumber(false);
            System.arraycopy(tempBytes, 0, pcData, 1, tempBytes.length);
            tempBytes = DeviceParameter.getDateTime();
            System.arraycopy(tempBytes, 0, pcData, 11, tempBytes.length);
            pcData[17] = getCurItem();
            return pcData;
        }
        else
        {
            return null;
        }

    }

    //mode 上传模式 1自动请求上传 FF停止拍摄并上传 81终端主动拍照上传
    //channel 拍摄通道 0：自动 1~255 表示通道号
    //size 图像尺寸
    //    SQCIF（160x120）	0
    //    QCIF（176x144）	1
    //    CIF（352x288）	2
    //    QQVGA（160x120）	3
    //    QVGA（320x240）	4
    //    VGA（640x480）	5
    //type 事件类型发起图片的事件类型
    //    0：中心查询的图片
    //    1：紧急报警主动上传的图片
    //    2：关车门后达到指定车速主动上传的图片
    //    3：侧翻报警主动上传的图片
    //    4：上客
    //    5：定时拍照
    //    6：进区域 7：出区域
    //    8：事故疑点(紧急刹车) 9：开车门
    //    17(0x11)--学员登录拍照
    //    18(0x12)--学员登出拍照
    //    19(0x13)--学员培训过程中拍照
    public static byte[] getPictureHeadInfo(PhotoHead ph,byte[] gpsdate)
    {
        byte[] pcData = new byte[256];
        byte[] tempByte;
        int index = 0;

        System.arraycopy(ph.photoSerial, 0, pcData, index, ph.photoSerial.length);//照片编号 10字节
        index += ph.photoSerial.length;

        System.arraycopy(ph.stuNumber, 0, pcData, index, ph.stuNumber.length);
        index += ph.stuNumber.length;
        pcData[index++] = ph.upMode;//1、主动上传    0x81、平台指定上传  0xff、停止上传
        pcData[index++] = ph.channel;//拍摄通道  0、自动
        pcData[index++] = ph.size;//图像尺寸   1、240*320  2、480*640
        pcData[index++] = ph.eventType;//事件类型

        pcData[index++] = (byte)(ph.totalPackages>>8);//
        pcData[index++] = (byte)(ph.totalPackages);//总包数
        pcData[index++] = (byte)(ph.fileSize>>24);
        pcData[index++] = (byte)(ph.fileSize>>16);
        pcData[index++] = (byte)(ph.fileSize>>8);
        pcData[index++] = (byte)(ph.fileSize);//照片数据大小

        pcData[index++] = (byte)(ph.classID>>24);
        pcData[index++] = (byte)(ph.classID>>16);
        pcData[index++] = (byte)(ph.classID>>8);
        pcData[index++] = (byte)(ph.classID);//
        tempByte = gpsdate;
        //tempByte = getGpsPackage();
        System.arraycopy(tempByte, 0, pcData, index, tempByte.length);
        index += tempByte.length;

        byte[] retBytes = new byte[index];
        System.arraycopy(pcData, 0, retBytes, 0, retBytes.length);
        return retBytes;
    }

    //mode 上传模式 1自动请求上传 FF停止拍摄并上传 81终端主动拍照上传
    //channel 拍摄通道 0：自动 1~255 表示通道号
    //size 图像尺寸
    //    SQCIF（160x120）	0
    //    QCIF（176x144）	1
    //    CIF（352x288）	2
    //    QQVGA（160x120）	3
    //    QVGA（320x240）	4
    //    VGA（640x480）	5
    //type 事件类型发起图片的事件类型
    //    0：中心查询的图片
    //    1：紧急报警主动上传的图片
    //    2：关车门后达到指定车速主动上传的图片
    //    3：侧翻报警主动上传的图片
    //    4：上客
    //    5：定时拍照
    //    6：进区域 7：出区域
    //    8：事故疑点(紧急刹车) 9：开车门
    //    17(0x11)--学员登录拍照
    //    18(0x12)--学员登出拍照
    //    19(0x13)--学员培训过程中拍照
    public static byte[] getVideoHeadInfo(VideoHead ph,byte[] gpsdate)
    {
        byte[] pcData = new byte[100];
        byte[] tempByte;
        int index = 0;

        pcData[index++] = ph.upMode;
        pcData[index++] = ph.channel;//拍摄通道
        pcData[index++] = ph.size;//图像尺寸
        pcData[index++] = ph.eventType;//事件类型
        pcData[index++] = (byte)(ph.totalPackages>>8);//
        pcData[index++] = (byte)(ph.totalPackages);//总包数
        pcData[index++] = (byte)(ph.fileSize>>24);
        pcData[index++] = (byte)(ph.fileSize>>16);
        pcData[index++] = (byte)(ph.fileSize>>8);
        pcData[index++] = (byte)(ph.fileSize);//照片数据大小

        System.arraycopy(ph.stuDrivingNo, 0, pcData, index, ph.stuDrivingNo.length);
        index += ph.stuDrivingNo.length;

        System.arraycopy(ph.stuNumber, 0, pcData, index, ph.stuNumber.length);
        index += ph.stuNumber.length;

        System.arraycopy(ph.stuLoginSerial, 0, pcData, index, ph.stuLoginSerial.length);
        index += ph.stuLoginSerial.length;

        System.arraycopy(ph.photoSerial, 0, pcData, index, ph.photoSerial.length);
        index += ph.photoSerial.length;
        tempByte = gpsdate;
        //tempByte = getGpsPackage();
        System.arraycopy(tempByte, 0, pcData, index, tempByte.length);
        index += tempByte.length;
        return pcData;
    }

    public static byte[] getPicturePackage(byte flag, int packageNum, int packageLength, byte[] photoSerial, byte[] bytes)
    {
        byte[] pcData = new byte[10+bytes.length];

        System.arraycopy(photoSerial, 0, pcData, 0, photoSerial.length);
//        pcData[10] = flag;
//        pcData[11] = (byte)(packageNum>>8);
//        pcData[12] = (byte)packageNum;
//        pcData[13] = (byte)(packageLength>>8);
//        pcData[14] = (byte)packageLength;
        System.arraycopy(bytes, 0, pcData, 10, bytes.length);
        return pcData;
    }

    public static  void clearStuInfo()
    {
//        stuNumber=new byte[0];
//        stuCardNumber=new byte[0];
//        stuDrivingNumber=new byte[0];
//        stuIdentifyNumnber=new byte[0];
//        stuName="";
//        totalTime=0;
//        course="";//培训科目
    }

    public static final File getTrainingVideoFile() {
        String fileDir;
        String fileName;
        String sRet;
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");

        if (CommonInfo.getTrainMode() == 3) { //长考视频保存地址
            fileDir = AppConfig.VIDEO_SAVE_DIRECTORY + File.separator + "DEFAULT";
        } else {
            final String sNum = new String(CommonInfo.getStuNumber());
            final String sLoginNum = StringUtils.bytesToHexString(CommonInfo.getStuLoginNumber(false));
            if (sNum != null && sLoginNum != null) {
                fileDir = AppConfig.VIDEO_SAVE_DIRECTORY + File.separator + sNum + File.separator + sLoginNum;
            } else {
                fileDir = AppConfig.VIDEO_SAVE_DIRECTORY + File.separator + "DEFAULT";
            }
        }
        File dirFile = new File(fileDir);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        fileName = sdf.format(new Date(System.currentTimeMillis())) + ".mp4";
        final File retFile = new File(fileDir, fileName);
        return retFile;
    }

    //把第N位置为1 你0～31
   private static long setBit(long orgData, int n)
    {
        return (orgData |= (1<<n));
    };
    //清零第N位 0～31；
  private static long clrBit(long orgData, int n)
    {
        return (orgData &= ~(1<<n));
    };

    public static byte[] getDateTime() {
        byte[] dateTime = new byte[6];
        int[] val = new int[6];

        String GPSTime = GPSInfo.getGPSTime();
        val[0] = Integer.parseInt(GPSTime.substring(0,2));
        val[1] = Integer.parseInt(GPSTime.substring(2,4));
        val[2] = Integer.parseInt(GPSTime.substring(4,6));
        val[3] = Integer.parseInt(GPSTime.substring(6,8));
        val[4] = Integer.parseInt(GPSTime.substring(8,10));
        val[5] = Integer.parseInt(GPSTime.substring(10));
        for (int i = 0; i < 6; i++) {
            dateTime[i] = intToHex(val[i]);
        }
        return dateTime;
    }

    public static byte intToHex(int val) {
        return (byte) (((val / 10) << 4) + val % 10);
    }
}
