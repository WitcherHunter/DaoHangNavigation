package com.serenegiant.entiy;

import java.util.List;

public class DeviceInfoResponse {

    /**
     * success : true
     * result : [{"DeviceNo":"13745612378","ProvinceId":"43","CityCountyId":"1322","ManufacturerId":"BDMCH","Model":"V600","Sn":"C123456","Imei":"868704043712345","PlateColor":2,"LicNum":"湘KE609学","coachlogintype":"iccard","studentlogintype":"iccard|phone"}]
     */

    private boolean success;
    private List<ResultBean> result;

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
         * DeviceNo : 13745612378
         * ProvinceId : 43
         * CityCountyId : 1322
         * ManufacturerId : BDMCH
         * Model : V600
         * Sn : C123456
         * Imei : 868704043712345
         * PlateColor : 2
         * LicNum : 湘KE609学
         * coachlogintype : iccard
         * studentlogintype : iccard|phone
         */

        private String DeviceNo;
        private int ProvinceId;
        private int CityCountyId;
        private String ManufacturerId;
        private String Model;
        private String Sn;
        private String Imei;
        private int PlateColor;
        private String LicNum;
        private String coachlogintype;
        private String studentlogintype;

        public String getDeviceNo() {
            return DeviceNo;
        }

        public void setDeviceNo(String DeviceNo) {
            this.DeviceNo = DeviceNo;
        }

        public int getProvinceId() {
            return ProvinceId;
        }

        public void setProvinceId(int provinceId) {
            ProvinceId = provinceId;
        }

        public int getCityCountyId() {
            return CityCountyId;
        }

        public void setCityCountyId(int cityCountyId) {
            CityCountyId = cityCountyId;
        }

        public String getManufacturerId() {
            return ManufacturerId;
        }

        public void setManufacturerId(String ManufacturerId) {
            this.ManufacturerId = ManufacturerId;
        }

        public String getModel() {
            return Model;
        }

        public void setModel(String Model) {
            this.Model = Model;
        }

        public String getSn() {
            return Sn;
        }

        public void setSn(String Sn) {
            this.Sn = Sn;
        }

        public String getImei() {
            return Imei;
        }

        public void setImei(String Imei) {
            this.Imei = Imei;
        }

        public int getPlateColor() {
            return PlateColor;
        }

        public void setPlateColor(int plateColor) {
            PlateColor = plateColor;
        }

        public String getLicNum() {
            return LicNum;
        }

        public void setLicNum(String LicNum) {
            this.LicNum = LicNum;
        }

        public String getCoachlogintype() {
            return coachlogintype;
        }

        public void setCoachlogintype(String coachlogintype) {
            this.coachlogintype = coachlogintype;
        }

        public String getStudentlogintype() {
            return studentlogintype;
        }

        public void setStudentlogintype(String studentlogintype) {
            this.studentlogintype = studentlogintype;
        }
    }
}
