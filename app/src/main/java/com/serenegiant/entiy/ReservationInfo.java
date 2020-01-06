package com.serenegiant.entiy;

import java.util.Date;

/**
 * Created by Administrator on 2016/6/6 0006.
 */
//预约信息
public class ReservationInfo {
    String reservation_id;//预约订单编号
    int reservation_course;//培训科目
    Date reservation_startTime;//预约开始时间
    Date reservation_endTime;//预约结束时间
    String reservation_area_number;//培训区域编号
    String reservation_instructor_number;//教练员编号
    String reservation_coach_name;//教练员姓名
    String reservation_learner_number;//学员编号
    String reservation_student_name;//学员姓名
    Date reservation_createTime;//创建时间
    int reservation_status; //0 表示未处理  1表示已经处理
    int  reservation_order_time; //订单培训时间

    public int getReservation_learned_miles() {
        return reservation_learned_miles;
    }

    public void setReservation_learned_miles(int reservation_learned_miles) {
        this.reservation_learned_miles = reservation_learned_miles;
    }

    int reservation_learned_miles;

    public int getReservation_order_time() {
        return reservation_order_time;
    }

    public void setReservation_order_time(int reservation_order_time) {
        this.reservation_order_time = reservation_order_time;
    }

    public String getReservation_coach_name() {
        return reservation_coach_name;
    }

    public void setReservation_coach_name(String reservation_coach_name) {
        this.reservation_coach_name = reservation_coach_name;
    }

    public String getReservation_student_name() {
        return reservation_student_name;
    }

    public void setReservation_student_name(String reservation_student_name) {
        this.reservation_student_name = reservation_student_name;
    }
    public String getReservation_id() {
        return reservation_id;
    }

    public void setReservation_id(String reservation_id) {
        this.reservation_id = reservation_id;
    }

    public int getReservation_course() {
        return reservation_course;
    }

    public void setReservation_course(int reservation_course) {
        this.reservation_course = reservation_course;
    }

    public Date getReservation_startTime() {
        return reservation_startTime;
    }

    public void setReservation_startTime(Date reservation_startTime) {
        this.reservation_startTime = reservation_startTime;
    }

    public Date getReservation_endTime() {
        return reservation_endTime;
    }

    public void setReservation_endTime(Date reservation_endTime) {
        this.reservation_endTime = reservation_endTime;
    }

    public String getReservation_area_number() {
        return reservation_area_number;
    }

    public void setReservation_area_number(String reservation_area_number) {
        this.reservation_area_number = reservation_area_number;
    }

    public String getReservation_instructor_number() {
        return reservation_instructor_number;
    }

    public void setReservation_instructor_number(String reservation_instructor_number) {
        this.reservation_instructor_number = reservation_instructor_number;
    }

    public String getReservation_learner_number() {
        return reservation_learner_number;
    }

    public void setReservation_learner_number(String reservation_learner_number) {
        this.reservation_learner_number = reservation_learner_number;
    }

    public Date getReservation_createTime() {
        return reservation_createTime;
    }

    public void setReservation_createTime(Date reservation_createTime) {
        this.reservation_createTime = reservation_createTime;
    }

    public int getReservation_status() {
        return reservation_status;
    }

    public void setReservation_status(int reservation_status) {
        this.reservation_status = reservation_status;
    }
}
