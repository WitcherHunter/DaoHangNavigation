package com.serenegiant.net;

/**
 * Created by Administrator on 2016/8/18.
 */
public class FenceDataStruct {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public long[] getLatitude() {
        return latitude;
    }

    public void setLatitude(long[] latitude) {
        this.latitude = latitude;
    }

    public long[] getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(long[] longtitude) {
        this.longtitude = longtitude;
    }

    String id;
    int mode;
    int cnt;
    long[] latitude;
    long[] longtitude;
}
