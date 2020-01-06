package com.serenegiant.constants;

import java.util.HashMap;
import java.util.Map;

public class CourseCodeGenerator {

    private static Map<String, String> carTypeMap = new HashMap<>();

    public static void init() {
        carTypeMap.put("A1","01");
        carTypeMap.put("A2","02");
        carTypeMap.put("A3","03");
        carTypeMap.put("B1","11");
        carTypeMap.put("B2","12");
        carTypeMap.put("C1","21");
        carTypeMap.put("C2","22");
        carTypeMap.put("C3","23");
        carTypeMap.put("C4","24");
        carTypeMap.put("C5","25");
        carTypeMap.put("D","31");
        carTypeMap.put("E","32");
        carTypeMap.put("F","33");
        carTypeMap.put("M","41");
        carTypeMap.put("N","42");
        carTypeMap.put("P","43");
    }

    public static String generateCourseCode(int type, String carType) {
        if (type == 2){
            return "1" +
                    carTypeMap.get(carType) +
                    "2" +
                    "13"+
                    "0000";

        } else if (type == 3){
            return "1" +
                    carTypeMap.get(carType) +
                    "3" +
                    "36"+
                    "0000";
        } else
            return "";
    }

    public static String generateDemoCourseCode(int type, String carType){
        if (type == 2){
            return "3" +
                    carTypeMap.get(carType) +
                    "2" +
                    "13"+
                    "0000";

        } else if (type == 3){
            return "3" +
                    carTypeMap.get(carType) +
                    "3" +
                    "36"+
                    "0000";
        } else
            return "";
    }
}
