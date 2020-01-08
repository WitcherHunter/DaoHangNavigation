package com.serenegiant.entiy;

public class FaceVerifyRequest {
    private String photopath;
    private String imagebase64;

    public FaceVerifyRequest(String photopath, String imagebase64) {
        this.photopath = photopath;
        this.imagebase64 = imagebase64;
    }

    public String getPhotopath() {
        return photopath;
    }

    public void setPhotopath(String photopath) {
        this.photopath = photopath;
    }

    public String getImagebase64() {
        return imagebase64;
    }

    public void setImagebase64(String imagebase64) {
        this.imagebase64 = imagebase64;
    }
}
