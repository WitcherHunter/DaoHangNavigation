package com.serenegiant.entiy;

public class StudentAndCoachInfoRequest {

    long iccard;

    public StudentAndCoachInfoRequest(long iccard) {
        this.iccard = iccard;
    }

    public long getIccard() {
        return iccard;
    }

    public void setIccard(long iccard) {
        this.iccard = iccard;
    }
}
