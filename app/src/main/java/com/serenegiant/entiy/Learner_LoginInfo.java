package com.serenegiant.entiy;


import java.util.Date;

/**
 * Created by Administrator on 2016/6/19 0019.
 */
public class Learner_LoginInfo {


    String learner_loginID; //学员登录编号
    String learner_number;//学员编号
    String learner_instructorNumber; //当前教练员编码
    String learner_instructorLoginID;  //当前教练员登录ID
    String learner_loginData;//登录数据
    int learner_loginType; //1:表示登录  ，2表示登出
    int learner_uploadStatus; //状态：0表示未上传      1:表示已上传
    Date learner_uploadTime;//上传时间

    public String getLearner_loginID() {
        return learner_loginID;
    }

    public void setLearner_loginID(String learner_loginID) {
        this.learner_loginID = learner_loginID;
    }

    public String getLearner_number() {
        return learner_number;
    }

    public void setLearner_number(String learner_number) {
        this.learner_number = learner_number;
    }

    public String getLearner_instructorNumber() {
        return learner_instructorNumber;
    }

    public void setLearner_instructorNumber(String learner_instructorNumber) {
        this.learner_instructorNumber = learner_instructorNumber;
    }

    public String getLearner_instructorLoginID() {
        return learner_instructorLoginID;
    }

    public void setLearner_instructorLoginID(String learner_instructorLogID) {
        this.learner_instructorLoginID = learner_instructorLogID;
    }

    public String getLearner_loginData() {
        return learner_loginData;
    }

    public void setLearner_loginData(String learner_logData) {
        this.learner_loginData = learner_logData;
    }

    public int getLearner_loginType() {
        return learner_loginType;
    }

    public void setLearner_loginType(int learner_loginType) {
        this.learner_loginType = learner_loginType;
    }

    public int getLearner_uploadStatus() {
        return learner_uploadStatus;
    }

    public void setLearner_uploadStatus(int learner_uploadStatus) {
        this.learner_uploadStatus = learner_uploadStatus;
    }

    public Date getLearner_uploadTime() {
        return learner_uploadTime;
    }

    public void setLearner_uploadTime(Date learner_uploadTime) {
        this.learner_uploadTime = learner_uploadTime;
    }
}
