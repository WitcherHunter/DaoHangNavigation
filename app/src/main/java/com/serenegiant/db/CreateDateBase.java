package com.serenegiant.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2016/6/6 0006.
 */
public class CreateDateBase {
    public  static  String TABLE_IMG="imgInfo";//图片表
    public  static  String TABLE_VIDEO_HEAD="videoInfo";//视频头信息
    public  static  String TABLE_REAERVATION="reservationInfo";//预约信息表
    public  static  String TABLE_TRAINING_RECORD="trainingRecord";//培训记录表
    public  static  String TABLE_CHECK_RECORD="checkRecord";//培训记录查询表
    public  static  String TABLE_INSTRUCTOR_LOGIN_INFO ="instructor_loginInfo";//登录信息
    public  static  String TABLE_LEARNER_LOGIN_INFO ="learner_loginInfo";//学员登录信息
    public  static  String TABLE_DRIVINGRECORD="drivingRecord";//驾驶员行为记录
    public  static  String TABLE_ELECTRONIC_FENCE = "electronicFence";//电子围栏信息
    public  static  String TABLE_POSITION_RECORD = "positionRecord";//点位信息
    public  static  String TABLE_SEND_ERROR = "sendError";//发送失败信息

    //发送失败包
    public static  void   create_TABLE_SEND_ERROR(SQLiteDatabase db)
    {
        if(!tabIsExist(TABLE_SEND_ERROR,db)) {
            StringBuilder sqlData = new StringBuilder();
            sqlData.append("Create TABLE IF NOT EXISTS "+TABLE_SEND_ERROR);
            sqlData.append("(id integer primary key autoincrement UNIQUE NOT NULL, ");    //id 唯一值，自动增长
            sqlData.append("[instructor_message_num] varchar(10), ");                 //流水号
            sqlData.append("[instructor_message_mesid] varchar(10), ");                 //包数据ID
            sqlData.append("[instructor_message_data] varchar(1024)");                 //包数据
            sqlData.append(")");
            db.execSQL(sqlData.toString());
        }
    }
 //教练员登录信息
    public static  void   create_instructorLoginInfo(SQLiteDatabase db)
    {
        if(!tabIsExist(TABLE_INSTRUCTOR_LOGIN_INFO,db)) {
            StringBuilder sqlData = new StringBuilder();
            sqlData.append("Create TABLE IF NOT EXISTS "+TABLE_INSTRUCTOR_LOGIN_INFO);
            sqlData.append("(id integer primary key autoincrement UNIQUE NOT NULL, ");    //id 唯一值，自动增长
            sqlData.append("[instructor_identify] varchar(18),");                             //教练员登录ID
            sqlData.append("[instructor_number] varchar(16),");                              //教练员编号
            sqlData.append("[instructor_teach_car] varchar(10),");
            sqlData.append("[instructor_loginType] integer,");                            //1:表示登录  ，2表示登出
            sqlData.append("[instructor_uploadStatus] integer default 0,");           //状态：0表示未上传      1:表示已上传
            sqlData.append("[instructor_gps_data] varchar(256),");                 //GPS数据
            sqlData.append("[instructor_isBlindArea] integer default 0,");        //盲区：0表示不是盲区数据      1：表示是盲区数据
            sqlData.append("[instructor_createTime] datetime default (datetime('now','localtime'))");//创建时间
            sqlData.append(")");
            db.execSQL(sqlData.toString());
        }
    }


    //学员登录信息
    public static  void   create_learner_LoginInfo(SQLiteDatabase db)
    {
        if(!tabIsExist(TABLE_LEARNER_LOGIN_INFO,db)) {
            StringBuilder sqlData = new StringBuilder();
            sqlData.append("Create TABLE IF NOT EXISTS "+TABLE_LEARNER_LOGIN_INFO);
            sqlData.append("([learner_number] varchar(16),");                           //学员编号
            sqlData.append("[learner_instructorNumber] varchar(16),");            //当前教练员编码
            sqlData.append("[learner_course] varchar(10),");        //当前课程
            sqlData.append("[learner_class_id] integer,");           //当前培训ID
            sqlData.append("[learner_login_date] datetime,");            //签到时间
            sqlData.append("[learner_logout_date] datetime,");           //签退时间
            sqlData.append("[learner_learned_time] integer, ");           //签退时间
            sqlData.append("[learner_learned_miles] integer, ");           //签退时间
            sqlData.append("[learner_loginType] integer,");                      //1:表示登录  ，2表示登出
            sqlData.append("[learner_uploadStatus] integer default 0,");       //状态：0表示未上传      1:表示已上传
            sqlData.append("[learner_gps_data] varchar(256),");                 //GPS数据
            sqlData.append("[learner_isBlindArea] integer default 0,");        //盲区：0表示不是盲区数据      1：表示是盲区数据
            sqlData.append("[learner_createTime] datetime default (datetime('now','localtime'))");//创建时间
            sqlData.append(")");
            db.execSQL(sqlData.toString());
        }
    }
    //培训记录
    public static void  create_trainingRecord(SQLiteDatabase db)
    {
        if(!tabIsExist(TABLE_TRAINING_RECORD,db)) {
            StringBuilder sqlData = new StringBuilder();
            sqlData.append("Create TABLE IF NOT EXISTS "+TABLE_TRAINING_RECORD);
            sqlData.append("([training_id] varchar(32) primary key  UNIQUE NOT NULL,");    //学时记录编号
            sqlData.append("[training_instructor_number] varchar(16),");                 //教练员编号
            sqlData.append("[training_learner_number] varchar(16),");                   //学员编号
            sqlData.append("[training_class_id] integer, ");                     //课堂ID
            sqlData.append("[training_created_time] varchar(6), ");                     //产生时间
            sqlData.append("[training_course_name] varchar(10), ");                     //培训课程
            sqlData.append("[training_state] integer, ");                     //有效状态
            sqlData.append("[training_max_speed] integer, ");                     //最大时速 当前一分钟内
            sqlData.append("[training_miles] integer, ");                     //本次产生的里程
            sqlData.append("[training_gps_data] varchar(256),");                 //GPS数据
            sqlData.append("[training_isBlindArea] integer default 0,");        //盲区：0表示不是盲区数据      1：表示是盲区数据
            sqlData.append("[training_uploadStatus] integer default 0,");       //状态：0表示未上传      1:表示已上传
            sqlData.append("[training_createTime] datetime default (datetime('now','localtime'))");//创建时间
            sqlData.append(")");
            db.execSQL(sqlData.toString());
        }
    }

    public static void   create_checkRecord(SQLiteDatabase db)
    {
        if(!tabIsExist(TABLE_CHECK_RECORD,db)) {
            StringBuilder sqlData = new StringBuilder();
            sqlData.append("Create TABLE IF NOT EXISTS "+TABLE_CHECK_RECORD);
            sqlData.append("([record_id] varchar(20) primary key  UNIQUE NOT NULL,");    //学时记录编号
            sqlData.append("[record_coach_id] varchar(8),");                 //教练员编号
            sqlData.append("[record_coach_login_id] varchar(12),");                 //教练员登录ID
            sqlData.append("[record_student_id] varchar(8),");                   //学员编号
            sqlData.append("[record_student_login_id] varchar(16),");                 //学员登录ID
            sqlData.append("[record_start_time] datetime,");                 //开始时间
            sqlData.append("[record_end_time] datetime,");                 //结束时间
            sqlData.append("[record_train_time] integer default 0,");      //培训时长
            sqlData.append("[record_fence_id] varchar[6], "); //培训区域ID
            sqlData.append("[record_student_name] varchar[32], "); //学员姓名
            sqlData.append("[record_coach_name] varchar[32], "); //教练员姓名
            sqlData.append("[training_uploadStatus] integer default 0,");       //状态：0表示未上传      1:表示已上传
            sqlData.append("[record_gps_data] varchar(256),");                 //GPS数据
            sqlData.append("[record_isBlindArea] integer default 0,");        //盲区：0表示不是盲区数据      1：表示是盲区数据
            sqlData.append("[record_createTime] datetime default (datetime('now','localtime'))");//创建时间
            sqlData.append(")");
            db.execSQL(sqlData.toString());
        }
    }
    //驾驶员行为记录
    public  static void create_drivingRecord(SQLiteDatabase db)
    {
        if(!tabIsExist(TABLE_DRIVINGRECORD,db)) {
            StringBuilder sqlData = new StringBuilder();
            sqlData.append("Create TABLE IF NOT EXISTS "+TABLE_DRIVINGRECORD);
            sqlData.append("([driving_student_number] varchar(20),");       //学员编号
            sqlData.append("[driving_coach_number] varchar(20),");       //教练员编号
            sqlData.append("[driving_training_id] varchar(20) primary key  UNIQUE NOT NULL,");    //学时记录编号
            sqlData.append("[driving_data] varchar(512),");                 //行为数据
            sqlData.append("[driving_uploadStatus] integer default 0,");       //状态：0表示未上传      1:表示已上传
            sqlData.append("[driving_uploadTime] datetime,");//上传时间
            sqlData.append("[driving_gps_data] varchar(256),");                 //GPS数据
            sqlData.append("[driving_isBlindArea] integer default 0,");        //盲区：0表示不是盲区数据      1：表示是盲区数据
            sqlData.append("[driving_createTime] datetime default (datetime('now','localtime'))");//创建时间
            sqlData.append(")");
            db.execSQL(sqlData.toString());
        }
    }

    //点位信息记录
    public  static void create_PositionRecord(SQLiteDatabase db)
    {
        if(!tabIsExist(TABLE_POSITION_RECORD,db)) {
            StringBuilder sqlData = new StringBuilder();
            sqlData.append("Create TABLE IF NOT EXISTS "+TABLE_POSITION_RECORD);
            sqlData.append("([position_id] INTEGER PRIMARY KEY AUTOINCREMENT,");    //学时记录编号
            sqlData.append("[position_data] varchar(512),");                 //行为数据
            sqlData.append("[position_uploadStatus] integer default 0,");       //状态：0表示未上传      1:表示已上传
            sqlData.append("[position_createTime] datetime default (datetime('now','localtime'))");//创建时间
            sqlData.append(")");
            db.execSQL(sqlData.toString());
        }
    }

    public static void  create_electronicFence(SQLiteDatabase db)
    {
        if(!tabIsExist(TABLE_ELECTRONIC_FENCE,db)) {
            StringBuilder sqlData = new StringBuilder();
            sqlData.append("Create TABLE IF NOT EXISTS "+TABLE_ELECTRONIC_FENCE);
            sqlData.append("([fence_id] varchar(6) primary key  UNIQUE NOT NULL,");    //电子围栏ID
            sqlData.append("[fence_mode] integer,");
            sqlData.append("[fence_point_number] integer,");                 //多边形边数
            sqlData.append("[fence_point_value] varchar(512),");       //经纬度10六次方倍数值
            sqlData.append("[fence_update_time] datetime,");//同步时间
            sqlData.append("[driving_createTime] datetime default (datetime('now','localtime'))");//创建时间
            sqlData.append(")");
            db.execSQL(sqlData.toString());
        }
    }
    //预约信息
    public static void   create_reservationInfo(SQLiteDatabase db)
    {
        if(!tabIsExist(TABLE_REAERVATION,db)) {
            StringBuilder sqlData = new StringBuilder();
            sqlData.append("Create TABLE IF NOT EXISTS "+TABLE_REAERVATION);
            sqlData.append("([reservation_id] varchar(16) primary key  UNIQUE NOT NULL,");    //预约订单编号
            sqlData.append("[reservation_course] integer,");         //培训科目
            sqlData.append("[reservation_startTime] datetime,");           //预约开始时间
            sqlData.append("[reservation_endTime] datetime, ");           //预约结束时间
            sqlData.append("[reservation_area_number] varchar(6),");       //预约场地编号
            sqlData.append("[reservation_instructor_number] varchar(8),");        // 教练员编号
            sqlData.append("[reservation_coach_name] varchar(32), ");              //教练员姓名
            sqlData.append("[reservation_learner_number] varchar(8),");        // 学员编号
            sqlData.append("[reservation_student_name] varchar(32), ");   //学员姓名
            sqlData.append("[reservation_createTime] datetime default (datetime('now','localtime')), ");//创建时间
            sqlData.append("[reservation_status] integer default 0,");                                            //0 表示未处理  1表示已经处理
            sqlData.append("[reservation_order_time] integer, ");             //状态更新时间
            sqlData.append("[reservation_learned_miles] integer default 0");
            sqlData.append(")");
            db.execSQL(sqlData.toString());
        }
    }

    byte upMode;//上传模式
    byte channel;//摄像头通道号
    byte size;//图像尺寸
    byte eventType;//发起图片的事件类型
    String stuDrivingNo;//学员驾校编号
    String stuNumber;//学员编号
    byte[] stuLoginNumber;//学员登录编号/预约订单编号
    byte[] takeID;//拍摄编号
    byte[] gpsInfo;//完整GPS数据包

    //图片表字段
    public static void create_imgTable(SQLiteDatabase db)
    {
        if(!tabIsExist(TABLE_IMG,db)) {
            StringBuilder sqlData = new StringBuilder();
            sqlData.append("Create TABLE IF NOT EXISTS "+TABLE_IMG);
            sqlData.append("([photo_up_mode] integer, ");//上传模式
            sqlData.append("[photo_channel] integer, ");                                //摄像头通道号
            sqlData.append("[photo_size] integer, ");                             //图像尺寸
            sqlData.append("[photo_event_type] integer, ");                             //发起图片的事件类型
            sqlData.append("[photo_class_id] integer, ");                     //学员驾校编号
            sqlData.append("[photo_student_number] varchar(32), ");                             //学员登录编号/预约订单编号
            sqlData.append("[photo_student_take_id] varchar(32), ");                             //拍摄编号
            sqlData.append("[photo_student_gps_info] varchar(64), ");                             //完整GPS数据包
            sqlData.append("[photo_path] varchar(128) UNIQUE NOT NULL, ");                             //照片存储地址
            sqlData.append("[photo_state] integer default 0, ");  //状态：0表示未上传      1:表示已上传
            sqlData.append("[photo_gps_data] varchar(256),");                 //GPS数据
            sqlData.append("[photo_isBlindArea] integer default 0, "); //盲区： 0表示不是盲区数据    1：表示是盲区数据
            sqlData.append("[photo_createTime] datetime default (datetime('now','localtime'))) ");//创建时间
            db.execSQL(sqlData.toString());
        }
    }

    //视频路径索引表
    public static void create_videoTable(SQLiteDatabase db)
    {
        if(!tabIsExist(TABLE_VIDEO_HEAD,db)) {
            StringBuilder sqlData = new StringBuilder();
            sqlData.append("Create TABLE IF NOT EXISTS "+TABLE_VIDEO_HEAD);
            sqlData.append("([video_up_mode] integer, ");//上传模式
            sqlData.append("[video_channel] integer, ");                                //摄像头通道号
            sqlData.append("[video_size] integer, ");                             //图像尺寸
            sqlData.append("[video_event_type] integer, ");                             //发起图片的事件类型
            sqlData.append("[video_student_driving_no] varchar(32), ");                     //学员驾校编号
            sqlData.append("[video_student_number] varchar(32), ");                             //学员登录编号/预约订单编号
            sqlData.append("[video_student_login_number] varchar(32), ");                             //学员编号
            sqlData.append("[video_student_take_id] varchar(32), ");                             //拍摄编号
            sqlData.append("[video_student_gps_info] varchar(64), ");                             //完整GPS数据包
            sqlData.append("[video_path] varchar(128) UNIQUE NOT NULL, ");                             //照片存储地址
            sqlData.append("[video_state] integer default 0,");  //状态：0表示未上传      1:表示已上传
            sqlData.append("[video_createTime] datetime default (datetime('now','localtime')))");//创建时间
            db.execSQL(sqlData.toString());
        }
    }


    /**
     * 判断某张表是否存在
     * @param tabName 表名
     * @return
     */
    public static boolean tabIsExist(String tabName,SQLiteDatabase db){
        boolean result = false;
        if(tabName == null){
            return false;
        }
        try {
            String sql = "select count(*) as c from sqlite_master where type ='table' and name ='"+tabName.trim()+"'" ;
            Cursor cursor = db.rawQuery(sql, null);
            if(cursor.moveToNext()){
                int count = cursor.getInt(0);
                if(count>0){
                    result = true;
                }
            }

        } catch (Exception e) {
            // TODO: handle exception
        }
        return result;
    }








    private  static void initLearnerData()
    {
        /*
        LearnerInfo learnerInfo=new LearnerInfo();
        learnerInfo.setLearner_card("12181FF3");
        learnerInfo.setLearner_id("F1234568");
        learnerInfo.setLearner_identification("441333198809098881");
        learnerInfo.setLearner_name("王大成");
        learnerInfo.setLearner_phone("18866665555");
        Insert_DB.getInstance().insertLearner(learnerInfo);

        learnerInfo=new LearnerInfo();
        learnerInfo.setLearner_card("5B901B73");
        learnerInfo.setLearner_id("F1234569");
        learnerInfo.setLearner_identification("441333199909098888");
        learnerInfo.setLearner_name("吕四儿");
        learnerInfo.setLearner_phone("13566665555");
        Insert_DB.getInstance().insertLearner(learnerInfo);

        learnerInfo=new LearnerInfo();
        learnerInfo.setLearner_card("5B901B74");
        learnerInfo.setLearner_id("F0034569");
        learnerInfo.setLearner_identification("10C770008557112A");
        learnerInfo.setLearner_name("零零柒");
        learnerInfo.setLearner_phone("18718518888");
        Insert_DB.getInstance().insertLearner(learnerInfo);
        */
    }

    private  static void initInstructorData()
    {
        /*
        InstructorInfo instructorInfo=new InstructorInfo();
        instructorInfo.setInstructor_cardID("823F30E3");
        instructorInfo.setInstructor_id("A1234569");
        instructorInfo.setInstructor_identification("551333198809098889");
        instructorInfo.setInstructor_licensePlate("粤U88888");
        instructorInfo.setInstructor_name("李飞飞");
        instructorInfo.setInstructor_schoolID("12345678");
        instructorInfo.setInstructor_phone("18866665555");
        Insert_DB.getInstance().insertInstructor(instructorInfo);

        instructorInfo=new InstructorInfo();
        instructorInfo.setInstructor_cardID("DB773073");
        instructorInfo.setInstructor_id("F1234567");
        instructorInfo.setInstructor_identification("221333199909098888");
        instructorInfo.setInstructor_licensePlate("粤U77777");
        instructorInfo.setInstructor_name("李大龙");
        instructorInfo.setInstructor_schoolID("12345678");
        instructorInfo.setInstructor_phone("18777776666");
        Insert_DB.getInstance().insertInstructor(instructorInfo);
        */
    }


    //教练员信息
    public static  void   create_instructor(SQLiteDatabase db)
    {
        /*
        if(!tabIsExist(TABLE_INSTRUCTOR,db)) {
            StringBuilder sqlData = new StringBuilder();
            sqlData.append("Create TABLE IF NOT EXISTS "+TABLE_INSTRUCTOR);
           // sqlData.append("([instructor_loginID] varchar(30),"); //教练登录编号
            sqlData.append("([instructor_id] varchar(20)  primary key UNIQUE NOT NULL,");      //教练员编号 唯一
            sqlData.append("[instructor_cardID] varchar(20) NOT NULL,");                          //卡号
            sqlData.append("[instructor_identification] varchar(20),");                           //教练身份证号
          //  sqlData.append("[instructor_GPS] varchar(50),");                                       //GPS数据
            sqlData.append("[instructor_schoolID] varchar(50),");     //教练驾校编号

            sqlData.append("[instructor_name] varchar(20) NOT NULL,");                           //教练员姓名
            sqlData.append("[instructor_licensePlate] varchar(20),");                            //车牌
            sqlData.append("[instructor_phone] varchar(30)");                                   //教练员电话
            sqlData.append(")");
            db.execSQL(sqlData.toString());

            initInstructorData();//插入基础数据
        }
        */
    }
    //学员信息表
    public static  void   create_learner(SQLiteDatabase db)
    {
          /*
        if(!tabIsExist(TABLE_LEARNER,db)) {
            StringBuilder sqlData = new StringBuilder();
            sqlData.append("Create TABLE IF NOT EXISTS "+TABLE_LEARNER);
            sqlData.append("([learner_id] varchar(20)  primary key UNIQUE NOT NULL,");                           //学员编号
            sqlData.append("[learner_card] varchar(30) NOT NULL,");                           //学员卡号
            sqlData.append("[learner_identification] varchar(30) ,");                //身份证
            sqlData.append("[learner_name] varchar(30) NOT NULL,");                           //学员姓名
            sqlData.append("[learner_course] varchar(20),");                           //科目
            sqlData.append("[learner_totalTime] varchar(50),");                    //总的学时
            sqlData.append("[learner_phone] varchar(30))");                                   //学员电话
            db.execSQL(sqlData.toString());
            initLearnerData();//插入基础数据
        }
        */
    }

}
