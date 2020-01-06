package com.serenegiant.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.serenegiant.utils.MessageDefine;

import java.util.Objects;

/**
 * Created by Administrator on 2016/6/6 0006.
 */
public class Update_DB {

    public boolean updateData() {
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null)
            return false;

        //database.execSQL("update instructorInfo  set instructor_id='BD773073'   where instructor_id='F1234567'");

        ContentValues contentValues = new ContentValues();
        contentValues.put("instructor_cardID", "BD773073");
        String whereClause = " instructor_id=? ";
        String[] whereArgs = new String[]{"F1234567"};


        int r = 0;
        r = update(db, "instructorInfo", contentValues, whereClause, whereArgs);
        if (r > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean clearCoachSignFlag(String coachNumber, int type) {
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null)
            return false;
        ContentValues contentValues = new ContentValues();
        contentValues.put("instructor_uploadStatus", 1);
        String whereClause;
        if (type == 1) {
            whereClause = "instructor_number='" + coachNumber + "' and " + "instructor_loginType=1";
        } else {
            whereClause = "instructor_number='" + coachNumber + "' and " + "instructor_loginType=2";
        }
        int rows = update(db, "instructor_loginInfo", contentValues, whereClause, null);
        return (rows > 0);
    }

    /**
     * 修改上传状态
     * @param stuhNumber 实为classid
     * @param type
     * @return
     */
    public boolean clearStuSignFlag(String stuhNumber, int type) {
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null)
            return false;
        ContentValues contentValues = new ContentValues();
        contentValues.put("learner_uploadStatus", 1);
        String whereClause;
        if (type == 1) {
//            whereClause = "learner_number='" + stuhNumber + "' and " + "learner_loginType=1";
            whereClause = "learner_class_id='" + stuhNumber + "' and " + "learner_loginType=1";
        } else {
//            whereClause = "learner_number='" + stuhNumber + "' and " + "learner_loginType=2";
            whereClause = "learner_class_id='" + stuhNumber + "' and " + "learner_loginType=2";
        }
        int rows = update(db, "learner_loginInfo", contentValues, whereClause, null);
        return (rows > 0);
    }

    public boolean clearTrainListFlag(String trainlistSerial) {
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null) {
            return false;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("training_uploadStatus", 1);
        String whereClause;
        whereClause = " training_id='" + trainlistSerial + "'";
        int rows = update(db, "trainingRecord", contentValues, whereClause, null);
        return (rows > 0);
    }


    public boolean clearPositionFlag(int positionID) {
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null) {
            return false;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("position_uploadStatus", 1);
        String whereClause;
        whereClause = " position_id='" + positionID + "'";
        int rows = update(db, CreateDateBase.TABLE_POSITION_RECORD, contentValues, whereClause, null);
        return (rows > 0);
    }

    /**
     * 删除位置信息
     *
     * @param positionId
     * @return
     */
    public int deletePositionData(int positionId) {
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null)
            return -1;
        String table = CreateDateBase.TABLE_POSITION_RECORD;
        return db.delete(table, "position_id=?", new String[]{String.valueOf(positionId)});
    }

    public boolean clearDrivingFlag(String trainlistSerial) {
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null)
            return false;
        ContentValues contentValues = new ContentValues();
        contentValues.put("driving_uploadStatus", 1);
        String whereClause;
        whereClause = " driving_training_id='" + trainlistSerial + "'";
        int rows = update(db, CreateDateBase.TABLE_DRIVINGRECORD, contentValues, whereClause, null);
        return (rows > 0);
    }

    public boolean clearPhotoHeadFlag(String photoID) {
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null) {
            return false;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("photo_state", 1);
        String whereClause;
        whereClause = " photo_student_take_id='" + photoID + "'";
        int rows = update(db, CreateDateBase.TABLE_IMG, contentValues, whereClause, null);
        return (rows > 0);
    }


    public boolean clearVideoHeadFlag(String videoID) {
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null)
            return false;
        ContentValues contentValues = new ContentValues();
        contentValues.put("video_state", 1);
        String whereClause;
        whereClause = " video_student_take_id='" + videoID + "'";
        int rows = update(db, CreateDateBase.TABLE_VIDEO_HEAD, contentValues, whereClause, null);
        return (rows > 0);
    }

    public boolean setOrderlistFinished(String orderNumber, int learnedTime, int learnedMiles) {
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null)
            return false;
        ContentValues contentValues = new ContentValues();
        contentValues.put("reservation_status", learnedTime);
        contentValues.put("reservation_learned_miles", learnedMiles);
        String whereClause;
        whereClause = " reservation_id='" + orderNumber + "'";
        int rows = update(db, CreateDateBase.TABLE_REAERVATION, contentValues, whereClause, null);
        return (rows > 0);
    }

    /*
     table:表名
      ContentValues：更新的值
      whereClause：where条件
      whereArgs：where条件吃参数值
   */
    private synchronized int update(SQLiteDatabase db, String table, ContentValues values, String whereClause, String[] whereArgs) {
        if (db == null) {
            return -1;
        }
        try {
            return db.update(table, values, whereClause, whereArgs);
        } catch (Exception ex) {
            return -1;
        }

    }


    /**
     * 清理一个月前的堆积数据
     */
    public static void deleteOutDateRecord() {
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null)
            return;

        //清理位置信息表（保留一周数据）
        String positionTable = CreateDateBase.TABLE_POSITION_RECORD;
        String exec1 = "DELETE FROM " + positionTable + " WHERE date('now','-7 day') >= date(position_createTime)";
        db.execSQL(exec1);

        //清理培训记录表
        String trainRecordTable = CreateDateBase.TABLE_TRAINING_RECORD;
        String exec2 = "DELETE FROM " + trainRecordTable + " WHERE date('now','-30 day') >= date(training_createTime)";
        db.execSQL(exec2);

        //清理教练员登录信息表
        String coachLoginInfoTable = CreateDateBase.TABLE_INSTRUCTOR_LOGIN_INFO;
        String exec3 = "DELETE FROM " + coachLoginInfoTable + " WHERE date('now','-30 day') >= date(instructor_createTime)";
        db.execSQL(exec3);

        //清理学员登录信息表
        String studentLoginInfoTable = CreateDateBase.TABLE_LEARNER_LOGIN_INFO;
        String exec4 = "DELETE FROM " + studentLoginInfoTable + " WHERE date('now','-30 day') >= date(learner_createTime)";
        db.execSQL(exec4);

        //清理照片信息表
        String imgInfoTable = CreateDateBase.TABLE_IMG;
        String exec5 = "DELETE FROM " + imgInfoTable + " WHERE date('now','-30 day') >= date(photo_createTime)";
        db.execSQL(exec5);
    }
}
