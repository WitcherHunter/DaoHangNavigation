package com.serenegiant.utils;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class StringUtils {

    public static String replaceUrlWithPlus(String url) {
        // 1. 处理特殊字符
        // 2. 去除后缀名带来的文件浏览器的视图凌乱(特别是图片更�?��如此类似处理，否则有的手机打�?��库，全是我们的缓存图�?
        if (url != null) {
            return url.replaceAll("http://(.)*?/", "")
                    .replaceAll("[.:/,%?&=]", "+").replaceAll("[+]+", "+");
        }
        return null;
    }

    /**
     * 验证ip是否合法
     *
     * @param text ip地址
     * @return 验证信息
     */
    public static Boolean isIP(String text) {
        if (text != null && !text.isEmpty()) {
            // 定义正则表达式
            String regex = "^((25[0-5])|(2[0-4]\\d)|(1\\d\\d)|([1-9]\\d)|\\d)(\\.((25[0-5])|(2[0-4]\\d)|(1\\d\\d)|([1-9]\\d)|\\d)){3}$";
            // 判断ip地址是否与正则表达式匹配
            if (text.matches(regex)) {
                // 返回判断信息
                return true;
            } else {
                // 返回判断信息
                return false;
            }
        }
        // 返回判断信息
        return false;
    }

    /**
     * 验证域名是否合法
     *
     * @param text 域名
     * @return 验证信息
     */
    public static Boolean isDomain(String text) {
        if (text != null && !text.isEmpty()) {
            // 定义正则表达式
            String regex = "^([a-zA-Z0-9\\.\\-]+(\\:[a-zA-Z0-9\\.&amp;%\\$\\-]+)*@)*((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|localhost|([a-zA-Z0-9\\-]+\\.)*[a-zA-Z0-9\\-]+\\.(com|edu|gov|int|mil|net|org|biz|arpa|info|name|pro|aero|coop|museum|[a-zA-Z]{2}))$";
            // 判断域名是否与正则表达式匹配
            if (text.matches(regex)) {
                // 返回判断信息
                return true;
            } else {
                // 返回判断信息
                return false;
            }
        }
        // 返回判断信息
        return false;
    }

    public static boolean isEmpty(CharSequence cs) {

        return cs == null || cs.length() == 0;

    }

    public static boolean isNotEmpty(CharSequence cs) {

        return !StringUtils.isEmpty(cs);

    }

    public static String trim(String str) {

        return str == null ? null : str.trim();

    }

    /**
     * 字符串转整数
     *
     * @param str
     * @param defValue
     * @return
     */
    public static int toInt(String str, int defValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
        }
        return defValue;
    }

    /**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static int toInt(Object obj) {
        if (obj == null)
            return 0;
        return toInt(obj.toString(), 0);
    }

    /**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static long toLong(String obj) {
        try {
            return Long.parseLong(obj);
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * 字符转double
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static double toDouble(String obj) {
        try {
            return Double.parseDouble(obj);
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * 字符串转布尔值
     *
     * @param b
     * @return 转换异常返回 false
     */
    public static boolean toBool(String b) {
        try {
            return Boolean.parseBoolean(b);
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 判断是否为整数 INT
     *
     * @param val
     * @return
     */
    public static Boolean isInt(String val) {
        try {
            Integer.parseInt(val);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getTimeString() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        return formatter.format(curDate);
    }

    public static String getTimeFormat(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
        Date curDate = new Date(time);// 获取当前时间
        return formatter.format(curDate);
    }

    /**
     * 判断是否是十六进制
     *
     * @param str
     * @return
     */
    public static boolean isHexNumber(String str) {
        boolean flag = false;
        for (int i = 0; i < str.length(); i++) {
            char cc = str.charAt(i);
            if (cc == '0' || cc == '1' || cc == '2' || cc == '3' || cc == '4'
                    || cc == '5' || cc == '6' || cc == '7' || cc == '8'
                    || cc == '9' || cc == 'A' || cc == 'B' || cc == 'C'
                    || cc == 'D' || cc == 'E' || cc == 'F' || cc == 'a'
                    || cc == 'b' || cc == 'c' || cc == 'c' || cc == 'd'
                    || cc == 'e' || cc == 'f') {
                flag = true;
            }
        }
        return flag;
    }
    /**
     * 十六进制字符串转换成char数组
     *
     * @param s
     * @return
     */
    public static char[] StringToChars(String s) {
        char[] bytes;
        bytes = new char[s.length() / 2];

        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (char) Integer.parseInt(s.substring(2 * i, 2 * i + 2),
                    16);
        }

        return bytes;
    }

    public static String bytesToHexString(byte[] bytes) {

        if (bytes == null || bytes.length == 0)
        {
            return null;
        }
        String s = "";
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            s += hex;
        }
        return s;
    }

    public static byte[] HexStringToBytes(String s) {
        if(s == null){
            return null;
        }

        byte[] bytes;
        bytes = new byte[s.length() / 2];

        for (int i = 0; i < bytes.length; i++) {
            String str=s.substring(2 * i, 2 * i + 2);
            int value = Integer.parseInt(str, 16);
            bytes[i] =(byte)value;
        }

        return bytes;
    }

    //十六进制转汉字
    public  static  String getGB2312_Data(String hex)
    {
        try {
            byte[] buff=StringUtils.HexStringToBytes(hex);
            String  data = new String(buff,"gb2312");
            return  data;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }
    public static String bytesToHexString(byte[] bytes, int offset, int length) {
        StringBuffer temp = new StringBuffer(length * 2);
        int val;

        if (length < 1 || offset + length > bytes.length) {
            return "".toString();
        }
        for (int i = 0; i < length; i++) {
            for (int k = 0; k < 2; k++) {
                if (0 == k) {
                    val = (bytes[offset + i] & 0xF0) >> 4;
                } else {
                    val = bytes[offset + i] & 0x0F;
                }
                if (val < 10) {
                    temp.append(val);
                } else {
                    switch (val) {
                        case 10:
                            temp.append("A");
                            break;
                        case 11:
                            temp.append("B");
                            break;
                        case 12:
                            temp.append("C");
                            break;
                        case 13:
                            temp.append("D");
                            break;
                        case 14:
                            temp.append("E");
                            break;
                        case 15:
                            temp.append("F");
                            break;
                    }
                }
            }
        }
        return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp
                .toString().substring(1) : temp.toString();
    }

    public static byte[] formatRecvData(byte[] recvBytes)
    {
        int cnt = 0;
        //统计长度
        for (byte val:recvBytes)
        {
            if(val == 0x7E || val == 0x7D)
            {
                cnt++;
            }
        }
        if (recvBytes.length > (cnt+1))
        {
            int index = 0;
            byte xor = 0;
            byte[] retData = new byte[recvBytes.length-cnt];
            for (int i = 0; i <  recvBytes.length; i++)          //去除转译字符
            {
                if (recvBytes[i] == 0x7E)
                {
                    continue;
                }
                else if(recvBytes[i] == 0x7D)
                {
                    i++;
                    if (recvBytes[i] == 0x01)
                    {
                        retData[index++] = 0x7D;
                        xor ^= 0x7D;
                    }
                    else if (recvBytes[i] == 0x02)
                    {
                        retData[index++] = 0x7E;
                        xor ^= 0x7E;
                    }
                }
                else
                {
                    retData[index++] = recvBytes[i];
                    xor ^= recvBytes[i];
                }
            }
            if (0 == xor)
            {
                return retData;
            }
            else
            {
                System.out.println("The receive data XOR failure!");
                return null;
            }
        }
        else
        {
            System.out.println("no valid date resolve!");
            return null;
        }

    }

    public static byte[] formatDataToSend(byte[] bytes) {
        int cnt = 0;
        byte xor = 0;
        int index;

        for (int i = 0; i < bytes.length; i++) {
            xor ^= bytes[i];
            if (bytes[i] == 0x7E || bytes[i] == 0x7D) {
                cnt++;
            }
        }
        byte retBytes[] = new byte[bytes.length + 1 + 2+cnt];
        retBytes[0] = 0x7E;//SOF
        index = 1;
        for (byte val : bytes) {//head data & context data
            if (val == 0x7D) {
                retBytes[index++] = 0x7D;
                retBytes[index++] = 0x01;
            } else if (val == 0x7E) {
                retBytes[index++] = 0x7D;
                retBytes[index++] = 0x02;
            } else {
                retBytes[index++] = val;
            }
        }
        retBytes[index++] = xor;//XOR
        retBytes[index++] = 0x7E;//EOF
        return retBytes;
    }
    public static byte[] longToBytes(long val) {
        byte[] tmp = new byte[4];
        tmp[0] = (byte) ((val >> 24) & 0xFF);
        tmp[1] = (byte) ((val >> 16) & 0xFF);
        tmp[2] = (byte) ((val >> 8) & 0xFF);
        tmp[3] = (byte) (val & 0xFF);
        return tmp;
    }

    public static byte[] intToBytes(int val) {
        byte[] tmp = new byte[4];
        tmp[0] = (byte) ((val >> 8) & 0xFF);
        tmp[1] = (byte) (val & 0xFF);
        return tmp;
    }
    public static int  hexbyteToInt(byte hex)
    {
        int res;
        res = hex&0xFF;
        res = res/16*10 + res%16;
        return res;
    }

    //4字节转换为long
    public static long  bytesToLong(byte[] bt)
    {
        long result;
        result = (bt[0]&0xFF)<<24 | (bt[1]&0xFF)<<16 | (bt[2]&0xFF)<<8 | (bt[3]&0xFF);
        return result;
    }

    //比较两个数组的值是否相等，以seg为单位切割数据，比对无序的segment，若元素值一样返回true，其他返回false
    public static boolean arrayCompareWithSegment(byte[] array, byte[] array2, int seg)
    {
        if (array == array2)
        {
            return true;
        }
        else if (seg == 0 || array == null || array2 == null || array.length != array2.length)
        {
            return false;
        }
        else
        {
            byte[] temp = new byte[seg];
            byte[] temp2 = new byte[seg];
            int count = array.length/seg;
            if (count < 1)
            {
                return false;
            }
            for (int i = 0; i < count; i++)
            {
                System.arraycopy(array, i*seg, temp, 0, seg);
                for (int j = 0; j < count; j++)
                {
                    System.arraycopy(array2, j*seg, temp2, 0, seg);
                    if (Arrays.equals(temp, temp2))
                    {
                        break;
                    }
                    else if (j == (count-1))
                    {
                        return false;
                    }

                }
            }
            return true;
        }
    }

    public static byte[] getValidChar(byte[] src, int length)
    {
        int startPos = -1;
        int len = 0;
        for (int i = 0; i < length; i++)
        {
            if(startPos >= 0)
            {
                if ((src[i]&0xFF) > 0x7F)
                {
                    i++;
                    len += 2;
                }
                else if (src[i] != 0)
                {
                    len++;
                }
                else
                {
                    break;
                }
            }
            else
            {
                if (src[i] != 0)
                {
                    startPos = i;
                    if ((src[i]&0xFF) > 0x7F)
                    {
                        i++;
                        len += 2;
                    }
                    else if (src[i] != 0)
                    {
                        len++;
                    }
                }
            }
        }
        if(len > 0)
        {
            byte[] ret = new byte[len];
            System.arraycopy(src, startPos, ret, 0, ret. length);
            return ret;
        }
        return null;
    }

    /**
     * 16进制字符串转换为字符串
     *
     * @param s
     * @return
     */
    public static String hexStringToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(
                        s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "utf-8");
            new String();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }
}
