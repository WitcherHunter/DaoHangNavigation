package com.serenegiant.ui;

import java.util.Date;

/**
 * Created by Administrator on 2016-08-05.
 */
public class TrainListInfo {

    public String getRecordFenceID() {
        return recordFenceID;
    }

    public void setRecordFenceID(String recordFenceID) {
        this.recordFenceID = recordFenceID;
    }

    public String getRecordID() {
        return recordID;
    }

    public void setRecordID(String recordID) {
        this.recordID = recordID;
    }

    public String getRecordCoachID() {
        return recordCoachID;
    }

    public void setRecordCoachID(String recordCoachID) {
        this.recordCoachID = recordCoachID;
    }

    public String getRecordCoachLoginID() {
        return recordCoachLoginID;
    }

    public void setRecordCoachLoginID(String recordCoachLoginID) {
        this.recordCoachLoginID = recordCoachLoginID;
    }

    public String getRecordStudentID() {
        return recordStudentID;
    }

    public void setRecordStudentID(String recordStudentID) {
        this.recordStudentID = recordStudentID;
    }

    public String getRecordStudentLoginID() {
        return recordStudentLoginID;
    }

    public void setRecordStudentLoginID(String recordStudentLoginID) {
        this.recordStudentLoginID = recordStudentLoginID;
    }

    public Date getRecordStartTime() {
        return recordStartTime;
    }

    public void setRecordStartTime(Date recordStartTime) {
        this.recordStartTime = recordStartTime;
    }

    public Date getRecordEndTime() {
        return recordEndTime;
    }

    public void setRecordEndTime(Date recordEndTime) {
        this.recordEndTime = recordEndTime;
    }

    public int getRecordLearnTime() {
        return recordLearnTime;
    }

    public void setRecordLearnTime(int recordLearnTime) {
        this.recordLearnTime = recordLearnTime;
    }

    public String getStuName() {
        return stuName;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
    }

    public String getCoachName() {
        return coachName;
    }

    public void setCoachName(String coachName) {
        this.coachName = coachName;
    }


    String recordID;
    String recordCoachID;
    String recordCoachLoginID;
    String recordStudentID;
    String recordStudentLoginID;
    Date recordStartTime;
    Date recordEndTime;
    int recordLearnTime;
    String recordFenceID;
    String stuName;
    String coachName;
}
