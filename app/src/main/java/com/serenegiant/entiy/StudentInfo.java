package com.serenegiant.entiy;

/**
 * Created by Administrator on 2017-04-20.
 */

public class StudentInfo {
    String StuCardUid = "EBB03359";
    String DrivingNumber = "6218629213252614";
    String StuName = "王龙";
    String StuNumber = "7590333815255325";
    String StuCardNumber = "EBB03359";
    String StuIdentifyNumber = "130682198909202430";
    String NewStuIdentifyNumber ="20EC343AF0289C8A";

    public StudentInfo() {
    }

    public String getStuCardUid() {
        return StuCardUid;
    }

    public void setStuCardUid(String stuCardUid) {
        StuCardUid = stuCardUid;
    }

    public String getDrivingNumber() {
        return DrivingNumber;
    }

    public void setDrivingNumber(String drivingNumber) {
        DrivingNumber = drivingNumber;
    }

    public String getStuName() {
        return StuName;
    }

    public void setStuName(String stuName) {
        StuName = stuName;
    }

    public String getStuNumber() {
        return StuNumber;
    }

    public void setStuNumber(String stuNumber) {
        StuNumber = stuNumber;
    }

    public String getStuCardNumber() {
        return StuCardNumber;
    }

    public void setStuCardNumber(String stuCardNumber) {
        StuCardNumber = stuCardNumber;
    }

    public String getStuIdentifyNumber() {
        return StuIdentifyNumber;
    }

    public void setStuIdentifyNumber(String stuIdentifyNumber) {
        StuIdentifyNumber = stuIdentifyNumber;
    }

    public String getNewStuIdentifyNumber() {
        return NewStuIdentifyNumber;
    }

    public void setNewStuIdentifyNumber(String newStuIdentifyNumber) {
        NewStuIdentifyNumber = newStuIdentifyNumber;
    }

    public StudentInfo(String stuCardUid, String drivingNumber, String stuName, String stuNumber, String stuCardNumber, String stuIdentifyNumber, String newStuIdentifyNumber) {
        StuCardUid = stuCardUid;
        DrivingNumber = drivingNumber;
        StuName = stuName;
        StuNumber = stuNumber;
        StuCardNumber = stuCardNumber;
        StuIdentifyNumber = stuIdentifyNumber;
        NewStuIdentifyNumber = newStuIdentifyNumber;
    }
}
