package com.serenegiant.dataFormat;

import java.util.Date;
/**
 * Created by Administrator on 2016/6/26 0026.
 */
public class DrivingRecordInfo {

    String driving_student_number;

    public String getDriving_coach_number() {
        return driving_coach_number;
    }

    public void setDriving_coach_number(String driving_coach_number) {
        this.driving_coach_number = driving_coach_number;
    }

    public String getDriving_student_number() {
        return driving_student_number;
    }

    public void setDriving_student_number(String driving_student_number) {
        this.driving_student_number = driving_student_number;
    }

    String driving_coach_number;
    String driving_training_id;
    String driving_data;
    int driving_uploadStatus;
    Date driving_uploadTime;

    public String getDriving_training_id() {
        return driving_training_id;
    }

    public void setDriving_training_id(String driving_training_id) {
        this.driving_training_id = driving_training_id;
    }

    public String getDriving_data() {
        return driving_data;
    }

    public void setDriving_data(String driving_data) {
        this.driving_data = driving_data;
    }

    public int getDriving_uploadStatus() {
        return driving_uploadStatus;
    }

    public void setDriving_uploadStatus(int driving_uploadStatus) {
        this.driving_uploadStatus = driving_uploadStatus;
    }

    public Date getDriving_uploadTime() {
        return driving_uploadTime;
    }

    public void setDriving_uploadTime(Date driving_uploadTime) {
        this.driving_uploadTime = driving_uploadTime;
    }
}
