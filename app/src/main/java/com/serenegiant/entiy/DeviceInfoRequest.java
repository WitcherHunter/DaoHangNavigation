package com.serenegiant.entiy;

public class DeviceInfoRequest {
    String imei;

    public DeviceInfoRequest(String imei) {
        this.imei = imei;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
}
