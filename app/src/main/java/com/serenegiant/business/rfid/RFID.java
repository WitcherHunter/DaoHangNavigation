//package com.serenegiant.business.rfid;
//
//import android.app.Activity;
//import android.util.Log;
//
//import com.rscja.deviceapi.RFIDWithISO14443A;
//import com.rscja.deviceapi.RFIDWithISO14443B;
//import com.rscja.deviceapi.entity.SimpleRFIDEntity;
//import com.rscja.deviceapi.exception.RFIDReadFailureException;
//import com.serenegiant.utils.IUtil;
//import com.serenegiant.utils.MyBuffer;
//import com.serenegiant.utils.SoundManage;
//import com.serenegiant.utils.StringUtils;
//
//import java.text.SimpleDateFormat;
//
///**
// * Created by zhoupin on 2016/6/2 0002.
// */
//public class RFID {
//    private static final String TAG = "RFID";
//    //int rfidPower=0;//  0：未来上电，    1：这正上电     2：上电成功        3上电失败
//    private static RFIDWithISO14443A mRFID_A;
//    private static RFIDWithISO14443B mRFID_B;
//    public static VerificationType verificationType = VerificationType.CARD;//验证类型
//    private Activity ac;
//    public enum VerificationType {
//        CARD,//卡号验证
//        IDENTIFICATION//身份证验证
//    }
//
//    public RFIDWithISO14443A getmRFID_A() {
//        return mRFID_A;
//    }
//
//
//    public RFID(Activity ac) {
//        this.ac = ac;
//        try {
//            if (mRFID_A == null)
//                mRFID_A = RFIDWithISO14443A.getInstance();
//            if (mRFID_B == null)
//                mRFID_B = RFIDWithISO14443B.getInstance();
//        } catch (Exception ex) {
//            Log.e("RFID", "RFID 初始化失败！！！！！！");
//        }
//    }
//
//
//    public boolean rfid_init() {
//        boolean re = false;
//        if (verificationType == VerificationType.CARD) {
//            if (mRFID_A != null) {
//                if (mRFID_B != null)
//                    mRFID_B.free();
//
//                re = mRFID_A.init();
//            }
//        } else {
//            if (mRFID_B != null) {
//                if (mRFID_A != null)
//                    mRFID_A.free();
//                re = mRFID_B.init();
//            }
//        }
//        return re;
//    }
//
//
//    public boolean rfid_init(VerificationType type) {
//        boolean re = false;
//        verificationType = type;
//        if (verificationType == VerificationType.CARD) {
//            if (mRFID_A != null) {
//                if (mRFID_B != null)
//                    mRFID_B.free();
//
//                re = mRFID_A.init();
//            }
//        } else {
//            if (mRFID_B != null) {
//                if (mRFID_A != null)
//                    mRFID_A.free();
//                re = mRFID_B.init();
//            }
//        }
//
//        return re;
//    }
//
//
//    public boolean rfid_free() {
//        if (verificationType == VerificationType.CARD) {
//            if (mRFID_A != null) {
//                return mRFID_A.free();
//            }
//        } else {
//            if (mRFID_B != null) {
//                return mRFID_B.free();
//            }
//        }
//        return false;
//    }
//
//    public String getCardUid() {
//        SimpleRFIDEntity entity = null;
//        entity = mRFID_A.request();
//        if (entity != null) {
//            return entity.getId();
//        } else {
//            return null;
//        }
//    }
//
//    public String getIdentifyUid(){
//        String str=mRFID_B.getUID();
//        if(str == null || str.length() == 0)
//            return null;
//        else {
//            return str;
//        }
//    }
//
//
//    public CardInfo readCardInfo(byte type) {
//        try {
//            if (mRFID_A != null) {
//                SimpleRFIDEntity entity = null;
//                entity = mRFID_A.request();
////                    tvLine2.setText(skey);
//                if (entity != null) {
//                    String sUid = entity.getId();
//                    String skey = "FFFFFFFFFFFF";
//                    CardInfo info = new CardInfo();
//                    info.setCardID(entity.getId());
//                    String name;
//                    String number;
//                    String identify;
//                    String DrivingNo;
//                    String cardID;
//                    byte[] readDate;
//                    byte[] validData;
//                    byte[] cardData = new byte[256];
//                    int index = 0;
//
//                    RFIDWithISO14443A.KeyType nKeyType = RFIDWithISO14443A.KeyType.TypeA;
//                    String sRead;
//                    String sDecrypt;
//                    StringBuffer sb = new StringBuffer();
//                    String temp;
//                    char[] recvChars;
//                    for (int sector = 0; sector < 2; sector++) {
//                            if (mRFID_A.VerifySector(sector, skey, nKeyType)) {
//                            for (int block = 0; block < 3; block++) {
//                                if ((sector > 0 || block > 0)) {//跳过第一扇区的第一块
//                                    recvChars = mRFID_A.M1_ReadData(sector, block);
//                                    String hexData = String.valueOf(recvChars);
////                                    //去掉补位0
////                                    if(hexData != null && hexData.contains("00")){
////                                        hexData = hexData.substring(0, hexData.indexOf("00"));
////                                    }
////                                    String blockDataStr = StringUtils.hexStringToString(hexData);
////                                    sb.append(blockDataStr);
//
//                                    if (recvChars != null && recvChars.length > 0) {
//                                        readDate = new byte[recvChars.length];
//                                        for (int i = 0; i < readDate.length; i++) {
//                                            readDate[i] = (byte) (recvChars[i] & 0xFF);
//                                        }
//                                        for (int i = 0; i < readDate.length; i++)
//                                        {
//                                            if (readDate[i] != 0x00)
//                                            {
//                                                cardData[index++] = readDate[i];
//                                            }
//                                        }
//                                    }
//                                    else
//                                    {
//                                        return null;
//                                    }
//                                }
//                            }
//                        }
//                        else
//                        {
//                            return null;
//                        }
//                    }
//                    validData = StringUtils.getValidChar(cardData, index);
//                    //System.out.println("end getValidChar");
//                    if (validData != null) {
//                        temp = new String(validData, "gbk");
//                        sb.append(temp);
//                    }
//
//                    String sInfo = sb.toString();
//                    String[] sArray = sInfo.split(",");
//                    if (sArray.length >= 5) {
//                        if (sArray[0].contains("1")) {
//                            info.setType(1);//student
//                        } else if (sArray[0].contains("2")) {
//                            info.setType(2);//coach
//                        } else {
//                            info.setType(0);//null type
//                        }
//                        info.setNumberID(sArray[1]);
//                        info.setIdentification(sArray[2]);
//                        info.setName(sArray[3]);
//                        info.setDrivingNumber(sArray[4]);
//                        info.setCarTypr(sArray[5]);
//                        info.setCardID(sUid);
//                        info.setUid(sUid);
//                        return info;
//                    }
//                }
//            }
//        } catch (RFIDReadFailureException e) {
//            e.printStackTrace();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return null;
//    }
//
//    public CardInfo readCardInfo() {
//        try {
//            if (mRFID_A != null) {
//                SimpleRFIDEntity entity = null;
//                entity = mRFID_A.request();
//                if (entity != null) {
//                    String sUid = entity.getId();
//                    String skey = "a1a2a3";
//                    CardInfo info = new CardInfo();
//                    info.setCardID(entity.getId());
//
//                    byte[] readDate;
//
//                    int index = 0;
//
//                    RFIDWithISO14443A.KeyType nKeyType = RFIDWithISO14443A.KeyType.TypeA;
//                    StringBuffer sb = new StringBuffer();
//                    char[] recvChars;
//                    for (int sector = 1; sector < 27; sector++) {
//                        if( sector != 1 && sector != 2 && sector != 3 && sector != 26)continue;
//                        if (mRFID_A.VerifySector(sector, skey, nKeyType)) {
//                            //1 2 3 26 扇区可读
//                            byte[] cardData = new byte[48];
//                            for (int block = 0; block < 3; block++) {
//                                    recvChars = mRFID_A.M1_ReadData(sector, block);
////                                    //去掉补位0
////                                    if(hexData != null && hexData.contains("00")){
////                                        hexData = hexData.substring(0, hexData.indexOf("00"));
////                                    }
////                                    String blockDataStr = StringUtils.hexStringToString(hexData);
////                                    sb.append(blockDataStr);
//
//                                    if (recvChars != null && recvChars.length > 0) {
//                                        readDate = new byte[recvChars.length];
//                                        for (int i = 0; i < readDate.length; i++) {
//                                            readDate[i] = (byte) (recvChars[i] & 0xFF);
//                                        }
//                                        for (int i = 0; i < readDate.length; i++)
//                                        {
//                                                cardData[index] = readDate[i];
//                                                index++;
//                                        }
//                                    }
//                                    else
//                                    {
//                                        return null;
//                                    }
//                            }
//                                MyBuffer buffer = new MyBuffer(cardData);
//                            if(sector == 1){
//                                info.setCardCompany(buffer.getString(4).trim());
//                                info.setType(buffer.get());
//                                info.setNumberID(buffer.getString(16).trim());
//                                info.setName(buffer.getString(20).trim());
//                                info.setOneDayTime(bytesToInt(buffer.gets(4),0));
//                            }else if(sector == 2){
//                                info.setIdentification(buffer.getString(18).trim());
//                                info.setDrivingNumber(buffer.getString(20).trim());
//                                String cardTime = "20"+ bcd2Str(buffer.gets(3));
//                                info.setCardDate(cardTime);
//                                String nowTime = new SimpleDateFormat().format(System.currentTimeMillis());
//                                if(nowTime.compareTo(cardTime) > 0){
//                                    /*
//                                    *超过有卡片效期
//                                    * */
//                                    SoundManage.ttsPlaySound(ac, "超过有效期");
//                                    return null;
//                                }
//                                info.setCarTypr(getCarType(buffer.get()));
//                            }else if(sector == 3){
//                                info.setSubjectOneFixTime(bytesToInt(buffer.gets(4),0));
//                                info.setSubjectTwoFixTime(bytesToInt(buffer.gets(4),0));
//                                info.setSubjectTwoFixMlleage(bytesToInt(buffer.gets(4),0));
//                                info.setSubjectThreeFixTime(bytesToInt(buffer.gets(4),0));
//                                info.setSubjectThreeFixMileage(bytesToInt(buffer.gets(4),0));
//                                info.setSubjectForFixTime(bytesToInt(buffer.gets(4),0));
//                            }else if(sector == 26){
//                                info.setCardState(buffer.get());
//                                info.setOneDayTobeTime(bytesToInt(buffer.gets(4),0));
//                                info.setSignNum(buffer.getShort());
//                                info.setSubjectOneTobeTime(bytesToInt(buffer.gets(4),0));
//                                info.setSubjectTwoTobeTime(bytesToInt(buffer.gets(4),0));
//                                info.setSubjectTwoTobeMlleage(bytesToInt(buffer.gets(4),0));
//                                info.setSubjectThreeTobeTime(bytesToInt(buffer.gets(4),0));
//                                info.setSubjectThreeTobeMileage(bytesToInt(buffer.gets(4),0));
//                                info.setSubjectForTobeTime(bytesToInt(buffer.gets(4),0));
//                                info.setMoney(bytesToInt(buffer.gets(4),0));
//                            }
//                            buffer.clear();
//                            index = 0;
//                        }
//                        else
//                        {
//                            return null;
//                        }
//                    }
//                    if(info.getType() == 1 && info.getCardState() == 0){
//                        int indexs = 0;
//                        data = new byte[48];
//                        data[indexs] = (byte) (1&0xFF);
//                        indexs = intToBytes2(info.getOneDayTobeTime(),data,1);
//                        indexs = shortToBytes2((short) info.getSignNum(),data,indexs);
//                        indexs = intToBytes2(info.getSubjectOneTobeTime(),data,indexs);
//                        indexs = intToBytes2(info.getSubjectTwoTobeTime(),data,indexs);
//                        indexs = intToBytes2(info.getSubjectTwoTobeMlleage(),data,indexs);
//                        indexs = intToBytes2(info.getSubjectThreeTobeTime(),data,indexs);
//                        indexs = intToBytes2(info.getSubjectThreeTobeMileage(),data,indexs);
//                        indexs = intToBytes2(info.getSubjectForTobeTime(),data,indexs);
//                        indexs = intToBytes2(info.getMoney(),data,indexs);
//                        Log.e("TAG",""+indexs);
//
//                        for(int block = 0; block < 3; block++) {
//                            byte[] d16 = new byte[16];
//                            for(int i = 0 ; i < 16 ; i++){
//                                d16[i] = data[block*16 + i];
//                            }
//
//                            if (!mRFID_A.write("a1a2a3", RFIDWithISO14443A.KeyType.TypeA, 26, block, StringUtils.bytesToHexString(d16))) {
//                                break;
//                            }else{
//                                Log.e("Tag","学员卡写入失败");
//                            }
//
//                        }
//                    }
//                    return info;
//                }
//            }
//        } catch (RFIDReadFailureException e) {
//            e.printStackTrace();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return null;
//    }
//    byte[] data;
//    /**
//     * 将int数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。  和bytesToInt2（）配套使用
//     */
//    public  int intToBytes2(int value , byte[] src , int index)
//    {
//        data[index++] = (byte) ((value>>24) & 0xFF);
//        data[index++] = (byte) ((value>>16)& 0xFF);
//        data[index++] = (byte) ((value>>8)&0xFF);
//        data[index++] = (byte) (value & 0xFF);
//        return index;
//    }    /**
//     * 将short数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。  和bytesToInt2（）配套使用
//     */
//    public int shortToBytes2(short value , byte[] src , int index)
//    {
//        data[index++] = (byte) ((value>>8)&0xFF);
//        data[index++] = (byte) (value & 0xFF);
//        return  index;
//    }
//
//    /**
//     * @功能: BCD码转为10进制串(阿拉伯数据)
//     * @参数: BCD码
//     * @结果: 10进制串
//     */
//    public static String bcd2Str(byte[] bytes) {
//        StringBuffer temp = new StringBuffer(bytes.length * 2);
//        for (int i = 0; i < bytes.length; i++) {
//            temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
//            temp.append((byte) (bytes[i] & 0x0f));
//        }
//        return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp
//                .toString().substring(1) : temp.toString();
//    }
//
//    /// <summary>
//    /// 10进制串转为BCD码
//    /// </summary>
//    /// <param name="asc">10进制串 </param>
//    /// <returns>BCD码 </returns>
//    public static byte[] str2Bcd(String asc) {
//        int len = asc.length();
//        int mod = len % 2;
//        if (mod != 0) {
//            asc = "0" + asc;
//            len = asc.length();
//        }
//        byte abt[] = new byte[len];
//        if (len >= 2) {
//            len = len / 2;
//        }
//        byte bbt[] = new byte[len];
//        abt = asc.getBytes();
//        int j, k;
//        for (int p = 0; p < asc.length() / 2; p++) {
//            if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
//                j = abt[2 * p] - '0';
//            } else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
//                j = abt[2 * p] - 'a' + 0x0a;
//            } else {
//                j = abt[2 * p] - 'A' + 0x0a;
//            }
//            if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
//                k = abt[2 * p + 1] - '0';
//            } else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
//                k = abt[2 * p + 1] - 'a' + 0x0a;
//            } else {
//                k = abt[2 * p + 1] - 'A' + 0x0a;
//            }
//            int a = (j << 4) + k;
//            byte b = (byte) a;
//            bbt[p] = b;
//        }
//        return bbt;
//    }
//
//    /*车型转换*/
//    private String getCarType(int type){
//        switch (type){
//            case 0x01: return "A0";
//            case 0x11: return "A1";
//            case 0x12: return "A2";
//            case 0x13: return "A3";
//            case 0x20: return "B0";
//            case 0x21: return "B1";
//            case 0x22: return "B2";
//            case 0x30: return "C0";
//            case 0x31: return "C1";
//            case 0x32: return "C2";
//            case 0x33: return "C3";
//            case 0x34: return "C4";
//            case 0x35: return "C5";
//            case 0x40: return "D";
//            case 0x50: return "E";
//            case 0x60: return "F";
//            case 0x70: return "M";
//            case 0x80: return "N";
//            case 0x90: return "P";
//        }
//
//
//        return null;
//    }
//    /*byte 转 int*/
//    public static int bytesToInt(byte[] src, int offset) {
//        int value;
//        value = (int) ( ((src[offset] & 0xFF)<<24)
//                |((src[offset+1] & 0xFF)<<16)
//                |((src[offset+2] & 0xFF)<<8)
//                |(src[offset+3] & 0xFF));
//        return value;
//    }
//    public boolean writeCardFingerChar(byte type,String fingerChar) {
//        String sTemp;
//        try {
//            if (fingerChar == null || fingerChar.length() < 512)
//            {
//                return false;
//            }
//            if (mRFID_A != null) {
//                SimpleRFIDEntity entity = null;
//                entity = mRFID_A.request();
//                if (entity != null) {
//                    String sUid = entity.getId();
//                    String skey = "FFFFFFFFFFFF";
//                    RFIDWithISO14443A.KeyType nKeyType = RFIDWithISO14443A.KeyType.TypeA;
//                    int wIndex = 0;
//                    for (int sector = 5; sector < 16; sector++) {
//                        for (int block = 0; block < 3; block++) {
//                            if (sector > 5 || block > 0) {
//                                sTemp = fingerChar.substring(wIndex * 32, (wIndex + 1) * 32);
//                                if (!mRFID_A.write(skey, nKeyType, sector, block,  sTemp))
//                                {
//                                    return false;
//                                }
//                                wIndex++;
//                            }
//                        }
//                    }
//                    return true;
//                }
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return false;
//    }
//
//    public String readCardFingerChar(byte type) {
//        char[] figChar;
//        byte[] tempBytes = new byte[16];
//        try {
//            StringBuffer sb = new StringBuffer();
//            if (mRFID_A != null) {
//                SimpleRFIDEntity entity = null;
//                entity = mRFID_A.request();
//                if (entity != null) {
//                    String sUid = entity.getId();
//                    String skey = "FFFFFFFFFFFF";
//                    CardInfo info = new CardInfo();
//                    info.setCardID(entity.getId());
//
//                    RFIDWithISO14443A.KeyType nKeyType = RFIDWithISO14443A.KeyType.TypeA;
//                    String sWriteData;
//                    int wIndex = 0;
//                    char[] recvChars;
//                    for (int sector = 5; sector < 16; sector++) {
//                        if (mRFID_A.VerifySector(sector, skey, nKeyType)) {
//                            for (int block = 0; block < 3; block++) {
//                                if (sector > 5 || block > 0) {//跳过第5扇区的第0块
////                                    if(sector == 10 && block > 1){
////                                        continue;
////                                    }
//                                    figChar = mRFID_A.M1_ReadData(sector, block);
//                                    if (figChar == null || figChar.length == 0)
//                                    {
//                                        return null;
//                                    }
////                                    for (int i = 0; i < figChar.length; i++) {
////                                        tempBytes[i] = (byte) (figChar[i] & 0xFF);
////                                    }
////                                    sb.append(StringUtils.bytesToHexString(tempBytes));
//                                    sb.append(String.valueOf(figChar));
//                                }
//                            }
//                        }
//                        else
//                        {
//                            return null;
//                        }
//                    }
//                    return sb.toString();
//                }
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return null;
//    }
//    public String readCardFingerChar(int sector1,int sector2) {
//        char[] figChar;
//        try {
//            StringBuffer sb = new StringBuffer();
//            if (mRFID_A != null) {
//                SimpleRFIDEntity entity = null;
//                entity = mRFID_A.request();
//                if (entity != null) {
//                    String skey = "a1a2a3";
//                    CardInfo info = new CardInfo();
//                    info.setCardID(entity.getId());
//
//                    RFIDWithISO14443A.KeyType nKeyType = RFIDWithISO14443A.KeyType.TypeA;
//                    for (int sector = sector1; sector <sector2; sector++) {
//                        if (mRFID_A.VerifySector(sector, skey, nKeyType)) {
//                            for (int block = 0; block < 3; block++) {
//
//                                    figChar = mRFID_A.M1_ReadData(sector, block);
//                                    if (figChar == null || figChar.length == 0)
//                                    {
//                                        return null;
//                                    }
//                                    sb.append(String.valueOf(figChar));
//                                }
//                            }
//                        else
//                        {
//                            return null;
//                        }
//                    }
//                    return sb.toString();
//                }
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return null;
//    }
//
//    //去掉数据前面的0
//    private String trimZero(String strData) {
//        String str = "";
//        if (strData.trim().length() > 0) {
//            char[] c = strData.toCharArray();
//            for (int k = 0; k < c.length; k++) {
//                if (c[k] != 48) {
//                    str = strData.substring(k, strData.length());
//                    break;
//                }
//            }
//        }
//        return str;
//    }
//
//    //卡片信息
//    public class CardInfo {
//        /*新卡不要卡片ID*/
//        String cardID = "";//卡片ID
//        /*老卡新卡共有数据*/
//        String identification = "";//身份证
//        String name = "";//姓名
//        String numberID = "";//编号
//        String drivingNumber = "";//驾校编号
//        String CarTypr = ""; //车型 C1 C2 A1 A2
//        int type;//卡片类型
//        /*新卡特有数据字段*/
//        String CardCompany;
//        int OneDayTime; //单日训练时长
//        String CardDate;
//        int OneDayTobeTime;
//        int CardState = 0;
//        int SignNum;
//        int Money = -1;
//        int SubjectOneFixTime;
//        int SubjectTwoFixTime;
//        int SubjectThreeFixTime;
//        int SubjectForFixTime;
//        int SubjectThreeFixMileage;
//        int SubjectTwoFixMlleage;
//        int SubjectOneTobeTime;//科目一已学学时
//        int SubjectTwoTobeTime;//科目二已学学时
//        int SubjectThreeTobeTime;//科目三已学学时
//        int SubjectForTobeTime;//科目四已学学时
//        int SubjectThreeTobeMileage;//科目三已培训里程
//        int SubjectTwoTobeMlleage;//科目二已培训里程
//
//        public String getCardCompany() {
//            return CardCompany;
//        }
//
//        public void setCardCompany(String cardCompany) {
//            CardCompany = cardCompany;
//        }
//
//        public int getOneDayTime() {
//            return OneDayTime;
//        }
//
//        public void setOneDayTime(int oneDayTime) {
//            OneDayTime = oneDayTime;
//        }
//
//        public String getCardDate() {
//            return CardDate;
//        }
//
//        public void setCardDate(String cardDate) {
//            CardDate = cardDate;
//        }
//
//        public int getOneDayTobeTime() {
//            return OneDayTobeTime;
//        }
//
//        public void setOneDayTobeTime(int oneDayTobeTime) {
//            OneDayTobeTime = oneDayTobeTime;
//        }
//
//        public int getCardState() {
//            return CardState;
//        }
//
//        public void setCardState(int cardState) {
//            CardState = cardState;
//        }
//
//        public int getSignNum() {
//            return SignNum;
//        }
//
//        public void setSignNum(int signNum) {
//            SignNum = signNum;
//        }
//
//        public int getMoney() {
//            return Money;
//        }
//
//        public void setMoney(int money) {
//            Money = money;
//        }
//
//        public int getSubjectOneFixTime() {
//            return SubjectOneFixTime;
//        }
//
//        public void setSubjectOneFixTime(int subjectOneFixTime) {
//            SubjectOneFixTime = subjectOneFixTime;
//        }
//
//        public int getSubjectTwoFixTime() {
//            return SubjectTwoFixTime;
//        }
//
//        public void setSubjectTwoFixTime(int subjectTwoFixTime) {
//            SubjectTwoFixTime = subjectTwoFixTime;
//        }
//
//        public int getSubjectThreeFixTime() {
//            return SubjectThreeFixTime;
//        }
//
//        public void setSubjectThreeFixTime(int subjectThreeFixTime) {
//            SubjectThreeFixTime = subjectThreeFixTime;
//        }
//
//        public int getSubjectForFixTime() {
//            return SubjectForFixTime;
//        }
//
//        public void setSubjectForFixTime(int subjectForFixTime) {
//            SubjectForFixTime = subjectForFixTime;
//        }
//
//        public int getSubjectThreeFixMileage() {
//            return SubjectThreeFixMileage;
//        }
//
//        public void setSubjectThreeFixMileage(int subjectThreeFixMileage) {
//            SubjectThreeFixMileage = subjectThreeFixMileage;
//        }
//
//        public int getSubjectTwoFixMlleage() {
//            return SubjectTwoFixMlleage;
//        }
//
//        public void setSubjectTwoFixMlleage(int subjectTwoFixMlleage) {
//            SubjectTwoFixMlleage = subjectTwoFixMlleage;
//        }
//
//        public int getSubjectOneTobeTime() {
//            return SubjectOneTobeTime;
//        }
//
//        public void setSubjectOneTobeTime(int subjectOneTobeTime) {
//            SubjectOneTobeTime = subjectOneTobeTime;
//        }
//
//        public int getSubjectTwoTobeTime() {
//            return SubjectTwoTobeTime;
//        }
//
//        public void setSubjectTwoTobeTime(int subjectTwoTobeTime) {
//            SubjectTwoTobeTime = subjectTwoTobeTime;
//        }
//
//        public int getSubjectThreeTobeTime() {
//            return SubjectThreeTobeTime;
//        }
//
//        public void setSubjectThreeTobeTime(int subjectThreeTobeTime) {
//            SubjectThreeTobeTime = subjectThreeTobeTime;
//        }
//
//        public int getSubjectForTobeTime() {
//            return SubjectForTobeTime;
//        }
//
//        public void setSubjectForTobeTime(int subjectForTobeTime) {
//            SubjectForTobeTime = subjectForTobeTime;
//        }
//
//        public int getSubjectThreeTobeMileage() {
//            return SubjectThreeTobeMileage;
//        }
//
//        public void setSubjectThreeTobeMileage(int subjectThreeTobeMileage) {
//            SubjectThreeTobeMileage = subjectThreeTobeMileage;
//        }
//
//        public int getSubjectTwoTobeMlleage() {
//            return SubjectTwoTobeMlleage;
//        }
//
//        public void setSubjectTwoTobeMlleage(int subjectTwoTobeMlleage) {
//            SubjectTwoTobeMlleage = subjectTwoTobeMlleage;
//        }
//
//        public String getCarTypr() {
//            return CarTypr;
//        }
//
//        public void setCarTypr(String carTypr) {
//            CarTypr = carTypr;
//        }
//
//        public int getType() {
//            return type;
//        }
//
//        public void setType(int type) {
//            this.type = type;
//        }
//
//
//
//        public String getUid() {
//            return uid;
//        }
//
//        public void setUid(String uid) {
//            this.uid = uid;
//        }
//
//        String uid;
//
//        public String getDrivingNumber() {
//            return drivingNumber;
//        }
//
//        public void setDrivingNumber(String drivingNumber) {
//            this.drivingNumber = drivingNumber;
//        }
//
//        public String getCardID() {
//            return cardID;
//        }
//
//        public void setCardID(String cardID) {
//            this.cardID = cardID;
//        }
//
//        public String getIdentification() {
//            return identification;
//        }
//
//        public void setIdentification(String identification) {
//            this.identification = identification;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//        public String getNumberID() {
//            return numberID;
//        }
//
//        public void setNumberID(String numberID) {
//            this.numberID = numberID;
//        }
//
//        @Override
//        public String toString() {
//            StringBuffer buffer = new StringBuffer();
//            return buffer.toString();
//        }
//    }
//}
