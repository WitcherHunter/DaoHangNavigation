package com.serenegiant.entiy;

public class FaceVerifyResponse {
    private Boolean success = false;

    private int result;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
