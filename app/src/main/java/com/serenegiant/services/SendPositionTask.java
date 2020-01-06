package com.serenegiant.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.serenegiant.db.Insert_DB;
import com.serenegiant.net.CommonInfo;
import com.serenegiant.net.DeviceParameter;
import com.serenegiant.utils.IUtil;
import com.serenegiant.utils.MessageDefine;
import com.serenegiant.utils.StringUtils;

import java.util.TimerTask;

public class SendPositionTask extends TimerTask {
  private final String TAG = "SendPositionTask";
  private String zdid = "0000015813885986";

  SharedPreferences sendMessageCount;

  public SendPositionTask(Context context) {
    if (DeviceParameter.getDeviceNumber() != null)
      zdid = DeviceParameter.getDeviceNumber();
    sendMessageCount = context.getSharedPreferences("SendMessageCount", Context.MODE_PRIVATE);
  }

  @Override
  public void run() {
    byte[] position = getCmdData(0x0200);
    if (null != position && position.length != 0){
      String info = StringUtils.bytesToHexString(position);
      Insert_DB.getInstance().insertPositionRecord(info);

      int positionCount = sendMessageCount.getInt("PositionCount",0);
      SharedPreferences.Editor editor = sendMessageCount.edit();
      editor.putInt("PositionCount", positionCount + 1);
      editor.apply();
    }
  }

  public byte[] getCmdData(int cmd) {
    return getCmdData(cmd, 0, (byte) 1, null);
  }
  public byte[] getCmdData(int cmd, int subCmd, byte ackFlag, byte[] dataContent) {
    return getCmdData(cmd, subCmd, ackFlag, dataContent, 1, 1);
  }

  public byte[] getCmdData(int cmd, int subCmd, byte ackFlag, byte[] dataContent, int packNumber, int totalNumber) {
    String phoneNum = "";
    if (IUtil.Num != null){
      if (IUtil.Num.length() == 11)
        phoneNum = "00000" + IUtil.Num;
      else if (IUtil.Num.length() == 12)
        phoneNum = "0000" + IUtil.Num;
    }else
      phoneNum = zdid;

    byte[] phoneNumber = StringUtils.HexStringToBytes(phoneNum);
    byte[] buf = new byte[2048];
    byte[] tempBuf;
    int packageProperty = 0;

    int index = 0;
    buf[index++] = (byte) 0x80;

    buf[index++] = (byte) ((cmd >> 8) & 0xFF);      //消息ID(word)
    buf[index++] = (byte) (cmd & 0xFF);

    buf[index++] = 0;  //分包(B13)，数据加密方式(B10-B12)，消息长度(B0-B9)，预留
    buf[index++] = 0; //长度， 预留
    for (byte val : phoneNumber) {
      buf[index++] = val;
    }
    if (ackFlag == 1)//需要应答
    {
      if (++MessageDefine.sendSequence == 0) {
        MessageDefine.sendSequence++;
      }
      buf[index++] = (byte) ((MessageDefine.sendSequence >> 8) & 0xFF);     //消息流水号(word)
      buf[index++] = (byte) (MessageDefine.sendSequence & 0xFF);
    }
//        else {
//            buf[index++] = (byte) ((recvSequence >> 8) & 0xFF);     //消息流水号(word)
//            buf[index++] = (byte) (recvSequence & 0xFF);
//        }
    buf[index++] = 0x3C;
    if (totalNumber > 1) {
      packageProperty |= 0x2000;
    } else {
      packageProperty = 0;
    }
    if ((packageProperty & 0x2000) > 0) {
      buf[index++] = (byte) ((totalNumber >> 8) & 0xFF);     //消息流水号(word)
      buf[index++] = (byte) (totalNumber & 0xFF);
      buf[index++] = (byte) ((packNumber >> 8) & 0xFF);     //消息流水号(word)
      buf[index++] = (byte) (packNumber & 0xFF);
    }
    if ((packageProperty & 0x2000) > 0 && packNumber > 1) {
      System.arraycopy(dataContent, 0, buf, index, dataContent.length);
      index += dataContent.length;
    } else {
      switch (cmd) {
        case 0x0200:
          //位置基本信息
          //tempBuf = CommonInfo.getGpsData();
          tempBuf = CommonInfo.getGpsPackage();
          StringBuffer buf1 = new StringBuffer();
          for(int i = 28; i < tempBuf.length; i ++){
            buf1.append(tempBuf[i]);
          }
          Log.e(TAG, "getCmdData: package is " + buf1);
          System.arraycopy(tempBuf, 0, buf, index, tempBuf.length);
          index += tempBuf.length;
          break;
      }
    }
    if ((packageProperty & 0x2000) > 0) {
      packageProperty |= (index - 20);
    } else {
      packageProperty |= (index - 16);
    }
    buf[3] = (byte) ((packageProperty >> 8) & 0xFF);       //分包(B13)，数据加密方式(B10-B12)，消息长度(B0-B9)
    buf[4] = (byte) (packageProperty & 0xFF);//长度
    byte[] sendProtocolbuf = new byte[index];
    System.arraycopy(buf, 0, sendProtocolbuf, 0, index);
    byte[] retData = formatToProtocolData(sendProtocolbuf);
    return retData;
  }

  byte[] formatToProtocolData(byte[] bytes) {
    int add = 0;
    byte xor = 0;
    for (byte val : bytes) {
      xor ^= val;
      if (val == 0x7E || val == 0x7D) {
        add++;
      }
    }
    if (xor == 0x7E || xor == 0x7D) {
      add++;
    }
    byte[] packageBytes = new byte[add + 3 + bytes.length];
    int index = 0;

    packageBytes[index++] = 0x7E;
    for (byte val : bytes) {
      if (val == 0x7D) {
        packageBytes[index++] = 0x7D;
        packageBytes[index++] = 0x01;
      } else if (val == 0x7E) {
        packageBytes[index++] = 0x7D;
        packageBytes[index++] = 0x02;
      } else {
        packageBytes[index++] = val;
      }
    }
    if (xor == 0x7D) {
      packageBytes[index++] = 0x7D;
      packageBytes[index++] = 0x01;
    } else if (xor == 0x7E) {
      packageBytes[index++] = 0x7D;
      packageBytes[index++] = 0x02;
    } else {
      packageBytes[index++] = xor;
    }
    packageBytes[index++] = 0x7E;
    return packageBytes;
  }
}
