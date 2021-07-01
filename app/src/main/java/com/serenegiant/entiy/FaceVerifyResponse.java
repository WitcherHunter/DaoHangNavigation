package com.serenegiant.entiy;

public class FaceVerifyResponse {
    private boolean success = false;

    private int result;

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
