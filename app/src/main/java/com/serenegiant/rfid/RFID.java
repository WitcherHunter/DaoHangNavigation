package com.serenegiant.rfid;

import android.content.Context;

import com.serenegiant.utils.SoundManage;
import com.serenegiant.utils.StringUtils;
import com.yz.lz.modulapi.JNIUtils;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;

public class RFID {
    private static JNIUtils mInstance = JNIUtils.getInstance();
    public static String password = "^4ADgl";
//    public static String password = "lf&9iQ";
    private Context mContext;

    public enum CardType {
        StudentCard,                //学员卡
        TheoryCoachCard,            //理论教练卡
        PracticeCoachCard,          //实操教练卡
        CommonCoachCard,            //理论+实操教练卡
        DrivingSchoolManageCard,    //驾校管理卡
        DaohangManageCard,          //导航管理卡
        UnKnown                     //未知卡类型
    }

    public RFID(Context mContext) {
        this.mContext = mContext;
    }

    private byte[] getPassword() {
        if (password.isEmpty()){
//            byte[] pwd = new byte[]{(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF};
//            return pwd;
            return StringUtils.HexStringToBytes("ffffffffffff");
        }
        return password.getBytes();
    }

    /**
     * 打开Rfid设备
     *
     * @return true 成功, false 失败.
     */
    public boolean rfid_init() {
        return mInstance.openRfidDevice();
    }

    /**
     * 关闭Rfid设备
     */
    public void rfid_free() {
        mInstance.closeRfidDevice();
    }

    /**
     * 检测卡片
     * @return 是否检测到卡
     */
    public boolean checkCard(){
        return JNIUtils.getInstance().checkCard();
    }

    /**
     * 读取卡片类型
     * @return 1:UltraLight卡 2:1k卡 3:4k卡 4：B卡或身份证
     */
    public byte readCardType(){
        return mInstance.readCardType();
    }

    /**
     * 读取卡id
     * @return
     */
    public byte[] readIdFromCard(){
        return mInstance.readIdFromCard();
    }


    /**
     * 获取卡id
     *
     * @return
     */
    public String getCardUid() {
        byte[] id = mInstance.readIdFromCard();
        if (id == null || id.length == 0)
            return null;
        return new String(id);
    }

    public byte getCardState(){
        byte[] data = readCard(1232,1);
        if (data != null && data.length >= 1)
            return data[0];
        else
            return 0;
    }

    /**
     * 获取卡类型
     *
     * @return
     */
    public CardType getCardType(byte[] type) {
//        byte[] type = mInstance.readDataByAddrNum((short) 36,(byte) 0, getPassword(), (short) 1);
        if (type != null && type.length != 0) {
            switch (type[0]) {
                case (byte) 1:
                    return CardType.StudentCard;
                case (byte) 2:
                    return CardType.TheoryCoachCard;
                case (byte) 3:
                    return CardType.PracticeCoachCard;
                case (byte) 4:
                    return CardType.CommonCoachCard;
                case (byte) 5:
                    return CardType.DaohangManageCard;
                case (byte) 6:
                    return CardType.DrivingSchoolManageCard;
                default:
                    return CardType.UnKnown;
            }
        }
        return CardType.UnKnown;
    }

    /**
     * 读取卡片信息
     *
     * @return
     */
    public CardInfo getCardInfo() {
        byte[] data1 = readCard(32,48 * 3);

        CardInfo cardInfo = new CardInfo();

        if (data1 != null && data1.length != 0) {
            cardInfo.setCardType(getCardType(splitArray(data1, 4, 1)));
            cardInfo.setId(readCardByString(splitArray(data1, 5, 16)));
            cardInfo.setName(readCardByStringGB(splitArray(data1, 21, 20)));
            cardInfo.setPracticeTimePerDay(readCardByInt(splitArray(data1, 41, 4)));

            cardInfo.setIdentification(readCardByString(splitArray(data1, 48, 18)));
            cardInfo.setCompany(readCardByStringGB(splitArray(data1, 66, 20)));
            String validityPeriod = "20" + bcd2Str(splitArray(data1, 86, 3));
            cardInfo.setValidityPeriod(validityPeriod);
            String currentTime = new SimpleDateFormat().format(System.currentTimeMillis());
            if (currentTime.compareTo(validityPeriod) > 0) {
                SoundManage.ttsPlaySound(mContext, "超过有效期");
            }
            cardInfo.setCarType(splitArray(data1, 89, 1)[0]);
            cardInfo.setSubjectOneTotalTime(readCardByInt(splitArray(data1, 96, 4)));
            cardInfo.setSubjectOneTotalTime(readCardByInt(splitArray(data1, 100, 4)));
            cardInfo.setSubjectTwoTotalTime(readCardByInt(splitArray(data1, 104, 4)));
            cardInfo.setSubjectTwoTotalMiles(readCardByInt(splitArray(data1, 108, 4)));
            cardInfo.setSubjectThreeTotalTime(readCardByInt(splitArray(data1, 112, 4)));
            cardInfo.setSubjectThreeTotalMiles(readCardByInt(splitArray(data1, 116, 4)));
            cardInfo.setSubjectFourTotalMiles(readCardByInt(splitArray(data1, 120, 4)));
        }

        byte[] data2 = readCard(1232,48);

        if (data2 != null && data2.length != 0) {
            cardInfo.setCardState(splitArray(data2, 0, 1)[0]);
            cardInfo.setPracticeTime(readCardByInt(splitArray(data2, 1, 4)));
            cardInfo.setCheckInTimes(readCardByShort(splitArray(data2, 5, 2)));
            cardInfo.setSubjectOneLearnedTime(readCardByInt(splitArray(data2, 7, 4)));
            cardInfo.setSubjectTwoLearnedTime(readCardByInt(splitArray(data2, 11, 4)));
            cardInfo.setSubjectTwoLearnedMiles(readCardByInt(splitArray(data2, 15, 4)));
            cardInfo.setSubjectThreeLearnedTime(readCardByInt(splitArray(data2, 19, 4)));
            cardInfo.setSubjectThreeLearnedMiles(readCardByInt(splitArray(data2, 23, 4)));
            cardInfo.setSubjectFourLearnedTime(readCardByInt(splitArray(data2, 27, 4)));
            cardInfo.setVirtualCurrency(splitArray(data2, 31, 4));
            cardInfo.setLastExitDate(bcd2Str(splitArray(data2, 35,3)));
        }

//        cardInfo.setFinger1(readFingerPrint1());
//        cardInfo.setFinger2(readFingerPrint2());

        return cardInfo;
    }

    /**
     * 读卡
     *
     * @param addr 物理地址
     * @param len  读取长度
     * @return Byte数组
     */
    private byte[] readCard(int addr, int len) {
        byte[] result = mInstance.readDataByAddrNum((short) addr,(byte)0, getPassword(), (short) len);
        if (result != null && result.length > 0)
            return result;
        else
            return new byte[0];
    }

    /**
     * 读卡
     *
     * @return 字符串
     */
    private String readCardByString(byte[] src) {
        return new String(src);
    }

    /**
     * 读卡
     *
     * @return GB2312格式字符串
     */
    private String readCardByStringGB(byte[] src) {
        try {
            return new String(src, "GB2312");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    /**
     * 读卡
     *
     * @return 整型
     */
    private int readCardByInt(byte[] src) {
        if (src.length == 1)
            return  -1;
        return byteArrayToInt(src);
    }

    private short readCardByShort(byte[] src){
        if (src.length == 1)
            return -1;
        return byteArrayToShort(src);
    }

    /**
     * 读取第一段指纹
     *
     * @return
     */
    public byte[] readFingerPrint1() {
        byte[] fingerPrint = mInstance.readDataByAddrNum((short) 1520,(byte)0, getPassword(), (short) 768);
        if (fingerPrint != null && fingerPrint.length > 0) {
            return fingerPrint;
        }
        return null;
    }

    /**
     * 读取第二段指纹
     *
     * @return
     */
    public byte[] readFingerPrint2() {
        byte[] fingerPrint = mInstance.readDataByAddrNum((short) 2480,(byte)0, getPassword(), (short) 768);
        if (fingerPrint != null && fingerPrint.length > 0) {
            return fingerPrint;
        }
        return null;
    }

    public boolean writeCard(int addr, byte[] data){
        return mInstance.writeDataByAddrNum((short)addr,(byte)0, getPassword(),data);
    }

    /**
     * @功能: BCD码转为10进制串(阿拉伯数据)
     * @参数: BCD码
     * @结果: 10进制串
     */
    public static String bcd2Str(byte[] bytes) {
        StringBuffer temp = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
            temp.append((byte) (bytes[i] & 0x0f));
        }
        return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp
                .toString().substring(1) : temp.toString();
    }

    public static byte[] str2Bcd(String asc) {
        int len = asc.length();
        int mod = len % 2;
        if (mod != 0) {
            asc = "0" + asc;
            len = asc.length();
        }
        byte abt[] = new byte[len];
        if (len >= 2) {
            len = len / 2;
        }
        byte bbt[] = new byte[len];
        abt = asc.getBytes();
        int j, k;
        for (int p = 0; p < asc.length() / 2; p++) {
            if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
                j = abt[2 * p] - '0';
            } else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
                j = abt[2 * p] - 'a' + 0x0a;
            } else {
                j = abt[2 * p] - 'A' + 0x0a;
            }
            if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
                k = abt[2 * p + 1] - '0';
            } else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
                k = abt[2 * p + 1] - 'a' + 0x0a;
            } else {
                k = abt[2 * p + 1] - 'A' + 0x0a;
            }
            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
        }
        return bbt;
    }

    /**
     * Byte数组转Int
     *
     * @param b Byte数组
     * @return 整型值
     */
    public static int byteArrayToInt(byte[] b) {
        return b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    public static short byteArrayToShort(byte[] b){
        return (short) (((b[1] << 8) | b[0] & 0xff));
    }

    public static byte[] splitArray(byte[] src, int start, int length){
        byte[] temp = new byte[length];
        if (src != null && src.length >= length)
            System.arraycopy(src,start,temp,0,length);
        return temp;
    }

    public static byte[] intToByte4(int i) {
        byte[] targets = new byte[4];
        targets[3] = (byte) (i & 0xFF);
        targets[2] = (byte) (i >> 8 & 0xFF);
        targets[1] = (byte) (i >> 16 & 0xFF);
        targets[0] = (byte) (i >> 24 & 0xFF);
        return targets;
    }

    public static byte[] shortToByte2(int s) {
        byte[] targets = new byte[2];
        targets[0] = (byte) (s >> 8 & 0xFF);
        targets[1] = (byte) (s & 0xFF);
        return targets;
    }

    public static byte[] longToByte8(long lo) {
        byte[] targets = new byte[8];
        for (int i = 0; i < 8; i++) {
            int offset = (targets.length - 1 - i) * 8;
            targets[i] = (byte) ((lo >>> offset) & 0xFF);
        }
        return targets;
    }
}
