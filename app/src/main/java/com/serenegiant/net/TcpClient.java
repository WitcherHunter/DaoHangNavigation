package com.serenegiant.net;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.kongqw.serialportlibrary.Device;
import com.serenegiant.AppConfig;
import com.serenegiant.AppContext;
import com.serenegiant.dataFormat.DrivingRecordInfo;
import com.serenegiant.db.Delete_DB;
import com.serenegiant.db.Insert_DB;
import com.serenegiant.db.Select_DB;
import com.serenegiant.db.Update_DB;
import com.serenegiant.entiy.CoachLoginInfo;
import com.serenegiant.entiy.PhotoHeadInformation;
import com.serenegiant.entiy.StudentLoginInfo;
import com.serenegiant.entiy.TimerDerminalEvent;
import com.serenegiant.entiy.TrainingRecord;
import com.serenegiant.entiy.VideoHeadInformation;
import com.serenegiant.rsa.Base64;
import com.serenegiant.rsa.HexBin;
import com.serenegiant.rsa.ISign;
import com.serenegiant.rsa.IVerify;
import com.serenegiant.rsa.Sign;
import com.serenegiant.rsa.Verify;
import com.serenegiant.ui.RainingActivity;
import com.serenegiant.utils.IUtil;
import com.serenegiant.utils.MessageDefine;
import com.serenegiant.utils.MyBuffer;
import com.serenegiant.utils.SharedPreferencesUtil;
import com.serenegiant.utils.SoundManage;
import com.serenegiant.utils.StringUtils;
import com.serenegiant.utils.Utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;

import de.greenrobot.event.EventBus;

import static android.content.Context.MODE_PRIVATE;

/*
 * Created by Hambobo on 2016-06-09.
 */
public class TcpClient extends Thread {
    enum SendRunState {SENDCOACH, SENDSTULOGIN, SENDSTUOUT, SENDPOSITION, SENDTRAIN, SELPHOTO, SENDPHOTO, STULOG}

    SendRunState sendRunState = SendRunState.SENDCOACH;
    private boolean coahBool, stuBool, stuOutBool, trainBool, positionBool, photoBool = true;
    private int sendTime = 2000;
    private int positionId;
    boolean DEBUG = AppConfig.DEBUG_EN;
    private static final String CERTIFICATION_NAME = "certification.info";
    private static boolean hasBeenCreated = false;
    private static final int PHOTO_PACKAGE_SIZE = 600;
    private final int CHECK_PHOTOS_ONCE = 255;
    private final int CHECK_TRAINLIST_ONCE = 100;
    TransmissionState transState;
    public static long sendDBDataTimeMillis;
    public static boolean tcpLinkState;
    private static boolean isSendPic = false;
    private static boolean isSendingVideo = false;
    private byte[] ordersList;
    private int ordersListCnt;
    private int ordersSendIndex;
    private byte[] areasList;
    private int areasListCnt;
    private int areasSendIndex;
    PhotoHead curPhotoHead;
    VideoHead curVideoHead;
    public static boolean isUpdateOrder = false;
    ArrayList<PhotoHeadInformation> myPhotoHeadInfo;
    ArrayList<VideoHeadInformation> myVideoHeadInfo;
    //订单状态
    boolean sendOrderState = false;
    byte[] orderStateData;
    public int isfirst = 0;

    private WeakReference<Handler> handlerMain = null;
    //Handler handlerMain = null; //主线程句柄
    static Handler handler = null;
    private Context context;
    private boolean hasBeenStarted = false;

    static private boolean isTakePhotoPrompt = false;
    private ByteBuffer myPhotoBuff;

    CoachLoginInfo info;
    StudentLoginInfo stuInfo;

    long currentClassId;

    //此时是否为学员登录
    private boolean isStuLogin = true;

    public byte[] getOrderStateData() {
        return orderStateData;
    }

    public void setHandlerMain(Handler handler) {
        this.handlerMain = new WeakReference(handler);
    }

    @Override
    public synchronized void start() {
        if (!hasBeenStarted) {
            super.start();
        }
    }

    public void sendMsgToUi(int what) {
        if (handlerMain != null) {
            if (handlerMain.get() != null) {
                handlerMain.get().sendEmptyMessage(what);
            }
        }
    }

    public void setOrderStateData(byte[] orderStateData) {
        this.orderStateData = orderStateData;
        sendOrderState = true;
    }

    byte[] getOrderStatePackage() {
        byte[] sendData;
        byte[] order = getOrderStateData();
        if (order == null) {
            return null;
        } else {
            byte[] gpsPac = CommonInfo.getGpsPackage();
            sendData = new byte[order.length + gpsPac.length];
            System.arraycopy(order, 0, sendData, 0, order.length);
            System.arraycopy(gpsPac, 0, sendData, order.length, gpsPac.length);
            return sendData;
        }
    }

    public static TcpClient myTcpThread = null;

    public static TcpClient getInstance(final Context context) {
        if (myTcpThread == null) {
            myTcpThread = new TcpClient(DeviceParameter.getLoginIP(), DeviceParameter.getLoginPort(), context);
            //myTcpThread.handlerMain = handler;//new Handler(Looper.getMainLooper());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    myTcpThread.handler = new Handler(Looper.getMainLooper()) {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            switch (msg.what) {
                                case MessageDefine.MSG_COACH_LOGIN:
                                    Log.v(TAG, "receive UI Message: Coach signed");
                                    break;
                                case MessageDefine.MSG_UPLOAD_PROMOTE_PHOTO:
                                    isTakePhotoPrompt = true;
                                    break;
                                case MessageDefine.MSG_REFRESH_ORDERLIST://订单刷新
                                    isUpdateOrder = false;
                                    break;
                            }
                        }
                    };
                    Looper.loop();
                }
            }).start();
        }
        return myTcpThread;
    }

    public TcpClient(String address, int port, Context context) {
        hasBeenCreated = true;
        this.SEVER_IP = address;
        this.SEVER_PORT = port;
        this.context = context.getApplicationContext();
        //mHandler = new EevntHandler(Looper.getMainLooper());
        //this.uiHandler =;
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            initParameter(); //初始化设备网络参数
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        transState = new TransmissionState();
        curPhotoHead = new PhotoHead();
        curVideoHead = new VideoHead();
    }

    public void initParameter() throws UnsupportedEncodingException {
        //phoneNumber = StringUtils.HexStringToBytes("0000015813885976");
        phoneNumber = StringUtils.HexStringToBytes(DeviceParameter.getDeviceNumber());
        if (DeviceParameter.getDeviceNumber() != null)
            Log.e("Sim卡号", DeviceParameter.getDeviceNumber());
        plateNumber = DeviceParameter.getLoginPlate().getBytes("GBK");
        loginInfo = "123456".getBytes();
        MessageDefine.sendSequence = 1;
        workSequence = 1;
        File certiFile = new File(AppConfig.REGISTER_SAVE_PATH + File.separator + CERTIFICATION_NAME);
        if (CommonInfo.getCertificationPassword() == null || CommonInfo.getTelnetDeviceNumber() == null || !certiFile.exists()) {
            handlerState = EnumState.UNREGISTER;
            Log.i("zxj", "initParameter");
        } else {
            handlerState = EnumState.UNLOGIN;
        }
        tcpLinkInterval = 1000;
        tcpLastLinkTime = System.currentTimeMillis();

        CommonInfo.setDeviceLoginState(0);
        Select_DB selectable = new Select_DB();
        areasList = selectable.getFenceList();
        if (areasList != null && areasList.length > 0) {
            areasListCnt = areasList.length / 6;
            areasSendIndex = areasListCnt;
        }
        ordersList = selectable.getOrderList();
        if (ordersList != null && ordersList.length > 0) {
            ordersListCnt = ordersList.length / 8;
            ordersSendIndex = ordersListCnt;
        }
    }

    private int tcpLinkInterval;
    private long tcpLastLinkTime;

    public void connect() {
//        if(client != null && in_put != null && out_put != null){
//            return;
//        }
        clientting = false;
        CommonInfo.setDeviceLoginState(0);
        boolean isHadRegister = context.getSharedPreferences(SharedPreferencesUtil.REGISTER_INFO_FILE, MODE_PRIVATE)
                .getBoolean(SharedPreferencesUtil.REGISTER_SUCCESS, false);
        if (!isHadRegister) {
            handlerState = EnumState.UNREGISTER;
        } else {
            handlerState = EnumState.UNLOGIN;
        }
        while (!SocketOnLine) {
            if (tcpLastLinkTime + tcpLinkInterval < System.currentTimeMillis()) {
                tcpLastLinkTime = System.currentTimeMillis();
                tcpLinkInterval *= 2;
                if (tcpLinkInterval > 120000) {
                    tcpLinkInterval = 120000;
                }
                break;
            }
//            else {
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
        }
        try {
            SocketAddress socketaddress = new InetSocketAddress(this.SEVER_IP, this.SEVER_PORT);
            if (DEBUG) Log.i(TAG, "连接中……");
            client = new Socket();
            client.connect(socketaddress, 2000);
            // 发送数据包，默认为 false，即客户端发送数据采用 Nagle 算法；
            // 但是对于实时交互性高的程序，建议其改为 true，即关闭 Nagle 算法，客户端每发送一次数据，无论数据包大小都会将这些数据发送出去
            client.setTcpNoDelay(true);
            // 设置客户端 socket 关闭时，close() 方法起作用时延迟 30 秒关闭，如果 30 秒内尽量将未发送的数据包发送出去
            client.setSoLinger(true, 30);
            // 设置输出流的发送缓冲区大小，默认是4KB，即4096字节
            client.setSendBufferSize(4096);
            // 设置输入流的接收缓冲区大小，默认是4KB，即4096字节
            client.setReceiveBufferSize(4096);
            // 作用：每隔一段时间检查服务器是否处于活动状态，如果服务器端长时间没响应，自动关闭客户端socket
            // 防止服务器端无效时，客户端长时间处于连接状态
            client.setKeepAlive(true);
            // 代表可以立即向服务器端发送单字节数据
            client.setOOBInline(true);
            // 客户端向服务器端发送数据，获取客户端向服务器端输出流//
            out_put = client.getOutputStream();
            // 数据不经过输出缓冲区，立即发送
            //clientSocket.sendUrgentData(0x44);//"D"
            in_put = new BufferedInputStream(client.getInputStream());
            if (DEBUG) Log.i(TAG, "连接成功");
            //br = new InputStreamReader(client.getInputStream()));
            //br.mark(2048);
            if (DEBUG) Log.i(TAG, "输入输出流获取成功");
            //初始化连接时间参数
            tcpLinkState = true;
            tcpLinkInterval = 1000;
            tcpLastLinkTime = System.currentTimeMillis();
            clientting = true;
            SocketOnLine = true;
            MessageDefine.isNetwork = true;
        } catch (UnknownHostException e) {
            if (DEBUG) Log.i(TAG, "连接错误UnknownHostException 重新获取");
            sendMsgToUi(MessageDefine.MSG_UNKNOW_HOST_EXCEPTION);
            e.printStackTrace();
            set_clent();
            ReSocketContent();
        } catch (IOException e) {
            if (DEBUG) Log.i(TAG, "连接服务器io错误");
            sendMsgToUi(MessageDefine.MSG_CONNECT_IO_EXCEPTION);
            e.printStackTrace();
            set_clent();
            ReSocketContent();
        } catch (IllegalArgumentException ex) {
            if (DEBUG) Log.i(TAG, "无效服务配置参数，Exception：" + ex.getMessage());
            sendMsgToUi(MessageDefine.MSG_ILLEGAL_ARG_EXCEPTION);
            ex.printStackTrace();
            set_clent();
            ReSocketContent();
        } catch (Exception e) {
            if (DEBUG) Log.i(TAG, "连接服务器错误Exception" + e.getMessage());
            sendMsgToUi(MessageDefine.MSG_CONNECT_EXCETION);
            e.printStackTrace();
            set_clent();
            ReSocketContent();
        }
    }

    private long lastSendTcpData;
    private String sendingTrainListSerial;

    private void sendTcpBytes(byte[] bt, int offset, int count) {

//        if (client == null) {
//            if (DEBUG)  Log.i(TAG, "连接无效");
//            return;
//        }
//        if (!client.isBound()) {
//            if (DEBUG)  Log.i(TAG, "没有绑定本地地址");
//            return;
//        }
//        if (client.isClosed()) {
//            if (DEBUG)  Log.i(TAG, "连接已关闭");
//            return;
//        }
//        if (client.isOutputShutdown()) {
//            if (DEBUG)  Log.i(TAG, "输出流已关闭");
//            return;
//        }
        try {
            Log.i("zxj", "send data");
            Log.e(TAG, "发送数据：" + StringUtils.bytesToHexString(bt));

            lastSendTcpData = System.currentTimeMillis();
            out_put.write(bt, offset, count);
        } catch (IOException e) {
            Log.e(TAG, "连接出错，重新尝试连接...");
            e.printStackTrace();

            ReSocketContent();
        }
    }

    public void sendTcpBytes(byte[] bt) {

        if (bt == null || bt.length == 0)
            return;
        sendTcpBytes(bt, 0, bt.length);
    }


    public void disConnect() {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取指定文件大小
     *
     * @param
     * @return
     * @throws Exception
     */
    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
        }
        return size;
    }

    /**
     * 随机读取文件内容
     *
     * @param fileName 文件名
     */
    private static byte[] readFileByRandomAccess(String fileName, long offset, int readLength) {
        RandomAccessFile randomFile = null;
        try {
            System.out.println("随机读取一段文件内容：");
            Utils.saveRunningLog("readFileByRandomAccess: " + fileName);
            // 打开一个随机访问文件流，按只读方式
            randomFile = new RandomAccessFile(fileName, "r");
            // 文件长度，字节数
            long fileLength = randomFile.length();
            // 读文件的起始位置
            long beginIndex = (fileLength > offset) ? offset : 0;
            //将读文件的开始位置移到beginIndex位置。
            randomFile.seek(beginIndex);
            byte[] bytes = new byte[readLength];
            int byteread = 0;
            byteread = randomFile.read(bytes);
            if (byteread != -1) {
                return bytes;
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (randomFile != null) {
                try {
                    randomFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private boolean testRsa(byte[] deviceID, long timestamp) {
        String filePath = AppConfig.REGISTER_SAVE_PATH + File.separator + CERTIFICATION_NAME;

        //RSAEncrypt.genKeyPair(filepath);
        try {
            String password = "pbpOW2Gqvxfp";
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            InputStream input = new FileInputStream(filePath);
            keyStore.load(input, password.toCharArray());
            Enumeration<String> aliases = keyStore.aliases();
            if (!aliases.hasMoreElements()) {
                throw new RuntimeException("no alias found");
            }
            String alias = aliases.nextElement();
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, password.toCharArray());
            X509Certificate cert = (X509Certificate) keyStore.getCertificate(alias);
            String data = "6567549541680453";
            timestamp = 1479450299;
            ISign sign = new Sign();
            String sign_hex = sign.sign(data, timestamp, privateKey);

            System.out.println("Password:");//18509202598
            System.out.println(password);
            System.out.println("Data:");
            System.out.println(data);
            System.out.println("Timestamp");
            System.out.println("" + timestamp);
            System.out.println("SignedData");
            System.out.println(sign_hex);

            IVerify verify = new Verify();
            boolean ok = verify.verify(data, timestamp, sign_hex, cert);
            System.out.println(ok);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private char[] getChars(byte[] bytes) {
        Charset cs = Charset.forName("UTF-8");
        ByteBuffer bb = ByteBuffer.allocate(bytes.length);
        bb.put(bytes);
        bb.flip();
        CharBuffer cb = cs.decode(bb);

        return cb.array();
    }


    private byte[] getRsaSignedData(byte[] deviceID, long tempTime) {
        try {
            String filePath = AppConfig.REGISTER_SAVE_PATH + File.separator + CERTIFICATION_NAME;
            // String cadata = RSAEncrypt.loadPublicKeyByFile(filePath); //"全国平台返回的终端证书（base64编码）";
            String password = CommonInfo.getCertificationPassword();
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            InputStream input = new FileInputStream(filePath);
            keyStore.load(input, password.toCharArray());
//            keyStore.load(new ByteArrayInputStream(cabuf), password.toCharArray());
            Enumeration<String> aliases = keyStore.aliases();
            if (!aliases.hasMoreElements()) {
                throw new RuntimeException("no alias found");
            }
            String alias = aliases.nextElement();
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, password.toCharArray());
            System.out.println("privateKey");
            System.out.println("privateKey" + privateKey.toString() + privateKey.getAlgorithm());
            Sign sign = new Sign();
            String result = sign.sign(deviceID, tempTime, privateKey);
            return HexBin.decode(result);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private byte[] getRsaEncryptData(byte[] pdata) {
        try {
            String filePath = AppConfig.REGISTER_SAVE_PATH + File.separator + CERTIFICATION_NAME;
            // String cadata = RSAEncrypt.loadPublicKeyByFile(filePath); //"全国平台返回的终端证书（base64编码）";
            String password = CommonInfo.getCertificationPassword();
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            InputStream input = new FileInputStream(filePath);
            keyStore.load(input, password.toCharArray());
            Enumeration<String> aliases = keyStore.aliases();
            if (!aliases.hasMoreElements()) {
                throw new RuntimeException("no alias found");
            }
            String alias = aliases.nextElement();
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, password.toCharArray());
            Sign sign = new Sign();
            String result = sign.sign(pdata, 0, privateKey);
            return HexBin.decode(result);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    long tempSeconds;

    public Handler getHandle() {
        return handler;
    }

    int seleck = 0;

    @Override
    public void run() {
        super.run();
        byte[] read_byte = new byte[SOCKET_MAX_SIZE];
        int ret_value = 0;
        read_byte.toString();

        if (DEBUG) Log.i(TAG, "线程socket开始运行");
        connect();
        //发送数据任务
        new Thread() {
            @Override
            public void run() {
                super.run();
                long lastTimeOfCheckDB = 0;
                byte[] sendData;
                long lPreMillSecond = 0;
                long lPreMillSecond1 = 0;
                boolean isLogcat = false;
                long sendPicTime;
                boolean isUpdateArea = false;
                //定时处理标志
                long preUpdateOrdersCnt = 0;
                long preUpDroplineGPS = 0;
                boolean isTraining = true;
                SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
                sendPicTime = System.currentTimeMillis();
                while (IUtil.WhileB) {
//                    Log.e(TAG, "发送数据：");
                    if (!SocketOnLine && !send8003) {
                        try {
                            sleep(5 * 1000);
                            ThisLog("发送暂停");

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }
                    try {
//                            ThisLog(""+transState.getState());
//                            ThisLog(""+transState.WAIT_ACK);
//                            ThisLog(""+transState.USING);
                        if ((transState.getState() & transState.WAIT_ACK) != 0 || (transState.getState() & transState.USING) != 0) {
                            Log.i("zxj", "wait_jump");

                            //断网重连之后数据赌赛
                            if (sendif == transState.getState()) {
                                resend++;
                                Log.e(TAG, "" + resend);
                                if (resend > 20) {
                                    if (transState.getState() == 1) {
                                        transState.clearState(transState.WAIT_ACK);
                                    } else if (transState.getState() == 4) {
                                        transState.clearState(transState.USING);
                                    }
                                    resend = 0;
                                    sendRunState = SendRunState.SENDCOACH;
                                    ThisLog("清理一次数据————————————");
                                }
                            } else {
                                resend = 0;
                                sendif = transState.getState();
                            }

                            if (!isLogcat) {
                                if (DEBUG) Log.i(TAG, "正在等待应答数据");
                                isLogcat = true;
                            }
                            Thread.sleep(500);
                            continue;
                        } else {
                            isLogcat = false;
                            resend = 0;
                        }
                        if (System.currentTimeMillis() - lPreMillSecond1 > 1000) {
                            lPreMillSecond1 = System.currentTimeMillis();
                        }
                        if (handlerState == EnumState.UNREGISTER) {
                            //终端注册
                            byte[] sendBytes = getCmdData(GPRS_ID_CMD0100, 0);
                            sendTcpBytes(sendBytes);
                            transState.setState(GPRS_ID_CMD0100, 0, transState.WAIT_ACK);
                        } else if (handlerState == EnumState.REGCOMPLICT && MessageDefine.MSG_TERMINAL_CANCEL == 1) {
                            byte[] sendBytes = getCmdData(GPRS_ID_CMD0003, 0);
                            sendTcpBytes(sendBytes);
                            transState.setState(GPRS_ID_CMD0003, 0, transState.WAIT_ACK);
                            MessageDefine.MSG_TERMINAL_CANCEL = 0;
                        } else if (handlerState == EnumState.UNLOGIN) {
                            tempSeconds = System.currentTimeMillis() / 1000;
                            byte[] signdata;
                            byte[] sendSignData;
                            byte[] sendBytes;
                            String deviceID;
                            //鉴权时对证书的判断可以去掉（注册之后，已做判断）

                            if (CommonInfo.getTelnetDeviceNumber() != null && CommonInfo.getCertificationPassword() != null) {
                                //deviceID = new String(CommonInfo.getTelnetDeviceNumber());
//                                testRsa("1".getBytes(),1);
                                signdata = getRsaSignedData(CommonInfo.getTelnetDeviceNumber().getBytes("utf-8"), tempSeconds);
                                if (signdata != null) {
                                    sendBytes = getCmdData(GPRS_ID_CMD0102, 0, (byte) 1, signdata, 0);
                                    sendTcpBytes(sendBytes);
                                    transState.setState(GPRS_ID_CMD0102, 0, transState.WAIT_ACK);
                                    handlerState = EnumState.SLEEP;
                                } else {
                                    handlerState = EnumState.UNREGISTER; //重新注册
                                }
                            } else {
                                handlerState = EnumState.UNREGISTER; //重新注册
                            }

                        } else if (handlerState == EnumState.SLEEP) {//鉴权之后
                            if ((transState.getState() & transState.SEND_ACK) != 0 && transState.getSendAckSubCmd() == 0x0900) {
                                int sendCmd = transState.getSendAckSubCmd();
                                byte[] sendAckData = getCmdData(0x0900, sendCmd, (byte) 0, null, 0);
                                sendTcpBytes(sendAckData);
                                transState.clearState(transState.SEND_ACK);
                            } else if (sendOrderState) {// 上报订单状态
                                byte[] orderPackage = getOrderStatePackage();
                                if (orderPackage != null) {
                                    byte[] sendBytes = getCmdData(0x0900, GD_UP_ORDER_STATE, (byte) 1, orderPackage, 0);
                                    sendTcpBytes(sendBytes);
                                    transState.setState(0x8900, GD_UP_ORDER_STATE, transState.WAIT_ACK);
                                    if (DEBUG) Log.i(TAG, "上报订单状态...");
                                }
                                sendOrderState = false;
                            } else if (isPromotTakePhoto && System.currentTimeMillis() - takePhotoTime > 4000 && (myPhotoHeadInfo == null || myPhotoHeadInfo.size() == 0)) { //立即上传照片
                                Select_DB selectable = new Select_DB();
                                myPhotoHeadInfo = selectable.getPiturePath(2);
                                if (myPhotoHeadInfo == null && System.currentTimeMillis() - takePhotoTime > 20000) {
                                    sendMsgToUi(10086);
                                    Utils.saveRunningLog("Uploading photo for ok");
                                    isPromotTakePhoto = false;
                                }
                                if (myPhotoHeadInfo != null && myPhotoHeadInfo.size() > 0) {
                                    takePhotoTime = System.currentTimeMillis() - 20000;
                                    Utils.saveRunningLog("Uploading photo for immediatly");
                                    uploadPhoto();
                                    Log.d(TAG, "上传照片成功");
                                }
                            } else if (lastTimeOfCheckDB < System.currentTimeMillis()) {//send basedata train list
                                Select_DB selectable = new Select_DB();
                                byte[] sendBytes;
                                byte[] protocolData = null;
//                                IUtil.signnumStudent = selectable.getDBStudentSignNun();
//                                IUtil.signnumTeacher = selectable.getDBTeacherSignNun();
//                                IUtil.getphotoNun = selectable.getphotoNun();
//                                IUtil.getlocationNun = selectable.getlocationNun();
//                                IUtil.getclassSignNun = selectable.getclassSignNun();
                                switch (seleck) {
                                    case 0:
                                        IUtil.signnumStudent = selectable.getDBStudentSignNun();
                                        seleck++;
                                        Thread.sleep(200);
                                        break;
                                    case 1:
                                        IUtil.signnumTeacher = selectable.getDBTeacherSignNun();
                                        seleck++;
                                        Thread.sleep(200);
                                        break;
                                    case 2:
                                        IUtil.getphotoNun = selectable.getphotoNun();
                                        seleck++;
                                        Thread.sleep(200);
                                        break;
                                    case 3:
                                        IUtil.getlocationNun = selectable.getlocationNun();
                                        seleck++;
                                        Thread.sleep(200);
                                        break;
                                    case 4:
                                        IUtil.getclassSignNun = selectable.getclassSignNun();
                                        seleck++;
                                        Thread.sleep(200);
                                        break;
                                    default:
                                        seleck = 0;
                                        break;

                                }
//                                seleck += 1;
                                switch (sendRunState) {
                                    case SENDCOACH://教练签到
                                        info = selectable.getDBCoachSignedData(1);
                                        if (info != null) {
                                            info.getNumber();
                                            info.getIdentifyNumber();
                                            info.getTeachCarType();
                                            if (1 == info.getType())//签到
                                            {
                                                if (DEBUG) Log.i(TAG, "获取教练签到信息");
                                                sendBytes = new byte[64];
                                                System.arraycopy(info.getNumber().getBytes(), 0, sendBytes, 0, 16);
                                                System.arraycopy(info.getIdentifyNumber().getBytes(), 0, sendBytes, 16, 18);
                                                System.arraycopy(info.getTeachCarType().getBytes(), 0, sendBytes, 34, 2);
                                                System.arraycopy(info.getGpsDate(), 0, sendBytes, 36, 28);
                                                //System.arraycopy(CommonInfo.getGpsData(), 0, sendBytes, 36, 28);
                                                protocolData = getCmdData(0x0900, GD_UP_COACH_LOGIN, (byte) 1, sendBytes, info.getIsBlindArea());
                                                transState.setState(0x8900, 0x8101, transState.WAIT_ACK);

                                            } else { //签退0
                                                if (DEBUG) Log.i(TAG, "获取教练签退信息");
                                                sendBytes = new byte[44];
                                                System.arraycopy(info.getNumber().getBytes(), 0, sendBytes, 0, 16);
                                                System.arraycopy(info.getGpsDate(), 0, sendBytes, 16, 28);
                                                //System.arraycopy(CommonInfo.getGpsData(), 0, sendBytes, 16, 28);
                                                protocolData = getCmdData(0x0900, GD_UP_COACH_LOGOUT, (byte) 1, sendBytes, info.getIsBlindArea());
                                                transState.setState(0x8900, 0x8102, transState.WAIT_ACK);
                                            }
                                            coahBool = true;
                                            sendRunState = SendRunState.STULOG;
                                            sendTime = 2000;
                                        } else {
                                            //教练签到之后，会清除存储的数据
                                            coahBool = false;
                                            sendRunState = SendRunState.STULOG;
                                            Log.d("TCP", "无教练员信息");
                                        }
                                        break;

                                    case STULOG:
                                        if (isStuLogin) {
                                            stuInfo = selectable.getDBStuSignedData(1, 0L);
                                            if (stuInfo != null) {
                                                Log.e(TAG, "run: 发送学员登录");
                                                if (DEBUG) Log.i(TAG, "获取学员签到信息");
                                                sendBytes = new byte[88];
                                                System.arraycopy(stuInfo.getStuNumber().getBytes(), 0, sendBytes, 0, 16);
                                                System.arraycopy(stuInfo.getCoachNumber().getBytes(), 0, sendBytes, 16, 16);
                                                System.arraycopy(stuInfo.getCourse(), 0, sendBytes, 32, 5);
                                                long classid = stuInfo.getClassID();
                                                currentClassId = classid;
                                                sendBytes[37] = (byte) (classid >> 24);
                                                sendBytes[38] = (byte) (classid >> 16);
                                                sendBytes[39] = (byte) (classid >> 8);
                                                sendBytes[40] = (byte) (classid);
                                                System.arraycopy(stuInfo.getGpsDate(), 0, sendBytes, 41, 28);
                                                //System.arraycopy(CommonInfo.getGpsData(), 0, sendBytes, 41, 28);
                                                //sendBytes[69] = (byte) 18;
                                                //System.arraycopy(CommonInfo.getGpsAddition(), 0, sendBytes, 70, 18);
                                                protocolData = getCmdData(0x0900, GD_UP_STUDENT_LOGIN, (byte) 1, sendBytes, stuInfo.getIsBlindArea());
                                                transState.setState(0x8900, 0x8201, transState.WAIT_ACK);
                                                stuBool = true;
                                                sendRunState = SendRunState.SENDTRAIN;
                                                sendTime = 2000;
                                            } else {
                                                sendRunState = SendRunState.SENDTRAIN;
                                                Log.d("TCP", "无学员签到信息");
                                            }
                                        } else {
                                            stuInfo = selectable.getDBStuSignedData(2, currentClassId);
                                            if (stuInfo != null) {
                                                Log.e(TAG, "run: 发送学员登出");
                                                if (DEBUG) Log.i(TAG, "获取学员签退信息");
                                                sendBytes = new byte[58];
                                                System.arraycopy(stuInfo.getStuNumber().getBytes(), 0, sendBytes, 0, 16);
                                                String sTime = sdf.format(stuInfo.getLogoutDate());
                                                byte[] hexTime = StringUtils.HexStringToBytes(sTime);
                                                System.arraycopy(hexTime, 0, sendBytes, 16, 6);
                                                int learnedMinutes = stuInfo.getMinutes();
                                                sendBytes[22] = (byte) (learnedMinutes >> 8);
                                                sendBytes[23] = (byte) learnedMinutes;
                                                int learnedMiles = stuInfo.getMiles();
                                                sendBytes[24] = (byte) (learnedMiles >> 8);
                                                sendBytes[25] = (byte) learnedMiles;
                                                long classid = stuInfo.getClassID();
                                                sendBytes[26] = (byte) (classid >> 24);
                                                sendBytes[27] = (byte) (classid >> 16);
                                                sendBytes[28] = (byte) (classid >> 8);
                                                sendBytes[29] = (byte) (classid);
                                                System.arraycopy(stuInfo.getGpsDate(), 0, sendBytes, 30, 28);
                                                //System.arraycopy(CommonInfo.getGpsData(), 0, sendBytes, 30, 28);
                                                protocolData = getCmdData(0x0900, GD_UP_STUDENT_LOGOUT, (byte) 1, sendBytes, stuInfo.getIsBlindArea());
                                                transState.setState(0x8900, 0x8202, transState.WAIT_ACK);
                                                sendRunState = SendRunState.SENDTRAIN;
                                                sendTime = 2000;
                                            }else {
                                                sendRunState = SendRunState.SENDTRAIN;
                                                Log.d("TCP", "无学员签退信息");
                                            }
                                        }
                                        break;

//                                    case SENDSTULOGIN://学员签到
//                                        if (isStuLogin) {
//                                            stuInfo = selectable.getDBStuSignedData(1);
//                                            if (stuInfo != null) {
//                                                if (DEBUG) Log.i(TAG, "获取学员签到信息");
//                                                sendBytes = new byte[88];
//                                                System.arraycopy(stuInfo.getStuNumber().getBytes(), 0, sendBytes, 0, 16);
//                                                System.arraycopy(stuInfo.getCoachNumber().getBytes(), 0, sendBytes, 16, 16);
//                                                System.arraycopy(stuInfo.getCourse(), 0, sendBytes, 32, 5);
//                                                long classid = stuInfo.getClassID();
//                                                sendBytes[37] = (byte) (classid >> 24);
//                                                sendBytes[38] = (byte) (classid >> 16);
//                                                sendBytes[39] = (byte) (classid >> 8);
//                                                sendBytes[40] = (byte) (classid);
//                                                System.arraycopy(stuInfo.getGpsDate(), 0, sendBytes, 41, 28);
//                                                //System.arraycopy(CommonInfo.getGpsData(), 0, sendBytes, 41, 28);
//                                                //sendBytes[69] = (byte) 18;
//                                                //System.arraycopy(CommonInfo.getGpsAddition(), 0, sendBytes, 70, 18);
//                                                protocolData = getCmdData(0x0900, GD_UP_STUDENT_LOGIN, (byte) 1, sendBytes, stuInfo.getIsBlindArea());
//                                                transState.setState(0x8900, 0x8201, transState.WAIT_ACK);
//                                                stuBool = true;
//                                                sendRunState = SendRunState.SENDSTUOUT;
//                                                sendTime = 2000;
//                                            } else {
//                                                stuBool = false;
//                                                sendRunState = SendRunState.SENDSTUOUT;
//                                                Log.d("TCP", "无学签到员信息");
//                                            }
//                                        }
//                                        isStuLogin = false;
//                                        break;
//                                    case SENDSTUOUT://学院签退
//                                        if (!isStuLogin) {
//                                            stuInfo = selectable.getDBStuSignedData(2);
//                                            if (stuInfo != null) {
//                                                if (DEBUG) Log.i(TAG, "获取学员签退信息");
//                                                sendBytes = new byte[58];
//                                                System.arraycopy(stuInfo.getStuNumber().getBytes(), 0, sendBytes, 0, 16);
//                                                String sTime = sdf.format(stuInfo.getLogoutDate());
//                                                byte[] hexTime = StringUtils.HexStringToBytes(sTime);
//                                                System.arraycopy(hexTime, 0, sendBytes, 16, 6);
//                                                int learnedMinutes = stuInfo.getMinutes();
//                                                sendBytes[22] = (byte) (learnedMinutes >> 8);
//                                                sendBytes[23] = (byte) learnedMinutes;
//                                                int learnedMiles = stuInfo.getMiles();
//                                                sendBytes[24] = (byte) (learnedMiles >> 8);
//                                                sendBytes[25] = (byte) learnedMiles;
//                                                long classid = stuInfo.getClassID();
//                                                sendBytes[26] = (byte) (classid >> 24);
//                                                sendBytes[27] = (byte) (classid >> 16);
//                                                sendBytes[28] = (byte) (classid >> 8);
//                                                sendBytes[29] = (byte) (classid);
//                                                System.arraycopy(stuInfo.getGpsDate(), 0, sendBytes, 30, 28);
//                                                //System.arraycopy(CommonInfo.getGpsData(), 0, sendBytes, 30, 28);
//                                                protocolData = getCmdData(0x0900, GD_UP_STUDENT_LOGOUT, (byte) 1, sendBytes, stuInfo.getIsBlindArea());
//                                                transState.setState(0x8900, 0x8202, transState.WAIT_ACK);
//                                                stuOutBool = true;
//                                                sendRunState = SendRunState.SENDTRAIN;
//                                                sendTime = 2000;
//                                            } else {
//                                                stuOutBool = false;
//                                                sendRunState = SendRunState.SENDTRAIN;
//                                                Log.d("TCP", "无学员签退信息");
//                                            }
//                                        }
//                                        isStuLogin = true;
//                                        break;
                                    case SENDTRAIN://上传培训记录
                                        Log.e(TAG, "run: 上传培训记录,");
                                        TrainingRecord tr = selectable.getDBTrainlistData();
                                        if (tr != null) {
                                            sendingTrainListSerial = tr.getTraining_id();
                                            sendBytes = getTrainRecordData(tr, 1);
                                            if (sendBytes != null) {
                                                if (DEBUG) Log.i(TAG, "获取培训记录信息");
                                                protocolData = getCmdData(0x0900, GD_UP_TRAIN_LIST, (byte) 1, sendBytes, tr.getIsBlindArea());
                                                transState.setState(0x8900, 0x8203, transState.WAIT_ACK);
                                                trainBool = true;
                                                sendRunState = SendRunState.SELPHOTO;
                                                sendTime = 5000;
                                            }
                                        } else {
                                            trainBool = false;
                                            sendRunState = SendRunState.SELPHOTO;
                                            Log.d("TCP", "无教培训记录信息");
                                        }
                                        break;
//                                        case 5: //上传行为数据
//                                            sendBytes = selectable.getDBDrivingData();
//                                            if (sendBytes != null) {
//                                                if (DEBUG) Log.i(TAG, "获取驾驶行为数据");
//                                                protocolData = getCmdData(0x0900, GD_UP_DRIVING_BEHAVIOR, (byte) 1, sendBytes,0);
//                                            }
//                                            break;
                                    case SELPHOTO://查询照片列表
                                        if (myPhotoHeadInfo == null || myPhotoHeadInfo.size() == 0) {
                                            myPhotoHeadInfo = selectable.getPiturePath(1);
                                        }
                                        sendRunState = SendRunState.SENDPHOTO;
                                        break;
//                                        case 7://
//                                            if (myVideoHeadInfo == null || myVideoHeadInfo.size() == 0) {
//                                                myVideoHeadInfo = selectable.getVideosPath(1, 1);
//                                            }
//                                            break;
                                    case SENDPOSITION:
                                        // if (isTraining && System.currentTimeMillis() - lastSendTcpData > 30000
                                        //        || !isTraining && System.currentTimeMillis() - preUpDroplineGPS > DeviceParameter.getUpGpsIntervalOff() * 1000)//至少30秒发送一次数据
                                        //{
//                                                preUpDroplineGPS = System.currentTimeMillis();
                                        //sendTcpBytes(getCmdData(GPRS_ID_CMD0200)); //发送位置信息
                                        //protocolData = getCmdData(GPRS_ID_CMD0200);
                                        String positiondata[] = selectable.getDBPositionListData();
                                        if (null != positiondata && positiondata.length != 0) {
                                            positionId = Integer.parseInt(positiondata[0]);
                                            protocolData = StringUtils.HexStringToBytes(positiondata[1]);
                                            transState.setState(GPRS_ID_CMD0200, 0, transState.WAIT_ACK);
                                            sendTime = 2000;
                                            sendRunState = SendRunState.SENDCOACH;
                                            positionBool = true;
                                        } else {
                                            positionBool = false;
                                            sendRunState = SendRunState.SENDCOACH;
                                            Log.d("TCP", "无轨迹信息");
                                        }

                                        //  }
                                        break;

                                    case SENDPHOTO:
                                        if (DeviceParameter.getUpPhotoMode() == 0x01 && myPhotoHeadInfo != null && myPhotoHeadInfo.size() > 0) {
                                            Utils.saveRunningLog("Uploading photo for cache saved");
                                            uploadPhoto();//上传缓存照片
//                                                if(myPhotoHeadInfo.size()>1) {
                                            sendTime = 5000;
//                                                }else{
//                                                    sendTime = 5000;
//                                                }
                                            sendRunState = SendRunState.SELPHOTO;

                                            photoBool = true;
                                        } else {
                                            photoBool = false;
                                            sendRunState = SendRunState.SENDPOSITION;
                                            Log.d("TCP", "无照片信息");
                                        }

                                        break;
//                                        case 10:
//                                            if (DeviceParameter.getUpVideoMode() == 0x01 && !isSendingVideo //避免重复上传输频文件
//                                                    && myVideoHeadInfo != null && myVideoHeadInfo.size() > 0) {
//                                                FileUtils.saveRunningLog("Uploading video for cache saved");
//                                                uploadVideo();//上传视频文件
//                                            }
//                                            break;
                                    default:
//                                            sendBytes = null;
                                        break;
                                }
//                                    if (protocolData != null) {
//                                        break;
//                                    }
//                                }
                                if (protocolData != null) {
                                    sendTcpBytes(protocolData);
                                    sendDBDataTimeMillis = System.currentTimeMillis();
                                    lastTimeOfCheckDB = System.currentTimeMillis() + sendTime;//正常传输2秒一次
                                } else {
//                                    if (DEBUG) Log.i(TAG, "无积压数据");
//                                    sendMsgToUi(198802);
                                    if (!coahBool && !stuBool && !stuOutBool && !trainBool && !positionBool && !photoBool) {
//                                        lastTimeOfCheckDB = System.currentTimeMillis() + 1000;//为空时3秒查询一次
                                        lastTimeOfCheckDB = System.currentTimeMillis();//处理位置信息10s发送一次
                                    } else {
                                        lastTimeOfCheckDB = System.currentTimeMillis();
                                    }
                                }
                            } else {
                                Thread.sleep(100);
                            }
                        } else if (client == null || client.isClosed()) {
                            Thread.sleep(5000);
                            // do nothing
                        } else if (client.getKeepAlive())//断开连接
                        {
                            disConnect();
                            if (DEBUG) Log.i(TAG, "设备参数无效，主动断开连接");
                        } else {

                        }
                    } catch (InterruptedException ex) {
                        System.out.println(ex.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        if (DEBUG) Log.i(TAG, "读取数据错误" + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }.start();

//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                Looper.prepare();
//                handler = new Handler() {
//                    @Override
//                    public void handleMessage(Message msg) {
//                        super.handleMessage(msg);
//                        //byte[] sendBytes;
//                        switch(msg.what)
//                        {
//                            case MessageDefine.MSG_REFRESH_ORDERLIST:
//                                isUpdateOrder =false;
//                                break;
//
//                        }
//                        if (DEBUG)  Log.i(TAG, "收到主线程消息" + "Message what:" + msg.what + " Mwssage arg1:" + msg.arg1 + " Content:" + msg.obj.toString());
//                    }
//                };
//                Looper.loop();
//            }
//        }.start();

        //数据接收任务
        if (DEBUG) Log.i(TAG, "receive Thread is running");
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int readByte;
        while (IUtil.WhileB) {
            Log.e(TAG, "接收数据：");
            if (!SocketOnLine) {
                try {
                    sleep(5 * 1000);
                    ThisLog("sleep 5000");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            if (tcpLinkState) {
                int len = 0;
                int read_state = 0;
                try {
                    //len = in_put.read(read_byte);
                    while ((readByte = in_put.read()) >= 0) {
                        read_byte[len++] = (byte) readByte;
                        if (readByte == 0x7E) {
                            if (len > 2) {
                                break;
                            } else {
                                len = 1;
                            }
                        } else {
                            if (len == 1) {
                                len = 0;
                            }
                        }
                    }
                } catch (IOException e) {
                    if (DEBUG) Log.i(TAG, e.toString() + "读取数据异常");
                }
                if (len > 0) {
                    //分包处理
                    String printout = StringUtils.bytesToHexString(read_byte, 0, len);
                    Log.e(TAG, "接收数据：" + printout);
                    resend = 0;
                    parseReceivePackage(read_byte, len);
                }
//                else if (len == 0) {
//                    if (DEBUG)  Log.i(TAG, "没有可接收数据");
//                }
                else {
                    try {
                        client.sendUrgentData(0xFF);
                    } catch (IOException e) {
                        //connect();
                        tcpLinkState = false;
                        SocketOnLine = false;
                        Log.e(TAG, e.toString() + "0xFF连接断开，准备重新连接");
                        ReSocketContent();
                    }
                }
            } else {
                try {
                    Thread.sleep(1000);
                    //Thread.yield();
                } catch (InterruptedException e) {
                    e.printStackTrace();

                }
            }
        }
    }

    // TODO: 2019-12-19 上传照片
    void uploadPhoto() {
        isSendPic = true;
        PhotoHeadInformation ptInfo;
        ptInfo = myPhotoHeadInfo.get(0);
        File myPhoto = new File(ptInfo.getSavePath());
        if (!myPhoto.exists()) {
            Update_DB datebase_update = new Update_DB();
            curPhotoHead.photoSerial = ptInfo.getTakeID();
            datebase_update.clearPhotoHeadFlag(StringUtils.bytesToHexString(curPhotoHead.photoSerial));
            if (myPhotoHeadInfo != null && myPhotoHeadInfo.size() > 0) {
                myPhotoHeadInfo.remove(0);
                SharedPreferences sendMessageCount = context.getSharedPreferences("SendMessageCount", MODE_PRIVATE);
                SharedPreferences.Editor sendMessageEditor = sendMessageCount.edit();
                int phoptCount = sendMessageCount.getInt("PhoptCount", 0);
                sendMessageEditor.putInt("PhoptCount", phoptCount - 1);
                sendMessageEditor.commit();
                sendMsgToUi(10086);
            }
            if (DEBUG)
                Log.i(TAG, "删除无效照片ID：" + StringUtils.bytesToHexString(curPhotoHead.photoSerial));
        } else {
            try {
                curPhotoHead.fileSize = getFileSize(myPhoto);
            } catch (Exception e) {
                e.printStackTrace();
                if (DEBUG) Log.i(TAG, "获取照片文件大小失败!");
            }
            curPhotoHead.totalPackages = (int) (curPhotoHead.fileSize / PHOTO_PACKAGE_SIZE);
            if (curPhotoHead.fileSize % PHOTO_PACKAGE_SIZE != 0) {
                curPhotoHead.totalPackages++;
            }
            curPhotoHead.upMode = ptInfo.getUpMode();
            curPhotoHead.channel = ptInfo.getChannel();
            curPhotoHead.size = ptInfo.getSize();
            curPhotoHead.eventType = ptInfo.getEventType();//定时拍照
            curPhotoHead.classID = ptInfo.getClassID();
            curPhotoHead.stuNumber = ptInfo.getStuNumber().getBytes();
            Log.d(TAG, "照片学员编号：" + ptInfo.getStuNumber());
            //curPhotoHead.stuNumber = "3400452633643758".getBytes();
            curPhotoHead.photoSerial = ptInfo.getTakeID();
            curPhotoHead.filePath = ptInfo.getSavePath();
            curPhotoHead.isBlindArea = ptInfo.getIsBlindArea();
            byte[] picHead = CommonInfo.getPictureHeadInfo(curPhotoHead, ptInfo.getGpsInfo());
            byte[] sendBytes = getCmdData(0x0900, GD_UP_PHOTO_REQUEST, (byte) 1, picHead, ptInfo.getIsBlindArea());
            sendTcpBytes(sendBytes);
            transState.setState(0x8900, GD_UP_PHOTO_REQUEST, transState.WAIT_ACK);
            Log.d(TAG, "照片编号" + new String(curPhotoHead.photoSerial));
            Utils.saveRunningLog("Ask to upload photo: " + StringUtils.bytesToHexString(curPhotoHead.photoSerial));
            if (DEBUG) Log.i(TAG, "请求上传照片");
        }
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

    private byte[] getValueData(byte[] bytes) {
        int cnt = 0;
        byte xor = 0;

        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == 0x7D) {
                if (bytes[i + 1] == 0x01) {
                    bytes[cnt++] = 0x7D;
                    xor ^= 0x7D;
                } else if (bytes[i + 1] == 0x02) {
                    bytes[cnt++] = 0x7E;
                    xor ^= 0x7E;
                }
                i++;
            } else {
                bytes[cnt++] = bytes[i];
                xor ^= bytes[i];
            }
        }
        if (xor == 0 && cnt > 1) {
            byte[] retData = new byte[cnt - 1];
            System.arraycopy(bytes, 0, retData, 0, retData.length);
            return retData;
        } else {
            return null;
        }
    }

    // 0 成功应道
    // 1 失败
    // 2 消息有误
    // 3 不支持
    // 4 报警处理确认
    public void handlerCmd8001(int sequ, int recvCmd, int result) {
        switch (recvCmd) {
            case GPRS_ID_CMD0002:           //心跳
                break;
            case GPRS_ID_CMD0003:           //注销成功否？
                if (result == 0) {
                    if (DEBUG) Log.i(TAG, "**********成功注销设备********");
                    setHandlerMain(null);
//                     disConnect();
//                    handlerState = EnumState.UNREGISTER;
                    CommonInfo.setDeviceLoginState(0);
                    TimerDerminalEvent event = new TimerDerminalEvent();
                    event.setObj(13);
                    EventBus.getDefault().post(event);

                } else {
                    if (DEBUG) Log.i(TAG, "**********设备注销失败********");
//                    handlerState = EnumState.UNREGISTER;
                    CommonInfo.setDeviceLoginState(0);
                }
                break;
            case GPRS_ID_CMD0102:                              //鉴权成功否？
                if (result == 0) {
                    if (DEBUG) Log.i(TAG, "**********终端鉴权成功********");
//                    if(isfirst==0)
//                    {
//                        MessageDefine.MSG_TERMINAL_CANCEL=1;
//                        isfirst=1;
//                    }else {
                    if (tcpLinkState) {
                        TimerDerminalEvent event = new TimerDerminalEvent();
                        event.setObj(0);
                        EventBus.getDefault().post(event);
                    }
                    CommonInfo.setDeviceLoginState(1);
                    handlerState = EnumState.SLEEP;
//                    }
                } else {
                    if (DEBUG) Log.i(TAG, "**********终端鉴权失败********");
                    handlerState = EnumState.UNREGISTER;
                    CommonInfo.setDeviceLoginState(0);
                }
                //鉴权之后发送位置信息
                sendRunState = SendRunState.SENDPOSITION;
                break;
            case GPRS_ID_CMD0200:
                Log.d("TCP", "点位接收成功！");
                if (result == 0) {
                    Update_DB datebase_update = new Update_DB();
                    //更改位置信息状态
                    datebase_update.clearPositionFlag(positionId);
                    datebase_update.deletePositionData(positionId);
                    SharedPreferences sendMessageCount = context.getSharedPreferences("SendMessageCount", MODE_PRIVATE);
                    SharedPreferences.Editor sendMessageEditor = sendMessageCount.edit();
                    int positionCount = sendMessageCount.getInt("PositionCount", 0);
                    if (positionCount > 0) {
                        sendMessageEditor.putInt("PositionCount", positionCount - 1);
                        sendMessageEditor.commit();
                    }
                    Log.d(TAG, "更新点位信息成功");
                }
                break;
            case GPRS_ID_CMD0900://上行透传协议应答
                if (waitFor0900Ack) {
                    waitFor0900Ack = false;
                } else {
                    Update_DB datebase_update = new Update_DB();
                    if (wait_for_up_query_trainlist == 4)//正在上传查询记录
                    {
                        wait_for_up_query_trainlist = 0;
                    } else if (sendingTrainListSerial != null && datebase_update.clearTrainListFlag(sendingTrainListSerial)) {
                        if (DEBUG) Log.i(TAG, "清除数据库中培训记录标志" + sendingTrainListSerial);
                        sendingTrainListSerial = null;
                        SharedPreferences sendMessageCount = context.getSharedPreferences("SendMessageCount", MODE_PRIVATE);
                        SharedPreferences.Editor sendMessageEditor = sendMessageCount.edit();
                        int trainCount = sendMessageCount.getInt("TrainCount", 0);
                        if (trainCount > 0) {
                            sendMessageEditor.putInt("TrainCount", trainCount - 1);
                            sendMessageEditor.commit();
                        }
                    } else {
                        if (DEBUG) Log.i(TAG, "培训记录数据库更新失败");
                    }
                }
                break;
            default:
                break;
        }
    }

    private boolean waitFor0900Ack = false;

    private void parseReceivePackage(byte[] recvPackage, int packageLen) {
        int index = 0;
        int startIndex = 0;

        for (int i = 0; i < packageLen; i++) {
            if (recvPackage[i] == 0x7E) {
                if (index > 0) {
                    byte[] recvData = new byte[index];
                    System.arraycopy(recvPackage, startIndex, recvData, 0, index);
                    handleReceiveData(recvData, recvData.length);
                    index = 0;
                } else {
                    startIndex = i + 1;
                    index = 0;
                }
            } else {
                index++;
            }
        }
    }

    void handleResendPackage(int srcPackageSerial, int[] patchSrial) {
        byte[] sendBytes;
        byte[] tempBytes;
        byte[] photoContent = new byte[PHOTO_PACKAGE_SIZE];
        try {
            ThisLog(srcPackageSerial + "补传" + patchSrial.length);
            for (int i = 0; i < patchSrial.length; i++) {
                if (patchSrial[i] == 1) {
                    photoContent = readFileByRandomAccess(curPhotoHead.filePath, patchSrial[i] * PHOTO_PACKAGE_SIZE, PHOTO_PACKAGE_SIZE);
                    tempBytes = new byte[10 + photoContent.length];
                    System.arraycopy(curPhotoHead.photoSerial, 0, tempBytes, 0, 10);
                    Log.e(TAG, new String(curPhotoHead.photoSerial));
                    System.arraycopy(photoContent, 0, tempBytes, 10, photoContent.length);
                } else if (patchSrial[i] == curPhotoHead.totalPackages - 1) {//the last package
                    photoContent = readFileByRandomAccess(curPhotoHead.filePath, patchSrial[i] * PHOTO_PACKAGE_SIZE, (int) (curPhotoHead.fileSize - patchSrial[i] * PHOTO_PACKAGE_SIZE));
                } else {
                    photoContent = readFileByRandomAccess(curPhotoHead.filePath, patchSrial[i] * PHOTO_PACKAGE_SIZE, PHOTO_PACKAGE_SIZE);
                }
                MessageDefine.sendSequence = srcPackageSerial;

                int photoisBlindArea = 0;
                if (patchSrial[i] == 1 && curPhotoHead.isBlindArea == 1) {
                    photoisBlindArea = 1;
                }
                sendBytes = getCmdData(0x0900, GD_UP_PHOTO_PACKAGE, (byte) 1, photoContent, patchSrial[i], curPhotoHead.totalPackages, photoisBlindArea);
                sendTcpBytes(sendBytes);
                sleep(1000);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            transState.clearState(transState.USING);
            sendRunState = SendRunState.SENDCOACH;
            send8003 = true;
        }
    }

    //定义为最大照片字节数
    static byte[] imageBuff;
    static int imageBuffIndex;

    void upPhotoPackage() {
        if (DEBUG) Log.i(TAG, "开始上传照片");
        byte[] photoContent = new byte[PHOTO_PACKAGE_SIZE];
        byte flag;
        long waitTime;
        transState.setState(0x8900, GD_REQUIR_VERIFY_PHOTO, transState.USING);
        try {
            File file = new File(curPhotoHead.filePath);
            FileInputStream fis = new FileInputStream(file);
            int readLen;
            if (DEBUG) Log.i(TAG, "start send photo, size:" + file.length());
            if (file.length() == 0) {
                //clear
                Update_DB datebase_update = new Update_DB();
                datebase_update.clearPhotoHeadFlag(StringUtils.bytesToHexString(curPhotoHead.photoSerial));
                if (myPhotoHeadInfo != null && myPhotoHeadInfo.size() > 0) {
                    myPhotoHeadInfo.remove(0); //清空照片上传标识
                    SharedPreferences sendMessageCount = context.getSharedPreferences("SendMessageCount", MODE_PRIVATE);
                    SharedPreferences.Editor sendMessageEditor = sendMessageCount.edit();
                    int phoptCount = sendMessageCount.getInt("PhoptCount", 0);
                    sendMessageEditor.putInt("PhoptCount", phoptCount - 1);
                    sendMessageEditor.commit();
                    sendMsgToUi(10086);
                }
                transState.clearState(transState.USING);
                isSendingVideo = false;
                if (DEBUG) Log.i(TAG, "删除无效照片~");
                return;
            }
            byte[] tempBytes;
            for (int i = 1; i <= curPhotoHead.totalPackages; i++) {
                if (i == 1) {
                    readLen = fis.read(photoContent);
                    tempBytes = new byte[10 + readLen];
                    System.arraycopy(curPhotoHead.photoSerial, 0, tempBytes, 0, 10);
                    System.arraycopy(photoContent, 0, tempBytes, 10, readLen);
                } else if (i < curPhotoHead.totalPackages) {
                    readLen = fis.read(photoContent);
                    tempBytes = new byte[readLen];
                    System.arraycopy(photoContent, 0, tempBytes, 0, readLen);
                    System.arraycopy(tempBytes, 0, imageBuff, imageBuffIndex, tempBytes.length);
                    imageBuffIndex += tempBytes.length;
                } else {//last package
                    readLen = fis.read(photoContent);
                    tempBytes = new byte[readLen + 256];
                    System.arraycopy(photoContent, 0, tempBytes, 0, readLen);
                    System.arraycopy(photoContent, 0, imageBuff, imageBuffIndex, readLen);
                    imageBuffIndex += readLen;
                    if (imageBuffIndex == imageBuff.length) {
                        if (DEBUG)
                            Log.i(TAG, "长度校验正确~imageBuffIndex：" + String.valueOf(imageBuffIndex) + " imageBuff.length:" + String.valueOf(imageBuff.length));
                        if (DEBUG)
                            Log.i(TAG, "start:" + StringUtils.bytesToHexString(imageBuff, 0, 256));
                        if (DEBUG)
                            Log.i(TAG, "end:" + StringUtils.bytesToHexString(imageBuff, imageBuff.length - 256, 256));
                    } else {
                        if (DEBUG)
                            Log.i(TAG, "长度校验异常！imageBuffIndex：" + String.valueOf(imageBuffIndex) + " imageBuff.length:" + String.valueOf(imageBuff.length));
                    }
                    //add 256 bytes crypt data
                    byte[] cryptData = getRsaEncryptData(imageBuff);
                    if (DEBUG) Log.i(TAG, "Encrypt:" + StringUtils.bytesToHexString(cryptData));
                    System.arraycopy(cryptData, 0, tempBytes, readLen, 256);

                }
                int photoisBlindArea = 0;
                if (i == 1 && curPhotoHead.isBlindArea == 1) {
                    photoisBlindArea = 1;
                }
                waitFor0900Ack = true;
                byte[] sendBytes = getCmdData(0x0900, GD_UP_PHOTO_PACKAGE, (byte) 1, tempBytes, i, curPhotoHead.totalPackages, photoisBlindArea);
                sendTcpBytes(sendBytes);
                waitTime = System.currentTimeMillis();
                do {
//                    if(myPhotoHeadInfo.size()>1){
//                    sleep(1000);}else{
                    sleep(100);
//                    }
                } while (waitFor0900Ack && System.currentTimeMillis() - waitTime < 10000);
                if (i == curPhotoHead.totalPackages && !waitFor0900Ack) {//最后一包正确应答
                    Update_DB datebase_update = new Update_DB();
                    datebase_update.clearPhotoHeadFlag(StringUtils.bytesToHexString(curPhotoHead.photoSerial));
                    if (myPhotoHeadInfo != null && myPhotoHeadInfo.size() > 0) {
                        myPhotoHeadInfo.remove(0); //清空照片上传标识
                        SharedPreferences sendMessageCount = context.getSharedPreferences("SendMessageCount", MODE_PRIVATE);
                        SharedPreferences.Editor sendMessageEditor = sendMessageCount.edit();
                        int phoptCount = sendMessageCount.getInt("PhoptCount", 0);
                        sendMessageEditor.putInt("PhoptCount", phoptCount - 1);
                        sendMessageEditor.commit();
                        sendMsgToUi(10086);
                    }
                    transState.clearState(transState.USING);
                    isSendingVideo = false;
                    if (DEBUG) Log.i(TAG, "应答照片传输完成");
                }
            }
            fis.close();
            Utils.saveRunningLog("send photo file complished");
//            transState.setState(0x8900, GD_REQUIR_VERIFY_PHOTO, transState.WAIT_ACK);
//            int timeCnt = 0;
//            while (transState.getState() == transState.WAIT_ACK && transState.getWaitAckSubCmd() == GD_REQUIR_VERIFY_PHOTO) {
//                Thread.sleep(100);
//                if (timeCnt++ > 10) {
//                    FileUtils.saveRunningLog("request ack timeout, send 0307 command");
//                    byte[] requestContent = new byte[12];
//                    System.arraycopy(curPhotoHead.photoSerial, 0, requestContent, 0, 10);
//                    requestContent[10] = (byte) (curPhotoHead.totalPackages / 256);
//                    requestContent[11] = (byte) (curPhotoHead.totalPackages % 256);
//                    byte[] sendBytes = getCmdData(0x0900, GD_REQUIR_VERIFY_PHOTO, (byte) 1, requestContent);
//                    sendTcpBytes(sendBytes);
//                    transState.setState(0x8900, GD_REQUIR_VERIFY_PHOTO, transState.WAIT_ACK);
//                    break;
//                }
//            }
        } catch (Exception ex) {
            ex.printStackTrace();
            if (DEBUG) Log.i(TAG, "照片上传结束");
        }
    }

    byte[] device_certification = new byte[5 * 1024];
    int cerfi_index = 0;
    int srcPackageSerial;
    int[] patchSrial;

    private void handleReceiveData(byte[] recvBuf, int len) {
        byte[] recvData = new byte[len];
        byte[] context;
        System.arraycopy(recvBuf, 0, recvData, 0, len);
        byte[] buf = getValueData(recvData);
        if (buf == null || buf.length == 0) {
            return;
        }

        int cmdType;
        int cmd;
        int cmdLen;
        int packageProperty;
        int ackSequence;
        int totalPackages;
        int packageNum;
        int index = 0;
        byte[] tempBytes;

        cmdType = buf[index++] & 0xFF;
        cmd = (buf[index++] & 0xFF) * 256 + (buf[index++] & 0xFF);
        packageProperty = (buf[index++] & 0xFF) * 256 + (buf[index++] & 0xFF);
        cmdLen = packageProperty & 0x3FF;
        index += 8;//phone number
        recvSequence = (buf[index++] & 0xFF) * 256 + (buf[index++] & 0xFF);
        index++;// reservation
        if ((packageProperty & 0x2000) != 0) {
            totalPackages = (buf[index++] & 0xFF) * 256 + (buf[index++] & 0xFF);
            packageNum = (buf[index++] & 0xFF) * 256 + (buf[index++] & 0xFF);
        } else {
            totalPackages = 1;
            packageNum = 1;
        }
        try {
            switch (cmd) {

                case GPRS_ID_CMD8001://对于客户端上传的补传信息，服务器端应回复通用应答。
                    //在通用应答中需有对补传消息情况的处理
                    /*if(isRepairData){
                        //此时为0900 的补传数据,需 8900应答
                        context = new byte[cmdLen];
                        if ((packageProperty & 0x2000) != 0)//分包处理
                        {
                            System.arraycopy(buf, 20, context, 0, cmdLen);
                        } else {
                            System.arraycopy(buf, 16, context, 0, cmdLen);
                        }
                        parserCmdData8900(context);
                        isRepairData = false;
                    } else {
                        ackSequence = (buf[16] & 0xFF) * 256 + (buf[17] & 0xFF);
                        handlerCmd8001(ackSequence, (buf[18] & 0xFF) * 256 + (buf[19] & 0xFF), (int) buf[20]);
                        transState.clearState(transState.WAIT_ACK);
                    }*/
                    ackSequence = (buf[16] & 0xFF) * 256 + (buf[17] & 0xFF);
                    handlerCmd8001(ackSequence, (buf[18] & 0xFF) * 256 + (buf[19] & 0xFF), (int) buf[20]);
                    transState.clearState(transState.WAIT_ACK);
                    break;
                case 0x8003:
                    ThisLog("8003");
                    send8003 = false;
                    srcPackageSerial = (buf[16] & 0xFF) * 256 + (buf[17] & 0xFF);
                    int packageNumer = buf[18] & 0xFF;
                    patchSrial = new int[packageNumer];
                    for (int i = 0; i < packageNumer; i++) {
                        patchSrial[i] = (buf[19 + i * 2] & 0xFF) * 256 + (buf[20 + i * 2] & 0xFF);
                    }
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                        }
//                    }).start();
                    handleResendPackage(srcPackageSerial, patchSrial);
                    break;
                case GPRS_ID_CMD8100:
                    Log.i("zxj", "register answer");
                    CommonInfo.setDeviceLoginState(0);
                    if (packageNum == 1) {
                        cerfi_index = 0;
                        ackSequence = (buf[index++] & 0xFF) * 256 + (buf[index++] & 0xFF);
                        Log.i("zxj", "ackSequence--" + ackSequence);
                        Log.i("zxj", "MessageDefine.sendSequence--" + MessageDefine.sendSequence);
                        if (ackSequence == MessageDefine.sendSequence) {
                            byte[] certifCmd;
                            byte[] terminalNumber;
                            StringBuffer sb;
                            switch (buf[index++]) {
                                case 0://注册成功
                                    Log.i("zxj", "register success");
                                    sb = new StringBuffer();
                                    byte[] platformNumber = new byte[5];
                                    byte[] drivingNo = new byte[16];
                                    terminalNumber = new byte[16];
                                    certifCmd = new byte[12];
                                    System.arraycopy(buf, index, platformNumber, 0, platformNumber.length);
                                    index += platformNumber.length;
                                    System.arraycopy(buf, index, drivingNo, 0, drivingNo.length);
                                    index += drivingNo.length;
                                    System.arraycopy(buf, index, terminalNumber, 0, terminalNumber.length);
                                    index += terminalNumber.length;
                                    sb.append(new String(terminalNumber, "GBK"));
                                    System.arraycopy(buf, index, certifCmd, 0, certifCmd.length);
                                    index += certifCmd.length;
                                    sb.append("|");
//                                    sb.append(new String(certifCmd, "GBK"));
                                    MyBuffer mPwdBuffer = new MyBuffer(certifCmd);
                                    String pwd = mPwdBuffer.getString(12);
                                    sb.append(pwd);
                                    byte[] registerFile = sb.toString().getBytes("utf-8");
                                    Utils.writeFileCreated(AppConfig.REGISTER_SAVE_PATH, "register.info", registerFile); //保存注册信息 全国设备编号+证书密码
                                    CommonInfo.setTelnetDeviceNumber(new String(terminalNumber, "GBK"));
//                                    CommonInfo.setCertificationPassword(new String(certifCmd, "GBK"));
                                    CommonInfo.setCertificationPassword(pwd);
                                    System.arraycopy(buf, index, device_certification, cerfi_index, cmdLen - 52);
                                    cerfi_index += cmdLen - 52;
                                    if (DEBUG) Log.i(TAG, "终端注册成功~~");
                                    handlerState = EnumState.UNLOGIN;
                                    TimerDerminalEvent event = new TimerDerminalEvent();
                                    event.setObj(0);
                                    EventBus.getDefault().post(event);
                                    //证书正确才代表注册成功
                                    tempSeconds = System.currentTimeMillis() / 1000;
                                    byte[] signdata;
//                                    if (CommonInfo.getTelnetDeviceNumber() != null && CommonInfo.getCertificationPassword() != null) {
//                                        signdata = getRsaSignedData(CommonInfo.getTelnetDeviceNumber().getBytes("utf-8"), tempSeconds);
//                                        if (signdata != null) {
//                                            handlerState = EnumState.UNLOGIN;
//                                            TimerDerminalEvent event = new TimerDerminalEvent();
//                                            event.setObj(0);
//                                            EventBus.getDefault().post(event);
//                                        } else {
//                                            Log.i(TAG, "证书错误");
//                                            handlerState = EnumState.REGCOMPLICT;
//                                            TimerDerminalEvent event = new TimerDerminalEvent();
//                                            event.setObj(5);
//                                            EventBus.getDefault().post(event);
//                                        }
//                                    } else {
//                                        Log.i(TAG, "证书错误");
//                                        handlerState = EnumState.REGCOMPLICT;
//                                        TimerDerminalEvent event = new TimerDerminalEvent();
//                                        event.setObj(5);
//                                        EventBus.getDefault().post(event);
//                                    }
                                    break;
                                case 1://车辆已被注册
                                    handlerState = EnumState.REGCOMPLICT;
                                    if (DEBUG) Log.i(TAG, "车辆已被注册");
                                    TimerDerminalEvent event1 = new TimerDerminalEvent();
                                    event1.setObj(1);
                                    EventBus.getDefault().post(event1);
                                    break;
                                case 2://数据库中无该车辆
                                    handlerState = EnumState.HALT;
                                    if (DEBUG) Log.i(TAG, "数据库中无该车辆");
                                    TimerDerminalEvent event2 = new TimerDerminalEvent();
                                    event2.setObj(2);
                                    EventBus.getDefault().post(event2);
                                    break;
                                case 3://终端已被注册
                                    handlerState = EnumState.REGCOMPLICT;
                                    MessageDefine.MSG_TERMINAL_CANCEL = 1;
                                    if (DEBUG) Log.i(TAG, "终端已被注册");
                                    TimerDerminalEvent event3 = new TimerDerminalEvent();
                                    event3.setObj(3);
                                    EventBus.getDefault().post(event3);
                                    break;
                                case 4://数据库中无该终端
                                    handlerState = EnumState.HALT;
                                    if (DEBUG) Log.i(TAG, "数据库中无该终端");
                                    TimerDerminalEvent event4 = new TimerDerminalEvent();
                                    event4.setObj(4);
                                    EventBus.getDefault().post(event4);
                                    break;
                                default:
                                    break;
                            }
                        }
                    } else {
                        System.arraycopy(buf, index, device_certification, cerfi_index, cmdLen);
                        cerfi_index += cmdLen;
                        if (totalPackages == packageNum) {
                            cerfi_index -= cerfi_index % 4;
                            byte[] tempcerti = new byte[cerfi_index];
                            System.arraycopy(device_certification, 0, tempcerti, 0, tempcerti.length);
                            try {
                                byte[] cabuf = Base64.decode(new String(tempcerti, "utf-8"));
//                            FileUtils.writeFileCreated(AppConfig.RSA_DATA_DIRECTORY, CERTIFICATION_NAME, device_certification, 0, cerfi_index);
                                Utils.writeFileCreated(AppConfig.REGISTER_SAVE_PATH, CERTIFICATION_NAME, cabuf, 0, cabuf.length);
                                handlerState = EnumState.UNLOGIN;
                                transState.clearState(transState.WAIT_ACK);
//                                MessageDefine.sendSequence++;
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;

                case GPRS_ID_CMD8FE6:
                    byte[] sendBytes = getCmdData(GPRS_ID_CMD8FE6, 0);
                    sendTcpBytes(sendBytes);
                    break;
                case GPRS_ID_CMD8103: //参数设置
                    context = new byte[cmdLen];
                    if ((packageProperty & 0x2000) != 0)//分包处理
                    {
                        System.arraycopy(buf, 20, context, 0, cmdLen);
                    } else {
                        System.arraycopy(buf, 16, context, 0, cmdLen);
                    }
                    //if (DEBUG)  Log.i(TAG, "接收数据，ID：0x" + Integer.toHexString(cmd) + "数据内容：" + StringUtils.bytesToHexString(buf));
                    parserCmdData8103(context);
                    //ACK
                    byte[] cmd0001Data = new byte[5];
                    cmd0001Data[0] = (byte) ((recvSequence >> 8) & 0xFF);
                    cmd0001Data[1] = (byte) (recvSequence & 0xff);
                    cmd0001Data[2] = (byte) (GPRS_ID_CMD8103 >> 8);
                    cmd0001Data[3] = (byte) GPRS_ID_CMD8103;
                    cmd0001Data[4] = 0x00;//0、成功   1、失败    2、消息有误   3、不支持
                    tempBytes = getCmdData(GPRS_ID_CMD0001, 0, (byte) 0x00, cmd0001Data, 0);
                    sendTcpBytes(tempBytes);
                    break;
                case GPRS_ID_CMD8104: //获取参数
                    tempBytes = getCmdData(GPRS_ID_CMD8104, 0, (byte) 0x00, null, 1, 1, 0);
                    sendTcpBytes(tempBytes);
//                    tempBytes = getCmdData(GPRS_ID_CMD8104, 0, (byte) 0x00, get8104Data(recvSequence,0,2), 2, 2);
//                    sendTcpBytes(tempBytes);
                    break;
                case GPRS_ID_CMD8106: //指定终端参数
//                    context = new byte[cmdLen];
//                    if ((packageProperty & 0x2000) != 0)//分包处理
//                    {
//                        System.arraycopy(buf, 20, context, 0, cmdLen);
//                    } else {
//                        System.arraycopy(buf, 16, context, 0, cmdLen);
//                    }
                    //if (DEBUG)  Log.i(TAG, "接收数据，ID：0x" + Integer.toHexString(cmd) + "数据内容：" + StringUtils.bytesToHexString(buf));
                    // TODO: 2018/9/4 解析参数并保存
//                    parserCmdData8103(context);
                    tempBytes = getCmdData(GPRS_ID_CMD8106, 0, (byte) 0x00, null, 1, 1, 0);
                    sendTcpBytes(tempBytes);
                    break;
                case GPRS_ID_CMD8201://获取位置信息
                    tempBytes = getCmdData(GPRS_ID_CMD0201, 0);
                    sendTcpBytes(tempBytes);
                    break;
                case GPRS_ID_CMD8202:// 临时位置跟踪
                    //跟踪期间上传轨迹的间隔时间S
                    int traceInterval = (buf[index++] & 0xFF) << 8 | (buf[index++] & 0xFF);
                    if (traceInterval != 0) {

                    } else {//stop trace

                    }
                    //跟踪截至有效时间戳
                    long validTimestamp = (buf[index++] & 0xFF) << 24 | (buf[index++] & 0xFF) << 16 | (buf[index++] & 0xFF) << 8 | (buf[index++] & 0xFF);
                    break;

                case GPRS_ID_CMD8900:
                    //TODO 网络重连之后发送的请求，响应没有执行到这里
                    context = new byte[cmdLen];
                    if ((packageProperty & 0x2000) != 0)//分包处理
                    {
                        System.arraycopy(buf, 20, context, 0, cmdLen);
                    } else {
                        System.arraycopy(buf, 16, context, 0, cmdLen);
                    }
                    parserCmdData8900(context);
                    break;
            }
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }

    }


    public void parserCmdData8103(byte[] recvBytes) {
        int cnt = 0;
        int paramList;
        long paramID;
        int paramLen;
        int value;
        String sValue;
        byte[] paramBytes;
        cnt++;
        paramList = (recvBytes[cnt++] & 0xFF);
        try {
            for (int i = 0; i < paramList; i++) {
                if (cnt + 5 > recvBytes.length) {
                    break;
                }
                paramID = (recvBytes[cnt] & 0xFF) << 24 | (recvBytes[cnt + 1] & 0xFF) << 16 | (recvBytes[cnt + 2] & 0xFF) << 8 | (recvBytes[cnt + 3] & 0xFF);
                cnt += 4;
                paramLen = recvBytes[cnt++] & 0xFF;
                if (cnt + paramLen > recvBytes.length) {
                    break;
                }
                paramBytes = new byte[paramLen];
                System.arraycopy(recvBytes, cnt, paramBytes, 0, paramLen);
                cnt += paramLen;
                Log.d(TAG, String.valueOf((paramID & 0xFFFF)));
                String str = String.valueOf((paramID & 0xFFFF));
                int cssz = Integer.parseInt(str);
                //switch ( (int)(paramID & 0xFFFF)) {
                switch (cssz) {
                    case 0x0001://	DWORD	客户端心跳发送间隔，单位为秒(s)
                        value = (paramBytes[0] & 0xFF) << 24 | (paramBytes[1] & 0xFF) << 16 | (paramBytes[2] & 0xFF) << 8 | (paramBytes[3] & 0xFF);
                        TelnetParameter.setLinkPackageInterval(value);
                        break;
                    case 0x0002://	DWORD	TCP消息应答超时时间，单位为秒(s)
                        value = (paramBytes[0] & 0xFF) << 24 | (paramBytes[1] & 0xFF) << 16 | (paramBytes[2] & 0xFF) << 8 | (paramBytes[3] & 0xFF);
                        TelnetParameter.setTcpAckTimeout(value);
                        break;
                    case 0x0003://	DWORD	TCP消息重传次数
                        value = (paramBytes[0] & 0xFF) << 24 | (paramBytes[1] & 0xFF) << 16 | (paramBytes[2] & 0xFF) << 8 | (paramBytes[3] & 0xFF);
                        TelnetParameter.setTcpRepeatCount(value);
                        break;
                    case 0x0004://	DWORD	UDP消息应答超时时间，单位为秒(s)
                        value = (paramBytes[0] & 0xFF) << 24 | (paramBytes[1] & 0xFF) << 16 | (paramBytes[2] & 0xFF) << 8 | (paramBytes[3] & 0xFF);
                        TelnetParameter.setUdpAckTimeout(value);
                        break;
                    case 0x0005://	DWORD	UDP消息重传次数
                        value = (paramBytes[0] & 0xFF) << 24 | (paramBytes[1] & 0xFF) << 16 | (paramBytes[2] & 0xFF) << 8 | (paramBytes[3] & 0xFF);
                        TelnetParameter.setUdpRepeatCount(value);
                        break;
                    case 0x0006://	DWORD	SMS消息应答超时时间，单位为秒(s)
                        value = (paramBytes[0] & 0xFF) << 24 | (paramBytes[1] & 0xFF) << 16 | (paramBytes[2] & 0xFF) << 8 | (paramBytes[3] & 0xFF);
                        TelnetParameter.setSmsAckTimeout(value);
                        break;
                    case 0x0007://	DWORD	SMS消息重传次数
                        value = (paramBytes[0] & 0xFF) << 24 | (paramBytes[1] & 0xFF) << 16 | (paramBytes[2] & 0xFF) << 8 | (paramBytes[3] & 0xFF);
                        TelnetParameter.setSmsRepeatCount(value);
                        break;
                    case 0x0010://	STRING	主服务器APN，无线通信拨号访问点。若网络制式为CDMA，则该处为PPP拨号号码
                        sValue = new String(paramBytes);
                        TelnetParameter.setHostApn(sValue);
                        break;
                    case 0x0011://	STRING	主服务器无线通信拨号用户名
                        sValue = new String(paramBytes);
                        TelnetParameter.setHostDialName(sValue);
                        break;
                    case 0x0012://	STRING	主服务器无线通信拨号密码
                        sValue = new String(paramBytes);
                        TelnetParameter.setHostDialPassword(sValue);
                        break;
                    case 0x0013://	STRING	主服务器地址，IP或域名
                        sValue = new String(paramBytes);
                        TelnetParameter.setHostIpAddress(sValue);
                        break;
                    case 0x0014://	STRING	备份服务器APN，无线通信拨号访问点
                        sValue = new String(paramBytes);
                        TelnetParameter.setBakApn(sValue);
                        break;
                    case 0x0015://	STRING	备份服务器无线通信拨号用户名
                        sValue = new String(paramBytes);
                        TelnetParameter.setBakDialName(sValue);
                        break;
                    case 0x0016://	STRING	备份服务器无线通信拨号密码
                        sValue = new String(paramBytes);
                        TelnetParameter.setBakDialPassword(sValue);
                        break;
                    case 0x0017://	STRING	备份服务器地址，IP或域名
                        sValue = new String(paramBytes);
                        TelnetParameter.setBakIpAddress(sValue);
                        break;
                    case 0x0018://DWORD	服务器TCP端口
                        value = (paramBytes[0] & 0xFF) << 24 | (paramBytes[1] & 0xFF) << 16 | (paramBytes[2] & 0xFF) << 8 | (paramBytes[3] & 0xFF);
                        TelnetParameter.setTcpPort(value);
                        break;
                    case 0x0019://DWORD	服务器UDP端口
                        value = (paramBytes[0] & 0xFF) << 24 | (paramBytes[1] & 0xFF) << 16 | (paramBytes[2] & 0xFF) << 8 | (paramBytes[3] & 0xFF);
                        TelnetParameter.setUdpPort(value);
                        break;
                    //reservaytion
                    case 0x0020://	DWORD	位置汇报策略，0：定时汇报；1：定距汇报；2：定时和定距汇报
                        value = (paramBytes[0] & 0xFF) << 24 | (paramBytes[1] & 0xFF) << 16 | (paramBytes[2] & 0xFF) << 8 | (paramBytes[3] & 0xFF);
                        TelnetParameter.setUpPositionMode(value);
                        break;
                    case 0x0021://	DWORD	位置汇报方案，0：根据ACC状态；1：根据登录状态和ACC状态，先判断登录状态，若登录再根据ACC状态
                        value = (paramBytes[0] & 0xFF) << 24 | (paramBytes[1] & 0xFF) << 16 | (paramBytes[2] & 0xFF) << 8 | (paramBytes[3] & 0xFF);
                        TelnetParameter.setUpPositionMethods(value);
                        break;
                    case 0x0022://	DWORD	驾驶员未登录汇报时间间隔，单位为秒(s),>0
                        value = (paramBytes[0] & 0xFF) << 24 | (paramBytes[1] & 0xFF) << 16 | (paramBytes[2] & 0xFF) << 8 | (paramBytes[3] & 0xFF);
                        TelnetParameter.setUpPositionIntervalUnsigned(value);
                        break;
                    //reservation            case ://0x0023-0x0026	DWORD	保留
                    case 0x0027://	DWORD	休眠时汇报时间间隔，单位为秒(s),>0
                        value = (paramBytes[0] & 0xFF) << 24 | (paramBytes[1] & 0xFF) << 16 | (paramBytes[2] & 0xFF) << 8 | (paramBytes[3] & 0xFF);
                        TelnetParameter.setUpPositionIntervalSleep(value);
                        break;
                    case 0x0028://	DWORD	紧急报警时汇报时间间隔，单位为秒(s),>0
                        value = (paramBytes[0] & 0xFF) << 24 | (paramBytes[1] & 0xFF) << 16 | (paramBytes[2] & 0xFF) << 8 | (paramBytes[3] & 0xFF);
                        TelnetParameter.setUpPositionIntervalUrgent(value);
                        break;
                    case 0x0029://	DWORD	缺省时间汇报间隔，单位为秒(s),>0
                        value = (paramBytes[0] & 0xFF) << 24 | (paramBytes[1] & 0xFF) << 16 | (paramBytes[2] & 0xFF) << 8 | (paramBytes[3] & 0xFF);
                        Context ctx = TcpClient.this.context;
                        SharedPreferences sp = ctx.getSharedPreferences("PositionSave", MODE_PRIVATE);
                        //存入数据
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("PositionTime", value);
                        editor.commit();
                        TelnetParameter.setUpPositionTimelDefault(value);
                        break;
                    //reservation 0x002A-0x002B	DWORD	保留
                    case 0x002C://	DWORD	缺省距离汇报间隔，单位为米(m),>0
                        value = (paramBytes[0] & 0xFF) << 24 | (paramBytes[1] & 0xFF) << 16 | (paramBytes[2] & 0xFF) << 8 | (paramBytes[3] & 0xFF);
                        TelnetParameter.setUpPositionDistancelDefault(value);
                        break;
                    case 0x002D://	DWORD	驾驶员未登录汇报距离间隔，单位为米(m),>0
                        value = (paramBytes[0] & 0xFF) << 24 | (paramBytes[1] & 0xFF) << 16 | (paramBytes[2] & 0xFF) << 8 | (paramBytes[3] & 0xFF);
                        TelnetParameter.setUpPositionDistanceSleep(value);
                        break;
                    case 0x002E://	DWORD	休眠时汇报距离间隔，单位为米(m),>0
                        value = (paramBytes[0] & 0xFF) << 24 | (paramBytes[1] & 0xFF) << 16 | (paramBytes[2] & 0xFF) << 8 | (paramBytes[3] & 0xFF);
                        TelnetParameter.setUpPositionDistanceUnsigned(value);
                        break;
                    case 0x002F://	DWORD	紧急报警时汇报距离间隔，单位为米(m),>0
                        value = (paramBytes[0] & 0xFF) << 24 | (paramBytes[1] & 0xFF) << 16 | (paramBytes[2] & 0xFF) << 8 | (paramBytes[3] & 0xFF);
                        TelnetParameter.setUpPositionDistanceUrgent(value);
                        break;
                    case 0x0030://	DWORD	拐点补传角度，<180°
                        value = (paramBytes[0] & 0xFF) << 24 | (paramBytes[1] & 0xFF) << 16 | (paramBytes[2] & 0xFF) << 8 | (paramBytes[3] & 0xFF);
                        TelnetParameter.setUpPositionTurnAngle(value);
                        break;
                    //reservation 0x0031-0x003F		保留
                    case 0x0040://	STRING	监控平台电话号码
                        sValue = new String(paramBytes);
                        TelnetParameter.setTracePhotoNumber(sValue);
                        break;
                    case 0x0041://	STRING	复位电话号码，可采用此电话号码拨打终端电话让终端复位
                        sValue = new String(paramBytes);
                        TelnetParameter.setRestartPhotoNumnber(sValue);
                        break;
                    case 0x0042://	STRING	恢复出厂设置电话号码，可采用此电话号码拨打终端电话让终端恢复出厂设置
                        sValue = new String(paramBytes);
                        TelnetParameter.setResetPhoneNumber(sValue);
                        break;
                    case 0x0043://	STRING	监控平台SMS电话号码
                        sValue = new String(paramBytes);
                        TelnetParameter.setTraceSmsPhoneNumber(sValue);
                        break;
                    case 0x0044://	STRING	接收终端SMS文本报警号码
                        sValue = new String(paramBytes);
                        TelnetParameter.setReceiveSmsPhoneNumber(sValue);
                        break;
                    case 0x0045://	DWORD	终端电话接听策略，0：自动接听；1：ACC ON时自动接听，OFF时手动接听
                        value = (paramBytes[0] & 0xFF) << 24 | (paramBytes[1] & 0xFF) << 16 | (paramBytes[2] & 0xFF) << 8 | (paramBytes[3] & 0xFF);
                        TelnetParameter.setAnswerCallMode(value);
                        break;
                    case 0x0046://	DWORD	每次最长通话时间，单位为秒(s),0为不允许通话，0xFFFFFFFF为不限制
                        value = (paramBytes[0] & 0xFF) << 24 | (paramBytes[1] & 0xFF) << 16 | (paramBytes[2] & 0xFF) << 8 | (paramBytes[3] & 0xFF);
                        TelnetParameter.setMaxCallingTimeOnce(value);
                        break;
                    case 0x0047://	DWORD	当月最长通话时间，单位为秒(s),0为不允许通话，0xFFFFFFFF为不限制
                        value = (paramBytes[0] & 0xFF) << 24 | (paramBytes[1] & 0xFF) << 16 | (paramBytes[2] & 0xFF) << 8 | (paramBytes[3] & 0xFF);
                        TelnetParameter.setMaxCallTimeMonth(value);
                        break;
                    case 0x0048://	STRING	监听电话号码
                        sValue = new String(paramBytes);
                        TelnetParameter.setListenPhotoNumber(sValue);
                        break;
                    case 0x0049://	STRING	监管平台特权短信号码
                        sValue = new String(paramBytes);
                        TelnetParameter.setPrivilegeSmsPhoneNumber(sValue);
                        break;
                    //reservation 0x004A-0x004F		保留
                    case 0x0050://	DWORD	报警屏蔽字。与位置信息汇报消息中的报警标识相对应，相应位为1则相应报警被屏蔽
                        value = (paramBytes[0] & 0xFF) << 24 | (paramBytes[1] & 0xFF) << 16 | (paramBytes[2] & 0xFF) << 8 | (paramBytes[3] & 0xFF);
                        TelnetParameter.setWarningMaskValue(value);
                        break;
                    case 0x0051://	DWORD	报警发送文本SMS开关，与位置信息汇报消息中的报警标识相对应，相应位为1则相应报警时发送文本SMS
                        value = (paramBytes[0] & 0xFF) << 24 | (paramBytes[1] & 0xFF) << 16 | (paramBytes[2] & 0xFF) << 8 | (paramBytes[3] & 0xFF);
                        TelnetParameter.setSendWarningSmsEn(value);
                        break;
                    case 0x0052://	DWORD	报警拍摄开关，与位置信息汇报消息中的报警标识相对应，相应位为1则相应报警时摄像头拍摄
                        value = (paramBytes[0] & 0xFF) << 24 | (paramBytes[1] & 0xFF) << 16 | (paramBytes[2] & 0xFF) << 8 | (paramBytes[3] & 0xFF);
                        TelnetParameter.setTakephotoForWarning(value);
                        break;
                    case 0x0053://	DWORD	报警拍摄存储标识，与位置信息汇报消息中的报警标识相对应，相应位为1则对相应报警时牌的照片进行存储，否则实时长传
                        value = (paramBytes[0] & 0xFF) << 24 | (paramBytes[1] & 0xFF) << 16 | (paramBytes[2] & 0xFF) << 8 | (paramBytes[3] & 0xFF);
                        TelnetParameter.setSaveWarningPhoto(value);
                        break;
                    case 0x0054://	DWORD	关键标识，与位置信息汇报消息中的报警标识相对应，相应位为1则对相应报警为关键报警
                        value = (paramBytes[0] & 0xFF) << 24 | (paramBytes[1] & 0xFF) << 16 | (paramBytes[2] & 0xFF) << 8 | (paramBytes[3] & 0xFF);
                        TelnetParameter.setEssentiallWarningFlag(value);
                        break;
                    case 0x0055://	DWORD	最高速度，单位为公里每小时(km/h)
                        value = (paramBytes[0] & 0xFF) << 24 | (paramBytes[1] & 0xFF) << 16 | (paramBytes[2] & 0xFF) << 8 | (paramBytes[3] & 0xFF);
                        TelnetParameter.setLimitSpeed(value);
                        break;
                    case 0x0056://	DWORD	超速持续时间，单位为秒(s)
                        value = (paramBytes[0] & 0xFF) << 24 | (paramBytes[1] & 0xFF) << 16 | (paramBytes[2] & 0xFF) << 8 | (paramBytes[3] & 0xFF);
                        TelnetParameter.setConstantOverspeedTime(value);
                        break;
                    case 0x0057://	DWORD	连续驾驶时间门限，单位为秒(s)
                        value = (paramBytes[0] & 0xFF) << 24 | (paramBytes[1] & 0xFF) << 16 | (paramBytes[2] & 0xFF) << 8 | (paramBytes[3] & 0xFF);
                        TelnetParameter.setConstantDrivingLimitOnce(value);
                        break;
                    case 0x0058://	DWORD	当天累计驾驶时间门限，单位为秒(s)
                        value = (paramBytes[0] & 0xFF) << 24 | (paramBytes[1] & 0xFF) << 16 | (paramBytes[2] & 0xFF) << 8 | (paramBytes[3] & 0xFF);
                        TelnetParameter.setConstantDrivingLimitDay(value);
                        break;
                    case 0x0059://	DWORD	最小休息时间，单位为秒(s)
                        value = (paramBytes[0] & 0xFF) << 24 | (paramBytes[1] & 0xFF) << 16 | (paramBytes[2] & 0xFF) << 8 | (paramBytes[3] & 0xFF);
                        TelnetParameter.setMinRestTime(value);
                        break;
                    case 0x005A://	DWORD	最长停车时间，单位为秒(s)
                        value = (paramBytes[0] & 0xFF) << 24 | (paramBytes[1] & 0xFF) << 16 | (paramBytes[2] & 0xFF) << 8 | (paramBytes[3] & 0xFF);
                        TelnetParameter.setAllowStopTime(value);
                        break;
                    //reservation 0x005B-0x006F		保留
                    case 0x0070://	DWORD	图像/视频质量，1-10,1最好
                        value = (paramBytes[0] & 0xFF) << 24 | (paramBytes[1] & 0xFF) << 16 | (paramBytes[2] & 0xFF) << 8 | (paramBytes[3] & 0xFF);
                        TelnetParameter.setPhotoQuality(value);
                        break;
                    case 0x0071://	DWORD	亮度，0-255
                        value = (paramBytes[0] & 0xFF) << 24 | (paramBytes[1] & 0xFF) << 16 | (paramBytes[2] & 0xFF) << 8 | (paramBytes[3] & 0xFF);
                        TelnetParameter.setPhotoLight(value);
                        break;
                    case 0x0072://	DWORD	对比度，0-127
                        value = (paramBytes[0] & 0xFF) << 24 | (paramBytes[1] & 0xFF) << 16 | (paramBytes[2] & 0xFF) << 8 | (paramBytes[3] & 0xFF);
                        TelnetParameter.setPhotoContrast(value);
                        break;
                    case 0x0073://	DWORD	饱和度，0-127
                        value = (paramBytes[0] & 0xFF) << 24 | (paramBytes[1] & 0xFF) << 16 | (paramBytes[2] & 0xFF) << 8 | (paramBytes[3] & 0xFF);
                        TelnetParameter.setPhotoSaturation(value);
                        break;
                    case 0x0074://	DWORD	色度，0-255
                        value = (paramBytes[0] & 0xFF) << 24 | (paramBytes[1] & 0xFF) << 16 | (paramBytes[2] & 0xFF) << 8 | (paramBytes[3] & 0xFF);
                        TelnetParameter.setPhotoChroma(value);
                        break;
                    //reservation 0x0075-0x007F	DWORD
                    case 0x0080://	DWORD	车辆里程表读数，1/10km
                        value = (paramBytes[0] & 0xFF) << 24 | (paramBytes[1] & 0xFF) << 16 | (paramBytes[2] & 0xFF) << 8 | (paramBytes[3] & 0xFF);
                        TelnetParameter.setTotalMiles(value);
                        break;
                    case 0x0081://	DWORD	车辆所在的省域ID
                        value = (paramBytes[0] & 0xFF) << 8 | (paramBytes[1] & 0xFF) << 0;
                        TelnetParameter.setIntPoviceId(value);
                        break;
                    case 0x0082://	DWORD	车辆所在的市域ID
                        value = (paramBytes[0] & 0xFF) << 8 | (paramBytes[1] & 0xFF) << 0;
                        TelnetParameter.setInCityId(value);
                        break;
                    case 0x0083://	DWORD	公安交通管理部门颁发的机动车号牌
                        sValue = new String(paramBytes, "GBK");
                        TelnetParameter.setPlateNumber(sValue);
                        break;
                    case 0x0084://	DWORD	车牌颜色，按照JT/T415-2006的5.4.12
                        value = (paramBytes[0] & 0xFF);
                        TelnetParameter.setPlateColor(value);
                        break;
                    case 0x0085:
                        value = (paramBytes[0] & 0xFF) << 24 | (paramBytes[1] & 0xFF) << 16 | (paramBytes[2] & 0xFF) << 8 | (paramBytes[3] & 0xFF);
                        TelnetParameter.setMileCoeff(value);
                        break;
                    default://unkonow  return
                        Log.d(TAG, paramID + "不符合");
                        break;
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        DeviceParameter.createParameterXML();// save parameter
    }


    byte[] get8104Data1(int sequence, int totalNumber, int packNumber) {
        byte[] pdata = new byte[1500];
        int index = 0;
        int value;
        String sValue;

        pdata[index++] = (byte) (sequence >> 8);
        pdata[index++] = (byte) sequence;
        pdata[index++] = 60;
        pdata[index++] = 60;
        for (int i = 0; i < 60; i++) {
            switch (i) {
                case 0://	DWORD	客户端心跳发送间隔，单位为秒(s)
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x01;
                    value = TelnetParameter.getLinkPackageInterval();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                case 1://	DWORD	TCP消息应答超时时间，单位为秒(s)
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x02;
                    value = TelnetParameter.getTcpAckTimeout();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                case 2://	DWORD	TCP消息重传次数
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x03;
                    value = TelnetParameter.getTcpRepeatCount();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                case 3://	DWORD	UDP消息应答超时时间，单位为秒(s)
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x04;
                    value = TelnetParameter.getUdpAckTimeout();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                case 4://	DWORD	UDP消息重传次数
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x05;
                    value = TelnetParameter.getUdpRepeatCount();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                case 5://	DWORD	SMS消息应答超时时间，单位为秒(s)、
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x06;
                    value = TelnetParameter.getSmsAckTimeout();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                case 6://	DWORD	SMS消息重传次数
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x07;
                    value = TelnetParameter.getSmsRepeatCount();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                case 15://DWORD	服务器TCP端口
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x18;
                    value = TelnetParameter.getTcpPort();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                case 16://DWORD	服务器UDP端口
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x19;
                    value = TelnetParameter.getUdpPort();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                //reservaytion
                case 17://	DWORD	位置汇报策略，0：定时汇报；1：定距汇报；2：定时和定距汇报
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x20;
                    value = TelnetParameter.getUpPositionMode();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                case 18://	DWORD	位置汇报方案，0：根据ACC状态；1：根据登录状态和ACC状态，先判断登录状态，若登录再根据ACC状态
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x21;
                    value = TelnetParameter.getUpPositionMethods();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                case 19://	DWORD	驾驶员未登录汇报时间间隔，单位为秒(s),>0
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x22;
                    value = TelnetParameter.getUpPositionIntervalUnsigned();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                //reservation            case ://0x0023-0x0026	DWORD	保留
                case 20://	DWORD	休眠时汇报时间间隔，单位为秒(s),>0
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x27;
                    value = TelnetParameter.getUpPositionIntervalSleep();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                case 21://	DWORD	紧急报警时汇报时间间隔，单位为秒(s),>0
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x28;
                    value = TelnetParameter.getUpPositionIntervalUrgent();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                case 22://	DWORD	缺省时间汇报间隔，单位为秒(s),>0
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x29;
                    value = TelnetParameter.getUpPositionTimelDefault();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                //reservation 0x002A-0x002B	DWORD	保留
                case 23://	DWORD	缺省距离汇报间隔，单位为米(m),>0
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x2C;
                    value = TelnetParameter.getUpPositionDistancelDefault();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                case 24://	DWORD	驾驶员未登录汇报距离间隔，单位为米(m),>0
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x2D;
                    value = TelnetParameter.getUpPositionDistanceSleep();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                case 25://	DWORD	休眠时汇报距离间隔，单位为米(m),>0
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x2E;
                    value = TelnetParameter.getUpPositionDistanceUnsigned();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                case 26://	DWORD	紧急报警时汇报距离间隔，单位为米(m),>0
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x2F;
                    value = TelnetParameter.getUpPositionDistanceUrgent();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                case 27://	DWORD	拐点补传角度，<180°
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x30;
                    value = TelnetParameter.getUpPositionTurnAngle();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                case 33://	DWORD	终端电话接听策略，0：自动接听；1：ACC ON时自动接听，OFF时手动接听
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x45;
                    value = TelnetParameter.getAnswerCallMode();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                case 34://	DWORD	每次最长通话时间，单位为秒(s),0为不允许通话，0xFFFFFFFF为不限制
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x46;
                    value = TelnetParameter.getMaxCallingTimeOnce();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                case 35://	DWORD	当月最长通话时间，单位为秒(s),0为不允许通话，0xFFFFFFFF为不限制
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x47;
                    value = TelnetParameter.getMaxCallTimeMonth();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;

                //reservation 0x004A-0x004F		保留
                case 38://	DWORD	报警屏蔽字。与位置信息汇报消息中的报警标识相对应，相应位为1则相应报警被屏蔽
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x50;
                    value = TelnetParameter.getWarningMaskValue();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                case 39://	DWORD	报警发送文本SMS开关，与位置信息汇报消息中的报警标识相对应，相应位为1则相应报警时发送文本SMS
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x51;
                    value = TelnetParameter.getSendWarningSmsEn();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                case 40://	DWORD	报警拍摄开关，与位置信息汇报消息中的报警标识相对应，相应位为1则相应报警时摄像头拍摄
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x52;
                    value = TelnetParameter.getTakephotoForWarning();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                case 41://	DWORD	报警拍摄存储标识，与位置信息汇报消息中的报警标识相对应，相应位为1则对相应报警时牌的照片进行存储，否则实时长传
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x53;
                    value = TelnetParameter.getSaveWarningPhoto();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                case 42://	DWORD	关键标识，与位置信息汇报消息中的报警标识相对应，相应位为1则对相应报警为关键报警
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x54;
                    value = TelnetParameter.getEssentiallWarningFlag();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                case 43://	DWORD	最高速度，单位为公里每小时(km/h)
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x55;
                    value = TelnetParameter.getLimitSpeed();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                case 44://	DWORD	超速持续时间，单位为秒(s)
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x56;
                    value = TelnetParameter.getConstantOverspeedTime();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                case 45://	DWORD	连续驾驶时间门限，单位为秒(s)
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x57;
                    value = TelnetParameter.getConstantDrivingLimitOnce();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                case 46://	DWORD	当天累计驾驶时间门限，单位为秒(s)
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x58;
                    value = TelnetParameter.getConstantDrivingLimitDay();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                case 47://	DWORD	最小休息时间，单位为秒(s)
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x59;
                    value = TelnetParameter.getMinRestTime();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                case 48://	DWORD	最长停车时间，单位为秒(s)
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x5A;
                    value = TelnetParameter.getAllowStopTime();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                //reservation 0x005B-0x006F		保留
                case 49://	DWORD	图像/视频质量，1-10,1最好
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x70;
                    value = TelnetParameter.getPhotoQuality();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                case 50://	DWORD	亮度，0-255
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x71;
                    value = TelnetParameter.getPhotoLight();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                case 51://	DWORD	对比度，0-127
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x72;
                    value = TelnetParameter.getPhotoContrast();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                case 52://	DWORD	饱和度，0-127
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x73;
                    value = TelnetParameter.getPhotoSaturation();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                case 53://	DWORD	色度，0-255
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x74;
                    value = TelnetParameter.getPhotoChroma();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                //reservation 0x0075-0x007F	DWORD
                case 54://	DWORD	车辆里程表读数，1/10km
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = (byte) 0x80;
                    value = TelnetParameter.getTotalMiles();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                case 55://	WORD	车辆所在的省域ID
                    //TelnetParameter.setIntPoviceId(440000);
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = (byte) 0x81;
                    value = TelnetParameter.getIntPoviceId();
                    pdata[index++] = 2;
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                case 56://	WORD	车辆所在的市域ID
                    //TelnetParameter.setInCityId(440300);
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = (byte) 0x82;
                    value = TelnetParameter.getInCityId();
                    pdata[index++] = 2;
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                case 58://	byte	车牌颜色，按照JT/T415-2006的5.4.12
                    TelnetParameter.setPlateColor(2);
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = (byte) 0x84;
                    value = TelnetParameter.getPlateColor();
                    pdata[index++] = 1;
                    pdata[index++] = (byte) (value);
                    break;
                case 59:
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = (byte) 0x85;
                    value = TelnetParameter.getMileCoeff();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                case 7://	STRING	主服务器APN，无线通信拨号访问点。若网络制式为CDMA，则该处为PPP拨号号码
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = (byte) 0x10;
                    sValue = TelnetParameter.getHostApn();
                    try {
                        if (sValue != null && sValue.length() > 0) {
                            byte[] sb = sValue.getBytes("GBK");
                            pdata[index++] = (byte) sb.length;
                            System.arraycopy(sb, 0, pdata, index, sb.length);
                        }
                        index += sValue.getBytes("GBK").length;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
                case 8://	STRING	主服务器无线通信拨号用户名
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = (byte) 0x11;
                    sValue = TelnetParameter.getHostDialName();
                    try {
                        if (sValue != null && sValue.length() > 0) {
                            byte[] sb = sValue.getBytes("GBK");
                            pdata[index++] = (byte) sb.length;
                            System.arraycopy(sb, 0, pdata, index, sb.length);
                        }
                        index += sValue.getBytes("GBK").length;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
                case 9://	STRING	主服务器无线通信拨号密码
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = (byte) 0x12;
                    sValue = TelnetParameter.getHostDialPassword();
                    try {
                        if (sValue != null && sValue.length() > 0) {
                            byte[] sb = sValue.getBytes("GBK");
                            pdata[index++] = (byte) sb.length;
                            System.arraycopy(sb, 0, pdata, index, sb.length);
                        }
                        index += sValue.getBytes("GBK").length;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
                case 10://	STRING	主服务器地址，IP或域名
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = (byte) 0x13;
                    sValue = TelnetParameter.getHostIpAddress();
                    try {
                        if (sValue != null && sValue.length() > 0) {
                            byte[] sb = sValue.getBytes("GBK");
                            pdata[index++] = (byte) sb.length;
                            System.arraycopy(sb, 0, pdata, index, sb.length);
                        }
                        index += sValue.getBytes("GBK").length;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
                case 11://	STRING	备份服务器APN，无线通信拨号访问点
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = (byte) 0x14;
                    sValue = TelnetParameter.getBakApn();
                    try {
                        if (sValue != null && sValue.length() > 0) {
                            byte[] sb = sValue.getBytes("GBK");
                            pdata[index++] = (byte) sb.length;
                            System.arraycopy(sb, 0, pdata, index, sb.length);
                        }
                        index += sValue.getBytes("GBK").length;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
                case 12://	STRING	备份服务器无线通信拨号用户名
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = (byte) 0x15;
                    sValue = TelnetParameter.getBakDialName();
                    try {
                        if (sValue != null && sValue.length() > 0) {
                            byte[] sb = sValue.getBytes("GBK");
                            pdata[index++] = (byte) sb.length;
                            System.arraycopy(sb, 0, pdata, index, sb.length);
                        }
                        index += sValue.getBytes("GBK").length;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
                case 13://	STRING	备份服务器无线通信拨号密码
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = (byte) 0x16;
                    sValue = TelnetParameter.getBakDialPassword();
                    try {
                        if (sValue != null && sValue.length() > 0) {
                            byte[] sb = sValue.getBytes("GBK");
                            pdata[index++] = (byte) sb.length;
                            System.arraycopy(sb, 0, pdata, index, sb.length);
                        }
                        index += sValue.getBytes("GBK").length;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
                case 14://	STRING	备份服务器地址，IP或域名
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = (byte) 0x17;
                    sValue = TelnetParameter.getBakIpAddress();
                    try {
                        if (sValue != null && sValue.length() > 0) {
                            byte[] sb = sValue.getBytes("GBK");
                            pdata[index++] = (byte) sb.length;
                            System.arraycopy(sb, 0, pdata, index, sb.length);
                        }
                        index += sValue.getBytes("GBK").length;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
                //reservation 0x0031-0x003F		保留
                case 28://	STRING	监控平台电话号码
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = (byte) 0x40;
                    sValue = TelnetParameter.getTracePhotoNumber();
                    try {
                        if (sValue != null && sValue.length() > 0) {
                            byte[] sb = sValue.getBytes("GBK");
                            pdata[index++] = (byte) sb.length;
                            System.arraycopy(sb, 0, pdata, index, sb.length);
                        }
                        index += sValue.getBytes("GBK").length;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
                case 29://	STRING	复位电话号码，可采用此电话号码拨打终端电话让终端复位
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = (byte) 0x41;
                    sValue = TelnetParameter.getRestartPhotoNumnber();
                    try {
                        if (sValue != null && sValue.length() > 0) {
                            byte[] sb = sValue.getBytes("GBK");
                            pdata[index++] = (byte) sb.length;
                            System.arraycopy(sb, 0, pdata, index, sb.length);
                        }
                        index += sValue.getBytes("GBK").length;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
                case 30://	STRING	恢复出厂设置电话号码，可采用此电话号码拨打终端电话让终端恢复出厂设置
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = (byte) 0x42;
                    sValue = TelnetParameter.getResetPhoneNumber();
                    try {
                        if (sValue != null && sValue.length() > 0) {
                            byte[] sb = sValue.getBytes("GBK");
                            pdata[index++] = (byte) sb.length;
                            System.arraycopy(sb, 0, pdata, index, sb.length);
                        }
                        index += sValue.getBytes("GBK").length;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
                case 31://	STRING	监控平台SMS电话号码
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = (byte) 0x43;
                    sValue = TelnetParameter.getTraceSmsPhoneNumber();
                    try {
                        if (sValue != null && sValue.length() > 0) {
                            byte[] sb = sValue.getBytes("GBK");
                            pdata[index++] = (byte) sb.length;
                            System.arraycopy(sb, 0, pdata, index, sb.length);
                        }
                        index += sValue.getBytes("GBK").length;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
                case 32://	STRING	接收终端SMS文本报警号码
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = (byte) 0x44;
                    sValue = TelnetParameter.getReceiveSmsPhoneNumber();
                    try {
                        if (sValue != null && sValue.length() > 0) {
                            byte[] sb = sValue.getBytes("GBK");
                            pdata[index++] = (byte) sb.length;
                            System.arraycopy(sb, 0, pdata, index, sb.length);
                        }
                        index += sValue.getBytes("GBK").length;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
                case 36://	STRING	监听电话号码
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = (byte) 0x48;
                    sValue = TelnetParameter.getListenPhotoNumber();
                    try {
                        if (sValue != null && sValue.length() > 0) {
                            byte[] sb = sValue.getBytes("GBK");
                            pdata[index++] = (byte) sb.length;
                            System.arraycopy(sb, 0, pdata, index, sb.length);
                        }
                        index += sValue.getBytes("GBK").length;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
                case 37://	STRING	监管平台特权短信号码
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = (byte) 0x49;
                    sValue = TelnetParameter.getPrivilegeSmsPhoneNumber();
                    try {
                        if (sValue != null && sValue.length() > 0) {
                            byte[] sb = sValue.getBytes("GBK");
                            pdata[index++] = (byte) sb.length;
                            System.arraycopy(sb, 0, pdata, index, sb.length);
                        }
                        index += sValue.getBytes("GBK").length;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
                case 57://	String	公安交通管理部门颁发的机动车号牌
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = (byte) 0x83;
                    sValue = TelnetParameter.getPlateNumber() == null ? DeviceParameter.getLoginPlate() : TelnetParameter.getPlateNumber();
                    try {
                        if (sValue != null && sValue.length() > 0) {
                            byte[] sb = sValue.getBytes("GBK");
                            pdata[index++] = (byte) sb.length;
                            System.arraycopy(sb, 0, pdata, index, sb.length);
                        }
                        index += sValue.getBytes("GBK").length;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
        if (index > 0) {
            byte[] temp = new byte[index];
            System.arraycopy(pdata, 0, temp, 0, index);
            return temp;
        } else {
            return null;
        }
    }

    byte[] get8106Data1(int sequence, int totalNumber, int packNumber) {
        byte[] pdata = new byte[1500];
        int index = 0;
        int value;
        String sValue;

        pdata[index++] = (byte) (sequence >> 8);
        pdata[index++] = (byte) sequence;
        pdata[index++] = 4;
        pdata[index++] = 4;
        for (int i = 0; i < 4; i++) {
            switch (i) {
                case 0://	DWORD	客户端心跳发送间隔，单位为秒(s)
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x01;
                    value = TelnetParameter.getLinkPackageInterval();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                case 1://	DWORD	缺省时间汇报间隔，单位为秒(s),>0
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x29;
                    value = TelnetParameter.getUpPositionTimelDefault();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                //reservation 0x002A-0x002B	DWORD	保留
                case 2://	DWORD	当天累计驾驶时间门限，单位为秒(s)
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x58;
                    value = TelnetParameter.getConstantDrivingLimitDay();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                case 3:
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = 0x00;
                    pdata[index++] = (byte) 0x85;
                    value = TelnetParameter.getMileCoeff();
                    pdata[index++] = 4;
                    pdata[index++] = (byte) (value >> 24);
                    pdata[index++] = (byte) (value >> 16);
                    pdata[index++] = (byte) (value >> 8);
                    pdata[index++] = (byte) (value);
                    break;
                default:
                    break;
            }
        }
        if (index > 0) {
            byte[] temp = new byte[index];
            System.arraycopy(pdata, 0, temp, 0, index);
            return temp;
        } else {
            return null;
        }
    }

    private boolean isPromotTakePhoto = false;
    private long takePhotoTime = 0;

    public byte[] parserCmdData8900(byte[] rawBytes) {

        byte[] systemService = new byte[6];
        int ackType;
        int gdCmd;
        int property;
        int serial;
        byte[] deviceID;
        byte[] workTime = new byte[6];
        byte realtimeFlag;
        byte ackFlag;
        byte[] ackCoachNumber = null;
        byte[] ackStuNumber = null;
        byte[] ackInfo;
        int dataLen;
        byte[] dataContent = null;
        byte tempBytes[];
        byte queryMode;
        byte[] startTime = new byte[6];
        byte[] endTime = new byte[6];
        byte[] result;
        int index = 0;
        UpdateConfig updateConfig;
        Thread thread;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        ackType = rawBytes[index++];
        gdCmd = (rawBytes[index++] & 0xFF) << 8 | (rawBytes[index++] & 0xFF);
        property = (rawBytes[index++] & 0xFF) << 8 | (rawBytes[index++] & 0xFF);
        serial = (rawBytes[index++] & 0xFF) << 8 | (rawBytes[index++] & 0xFF);
        deviceID = new byte[16];
        System.arraycopy(rawBytes, index, deviceID, 0, deviceID.length);
        index += deviceID.length;
        dataLen = (rawBytes[index++] & 0xFF) << 24 | (rawBytes[index++] & 0xFF) << 16 | (rawBytes[index++] & 0xFF) << 8 | (rawBytes[index++] & 0xFF);
        dataContent = new byte[dataLen];
        System.arraycopy(rawBytes, index, dataContent, 0, dataLen);
        index += dataLen;

        if ((workSerial & 0xFFFF) == serial) {
            if (DEBUG) Log.i(TAG, "应答工作流水相等");
        } else {
            if (DEBUG) Log.i(TAG, "应答工作流水不同@@@");
        }
        if (DEBUG)
            Log.i(TAG, "获取8900下发数据，ID：0x" + Integer.toHexString(gdCmd) + "数据内容：" + StringUtils.bytesToHexString(dataContent));
        try {
            switch (gdCmd) {
                case 0x8101:

                    ackCoachNumber = new byte[16];
                    if (dataContent[0] == 1) {
                        if (DEBUG) Log.i(TAG, "正确应答教练数据");
//                        SharedPreferences sp = context.getSharedPreferences(SharedPreferencesUtil.REGISTER_INFO_FILE, MODE_PRIVATE);
//                        if(null != sp){
//                            if(!sp.getString(SharedPreferencesUtil.REGISTER_TYPE,"").equals(RainingActivity.CarType)){
//                                SharedPreferences.Editor editor = sp.edit();
//                                if(null != editor){
//                                    editor.putString(SharedPreferencesUtil.REGISTER_TYPE, RainingActivity.CarType);
//                                    editor.commit();
//                                }
//                            }
//                        }

                    } else {
                        if (DEBUG) Log.i(TAG, "应答教练登陆出错");
                    }
                    System.arraycopy(dataContent, 1, ackCoachNumber, 0, ackCoachNumber.length);
                    if ((dataContent[18] & 0xFF) > 0) {
                        ackInfo = new byte[dataContent[18] & 0xFF];
                        System.arraycopy(dataContent, 19, ackInfo, 0, ackInfo.length);
                        if (DEBUG) try {
                            Log.i(TAG, "上报教练员登陆应答：" + new String(ackInfo, "GBK"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 0x8102:
                    ackCoachNumber = new byte[16];
                    ThisLog("教练签到应答" + dataContent[0]);
                    if (dataContent[0] == 1) {
                        Log.i(TAG, "正确应答教练签退");

                    } else {
                        Log.i(TAG, "应答教练签退出错");
                    }
                    System.arraycopy(dataContent, 1, ackCoachNumber, 0, ackCoachNumber.length);
                    break;
                case 0x8201:
//                    AppContext.data = dataContent;
                    switch (dataContent[0]) {
                        case 1:
                            if (DEBUG) Log.i(TAG, "学员登陆成功");
                            break;
                        case 2:
                            if (DEBUG) Log.i(TAG, "无效的学员编号");
                            break;
                        case 3:
                            if (DEBUG) Log.i(TAG, "禁止学员登录");
                            break;
                        case 4:
                            if (DEBUG) Log.i(TAG, "区域外教学提醒");
                            break;
                        case 5:
                            if (DEBUG) Log.i(TAG, "准教车型不符");
                            break;
                        default:
                            if (DEBUG) Log.i(TAG, "学员登陆失败");
                            break;
                    }
                    tempBytes = new byte[16];
                    System.arraycopy(dataContent, 1, tempBytes, 0, tempBytes.length);
                    if (Arrays.equals(CommonInfo.getStuNumber(), tempBytes)) {
                        Log.i(TAG, "学员编号相同");
                        CommonInfo.setValidAckSignout(true);
                        CommonInfo.setCurItemOutline((dataContent[17] & 0xFF) * 256 + (dataContent[18] & 0xFF));//总培训学时
                        CommonInfo.setCurItemLearned((dataContent[19] & 0xFF) * 256 + (dataContent[20] & 0xFF));//当前部分培训学时
                        CommonInfo.setCurMilesOutline((dataContent[21] & 0xFF) * 256 + (dataContent[22] & 0xFF));//总培训里程
                        CommonInfo.setCurMilesLearned((dataContent[23] & 0xFF) * 256 + (dataContent[24] & 0xFF));//当前培训部分里程
                        CommonInfo.setCurStuValid(dataContent[0]);

                        System.out.println("学员登录应答----总培训学时：" + CommonInfo.getCurItemOutline());
                        System.out.println("学员登录应答----当前部分培训学时：" + CommonInfo.getCurItemLearned());
                        System.out.println("学员登录应答----总培训里程：" + CommonInfo.getCurMilesOutline());
                        System.out.println("学员登录应答----当前部分培训里程：" + CommonInfo.getCurMilesLearned());
                    } else {
                        Log.i(TAG, "学员编号不同" + new String(tempBytes));
                    }
                    Log.i(TAG, "返回登陆科目：" + dataContent[17]);

                    Log.i(TAG, "总学时" + ((dataContent[18] & 0xFF) * 256 + (dataContent[19] & 0xFF)) + "当前科目已完成学时" + ((dataContent[20] & 0xFF) * 256 + (dataContent[21] & 0xFF)));
                    if (dataContent[25] == 0) {
                        //根据全局设备决定读报
                    } else if (dataContent[25] == 1) {
                        //需要读报
                    } else {
                        //不需要读报
                    }
                    if (dataContent[26] > 0) {
                        byte[] appendData = new byte[dataContent[26]];
                        System.arraycopy(dataContent, 27, appendData, 0, appendData.length);
                        try {
                            String show = new String(appendData, "gbk");
                            Log.i(TAG, "播报信息：" + show);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
//                    tempBytes = new byte[8];
//                    System.arraycopy(dataContent, 9, tempBytes, 0, tempBytes.length);
//                    if (Arrays.equals(CommonInfo.getStuNumber(), tempBytes)) {
//                        if (DEBUG) Log.i(TAG, "学员编号相同");
//                        CommonInfo.setValidAckSignout(true);
//                        CommonInfo.setCurItemOutline((dataContent[18] & 0xFF) * 256 + (dataContent[19] & 0xFF));
//                        CommonInfo.setCurItemLearned((dataContent[20] & 0xFF) * 256 + (dataContent[21] & 0xFF));
//                        CommonInfo.setCurStuValid(dataContent[0]);
//                    } else {
//                        if (DEBUG) Log.i(TAG, "学员编号不同"+new String(tempBytes)+"::"+new String(CommonInfo.getStuNumber()));
//                    }
                    ackStuNumber = new byte[16];
                    System.arraycopy(dataContent, 1, ackStuNumber, 0, ackStuNumber.length);
                    break;
                case 0x8202:
                    ackStuNumber = new byte[16];
                    System.arraycopy(dataContent, 1, ackStuNumber, 0, ackStuNumber.length);
                    break;

                case 0x8205:
                    queryMode = dataContent[0];
                    System.arraycopy(dataContent, 1, startTime, 0, startTime.length);
                    System.arraycopy(dataContent, 7, endTime, 0, endTime.length);
                    //查询
                    int listNumber;
                    listNumber = dataContent[13] & 0xFF;
                    HandleUpQueryInfo handleUpQueryInfo = new HandleUpQueryInfo(gdCmd, queryMode, startTime, endTime, dataContent[13] & 0xFF);
                    thread = new Thread(handleUpQueryInfo);
                    thread.start();
                    break;


                case 0x8301:
                    byte upMode = dataContent[0];
                    if (upMode == 0x01)//自动上传
                    {

                    } else if (upMode == (byte) 0xFF) { //停止上传

                    } else {

                    }
                    byte photeChanal = dataContent[1];
//                SQCIF（160x120）	0
//                QCIF（176x144）	1
//                CIF（352x288）	2
//                QQVGA（160x120）	3
//                QVGA（320x240）	4
//                VGA（640x480）	5
                    byte picSize = dataContent[2];
                    Utils.saveRunningLog("Receive command 0301 for take photo immediatly");
                    //拍照并保存,请求
                    sendMsgToUi(MessageDefine.MSG_TAKE_PHOTO_PROMPT);
                    //应答结果
                    result = new byte[4];
                    result[0] = 0x01;
                    result[1] = upMode;
                    result[2] = photeChanal;
//                    0x01：320×240；
//                    0x02：640×480；
//                    0x03：800×600；
//                    0x04：1024×768；
//                    0x05：176×144[Qcif]；
//                    0x06：352×288[Cif]；
//                    0x07：704×288[HALF D1]；
//                    0x08：704×576[D1]；
                    result[3] = 0x02;//实际上传尺寸
                    tempBytes = getCmdData(GPRS_ID_CMD0900, GD_CMD_TAKE_PHOTO, (byte) 0x00, result, 0);
                    sendTcpBytes(tempBytes);
                    isPromotTakePhoto = true;
                    takePhotoTime = System.currentTimeMillis();
                    break;
                case 0x8302:
                    queryMode = dataContent[0];
                    System.arraycopy(dataContent, 1, startTime, 0, startTime.length);
                    System.arraycopy(dataContent, 7, endTime, 0, endTime.length);
                    //查询成功后通过MSG0303上报照片信息
                    HandleUpPhotoHead handleUpPhotoHead = new HandleUpPhotoHead(queryMode, startTime, endTime);
                    thread = new Thread(handleUpPhotoHead);
                    thread.start();
                    break;
                case 0x8305://响应上传照片请求
                    if (dataContent[0] == 0)//接受上传请求
                    {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                upPhotoPackage();
                            }
                        }).start();
//                        if (DEBUG) Log.i(TAG, "开始上传照片");
//                        byte[] photoContent = new byte[PHOTO_PACKAGE_SIZE];
//                        byte flag;
//                        long waitTime;
//                        transState.setState(0x8900, GD_REQUIR_VERIFY_PHOTO, transState.USING);
//                        File file = new File(curPhotoHead.filePath);
//                        FileInputStream fis = new FileInputStream(file);
//                        int readLen;
//                        FileUtils.saveRunningLog("start send photo, size:" + file.length());
//                        tempBytes = new byte[512+10];
//                        for (int i = 1; i <= curPhotoHead.totalPackages; i++) {
//                            if (i == 1)
//                            {
//                                readLen = fis.read(photoContent);
//                                tempBytes = new byte[10+readLen];
//                                System.arraycopy(curPhotoHead.photoSerial, 0, tempBytes, 0, 10);
//                                System.arraycopy(photoContent, 0, tempBytes, 10, readLen);
//
//                            }
//                            else
//                            {
//                                readLen = fis.read(photoContent);
//                                tempBytes = new byte[readLen];
//                                System.arraycopy(photoContent, 0, tempBytes, 0, readLen);
//                            }
//                            byte[] sendBytes = getCmdData(0x0900, GD_UP_PHOTO_PACKAGE, (byte) 1, tempBytes, i, curPhotoHead.totalPackages);
//                            sendTcpBytes(sendBytes);
//                            sleep(100);
////                            waitFor0900Ack = true;
////                            waitTime = System.currentTimeMillis();
////                            do {
////                                sleep(100);
////                            } while (waitFor0900Ack && System.currentTimeMillis() - waitTime < 10000);
//                        }
//                        fis.close();
//                        FileUtils.saveRunningLog("send photo file complished");
////                        for (int i = 0; i < curPhotoHead.totalPackages; i++) {
////                            if (i == curPhotoHead.totalPackages - 1) //the last package
////                            {
////                                photoContent = readFileByRandomAccess(curPhotoHead.filePath, i * 512, (int) (curPhotoHead.fileSize - i * 512));
////                                flag = 0x40;
////                                tempBytes = CommonInfo.getPicturePackage(flag, i + 1, photoContent.length, curPhotoHead.photoSerial, photoContent);
////                            } else {
////                                photoContent = readFileByRandomAccess(curPhotoHead.filePath, i * 512, 512);
////                                flag = 0x00;
////                                tempBytes = CommonInfo.getPicturePackage(flag, i + 1, 512, curPhotoHead.photoSerial, photoContent);
////                            }
////                            byte[] sendBytes = getCmdData(0x0900, GD_UP_PHOTO_PACKAGE, (byte) 0, tempBytes);
////                            sendTcpBytes(sendBytes);
////                        }
////                        byte[] requestContent = new byte[12];
////                        System.arraycopy(curPhotoHead.photoSerial, 0, requestContent, 0, 10);
////                        requestContent[10] = (byte) (curPhotoHead.totalPackages / 256);
////                        requestContent[11] = (byte) (curPhotoHead.totalPackages % 256);
////                        byte[] sendBytes = getCmdData(0x0900, GD_REQUIR_VERIFY_PHOTO, (byte) 1, requestContent);
////                        sendTcpBytes(sendBytes);
//                        transState.setState(0x8900, GD_REQUIR_VERIFY_PHOTO, transState.WAIT_ACK);
//                        int timeCnt = 0;
//                        while (transState.getState() == transState.WAIT_ACK && transState.getWaitAckSubCmd() == GD_REQUIR_VERIFY_PHOTO) {
//                            Thread.sleep(100);
//                            if (timeCnt++ > 10) {
//                                FileUtils.saveRunningLog("request ack timeout, send 0307 command");
//                                byte[] requestContent = new byte[12];
//                                System.arraycopy(curPhotoHead.photoSerial, 0, requestContent, 0, 10);
//                                requestContent[10] = (byte) (curPhotoHead.totalPackages / 256);
//                                requestContent[11] = (byte) (curPhotoHead.totalPackages % 256);
//                                byte[] sendBytes = getCmdData(0x0900, GD_REQUIR_VERIFY_PHOTO, (byte) 1, requestContent);
//                                sendTcpBytes(sendBytes);
//                                transState.setState(0x8900, GD_REQUIR_VERIFY_PHOTO, transState.WAIT_ACK);
//                                break;
//                            }
//                        }
//                        if (DEBUG) Log.i(TAG, "照片上传结束");

                    } else if (dataContent[0] == (byte) 0xFF)//拒绝上传
                    {

                    } else {
                        if (DEBUG) Log.i(TAG, "响应上传请求有误");
                    }
                    break;
                case 0x8304:
                    byte[] picNumber = new byte[10];
                    System.arraycopy(dataContent, 0, picNumber, 0, picNumber.length);
                    HandleUpPhotoData handleUpPhotoData = new HandleUpPhotoData(picNumber);
                    Thread myThread = new Thread(handleUpPhotoData);
                    myThread.start();
                    break;
                case GD_CMD_TAKE_VIDEO: //命令即时拍摄视频
                    if (dataContent[0] == 0x01) {
                        //
                    } else if (dataContent[0] == (byte) 0xFF)//停止拍摄并上传
                    {
                        //
                    } else {
                        if (DEBUG) Log.i(TAG, "立即拍摄命令上传模式解析出错");
                    }
                    byte channalNumber = dataContent[1];//0自动 1~255表示指定通道
                    break;
                case GD_CMD_QUERY_VIDEO:
                    break;
                case GD_CMD_UP_VIDEO:

                    break;
                case GD_CMD_INIT_VIDEO:
                    if (dataContent[0] == 0)//接受上传请求
                    {
                        if (DEBUG) Log.i(TAG, "开始上传视频");
                        byte[] photoContent = new byte[PHOTO_PACKAGE_SIZE];
                        byte flag;
                        isSendingVideo = true;
                        transState.setState(0x8900, GD_CMD_INIT_VIDEO, transState.USING);
                        File file = new File(curVideoHead.filePath);
                        FileInputStream fis = new FileInputStream(file);
                        int readLen;
                        Utils.saveRunningLog("start send video, size:" + file.length());
                        for (int i = 0; i < curVideoHead.totalPackages; i++) {
                            if (i == curVideoHead.totalPackages - 1) //the last package
                            {
                                //Thread.sleep(2000);
                                flag = 0x40;
                            } else {
                                flag = 0x00;
                            }
                            //Thread.sleep(100);
                            readLen = fis.read(photoContent);
                            tempBytes = CommonInfo.getPicturePackage(flag, i + 1, readLen, curVideoHead.photoSerial, photoContent);
                            byte[] sendBytes = getCmdData(0x0900, GD_UP_VIDEO_PACKAGE, (byte) 0, tempBytes, 0);
                            sendTcpBytes(sendBytes);
                        }
                        fis.close();
                        Utils.saveRunningLog("send video file complished");
                        byte[] requestContent = new byte[12];
                        System.arraycopy(curVideoHead.photoSerial, 0, requestContent, 0, 10);
                        requestContent[10] = (byte) (curVideoHead.totalPackages / 256);
                        requestContent[11] = (byte) (curVideoHead.totalPackages % 256);
                        byte[] sendBytes = getCmdData(0x0900, GD_UP_REQUIR_VERIFY_VIDEO, (byte) 1, requestContent, 0);
                        sendTcpBytes(sendBytes);
                        transState.setState(0x8900, GD_UP_REQUIR_VERIFY_VIDEO, transState.WAIT_ACK);
                        if (DEBUG) Log.i(TAG, "视频上传结束");
                    } else if (dataContent[0] == (byte) 0xFF)//拒绝上传
                    {

                    } else {
                        if (DEBUG) Log.i(TAG, "响应上传请求有误");
                    }
//
                    break;
                case GD_CMD_QUERY_SOFT_VERSION:
//                    case 0x01://终端软件1
//                    case 0x02://终端软件2
//                    case 0x03://终端软件3
//                    case 0x09://终端配置文件
                    byte softType = dataContent[0];
                    result = DeviceParameter.getSoftVersion1(softType);
                    tempBytes = getCmdData(GPRS_ID_CMD0900, GD_CMD_QUERY_SOFT_VERSION, (byte) 0x00, result, 0);
                    sendTcpBytes(tempBytes);
                    break;
                case GD_CMD_UPGRADE_SOFT:
                    updateConfig = new UpdateConfig();
                    updateConfig.setSoftType(dataContent[0]);
                    byte[] softVersion = new byte[4];
                    System.arraycopy(dataContent, 1, softVersion, 0, 4);
                    updateConfig.setSoftVersion(new String(softVersion));
                    String sTime = new String().format("20%02X-%02X-%02X %02X:%02X:%02X", dataContent[5] & 0xFF, dataContent[6] & 0xFF, dataContent[7] & 0xFF, dataContent[8] & 0xFF, dataContent[9] & 0xFF, dataContent[10] & 0xFF);
                    try {
                        Date workDate = sdf.parse(sTime);
                        updateConfig.setWorkTime(workDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    updateConfig.setUpdateMode(dataContent[11]);
                    byte[] apn = new byte[30];
                    try {
                        updateConfig.setApn(new String(apn, "GBK"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String ipAddress = String.format("%d.%d.%d.%d", dataContent[42] & 0xFF, dataContent[43] & 0xFF, dataContent[44] & 0xFF, dataContent[45] & 0xFF);
                    updateConfig.setIp(ipAddress);
                    updateConfig.setPort((dataContent[46] & 0xFF) * 256 + (dataContent[47] & 0xFF));
                    updateConfig.setReservation(dataContent[48] & 0xFF);
                    result = new byte[50];
                    result[0] = 1;
                    System.arraycopy(dataContent, 0, result, 1, 49);
                    tempBytes = getCmdData(GPRS_ID_CMD0900, GD_CMD_UPGRADE_SOFT, (byte) 0x00, result, 0);
                    sendTcpBytes(tempBytes);
                    break;
                case 0x8501:
                    result = new byte[1];//应答成功
                    if (DeviceParameter.setTelnetParameter(dataContent)) {
                        result[0] = 0x01;
                    } else {
                        result[0] = 0x02;
                    }
                    tempBytes = getCmdData(GPRS_ID_CMD0900, GD_CMD_SET_PARAMETER, (byte) 0x00, result, 0);

                    sendTcpBytes(tempBytes);

                    break;
                case 0x8502://设置禁考模式
                    DeviceParameter.setIsExaminationEn(dataContent[0]);
                    if (dataContent[1] > 0) {
                        byte[] appendData = new byte[dataContent[1]];
                        System.arraycopy(dataContent, 2, appendData, 0, appendData.length);
                        try {
                            String show = new String(appendData, "gbk");
                            if (DEBUG) Log.i(TAG, "播报信息：" + show);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    result = new byte[3];
                    result[0] = 1;
                    result[1] = DeviceParameter.getIsExaminationEn();
                    result[2] = 0;
                    tempBytes = getCmdData(GPRS_ID_CMD0900, GD_CMD_SET_FORBID, (byte) 0x00, result, 0);
                    sendTcpBytes(tempBytes);
                    DeviceParameter.createParameterXML();//保存设备参数
                    sendMsgToUi(111111);
                    break;
                case 0x8503:
                    result = DeviceParameter.getTelnetParameter();
                    tempBytes = getCmdData(GPRS_ID_CMD0900, GD_CMD_QUERY_PARAMETER, (byte) 0x00, result, 0);
                    sendTcpBytes(tempBytes);
                    break;
                /********************************* 应答终端透传透传命令 ******************************************************************/
                case 0x8203:
                    if (DEBUG) Log.i(TAG, "接收培训记录上传应答");
                    if (wait_for_up_query_trainlist == 4)//正在上传查询记录
                    {
                        wait_for_up_query_trainlist = 0;
                    }
                    break;
                case GD_UP_DRIVING_BEHAVIOR:
                    break;
                case 0x8303:
                    if (dataContent[0] == 0)//默认应答
                    {
                        wait_for_up_query_photo = 0;
                    } else if (dataContent[0] == 1)//停止上报查询结果
                    {
                        wait_for_up_query_photo = 1;
                    } else {
                        wait_for_up_query_photo = 2;
                        if (DEBUG) Log.i(TAG, "照片查询上传应答出错");
                    }
                    break;
                case GD_UP_PHOTO_PACKAGE://平台不应答此消息
                    break;
                case GD_REQUIR_VERIFY_PHOTO:
                    if (dataContent[0] == 0)//应接接收完整
                    {
                        Update_DB datebase_update = new Update_DB();
                        datebase_update.clearPhotoHeadFlag(StringUtils.bytesToHexString(curPhotoHead.photoSerial));
                        if (myPhotoHeadInfo != null && myPhotoHeadInfo.size() > 0) {
                            myPhotoHeadInfo.remove(0); //清空照片上传标识
                            SharedPreferences sendMessageCount = context.getSharedPreferences("SendMessageCount", MODE_PRIVATE);
                            SharedPreferences.Editor sendMessageEditor = sendMessageCount.edit();
                            int phoptCount = sendMessageCount.getInt("PhoptCount", 0);
                            sendMessageEditor.putInt("PhoptCount", phoptCount - 1);
                            sendMessageEditor.commit();
                            sendMsgToUi(10086);
                        }
                        transState.clearState(transState.USING);
                        isSendingVideo = false;
                        if (DEBUG) Log.i(TAG, "应答照片传输完成");
                    } else if (dataContent[0] == 1)//重传指定数据包
                    {
                        if (DEBUG) Log.i(TAG, "平台下发补传照片");
                        Utils.saveRunningLog("receive patch command");
                        int resendPackageSerial;
                        resendPackageSerial = (dataContent[1] & 0xFF) * 256 + (dataContent[2] & 0xFF);
                        if (curPhotoHead != null && curPhotoHead.stuNumber != null && curPhotoHead.photoSerial != null && curPhotoHead.filePath != null) {
                            byte[] photoContent;
                            if (resendPackageSerial == curPhotoHead.totalPackages) {
                                photoContent = readFileByRandomAccess(curPhotoHead.filePath, (resendPackageSerial - 1) * PHOTO_PACKAGE_SIZE, (int) (curPhotoHead.fileSize - (resendPackageSerial - 1) * PHOTO_PACKAGE_SIZE));
                            } else {
                                photoContent = readFileByRandomAccess(curPhotoHead.filePath, (resendPackageSerial - 1) * PHOTO_PACKAGE_SIZE, PHOTO_PACKAGE_SIZE);
                            }
                            tempBytes = CommonInfo.getPicturePackage((byte) 0x40, resendPackageSerial, PHOTO_PACKAGE_SIZE, curPhotoHead.photoSerial, photoContent);
                            byte[] sendBytes = getCmdData(0x0900, GD_UP_PHOTO_PACKAGE, (byte) 0, tempBytes, 0);
                            sendTcpBytes(sendBytes);
                        }
                        transState.setState(0x8900, GD_REQUIR_VERIFY_PHOTO, transState.WAIT_ACK);
                    } else if (dataContent[0] == (byte) 0xFF)//停止传输
                    {
                        if (DEBUG) Log.i(TAG, "应答停止照片上传");
                    } else {

                    }
                    int packageNumber = (dataContent[1] & 0xFF) * 256 + (dataContent[2] & 0xFF);
                    break;
                case GD_UP_QUERY_VIDEO:
                    break;
                case GD_UP_VIDEO_PACKAGE:
                    break;
                case GD_UP_REQUIR_VERIFY_VIDEO:
                    if (dataContent[0] == 0)//应接接收完整
                    {
                        Update_DB datebase_update = new Update_DB();
                        datebase_update.clearVideoHeadFlag(StringUtils.bytesToHexString(curVideoHead.photoSerial));
                        if (myVideoHeadInfo != null && myVideoHeadInfo.size() > 0) {
                            myVideoHeadInfo.remove(0); //清空视频上传标识
                        }
                        transState.clearState(transState.USING);
                        if (DEBUG) Log.i(TAG, "应答视频传输完成");
                    } else if (dataContent[0] == 1)//重传指定数据包
                    {
                        if (DEBUG) Log.i(TAG, "平台下发补传视频文件");
                        int resendPackageSerial;
                        resendPackageSerial = (dataContent[1] & 0xFF) * 256 + (dataContent[2] & 0xFF);
                        byte[] photoContent;
                        if (resendPackageSerial == curVideoHead.totalPackages) {
                            photoContent = readFileByRandomAccess(curVideoHead.filePath, (resendPackageSerial - 1) * PHOTO_PACKAGE_SIZE, (int) (curVideoHead.fileSize - (resendPackageSerial - 1) * PHOTO_PACKAGE_SIZE));
                        } else {
                            photoContent = readFileByRandomAccess(curVideoHead.filePath, (resendPackageSerial - 1) * PHOTO_PACKAGE_SIZE, PHOTO_PACKAGE_SIZE);
                        }
                        tempBytes = CommonInfo.getPicturePackage((byte) 0x40, resendPackageSerial, PHOTO_PACKAGE_SIZE, curVideoHead.photoSerial, photoContent);
                        byte[] sendBytes = getCmdData(0x0900, GD_UP_VIDEO_PACKAGE, (byte) 0, tempBytes, 0);
                        sendTcpBytes(sendBytes);
                        transState.setState(0x8900, GD_UP_REQUIR_VERIFY_VIDEO, transState.WAIT_ACK);
                    } else if (dataContent[0] == (byte) 0xFF)//停止传输
                    {
                        if (DEBUG) Log.i(TAG, "应答停止视频上传");
                    } else {

                    }
                    break;
                case GD_UP_UPGRADE_ACK:
                    if (dataContent[0] == 0)//应答升级反馈
                    {
                    } else {
                    }
                case GD_UP_REQUEST_AREA:
                    //查询结果	BYTE	必选
                    //符合的训练场地个数N	BYTE	可选
                    //训练场地编号1	ASCII[6]	可选
                    //训练场地编号2	ASCII[6]	可选
                    if (dataContent[0] == 1) {
                        areasListCnt = (dataContent[1] & 0xFF);
                        tempBytes = new byte[areasListCnt * 6];
                        System.arraycopy(dataContent, 2, tempBytes, 0, tempBytes.length);
                        if (false && StringUtils.arrayCompareWithSegment(areasList, tempBytes, 6)) {
                            areasSendIndex = areasListCnt;
                            if (DEBUG) Log.i(TAG, "围栏信息没有变化！");
                        } else {
                            Delete_DB.getInstance().clearFenceData();// 清空电子围栏数据
                            areasList = new byte[6 * areasListCnt];
                            areasSendIndex = 0;
                            System.arraycopy(dataContent, 2, areasList, 0, areasList.length);
                        }
                        if (DEBUG)
                            Log.i(TAG, "训练场地查询结果：" + dataContent[0] + " 场地个数：" + areasListCnt);
                    } else {
                        if (DEBUG) Log.i(TAG, "场地查询失败！！！");
                    }
                    break;
                case GD_UP_GET_AREA:
                    //查询结果	BYTE 1、成功  2、失败
                    //训练场地编号	ASCII[6]
                    //区域总定点数	WORD
                    //顶点1纬度	DWORD
                    //顶点1经度	DWORD
                    //.......
                    areasSendIndex++;
                    if (dataContent[0] == 1) {
                        int pointNumber;
                        int fenceMode;
                        tempBytes = new byte[6];
                        System.arraycopy(dataContent, 1, tempBytes, 0, tempBytes.length);
                        try {
                            String fenceID = new String(tempBytes, "GBK");
                            fenceMode = dataContent[7];
                            pointNumber = (dataContent[8] & 0xFF) * 256 + (dataContent[9] & 0xFF);
                            tempBytes = new byte[pointNumber * 8];
                            System.arraycopy(dataContent, 10, tempBytes, 0, tempBytes.length);
                            Date time = new Date();
                            if (Insert_DB.getInstance().insertFence(fenceID, fenceMode, pointNumber, tempBytes, time)) {
                                if (DEBUG) Log.i(TAG, "电子围栏存储成功");
                            } else {
                                if (DEBUG) Log.i(TAG, "电子围栏存储失败");
                            }
//                        loadFencePointData();//重新加载电子围栏
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (DEBUG) Log.i(TAG, "围栏信息下载失败！！！");
                    }
                    break;
                case GD_UP_UPDATE_ORDER:
                    if (dataContent[0] == 1) {
                        ordersListCnt = (dataContent[1] & 0xFF);
                        tempBytes = new byte[ordersListCnt * 8];
                        System.arraycopy(dataContent, 2, tempBytes, 0, tempBytes.length);
                        if (StringUtils.arrayCompareWithSegment(ordersList, tempBytes, ordersListCnt)) {
                            ordersSendIndex = ordersListCnt;
                            if (DEBUG) Log.i(TAG, "订单信息没有变化！");
                        } else {
                            Delete_DB.getInstance().clearOrder();//删除历史订单数据
                            ordersList = new byte[8 * ordersListCnt];
                            ordersSendIndex = 0;
                            System.arraycopy(dataContent, 2, ordersList, 0, ordersList.length);
                        }
                        if (DEBUG) Log.i(TAG, "同步订单个数：" + ordersListCnt);
                    } else {
                        ordersListCnt = 0;
                        ordersSendIndex = ordersListCnt;
                        if (DEBUG) Log.i(TAG, "订单查询失败！！！");
                    }
                    break;


                default:
                    break;
            }
            //no here
            if (transState.getWaitAckSubCmd() == gdCmd) {
                if (System.currentTimeMillis() - sendDBDataTimeMillis < 20000)//10秒内应答有效
                {
                    Update_DB datebase_update = new Update_DB();
                    switch (gdCmd) //清空数据上传标志
                    {
                        case 0x0200:
//                            Log.d(TAG,"接收点位信息成功");
                            break;
                        case 0x8101:
                            if (ackCoachNumber != null && datebase_update.clearCoachSignFlag(new String(ackCoachNumber), 1)) {
                                if (DEBUG) Log.i(TAG, "清除数据库教练签到标志");
                            } else {
                                if (DEBUG) Log.i(TAG, "教练签到数据库更新失败");
                            }
                            break;
                        case 0x8102:
                            if (ackCoachNumber != null && datebase_update.clearCoachSignFlag(new String(ackCoachNumber), 2)) {
                                if (DEBUG) Log.i(TAG, "清除数据库教练签退标志");
                            } else {
                                if (DEBUG) Log.i(TAG, "教练签退数据库更新失败");
                            }
                            break;
                        case 0x8201:
                            if (datebase_update.clearStuSignFlag(String.valueOf(currentClassId),1)){
//                            if (datebase_update.clearStuSignFlag(new String(ackStuNumber), 1)) {
                                isStuLogin = false;
                                Log.e(TAG, "清除数据库中学员签到标志");
                                if (DEBUG) Log.i(TAG, "清除数据库中学员签到标志");
                            } else {
                                Log.e(TAG, "学员签到数据库更新失败");
                                if (DEBUG) Log.i(TAG, "学员签到数据库更新失败");
                            }
                            break;
                        case 0x8202:
                            if (datebase_update.clearStuSignFlag(String.valueOf(currentClassId),2)) {
//                            if (datebase_update.clearStuSignFlag(new String(ackStuNumber), 2)) {
                                isStuLogin = true;
                                Log.e(TAG, "清除数据库中学员签退标志");
                                if (DEBUG) Log.i(TAG, "清除数据库中学员签退标志");
                            } else {
                                Log.e(TAG, "学员签退数据库更新失败");
                                if (DEBUG) Log.i(TAG, "学员签退数据库更新失败");
                            }
                            break;
                        case 0x8203:
                            if (wait_for_up_query_trainlist == 4)//正在上传查询记录
                            {
                                wait_for_up_query_trainlist = 0;
                            } else if (sendingTrainListSerial != null && datebase_update.clearTrainListFlag(sendingTrainListSerial)) {
                                if (DEBUG) Log.i(TAG, "清除数据库中培训记录标志");
                                SharedPreferences sendMessageCount = context.getSharedPreferences("SendMessageCount", MODE_PRIVATE);
                                SharedPreferences.Editor sendMessageEditor = sendMessageCount.edit();
                                int trainCount = sendMessageCount.getInt("TrainCount", 0);
                                sendMessageEditor.putInt("TrainCount", trainCount - 1);
                                sendMessageEditor.commit();
                                sendingTrainListSerial = null;
                            } else {
                                if (DEBUG) Log.i(TAG, "培训记录数据库更新失败");
                            }
                            break;
                        case GD_UP_DRIVING_BEHAVIOR:
                            if (datebase_update.clearDrivingFlag(Select_DB.trainlistSerial)) {
                                if (DEBUG) Log.i(TAG, "清除数据库中驾驶行为上传标志");
                            } else {
                                if (DEBUG) Log.i(TAG, "驾驶行为数据库更新失败");
                            }
                            break;
                    }
                }
                transState.clearState(transState.WAIT_ACK);
                if (DEBUG) Log.i(TAG, "获得应答命令" + Integer.toHexString(gdCmd));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return dataContent;
    }

    class HandleUpPhotoData implements Runnable {
        byte[] photoID;
        byte[] result = new byte[1];
        byte[] tempBytes;

        public HandleUpPhotoData(byte[] photoID) {
            this.photoID = photoID;
        }

        @Override
        public void run() {
            String sPhotoID;
            sPhotoID = StringUtils.bytesToHexString(photoID);
            PhotoHeadInformation curHeadInformation = Select_DB.getInstance().getPiturePath(sPhotoID);
            if (curHeadInformation != null) {
                result[0] = 0x00;//00找到照片稍后上传 01没有该照片 02执行成功，正在查找
                tempBytes = getCmdData(GPRS_ID_CMD0900, GD_CMD_UP_PHOTO, (byte) 0x00, result, 0);
                sendTcpBytes(tempBytes);
                if (myPhotoHeadInfo == null) {
                    myPhotoHeadInfo = new ArrayList<PhotoHeadInformation>();
                }
                myPhotoHeadInfo.add(curHeadInformation);
                Utils.saveRunningLog("Uploading photo for telnet command");
                uploadPhoto();
            } else {
                result[0] = 0x01;//00找到照片稍后上传 01没有该照片 02执行成功，正在查找
                tempBytes = getCmdData(GPRS_ID_CMD0900, GD_CMD_UP_PHOTO, (byte) 0x00, result, 0);
                sendTcpBytes(tempBytes);
            }
        }
    }

    // 处理0302图片头命令上传
    class HandleUpPhotoHead implements Runnable {
        int queryMode;
        byte[] startTime;
        byte[] endTime;
        Date startDate = null;
        Date endDate = null;

        public HandleUpPhotoHead(int queryMode, byte[] startTime, byte[] endTime) {
            this.queryMode = queryMode;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        @Override
        public void run() {
            int waitCount;
            byte[] tempBytes;
            byte[] result;
            byte[] photoHeadData = new byte[512];
            byte[] photoID;
            result = new byte[1];
            result[0] = 0x01;//开始查询
            tempBytes = getCmdData(GPRS_ID_CMD0900, GD_CMD_QUERY_PHOTO, (byte) 0x00, result, 0);
            sendTcpBytes(tempBytes);
            ArrayList<PhotoHeadInformation> myList;
            PhotoHeadInformation curHeadInformation;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            int page = 0;
            try {
                String sStart = String.format("20%02X-%02X-%02X %02X:%02X:%02X", startTime[0] & 0xFF, startTime[1] & 0xFF, startTime[2] & 0xFF, startTime[3] & 0xFF, startTime[4] & 0xFF, startTime[5] & 0xFF);
                String sEnd = String.format("20%02X-%02X-%02X %02X:%02X:%02X", endTime[0] & 0xFF, endTime[1] & 0xFF, endTime[2] & 0xFF, endTime[3] & 0xFF, endTime[4] & 0xFF, endTime[5] & 0xFF);
                startDate = sdf.parse(sStart);
                endDate = sdf.parse(sEnd);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (startDate != null && endDate != null) {
                try {
                    while (IUtil.WhileB) {
                        ThisLog("上传数据");

                        //if (queryMode == 1) {//按照拍摄时间
                        myList = Select_DB.getInstance().getPiturePath(startDate, endDate, (page++) * CHECK_PHOTOS_ONCE, CHECK_PHOTOS_ONCE);
                        if (myList != null && myList.size() > 0) {
                            int dataPackages;
                            int lastPackages;
                            dataPackages = myList.size() / 40;
                            if (myList.size() % 40 != 0) {
                                dataPackages++;
                                lastPackages = myList.size() % 40;
                            } else {
                                lastPackages = 40;
                            }
                            int index;
                            for (int i = 0; i < dataPackages; i++) {
                                wait_for_up_query_photo = 4; //初始化上传标识
                                index = 0;
                                if (i != dataPackages - 1) {
                                    photoHeadData[index++] = 0;
                                } else {
                                    photoHeadData[index++] = 1;
                                }
                                photoHeadData[index++] = (byte) (myList.size() & 0xFF);
                                photoHeadData[index++] = (byte) ((i != dataPackages - 1) ? 40 : lastPackages);
                                for (int k = 0; k < ((i != dataPackages - 1) ? 40 : lastPackages); k++) {
                                    curHeadInformation = myList.get(i * 40 + k);
                                    photoID = curHeadInformation.getTakeID();
                                    System.arraycopy(photoID, 0, photoHeadData, index, 10);
                                    index += 10;
                                }
                                byte[] sendBytes = new byte[index];
                                System.arraycopy(photoHeadData, 0, sendBytes, 0, sendBytes.length);
                                tempBytes = getCmdData(GPRS_ID_CMD0900, GD_UP_QUERY_PHOTO, (byte) 0x01, sendBytes, 0);
                                sendTcpBytes(tempBytes);
                                waitCount = 0;
                                do {
                                    Thread.sleep(100);
                                    if (wait_for_up_query_photo != 4) {
                                        break;
                                    }
                                } while (waitCount++ < 100);
                                if (wait_for_up_query_photo != 0) {//应答错误或者无应答，退出图片头信息上报
                                    break;
                                }
                            }
                        } else {
                            if (page == 1) {//没有查询到有效照片
                                byte[] sendBytes = new byte[2];
                                sendBytes[0] = 0x01;
                                sendBytes[1] = 0x00;
                                tempBytes = getCmdData(GPRS_ID_CMD0900, GD_UP_QUERY_PHOTO, (byte) 0x01, sendBytes, 0);
                                sendTcpBytes(tempBytes);
                                waitCount = 0;
                                do {
                                    Thread.sleep(100);
                                    if (wait_for_up_query_photo != 4) {
                                        break;
                                    }
                                } while (waitCount++ < 100);
                                if (wait_for_up_query_photo != 0) {//应答错误或者无应答，退出图片头信息上报
                                    break;
                                }
                            }
                            break;
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                result[0] = 0x02;//执行失败
                tempBytes = getCmdData(GPRS_ID_CMD0900, GD_CMD_QUERY_PHOTO, (byte) 0x00, result, 0);
                sendTcpBytes(tempBytes);
            }
        }
    }


    // 处理0302图片头命令上传
    class HandleUpQueryInfo implements Runnable {
        int cmd;
        int queryMode;
        byte[] startTime;
        byte[] endTime;
        int listNumber;
        Date startDate = null;
        Date endDate = null;

        public HandleUpQueryInfo(int cmd, int queryMode, byte[] startTime, byte[] endTime, int listNumber) {
            this.cmd = cmd;
            this.queryMode = queryMode;
            this.startTime = startTime;
            this.endTime = endTime;
            this.listNumber = listNumber;
        }

        @Override
        public void run() {
            int waitCount;
            byte[] tempBytes;
            byte[] result;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            int page = 0;
            result = new byte[1];
            try {
                String sStart = String.format("20%02X-%02X-%02X %02X:%02X:%02X", startTime[0] & 0xFF, startTime[1] & 0xFF, startTime[2] & 0xFF, startTime[3] & 0xFF, startTime[4] & 0xFF, startTime[5] & 0xFF);
                String sEnd = String.format("20%02X-%02X-%02X %02X:%02X:%02X", endTime[0] & 0xFF, endTime[1] & 0xFF, endTime[2] & 0xFF, endTime[3] & 0xFF, endTime[4] & 0xFF, endTime[5] & 0xFF);
                startDate = sdf.parse(sStart);
                endDate = sdf.parse(sEnd);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (startDate != null && endDate != null) {
                try {
                    result[0] = 0x01;//开始查询
                    tempBytes = getCmdData(GPRS_ID_CMD0900, GD_CMD_TRAIN_LIST, (byte) 0x00, result, 0);
                    sendTcpBytes(tempBytes);
                    if (cmd == 0x8205) {
                        while (IUtil.WhileB) {
                            ArrayList<TrainingRecord> myList;
                            if (queryMode == 1) {//按照拍摄时间getTrainListT(Date startTime, Date endTime, int startIndex, int number)
                                myList = Select_DB.getInstance().getTrainListT(startDate, endDate, (page++) * CHECK_TRAINLIST_ONCE, CHECK_TRAINLIST_ONCE);
                            } else {//按学员登录流水号
                                myList = Select_DB.getInstance().getTrainListT(startDate, endDate, (page++) * CHECK_TRAINLIST_ONCE, CHECK_TRAINLIST_ONCE);//预留
                            }
                            if (myList != null && myList.size() > 0) {
                                for (int i = 0; i < myList.size(); i++) {
                                    wait_for_up_query_trainlist = 4;
                                    //tempBytes = getCmdData(0x0900, GD_UP_TRAIN_LIST, (byte) 1, StringUtils.HexStringToBytes(myList.get(i).getTraining_data()));
                                    sendTcpBytes(tempBytes);
                                    waitCount = 0;
                                    do {
                                        Thread.sleep(100);
                                        if (wait_for_up_query_trainlist != 4) {
                                            break;
                                        }
                                    } while (waitCount++ < 100);
                                    if (wait_for_up_query_trainlist != 0) {//应答错误或者无应答，退出图片头信息上报
                                        break;
                                    }
                                }
                            } else {
                                break;
                            }
                        }
                    } else if (cmd == GD_CMD_DRIVING_BEHAVIOR) {
                        ArrayList<DrivingRecordInfo> myList;
                        while (IUtil.WhileB) {
                            if (queryMode == 1) {//按照拍摄时间getTrainListT(Date startTime, Date endTime, int startIndex, int number)
                                myList = Select_DB.getInstance().getDBDrivingData(startDate, endDate, (page++) * CHECK_TRAINLIST_ONCE, CHECK_TRAINLIST_ONCE);
                            } else {//按学员登录流水号
                                myList = Select_DB.getInstance().getDBDrivingData(startDate, endDate, (page++) * CHECK_TRAINLIST_ONCE, CHECK_TRAINLIST_ONCE);//预留
                            }
                            if (myList != null && myList.size() > 0) {
                                for (int i = 0; i < myList.size(); i++) {
                                    wait_for_up_query_trainlist = 4;
                                    tempBytes = getCmdData(0x0900, GD_UP_DRIVING_BEHAVIOR, (byte) 1, StringUtils.HexStringToBytes(myList.get(i).getDriving_data()), 0);
                                    sendTcpBytes(tempBytes);
                                    waitCount = 0;
                                    do {
                                        Thread.sleep(100);
                                        if (wait_for_up_query_trainlist != 4) {
                                            break;
                                        }
                                    } while (waitCount++ < 100);
                                    if (wait_for_up_query_trainlist != 0) {//应答错误或者无应答，退出图片头信息上报
                                        break;
                                    }
                                }
                            } else {
                                break;
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                result[0] = 0x02;//执行失败
                tempBytes = getCmdData(GPRS_ID_CMD0900, cmd, (byte) 0x00, result, 0);
                sendTcpBytes(tempBytes);
            }
        }
    }

    private int workSerial;
    private int msgPropertyEx;

    public byte[] getCmdData0900(int gdCmd, byte ackFlag, byte[] dataContext, int isBlindArea) {
        byte[] currentTime = new byte[6];
        byte[] pcData = new byte[1024];
        byte[] tempBytes;
        int index = 0;
        int startIndex;

        try {
            pcData[index++] = 0x13;//透传消息类型	BYTE	0x13为驾培业务
            pcData[index++] = (byte) (gdCmd >> 8);// 功能编号
            pcData[index++] = (byte) gdCmd; //消息ID
//        bit0表示消息时效类型，应答中也应附带此内容，0：实时消息，1：补传消息；
//        bit1表示应答属性，0：不需要应答，1：需要应答；
//        bit4-7表示加密算法，0：未加密，1：SHA1，2：SHA256；
//        其他保留
            msgPropertyEx = 0;
            if (isBlindArea == 1) {
                msgPropertyEx |= 0x0001;//设置为 补传消息
            }
            if (ackFlag == 1) {
                msgPropertyEx |= 0x0002;
            }
//            if (gdCmd != GD_UP_PHOTO_PACKAGE) {
            msgPropertyEx |= 0x0020;//SHA256；
//            }
            pcData[index++] = (byte) (msgPropertyEx >> 8);// 作业序号
            pcData[index++] = (byte) msgPropertyEx;
            workSerial++;
            if ((workSerial & 0xFFFF) == 0) {
                workSerial++;
            }
            pcData[index++] = (byte) (workSerial >> 8);// 作业序号
            pcData[index++] = (byte) workSerial;
            //计时终端编号
            System.arraycopy(CommonInfo.getTelnetDeviceNumber().getBytes("GBK"), 0, pcData, index, CommonInfo.getTelnetDeviceNumber().length());
            index += CommonInfo.getTelnetDeviceNumber().length();
            pcData[index++] = 0;
            pcData[index++] = 0;
            pcData[index++] = 0;
            pcData[index++] = 0;//数据长度
            startIndex = index;
            switch (gdCmd) {
                case GD_UP_COACH_LOGIN:
                    System.arraycopy(dataContext, 0, pcData, index, dataContext.length);//
                    index += dataContext.length;
//                tempBytes = CommonInfo.getCoachLoginPackage();
//                System.arraycopy(tempBytes, 0, pcData, index, tempBytes.length);
//                index += tempBytes.length;
                    break;
                case GD_UP_COACH_LOGOUT:
                    System.arraycopy(dataContext, 0, pcData, index, dataContext.length);//
                    index += dataContext.length;
//                tempBytes = CommonInfo.getCoachLogoutPackage();
//                System.arraycopy(tempBytes, 0, pcData, index, tempBytes.length);
//                index += tempBytes.length;
                    break;
                case GD_UP_STUDENT_LOGIN:
                    System.arraycopy(dataContext, 0, pcData, index, dataContext.length);//
                    index += dataContext.length;
//
//                tempBytes = CommonInfo.getStuLoginPackage();
//                System.arraycopy(tempBytes, 0, pcData, index, tempBytes.length);
//                index += tempBytes.length;
                    break;
                case GD_UP_STUDENT_LOGOUT:
                    System.arraycopy(dataContext, 0, pcData, index, dataContext.length);//
                    index += dataContext.length;
//                tempBytes = CommonInfo.getStuLogoutPackage();
//                System.arraycopy(tempBytes, 0, pcData, index, tempBytes.length);
//                index += tempBytes.length;
                    break;
                case GD_UP_TRAIN_LIST:
                    System.arraycopy(dataContext, 0, pcData, index, dataContext.length);//
                    index += dataContext.length;
                    break;
                case GD_UP_DRIVING_BEHAVIOR:
                    System.arraycopy(dataContext, 0, pcData, index, dataContext.length);//
                    index += dataContext.length;
                    break;
                case GD_UP_QUERY_PHOTO:
                    //上报结束标志 ?	MSG0x0303消息的作业序号应和MSG0x0302的一致。
                    pcData[index++] = dataContext[0];//是否上报结束 0、否  1、是
                    pcData[index++] = dataContext[1];//符合条件的照片总数
                    pcData[index++] = dataContext[2];//此次发送的照片数目
                    if (dataContext[2] > 0) {
                        System.arraycopy(dataContext, 3, pcData, index, dataContext[2] * 10);
                        index += dataContext[2] * 10;
                    }
                    break;
                case GD_UP_PHOTO_REQUEST:
                    System.arraycopy(dataContext, 0, pcData, index, dataContext.length);//6字节 类型+版本+状态
                    index += dataContext.length;
//                pcData[index++] = dataContext[0];//上传模式 1自动请求上传 FF停止拍摄并上传 81终端主动拍照上传
//                pcData[index++] = dataContext[1];//拍摄通道
//                pcData[index++] = dataContext[2];//图像尺寸
//                pcData[index++] = dataContext[3];//事件类型
//                pcData[index++] = dataContext[4];//
//                pcData[index++] = dataContext[5];//总包数
//                pcData[index++] = dataContext[6];//事件类型
//                pcData[index++] = dataContext[7];//事件类型
//                pcData[index++] = dataContext[8];//事件类型
//                pcData[index++] = dataContext[9];//照片数据大小
//                System.arraycopy(dataContext, 10, pcData, index, 8);//学员驾校编号
//                index += 8;
//                System.arraycopy(dataContext, 18, pcData, index, 8);//学员编号
//                index += 8;
//                System.arraycopy(dataContext, 26, pcData, index, 8);//学员登录编号或预约编号
//                index += 8;
//                System.arraycopy(dataContext, 34, pcData, index, 10);//拍摄编号
//                index += 10;
//                System.arraycopy(dataContext, 44, pcData, index, 38);//完整的GPS数据包
//                index += 38;
                    break;
                case GD_UP_PHOTO_PACKAGE:
                    System.arraycopy(dataContext, 0, pcData, index, dataContext.length);//拍照编号
                    index += dataContext.length;
//                System.arraycopy(dataContext, 0, pcData, index, 10);//拍照编号
//                index += 10;
//                pcData[index++] = dataContext[10];//标记位 BIT7: 最高位表示是否需要中心应答。BIT6: 是否最后一包
//                pcData[index++] = dataContext[11];//
//                pcData[index++] = dataContext[12];//包序号
//                pcData[index++] = dataContext[13];//
//                pcData[index++] = dataContext[14];//本包数据长度
//                //数据内容
//                System.arraycopy(dataContext, 15, pcData, index, (dataContext[13]*0xFF)*256+(dataContext[14]&0xFF));//拍照编号
//                index +=  (dataContext[13]*0xFF)*256+(dataContext[14]&0xFF);
                    break;
                case GD_REQUIR_VERIFY_PHOTO:
                    System.arraycopy(dataContext, 0, pcData, index, dataContext.length);//拍摄编号+包序号
                    index += dataContext.length;
                    break;
                case GD_UP_QUERY_VIDEO:

                    break;
                case GD_UP_VIDEO_PACKAGE:
                    System.arraycopy(dataContext, 0, pcData, index, dataContext.length);//拍照编号
                    index += dataContext.length;
                    break;
                case GD_UP_REQUIR_VERIFY_VIDEO:

                    break;
                case GD_UP_UPGRADE_ACK:
                    System.arraycopy(dataContext, 0, pcData, index, dataContext.length);//6字节 类型+版本+状态
                    index += dataContext.length;
                    break;
                case GD_UP_REQUEST_AREA:
                    tempBytes = CommonInfo.getGpsPackage();
                    System.arraycopy(tempBytes, 0, pcData, index, tempBytes.length);
                    index += tempBytes.length;
                    break;
                case GD_UP_GET_AREA:
                    System.arraycopy(dataContext, 0, pcData, index, dataContext.length);//6字节 训练场地编号
                    index += dataContext.length;
                    break;
                case GD_UP_UPDATE_ORDER:     //请求更新订单信息
                    tempBytes = CommonInfo.getGpsPackage();
                    System.arraycopy(tempBytes, 0, pcData, index, tempBytes.length);
                    index += tempBytes.length;
                    break;
                case GD_UP_DOWN_ORDER_INFO:
                    System.arraycopy(dataContext, 0, pcData, index, dataContext.length);//8字节订单编号
                    index += dataContext.length;
                    break;

                /**************************************应答透传命令************************************************************************/
                case GD_CMD_TRAIN_LIST:
                    if (dataContext != null) {
                        if (dataContext.length > 0) {
                            pcData[index++] = dataContext[0];
                        }
                    }
                    break;
                case GD_CMD_DRIVING_BEHAVIOR:
                    if (dataContext != null) {
                        if (dataContext.length > 0) {
                            pcData[index++] = dataContext[0];
                        }
                    }
                    break;
                case GD_UP_ORDER_STATE:
                    if (dataContext != null) {
                        if (dataContext.length > 0) {
                            System.arraycopy(dataContext, 0, pcData, index, dataContext.length);
                            index += dataContext.length;
                        }
                    }
                    break;
                case GD_CMD_TAKE_PHOTO:
                    if (dataContext != null) {
                        if (dataContext.length > 0) {
                            System.arraycopy(dataContext, 0, pcData, index, dataContext.length);
                            index += dataContext.length;
                        }
                    }
                    break;
                case GD_CMD_UP_PHOTO:
                    pcData[index++] = dataContext[0];
                    break;

                case GD_CMD_TAKE_VIDEO:
                    break;
                case GD_CMD_QUERY_VIDEO:

                    break;
                case GD_CMD_UP_VIDEO:

                    break;
                case GD_CMD_INIT_VIDEO:
                    System.arraycopy(dataContext, 0, pcData, index, dataContext.length);//6字节 类型+版本+状态
                    index += dataContext.length;
                    break;
                case GD_CMD_QUERY_SOFT_VERSION:
                    if (dataContext != null) {
                        if (dataContext.length > 0) {
                            System.arraycopy(dataContext, 0, pcData, index, dataContext.length);
                            index += dataContext.length;
                        }
                    }
                    break;
                case GD_CMD_UPGRADE_SOFT:
                    if (dataContext != null) {
                        if (dataContext.length > 0) {
                            System.arraycopy(dataContext, 0, pcData, index, dataContext.length);
                            index += dataContext.length;
                        }
                    }
                    break;
                case GD_CMD_QUERY_PHOTO:
                    if (dataContext != null) {
                        if (dataContext.length > 0) {
                            pcData[index++] = dataContext[0];
                        }
                    }
                    break;
                case GD_CMD_SET_PARAMETER:
                    if (dataContext != null) {
                        if (dataContext.length > 0) {
                            pcData[index++] = dataContext[0];
                        }
                    }
                    break;
                case GD_CMD_SET_FORBID:
                    if (dataContext != null) {
                        if (dataContext.length > 0) {
                            System.arraycopy(dataContext, 0, pcData, index, dataContext.length);
                            index += dataContext.length;
                        }
                    }
                    break;
                case GD_CMD_QUERY_PARAMETER:
                    if (dataContext != null) {
                        if (dataContext.length > 0) {
                            System.arraycopy(dataContext, 0, pcData, index, dataContext.length);
                            index += dataContext.length;
                        }
                    }
                    break;
                case GD_CMD_SET_DRIVINGNO:
                    if (dataContext != null) {
                        if (dataContext.length > 0) {
                            System.arraycopy(dataContext, 0, pcData, index, dataContext.length);
                            index += dataContext.length;
                        }
                    }
                    break;
                case GD_CMD_FORBID_COUNT:

                    break;
                case GD_CMD_SET_TIME:

                    break;
                default:
                    //数据长度
                    System.arraycopy(dataContext, 0, pcData, index, dataContext.length);//数据内容
                    index += dataContext.length;
                    break;
            }
            if (gdCmd == GD_UP_PHOTO_PACKAGE) {
                pcData[startIndex - 4] = (byte) ((curPhotoHead.fileSize + 10) >> 24);//消息长度赋值
                pcData[startIndex - 3] = (byte) ((curPhotoHead.fileSize + 10) >> 16);
                pcData[startIndex - 2] = (byte) ((curPhotoHead.fileSize + 10) >> 8);
                pcData[startIndex - 1] = (byte) (curPhotoHead.fileSize + 10);

//                byte[] clearText = new byte[index];
//                System.arraycopy(pcData, 0, clearText, 0, index);
                byte[] retBytes = new byte[index];
                System.arraycopy(pcData, 0, retBytes, 0, retBytes.length);
                if (ackFlag != 0) {
                    if (DEBUG) Log.i(TAG, "开始等待应答数据");
                    transState.setState(0x8900, gdCmd, transState.WAIT_ACK);
                }
                Log.i(TAG, "是否更新");
                return retBytes;
            } else {
                pcData[startIndex - 2] = (byte) ((index - startIndex) >> 8);//消息长度赋值
                pcData[startIndex - 1] = (byte) (index - startIndex);
                //加密内容
//                byte[] clearText = new byte[index];
//                System.arraycopy(pcData, 0, clearText, 0, index);
                byte[] clearText = new byte[index - 1];
                System.arraycopy(pcData, 1, clearText, 0, index - 1);
                byte[] cdata = getRsaEncryptData(clearText);
                if (cdata != null) {
                    System.arraycopy(cdata, 0, pcData, index, cdata.length);
                    index += cdata.length;
                    byte[] retBytes = new byte[index];
                    System.arraycopy(pcData, 0, retBytes, 0, retBytes.length);

                    if (ackFlag != 0) {
                        if (DEBUG) Log.i(TAG, "开始等待应答数据");
                        transState.setState(0x8900, gdCmd, transState.WAIT_ACK);
                    }
                    Log.i(TAG, "是否更新");
                    return retBytes;
                } else {
                    return null;
                }
            }
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        return null;
    }


    public byte[] getCmdData(int cmd, int subCmd, byte ackFlag, byte[] dataContent, int isBlindArea) {
        return getCmdData(cmd, subCmd, ackFlag, dataContent, 1, 1, isBlindArea);
    }


    public byte[] getCmdData(int cmd, int subCmd, byte ackFlag, byte[] dataContent, int packNumber, int totalNumber, int isBlindArea) {
        /*if(cmd == 0x0900 && isBlindArea == 1){
            //为无网路时保存的 0900 数据
            isRepairData = true;
        }*/
        byte[] buf = new byte[2048];
        byte[] tempBuf;
        int packageProperty = 0;

        int index = 0;
        buf[index++] = (byte) 0x80;
        if (cmd == GPRS_ID_CMD8104 || cmd == GPRS_ID_CMD8106) {
            buf[index++] = (byte) ((GPRS_ID_CMD0104 >> 8) & 0xFF);      //消息ID(word)
            buf[index++] = (byte) (GPRS_ID_CMD0104 & 0xFF);
        } else {
            buf[index++] = (byte) ((cmd >> 8) & 0xFF);      //消息ID(word)
            buf[index++] = (byte) (cmd & 0xFF);
        }
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
        } else {
            buf[index++] = (byte) ((recvSequence >> 8) & 0xFF);     //消息流水号(word)
            buf[index++] = (byte) (recvSequence & 0xFF);
        }
        buf[index++] = 0x3C;
        if (totalNumber > 1) {
            packageProperty |= 0x2000;
        } else {
            packageProperty = 0;
        }
        if (cmd == 0x0900 || cmd == 0x0102) {
            packageProperty |= 0x01 << 10;
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
                case GPRS_ID_CMD0100://注册
                    buf[index++] = (byte) (DeviceParameter.getProviceID() >> 8);//省域ID
                    buf[index++] = (byte) DeviceParameter.getProviceID();
                    buf[index++] = (byte) (DeviceParameter.getCityID() >> 8);//市县域ID
                    buf[index++] = (byte) DeviceParameter.getCityID();
                    System.arraycopy(DeviceParameter.getManufacturerID().getBytes(), 0, buf, index, 5);//制造商ID 5zijei
                    index += 5;
                    System.arraycopy(DeviceParameter.getDeviceType().getBytes(), 0, buf, index, DeviceParameter.getDeviceType().getBytes().length);
                    index += DeviceParameter.getDeviceType().getBytes().length;
                    for (int i = 0; i < 20 - DeviceParameter.getDeviceType().getBytes().length; i++) {
                        buf[index++] = 0x00;
                    }
                    // TODO: 2020-01-07 出厂序列号暂时也是假的
//                    System.arraycopy(DeviceParameter.getDeviceSerial(), 0, buf, index, 7);//计时终端出厂序列号 7zijie
                    System.arraycopy("C123456".getBytes(), 0, buf, index, 7);//计时终端出厂序列号 7zijie
                    index += 7;
                    // TODO: 2020-01-07 这里的imei号暂时是假的
//                    System.arraycopy(DeviceParameter.getDeviceIMEI().getBytes(), 0, buf, index, 15);
                    System.arraycopy("868704043712345".getBytes(), 0, buf, index, 15);
                    index += 15;
                    buf[index++] = DeviceParameter.getCarColor();//车牌颜色
                    for (byte b : plateNumber) { //车牌号
                        buf[index++] = b;
                    }
                    break;
                case GPRS_ID_CMD0002://心跳
                    break;
                case GPRS_ID_CMD0003://注销命令
                    break;
                case GPRS_ID_CMD0102://登陆命令
                    if (packNumber == 1) {
                        buf[index++] = (byte) (tempSeconds >> 24);
                        buf[index++] = (byte) (tempSeconds >> 16);
                        buf[index++] = (byte) (tempSeconds >> 8);
                        buf[index++] = (byte) (tempSeconds);
                    }
                    System.arraycopy(dataContent, 0, buf, index, dataContent.length);
                    index += dataContent.length;

                    break;
                case GPRS_ID_CMD0200:
                case GPRS_ID_CMD0201:
                    //位置基本信息
                    //tempBuf = CommonInfo.getGpsData();
                    tempBuf = CommonInfo.getGpsPackage();
                    System.arraycopy(tempBuf, 0, buf, index, tempBuf.length);
                    index += tempBuf.length;
                    break;
                case GPRS_ID_CMD0FE6:
                    byte[] gpspackage = CommonInfo.getGpsPackage();//
                    int startIndex = index;
                    for (byte bt : gpspackage) {
                        buf[index++] = bt;
                    }
                    byte[] simCardManufacturer = "01234567890123456789".getBytes();
                    byte[] plateTypeInfo = "MadeInChina".getBytes();
                    byte[] hardwareVersion = new byte[4];
                    hardwareVersion[0] = 0x16;
                    hardwareVersion[1] = 0x06;
                    hardwareVersion[2] = 0x16;
                    hardwareVersion[3] = 0x01;
                    System.arraycopy(simCardManufacturer, 0, buf, index, simCardManufacturer.length);
                    index += simCardManufacturer.length;
                    for (int i = 0; i < 12 - plateNumber.length; i++)//车牌不足12位补0x00
                    {
                        buf[index++] = 0x00;
                    }
                    for (byte bt : plateNumber) {
                        buf[index++] = bt;
                    }
                    //车跑分类
                    for (int i = 0; i < 12 - plateTypeInfo.length; i++)//不足12位补0x00
                    {
                        buf[index++] = 0x00;
                    }
                    for (byte bt : plateTypeInfo) {
                        buf[index++] = bt;
                    }
                    for (byte bt : hardwareVersion) {
                        buf[index++] = bt;
                    }
                    if (index - startIndex != 86) {
                        if (DEBUG) Log.i(TAG, "终端发行信息长度获取有误");
                    }
                    byte[] pData = new byte[index - startIndex];
                    System.arraycopy(buf, startIndex, pData, 0, pData.length);
                    int crcVal = Crc16.getCrc(pData);
                    buf[index++] = (byte) (crcVal >> 8);
                    buf[index++] = (byte) crcVal;
                    break;
                case GPRS_ID_CMD8104://获取设备所有参数
                    tempBuf = get8104Data1(recvSequence, totalNumber, packNumber);
                    //tempBuf = get8104Data(recvSequence, totalNumber, packNumber);
                    System.arraycopy(tempBuf, 0, buf, index, tempBuf.length);
                    index += tempBuf.length;
                    break;
                case GPRS_ID_CMD8106://获取设备所有参数
                    tempBuf = get8106Data1(recvSequence, totalNumber, packNumber);
                    //tempBuf = get8104Data(recvSequence, totalNumber, packNumber);
                    System.arraycopy(tempBuf, 0, buf, index, tempBuf.length);
                    index += tempBuf.length;
                    break;
//                case GPRS_ID_CMD0900:
//                    tempBuf = getCmdData0900(subCmd, ackFlag, dataContent);
//                    System.arraycopy(tempBuf, 0, buf, index, tempBuf.length);
//                    index += tempBuf.length;
//                    break;
                case GPRS_ID_CMD0900://20170401 start
                    if (packNumber != 1) {//if (packNumber < totalNumber && packNumber != 1) {
//                        tempBuf = new byte[dataContent.length];
//                        System.arraycopy(dataContent, 0, tempBuf, 0, dataContent.length); //追加照片数据包
//                        byte[] cData = getRsaEncryptData(dataContent); // 获取本照片数据包的加密数据
//                        tempBuf = new byte[cData.length + dataContent.length];//组包长度
//                        System.arraycopy(dataContent, 0, tempBuf, 0, dataContent.length); //追加照片数据包
//                        System.arraycopy(cData, 0, tempBuf, dataContent.length, cData.length); //追加本照片数据包加密字符串
                    } else {
                        tempBuf = getCmdData0900(subCmd, ackFlag, dataContent, isBlindArea);
                        System.arraycopy(tempBuf, 0, buf, index, tempBuf.length);
                        index += tempBuf.length;//20170401 end
                        if (subCmd == GD_UP_PHOTO_PACKAGE) {
                            imageBuffIndex = 0;
                            imageBuff = new byte[(int) (curPhotoHead.fileSize + 36)];//照片文件+编号
                            System.arraycopy(tempBuf, 1, imageBuff, imageBuffIndex, tempBuf.length - 1);
                            imageBuffIndex += tempBuf.length - 1;
                        }
                    }
                    break;

                case GPRS_ID_CMD0001:
                    System.arraycopy(dataContent, 0, buf, index, dataContent.length);
                    index += dataContent.length;
                    break;
            }
        }
        //有分包
        if ((packageProperty & 0x2000) > 0) {
            packageProperty |= (index - 20);
        }
        //无分包
        else {
            packageProperty |= (index - 16);
        }
        buf[3] = (byte) ((packageProperty >> 8) & 0xFF);       //分包(B13)，数据加密方式(B10-B12)，消息长度(B0-B9)
        buf[4] = (byte) (packageProperty & 0xFF);//长度
        byte[] sendProtocolbuf = new byte[index];
        System.arraycopy(buf, 0, sendProtocolbuf, 0, index);
        byte[] retData = formatToProtocolData(sendProtocolbuf);
        return retData;
    }

    public byte[] getCmdData(int cmd, int subCmd, int isBlinArea) {
        return getCmdData(cmd, subCmd, (byte) 1, null, 0);
    }

    public byte[] getCmdData(int cmd, int isBlindArea) {
        return getCmdData(cmd, 0, (byte) 1, null, isBlindArea);
    }

    byte[] getTrainRecordData(TrainingRecord tr, int mode) {
        try {
            byte[] buf = new byte[122];
            int index = 0;

            System.arraycopy(tr.getTraining_id().getBytes("GBK"), 0, buf, index, 26);//学时记录编号
            index += 26;
            buf[index++] = (byte) mode;//上传模式1，自动上传，2，中心命令上传
            System.arraycopy(tr.getTraining_learner_number().getBytes("GBK"), 0, buf, index, 16);//学员编号
            index += 16;
            System.arraycopy(tr.getTraining_instructor_number().getBytes("GBK"), 0, buf, index, 16);//教练员编号
            index += 16;
            int classId = tr.getTrainClassId();//课堂ID
            buf[index++] = (byte) (classId >> 24);
            buf[index++] = (byte) (classId >> 16);
            buf[index++] = (byte) (classId >> 8);
            buf[index++] = (byte) (classId);
            System.arraycopy(StringUtils.HexStringToBytes(tr.getTraining_end_time()), 0, buf, index, 3);//培训时间
            index += 3;
            System.arraycopy(StringUtils.HexStringToBytes(tr.getTrainCourseName()), 0, buf, index, 5);//培训课程
            index += 5;
            buf[index++] = (byte) tr.getState();//记录状态
            int maxSpeed = tr.getMaxSpeed();//最大速度
            buf[index++] = (byte) (maxSpeed >> 8);
            buf[index++] = (byte) (maxSpeed);
            int miles = tr.getMiles();//里程
            buf[index++] = (byte) (miles >> 8);
            buf[index++] = (byte) (miles);
            System.arraycopy(tr.getGpsDate(), 0, buf, index, tr.getGpsDate().length);//GNSS包
            StringBuffer buf1 = new StringBuffer();
            for (int i = 28; i < tr.getGpsDate().length; i++) {
                buf1.append(tr.getGpsDate()[i]);
            }
            Log.e(TAG, "TcpClient: package is " + buf1);
            index += tr.getGpsDate().length;
            return buf;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }


    private final int GPRS_ID_CMD0001 = 0x0001;               //终端通用应答
    private final int GPRS_ID_CMD8001 = 0x8001;               //平台通用应答
    private final int GPRS_ID_CMD0002 = 0x0002;               //终端心跳
    private final int GPRS_ID_CMD0100 = 0x0100;               //终端注册
    private final int GPRS_ID_CMD8100 = 0x8100;               //终端注册应答
    private final int GPRS_ID_CMD0003 = 0x0003;               //终端注销
    private final int GPRS_ID_CMD0102 = 0x0102;               //终端鉴权
    private final int GPRS_ID_CMD8103 = 0x8103;               //设置终端参数
    private final int GPRS_ID_CMD8104 = 0x8104;               //查询终端参数
    private final int GPRS_ID_CMD8106 = 0x8106;               //指定终端参数
    private final int GPRS_ID_CMD0104 = 0x0104;               //查询终端参数应答
    private final int GPRS_ID_CMD8105 = 0x8105;               //终端控制
    private final int GPRS_ID_CMD0200 = 0x0200;               //位置信息汇报
    private final int GPRS_ID_CMD8201 = 0x8201;               //位置信息查询
    private final int GPRS_ID_CMD0201 = 0x0201;               //位置信息查询应答
    private final int GPRS_ID_CMD8202 = 0x8202;               //临时位置跟踪控制
    private final int GPRS_ID_CMD8300 = 0x8300;               //文本信息下发
    private final int GPRS_ID_CMD8301 = 0x8301;               //事件设置
    private final int GPRS_ID_CMD0301 = 0x0301;               //事件报告
    private final int GPRS_ID_CMD8302 = 0x8302;               //提问下发
    private final int GPRS_ID_CMD0302 = 0x0302;               //提问应答
    private final int GPRS_ID_CMD8303 = 0x8303;               //信息点播菜单设置
    private final int GPRS_ID_CMD0303 = 0x0303;               //信息点播/取消
    private final int GPRS_ID_CMD8304 = 0x8304;               //信息服务
    private final int GPRS_ID_CMD8400 = 0x8400;               //电话回拨
    private final int GPRS_ID_CMD8401 = 0x8401;               //设置电话本
    private final int GPRS_ID_CMD8500 = 0x8500;               //车辆控制
    private final int GPRS_ID_CMD0500 = 0x0500;               //车辆控制应答
    private final int GPRS_ID_CMD8600 = 0x8600;               //设置圆形区域
    private final int GPRS_ID_CMD8601 = 0x8601;               //删除圆形区域
    private final int GPRS_ID_CMD8602 = 0x8602;               //设置矩形区域
    private final int GPRS_ID_CMD8603 = 0x8603;               //删除矩形区域
    private final int GPRS_ID_CMD8604 = 0x8604;               //设置多边形区域
    private final int GPRS_ID_CMD8605 = 0x8605;               //删除多边形区域
    private final int GPRS_ID_CMD8606 = 0x8606;               //设置路线
    private final int GPRS_ID_CMD8607 = 0x8607;               //删除路线
    private final int GPRS_ID_CMD8700 = 0x8700;               //行驶记录仪数据采集命令
    private final int GPRS_ID_CMD0700 = 0x0700;               //行驶记录仪数据上传
    private final int GPRS_ID_CMD8701 = 0x8701;               //行驶记录仪参数下传命令
    private final int GPRS_ID_CMD0701 = 0x0701;               //电子运单上报
    private final int GPRS_ID_CMD0702 = 0x0702;               //驾驶员身份信息采集上报
    private final int GPRS_ID_CMD0800 = 0x0800;               //多媒体事件信息上传
    private final int GPRS_ID_CMD0801 = 0x0801;               //多媒体数据上传
    private final int GPRS_ID_CMD8800 = 0x8800;               //多媒体数据上传应答
    private final int GPRS_ID_CMD8801 = 0x8801;               //摄像头立即拍摄命令
    private final int GPRS_ID_CMD8802 = 0x8802;               //存储多媒体数据检索
    private final int GPRS_ID_CMD0802 = 0x0802;               //存储多媒体数据检索应答
    private final int GPRS_ID_CMD8803 = 0x8803;               //存储多媒体数据上传
    private final int GPRS_ID_CMD8804 = 0x8804;               //录音开始命令
    private final int GPRS_ID_CMD8900 = 0x8900;               //数据下行透传
    private final int GPRS_ID_CMD0900 = 0x0900;               //数据上行透传
    private final int GPRS_ID_CMD0901 = 0x0901;               //数据压缩上报
    private final int GPRS_ID_CMD8A00 = 0x8A00;               //平台RSA公钥
    private final int GPRS_ID_CMD0A00 = 0x0A00;               //终端RSA公钥

    private final int GPRS_ID_CMD0FE6 = 0x0FE6;               //终端发行信息上报
    private final int GPRS_ID_CMD8FE6 = 0x8FE6;               //终端发行信息查询

    //    广东驾培自定义通讯协议
    private static final int GD_UP_COACH_LOGIN = 0x0101;
    private static final int GD_UP_COACH_LOGOUT = 0x0102;
    private static final int GD_UP_STUDENT_LOGIN = 0x0201;
    private static final int GD_UP_STUDENT_LOGOUT = 0x0202;
    private static final int GD_UP_TRAIN_LIST = 0x0203;
    private static final int GD_UP_DRIVING_BEHAVIOR = 0x0204;
    private static final int GD_CMD_TRAIN_LIST = 0x0205;
    private static final int GD_CMD_DRIVING_BEHAVIOR = 0x0206;
    private static final int GD_UP_ORDER_STATE = 0x0207;//上传订单状态变化
    private static final int GD_CMD_TAKE_PHOTO = 0x0301;
    private static final int GD_CMD_QUERY_PHOTO = 0x0302;
    private static final int GD_UP_QUERY_PHOTO = 0x0303;
    private static final int GD_CMD_UP_PHOTO = 0x0304;
    private static final int GD_UP_PHOTO_REQUEST = 0x0305;
    private static final int GD_UP_PHOTO_PACKAGE = 0x0306;
    private static final int GD_REQUIR_VERIFY_PHOTO = 0x0307;
    private static final int GD_CMD_TAKE_VIDEO = 0x0311;
    private static final int GD_CMD_QUERY_VIDEO = 0x0312;
    private static final int GD_UP_QUERY_VIDEO = 0x0313;
    private static final int GD_CMD_UP_VIDEO = 0x0314;
    private static final int GD_CMD_INIT_VIDEO = 0x0315;
    private static final int GD_UP_VIDEO_PACKAGE = 0x0316;
    private static final int GD_UP_REQUIR_VERIFY_VIDEO = 0x0317;
    private static final int GD_CMD_QUERY_SOFT_VERSION = 0x0401;
    private static final int GD_CMD_UPGRADE_SOFT = 0x0402;
    private static final int GD_UP_UPGRADE_ACK = 0x0403;
    private static final int GD_CMD_SET_PARAMETER = 0x0501;
    private static final int GD_CMD_SET_FORBID = 0x0502;
    private static final int GD_CMD_QUERY_PARAMETER = 0x0503;
    private static final int GD_CMD_SET_DRIVINGNO = 0x0505;
    private static final int GD_CMD_FORBID_COUNT = 0x0506;
    private static final int GD_CMD_SET_TIME = 0x0507;
    private static final int GD_UP_REQUEST_AREA = 0x0601;
    private static final int GD_UP_GET_AREA = 0x0602;
    private static final int GD_UP_UPDATE_ORDER = 0x0603;
    private static final int GD_UP_DOWN_ORDER_INFO = 0x0604;

    public static final String TAG = "TcpClient";
    public static final int SOCKET_MAX_SIZE = (1500);
    private String SEVER_IP;
    private int SEVER_PORT;

    //    public Handler handler;
    //public Handler uiHandler;
    byte[] phoneNumber;
    byte[] plateNumber;
    byte[] loginInfo;
    int sendSequence;
    int recvSequence;

    int workSequence;
    public Socket client = null;
    //BufferedReader br = null;
    OutputStream out_put = null;
    InputStream in_put = null;

    public enum EnumState {HALT, REGCOMPLICT, UNREGISTER, UNLOGIN, WAIT, SLEEP}

    public EnumState handlerState;

    boolean isSendDeviceInfo = false;
    int wait_for_up_query_photo;
    int wait_for_up_query_trainlist;

    long flagPlayTiem = System.currentTimeMillis();//最后一次播放声音的时间

    private void play(String txt, long time) {
        if (System.currentTimeMillis() - flagPlayTiem >= time) {
            flagPlayTiem = System.currentTimeMillis();
            SoundManage.ttsPlaySound(TcpClient.this.context, txt);
        }
    }

    /*
     * Socket重连
     * */
    private static boolean SocketOnLine = true;

    private void ReSocketContent() {
        boolean isNetStop = false;
        tcpLinkState = false;
        SocketOnLine = false;

        try {
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
            //Toast.makeText(context, "网络不可以用",Toast.LENGTH_LONG).show();
            //改变背景或者 处理网络的全局变量
            Log.e("ReSocketContent", "网络断开");
            MessageDefine.isNetwork = false;
            MessageDefine.lxpxyc = System.currentTimeMillis();
//            MessageDefine.isBlindArea=1;
            Log.d("ReSocketContent", "网络不可用");
            isNetStop = true;

            //无网络 关闭线程

            try {
                if (in_put != null)
                    in_put.close();
                if (out_put != null)
                    out_put.close();
                if (client != null)
                    client.close();
                in_put = null;
                out_put = null;
                client = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
            ReSocketContent();

//            if (MessageDefine.isTcpStart)
//            {
//                TcpClient.getInstance(context).setHandlerMain(null);
//            }

        } else {

            Log.e("ReSocketContent", "网络重连");
            SocketOnLine = true;
            MessageDefine.isNetwork = true;
//            sendMsgToUi(MessageDefine.MSG_CONNECT_EXCETION);
            //如果之前已经注册成功，点击保存进行鉴权。否则，进行注册。（注销成功之后，点击保存，才会重新注册）

//            if(client == null || out_put == null || in_put == null){
//            }
            connect();
//            TcpClient.getInstance(context).setHandlerMain(null);
        }

    }

    private boolean clientting = true;
    private boolean send8003 = true;
    private int sendif = 0;
    private int resend = 0;

    private void ThisLog(String s) {
        Log.e(TAG, s);
    }

    private void set_clent() {
//        try {
//            if(client != null)
//                client.close();
//            if(in_put != null)
//                in_put.close();
//            if(out_put != null)
//                out_put.close();
//            client = null;
//            in_put = null;
//            out_put = null;
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

}