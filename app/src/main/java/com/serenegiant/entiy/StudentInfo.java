package com.serenegiant.entiy;

import java.util.List;

/**
 * Created by Administrator on 2017-04-20.
 */

public class StudentInfo {
    /**
     * success : true
     * result : [{"Name":"李怡琛","IdCard":"420104197809080408","TrainType":"C1","StuNum":"5114153413567166","ReportState":1,"DepartMentName":"铭扬驾校","InsCode":"5015414765388886","HeadImage":"/HeadImage/Student/13221883731907000002.jpg","TodaySubOne":0,"TodaySubTwo":0,"TodaySubThree":0,"TodaySubFour":0}]
     */

    private boolean success;
    private List<ResultBean> result;

    public StudentInfo(boolean success, List<ResultBean> result) {
        this.success = success;
        this.result = result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * Name : 李怡琛
         * IdCard : 420104197809080408
         * TrainType : C1
         * StuNum : 5114153413567166
         * ReportState : 1
         * DepartMentName : 铭扬驾校
         * InsCode : 5015414765388886
         * HeadImage : /HeadImage/Student/13221883731907000002.jpg
         * TodaySubOne : 0
         * TodaySubTwo : 0
         * TodaySubThree : 0
         * TodaySubFour : 0
         */

        private String Name;
        private String IdCard;
        private String TrainType;
        private String StuNum;
        private int ReportState;
        private String DepartMentName;
        private String InsCode;
        private String HeadImage;
        private int TodaySubOne;
        private int TodaySubTwo;
        private int TodaySubThree;
        private int TodaySubFour;

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
        }

        public String getIdCard() {
            return IdCard;
        }

        public void setIdCard(String IdCard) {
            this.IdCard = IdCard;
        }

        public String getTrainType() {
            return TrainType;
        }

        public void setTrainType(String TrainType) {
            this.TrainType = TrainType;
        }

        public String getStuNum() {
            return StuNum;
        }

        public void setStuNum(String StuNum) {
            this.StuNum = StuNum;
        }

        public int getReportState() {
            return ReportState;
        }

        public void setReportState(int ReportState) {
            this.ReportState = ReportState;
        }

        public String getDepartMentName() {
            return DepartMentName;
        }

        public void setDepartMentName(String DepartMentName) {
            this.DepartMentName = DepartMentName;
        }

        public String getInsCode() {
            return InsCode;
        }

        public void setInsCode(String InsCode) {
            this.InsCode = InsCode;
        }

        public String getHeadImage() {
            return HeadImage;
        }

        public void setHeadImage(String HeadImage) {
            this.HeadImage = HeadImage;
        }

        public int getTodaySubOne() {
            return TodaySubOne;
        }

        public void setTodaySubOne(int TodaySubOne) {
            this.TodaySubOne = TodaySubOne;
        }

        public int getTodaySubTwo() {
            return TodaySubTwo;
        }

        public void setTodaySubTwo(int TodaySubTwo) {
            this.TodaySubTwo = TodaySubTwo;
        }

        public int getTodaySubThree() {
            return TodaySubThree;
        }

        public void setTodaySubThree(int TodaySubThree) {
            this.TodaySubThree = TodaySubThree;
        }

        public int getTodaySubFour() {
            return TodaySubFour;
        }

        public void setTodaySubFour(int TodaySubFour) {
            this.TodaySubFour = TodaySubFour;
        }
    }
}
