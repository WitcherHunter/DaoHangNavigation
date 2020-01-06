package com.serenegiant.entiy;

import java.util.List;

public class GeoFenceModel {

    /**
     * result : 200
     * message : success
     * data : [{"polygon":"108.93628,34.27621;108.93879,34.27617;108.93881,34.27496;108.93627,34.27498","regionId":1,"subject":2}]
     */

    private int result;
    private String message;
    private List<DataBean> data;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * polygon : 108.93628,34.27621;108.93879,34.27617;108.93881,34.27496;108.93627,34.27498
         * regionId : 1
         * subject : 2
         */

        private String polygon;
        private int regionId;
        private int subject;

        public String getPolygon() {
            return polygon;
        }

        public void setPolygon(String polygon) {
            this.polygon = polygon;
        }

        public int getRegionId() {
            return regionId;
        }

        public void setRegionId(int regionId) {
            this.regionId = regionId;
        }

        public int getSubject() {
            return subject;
        }

        public void setSubject(int subject) {
            this.subject = subject;
        }
    }
}
