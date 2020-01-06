package com.serenegiant.net;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.util.Xml;

import com.serenegiant.utils.StringUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;

/*
 * Created by Hambobo on 2016-06-13.
 */
public class DeviceParameter {


    /**
     * 获取软件版本号
     * @param context
     * @return
     */
    public static int getVerCode(Context context) {
        int verCode = -1;
        PackageManager pm = context.getPackageManager();//context为当前Activity上下文
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(context.getPackageName(), 0);
            verCode =  pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verCode;
    }
    /**
     * 获取版本名称
     * @param context
     * @return
     */
    public static String getVerName(Context context) {

        PackageManager pm = context.getPackageManager();//context为当前Activity上下文
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(context.getPackageName(), 0);
            return  pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "null";
    }

    public static byte getTakePhotoInterval() {
        return takePhotoInterval;
    }

    public static void setTakePhotoInterval(byte takePhotoInterval) {
        DeviceParameter.takePhotoInterval = takePhotoInterval;
    }

    public static byte getUpPhotoMode() {
        return upPhotoMode;
    }

    public static void setUpPhotoMode(byte upPhotoMode) {
        DeviceParameter.upPhotoMode = upPhotoMode;
    }

    public static byte getIsBroadcastInfo() {
        return isBroadcastInfo;
    }

    public static void setIsBroadcastInfo(byte isBroadcastInfo) {
        DeviceParameter.isBroadcastInfo = isBroadcastInfo;
    }

    public static byte getAllowTrainOfffMin() {
        return allowTrainOfffMin;
    }

    public static void setAllowTrainOfffMin(byte allowTrainOfffMin) {
        DeviceParameter.allowTrainOfffMin = allowTrainOfffMin;
    }

    public static int getUpGpsIntervalOff() {
        return upGpsIntervalOff;
    }

    public static void setUpGpsIntervalOff(int upGpsIntervalOff) {
        DeviceParameter.upGpsIntervalOff = upGpsIntervalOff;
    }

    public static int getAllowCoachOffMin() {
        return allowCoachOffMin;
    }

    public static void setAllowCoachOffMin(int allowCoachOffMin) {
        DeviceParameter.allowCoachOffMin = allowCoachOffMin;
    }

    public static byte getAllowCoachCrossSchool() {
        return allowCoachCrossSchool;
    }

    public static void setAllowCoachCrossSchool(byte allowCoachCrossSchool) {
        DeviceParameter.allowCoachCrossSchool = allowCoachCrossSchool;
    }

    public static byte getAllowStuCrossSchool() {
        return allowStuCrossSchool;
    }

    public static void setAllowStuCrossSchool(byte allowStuCrossSchool) {
        DeviceParameter.allowStuCrossSchool = allowStuCrossSchool;
    }

//    public static byte getTakeVideoInterval() {
//        return takeVideoInterval;
//    }
//
//    public static void setTakeVideoInterval(byte takeVideoInterval) {
//        DeviceParameter.takeVideoInterval = takeVideoInterval;
//    }
//
//    public static byte getTakevideoDuration() {
//        return takevideoDuration;
//    }
//
//    public static void setTakevideoDuration(byte takevideoDuration) {
//        DeviceParameter.takevideoDuration = takevideoDuration;
//    }
//
//    public static byte getUpVideoMode() {
//        return upVideoMode;
//    }
//
//    public static void setUpVideoMode(byte upVideoMode) {
//        DeviceParameter.upVideoMode = upVideoMode;
//    }
//
//    public static int getUpdateOrderAutoInterval() {
//        return (int) (updateOrderAutoInterval&0xFF);
//    }
//
//    public static void setUpdateOrderAutoInterval(byte updateOrderAutoInterval) {
//        DeviceParameter.updateOrderAutoInterval = updateOrderAutoInterval;
//    }
//
//    public static byte getUpdateOrderManualInterval() {
//        return updateOrderManualInterval;
//    }
//
//    public static void setUpdateOrderManualInterval(byte updateOrderManualInterval) {
//        DeviceParameter.updateOrderManualInterval = updateOrderManualInterval;
//    }

    public static byte getIsExaminationEn() {
        return isExaminationEn;
    }

    public static void setIsExaminationEn(byte isExaminationEn) {
        DeviceParameter.isExaminationEn = isExaminationEn;
    }

    public static byte getIsTimingChargeEn() {
        return isTimingChargeEn;
    }

    public static void setIsTimingChargeEn(byte isTimingChargeEn) {
        DeviceParameter.isTimingChargeEn = isTimingChargeEn;
    }

    public static String getLoginIP() {
        return loginIP;
    }

    public static void setLoginIP(String loginIP) {
        DeviceParameter.loginIP = loginIP;
    }

    public static int getLoginPort() {
        return loginPort;
    }

    public static void setLoginPort(int loginPort) {
        DeviceParameter.loginPort = loginPort;
    }

    public static String getLoginPlate() {
        return loginPlate;
    }

    public static void setLoginPlate(String loginPlate) {
        DeviceParameter.loginPlate = loginPlate;
    }

    public static String getDeviceNumber() {
        return deviceNumber;
    }

    public static void setDeviceNumber(String number) {
        DeviceParameter.deviceNumber = number;
    }
    public static String getDrivingNo() {
        return drivingNo;
    }

    public static void setDrivingNo(String drivingNo) {
        DeviceParameter.drivingNo = drivingNo;
    }

    public static String getDrivingName() {
        return drivingName;
    }

    public static void setDrivingName(String drivingName) {
        DeviceParameter.drivingName = drivingName;
    }

    private static final byte softType = 0x01;
    private static final String currentSoftVersion = "0001";
    private static String willSoftVersion = null;
    private static byte takePhotoInterval;//定时上传照片时间间隔 minute

    private static byte upPhotoMode;//上传照片模式，主动或被动
    private static byte isBroadcastInfo;//是否播放远程消息附加信息
    private static byte allowTrainOfffMin;//熄火后允许学员继续培训时长，minute
    private static int upGpsIntervalOff;//熄火后上传GPS数据包时间间隔 second
    private static int allowCoachOffMin;//熄火后允许教练保持最大签到时长 minute
    private static byte maxSpeed;//超速阀值
    private static byte allowCoachCrossSchool;//是否允许教练员跨驾校 01允许 0x02=禁止
    private static byte allowStuCrossSchool;//是否允许学员跨驾校培训 01允许 0x02=禁止

    public static byte getmaxSpeed() {
        return maxSpeed;
    }

    public static void setmaxSpeed(byte maxSpeed) {
        DeviceParameter.maxSpeed = maxSpeed;
    }


    public static int getReceiveMaxInterval() {
        return receiveMaxInterval;
    }

    public static void setReceiveMaxInterval(int receiveMaxInterval) {
        DeviceParameter.receiveMaxInterval = receiveMaxInterval;
    }

    public static int getReauthorInterval() {
        return reauthorInterval;
    }

    public static void setReauthorInterval(int reauthorInterval) {
        DeviceParameter.reauthorInterval = reauthorInterval;
    }

    private static int receiveMaxInterval;// 响应平台同类消息最大时间间隔  second
    private static int reauthorInterval;// 重新验证身份时间间隔
    //0x01=可用 0x02=禁用
    private static byte isExaminationEn; //是否可以进入长考模式
    private static byte isTimingChargeEn; //是否允许计时收费模式
    //本机登录参数
    private static String loginIP = null;
    private static int loginPort;
    private static String loginPlate = null;


    private static byte carColor;
    private static String deviceNumber = null;

    private static String drivingNo = null;//驾校编码
    private static String drivingName = null;//驾校编号

    public static int getCityID() {
        return cityID;
    }

    public static int getProviceID() {
        return proviceID;
    }

    private static int proviceID;
    private static int cityID;

    public static byte[] getDeviceSerial() {
        return deviceSerial;
    }

    public static void setDeviceSerial(byte[] deviceSerial) {
        DeviceParameter.deviceSerial = deviceSerial;
    }

    private static byte[] deviceSerial;

    public static String getDeviceIMEI() {
        return deviceIMEI;
    }

    public static void setDeviceIMEI(String deviceIMEI) {
        DeviceParameter.deviceIMEI = deviceIMEI;
    }

    private static String deviceIMEI = null; // 15位IMEI号

    public static String getManufacturerID() {
        return manufacturerID;
    }

    public static String getDeviceType() {
        return deviceType;
    }

    private static  String manufacturerID;
    private static  String deviceType;

    public static String getParameterFilePath() {
        return parameterFilePath;
    }


    public static byte getCarColor() {
        return carColor;
    }

    public static void setCarColor(byte carColor) {
        DeviceParameter.carColor = carColor;
    }


    public static void setParameterFilePath(String parameterFilePath) {
        DeviceParameter.parameterFilePath = parameterFilePath;
    }

    private static String parameterFilePath;

    public static boolean isCoachFingerEn() {
        return coachFingerEn;
    }

    public static void setCoachFingerEn(boolean coachFingerEn) {
        DeviceParameter.coachFingerEn = coachFingerEn;
    }

    public static boolean isStudentFingerEn() {
        return studentFingerEn;
    }

    public static void setStudentFingerEn(boolean studentFingerEn) {
        DeviceParameter.studentFingerEn = studentFingerEn;
    }

    private static boolean coachFingerEn;
    private static boolean studentFingerEn;


    public static boolean isStudentFingerEnOut() {
        return studentFingerEnOut;
    }

    public static void setStudentFingerEnOut(boolean studentFingerEnOut) {
        DeviceParameter.studentFingerEnOut = studentFingerEnOut;
    }

    public static boolean isCoachFingerEnOut() {
        return coachFingerEnOut;
    }

    public static void setCoachFingerEnOut(boolean coachFingerEnOut) {
        DeviceParameter.coachFingerEnOut = coachFingerEnOut;
    }
    private static boolean coachFingerEnOut;
    private static boolean studentFingerEnOut;

    public static int getSoftVersionCode() {
        return softVersionCode;
    }

    public static void setSoftVersionCode(int softVersionCode) {
        DeviceParameter.softVersionCode = softVersionCode;
    }

    public static String getSoftVersionName() {
        return softVersionName;
    }

    public static void setSoftVersionName(String softVersionName) {
        DeviceParameter.softVersionName = softVersionName;
    }

    private static int softVersionCode= 0;
    private static String softVersionName = null;

    public static void setDateTime(int year, int month, int day, int hour, int minute, int second) throws IOException{
        Calendar c = Calendar.getInstance();
        c.set(year, month, day, hour, minute, second);
        long when = c.getTimeInMillis();
        if (when / 1000 < Integer.MAX_VALUE) {
            try {
                SystemClock.setCurrentTimeMillis(when);
            }catch(SecurityException ex)
            {
                ex.printStackTrace();
            }
        }
        long now = Calendar.getInstance().getTimeInMillis();
        //Log.d(TAG, "set tm="+when + ", now tm="+now);
        if (now - when > 10000)
            throw new IOException("failed to set Date.");
    }

    public static byte intToHex(int val) {
        return (byte) (((val / 10) << 4) + val % 10);
    }

    //get BCD DateTime YYMMDD hhmmss
    public static byte[] getDateTime() {
        byte[] dateTime = new byte[6];
        int[] val = new int[6];

        Calendar c = Calendar.getInstance();
        val[0] = c.get(Calendar.YEAR) - 2000;
        val[1] = c.get(Calendar.MONTH) + 1;
        val[2] = c.get(Calendar.DATE);
        val[3] = c.get(Calendar.HOUR_OF_DAY);
        val[4] = c.get(Calendar.MINUTE);
        val[5] = c.get(Calendar.SECOND);
        for (int i = 0; i < 6; i++) {
            dateTime[i] = intToHex(val[i]);
        }
        return dateTime;
    }

    public static byte[] getSoftVersion1(byte type) {
        byte[] retBytes = new byte[15];
        byte[] tempBytes;
        byte[] pbyte;
        retBytes[0] = type;
        switch (type) {
            case 1://    0x01=终端软件1
            case 2://    0x02=终端软件2
            case 3://    0x03=终端软件3
            case 9://    0x09=终端配置文件
                try {
                    tempBytes = DeviceParameter.getSoftVersionName().getBytes("GBK"); //当前软件版本
                    System.arraycopy(tempBytes, 0, retBytes, 1, 4);
                    System.arraycopy(tempBytes, 0, retBytes, 5, 4);//待升级软件版本
                    pbyte = StringUtils.HexStringToBytes("190616120000".toString());//软件生效时间
                    System.arraycopy(pbyte, 0, retBytes, 9, 6);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
        }
        return retBytes;
    }

    public static void initParameter()
    {
        takePhotoInterval = 15;
        upPhotoMode = 0x01;
        isBroadcastInfo = 0x01;
        allowTrainOfffMin = 10;//minute
        upGpsIntervalOff = 3600;//second
        allowCoachOffMin = 15;//minute
        reauthorInterval = 30;//min
        allowCoachCrossSchool = 0x01;//允许
        allowStuCrossSchool = 0x01; //允许
        receiveMaxInterval = 30;// second
        isExaminationEn = 0x01;
        isTimingChargeEn = 0x01;

        //drivingNo = "6218629213252614";//驾校编码
//        drivingNo = "9694949161640983";//测试
//        drivingName="终端实操检测驾校";//驾校编号
        drivingName="驾校";

        //loginIP = "114.215.173.239";//检测
        loginIP="www.gyjponline.com";//39
        //loginIP="203.86.28.37";//公司测试
//        loginIP="";//测试默认
        //loginPort = 9001;
//        loginPort=48080;
        loginPort=8081;
//        loginPort=0;//测试默认
        loginPlate = "陕AA521学";
        //deviceIMEI = "100203086028045";
        proviceID = 11;
        cityID = 108;

        manufacturerID = "J0101";
        //deviceType = "ST-001";
        deviceType = "V600";
        deviceSerial = "1001002".getBytes();

        studentFingerEn = false;
        coachFingerEn = false;
//        long serial = Long.valueOf(Build.SERIAL, 16);
        deviceNumber = "0000013622221234";//测试终端ID
        carColor = 2;
    }

    public static byte[] getTelnetParameter()
    {
        byte[] pdt = new byte[15];

        pdt[0] = 0;
        pdt[1] = takePhotoInterval;
        pdt[2] = upPhotoMode;
        pdt[3] = isBroadcastInfo;
        pdt[4] = allowTrainOfffMin;
        pdt[5] = (byte)(upGpsIntervalOff>>8);
        pdt[6] = (byte)upGpsIntervalOff;
        pdt[7] = (byte)(allowCoachOffMin>>8);
        pdt[8] = (byte)allowCoachOffMin;
        pdt[9] = (byte)(reauthorInterval>>8);
        pdt[10] = (byte)reauthorInterval;
        pdt[11] = allowCoachCrossSchool;
        pdt[12] = allowStuCrossSchool;
        pdt[13] = (byte)(receiveMaxInterval>>8);
        pdt[14] = (byte)receiveMaxInterval;
        return pdt;
    }
    public static boolean setTelnetParameter(byte[] param) {
        if (param == null)
        {
            return false;
        }
        else if (param.length == 0)
        {
            return false;
        }
        if (param[0] == 0) {
            if (param.length < 15)
            {
                return false;
            }
            takePhotoInterval = param[1];
            upPhotoMode = param[2];
            isBroadcastInfo = param[3];
            allowTrainOfffMin = param[4];//minute
            upGpsIntervalOff = (param[5] & 0xFF) * 256 + (param[6] & 0xFF);//second
            allowCoachOffMin = (param[7] & 0xFF) * 256 + (param[8] & 0xFF);//minute
            reauthorInterval = (param[9] & 0xFF) * 256 + (param[10] & 0xFF);//minute
            allowCoachCrossSchool = param[11];
            allowStuCrossSchool = param[12];
            receiveMaxInterval = (param[13] & 0xFF) * 256 + (param[14] & 0xFF);//second
        } else {
            switch (param[0]) {
                case 1:
                    takePhotoInterval = param[1];
                    break;
                case 2:
                    upPhotoMode = param[1];
                    break;
                case 3:
                    isBroadcastInfo = param[1];
                    break;
                case 4:
                    allowTrainOfffMin = param[1];
                    break;
                case 5:
                    upGpsIntervalOff = (param[1] & 0xFF) * 256 + (param[2] & 0xFF);
                    break;
                case 6:
                    allowCoachOffMin = (param[1] & 0xFF) * 256 + (param[2] & 0xFF);
                    break;
                case 7:
                    reauthorInterval = (param[1] & 0xFF) * 256 + (param[2] & 0xFF);
                    break;
            }
        }
        createParameterXML();
        return true;
    }


    public static void createParameterXML() {
        if (parameterFilePath == null)
        {
            return;
        }
        try {
            OutputStream os = new FileOutputStream(parameterFilePath);
            OutputStreamWriter osw = new OutputStreamWriter(os);
            XmlSerializer serializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            serializer.setOutput(writer);
            // <?xml version=”1.0″ encoding=”UTF-8″ standalone=”yes”?>
            serializer.startDocument("UTF-8", true);
            serializer.startTag(null, "DeviceParameter");
            serializer.startTag(null, "takePhotoInterval");
            serializer.text(String.valueOf(DeviceParameter.getTakePhotoInterval()));
            serializer.endTag(null, "takePhotoInterval");
            serializer.startTag(null, "upPhotoMode");
            serializer.text(String.valueOf(DeviceParameter.getUpPhotoMode()));
            serializer.endTag(null, "upPhotoMode");
            serializer.startTag(null, "isBroadcastInfo");
            serializer.text(String.valueOf(DeviceParameter.getIsBroadcastInfo()));
            serializer.endTag(null, "isBroadcastInfo");
            serializer.startTag(null, "allowTrainOfffMin");
            serializer.text(String.valueOf(DeviceParameter.getAllowTrainOfffMin()));
            serializer.endTag(null, "allowTrainOfffMin");
            serializer.startTag(null, "upGpsIntervalOff");
            serializer.text(String.valueOf(DeviceParameter.getUpGpsIntervalOff()));
            serializer.endTag(null, "upGpsIntervalOff");
            serializer.startTag(null, "allowCoachOffMin");
            serializer.text(String.valueOf(DeviceParameter.getAllowCoachOffMin()));
            serializer.endTag(null, "allowCoachOffMin");
            serializer.startTag(null, "allowCoachCrossSchool");
            serializer.text(String.valueOf(DeviceParameter.getAllowCoachCrossSchool()));
            serializer.endTag(null, "allowCoachCrossSchool");
            serializer.startTag(null, "allowStuCrossSchool");
            serializer.text(String.valueOf(DeviceParameter.getAllowStuCrossSchool()));
            serializer.endTag(null, "allowStuCrossSchool");
//            serializer.startTag(null, "takeVideoInterval");
//            serializer.text(String.valueOf(DeviceParameter.getTakeVideoInterval()));
//            serializer.endTag(null, "takeVideoInterval");
//            serializer.startTag(null, "takevideoDuration");
//            serializer.text(String.valueOf(DeviceParameter.getTakevideoDuration()));
//            serializer.endTag(null, "takevideoDuration");
//            serializer.startTag(null, "upVideoMode");
//            serializer.text(String.valueOf(DeviceParameter.getUpVideoMode()));
//            serializer.endTag(null, "upVideoMode");
//            serializer.startTag(null, "updateOrderAutoInterval");
//            serializer.text(String.valueOf(DeviceParameter.getUpdateOrderAutoInterval()));
//            serializer.endTag(null, "updateOrderAutoInterval");
//            serializer.startTag(null, "updateOrderManualInterval");
//            serializer.text(String.valueOf(DeviceParameter.getUpdateOrderManualInterval()));
//            serializer.endTag(null, "updateOrderManualInterval");
            serializer.startTag(null, "isExaminationEn");
            serializer.text(String.valueOf(DeviceParameter.getIsExaminationEn()));
            serializer.endTag(null, "isExaminationEn");
            serializer.startTag(null, "isTimingChargeEn");
            serializer.text(String.valueOf(DeviceParameter.getIsTimingChargeEn()));
            serializer.endTag(null, "isTimingChargeEn");
            serializer.startTag(null, "loginIP");
            serializer.text(DeviceParameter.getLoginIP());
            serializer.endTag(null, "loginIP");
            serializer.startTag(null, "loginPort");
            serializer.text(String.valueOf(DeviceParameter.getLoginPort()));
            serializer.endTag(null, "loginPort");
            serializer.startTag(null, "loginPlate");
            serializer.text(DeviceParameter.getLoginPlate());
            serializer.endTag(null, "loginPlate");
            serializer.startTag(null, "deviceSerial");
            serializer.text(new String(DeviceParameter.getDeviceSerial()));
            serializer.endTag(null, "deviceSerial");
            serializer.startTag(null, "deviceNumber");
            serializer.text(DeviceParameter.getDeviceNumber());
            serializer.endTag(null, "deviceNumber");
//            serializer.startTag(null, "drivingNo");
//            serializer.text(DeviceParameter.getDrivingNo());
//            serializer.endTag(null, "drivingNo");
            serializer.startTag(null, "drivingName");
            serializer.text(DeviceParameter.getDrivingName());
            serializer.endTag(null, "drivingName");
            serializer.startTag(null, "carColor");
            serializer.text(String.valueOf(DeviceParameter.getCarColor()));
            serializer.endTag(null, "carColor");
            serializer.startTag(null, "coachFingerEn");
            serializer.text(String.valueOf(DeviceParameter.isCoachFingerEn()));
            serializer.endTag(null, "coachFingerEn");
            serializer.startTag(null, "studentFingerEn");
            serializer.text(String.valueOf(DeviceParameter.isStudentFingerEn()));
            serializer.endTag(null, "studentFingerEn");
            serializer.startTag(null, "reauthorInterval");
            serializer.text(String.valueOf(DeviceParameter.getReauthorInterval()));
            serializer.endTag(null, "reauthorInterval");
            serializer.startTag(null, "receiveMaxInterval");
            serializer.text(String.valueOf(DeviceParameter.getReceiveMaxInterval()));
            serializer.endTag(null, "receiveMaxInterval");
            serializer.endTag(null, "DeviceParameter");
            serializer.endDocument();
            osw.write(writer.toString());
            osw.close();
            os.close();
//            DocumentBuilderFactory factory =  DocumentBuilderFactory.newInstance();
//            DocumentBuilder builder = factory.newDocumentBuilder();
//            Document document = builder.newDocument();
//            //创建属性名、赋值
//            Element root = document.createElement("DeviceParameter");
//            root.setAttribute("takePhotoInterval", String.valueOf(DeviceParameter.getTakePhotoInterval()));
//            root.setAttribute("upPhotoMode", String.valueOf(DeviceParameter.getUpPhotoMode()));
//            root.setAttribute("isBroadcastInfo", String.valueOf(DeviceParameter.getIsBroadcastInfo()));
//            root.setAttribute("allowTrainOfffMin", String.valueOf(DeviceParameter.getAllowTrainOfffMin()));
//            root.setAttribute("upGpsIntervalOff", String.valueOf(DeviceParameter.getUpGpsIntervalOff()));
//            root.setAttribute("allowCoachOffMin", String.valueOf(DeviceParameter.getAllowCoachOffMin()));
//            root.setAttribute("allowCoachCrossSchool", String.valueOf(DeviceParameter.getAllowCoachCrossSchool()));
//            root.setAttribute("allowStuCrossSchool", String.valueOf(DeviceParameter.getAllowStuCrossSchool()));
//            root.setAttribute("takeVideoInterval", String.valueOf(DeviceParameter.getTakeVideoInterval()));
//            root.setAttribute("takevideoDuration", String.valueOf(DeviceParameter.getTakevideoDuration()));
//            root.setAttribute("upVideoMode", String.valueOf(DeviceParameter.getUpVideoMode()));
//            root.setAttribute("updateOrderAutoInterval", String.valueOf(DeviceParameter.getUpdateOrderAutoInterval()));
//            root.setAttribute("updateOrderManualInterval", String.valueOf(DeviceParameter.getUpdateOrderManualInterval()));
//            root.setAttribute("isExaminationEn", String.valueOf(DeviceParameter.getIsExaminationEn()));
//            root.setAttribute("isTimingChargeEn", String.valueOf(DeviceParameter.getIsTimingChargeEn()));
//            root.setAttribute("loginIP", DeviceParameter.getLoginIP());
//            root.setAttribute("loginPort", String.valueOf(DeviceParameter.getLoginPort()));
//            root.setAttribute("loginPlate", DeviceParameter.getLoginPlate());
//            root.setAttribute("deviceNumber", DeviceParameter.getDeviceNumber());
//            root.setAttribute("drivingNo", DeviceParameter.getDrivingNo());
//            root.setAttribute("drivingName", DeviceParameter.getDrivingName());
//            document.appendChild(root);
//            //定义了用于处理转换指令，以及执行从源到结果的转换的
//            TransformerFactory transformerFactory = TransformerFactory.newInstance();
//            Transformer transformer = transformerFactory.newTransformer();
//            transformer.setOutputProperty("encoding", "UTF-8");
//
////            StringWriter writer = new StringWriter();
////            transformer.transform(new DOMSource(document), new StreamResult(writer));
////            System.out.println(writer.toString());
//            transformer.transform(new DOMSource(document), new StreamResult(new File(path)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static HashMap<String, String> getParameterData() {
        HashMap<String, String> map = new HashMap<String, String>();
        if (parameterFilePath == null)
        {
            return null;
        }
        try {
            File f = new File(parameterFilePath);
            InputStream in = new FileInputStream(f);
            XmlPullParser pullParser = Xml.newPullParser();
            pullParser.setInput(in, "UTF-8"); //为Pull解释器设置要解析的XML数据
            int event = pullParser.getEventType();
            String keyName = "";
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        keyName = pullParser.getName();
                        break;
                    case XmlPullParser.TEXT:
                        String sValue = pullParser.getText();
                        int tempValue;
                        if (!keyName.equals("") && !sValue.equals("")) {
                            map.put(keyName, sValue);
                            if (keyName.equals("takePhotoInterval")) {
                                tempValue = Integer.valueOf(sValue);
                                DeviceParameter.setTakePhotoInterval((byte) (tempValue & 0xff));
                            } else if (keyName.equals("upPhotoMode")) {
                                tempValue = Integer.valueOf(sValue);
                                DeviceParameter.setUpPhotoMode((byte) ((tempValue & 0xff)));
                            } else if (keyName.equals("isBroadcastInfo")) {
                                tempValue = Integer.valueOf(sValue);
                                DeviceParameter.setIsBroadcastInfo((byte) (tempValue & 0xff));
                            } else if (keyName.equals("allowTrainOfffMin")) {
                                tempValue = Integer.valueOf(sValue);
                                DeviceParameter.setAllowTrainOfffMin((byte) (tempValue & 0xff));
                            } else if (keyName.equals("upGpsIntervalOff")) {
                                tempValue = Integer.valueOf(sValue);
                                DeviceParameter.setUpGpsIntervalOff(tempValue);   //int
                            } else if (keyName.equals("allowCoachOffMin")) {
                                tempValue = Integer.valueOf(sValue);
                                DeviceParameter.setAllowCoachOffMin(tempValue);//int
                            } else if (keyName.equals("allowCoachCrossSchool")) {
                                tempValue = Integer.valueOf(sValue);
                                DeviceParameter.setAllowCoachCrossSchool((byte) (tempValue & 0xff));
                            } else if (keyName.equals("allowStuCrossSchool")) {
                                tempValue = Integer.valueOf(sValue);
                                DeviceParameter.setAllowStuCrossSchool((byte) (tempValue & 0xff));
//                            } else if (keyName.equals("takeVideoInterval")) {
//                                tempValue = Integer.valueOf(sValue);
//                                DeviceParameter.setTakeVideoInterval((byte) (tempValue & 0xff));
//                            } else if (keyName.equals("takevideoDuration")) {
//                                tempValue = Integer.valueOf(sValue);
//                                DeviceParameter.setTakevideoDuration((byte) (tempValue & 0xff));
//                            } else if (keyName.equals("upVideoMode")) {
//                                tempValue = Integer.valueOf(sValue);
//                                DeviceParameter.setUpVideoMode((byte) (tempValue & 0xff));
//                            } else if (keyName.equals("updateOrderAutoInterval")) {
//                                tempValue = Integer.valueOf(sValue);
//                                DeviceParameter.setUpdateOrderAutoInterval((byte)tempValue);
//                            } else if (keyName.equals("updateOrderManualInterval")) {
//                                tempValue = Integer.valueOf(sValue);
//                                DeviceParameter.setUpdateOrderManualInterval((byte) (tempValue & 0xff));
                            } else if (keyName.equals("isExaminationEn")) {
                                tempValue = Integer.valueOf(sValue);
                                DeviceParameter.setIsExaminationEn((byte) (tempValue & 0xff));
                            } else if (keyName.equals("isTimingChargeEn")) {
                                tempValue = Integer.valueOf(sValue);
                                DeviceParameter.setIsTimingChargeEn((byte) (tempValue & 0xff));
                            } else if (keyName.equals("loginIP")) {
                                DeviceParameter.setLoginIP(sValue);
                            } else if (keyName.equals("loginPort")) {
                                tempValue = Integer.valueOf(sValue);
                                DeviceParameter.setLoginPort(tempValue);
                            } else if (keyName.equals("loginPlate")) {
                                DeviceParameter.setLoginPlate(sValue);
                            }else if (keyName.equals("deviceSerial")) {
                                DeviceParameter.setDeviceSerial(sValue.getBytes());
                            } else if (keyName.equals("deviceNumber")) {
                                DeviceParameter.setDeviceNumber(sValue);
                            } else if (keyName.equals("drivingNo")) {
                                DeviceParameter.setDrivingNo(sValue);
                            } else if (keyName.equals("drivingName")) {
                                DeviceParameter.setDrivingName(sValue);
                            } else if (keyName.equals("carColor")) {
                                DeviceParameter.setCarColor((byte)(Integer.valueOf(sValue)&0xFF));
                            }else if (keyName.equals("coachFingerEn")) {
                                DeviceParameter.setCoachFingerEn(Boolean.valueOf(sValue));
                            }else if (keyName.equals("studentFingerEn")) {
                                DeviceParameter.setStudentFingerEn(Boolean.valueOf(sValue));
                            }else if (keyName.equals("receiveMaxInterval")) {
                                DeviceParameter.setReceiveMaxInterval(Integer.valueOf(sValue));
                            }else if (keyName.equals("reauthorInterval")) {
                                DeviceParameter.setReauthorInterval(Integer.valueOf(sValue));
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        keyName = "";
                        break;

                }
                event = pullParser.next();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }



}
//        try {
//            //使用工厂方法初始化SAXParserFactory变量spf
//            SAXParserFactory factory = SAXParserFactory.newInstance();
//            //通过SAXParserFactory得到SAXParser的实例
//            SAXParser parser = factory.newSAXParser();
//            //通过SAXParser得到XMLReader的实例
//            XMLReader reader = parser.getXMLReader();
//            //初始化自定义的类MySaxHandler的变量msh，将beautyList传递给它，以便装载数据
//            DeviceparameterXMLHelper msh = new DeviceparameterXMLHelper();
//            //将对象msh传递给xr
//            reader.setContentHandler(msh);
//            InputSource is = new InputSource(pathFile);
//            //调用xr的parse方法解析输入流
//            reader.parse(is);
//
//
////            SAXParserFactory factory = SAXParserFactory.newInstance();
////            SAXParser parser = factory.newSAXParser();
////            XMLReader reader = parser.getXMLReader();
////
////            DeviceparameterXMLHelper handler = new DeviceparameterXMLHelper();
////            reader.setContentHandler(handler);
////            InputSource is = new InputSource(this.getClassLoader().getResourceAsStream(pathFile));//取得本地xml文件
////            reader.parse(is);
//        } catch (IOException | SAXException | ParserConfigurationException e) {
//            e.printStackTrace();
//        }

//        SAXHandleXML mySAXHandler = new SAXHandleXML(f, 0);//解析文件，并初始参数
//
//        mySAXHandler.process();