package com.serenegiant.entiy;

import java.util.List;

public class CoachInfo {


    /**
     * success : true
     * result : [{"Name":"张居岩","IdCard":"130825198509035326","Teachpermitted":"C1","CoachNum":"9541612744471311","ReportState":1,"DepartMentName":"铭扬驾校","InsCode":"5015414765388886","HeadImage":"/HeadImage/Coach/132218838597930000.jpg"}]
     */

    private boolean success;
    private List<ResultBean> result;

    public CoachInfo(boolean success, List<ResultBean> result) {
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
         * Name : 张居岩
         * IdCard : 130825198509035326
         * Teachpermitted : C1
         * CoachNum : 9541612744471311
         * ReportState : 1
         * DepartMentName : 铭扬驾校
         * InsCode : 5015414765388886
         * HeadImage : /HeadImage/Coach/132218838597930000.jpg
         */

        private String Name;
        private String IdCard;
        private String Teachpermitted;
        private String CoachNum;
        private int ReportState;
        private String DepartMentName;
        private String InsCode;
        private String HeadImage;

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

        public String getTeachpermitted() {
            return Teachpermitted;
        }

        public void setTeachpermitted(String Teachpermitted) {
            this.Teachpermitted = Teachpermitted;
        }

        public String getCoachNum() {
            return CoachNum;
        }

        public void setCoachNum(String CoachNum) {
            this.CoachNum = CoachNum;
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
    }
}
