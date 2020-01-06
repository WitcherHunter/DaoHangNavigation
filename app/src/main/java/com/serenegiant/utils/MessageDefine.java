package com.serenegiant.utils;

/**
 * Created by Hambobo on 2016-08-03
 * 定义所有与UI通讯有关的Message.what，统一定义此文件防止多文件的定义中存在冲突，特别是tcpClient线程运行过程中收发消息.
 */
public class MessageDefine {

    //OBDAPI
    public static final int REFRESH_OBD_DATA = 6;



    //    LearnerActivity
    public static final int MSG_REFRESH_ACTIVITY = 9;
    public static final int MSG_REQUIRE_STU_CARD = 10;
    public static final int MSG_STU_FINGER_MATCH = 11;
    public static final int MSG_INVALID_CARD = 12;
    public static final int MSG_FINGER_NOT_MATCH = 13;
    public static final int MSG_INIT_API = 14;
    public static final int MSG_NOT_PERMISSION = 15;
    //ReservationActivity
    public static final int MSG_SEND_RESERVATION = 101;
    public static final int MSG_ORDERLIST_FORBID_LEARN = 102;

    //StudentLoginActivity
    public final static int MSG_STUDENT_SWIPING_CARD = 201;
    public final static int MSG_STUDENT_PUT_FINGER = 202;
    public final static int MSG_STUDENT_FINGER_OK = 203;
    public final static int MSG_STUDENT_CARD_INVALID = 204;
    public final static int MSG_STUDENT_FINGER_INVALID = 205;
    public final static int MSG_INITTAL_API_DEFAULT = 206;
    public final static int MSG_HAVE_BEEN_SIGNED = 207;
    public final static int MSG_STUDENT_CARD_WRONG_TYPE = 208;
    public final static int MSG_STUDENT_START_TRAIN = 209;
    //RainingActivity
    public static final int REFRESH_SECOND_1 = 301;//ms
    public static final int REFRESH_SECOND_2 = 302;//ms
    public static final int REFRESH_SECOND_3 = 303;//ms
    public static final int REFRESH_SECOND_4 = 304;//ms
    public static final int REFRESH_SECOND_5 = 305;//ms
    public static final int REFRESH_SECOND_6 = 306;//ms
    public static final int CAPTURE_PHOTO = 307;
    public static final int START_RECORDING = 308;
    public static final int STOP_RECORDING = 309;
    public static final int FORCE_END_TRAINNING = 310;
    public static final int SWITCH_COM_MODE = 311;
    public static final int SEND_TTS_STRING = 312;
    public static final int START_USE_CAMERA = 313;
    public static final int MSG_LOAD_CAMERA_VIEW = 314;
    public static final int MSG_RESELECT_TRAIN_MODE = 315;
    public static final int MSG_SAVE_EXAMINE_INFO = 316;
    public final static int MSG_TRAIN_SWIPING_CARD = 317;
    public final static int MSG_TRAIN_PUT_FINGER = 318;
    public final static int MSG_TRAIN_PUT_FINGER_NULL = 1318;
    public final static int MSG_TRAIN_FINGER_OK = 319;
    public final static int MSG_TRAIN_CARD_INVALID = 320;
    public final static int MSG_TRAIN_FINGER_INVALID = 321;
    public final static int MSG_TRAIN_API_DEFAULT = 322;
    public final static int MSG_TRAIN_INITIAL_CAMERA_VIEW = 323;
    public final static int MSG_TAKE_SIGNOUT_PHOTO = 324;

    public final static int MSG_TRAIN_FENCE_ID = 325;
    public static final int MSG_PHONE_SEM_CHANGED = 326;
    public static final int MSG_WIFI_SIGNAL_CHANGED = 327;
    public static final int MSG_GPS_SIGNAL_VALID = 328;
    public static final int MSG_GPS_SIGNAL_INVALID = 329;
    //Training mode
    public static final int MSG_TRAIN_MODE_QUIT = 401;

    //coach signed
    public final static int MSG_COACH_SWIPING_CARD = 501;
    public final static int MSG_COACH_PUT_FINGER = 502;
    public final static int MSG_COACH_FINGER_OK = 503;
    public final static int MSG_COACH_CARD_INVALID = 504;
    public final static int MSG_COACH_FINGER_INVALID = 505;
    public final static int MSG_COACH_INIT_API_FAILURE = 506;
    public final static int MSG_COACH_CARD_TYPE_WRONG = 507;

    //AutomaticActivity
    public final static int MSG_AUTOMATIC_UPDATE_SOFTWARE = 601;
    public final static int MSG_AUTOMATIC_UPDATE_PROGRESS = 602;
    public final static int MSG_AUTOMATIC_UPDATE_SUCCESS = 603;
    public final static int MSG_AUTOMATIC_UPDATE_FAILURE = 604;
    public final static int MSG_TCP_THREAD_START = 605;
    public final static int MSG_GET_UNDATAINFO_ERROR = 606;
    public final static int MSG_AUTOMATIC_LATEST_VERSION = 607;
    public final static int MSG_AUTOMATIC_ENTER_EXAMINE = 608;
    //ExportTrainlistFiles
    public final static int MSG_START_EXPORT = 701;
    public final static int MSG_END_EXPORT = 702;
    public final static int MSG_UPDATE_EXPORT = 703;

    //UI to thread
    public final static int MSG_REFRESH_ORDERLIST = 1001;//更新订单
    public final static int MSG_COACH_LOGIN = 1002;//教练员签到
    public final static int MSG_COACH_LOGOUT = 1003;//教练员签退
    public final static int MSG_STUDENT_LOGIN = 1004;//学员签到
    public final static int MSG_STUDENT_LOGOUT = 1005;//学员签退
    public final static int MSG_RESTART_TRAIN = 1006;//
    public final static int MSG_UPLOAD_PROMOTE_PHOTO = 1007; //上传抓拍照片

    //Thread to UI
    public static final int MSG_TAKE_PHOTO_PROMPT = 2001;
    public static final int MSG_TCP_SERVICE_START = 2002;
    public static final int MSG_UNKNOW_HOST_EXCEPTION = 2003;
    public static final int MSG_CONNECT_IO_EXCEPTION = 2004;
    public static final int MSG_ILLEGAL_ARG_EXCEPTION = 2005;
    public static final int MSG_CONNECT_EXCETION = 2006;
    public static  int MSG_TERMINAL_CANCEL =0;
    public static boolean isNetwork = true;//是否有网络
    public static int isBlindArea=0;//是否盲区
    public static boolean isTcpStart = false;//TCP是否开启
    public static boolean isFrist = true;//是否第一次打开设置
    public static boolean TcpSendIsRun = true;
    public static final int MSG_COACH_LOGINPHOTO= 1012;//教练员签到拍照
    public static final int MSG_COACH_LOGOUTPHOTO= 1013;//教练员签退拍照
    public static final int MSG_STU_FINGERCHAR=1014;//学员是否拥有指纹
    public static final int MSG_STU_IDENTIFY_NO=1015;//未绑定学员
    public static final int MSG_STU_IDENTIFY_OK = 1016;//身份认证成功
    public static int sendSequence;
    public static boolean isPositionService = false;
    public static boolean isGPS = false;
    public static long lxpxyc = 0;
    //public static BDNavigation mInstance;
    // 5000~5100 for camera service


    public static final int MSG_NO_NET = 1314;//请检查网络
    public static final int MSG_LAST_STU_NOT_EXITED = 1315;//上一学员还未签退
    public static final int MSG_LAST_COA_NOT_EXITED = 1316;//上一教练员还未签退
    public static final int MSG_REFRESH_LOC = 1317;//进入培训页面，就开始显示经纬度
    public static final int MSG_TEST = 1811;//test
    public static final int MSG_TEST_1 = 1812;//test
    public static final int MSG_TEST_2 = 1813;//test
    public static final int MSG_TEST_3 = 1814;//test
    public static final int MSG_TEST_4 = 1815;//test
    public static final int MSG_TEST_5 = 1816;//test


}
