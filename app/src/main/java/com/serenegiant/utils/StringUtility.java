package com.serenegiant.utils;

import android.util.Log;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * 字符工具类
 * 
 * @author liuruifeng
 * 
 */
public class StringUtility {
	
   //是否输出log信息
  public static boolean DEBUG=false;
	
	/**
	 * Byte转Bit
	 * 
	 * @param b
	 * @return
	 */
	public static String byte2Bit(byte b) {
		return "" + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)
				+ (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)
				+ (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)
				+ (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1);
	}

	/**
	 * Bit转Byte
	 */
	public static byte BitToByte(String byteStr) {
		int re, len;
		if (null == byteStr) {
			return 0;
		}
		len = byteStr.length();
		if (len != 4 && len != 8) {
			return 0;
		}
		if (len == 8) {// 8 bit处理
			if (byteStr.charAt(0) == '0') {// 正数
				re = Integer.parseInt(byteStr, 2);
			} else {// 负数
				re = Integer.parseInt(byteStr, 2) - 256;
			}
		} else {// 4 bit处理
			re = Integer.parseInt(byteStr, 2);
		}
		return (byte) re;
	}

	/**
	 * byte类型数组转十六进制字符串
	 * 
	 * @param b
	 *            byte类型数组
	 * @param size
	 *            数组长度
	 * @return 十六进制字符串
	 */
	public static String bytes2HexString(byte[] b, int size) {
		String ret = "";

		try {
			for (int i = 0; i < size; i++) {
				String hex = Integer.toHexString(b[i] & 0xFF);
				if (hex.length() == 1) {
					hex = "0" + hex;
				}
				ret += hex.toUpperCase();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public static String bytes2HexString(byte[] b) {
		return bytes2HexString(b,b.length);
	}
	
	
	public static String bytesToHexString(byte[] b, int size) {
		String ret = "";

		try {
			for (int i = 0; i < size; i++) {
				String hex = byte2HexString(b[i]);
				ret += hex;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	

	/**
	 * byte转十六进制字符
	 * 
	 * @param b
	 *            byte值
	 * @return 十六进制字符
	 */
	public static String byte2HexString(byte b) {
		String ret = "";

		try {
			String hex = Integer.toHexString(b & 0xFF);
			if (hex.length() == 1) {
				hex = "0" + hex;
			}
			ret += hex.toUpperCase();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * char类型数组转十六进制字符串
	 * 
	 * @param c
	 *            char类型数组
	 * @param size
	 *            数组大小
	 * @return 十六进制字符串
	 */
	public static String chars2HexString(char[] c, int size) {
		String ret = "";

		try {
			int j = 0;
			for (int i = 0; i < size; i++) {
				j = Integer.valueOf(c[i]);
				String hex = Integer.toHexString(j);
				if (hex.length() == 1) {
					hex = "0" + hex;
				}
				ret += hex.toUpperCase();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	
	/**
	 * long转byte数组
	 * @param num
	 * @return
	 */
	public static byte[] long2Bytes(long num) {  
	    byte[] byteNum = new byte[8];  
	    for (int ix = 0; ix < 8; ++ix) {  
	        int offset = 64 - (ix + 1) * 8;  
	        byteNum[ix] = (byte) ((num >> offset) & 0xff);  
	    }  
	    return byteNum;  
	}  
	/**
	 * int转byte数组
	 * @param num
	 * @return
	 */
	public static byte[] int2Bytes(int num) {  
        byte[] byteNum = new byte[4];  
        for (int ix = 0; ix < 4; ++ix) {  
            int offset = 32 - (ix + 1) * 8;  
            byteNum[ix] = (byte) ((num >> offset) & 0xff);  
        }  
        return byteNum;  
    }  
	
	
	

	/**
	 * 将8字节的byte数组转成一个long值
	 * 
	 * @param byteArray
	 * @return 转换后的long型数值
	 */
	public static long byteArrayTolong(byte[] byteArray) {
		byte[] a = new byte[8];
		int i = a.length - 1, j = byteArray.length - 1;
		for (; i >= 0; i--, j--) {// 从b的尾部(即int值的低位)开始copy数据
			if (j >= 0)
				a[i] = byteArray[j];
			else
				a[i] = 0;// 如果b.length不足4,则将高位补0
		}
		
//		for(i=0;i<8;i++)
//		{
//			Log.i("StringUtility","byteArrayTolong() "+ String.format("%02x", a[i]) + " ");
//		}
		
		// 注意此处和byte数组转换成int的区别在于，下面的转换中要将先将数组中的元素转换成long型再做移位操作，
		// 若直接做位移操作将得不到正确结果，因为Java默认操作数字时，若不加声明会将数字作为int型来对待，此处必须注意。
		long v0 = (long) (a[0] & 0xff) << 56;// &0xff将byte值无差异转成int,避免Java自动类型提升后,会保留高位的符号位
		long v1 = (long) (a[1] & 0xff) << 48;
		long v2 = (long) (a[2] & 0xff) << 40;
		long v3 = (long) (a[3] & 0xff) << 32;
		long v4 = (long) (a[4] & 0xff) << 24;
		long v5 = (long) (a[5] & 0xff) << 16;
		long v6 = (long) (a[6] & 0xff) << 8;
		long v7 = (long) (a[7] & 0xff);

//		Log.i("StringUtility", "" + v0 + "@" + v1 + "@" + v2 + "@" + v3 + "@"
//				+ v4 + "@" + v5 + "@" + v6 + "@" + v7);
		return v0 | v1 | v2 | v3 | v4 | v5 | v6 | v7;
	}

	/**
	 * byte数组转成int值
	 * 
	 * @param bytes
	 * @return
	 */
	public static int bytesToInt(byte[] bytes) {

		int addr = bytes[0] & 0xFF;

		addr |= ((bytes[1] << 8) & 0xFF00);

		addr |= ((bytes[2] << 16) & 0xFF0000);

		addr |= ((bytes[3] << 24) & 0xFF000000);

		return addr;

	}
	
	
	
	public static byte[] Int2bytes(int value) {
		
		byte[] bytes=new byte[4];
		
		bytes[0]=(byte) Integer.parseInt(((value>>24)&0xFF)+"",16);
		bytes[1]=(byte) Integer.parseInt(((value>>16)&0xFF)+"",16);
		bytes[2]=(byte) Integer.parseInt(((value>>8)&0xFF)+"",16);
		bytes[3]=(byte) Integer.parseInt(((value)&0xFF)+"",16);

		for(int i=0;i<4;i++)
		Log.i("StringUtility", "Int2bytes() bytes = "+String.format("%02x", bytes[i]));

		return bytes;

	}

	/**
	 * 将8字节的Char数组转成一个long值
	 * 
	 * @param byteArray
	 * @return 转换后的long型数值
	 */
	public static long charArrayTolong(char[] array) {
		char[] a = new char[8];
		int i = a.length - 1, j = array.length - 1;
		for (; i >= 0; i--, j--) {// 从b的尾部(即int值的低位)开始copy数据
			if (j >= 0)
				a[i] = array[j];
			else
				a[i] = 0;// 如果b.length不足4,则将高位补0
		}
		// 注意此处和byte数组转换成int的区别在于，下面的转换中要将先将数组中的元素转换成long型再做移位操作，
		// 若直接做位移操作将得不到正确结果，因为Java默认操作数字时，若不加声明会将数字作为int型来对待，此处必须注意。
		long v0 = (long) (a[0] & 0xff) << 56;// &0xff将byte值无差异转成int,避免Java自动类型提升后,会保留高位的符号位
		long v1 = (long) (a[1] & 0xff) << 48;
		long v2 = (long) (a[2] & 0xff) << 40;
		long v3 = (long) (a[3] & 0xff) << 32;
		long v4 = (long) (a[4] & 0xff) << 24;
		long v5 = (long) (a[5] & 0xff) << 16;
		long v6 = (long) (a[6] & 0xff) << 8;
		long v7 = (long) (a[7] & 0xff);

		Log.i("StringUtility", "" + v0 + "@" + v1 + "@" + v2 + "@" + v3 + "@"
				+ v4 + "@" + v5 + "@" + v6 + "@" + v7);
		return v0 | v1 | v2 | v3 | v4 | v5 | v6 | v7;

	}

	/**
	 * 逆转字节数组
	 * 
	 * @param b
	 * @return
	 */
	public static byte[] reverse(byte[] b) {

		byte[] temp = new byte[b.length];
		for (int i = 0; i < b.length; i++) {
			temp[i] = b[b.length - 1 - i];
		}
		return temp;
	}

	/**
	 * 读取无符号位的Short数，16位
	 * 
	 * @param readBuffer
	 * @return
	 * @throws IOException
	 */
	private static final BigInteger readUnsignedShort(byte[] readBuffer)
			throws IOException {
		if (readBuffer == null || readBuffer.length < 2)
			return new BigInteger("0");
		// 处理成无符号数
		byte[] uint64 = new byte[3];
		uint64[2] = 0;
		System.arraycopy(readBuffer, 0, uint64, 0, 2);
		return new BigInteger(reverse(uint64));
	}

	/**
	 * 读取无符号位的长整数，64位
	 * 
	 * @param readBuffer
	 * @return
	 * @throws IOException
	 */
	public static final BigInteger readUnsignedInt64(byte[] readBuffer)
			throws IOException {
		if (readBuffer == null || readBuffer.length < 8)
			return new BigInteger("0");
		// 处理成无符号数
		byte[] uint64 = new byte[9];
		uint64[8] = 0;
		System.arraycopy(readBuffer, 0, uint64, 0, 8);
		return new BigInteger(reverse(uint64));
	}

	/**
	 * char数组转long
	 * 
	 * @param c
	 * @param size
	 * @return
	 */
	public static long chars2Long(char[] c, int start, int len) {
		// String ret = "";
		//
		// try {
		// long j = 0L;
		// for (int i = start; i < start+len; i++) {
		// j = Long.valueOf(c[i]);
		// ret +=j;
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		// byte[] a = new byte[8];
		// int i = a.length - 1, j = c.length - 1;
		// for (; i >= 0; i--, j--) {// 从b的尾部(即int值的低位)开始copy数据
		// if (j >= 0)
		// {
		// char[] nc=new char[1];
		// nc[0]=c[j];
		// a[i] =getBytes(nc)[0];
		// }
		// else
		// a[i] = 0;// 如果b.length不足4,则将高位补0
		// }
		// // 注意此处和byte数组转换成int的区别在于，下面的转换中要将先将数组中的元素转换成long型再做移位操作，
		// // 若直接做位移操作将得不到正确结果，因为Java默认操作数字时，若不加声明会将数字作为int型来对待，此处必须注意。
		// long v0 = (long) (a[0] & 0xff) << 56;//
		// &0xff将byte值无差异转成int,避免Java自动类型提升后,会保留高位的符号位
		// long v1 = (long) (a[1] & 0xff) << 48;
		// long v2 = (long) (a[2] & 0xff) << 40;
		// long v3 = (long) (a[3] & 0xff) << 32;
		// long v4 = (long) (a[4] & 0xff) << 24;
		// long v5 = (long) (a[5] & 0xff) << 16;
		// long v6 = (long) (a[6] & 0xff) << 8;
		// long v7 = (long) (a[7] & 0xff);
		//
		// Log.i("StringUtility",
		// ""+v0+"@"+v1+"@"+v2+"@"+v3+"@"+v4+"@"+v5+"@"+v6+"@"+v7);
		// return v0 + v1 + v2 + v3 + v4 + v5 + v6 + v7;

		// return Long.parseLong(ret);

		byte[] bytes = getBytes(c);
		for (int i = 0; i < bytes.length; i++) {
			Log.i("StringUtility", "chars2Long bytes[i]:" + bytes[i]);
		}

		return byteArrayTolong(bytes);
	}

	/**
	 * char转十六进制
	 * 
	 * @param c
	 *            char值
	 * @return 十六进制字符
	 */
	public static String char2HexString(char c) {
		char[] cs = new char[1];
		cs[0] = c;

		return chars2HexString(cs, 1);

	}

	/**
	 * 判断字符串是否是十进制数字
	 * 
	 * @param str
	 *            要判断的字符串
	 * @return true是，false否
	 */
	public static boolean isOctNumber(String str) {
		boolean flag = false;
		for (int i = 0, n = str.length(); i < n; i++) {
			char c = str.charAt(i);
			if (c == '0' | c == '1' | c == '2' | c == '3' | c == '4' | c == '5'
					| c == '6' | c == '7' | c == '8' | c == '9') {
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * 判断字符串是否是十六进制数字
	 * 
	 * @param str
	 *            要判断的字符串
	 * @return true是，false否
	 */
	@Deprecated
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
	 * 判断字符串是否是十进制数字，使用正则表达式方式
	 * 
	 * @param str
	 *            要判断的字符串
	 * @return true是，false否
	 */
	public static boolean isOctNumberRex(String str) {
		String validate = "\\d+";
		return str.matches(validate);
	}

	/**
	 * 判断字符串是否是十六进制数字，使用正则表达式方式
	 * 
	 * @param str
	 *            要判断的字符串
	 * @return true是，false否
	 */
	public static boolean isHexNumberRex(String str) {
		String validate = "(?i)[0-9a-f]+";
		return str.matches(validate);
	}

	/**
	 * 十六进制字符串转换成char数组
	 * 
	 * @param s
	 *            十六进制字符串
	 * @return char数组
	 */
	public static char[] hexString2Chars(String s) {

		s = s.replace(" ", "");

		char[] bytes = new char[s.length() / 2];

		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (char) Integer.parseInt(s.substring(2 * i, 2 * i + 2),
					16);
		}

		return bytes;
	}

	/**
	 * char类型数组转byte类型数组
	 * 
	 * @param chars
	 *            char类型数组
	 * @return byte类型数组
	 */
	public static byte[] getBytes(char[] chars) {
		Charset cs = Charset.forName("UTF-8");
		CharBuffer cb = CharBuffer.allocate(chars.length);
		cb.put(chars);
		cb.flip();
		ByteBuffer bb = cs.encode(cb);

		return bb.array();

	}

	/**
	 * byte类型数组转char类型数组
	 * 
	 * @param bytes
	 *            byte类型数组
	 * @return char类型数组
	 */
	public static char[] getChars(byte[] bytes) {
		Charset cs = Charset.forName("UTF-8");
		ByteBuffer bb = ByteBuffer.allocate(bytes.length);
		bb.put(bytes);
		bb.flip();
		CharBuffer cb = cs.decode(bb);

		return cb.array();
	}

	/**
	 * 判断字符串是否是十进制数字
	 * 
	 * @param decimal
	 *            要判断的字符串
	 * @return true是，false否
	 */
	public static boolean isDecimal(String decimal) {
		int len = decimal.length();
		int i = 0;
		char ch;

		while (i < len) {
			ch = decimal.charAt(i++);
			if (!(ch >= '0' && ch <= '9'))
				return false;
		}
		return true;
	}

	/**
	 * 十六进制字符串转byte数组
	 * 
	 * @param s
	 *            十六进制字符串
	 * @return byte数组
	 */
	public static byte[] hexString2Bytes(String s) {
		byte[] bytes;
		bytes = new byte[s.length() / 2];

		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) Integer.parseInt(s.substring(2 * i, 2 * i + 2),
					16);
		}

		return bytes;
	}

	/**
	 * 十六进制字符串转byte数组
	 * 
	 * @param hexString
	 *            十六进制字符串
	 * @return byte数组
	 */
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * char转byte
	 * 
	 * @param c
	 *            char类型数组
	 * @return byte类型数组
	 */
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	/**
	 * 判断字符串是否为空
	 * 
	 * @param cs
	 *            字符串
	 * @return true为空，false不为空
	 */
	public static boolean isEmpty(CharSequence cs) {

		return cs == null || cs.length() == 0;

	}

	/**
	 * 判断是否为数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNum(String str) {
		return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
	}

	public static String int2HexString(int n) {
		String str = Integer.toHexString(n);
		int l = str.length();
		if (l == 1)
			return "0" + str;
		else
			return str.substring(l - 2, l);
	}

	public static String ints2HexString(int[] c, int size) {
		String ret = "";

		try {
			int j = 0;
			for (int i = 0; i < size; i++) {
				j = Integer.valueOf(c[i]);
				String hex = Integer.toHexString(j);
				if (hex.length() == 1) {
					hex = "0" + hex;
				}
				ret += hex.toUpperCase();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * 字符串转整数
	 * 
	 * @param str
	 * @param defValue
	 * @return
	 */
	public static int string2Int(String str, int defValue) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
		}
		return defValue;
	}

}
