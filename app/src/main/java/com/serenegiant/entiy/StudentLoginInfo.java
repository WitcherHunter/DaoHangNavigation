package com.serenegiant.entiy;

import java.util.Date;

/**
 * Created by Administrator on 2016-11-22.
 */
public class StudentLoginInfo {
    public String getStuNumber() {
        return stuNumber;
    }

    public void setStuNumber(String stuNumber) {
        this.stuNumber = stuNumber;
    }

    public String getCoachNumber() {
        return coachNumber;
    }

    public void setCoachNumber(String coachNumber) {
        this.coachNumber = coachNumber;
    }

    public byte[] getCourse() {
        return course;
    }

    public void setCourse(byte[] course) {
        this.course = course;
    }

    public long getClassID() {
        return classID;
    }

    public void setClassID(long classID) {
        this.classID = classID;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getMiles() {
        return miles;
    }

    public void setMiles(int miles) {
        this.miles = miles;
    }

    String stuNumber;  //学员编号
    String coachNumber;//教练员编号
    byte[] course;//培训课程	BCD[5]
    long classID; //课堂ID	DWORD	标识学员的一次培训过程，计时终端自行使用

    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    public Date getLogoutDate() {
        return logoutDate;
    }

    public void setLogoutDate(Date logoutDate) {
        this.logoutDate = logoutDate;
    }

    Date loginDate; //签到时间
    Date logoutDate; //签退时间
    int minutes;//培训时间
    int miles; //培训里程

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    int type;

    byte[] gpsDate; //GNSS数据

    public int getIsBlindArea() {
        return isBlindArea;
    }

    public void setIsBlindArea(int isBlindArea) {
        this.isBlindArea = isBlindArea;
    }

    public byte[] getGpsDate() {
        return gpsDate;
    }

    public void setGpsDate(byte[] gpsDate) {
        this.gpsDate = gpsDate;
    }

    int isBlindArea; //是否盲区

}
