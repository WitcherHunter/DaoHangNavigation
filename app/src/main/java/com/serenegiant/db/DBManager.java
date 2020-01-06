package com.serenegiant.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 将raw中得数据库文件写入到data数据库中
 * Created by Administrator on 2016/6/6 0006.
 */
//@SuppressLint("NewApi")
public class DBManager {


    private static   DBManager dbManager=null;

    public static  DBManager getInstance()
    {
        if(dbManager==null) {
            dbManager = new DBManager();
        }
        return  dbManager;
    }

    // public




    private  long insert(SQLiteDatabase db,String table, ContentValues values) {
        try{
            if (db == null) {
                return -1;
            }
            return db.insert(table, null, values);
        }catch (Exception ex)
        {
            return -1;
        }
    }

    private  Cursor query(SQLiteDatabase db,String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        if (db == null) {
            return null;
        }
        try {
            return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        }catch (Exception ex)
        {
            return null;
        }
    }

    /*
  table:表名
   ContentValues：更新的值
   whereClause：where条件
   whereArgs：where条件吃参数值
*/
    private  int update(SQLiteDatabase db,String table, ContentValues values, String whereClause, String[] whereArgs) {
        if (db == null) {
            return -1;
        }
        try {
            return db.update(table, values, whereClause, whereArgs);
        }catch (Exception ex)
        {
            return -1;
        }

    }

}
