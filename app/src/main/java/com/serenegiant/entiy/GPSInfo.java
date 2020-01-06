package com.serenegiant.entiy;

/**
 * Created by Administrator on 2016-06-19.
 */
public class GPSInfo {

    private static double longitude=0;
    private static double latitude=0;
    private static double speed=0;
    private static int viewCount=0;
    private static int useCount=0;
    private static double altitude=0;
    private static int angle=0;
    private static double maxSpeed=0;
    private static String GPSTime;

    public static double getMaxSpeed() {
        return maxSpeed;
    }

    public static void setMaxSpeed(double maxSpeed) {
        if (GPSInfo.maxSpeed<maxSpeed&&maxSpeed!=99999){
        GPSInfo.maxSpeed = maxSpeed;}else
        {
            GPSInfo.maxSpeed=0;
        }
    }

    public static double getAltitude() {
        return altitude;
    }

    public static void setAltitude(double altitude) {
        GPSInfo.altitude = altitude;
    }

    public static double getLatitude() {
        return latitude;
    }
    public static void setLatitude(double latitude) {
        GPSInfo.latitude = latitude;
    }

    public static double getLongitude() {

        return longitude;
    }

    public static void setLongitude(double longitude) {
        GPSInfo.longitude = longitude;
    }

    public static double getSpeed() {

        return (speed);
    }

    public static void setSpeed(double speed) {
        GPSInfo.speed = speed;
    }

    public static int getUseCount() {
        return useCount;
    }

    public static boolean isValid() {
         if (useCount > 3)
         {
             return true;
         }
        else
         {
             return false;
         }
    }

    public static void setUseCount(int useCount) {
        GPSInfo.useCount = useCount;
    }

    public static int getViewCount() {
        return viewCount;
    }

    public static void setViewCount(int viewCount) {
        GPSInfo.viewCount = viewCount;
    }

    public static int getAngle() {
        return angle;
    }

    public static void setAngle(int angle) {
        GPSInfo.angle = angle;
    }


    public static String getGPSTime() {
        return GPSTime;
    }

    public static void setGPSTime(String GPSTime) {
        GPSInfo.GPSTime = GPSTime;
    }
}
