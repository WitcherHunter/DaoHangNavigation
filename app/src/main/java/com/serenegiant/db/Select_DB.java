package com.serenegiant.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.serenegiant.dataFormat.DrivingRecordInfo;
import com.serenegiant.entiy.CoachLoginInfo;
import com.serenegiant.entiy.PhotoHeadInformation;
import com.serenegiant.entiy.ReservationInfo;
import com.serenegiant.entiy.StudentLoginInfo;
import com.serenegiant.entiy.TrainingRecord;
import com.serenegiant.entiy.VideoHeadInformation;
import com.serenegiant.net.FenceDataStruct;
import com.serenegiant.ui.TrainListInfo;
import com.serenegiant.utils.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/6/6 0006.
 */
public class Select_DB {
    private static Select_DB selectDB = null;
    public static String coachSerial;
    public static String coachNumber;
    public static String stuSerial;
    public static String stuNumber;
    public static String trainlistSerial;

    public static Select_DB getInstance() {
        if (selectDB == null) {
            selectDB = new Select_DB();
        }
        return selectDB;
    }
    /*
        发送失败包查询
         */
    public String ErrorMessage(String s1,String s2) {
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null)
            return null;
        String table = CreateDateBase.TABLE_SEND_ERROR;// CreateDateBase.TABLE_LEARNER;
        String[] columns;
        String selection;
        if(s1 == null || s2 == null){
            columns = new String[]{"instructor_message_data"};//返回那一列，如果参数是null,则返回所有列
            selection = null;  //查询条件
        }else{
            columns = new String[]{"instructor_message_data"};//返回那一列，如果参数是null,则返回所有列
            selection = " instructor_message_num=" + s1 +" and instructor_message_mesid=" + s2;  //查询条件
        }

        String groupBy = null; //分组
        String having = null;
        String orderBy = null;//排序
        Cursor c = query(db, table, columns, selection, null, groupBy, having, orderBy);
        if (c != null) {
            if (c.moveToNext()) {
                String  str = c.getString(0);//数据包
                return str;
            }
        }
        return null;
    }

    /*
    identification 身份证
     */
    public String[] verificationInstructor(String identification) {
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null)
            return null;
        String table = "";// CreateDateBase.TABLE_INSTRUCTOR;
        String[] columns = new String[]{"instructor_id,instructor_cardID,instructor_licensePlate,instructor_phone,instructor_name"};//返回那一列，如果参数是null,则返回所有列
        String selection = " instructor_identification=?";  //查询条件
        String[] selectionArgs = new String[]{identification};
        String groupBy = null; //分组
        String having = null;
        String orderBy = null;//排序
        Cursor c = query(db, table, columns, selection, selectionArgs, groupBy, having, orderBy);
        if (c != null) {
            if (c.moveToFirst()) {
                String[] str = new String[5];
                str[0] = c.getString(0);  ////编号
                str[1] = c.getString(1);//卡号
                str[2] = c.getString(2);//车牌
                str[3] = c.getString(3);//电话
                str[4] = c.getString(4);//教练姓名
                return str;
            }
        }
        return null;
    }

    /*
    identification 身份证
     */
    public String[] verificationLearner(String identification) {
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null)
            return null;
        String table = "";// CreateDateBase.TABLE_LEARNER;
        String[] columns = new String[]{"learner_id,learner_card,learner_phone,learner_name,learner_totalTime"};//返回那一列，如果参数是null,则返回所有列
        String selection = " learner_identification=?";  //查询条件
        String[] selectionArgs = new String[]{identification};
        String groupBy = null; //分组
        String having = null;
        String orderBy = null;//排序
        Cursor c = query(db, table, columns, selection, selectionArgs, groupBy, having, orderBy);
        if (c != null) {
            while (c.moveToNext()) {
                String[] str = new String[5];
                str[0] = c.getString(0);//学员编号
                str[1] = c.getString(1);//学员卡号
                str[2] = c.getString(2);//学员电话
                str[3] = c.getString(3);//学员姓名
                str[4] = c.getString(4);//总学时
                return str;

            }
        }
        return null;
    }

    //flag = 1 signin  2 signout
    public CoachLoginInfo getDBCoachSignedData(int flag) {
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null)
            return null;
        String table = CreateDateBase.TABLE_INSTRUCTOR_LOGIN_INFO;// CreateDateBase.TABLE_LEARNER;
        String[] columns = new String[]{"instructor_number,instructor_identify,instructor_teach_car,instructor_loginType,instructor_gps_data,instructor_isBlindArea"};//返回那一列，如果参数是null,则返回所有列
        String where;
        where = " instructor_uploadStatus=0 ";  //查询条件
        //String[] selectionArgs;
        String groupBy = null; //分组
        String orderBy = null;//排序
        String having = null;
        Cursor c = query(db, table, columns, where, null, groupBy, having, orderBy);
        if (c != null) {
            try {
                if (c.moveToNext()) {
                    CoachLoginInfo info = new CoachLoginInfo();
                    info.setNumber(c.getString(0));
                    info.setIdentifyNumber(c.getString(1));
                    info.setTeachCarType(c.getString(2));
                    info.setType(Integer.valueOf(c.getString(3)));
                    info.setGpsDate(StringUtils.HexStringToBytes(c.getString(4)));
                    info.setIsBlindArea(c.getInt(5));
                    return info;//签到数据
                }
            }catch (Exception e)
            {
                return null;
            }
        }
        return null;
    }
    /*
     * 学员登陆条数
     * */
    public int getDBStudentSignNun(){
        int result = 0;
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null)
            return -1;
        String Sql =  "select count(*)  from " + CreateDateBase.TABLE_LEARNER_LOGIN_INFO + " where learner_uploadStatus = 0";
        Cursor cursor = db.rawQuery(Sql, null);
        if(cursor.moveToNext()){
            int count = cursor.getInt(0);
            if(count>0){
                result = count;
            }
        }
        return result;
    }
    /*
     * 教练登陆条数
     * */
    public int getDBTeacherSignNun(){
        int result = 0;
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null)
            return -1;
        String Sql =  "select count(*) from " + CreateDateBase.TABLE_INSTRUCTOR_LOGIN_INFO + " where instructor_uploadStatus = 0";
        Cursor cursor = db.rawQuery(Sql, null);
        if(cursor.moveToNext()){
            int count = cursor.getInt(0);
            if(count>0){
                result = count;
            }
        }
        return result;
    }
    /*
     * 培训记录条数
     * */
    public int getclassSignNun(){
        int result = 0;
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null)
            return -1;
        String Sql =  "select count(*) from " + CreateDateBase.TABLE_TRAINING_RECORD + " where training_uploadStatus = 0";
        Cursor cursor = db.rawQuery(Sql, null);
        if(cursor.moveToNext()){
            int count = cursor.getInt(0);
            if(count>0){
                result = count;
            }
        }
        return result;
    }
    /*
     * 位置信息条数
     * */
    public int getlocationNun(){
        int result = 0;
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null)
            return -1;
        String Sql =  "select count(*) as c from " + CreateDateBase.TABLE_POSITION_RECORD + " where position_uploadStatus = 0";
        Cursor cursor = db.rawQuery(Sql, null);
        if(cursor.moveToNext()){
            int count = cursor.getInt(0);
            if(count>0){
                result = count;
            }
        }
        return result;
    }

    /*
     * 未上传照片条数
     * */
    public int getphotoNun(){
        int result = 0;
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null)
            return -1;
        String Sql =  "select count(*) from " + CreateDateBase.TABLE_IMG + " where photo_state = 0 and photo_event_type = 0 ";
        Cursor cursor = db.rawQuery(Sql, null);
        if(cursor.moveToNext()){
            int count = cursor.getInt(0);
            if(count>0){
                result = count;
            }
        }
        return result;
    }
    //flag = 1 signin  2 signout
    public StudentLoginInfo getDBStuSignedData(int flag, long classId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null)
            return null;
        String table = CreateDateBase.TABLE_LEARNER_LOGIN_INFO;
        String[] columns = new String[]{"learner_number,learner_instructorNumber,learner_course, learner_class_id, learner_login_date, learner_logout_date, learner_learned_time, learner_learned_miles, learner_loginType,learner_gps_data,learner_isBlindArea "};//返回那一列，如果参数是null,则返回所有列
        String where;
        if (flag == 1) {
            where = " learner_uploadStatus=0 and learner_loginType = 1";  //查询条件
        } else {
            where = " learner_uploadStatus=0 and learner_loginType = 2 and learner_class_id = '" + classId + "'";  //查询条件
        }

//        where = " learner_uploadStatus=0";
        //String[] selectionArgs;
        String groupBy = null; //分组
        String orderBy = "learner_createTime ASC";//排序
        String having = null;
        Cursor c = query(db, table, columns, where, null, groupBy, having, orderBy);
        if (c != null) {
            try {
                if (c.moveToNext()) {
                    StudentLoginInfo info = new StudentLoginInfo();
                    info.setStuNumber(c.getString(0));
                    info.setCoachNumber(c.getString(1));
                    info.setCourse(StringUtils.HexStringToBytes(c.getString(2)));
                    info.setClassID(c.getInt(3));
                    info.setLoginDate(sdf.parse(c.getString(4)));
                    info.setLogoutDate(sdf.parse(c.getString(5)));
                    info.setMinutes(c.getInt(6));
                    info.setMiles(c.getInt(7));
                    info.setGpsDate(StringUtils.HexStringToBytes(c.getString(9)));
                    info.setIsBlindArea(c.getInt(10));
                    return info;
                }
            } catch (Exception ex) {
                return null;
            }
        }
        return null;
    }

    public TrainingRecord getDBTrainlistData() {
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null)
            return null;
        String table = CreateDateBase.TABLE_TRAINING_RECORD;//
        String[] columns = null;//= new String[] {"training_id,training_instructor_number, training_learner_number, training_class_id"};//返回那一列，如果参数是null,则返回所有列
        String where;

        where = " training_uploadStatus=0 ";  //查询条件
        //String[] selectionArgs;
        String groupBy = null; //分组
        String orderBy = null;//排序
        String having = null;
        Cursor c = query(db, table, columns, where, null, groupBy, having, orderBy);
        if (c != null) {
            try {
                if (c.moveToNext()) {
                    TrainingRecord tr = new TrainingRecord();
                    tr.setTraining_id(c.getString(0));
                    tr.setTraining_instructor_number(c.getString(1));
                    tr.setTraining_learner_number(c.getString(2));
                    tr.setTrainClassId(c.getInt(3));
                    tr.setTraining_end_time(c.getString(4));
                    tr.setTrainCourseName(c.getString(5));
                    tr.setState(c.getInt(6));
                    tr.setMaxSpeed(c.getInt(7));
                    tr.setMiles(c.getInt(8));
                    tr.setGpsDate(StringUtils.HexStringToBytes(c.getString(9)));
                    tr.setIsBlindArea(c.getInt(10));
                    return tr;
                }
            }catch (Exception e)
            {
                return null;
            }
        }
        return null;
    }


    public String[] getDBPositionListData() {
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null)
            return null;
        String table = CreateDateBase.TABLE_POSITION_RECORD;//
        String[] columns = new String[]{"position_data,position_id,position_uploadStatus"};//= new String[] {"training_id,training_instructor_number, training_learner_number, training_class_id"};//返回那一列，如果参数是null,则返回所有列
        String where;

        where = " position_uploadStatus=0 ";  //查询条件
        //String[] selectionArgs;
        String groupBy = null; //分组
        String orderBy = null;//排序
        String having = null;
        orderBy = " position_createTime desc";
        Cursor c = query(db, table, columns, where, null, groupBy, having, orderBy);
        if (c != null) {
            try {
                if (c.moveToNext()) {
                    String info = c.getString(c.getColumnIndex("position_data"));
                    int id = c.getInt(c.getColumnIndex("position_id"));
                    int positionstate = c.getInt(c.getColumnIndex("position_uploadStatus"));
                    String[] position = new String[]{String.valueOf(id), info};
                    Log.d("TCP", "获取点位信息成功:" + id + " 上传标志" + positionstate);
                    return position;
                }
            }catch (Exception e)
            {
                return null;
            }
        }
        return null;
    }

    public ArrayList<TrainingRecord> getDBTrainlistData(String studentID, int startIndex, int number) {
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null)
            return null;
        String table = CreateDateBase.TABLE_TRAINING_RECORD;//
        String[] columns = null;
        String where = " training_uploadStatus=0 and training_learner_number = '" + studentID + "' ";  //查询条件
        String[] selectionArgs;
        String groupBy = null; //分组
        String orderBy = null;//排序
        String having = null;
        orderBy = String.format("training_createTime desc limit %d offset  %d", number, startIndex);
        Cursor c = query(db, table, columns, where, null, groupBy, having, orderBy);

        if (c != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ArrayList<TrainingRecord> list = new ArrayList<TrainingRecord>();
            while (c.moveToNext()) {
                TrainingRecord tr = new TrainingRecord();
                tr.setTraining_id(c.getString(0));
                tr.setTraining_instructor_number(c.getString(1));
                tr.setTraining_learner_number(c.getString(2));
                tr.setTrainClassId(c.getInt(3));
                tr.setTraining_end_time(c.getString(4));
                tr.setTrainCourseName(c.getString(5));
                tr.setState(c.getInt(6));
                tr.setMaxSpeed(c.getInt(7));
                tr.setMiles(c.getInt(8));
                tr.setGpsDate(StringUtils.HexStringToBytes(c.getString(9)));
                tr.setIsBlindArea(c.getInt(10));
                list.add(tr);
            }
            if (list.size() > 0) {
                Log.i("Select_DB", "查询培训记录数据成功");
            } else {
                Log.i("Select_DB", "查询培训记录数据失败");
            }
            return list;
        }
        return null;
    }



    public ArrayList<TrainingRecord> getTrainListT(Date startTime, Date endTime, int startIndex, int number) {
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (db == null)
            return null;
        String table = CreateDateBase.TABLE_TRAINING_RECORD;//
        String[] columns = null;
        String where = " training_createTime > '" + sdf.format(startTime) + "' and training_createTime < '" + sdf.format(endTime) + "' ";  //查询条件
        String[] selectionArgs;
        String groupBy = null; //分组
        String orderBy = null;//排序
        String having = null;
        orderBy = String.format("training_createTime desc limit %d offset  %d", number, startIndex);
        Cursor c = query(db, table, columns, where, null, groupBy, having, orderBy);

        if (c != null) {
            ArrayList<TrainingRecord> list = new ArrayList<TrainingRecord>();
            while (c.moveToNext()) {
                TrainingRecord tr = new TrainingRecord();
                tr.setTraining_id(c.getString(0));
                tr.setTraining_instructor_number(c.getString(1));
                tr.setTraining_learner_number(c.getString(2));
                tr.setTrainClassId(c.getInt(3));
                tr.setTraining_end_time(c.getString(4));
                tr.setTrainCourseName(c.getString(5));
                tr.setState(c.getInt(6));
                tr.setMaxSpeed(c.getInt(7));
                tr.setMiles(c.getInt(8));
                tr.setGpsDate(StringUtils.HexStringToBytes(c.getString(9)));
                tr.setIsBlindArea(c.getInt(10));
                list.add(tr);
            }
            if (list.size() > 0) {
                Log.i("Select_DB", "查询培训记录数据成功");
            } else {
                Log.i("Select_DB", "查询培训记录数据失败");
            }
            return list;
        }
        return null;
    }

    public byte[] getDBDrivingData() {
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null)
            return null;
        String table = CreateDateBase.TABLE_DRIVINGRECORD;//
        String[] columns = new String[]{"driving_training_id,driving_data"};//返回那一列，如果参数是null,则返回所有列
        String where;

        where = " driving_uploadStatus=0 ";  //查询条件
        //String[] selectionArgs;
        String groupBy = null; //分组
        String orderBy = null;//排序
        String having = null;
        Cursor c = query(db, table, columns, where, null, groupBy, having, orderBy);
        if (c != null) {
            if (c.moveToNext()) {
                trainlistSerial = c.getString(0);//培训记录编号
                return StringUtils.HexStringToBytes(c.getString(1));//培训记录数据
            }
        }
        return null;
    }

    public ArrayList<DrivingRecordInfo> getDBDrivingData(String studentID, int startIndex, int number) {
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null)
            return null;
        String table = CreateDateBase.TABLE_DRIVINGRECORD;//
        String[] columns = new String[]{"driving_training_id,driving_data"};//返回那一列，如果参数是null,则返回所有列
        String where = " driving_uploadStatus=0 and driving_student_number = '" + studentID + "' ";  //查询条件
        String[] selectionArgs;
        String groupBy = null; //分组
        String orderBy = null;//排序
        String having = null;
        orderBy = String.format("driving_uploadTime desc limit %d offset  %d", number, startIndex);
        Cursor c = query(db, table, columns, where, null, groupBy, having, orderBy);

        if (c != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ArrayList<DrivingRecordInfo> list = new ArrayList<DrivingRecordInfo>();
            while (c.moveToNext()) {
                DrivingRecordInfo info = new DrivingRecordInfo();
                info.setDriving_training_id(c.getString(0));
                info.setDriving_data(c.getString(1));
                list.add(info);
            }
            if (list.size() > 0) {
                Log.i("Select_DB", "查询行为数据成功");
            } else {
                Log.i("Select_DB", "查询行为数据失败");
            }
            return list;
        }
        return null;
    }

    public ArrayList<DrivingRecordInfo> getDBDrivingData(Date start, Date end, int startIndex, int number) {
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null)
            return null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String table = CreateDateBase.TABLE_DRIVINGRECORD;//
        String[] columns = new String[]{"driving_training_id,driving_data"};//返回那一列，如果参数是null,则返回所有列
        String where = " driving_createTime > '" + sdf.format(start) + "' and driving_createTime < '" + sdf.format(end) + "' ";  //查询条件
        String[] selectionArgs;
        String groupBy = null; //分组
        String orderBy = null;//排序
        String having = null;
        orderBy = String.format("driving_uploadTime desc limit %d offset  %d", number, startIndex);
        Cursor c = query(db, table, columns, where, null, groupBy, having, orderBy);

        if (c != null) {
            ArrayList<DrivingRecordInfo> list = new ArrayList<DrivingRecordInfo>();
            while (c.moveToNext()) {
                DrivingRecordInfo info = new DrivingRecordInfo();
                info.setDriving_training_id(c.getString(0));
                info.setDriving_data(c.getString(1));
                list.add(info);
            }
            if (list.size() > 0) {
                Log.i("Select_DB", "查询行为数据成功");
            } else {
                Log.i("Select_DB", "查询行为数据失败");
            }
            return list;
        }
        return null;
    }


    //mode  1、定时缓存照片上传    2、抓拍照片上传
    public ArrayList<PhotoHeadInformation> getPiturePath(int mode) {
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null)
            return null;
        String table = CreateDateBase.TABLE_IMG;//
        String[] columns = null;//返回那一列，如果参数是null,则返回所有列
        String where;

        if (mode == 1) {
            where = " photo_state=0 and photo_event_type!=0";  //查询条件
        } else {
            where = " photo_state=0 and photo_event_type=0 ";  //查询条件
        }
        //String[] selectionArgs;
        String groupBy = null; //分组
        String orderBy = null;//排序
        String having = null;
        Cursor c = query(db, table, columns, where, null, groupBy, having, orderBy);
        try{
            if (c != null && c.getCount() > 0) {
                ArrayList<PhotoHeadInformation> list = new ArrayList<>();
                PhotoHeadInformation ptList;
                int requireCount;
                if (c.getCount() > 10) {//限定每次获取列表不超过10个
                    requireCount = 10;
                } else {
                    requireCount = c.getCount();
                }
                for (int i = 0; i < requireCount; i++) {
                    try{
                        if (c.moveToNext()) {
                            ptList = new PhotoHeadInformation();
                            ptList.setUpMode((byte) c.getInt(0));
                            ptList.setChannel((byte) c.getInt(1));
                            ptList.setSize((byte) c.getInt(2));
                            ptList.setEventType((byte) c.getInt(3));
                            ptList.setClassID(c.getInt(4));
                            ptList.setStuNumber(c.getString(5));
                            ptList.setTakeID(StringUtils.HexStringToBytes(c.getString(6)));
                            ptList.setGpsInfo(StringUtils.HexStringToBytes(c.getString(7)));
                            ptList.setSavePath(c.getString(8));
                            ptList.setState((byte) c.getInt(9));
                            ptList.setIsBlindArea(c.getInt(11));
                            list.add(ptList);
                        } else {
                            break;
                        }}catch (Exception e){
                        return list;
                    }
                }
                return list;
            } else {
                return null;
            }}catch (Exception e)
        {
            return null;
        }
    }

    public PhotoHeadInformation getPiturePath(String photoID) {
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null)
            return null;
        String table = CreateDateBase.TABLE_IMG;//
        String[] columns = null;//返回那一列，如果参数是null,则返回所有列
        String where;

        where = " photo_student_take_id='" + photoID + "' ";  //查询条件
        //String[] selectionArgs;
        String groupBy = null; //分组
        String orderBy = null;//排序
        String having = null;
        orderBy = null;
        Cursor c = query(db, table, columns, where, null, groupBy, having, orderBy);

        if (c != null && c.getCount() > 0) {
            PhotoHeadInformation ptList;
            if (c.moveToNext()) {
                ptList = new PhotoHeadInformation();
                ptList.setUpMode((byte) c.getInt(0));
                ptList.setChannel((byte) c.getInt(1));
                ptList.setSize((byte) c.getInt(2));
                ptList.setEventType((byte) c.getInt(3));
                ptList.setClassID(c.getInt(4));
                ptList.setStuNumber(c.getString(5));
                ptList.setTakeID(StringUtils.HexStringToBytes(c.getString(6)));
                ptList.setGpsInfo(StringUtils.HexStringToBytes(c.getString(7)));
                ptList.setSavePath(c.getString(8));
                ptList.setState((byte) c.getInt(9));
                ptList.setIsBlindArea(c.getInt(11));
                return ptList;
            }
        }
        return null;
    }

    public ArrayList<PhotoHeadInformation> getPiturePath(String studentID, int startIndex, int number) {
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null)
            return null;
        String table = CreateDateBase.TABLE_IMG;//
        String[] columns = null;//返回那一列，如果参数是null,则返回所有列
        String where;

        where = " photo_state=0 and photo_student_number='" + studentID + "' ";  //查询条件
        //String[] selectionArgs;
        String groupBy = null; //分组
        String orderBy = null;//排序
        String having = null;
        orderBy = String.format("photo_student_take_id desc limit %d offset  %d", number, startIndex);
        Cursor c = query(db, table, columns, where, null, groupBy, having, orderBy);

        if (c != null && c.getCount() > 0) {
            ArrayList<PhotoHeadInformation> list = new ArrayList<>();
            PhotoHeadInformation ptList;
            for (int i = 0; i < number; i++) {
                if (c.moveToNext()) {
                    ptList = new PhotoHeadInformation();
                    ptList.setUpMode((byte) c.getInt(0));
                    ptList.setChannel((byte) c.getInt(1));
                    ptList.setSize((byte) c.getInt(2));
                    ptList.setEventType((byte) c.getInt(3));
                    ptList.setClassID(c.getInt(4));
                    ptList.setStuNumber(c.getString(5));
                    ptList.setTakeID(StringUtils.HexStringToBytes(c.getString(6)));
                    ptList.setGpsInfo(StringUtils.HexStringToBytes(c.getString(7)));
                    ptList.setSavePath(c.getString(8));
                    ptList.setState((byte) c.getInt(9));
                    ptList.setIsBlindArea(c.getInt(10));
                    list.add(ptList);
                } else {
                    break;
                }
            }
            return list;
        } else {
            return null;
        }
    }

//    public ArrayList<PhotoHeadInformation> getPiturePath(byte[] studentLoginID, int startIndex, int number) {
//        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
//        if (db == null)
//            return null;
//        String table = CreateDateBase.TABLE_IMG;//
//        String[] columns = null;//返回那一列，如果参数是null,则返回所有列
//        String where;
//
//        where = " photo_student_login_number='" + StringUtils.bytesToHexString(studentLoginID) + "' ";  //查询条件
//        //String[] selectionArgs;
//        String groupBy = null; //分组
//        String orderBy = null;//排序
//        String having = null;
//        orderBy = String.format("photo_student_take_id desc limit %d offset  %d", number, startIndex);
//        Cursor c = query(db, table, columns, where, null, groupBy, having, orderBy);
//
//        if (c != null && c.getCount() > 0) {
//            ArrayList<PhotoHeadInformation> list = new ArrayList<>();
//            PhotoHeadInformation ptList;
//            for (int i = 0; i < number; i++) {
//                if (c.moveToNext()) {
//                    ptList = new PhotoHeadInformation();
//                    ptList.setUpMode((byte) c.getInt(0));
//                    ptList.setChannel((byte) c.getInt(1));
//                    ptList.setSize((byte) c.getInt(2));
//                    ptList.setEventType((byte) c.getInt(3));
//                    ptList.setStuDrivingNo(c.getString(4));
//                    ptList.setStuNumber(c.getString(5));
//                    ptList.setStuLoginNumber(StringUtils.HexStringToBytes(c.getString(6)));
//                    ptList.setTakeID(StringUtils.HexStringToBytes(c.getString(7)));
//                    ptList.setGpsInfo(StringUtils.HexStringToBytes(c.getString(8)));
//                    ptList.setSavePath(c.getString(9));
//                    ptList.setState((byte) c.getInt(10));
//                    list.add(ptList);
//                } else {
//                    break;
//                }
//            }
//            return list;
//        } else {
//            return null;
//        }
//    }

    public ArrayList<PhotoHeadInformation> getPiturePath(Date start, Date end, int startIndex, int number) {
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null)
            return null;
        String table = CreateDateBase.TABLE_IMG;//
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String[] columns = null;//返回那一列，如果参数是null,则返回所有列
        String where;
        //where = null;
        where = " photo_createTime > '" + sdf.format(start) + "' and photo_createTime < '" + sdf.format(end) + "' ";  //查询条件
        //where = " datetime(photo_createTime) > datetime(start) and datetime(photo_createTime) < datetime(end)";  //查询条件
        //String[] selectionArgs;
        String groupBy = null; //分组
        String orderBy = null;//排序
        String having = null;
        orderBy = String.format("photo_createTime desc limit %d offset  %d", number, startIndex);
        Cursor c = query(db, table, columns, where, null, groupBy, having, orderBy);

        if (c != null && c.getCount() > 0) {
            ArrayList<PhotoHeadInformation> list = new ArrayList<>();
            PhotoHeadInformation ptList;
            for (int i = 0; i < number; i++) {
                if (c.moveToNext()) {
                    ptList = new PhotoHeadInformation();
                    ptList.setUpMode((byte) c.getInt(0));
                    ptList.setChannel((byte) c.getInt(1));
                    ptList.setSize((byte) c.getInt(2));
                    ptList.setEventType((byte) c.getInt(3));
                    ptList.setClassID(c.getInt(4));
                    ptList.setStuNumber(c.getString(5));
                    ptList.setTakeID(StringUtils.HexStringToBytes(c.getString(6)));
                    ptList.setGpsInfo(StringUtils.HexStringToBytes(c.getString(7)));
                    ptList.setSavePath(c.getString(8));
                    ptList.setState((byte) c.getInt(9));
                    ptList.setIsBlindArea(c.getInt(10));
                    ptList.setCreateTime(c.getString(11));
                    list.add(ptList);
                } else {
                    break;
                }
            }
            return list;
        } else {
            return null;
        }
    }

    public ArrayList<VideoHeadInformation> getVideosPath(String stuLoginID, int startIndex, int limit) {
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null)
            return null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String table = CreateDateBase.TABLE_VIDEO_HEAD;//
        String[] columns = null;//返回那一列，如果参数是null,则返回所有列
        String where;
        where = " video_student_login_number = '" + stuLoginID + "' ";  //查询条件
        //String[] selectionArgs;
        String groupBy = null; //分组
        String orderBy = null;//排序
        String having = null;
        orderBy = String.format("video_createTime desc limit %d offset  %d", limit, startIndex);

        Cursor c = query(db, table, columns, where, null, groupBy, having, orderBy);

        if (c != null && c.getCount() > 0) {
            ArrayList<VideoHeadInformation> list = new ArrayList<>();
            VideoHeadInformation ptList;
            int requireCount;
            if (c.getCount() > limit) {//限定每次获取列表不超过10个
                requireCount = limit;
            } else {
                requireCount = c.getCount();
            }
            for (int i = 0; i < requireCount; i++) {
                if (c.moveToNext()) {
                    ptList = new VideoHeadInformation();
                    ptList.setUpMode((byte) c.getInt(0));
                    ptList.setChannel((byte) c.getInt(1));
                    ptList.setSize((byte) c.getInt(2));
                    ptList.setEventType((byte) c.getInt(3));
                    ptList.setStuDrivingNo(c.getString(4));
                    ptList.setStuNumber(c.getString(5));
                    ptList.setStuLoginNumber(StringUtils.HexStringToBytes(c.getString(6)));
                    ptList.setTakeID(StringUtils.HexStringToBytes(c.getString(7)));
                    ptList.setGpsInfo(StringUtils.HexStringToBytes(c.getString(8)));
                    ptList.setSavePath(c.getString(9));
                    ptList.setState((byte) c.getInt(10));
                    list.add(ptList);
                } else {
                    break;
                }
            }
            return list;
        } else {
            return null;
        }
    }

    //视频上传
    public ArrayList<VideoHeadInformation> getVideosPath(Date start, Date end, int startIndex, int limit) {
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null)
            return null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String table = CreateDateBase.TABLE_VIDEO_HEAD;//
        String[] columns = null;//返回那一列，如果参数是null,则返回所有列
        String where;
        where = " video_createTime > '" + sdf.format(start) + "' and video_createTime < '" + sdf.format(end) + "' ";  //查询条件
        //String[] selectionArgs;
        String groupBy = null; //分组
        String orderBy = null;//排序
        String having = null;
        orderBy = String.format("video_createTime desc limit %d offset  %d", limit, startIndex);

        Cursor c = query(db, table, columns, where, null, groupBy, having, orderBy);

        if (c != null && c.getCount() > 0) {
            ArrayList<VideoHeadInformation> list = new ArrayList<>();
            VideoHeadInformation ptList;
            int requireCount;
            if (c.getCount() > limit) {//限定每次获取列表不超过10个
                requireCount = limit;
            } else {
                requireCount = c.getCount();
            }
            for (int i = 0; i < requireCount; i++) {
                if (c.moveToNext()) {
                    ptList = new VideoHeadInformation();
                    ptList.setUpMode((byte) c.getInt(0));
                    ptList.setChannel((byte) c.getInt(1));
                    ptList.setSize((byte) c.getInt(2));
                    ptList.setEventType((byte) c.getInt(3));
                    ptList.setStuDrivingNo(c.getString(4));
                    ptList.setStuNumber(c.getString(5));
                    ptList.setStuLoginNumber(StringUtils.HexStringToBytes(c.getString(6)));
                    ptList.setTakeID(StringUtils.HexStringToBytes(c.getString(7)));
                    ptList.setGpsInfo(StringUtils.HexStringToBytes(c.getString(8)));
                    ptList.setSavePath(c.getString(9));
                    ptList.setState((byte) c.getInt(10));
                    list.add(ptList);
                } else {
                    break;
                }
            }
            return list;
        } else {
            return null;
        }
    }

    //mode  1、定时缓存照片上传    2、抓拍照片上传
    public ArrayList<VideoHeadInformation> getVideosPath(int mode, int limit) {
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null)
            return null;
        String table = CreateDateBase.TABLE_VIDEO_HEAD;//
        String[] columns = null;//返回那一列，如果参数是null,则返回所有列
        String where;

        if (mode == 1) {
            where = " video_state=0 ";  //查询条件
        } else {
            where = " video_state=0 and video_event_type=0 ";  //查询条件
        }
        //String[] selectionArgs;
        String groupBy = null; //分组
        String orderBy = null;//排序
        String having = null;
        Cursor c = query(db, table, columns, where, null, groupBy, having, orderBy);

        if (c != null && c.getCount() > 0) {
            ArrayList<VideoHeadInformation> list = new ArrayList<>();
            VideoHeadInformation ptList;
            int requireCount;
            if (c.getCount() > limit) {//限定每次获取列表不超过10个
                requireCount = limit;
            } else {
                requireCount = c.getCount();
            }
            for (int i = 0; i < requireCount; i++) {
                if (c.moveToNext()) {
                    ptList = new VideoHeadInformation();
                    ptList.setUpMode((byte) c.getInt(0));
                    ptList.setChannel((byte) c.getInt(1));
                    ptList.setSize((byte) c.getInt(2));
                    ptList.setEventType((byte) c.getInt(3));
                    ptList.setStuDrivingNo(c.getString(4));
                    ptList.setStuNumber(c.getString(5));
                    ptList.setStuLoginNumber(StringUtils.HexStringToBytes(c.getString(6)));
                    ptList.setTakeID(StringUtils.HexStringToBytes(c.getString(7)));
                    ptList.setGpsInfo(StringUtils.HexStringToBytes(c.getString(8)));
                    ptList.setSavePath(c.getString(9));
                    ptList.setState((byte) c.getInt(10));
                    list.add(ptList);
                } else {
                    break;
                }
            }
            return list;
        } else {
            return null;
        }
    }

    public byte[] getFenceList() {
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null)
            return null;
        String table = CreateDateBase.TABLE_ELECTRONIC_FENCE;//
        String[] columns = new String[]{"fence_id"};//返回那一列，如果参数是null,则返回所有列
        String where;

        where = "DATE(fence_update_time) = DATE('now', 'localtime')";//查询条件
        //String[] selectionArgs;
        String groupBy = null; //分组
        String orderBy = null;//排序
        String having = null;
        Cursor c = query(db, table, columns, where, null, groupBy, having, orderBy);
        int count = c.getCount();
        byte[] retBytes = new byte[count * 6];
        if (c != null) {
            for (int i = 0; i < count; i++) {
                if (c.moveToNext()) {
                    byte[] id = c.getString(0).getBytes();
                    if (id.length == 6) {
                        System.arraycopy(id, 0, retBytes, i * 6, 6);
                    }
                } else {
                    break;
                }
            }
            return retBytes;
        }
        return null;
    }

    public byte[] getFencePoints(String fenceID) {
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null)
            return null;
        String table = CreateDateBase.TABLE_ELECTRONIC_FENCE;//
        String[] columns = new String[]{"fence_point_value"};//返回那一列，如果参数是null,则返回所有列
        String where;

        where = " fence_id= '" + fenceID + "' ";  //查询条件
        //String[] selectionArgs;
        String groupBy = null; //分组
        String orderBy = null;//排序
        String having = null;
        Cursor c = query(db, table, columns, where, null, groupBy, having, orderBy);

        if (c.moveToNext()) {
            return c.getBlob(0);
        } else {
            return null;
        }
    }

    public ArrayList<FenceDataStruct> getAllFencesData() {
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null)
            return null;
        String table = CreateDateBase.TABLE_ELECTRONIC_FENCE;//
        String[] columns = new String[]{"fence_id, fence_mode, fence_point_number, fence_point_value"};//返回那一列，如果参数是null,则返回所有列
        String where;
        where = null;  //查询条件
        //String[] selectionArgs;
        String groupBy = null; //分组
        String orderBy = null;//排序
        String having = null;
        Cursor c = query(db, table, columns, where, null, groupBy, having, orderBy);
        if (c != null && c.getCount() > 0) {
            ArrayList<FenceDataStruct> list = new ArrayList<FenceDataStruct>();
            while (c.moveToNext()) {
                FenceDataStruct info = new FenceDataStruct();
                info.setId(c.getString(0));//id
                info.setMode(c.getInt(1));//科目
                info.setCnt(c.getInt(2));//点位信息
                String tempBytes = c.getString(3);//data
                int cnt = info.getCnt();
                if (cnt > 0 && tempBytes != null && tempBytes.length() >= cnt * 8) {
                    long[] lat = new long[cnt];
                    long[] longt = new long[cnt];
                    byte[] temp;
                    String sLat;
                    String sLongt;
                    for (int i = 0; i < cnt; i++) {
                        sLongt = tempBytes.substring(i * 16, i * 16 + 8);
                        sLat = tempBytes.substring(i * 16 + 8, i * 16 + 16);
                        lat[i] = Long.parseLong(sLat, 16);
                        longt[i] = Long.parseLong(sLongt, 16);
                    }
                    info.setLatitude(lat);
                    info.setLongtitude(longt);
                }
                list.add(info);
            }
            if (list.size() > 0) {
                Log.i("Select_DB", "加载订单数据成功，订单数：" + list.size());
            } else {
                Log.i("Select_DB", "无订单数据");
            }
            return list;
        } else {
            return null;
        }
    }


    public byte[] getOrderList() {
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null)
            return null;
        String table = CreateDateBase.TABLE_REAERVATION;//
        String[] columns = new String[]{"reservation_id"};//返回那一列，如果参数是null,则返回所有列
        String where;

        where = "DATE(reservation_startTime) = DATE('now', 'localtime')";
        //where = " driving_uploadStatus=0 ";  //查询条件
        //String[] selectionArgs;
        String groupBy = null; //分组
        String orderBy = null;//排序
        String having = null;
        Cursor c = query(db, table, columns, where, null, groupBy, having, orderBy);
        int count = c.getCount();
        byte[] retBytes = new byte[count * 8];
        if (c != null) {
            for (int i = 0; i < count; i++) {
                if (c.moveToNext()) {
                    byte[] id = StringUtils.HexStringToBytes(c.getString(0));
                    System.arraycopy(id, 0, retBytes, i * 8, 8);
                } else {
                    break;
                }
            }
            return retBytes;
        }
        return null;
    }

    public ArrayList<TrainListInfo> getTrainList(String studentID, int startIndex, int number) {
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null)
            return null;
        String table = CreateDateBase.TABLE_CHECK_RECORD;//
        String[] columns = new String[]{"record_coach_name, record_student_name, record_start_time, record_train_time, record_student_id, record_student_login_id"};//返回那一列，如果参数是null,则返回所有列
        String where = " record_student_id= '" + studentID + "' ";  //查询条件
        String[] selectionArgs;
        String groupBy = null; //分组
        String orderBy = null;//排序
        String having = null;
        orderBy = String.format("record_start_time desc limit %d offset  %d", number, startIndex);
        Cursor c = query(db, table, columns, where, null, groupBy, having, orderBy);


        if (c != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            ArrayList<TrainListInfo> list = new ArrayList<TrainListInfo>();
            while (c.moveToNext()) {
                TrainListInfo info = new TrainListInfo();
                info.setCoachName(c.getString(0));//培训记录编号
                info.setStuName(c.getString(1));//教练员白闹
                try {
                    info.setRecordStartTime(sdf.parse(c.getString(2)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                info.setRecordLearnTime(Integer.valueOf(c.getString(3)));
                info.setRecordStudentID(c.getString(4));
                info.setRecordStudentLoginID(c.getString(5));
                list.add(info);
            }
            if (list.size() > 0) {
                Log.i("Select_DB", "查询培训记录表成功");
            } else {
                Log.i("Select_DB", "查询培训记录表失败");
            }
            return list;
        }
        return null;
    }

    public ArrayList<ReservationInfo> getReservationInfo(String coachNumber, String studentNumber) {
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null)
            return null;
        String table = CreateDateBase.TABLE_REAERVATION;//
        String[] columns = new String[]{"reservation_id ,reservation_course ,reservation_startTime ,reservation_endTime ," +
                "reservation_area_number,reservation_instructor_number,reservation_coach_name, reservation_learner_number, " +
                "reservation_student_name, reservation_status, reservation_order_time, reservation_learned_miles"};//返回那一列，如果参数是null,则返回所有列
        String where;
        if (coachNumber != null && studentNumber != null) {
            where = " reservation_instructor_number='" + coachNumber + "' and reservation_learner_number='" + studentNumber + "' ";
        } else if (coachNumber != null) {
            where = " reservation_instructor_number='" + coachNumber + "' ";
        } else {
            where = null;  //查询条件
        }
        String[] selectionArgs;
        String groupBy = null; //分组
        String orderBy = null;//排序
        String having = null;
        Cursor c = query(db, table, columns, where, null, groupBy, having, orderBy);
        if (c != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ArrayList<ReservationInfo> list = new ArrayList<ReservationInfo>();
            while (c.moveToNext()) {
                ReservationInfo info = new ReservationInfo();
                info.setReservation_id(c.getString(0));//预约订单编号
                info.setReservation_course(c.getInt(1));//培训科目
                try {
                    Date date = sdf.parse(c.getString(2));
                    info.setReservation_startTime(date);//开始时间
                    date = sdf.parse(c.getString(3));
                    info.setReservation_endTime(date);//结束时间
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                info.setReservation_area_number(c.getString(4));//培训区域编号
                info.setReservation_instructor_number(c.getString(5));//教练员编号
                info.setReservation_coach_name(c.getString(6));
                info.setReservation_learner_number(c.getString(7));//学员编号
                info.setReservation_student_name(c.getString(8));
                info.setReservation_status(c.getInt(9));//状态
                info.setReservation_order_time(c.getInt(10));
                info.setReservation_learned_miles(c.getInt(11));
                list.add(info);
            }
            if (list.size() > 0) {
                Log.i("Select_DB", "查询预约数据成功");
            } else {
                Log.i("Select_DB", "查询预约数据失败");
            }
            return list;
        }
        return null;
    }

    public ArrayList<ReservationInfo> getReservationInfo(String coachNumber) {
        return getReservationInfo(coachNumber, null);
    }

    public ArrayList<ReservationInfo> getReservationInfo() {
        return getReservationInfo(null);
    }

    private synchronized Cursor query(SQLiteDatabase db, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        if (db == null) {
            return null;
        }
        try {
            return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        } catch (Exception ex) {
            Log.i("Select_DB", ex.getMessage());
            return null;
        }
    }


    /*
     * 教练员签到签退
     * */

    public List<CoachLoginInfo> getDBCoachSignedData() {
        List<CoachLoginInfo> mList = new ArrayList<CoachLoginInfo>();
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null)
            return null;
        String table = CreateDateBase.TABLE_INSTRUCTOR_LOGIN_INFO;// CreateDateBase.TABLE_LEARNER;
        String[] columns = new String[]{"instructor_number,instructor_identify,instructor_teach_car,instructor_loginType,instructor_gps_data,instructor_isBlindArea"};//返回那一列，如果参数是null,则返回所有列
        String where;
        String groupBy = null; //分组
        String orderBy = null;//排序
        String having = null;
        Cursor c = query(db, table, columns, null, null, groupBy, having, orderBy);
        if (c != null) {
            try {
                if (c.moveToNext()) {
                    CoachLoginInfo info = new CoachLoginInfo();
                    info.setNumber(c.getString(0));
                    info.setIdentifyNumber(c.getString(1));
                    info.setTeachCarType(c.getString(2));
                    info.setType(Integer.valueOf(c.getString(3)));
                    info.setGpsDate(StringUtils.HexStringToBytes(c.getString(4)));
                    info.setIsBlindArea(c.getInt(5));
                    mList.add(info);
                }
            }catch (Exception e)
            {
                return null;
            }
        }
        return mList;
    }

    /*
     * 学员签到签退记录
     * */
    public List<StudentLoginInfo> getDBStuSignedData() {
        List<StudentLoginInfo> mList = new ArrayList<StudentLoginInfo>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null)
            return null;
        String table = CreateDateBase.TABLE_LEARNER_LOGIN_INFO;//
        String[] columns = new String[]{"learner_number,learner_instructorNumber,learner_course, learner_class_id, learner_login_date, learner_logout_date, learner_learned_time, learner_learned_miles, learner_loginType,learner_gps_data,learner_isBlindArea "};//返回那一列，如果参数是null,则返回所有列
        String groupBy = null; //分组
        String orderBy = null;//排序
        String having = null;
        Cursor c = query(db, table, columns, null, null, groupBy, having, orderBy);
        if (c != null) {
            try {
                while (c.moveToNext()) {
                    StudentLoginInfo info = new StudentLoginInfo();
                    info.setStuNumber(c.getString(0));
                    info.setCoachNumber(c.getString(1));
                    info.setCourse(StringUtils.HexStringToBytes(c.getString(2)));
                    info.setClassID(c.getInt(3));
                    info.setLoginDate(sdf.parse(c.getString(4)));
                    info.setLogoutDate(sdf.parse(c.getString(5)));
                    info.setMinutes(c.getInt(6));
                    info.setMiles(c.getInt(7));
                    info.setGpsDate(StringUtils.HexStringToBytes(c.getString(9)));
                    info.setIsBlindArea(c.getInt(10));
                    mList.add(info);
                }
            } catch (Exception ex) {
                return null;
            }
        }
        return mList;
    }

}




