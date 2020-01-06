package com.serenegiant.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2016/6/6 0006.
 */
public class Delete_DB {

    private static   Delete_DB delete_db=null;

    public static  Delete_DB getInstance()
    {
        if(delete_db==null) {
            delete_db = new Delete_DB();
        }
        return  delete_db;
    }

    public  boolean clearOrder()//清空订单信息
    {
        String WhereClause;
        String whereArgs;
        int ret;
        WhereClause = "DATE(reservation_startTime) < DATE('now', 'localtime')";
        try {
            SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
            if (db == null)
                return false;
            ret = db.delete(CreateDateBase. TABLE_REAERVATION, WhereClause, null);
            if (ret > 0)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        catch (Exception ex) {
            return false;
        }
    }

    public  boolean clearFenceData()//清空区域信息
    {
        try {
            SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
            if (db == null)
                return false;
            db.delete(CreateDateBase.TABLE_ELECTRONIC_FENCE,null, null);
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }

    /*
       发送失败包删除
        */
    public void DeleteErrorMessage(String s1,String s2) {
        SQLiteDatabase db = SQLiteHelper.getInstance().getSQLiteDatabase();
        if (db == null)
            return ;
        String table = CreateDateBase.TABLE_SEND_ERROR;// CreateDateBase.TABLE_LEARNER;
        if(s1 == null || s2 == null){
            String selection = " id= ?";  //查询条件
            db.delete(table,selection,new String[]{"0"});
        }else{

            String selection = " instructor_message_num= ? and instructor_message_mesid= ?";  //查询条件
            db.delete(table,selection,new String[]{s1,s2});
        }
    }
}
