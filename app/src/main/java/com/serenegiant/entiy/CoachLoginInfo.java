package com.serenegiant.entiy;

/**
 * Created by Administrator on 2016-11-21.
 */
public class CoachLoginInfo {
    private String number;

    public String getIdentifyNumber() {
        return identifyNumber;
    }

    public void setIdentifyNumber(String identifyNumber) {
        this.identifyNumber = identifyNumber;
    }

    public String getTeachCarType() {
        return teachCarType;
    }

    public void setTeachCarType(String teachCarType) {
        this.teachCarType = teachCarType;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    private String identifyNumber;
    private  String teachCarType;

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
