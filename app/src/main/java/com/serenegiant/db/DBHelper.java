package com.serenegiant.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * Created by Administrator on 2016-06-06.
 */
public class DBHelper {
    private static DBHelper sDBHelper = null;
    private SqliteHelper mSqliteHelper = null;

    private DBHelper() {
    }

    public synchronized static DBHelper instance() {
        if (sDBHelper == null) {
            sDBHelper = new DBHelper();
        }

        return sDBHelper;
    }

    public synchronized void open(Context context, String dbName) {
        close();
        mSqliteHelper = new SqliteHelper(context, dbName,  1);
    }

    public synchronized void insert(String sql) {
        if (mSqliteHelper == null) {
            return;
        }
        mSqliteHelper.getWritableDatabase().execSQL(sql);
    }

    public synchronized long insert(String table, ContentValues values) {
        if (mSqliteHelper == null) {
            return -1;
        }
        return mSqliteHelper.getWritableDatabase().insert(table, null, values);
    }

    public synchronized int update(String table, ContentValues values,
                                   String whereClause, String[] whereArgs) {
        if (mSqliteHelper == null) {
            return -1;
        }
        return mSqliteHelper.getWritableDatabase().update(table, values,
                whereClause, whereArgs);
    }

    public synchronized Cursor query(String table, String[] columns,
                                     String selection, String[] selectionArgs, String groupBy,
                                     String having, String orderBy) {
        if (mSqliteHelper == null) {
            return null;
        }
        return mSqliteHelper.getReadableDatabase().query(table, columns,
                selection, selectionArgs, groupBy, having, orderBy);
    }

    public synchronized Cursor query(String sql) {
        if (mSqliteHelper == null) {
            return null;
        }
        return mSqliteHelper.getReadableDatabase().rawQuery(sql, null);
    }

    public synchronized int delete(String table, String whereClause,
                                   String[] whereArgs) {
        if (mSqliteHelper == null) {
            return -1;
        }
        return mSqliteHelper.getReadableDatabase().delete(table, whereClause,
                whereArgs);
    }

    public synchronized void close() {
        if (mSqliteHelper != null) {
            mSqliteHelper.close();
            mSqliteHelper = null;
        }
    }

    /**
     * 用于初始化数据库
     *
     * @author Administrator
     *
     */
    public static class SqliteHelper extends SQLiteOpenHelper {
        // 定义数据库文件
        private static final String DB_NAME = "DB_NAME";
        // 定义数据库版本
        private static final int DB_VERSION = 1;

        public SqliteHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        public SqliteHelper(Context context, String dbName) {
            // CursorFactory设置为null,使用默认值
            super(context, dbName, null, DB_VERSION);
        }

        public SqliteHelper(Context context, String dbName,int version) {
            // CursorFactory设置为null,使用默认值
            super(context, dbName, null, version);
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        /**
         * 更新版本时更新表
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

    }
}
