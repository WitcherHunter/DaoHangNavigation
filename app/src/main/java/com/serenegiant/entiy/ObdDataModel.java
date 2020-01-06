package com.serenegiant.entiy;

public class ObdDataModel {
    //累积里程（米）
    private static int totalMiles;
    //累积油量（ml）
    private static int totalFuel;
    //瞬时油耗（ml/h)
    private static int currentFuel;
    //电压（mv）
    private static int voltage;
    //转速（rpm)
    private static int rotatingSpeed;
    //时速（km/h）
    private static int speed;

    public static int getTotalMiles() {
        return totalMiles;
    }

    public static void setTotalMiles(int totalMiles) {
        ObdDataModel.totalMiles = totalMiles;
    }

    public static int getTotalFuel() {
        return totalFuel;
    }

    public static void setTotalFuel(int totalFuel) {
        ObdDataModel.totalFuel = totalFuel;
    }

    public static int getCurrentFuel() {
        return currentFuel;
    }

    public static void setCurrentFuel(int currentFuel) {
        ObdDataModel.currentFuel = currentFuel;
    }

    public static int getVoltage() {
        return voltage;
    }

    public static void setVoltage(int voltage) {
        ObdDataModel.voltage = voltage;
    }

    public static int getRotatingSpeed() {
        return rotatingSpeed;
    }

    public static void setRotatingSpeed(int rotatingSpeed) {
        ObdDataModel.rotatingSpeed = rotatingSpeed;
    }

    public static int getSpeed() {
        return speed;
    }

    public static void setSpeed(int speed) {
        ObdDataModel.speed = speed;
    }
}
