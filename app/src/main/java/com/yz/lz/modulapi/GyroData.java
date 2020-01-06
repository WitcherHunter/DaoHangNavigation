package com.yz.lz.modulapi;

/**
 * 陀螺仪数据类
 *
 * @author lz
 * @date 2018/05/26
 */

public class GyroData {

    /**
     * 加速度x
     */
    private int accel_x;
    /**
     * 加速度y
     */
    private int accel_y;
    /**
     * 加速度z
     */
    private int accel_z;
    /**
     * 陀螺仪x
     */
    private int gyro_x;
    /**
     * 陀螺仪y
     */
    private int gyro_y;
    /**
     * 陀螺仪z
     */
    private int gyro_z;
    /**
     * 温度
     */
    private int temp;


    public int getAccel_x() {
        return accel_x;
    }

    public void setAccel_x(int accel_x) {
        this.accel_x = accel_x;
    }

    public int getAccel_y() {
        return accel_y;
    }

    public void setAccel_y(int accel_y) {
        this.accel_y = accel_y;
    }

    public int getAccel_z() {
        return accel_z;
    }

    public void setAccel_z(int accel_z) {
        this.accel_z = accel_z;
    }

    public int getGyro_x() {
        return gyro_x;
    }

    public void setGyro_x(int gyro_x) {
        this.gyro_x = gyro_x;
    }

    public int getGyro_y() {
        return gyro_y;
    }

    public void setGyro_y(int gyro_y) {
        this.gyro_y = gyro_y;
    }

    public int getGyro_z() {
        return gyro_z;
    }

    public void setGyro_z(int gyro_z) {
        this.gyro_z = gyro_z;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    @Override
    public String toString() {
        return "GyroData{" +
                "accel_x=" + accel_x +
                ", accel_y=" + accel_y +
                ", accel_z=" + accel_z +
                ", gyro_x=" + gyro_x +
                ", gyro_y=" + gyro_y +
                ", gyro_z=" + gyro_z +
                ", temp=" + temp +
                '}';
    }
}
