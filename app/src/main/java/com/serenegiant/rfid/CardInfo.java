package com.serenegiant.rfid;

public class CardInfo {

  /**
   * 第一扇区
   */
  //卡片类型
  RFID.CardType cardType = RFID.CardType.UnKnown;
  //学员/教练编号
  String id = "";
  //持卡人姓名
  String name = "";
  //单日训练时长，单位为分
  int practiceTimePerDay = 0;

  public RFID.CardType getCardType() {
    return cardType;
  }

  public void setCardType(RFID.CardType cardType) {
    this.cardType = cardType;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getPracticeTimePerDay() {
    return practiceTimePerDay;
  }

  public void setPracticeTimePerDay(int practiceTimePerDay) {
    this.practiceTimePerDay = practiceTimePerDay;
  }

  /**
   * 第二扇区
   */
  //证件号码
  String identification = "";
  //所属驾校
  String company = "";
  //卡有效期
  String validityPeriod = "";
  //培训车型
  String carType = "";

  public String getIdentification() {
    return identification;
  }

  public void setIdentification(String identification) {
    this.identification = identification;
  }

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public String getValidityPeriod() {
    return validityPeriod;
  }

  public void setValidityPeriod(String validityPeriod) {
    this.validityPeriod = validityPeriod;
  }

  public String getCarType() {
    return carType;
  }

  public void setCarType(byte type) {
    this.carType = enumCarType(type);
  }

  public static String enumCarType(byte type) {
    switch (type) {
      case 0x10:
        return "A0";
      case 0x11:
        return "A1";
      case 0x12:
        return "A2";
      case 0x13:
        return "A3";
      case 0x20:
        return "B0";
      case 0x21:
        return "B1";
      case 0x22:
        return "B2";
      case 0x30:
        return "C0";
      case 0x31:
        return "C1";
      case 0x32:
        return "C2";
      case 0x33:
        return "C3";
      case 0x34:
        return "C4";
      case 0x35:
        return "C5";
      case 0x40:
        return "D";
      case 0x50:
        return "E";
      case 0x60:
        return "F";
      case 0x70:
        return "M";
//      case 0x80:
//        return "N";
//      case 0x90:
//        return "P";
      default:
        return "";
    }
  }

  public static byte antiEnumCarType(String type) {
    switch (type) {
      case "A0":
        return 0x10;
      case "A1":
        return 0x11;
      case "A2":
        return 0x12;
      case "A3":
        return 0x13;
      case "B0":
        return 0x20;
      case "B1":
        return 0x21;
      case "B2":
        return 022;
      case "C0":
        return 0x30;
      case "C1":
        return 0x31;
      case "C2":
        return 0x32;
      case "C3":
        return 0x33;
      case "C4":
        return 0x34;
      case "C5":
        return 0x35;
      case "D":
        return 0x40;
      case "E":
        return 0x50;
      case "F":
        return 0x60;
      case "M":
        return 0x70;
//      case 0x80:
//        return "N";
//      case 0x90:
//        return "P";
      default:
        return 0x00;
    }
  }

  /**
   * 第三扇区
   */
  //科目一总学时
  int subjectOneTotalTime = 0;
  //科目二总学时
  int subjectTwoTotalTime = 0;
  //科目三总学时
  int subjectThreeTotalTime = 0;
  //科目二总里程
  int subjectTwoTotalMiles = 0;
  //科目三总里程
  int subjectThreeTotalMiles = 0;
  //科目四总学时
  int subjectFourTotalMiles = 0;

  public int getSubjectOneTotalTime() {
    return subjectOneTotalTime;
  }

  public void setSubjectOneTotalTime(int subjectOneTotalTime) {
    this.subjectOneTotalTime = subjectOneTotalTime;
  }

  public int getSubjectTwoTotalTime() {
    return subjectTwoTotalTime;
  }

  public void setSubjectTwoTotalTime(int subjectTwoTotalTime) {
    this.subjectTwoTotalTime = subjectTwoTotalTime;
  }

  public int getSubjectThreeTotalTime() {
    return subjectThreeTotalTime;
  }

  public void setSubjectThreeTotalTime(int subjectThreeTotalTime) {
    this.subjectThreeTotalTime = subjectThreeTotalTime;
  }

  public int getSubjectTwoTotalMiles() {
    return subjectTwoTotalMiles;
  }

  public void setSubjectTwoTotalMiles(int subjectTwoTotalMiles) {
    this.subjectTwoTotalMiles = subjectTwoTotalMiles;
  }

  public int getSubjectThreeTotalMiles() {
    return subjectThreeTotalMiles;
  }

  public void setSubjectThreeTotalMiles(int subjectThreeTotalMiles) {
    this.subjectThreeTotalMiles = subjectThreeTotalMiles;
  }

  public int getSubjectFourTotalMiles() {
    return subjectFourTotalMiles;
  }

  public void setSubjectFourTotalMiles(int subjectFourTotalMiles) {
    this.subjectFourTotalMiles = subjectFourTotalMiles;
  }

  /**
   * 第二十六扇区
   */
  //卡片状态 0.未签到 1.已签到
  byte cardState = 0;
  //当日训练时长
  int practiceTime = 0;
  //签到次数
  int checkInTimes = 0;
  //科目一已学学时
  int subjectOneLearnedTime = 0;
  //科目二已学学时
  int subjectTwoLearnedTime = 0;
  //科目二培训里程
  int subjectTwoLearnedMiles = 0;
  //科目三已学学时
  int subjectThreeLearnedTime = 0;
  //科目三培训里程
  int subjectThreeLearnedMiles = 0;
  //科目四已学学时
  int subjectFourLearnedTime = 0;
  //虚拟币(教练无)
  byte[] virtualCurrency = new byte[0];

  String lastExitDate = "";

  public String getLastExitDate() {
    return lastExitDate;
  }

  public void setLastExitDate(String lastExitDate) {
    this.lastExitDate = lastExitDate;
  }

  public byte getCardState() {
    return cardState;
  }

  public void setCardState(byte cardState) {
    this.cardState = cardState;
  }

  public int getPracticeTime() {
    return practiceTime;
  }

  public void setPracticeTime(int practiceTime) {
    this.practiceTime = practiceTime;
  }

  public int getCheckInTimes() {
    return checkInTimes;
  }

  public void setCheckInTimes(int checkInTimes) {
    this.checkInTimes = checkInTimes;
  }

  public int getSubjectOneLearnedTime() {
    return subjectOneLearnedTime;
  }

  public void setSubjectOneLearnedTime(int subjectOneLearnedTime) {
    this.subjectOneLearnedTime = subjectOneLearnedTime;
  }

  public int getSubjectTwoLearnedTime() {
    return subjectTwoLearnedTime;
  }

  public void setSubjectTwoLearnedTime(int subjectTwoLearnedTime) {
    this.subjectTwoLearnedTime = subjectTwoLearnedTime;
  }

  public int getSubjectTwoLearnedMiles() {
    return subjectTwoLearnedMiles;
  }

  public void setSubjectTwoLearnedMiles(int subjectTwoLearnedMiles) {
    this.subjectTwoLearnedMiles = subjectTwoLearnedMiles;
  }

  public int getSubjectThreeLearnedTime() {
    return subjectThreeLearnedTime;
  }

  public void setSubjectThreeLearnedTime(int subjectThreeLearnedTime) {
    this.subjectThreeLearnedTime = subjectThreeLearnedTime;
  }

  public int getSubjectThreeLearnedMiles() {
    return subjectThreeLearnedMiles;
  }

  public void setSubjectThreeLearnedMiles(int subjectThreeLearnedMiles) {
    this.subjectThreeLearnedMiles = subjectThreeLearnedMiles;
  }

  public int getSubjectFourLearnedTime() {
    return subjectFourLearnedTime;
  }

  public void setSubjectFourLearnedTime(int subjectFourLearnedTime) {
    this.subjectFourLearnedTime = subjectFourLearnedTime;
  }

  public byte[] getVirtualCurrency() {
    if (virtualCurrency == null || virtualCurrency.length == 0)
      return new byte[4];
    return virtualCurrency;
  }

  public void setVirtualCurrency(byte[] virtualCurrency) {
    this.virtualCurrency = virtualCurrency;
  }

  /**
   * 指纹扇区
   */
  private byte[] finger1; //指纹一
  private byte[] finger2; //指纹二

  public byte[] getFinger1() {
    return finger1;
  }

  public void setFinger1(byte[] finger1) {
    this.finger1 = finger1;
  }

  public byte[] getFinger2() {
    return finger2;
  }

  public void setFinger2(byte[] finger2) {
    this.finger2 = finger2;
  }
}
