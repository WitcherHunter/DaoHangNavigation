package com.serenegiant.net;

/**
 * Created by Administrator on 2016-11-21.
 */
public class ObdRunningData {
    public static float getBaterryVoltage() {
        return baterryVoltage;
    }

    public static void setBaterryVoltage(float baterryVoltage) {
        ObdRunningData.baterryVoltage = baterryVoltage;
    }

    public static int getEngineRpm() {
        return engineRpm;
    }

    public static void setEngineRpm(int engineRpm) {
        ObdRunningData.engineRpm = engineRpm;
    }

    public static float getSpeed() {
        return speed;
    }

    public static void setSpeed(float speed) {
        ObdRunningData.speed = speed;
    }

    public static float getCoolantTemperature() {
        return coolantTemperature;
    }

    public static void setCoolantTemperature(float coolantTemperature) {
        ObdRunningData.coolantTemperature = coolantTemperature;
    }

    public static float getCurMiles() {
        return curMiles;
    }

    public static void setCurMiles(float curMiles) {
        ObdRunningData.curMiles = curMiles;
    }

    public static float getTotalMiles() {
        return totalMiles;
    }

    public static void setTotalMiles(float totalMiles) {
        ObdRunningData.totalMiles = totalMiles;
    }

    public static float getCurUsedFuel() {
        return curUsedFuel;
    }

    public static void setCurUsedFuel(float curUsedFuel) {
        ObdRunningData.curUsedFuel = curUsedFuel;
    }

    private static float baterryVoltage=0;
    private static int engineRpm=0;//发动机转速
    private static float speed=0;//车速
    private static float coolantTemperature=0;//冷却液温度
    private static float curMiles=0;//本次里程
    private static float totalMiles=0;//总里程
    private static float curUsedFuel=0;//本次油耗
}
