package com.serenegiant.utils;

/**
 * Created by Hambobo on 2016-07-25.
 */
public class CountDistance {
    private static double EARTH_RADIUS = 6378.137;
    static boolean isStart = false;
    static double lastLat;
    static double lastLongt;
    static double lastArc;
    static double totalMile;
    static long  lastCalculateTime;

    //将角度转换为弧度
    static double deg2rad(double degree) {
        return degree / 180.0 * Math.PI;
    }
    //将弧度转换为角度
    static double rad2deg(double radian) {
        return radian * 180.0 / Math.PI;
    }


    private static double rad(double d)
    {
        return d * Math.PI / 180.0;
    }
    public static void startCount(double lat, double longt, double arc)
    {
        isStart = true;
        lastLat = lat;
        lastLongt = longt;
        lastArc = arc;
        totalMile = 0;
        lastCalculateTime = System.currentTimeMillis();
    }

//    lat1 纬度，0~359
//    arc1 角度 0~359
//    return  单位：米
    public static double GetDistance(double lat1, double lng1, double arc1, double lat2, double lng2, double arc2)
    {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double arc;
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2.0 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2d),2) +
                Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2d),2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000d) / 10000d; //直线距离计算完成
        s *= 1000d;
        arc = Math.abs(arc2-arc1);
        if (arc > 5)//转换弧度长度
        {
            arc = deg2rad(arc);//转换为弧度计算
            s= (s/2d/Math.sin(arc/2d))*arc;
        }
        return s;
    }
    public static void addDistance(double lat, double longt, double arc)
    {
        if (isStart) {
            double s = GetDistance(lastLat, lastLongt, lastArc, lat, longt, arc);
            double second = (System.currentTimeMillis()-lastCalculateTime)/1000d;
            if (s/second < 61)//若计算速度大于220Km/h，当成无效点处理
            {
                totalMile += s;
            }
            lastLat = lat;
            lastLongt = longt;
            lastArc = arc;
            lastCalculateTime = System.currentTimeMillis();
        }
        else
        {
            isStart = true;
            lastLat = lat;
            lastLongt = longt;
            lastArc = arc;
            totalMile = 0;
            lastCalculateTime = System.currentTimeMillis();
        }
    }

    public static double getTotalMile()
    {
        if (isStart) {
            return totalMile;
        }
        return 0;
    }
}
