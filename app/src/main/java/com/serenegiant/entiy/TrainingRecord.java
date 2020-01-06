package com.serenegiant.entiy;

/**
 * Created by Administrator on 2016/6/7 0007.
 */
public class TrainingRecord {

    String training_id; //学时记录编号 26字节 设备编号+日期YYMMDD+ “0001~9999”
    String training_instructor_number; //教练员编号
    String training_learner_number;//学员编号
    String stuName;
    String coachName;
    String training_end_time;//培训结束时间
    int trainClassId;
    String trainCourseName;
//    int training_duration;//培训时长
//    Date training_uploadTime;//上传时间
    int maxSpeed;
    int miles; // 1/10 KM
    int state;
    int training_uploadStatus; //状态：0表示未上传      1:表示已上传
    byte[] gpsDate;
    int speed; //速度

    public int getIsBlindArea() {
        return isBlindArea;
    }

    public void setIsBlindArea(int isBlindArea) {
        this.isBlindArea = isBlindArea;
    }

    int isBlindArea; //是否盲区

    public String getTrainCourseName() {
        return trainCourseName;
    }

    public void setTrainCourseName(String trainCourseName) {
        this.trainCourseName = trainCourseName;
    }

    public int getTrainClassId() {
        return trainClassId;
    }

    public void setTrainClassId(int trainClassId) {
        this.trainClassId = trainClassId;
    }

    public byte[] getGpsDate() {
        return gpsDate;
    }

    public void setGpsDate(byte[] gpsDate) {
        this.gpsDate = gpsDate;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public int getMiles() {
        return miles;
    }

    public void setMiles(int miles) {
        this.miles = miles;
    }
    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getTraining_end_time() {
        return training_end_time;
    }

    public void setTraining_end_time(String training_end_time) {
        this.training_end_time = training_end_time;
    }

//    public int getTraining_duration() {
//        return training_duration;
//    }
//
//    public void setTraining_duration(int training_duration) {
//        this.training_duration = training_duration;
//    }

    public String getTraining_id() {
        return training_id;
    }

    public void setTraining_id(String training_id) {
        this.training_id = training_id;
    }

    public String getTraining_instructor_number() {
        return training_instructor_number;
    }

    public void setTraining_instructor_number(String training_instructor_number) {
        this.training_instructor_number = training_instructor_number;
    }


    public String getTraining_learner_number() {
        return training_learner_number;
    }

    public void setTraining_learner_number(String training_learner_number) {
        this.training_learner_number = training_learner_number;
    }

    public int getTraining_uploadStatus() {
        return training_uploadStatus;
    }

    public void setTraining_uploadStatus(int training_uploadStatus) {
        this.training_uploadStatus = training_uploadStatus;
    }

//    public Date getTraining_uploadTime() {
//        return training_uploadTime;
//    }
//
//    public void setTraining_uploadTime(Date training_uploadTime) {
//        this.training_uploadTime = training_uploadTime;
//    }

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

}
