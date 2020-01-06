package com.serenegiant.db;

import java.util.Date;

/**
 * Created by Hambobo on 2016-07-05.
 */
public class OrdersInfo {
    public String id;//预约订单编号
    public boolean hasContent;
    public byte item;//培训科目
    public Date startTime;//预约开始时间
    public Date endTime;//预约结束时间
    public String areaNumber;//培训区域编号
    public String coachNumber;//教练员编号
    public String coachName;//教练员姓名
    public String stuNumber;//学员编号
    public String stuName;//学员姓名
    public byte isTrain; //0 表示未处理  1表示已经处理
    public int orderTime; //订单培训时长

//    public static String TAG = "OrdersInfo";
//    //插入预约列表
//
//
//    //获取预约信息
//    static OrdersInfo getOrderContent(String orderID)
//    {
//
//    }
//
//    static
}
