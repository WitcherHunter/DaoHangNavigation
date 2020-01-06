package com.serenegiant.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.serenegiant.dataFormat.DrivingRecordInfo;
import com.serenegiant.entiy.CoachLoginInfo;
import com.serenegiant.entiy.PhotoHeadInformation;
import com.serenegiant.entiy.ReservationInfo;
import com.serenegiant.entiy.StudentLoginInfo;
import com.serenegiant.entiy.TrainingRecord;
import com.serenegiant.entiy.VideoHeadInformation;
import com.serenegiant.utils.MessageDefine;
import com.serenegiant.utils.StringUtils;
import com.serenegiant.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Administrator on 2016/6/6 0006.
 */
public class Insert_DB {

    boolean Debug = true;
    String TAG = "Insert_DB";
    private static Insert_DB insert_DB = null;

    public static Insert_DB getInstance() {
        if (insert_DB == null) {
            insert_DB = new Insert_DB();
        }
        return insert_DB;
    }

    //插入预约信息
    public boolean insertError(String s1,String s2,String s3) {
        try {
            SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
            if (db == null)
                return false;

            ContentValues values = new ContentValues();
                values.put("instructor_message_num", "");
                values.put("instructor_message_mesid", "");
                values.put("instructor_message_data", s3);

            long result = insert(db, CreateDateBase.TABLE_SEND_ERROR, values);
            if (result >= 1) {
                Log.e(TAG, "发送失败数据保存成功");
                return true;
            } else {
                Log.e(TAG, "发送失败数据保存失败");
                return false;
            }
        } catch (Exception ex) {
            if (Debug) Log.i(TAG, "发送失败数据保存异常" + ex.getMessage());
            return false;
        }
    }
   //插入预约信息
    public boolean insertReservation(ReservationInfo info) {
        try {
            SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
            if (db == null)
                return false;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ContentValues values = new ContentValues();
            if (info.getReservation_id() != null)
                values.put("reservation_id", info.getReservation_id());
            //if(info.getReservation_course()!=null)
            values.put("reservation_course", info.getReservation_course());
            if (info.getReservation_startTime() != null)
                values.put("reservation_startTime", sdf.format(info.getReservation_startTime()));
            if (info.getReservation_endTime() != null)
                values.put("reservation_endTime", sdf.format(info.getReservation_endTime()));
            if (info.getReservation_area_number() != null)
                values.put("reservation_instructor_number", info.getReservation_area_number());
            if (info.getReservation_instructor_number() != null)
                values.put("reservation_instructor_number", info.getReservation_instructor_number());
            if (info.getReservation_learner_number() != null)
                values.put("reservation_learner_number", info.getReservation_learner_number());
            values.put("reservation_status", info.getReservation_status());
            values.put("reservation_order_time", info.getReservation_order_time());

            long result = insert(db, CreateDateBase.TABLE_REAERVATION, values);
            if (result >= 1) {
                if (Debug) Log.i(TAG, "插入预约数据成功");
                return true;
            } else {
                if (Debug) Log.i(TAG, "插入预约数据失败");
                return false;
            }
        } catch (Exception ex) {
            if (Debug) Log.i(TAG, "插入预约数据异常" + ex.getMessage());
            return false;
        }
    }

    // 插入学员登录信息
    public boolean insertLearner_Login(StudentLoginInfo info) {
        Log.e("插入学员登录信息",""+info.getStuNumber());
        try {
            SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
            if (db == null)
                return false;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ContentValues values = new ContentValues();
            if (info.getStuNumber() != null) {
                values.put("learner_number", info.getStuNumber());
            }
            if (info.getCoachNumber() != null) {
                values.put("learner_instructorNumber", info.getCoachNumber());
            }
            if (info.getCourse() != null) {
                values.put("learner_course", StringUtils.bytesToHexString(info.getCourse()));
            }
            values.put("learner_class_id", info.getClassID());
            values.put("learner_learned_time", info.getMinutes());
            values.put("learner_learned_miles", info.getMiles());
            if (info.getLoginDate() != null) {
                values.put("learner_login_date", sdf.format(info.getLoginDate()));
            }
            if (info.getLogoutDate() != null) {
                values.put("learner_logout_date", sdf.format(info.getLogoutDate()));
            }
            values.put("learner_loginType", info.getType());
            // if(info.getLearner_loginData()!=null)
            values.put("learner_uploadStatus", 0);
            if (info.getGpsDate() != null)
            {
                values.put("learner_gps_data", StringUtils.bytesToHexString(info.getGpsDate()));
            }
            values.put("learner_isBlindArea", MessageDefine.isBlindArea);
            long result = insert(db, CreateDateBase.TABLE_LEARNER_LOGIN_INFO, values);

            if (result >= 1) {
                if (Debug) Log.i(TAG, "插入学员登录数据成功");
                return true;
            } else {
                if (Debug) Log.i(TAG, "插入学员登录数据失败");
                return false;
            }
        } catch (Exception ex) {
            Log.e(TAG, "insertLearner_Login: " + ex.getMessage());
            if (Debug) Log.i(TAG, "插入预约数据异常" + ex.getMessage());
            return false;
        }
    }


    //插入教练员登录信息
    public boolean insertInstructorLogin(CoachLoginInfo info) {
        try {
            SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
            if (db == null)
                return false;
            ContentValues values = new ContentValues();
            values.put("instructor_number", info.getNumber());
            values.put("instructor_identify", info.getIdentifyNumber());
            values.put("instructor_teach_car", info.getTeachCarType());
            values.put("instructor_loginType", info.getType());
            values.put("instructor_uploadStatus", 0);
            if (info.getGpsDate() != null)
            {
                values.put("instructor_gps_data", StringUtils.bytesToHexString(info.getGpsDate()));
            }
            values.put("instructor_isBlindArea", MessageDefine.isBlindArea);
            long result = insert(db, CreateDateBase.TABLE_INSTRUCTOR_LOGIN_INFO, values);
            if (result >= 1) {
                if (Debug) Log.i(TAG, "插入教练登录数据成功");
                return true;
            } else {
                if (Debug) Log.i(TAG, "插入教练登录数据失败");
                return false;
            }
        } catch (Exception ex) {
            if (Debug) Log.i(TAG, "插入教练登录数据异常" + ex.getMessage());
            return false;
        }
    }


    //插入培训记录信息
    public boolean insertTrainingRecord(TrainingRecord info) {
        try {
            SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
            if (db == null) {
                return false;
            }

            ContentValues values = new ContentValues();
            if (info.getTraining_id() != null) {
                values.put("training_id", info.getTraining_id());//学时记录编号
            }
            if (info.getTraining_instructor_number() != null) {
                values.put("training_instructor_number", info.getTraining_instructor_number());//教练员编号
            }
            if (info.getTraining_learner_number() != null) {
                values.put("training_learner_number", info.getTraining_learner_number()); //学员编号
            }
            values.put("training_class_id", info.getTrainClassId());
            values.put("training_created_time", info.getTraining_end_time());
            values.put("training_course_name", info.getTrainCourseName());
            values.put("training_state", info.getState());
            values.put("training_max_speed", info.getMaxSpeed());
            values.put("training_miles", info.getMiles());

            if (info.getGpsDate() != null)
            {
                values.put("training_gps_data", StringUtils.bytesToHexString(info.getGpsDate()));
            }
            values.put("training_isBlindArea",MessageDefine.isBlindArea);
            values.put("training_uploadStatus", info.getTraining_uploadStatus());
            long result = insert(db, CreateDateBase.TABLE_TRAINING_RECORD, values);
            if (result >= 1) {
                if (Debug) Log.i(TAG, "插入培训记录数据成功");
                return true;
            } else {
                if (Debug) Log.i(TAG, "插入培训记录数据失败");
                return false;
            }
        } catch (Exception ex) {
            if (Debug) Log.e(TAG, "培训记录数据异常:" + ex.getMessage());
            return false;
        }
    }

    //插入点位记录信息
    public boolean insertPositionRecord(String info) {
            if(info == null){
                return true;
            }
        try {
            SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
            if (db == null)
                return false;
            ContentValues values = new ContentValues();
            values.put("position_data", info);
            values.put("position_uploadStatus", 0);
            long result = insert(db, CreateDateBase.TABLE_POSITION_RECORD, values);
            if (result >= 1) {
                if (Debug) Log.i(TAG, "插入点位信息数据成功");
                return true;
            } else {
                if (Debug) Log.i(TAG, "插入点位信息数据失败");
                return false;
            }
        } catch (Exception ex) {
            if (Debug) Log.e(TAG, "点位信息数据异常:" + ex.getMessage());
            return false;
        }
    }

    public boolean insertDrivingRecord(DrivingRecordInfo info) {
        try {
            SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
            if (db == null)
                return false;
            ContentValues values = new ContentValues();

            if (info.getDriving_student_number() != null) {
                values.put("driving_student_number", info.getDriving_student_number());
            }
            if (info.getDriving_coach_number() != null) {
                values.put("driving_coach_number", info.getDriving_coach_number());
            }
            if (info.getDriving_training_id() != null)
                values.put("driving_training_id", info.getDriving_training_id());//学时记录编号
            if (info.getDriving_data() != null)
                values.put("driving_data", info.getDriving_data());//教练员编号
            //  if (info.getDriving_uploadStatus() != null)
            values.put("driving_uploadStatus", info.getDriving_uploadStatus());   //教练员登录ID
            if (info.getDriving_uploadTime() != null)
                values.put("driving_uploadTime", info.getDriving_uploadTime().toString()); //上传时间

            long result = insert(db, CreateDateBase.TABLE_DRIVINGRECORD, values);
            if (result >= 1) {
                if (Debug) Log.i(TAG, "插入驾驶行为数据成功");
                return true;
            } else {
                if (Debug) Log.i(TAG, "插入驾驶行为数据失败");
                return false;
            }
        } catch (Exception ex) {
            if (Debug) Log.i(TAG, "插入驾驶行为数据异常" + ex.getMessage());
            return false;
        }
    }

    //插入图片数据
    public boolean insertImgInfo(PhotoHeadInformation info) {
        try {
            SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
            if (db == null)
                return false;
            ContentValues values = new ContentValues();
            values.put("photo_up_mode", info.getUpMode());
            values.put("photo_channel", info.getChannel());
            values.put("photo_size", info.getSize());
            values.put("photo_event_type", info.getEventType());
            values.put("photo_class_id", info.getClassID());
            values.put("photo_student_number", info.getStuNumber());
            values.put("photo_student_take_id", StringUtils.bytesToHexString(info.getTakeID()));
            values.put("photo_student_gps_info", StringUtils.bytesToHexString(info.getGpsInfo()));
            values.put("photo_path", info.getSavePath());
            values.put("photo_state", info.getState());
            values.put("photo_isBlindArea",MessageDefine.isBlindArea);
            long result = insert(db, CreateDateBase.TABLE_IMG, values);

            if (result >= 1) {
                if (Debug) Log.i(TAG, "插入图片检索数据成功");
                return true;
            } else {
                if (Debug) Log.i(TAG, "插入图片检索数据失败");
                return false;
            }
        } catch (Exception ex) {
            if (Debug) Log.i(TAG, "插入驾驶行为数据异常" + ex.getMessage());
            return false;
        }
    }

    //插入图片数据
    public boolean insertVideoInfo(VideoHeadInformation info) {
        try {
            SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
            if (db == null)
                return false;
            ContentValues values = new ContentValues();
            values.put("video_up_mode", info.getUpMode());
            values.put("video_channel", info.getChannel());
            values.put("video_size", info.getSize());
            values.put("video_event_type", info.getEventType());
            values.put("video_student_driving_no", info.getStuDrivingNo());
            values.put("video_student_number", info.getStuNumber());
            values.put("video_student_login_number", StringUtils.bytesToHexString(info.getStuLoginNumber()));
            values.put("video_student_take_id", StringUtils.bytesToHexString(info.getTakeID()));
            values.put("video_student_gps_info", StringUtils.bytesToHexString(info.getGpsInfo()));
            values.put("video_path", info.getSavePath());
            values.put("video_state", info.getState());
            long result = insert(db, CreateDateBase.TABLE_VIDEO_HEAD, values);
            if (result >= 1) {
                if (Debug) Log.i(TAG, "插入视频检索数据成功");
                return true;
            } else {
                if (Debug) Log.i(TAG, "插入视频检索数据失败");
                return false;
            }
        } catch (Exception ex) {
            if (Debug) Log.i(TAG, "插入视频检索数据异常" + ex.getMessage());
            return false;
        }
    }

    private synchronized long insert(SQLiteDatabase db, String table, ContentValues values) {
        try {
            if (db == null) {
                return -1;
            }
            return db.insert(table, null, values);
        } catch (Exception ex) {
           // if (Debug)
                Log.i(TAG, "插入数据异常" + ex.getMessage());
            Utils.saveRunningLog(ex.getMessage());
            return -1;
        }
    }

    public boolean insertOrder(OrdersInfo order) {
        try {
            SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
            if (db == null)
                return false;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ContentValues values = new ContentValues();
            values.put("reservation_id", order.id);
            values.put("reservation_course", order.item);
            values.put("reservation_startTime", sdf.format(order.startTime));
            values.put("reservation_endTime", sdf.format(order.endTime));
            values.put("reservation_area_number", order.areaNumber);
            values.put("reservation_instructor_number", order.coachNumber);
            values.put("reservation_coach_name", order.coachName);
            values.put("reservation_learner_number", order.stuNumber);
            values.put("reservation_student_name", order.stuName);
            values.put("reservation_status", order.isTrain);
            values.put("reservation_order_time", order.orderTime);
            long result = insert(db, CreateDateBase.TABLE_REAERVATION, values);
            if (result >= 1) {
                Log.i(TAG, "插入预约数据成功");
                return true;
            } else {
                Log.i(TAG, "插入预约数据失败");
                return false;
            }

        } catch (Exception ex) {
            if (Debug) Log.i(TAG, "插入数据异常" + ex.getMessage());
            return false;
        }
    }

    public boolean insertFence(String fenceID, int mode, int number, byte[] point, Date time) {
        try {
            SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
            if (db == null)
                return false;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ContentValues values = new ContentValues();
            values.put("fence_id", fenceID);
            values.put("fence_mode", mode);
            values.put("fence_point_number", number);
            values.put("fence_point_value", StringUtils.bytesToHexString(point));
            values.put("fence_update_time", sdf.format(time));
            long result = insert(db, CreateDateBase.TABLE_ELECTRONIC_FENCE, values);
            if (result >= 1) {
                Log.i(TAG, "插入电子围栏数据成功");
                return true;
            } else {
                Log.i(TAG, "插入电子围栏数据失败");
                return false;
            }

        } catch (Exception ex) {
            if (Debug) Log.i(TAG, "插入电子围栏数据异常" + ex.getMessage());
            return false;
        }
    }
}
