package com.serenegiant.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.serenegiant.AppConfig;

import java.io.File;


/**
 * Created by Administrator on 2016/6/1 0001.
 */
public class SQLiteHelper {

    private final int BUFFER_SIZE = 400000;
    public static SQLiteDatabase database;

    private static   SQLiteHelper sqLiteHelper=null;

    public static  SQLiteHelper getInstance()
    {
         if(sqLiteHelper==null) {
             sqLiteHelper = new SQLiteHelper();
         }
        return  sqLiteHelper;
    }
    /**
     * 打开数据库
     *
     * param dbFile
     * @return SQLiteDatabase
     * @author
     */
    public SQLiteDatabase openDateBase() {
        String databasePath = AppConfig.TRAIN_DATA_SAVE_DIRECTORY;
        try {
            File f=new File(databasePath);
            if(!f.exists()) {
                if(!f.mkdirs())//注意是mkdirs()有个s 这样可以创建多重目录。
                {
                    Log.e("openDateBase", "database 创建失败！");
                    return null;
                }
            }
            database = SQLiteDatabase.openOrCreateDatabase(databasePath+File.separator+AppConfig.TRAIN_LIST_FILE_NAME, null);
        } catch (Exception e) {
            Log.e("openDateBase", e.getMessage());
        }
        if(database!=null) {
//              database.execSQL("DROP TABLE "+CreateDateBase.TABLE_IMG);
            if (!checkColumnExists(database, CreateDateBase.TABLE_REAERVATION, "reservation_learned_miles"))
            {
                if (CreateDateBase.tabIsExist(CreateDateBase.TABLE_REAERVATION, database))
                {
                    database.execSQL("DROP TABLE "+CreateDateBase.TABLE_REAERVATION);
                }
            }
//              database.execSQL("DROP TABLE "+CreateDateBase.TABLE_REAERVATION);
//             database.execSQL("DROP TABLE "+CreateDateBase.TABLE_TRAINING_RECORD);
//             database.execSQL("DROP TABLE "+CreateDateBase.TABLE_CHECK_RECORD);
//               database.execSQL("DROP TABLE "+CreateDateBase.TABLE_INSTRUCTOR_LOGIN_INFO);
//              database.execSQL("DROP TABLE " + CreateDateBase.TABLE_LEARNER_LOGIN_INFO);
//            database.execSQL("DROP TABLE " + CreateDateBase.TABLE_DRIVINGRECORD);
//            database.execSQL("DROP TABLE " + CreateDateBase.TABLE_ELECTRONIC_FENCE);
            CreateDateBase.create_instructorLoginInfo(database);
            CreateDateBase.create_learner_LoginInfo(database);
            CreateDateBase.create_reservationInfo(database);
            CreateDateBase.create_trainingRecord(database);
            CreateDateBase.create_checkRecord(database);
            CreateDateBase.create_imgTable(database);
            CreateDateBase.create_videoTable(database);
            CreateDateBase.create_drivingRecord(database);
            CreateDateBase.create_electronicFence(database);
            CreateDateBase.create_PositionRecord(database);
            CreateDateBase.create_TABLE_SEND_ERROR(database);
        }
        return database;
    }

    private boolean checkColumnExists(SQLiteDatabase db, String tableName, String columnName) {
        boolean result = false ;
        Cursor cursor = null ;

        try{
            cursor = db.rawQuery( "select * from sqlite_master where name = ? and sql like ?"
                    , new String[]{tableName , "%" + columnName + "%"} );
            result = null != cursor && cursor.moveToFirst() ;
        }catch (Exception e){
            e.printStackTrace();
        }finally{
            if(null != cursor && !cursor.isClosed()){
                cursor.close() ;
            }
        }
        return result ;
    }
    public void closeDatabase() {
        if (database != null && database.isOpen()) {
            this.database.close();
        }
    }
    public SQLiteDatabase getSQLiteDatabase()
    {
        return database;
    }

}
