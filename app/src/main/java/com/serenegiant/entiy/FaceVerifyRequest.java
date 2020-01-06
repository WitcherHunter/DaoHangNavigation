package com.serenegiant.entiy;

import a.B;

public class FaceVerifyRequest {

    private String image;
    private int type;
    private String num;
    private String inscode;

    /**
     *
     * @param image
     * @param type
     * @param num
     * @param inscode
     */
    public FaceVerifyRequest(String image, int type, String num, String inscode) {
        this.image = image;
        this.type = type;
        this.num = num;
        this.inscode = inscode;

    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getInscode() {
        return inscode;
    }

    public void setInscode(String inscode) {
        this.inscode = inscode;
    }
}
