//package com.serenegiant.entiy;
//
//import com.serenegiant.rfid.CardInfo;
//
//import org.greenrobot.greendao.annotation.Entity;
//import org.greenrobot.greendao.annotation.Id;
//import org.greenrobot.greendao.annotation.Generated;
//
//@Entity
//public class CardInfoRecord {
//
//    @Id
//    long infoId;
//
//    //学员/教练编号
//    String id = "";
//    //持卡人姓名
//    String name = "";
//    //单日训练时长，单位为分
//    int practiceTimePerDay = 0;
//
//    //证件号码
//    String identification = "";
//    //所属驾校
//    String company = "";
//    //卡有效期
//    String validityPeriod = "";
//    //培训车型
//    String carType = "";
//
//    //科目一总学时
//    int subjectOneTotalTime = 0;
//    //科目二总学时
//    int subjectTwoTotalTime = 0;
//    //科目三总学时
//    int subjectThreeTotalTime = 0;
//    //科目二总里程
//    int subjectTwoTotalMiles = 0;
//    //科目三总里程
//    int subjectThreeTotalMiles = 0;
//    //科目四总学时
//    int subjectFourTotalMiles = 0;
//
//    //卡片状态 0.未签到 1.已签到
//    byte cardState = 0;
//    //当日训练时长
//    int practiceTime = 0;
//    //签到次数
//    int checkInTimes = 0;
//    //科目一已学学时
//    int subjectOneLearnedTime = 0;
//    //科目二已学学时
//    int subjectTwoLearnedTime = 0;
//    //科目二培训里程
//    int subjectTwoLearnedMiles = 0;
//    //科目三已学学时
//    int subjectThreeLearnedTime = 0;
//    //科目三培训里程
//    int subjectThreeLearnedMiles = 0;
//    //科目四已学学时
//    int subjectFourLearnedTime = 0;
//    //虚拟币(教练无)
//    byte[] virtualCurrency = new byte[0];
//
//    String lastExitDate = "";
//
//    @Generated(hash = 2014721225)
//    public CardInfoRecord(long infoId, String id, String name,
//            int practiceTimePerDay, String identification, String company,
//            String validityPeriod, String carType, int subjectOneTotalTime,
//            int subjectTwoTotalTime, int subjectThreeTotalTime,
//            int subjectTwoTotalMiles, int subjectThreeTotalMiles,
//            int subjectFourTotalMiles, byte cardState, int practiceTime,
//            int checkInTimes, int subjectOneLearnedTime, int subjectTwoLearnedTime,
//            int subjectTwoLearnedMiles, int subjectThreeLearnedTime,
//            int subjectThreeLearnedMiles, int subjectFourLearnedTime,
//            byte[] virtualCurrency, String lastExitDate) {
//        this.infoId = infoId;
//        this.id = id;
//        this.name = name;
//        this.practiceTimePerDay = practiceTimePerDay;
//        this.identification = identification;
//        this.company = company;
//        this.validityPeriod = validityPeriod;
//        this.carType = carType;
//        this.subjectOneTotalTime = subjectOneTotalTime;
//        this.subjectTwoTotalTime = subjectTwoTotalTime;
//        this.subjectThreeTotalTime = subjectThreeTotalTime;
//        this.subjectTwoTotalMiles = subjectTwoTotalMiles;
//        this.subjectThreeTotalMiles = subjectThreeTotalMiles;
//        this.subjectFourTotalMiles = subjectFourTotalMiles;
//        this.cardState = cardState;
//        this.practiceTime = practiceTime;
//        this.checkInTimes = checkInTimes;
//        this.subjectOneLearnedTime = subjectOneLearnedTime;
//        this.subjectTwoLearnedTime = subjectTwoLearnedTime;
//        this.subjectTwoLearnedMiles = subjectTwoLearnedMiles;
//        this.subjectThreeLearnedTime = subjectThreeLearnedTime;
//        this.subjectThreeLearnedMiles = subjectThreeLearnedMiles;
//        this.subjectFourLearnedTime = subjectFourLearnedTime;
//        this.virtualCurrency = virtualCurrency;
//        this.lastExitDate = lastExitDate;
//    }
//
//    @Generated(hash = 456147712)
//    public CardInfoRecord() {
//    }
//
//    public String getId() {
//        return this.id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return this.name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public int getPracticeTimePerDay() {
//        return this.practiceTimePerDay;
//    }
//
//    public void setPracticeTimePerDay(int practiceTimePerDay) {
//        this.practiceTimePerDay = practiceTimePerDay;
//    }
//
//    public String getIdentification() {
//        return this.identification;
//    }
//
//    public void setIdentification(String identification) {
//        this.identification = identification;
//    }
//
//    public String getCompany() {
//        return this.company;
//    }
//
//    public void setCompany(String company) {
//        this.company = company;
//    }
//
//    public String getValidityPeriod() {
//        return this.validityPeriod;
//    }
//
//    public void setValidityPeriod(String validityPeriod) {
//        this.validityPeriod = validityPeriod;
//    }
//
//    public String getCarType() {
//        return this.carType;
//    }
//
//    public void setCarType(String carType) {
//        this.carType = carType;
//    }
//
//    public int getSubjectOneTotalTime() {
//        return this.subjectOneTotalTime;
//    }
//
//    public void setSubjectOneTotalTime(int subjectOneTotalTime) {
//        this.subjectOneTotalTime = subjectOneTotalTime;
//    }
//
//    public int getSubjectTwoTotalTime() {
//        return this.subjectTwoTotalTime;
//    }
//
//    public void setSubjectTwoTotalTime(int subjectTwoTotalTime) {
//        this.subjectTwoTotalTime = subjectTwoTotalTime;
//    }
//
//    public int getSubjectThreeTotalTime() {
//        return this.subjectThreeTotalTime;
//    }
//
//    public void setSubjectThreeTotalTime(int subjectThreeTotalTime) {
//        this.subjectThreeTotalTime = subjectThreeTotalTime;
//    }
//
//    public int getSubjectTwoTotalMiles() {
//        return this.subjectTwoTotalMiles;
//    }
//
//    public void setSubjectTwoTotalMiles(int subjectTwoTotalMiles) {
//        this.subjectTwoTotalMiles = subjectTwoTotalMiles;
//    }
//
//    public int getSubjectThreeTotalMiles() {
//        return this.subjectThreeTotalMiles;
//    }
//
//    public void setSubjectThreeTotalMiles(int subjectThreeTotalMiles) {
//        this.subjectThreeTotalMiles = subjectThreeTotalMiles;
//    }
//
//    public int getSubjectFourTotalMiles() {
//        return this.subjectFourTotalMiles;
//    }
//
//    public void setSubjectFourTotalMiles(int subjectFourTotalMiles) {
//        this.subjectFourTotalMiles = subjectFourTotalMiles;
//    }
//
//    public byte getCardState() {
//        return this.cardState;
//    }
//
//    public void setCardState(byte cardState) {
//        this.cardState = cardState;
//    }
//
//    public int getPracticeTime() {
//        return this.practiceTime;
//    }
//
//    public void setPracticeTime(int practiceTime) {
//        this.practiceTime = practiceTime;
//    }
//
//    public int getCheckInTimes() {
//        return this.checkInTimes;
//    }
//
//    public void setCheckInTimes(int checkInTimes) {
//        this.checkInTimes = checkInTimes;
//    }
//
//    public int getSubjectOneLearnedTime() {
//        return this.subjectOneLearnedTime;
//    }
//
//    public void setSubjectOneLearnedTime(int subjectOneLearnedTime) {
//        this.subjectOneLearnedTime = subjectOneLearnedTime;
//    }
//
//    public int getSubjectTwoLearnedTime() {
//        return this.subjectTwoLearnedTime;
//    }
//
//    public void setSubjectTwoLearnedTime(int subjectTwoLearnedTime) {
//        this.subjectTwoLearnedTime = subjectTwoLearnedTime;
//    }
//
//    public int getSubjectTwoLearnedMiles() {
//        return this.subjectTwoLearnedMiles;
//    }
//
//    public void setSubjectTwoLearnedMiles(int subjectTwoLearnedMiles) {
//        this.subjectTwoLearnedMiles = subjectTwoLearnedMiles;
//    }
//
//    public int getSubjectThreeLearnedTime() {
//        return this.subjectThreeLearnedTime;
//    }
//
//    public void setSubjectThreeLearnedTime(int subjectThreeLearnedTime) {
//        this.subjectThreeLearnedTime = subjectThreeLearnedTime;
//    }
//
//    public int getSubjectThreeLearnedMiles() {
//        return this.subjectThreeLearnedMiles;
//    }
//
//    public void setSubjectThreeLearnedMiles(int subjectThreeLearnedMiles) {
//        this.subjectThreeLearnedMiles = subjectThreeLearnedMiles;
//    }
//
//    public int getSubjectFourLearnedTime() {
//        return this.subjectFourLearnedTime;
//    }
//
//    public void setSubjectFourLearnedTime(int subjectFourLearnedTime) {
//        this.subjectFourLearnedTime = subjectFourLearnedTime;
//    }
//
//    public byte[] getVirtualCurrency() {
//        return this.virtualCurrency;
//    }
//
//    public void setVirtualCurrency(byte[] virtualCurrency) {
//        this.virtualCurrency = virtualCurrency;
//    }
//
//    public String getLastExitDate() {
//        return this.lastExitDate;
//    }
//
//    public void setLastExitDate(String lastExitDate) {
//        this.lastExitDate = lastExitDate;
//    }
//
//    public long getInfoId() {
//        return this.infoId;
//    }
//
//    public void setInfoId(long infoId) {
//        this.infoId = infoId;
//    }
//
//    public static CardInfoRecord copyInfo(CardInfo info){
//        CardInfoRecord infoRecord = new CardInfoRecord();
//        infoRecord.setCardState(info.getCardState());
//        infoRecord.setCarType(info.getCarType());
//        infoRecord.setCheckInTimes(info.getCheckInTimes());
//        infoRecord.setCompany(info.getCompany());
//        infoRecord.setIdentification(info.getIdentification());
//        infoRecord.setId(info.getId());
//        infoRecord.setLastExitDate(info.getLastExitDate());
//        infoRecord.setName(info.getName());
//        infoRecord.setPracticeTime(info.getPracticeTime());
//        infoRecord.setSubjectFourLearnedTime(info.getSubjectFourLearnedTime());
//        infoRecord.setSubjectFourTotalMiles(info.getSubjectFourTotalMiles());
//        infoRecord.setSubjectOneLearnedTime(info.getSubjectOneLearnedTime());
//        infoRecord.setSubjectOneTotalTime(info.getSubjectOneTotalTime());
//        infoRecord.setSubjectThreeLearnedMiles(info.getSubjectThreeLearnedMiles());
//        infoRecord.setSubjectThreeLearnedTime(info.getSubjectThreeLearnedTime());
//        infoRecord.setSubjectThreeTotalMiles(info.getSubjectThreeTotalMiles());
//        infoRecord.setSubjectThreeTotalTime(info.getSubjectThreeTotalTime());
//        infoRecord.setSubjectTwoLearnedMiles(info.getSubjectTwoLearnedMiles());
//        infoRecord.setSubjectTwoLearnedTime(info.getSubjectTwoLearnedTime());
//        infoRecord.setSubjectTwoTotalMiles(info.getSubjectTwoTotalMiles());
//        infoRecord.setSubjectTwoTotalTime(info.getSubjectTwoTotalTime());
//        infoRecord.setValidityPeriod(info.getValidityPeriod());
//        infoRecord.setVirtualCurrency(info.getVirtualCurrency());
//        infoRecord.setPracticeTimePerDay(info.getPracticeTimePerDay());
//
//        return infoRecord;
//    }
//
//    public static CardInfo reTransform(CardInfoRecord info){
//        CardInfo infoRecord = new CardInfo();
//
//        infoRecord.setCardState(info.getCardState());
//        infoRecord.setCarType(CardInfo.antiEnumCarType(info.getCarType()));
//        infoRecord.setCheckInTimes(info.getCheckInTimes());
//        infoRecord.setCompany(info.getCompany());
//        infoRecord.setIdentification(info.getIdentification());
//        infoRecord.setId(info.getId());
//        infoRecord.setLastExitDate(info.getLastExitDate());
//        infoRecord.setName(info.getName());
//        infoRecord.setPracticeTime(info.getPracticeTime());
//        infoRecord.setSubjectFourLearnedTime(info.getSubjectFourLearnedTime());
//        infoRecord.setSubjectFourTotalMiles(info.getSubjectFourTotalMiles());
//        infoRecord.setSubjectOneLearnedTime(info.getSubjectOneLearnedTime());
//        infoRecord.setSubjectOneTotalTime(info.getSubjectOneTotalTime());
//        infoRecord.setSubjectThreeLearnedMiles(info.getSubjectThreeLearnedMiles());
//        infoRecord.setSubjectThreeLearnedTime(info.getSubjectThreeLearnedTime());
//        infoRecord.setSubjectThreeTotalMiles(info.getSubjectThreeTotalMiles());
//        infoRecord.setSubjectThreeTotalTime(info.getSubjectThreeTotalTime());
//        infoRecord.setSubjectTwoLearnedMiles(info.getSubjectTwoLearnedMiles());
//        infoRecord.setSubjectTwoLearnedTime(info.getSubjectTwoLearnedTime());
//        infoRecord.setSubjectTwoTotalMiles(info.getSubjectTwoTotalMiles());
//        infoRecord.setSubjectTwoTotalTime(info.getSubjectTwoTotalTime());
//        infoRecord.setValidityPeriod(info.getValidityPeriod());
//        infoRecord.setVirtualCurrency(info.getVirtualCurrency());
//        infoRecord.setPracticeTimePerDay(info.getPracticeTimePerDay());
//
//        return infoRecord;
//    }
//}
