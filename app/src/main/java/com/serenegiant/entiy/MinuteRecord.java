package com.serenegiant.entiy;

import com.serenegiant.rfid.CardInfo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class MinuteRecord {
    @Id
    private long classId;

    //通用信息
    private byte courseCode;
    private long startTime;
    private long lastRecordTime;
    private int learnedTime;
    private long learnedMiles;
    private String company;

    //学员信息
    private byte[] studentNumber;
    private byte[] studentIdentifyNumber;
    private String stuName;
    private String studentCarType;
    private int practiceTime;
    private int subjectTwoLearnedTime;
    private int subjectTwoLearnedMiles;
    private int subjectThreeLearnedTime;
    private int subjectThreeLearnedMiles;
    private int subjectOneLearnedTime;
    private int subjectFourLearnedTime;
    private int checkInTimes;
    private byte[] virtualCurrency;

    //教练信息
    private byte[] coachIdentifyNumber;
    private byte[] coachNumber;
    private String coachName;
    private String coachCarType;


    @Generated(hash = 1440125614)
    public MinuteRecord(long classId, byte courseCode, long startTime,
            long lastRecordTime, int learnedTime, long learnedMiles, String company,
            byte[] studentNumber, byte[] studentIdentifyNumber, String stuName,
            String studentCarType, int practiceTime, int subjectTwoLearnedTime,
            int subjectTwoLearnedMiles, int subjectThreeLearnedTime,
            int subjectThreeLearnedMiles, int subjectOneLearnedTime,
            int subjectFourLearnedTime, int checkInTimes, byte[] virtualCurrency,
            byte[] coachIdentifyNumber, byte[] coachNumber, String coachName,
            String coachCarType) {
        this.classId = classId;
        this.courseCode = courseCode;
        this.startTime = startTime;
        this.lastRecordTime = lastRecordTime;
        this.learnedTime = learnedTime;
        this.learnedMiles = learnedMiles;
        this.company = company;
        this.studentNumber = studentNumber;
        this.studentIdentifyNumber = studentIdentifyNumber;
        this.stuName = stuName;
        this.studentCarType = studentCarType;
        this.practiceTime = practiceTime;
        this.subjectTwoLearnedTime = subjectTwoLearnedTime;
        this.subjectTwoLearnedMiles = subjectTwoLearnedMiles;
        this.subjectThreeLearnedTime = subjectThreeLearnedTime;
        this.subjectThreeLearnedMiles = subjectThreeLearnedMiles;
        this.subjectOneLearnedTime = subjectOneLearnedTime;
        this.subjectFourLearnedTime = subjectFourLearnedTime;
        this.checkInTimes = checkInTimes;
        this.virtualCurrency = virtualCurrency;
        this.coachIdentifyNumber = coachIdentifyNumber;
        this.coachNumber = coachNumber;
        this.coachName = coachName;
        this.coachCarType = coachCarType;
    }

    @Generated(hash = 1303482556)
    public MinuteRecord() {
    }


    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public long getClassId() {
        return this.classId;
    }

    public void setClassId(long classId) {
        this.classId = classId;
    }

    public byte getCourseCode() {
        return this.courseCode;
    }

    public void setCourseCode(byte courseCode) {
        this.courseCode = courseCode;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getLastRecordTime() {
        return this.lastRecordTime;
    }

    public void setLastRecordTime(long lastRecordTime) {
        this.lastRecordTime = lastRecordTime;
    }

    public int getLearnedTime() {
        return this.learnedTime;
    }

    public void setLearnedTime(int learnedTime) {
        this.learnedTime = learnedTime;
    }

    public long getLearnedMiles() {
        return this.learnedMiles;
    }

    public void setLearnedMiles(long learnedMiles) {
        this.learnedMiles = learnedMiles;
    }

    public byte[] getStudentNumber() {
        return this.studentNumber;
    }

    public void setStudentNumber(byte[] studentNumber) {
        this.studentNumber = studentNumber;
    }

    public byte[] getStudentIdentifyNumber() {
        return this.studentIdentifyNumber;
    }

    public void setStudentIdentifyNumber(byte[] studentIdentifyNumber) {
        this.studentIdentifyNumber = studentIdentifyNumber;
    }

    public String getStuName() {
        return this.stuName;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
    }

    public String getStudentCarType() {
        return this.studentCarType;
    }

    public void setStudentCarType(String studentCarType) {
        this.studentCarType = studentCarType;
    }

    public int getPracticeTime() {
        return this.practiceTime;
    }

    public void setPracticeTime(int practiceTime) {
        this.practiceTime = practiceTime;
    }

    public int getSubjectTwoLearnedTime() {
        return this.subjectTwoLearnedTime;
    }

    public void setSubjectTwoLearnedTime(int subjectTwoLearnedTime) {
        this.subjectTwoLearnedTime = subjectTwoLearnedTime;
    }

    public int getSubjectTwoLearnedMiles() {
        return this.subjectTwoLearnedMiles;
    }

    public void setSubjectTwoLearnedMiles(int subjectTwoLearnedMiles) {
        this.subjectTwoLearnedMiles = subjectTwoLearnedMiles;
    }

    public int getSubjectThreeLearnedTime() {
        return this.subjectThreeLearnedTime;
    }

    public void setSubjectThreeLearnedTime(int subjectThreeLearnedTime) {
        this.subjectThreeLearnedTime = subjectThreeLearnedTime;
    }

    public int getSubjectThreeLearnedMiles() {
        return this.subjectThreeLearnedMiles;
    }

    public void setSubjectThreeLearnedMiles(int subjectThreeLearnedMiles) {
        this.subjectThreeLearnedMiles = subjectThreeLearnedMiles;
    }

    public int getSubjectOneLearnedTime() {
        return this.subjectOneLearnedTime;
    }

    public void setSubjectOneLearnedTime(int subjectOneLearnedTime) {
        this.subjectOneLearnedTime = subjectOneLearnedTime;
    }

    public int getSubjectFourLearnedTime() {
        return this.subjectFourLearnedTime;
    }

    public void setSubjectFourLearnedTime(int subjectFourLearnedTime) {
        this.subjectFourLearnedTime = subjectFourLearnedTime;
    }

    public int getCheckInTimes() {
        return this.checkInTimes;
    }

    public void setCheckInTimes(int checkInTimes) {
        this.checkInTimes = checkInTimes;
    }

    public byte[] getVirtualCurrency() {
        return this.virtualCurrency;
    }

    public void setVirtualCurrency(byte[] virtualCurrency) {
        this.virtualCurrency = virtualCurrency;
    }

    public byte[] getCoachIdentifyNumber() {
        return this.coachIdentifyNumber;
    }

    public void setCoachIdentifyNumber(byte[] coachIdentifyNumber) {
        this.coachIdentifyNumber = coachIdentifyNumber;
    }

    public byte[] getCoachNumber() {
        return this.coachNumber;
    }

    public void setCoachNumber(byte[] coachNumber) {
        this.coachNumber = coachNumber;
    }

    public String getCoachName() {
        return this.coachName;
    }

    public void setCoachName(String coachName) {
        this.coachName = coachName;
    }

    public String getCoachCarType() {
        return this.coachCarType;
    }

    public void setCoachCarType(String coachCarType) {
        this.coachCarType = coachCarType;
    }

    public CardInfo generateStudentCardInfo(){
        CardInfo studentCardInfo = new CardInfo();
        studentCardInfo.setId(new String(studentNumber));
        studentCardInfo.setName(stuName);
        studentCardInfo.setCheckInTimes(checkInTimes);
        studentCardInfo.setIdentification(new String(studentIdentifyNumber));
        studentCardInfo.setCarType(CardInfo.antiEnumCarType(studentCarType));
        studentCardInfo.setSubjectOneLearnedTime(subjectOneLearnedTime);
        studentCardInfo.setSubjectFourLearnedTime(subjectFourLearnedTime);
        studentCardInfo.setSubjectTwoLearnedTime(subjectTwoLearnedTime);
        studentCardInfo.setSubjectTwoLearnedMiles(subjectTwoLearnedMiles);
        studentCardInfo.setSubjectThreeLearnedTime(subjectThreeLearnedTime);
        studentCardInfo.setSubjectThreeLearnedMiles(subjectThreeLearnedMiles);
        studentCardInfo.setPracticeTime(practiceTime);
        studentCardInfo.setVirtualCurrency(virtualCurrency);
        studentCardInfo.setCompany(company);

        return studentCardInfo;
    }

    public CardInfo generateCoachCardInfo(){
        CardInfo coachCardInfo = new CardInfo();
        coachCardInfo.setId(new String(coachNumber));
        coachCardInfo.setIdentification(new String(coachIdentifyNumber));
        coachCardInfo.setName(coachName);
        coachCardInfo.setCarType(CardInfo.antiEnumCarType(coachCarType));
        coachCardInfo.setCompany(company);

        return coachCardInfo;
    }
}
