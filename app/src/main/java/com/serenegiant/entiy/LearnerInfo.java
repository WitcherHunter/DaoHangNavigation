package com.serenegiant.entiy;

/**
 * 学员信息表
 * Created by Administrator on 2016/6/1 0001.
 */
public class LearnerInfo {



 //   String  learner_loginID;//学员登录编号
    String  learner_id; //学员编号
    String  learner_card; //学员卡号
    String  learner_identification;  //身份证
    String  learner_course; //科目
   // String  learner_instructorId;  //当前教练员编码
  //  String  learner_GPS;    //GPS
    String  learner_totalTime;   //总的学时
  //  String  learner_finishTime;   //已经完成的学时
    String  learner_name;   //学员姓名
    String  learner_phone;   //学员电话

/*
    public String getLearner_loginID() {
        return learner_loginID;
    }

    public void setLearner_loginID(String learner_loginID) {
        this.learner_loginID = learner_loginID;
    }
    public String getLearner_finishTime() {
        return learner_finishTime;
    }

    public void setLearner_finishTime(String learner_finishTime) {
        this.learner_finishTime = learner_finishTime;
    }
    public String getLearner_instructorId() {
        return learner_instructorId;
    }

    public void setLearner_instructorId(String learner_instructorId) {
        this.learner_instructorId = learner_instructorId;
    }

    public String getLearner_GPS() {
        return learner_GPS;
    }

    public void setLearner_GPS(String learner_GPS) {
        this.learner_GPS = learner_GPS;
    }
    */


    public String getLearner_id() {
        return learner_id;
    }

    public void setLearner_id(String learner_id) {
        this.learner_id = learner_id;
    }

    public String getLearner_card() {
        return learner_card;
    }

    public void setLearner_card(String learner_card) {
        this.learner_card = learner_card;
    }

    public String getLearner_identification() {
        return learner_identification;
    }

    public void setLearner_identification(String learner_identification) {
        this.learner_identification = learner_identification;
    }

    public String getLearner_course() {
        return learner_course;
    }

    public void setLearner_course(String learner_course) {
        this.learner_course = learner_course;
    }



    public String getLearner_totalTime() {
        return learner_totalTime;
    }

    public void setLearner_totalTime(String learner_totalTime) {
        this.learner_totalTime = learner_totalTime;
    }



    public String getLearner_name() {
        return learner_name;
    }

    public void setLearner_name(String learner_name) {
        this.learner_name = learner_name;
    }

    public String getLearner_phone() {
        return learner_phone;
    }

    public void setLearner_phone(String learner_phone) {
        this.learner_phone = learner_phone;
    }
}
