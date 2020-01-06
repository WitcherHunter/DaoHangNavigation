package com.serenegiant.utils;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MilesCalculateUtil {
  private static final String TAG = "MilesCalculate";

  private static Map<String, int[]> timeAndMilesMap = new HashMap<>();
  private static Map<String, Float> ratioMap = new HashMap<>();
  private static Map<String, Integer> carTypeMap = new HashMap<>();

  public static void init(){
    timeAndMilesMap.put("A1",new int[]{2160,1200});
    timeAndMilesMap.put("B1",new int[]{2160,1200});
    timeAndMilesMap.put("A2",new int[]{2400,1320});
    timeAndMilesMap.put("A3",new int[]{3180,1980});
    timeAndMilesMap.put("B2",new int[]{3240,1920});
    timeAndMilesMap.put("C1",new int[]{960,1440});
    timeAndMilesMap.put("C2",new int[]{960,1440});
    timeAndMilesMap.put("C3",new int[]{840,960});
    timeAndMilesMap.put("C4",new int[]{600,600});
    timeAndMilesMap.put("D",new int[]{600,600});
    timeAndMilesMap.put("E",new int[]{600,600});
    timeAndMilesMap.put("F",new int[]{600,600});
    timeAndMilesMap.put("C5",new int[]{960,1440});
    timeAndMilesMap.put("P",new int[]{1200,1800});

    ratioMap.put("A1",0.16f);
    ratioMap.put("B1",0.153f);
    ratioMap.put("A2",0.135f);
    ratioMap.put("A3",0.147f);
    ratioMap.put("B2",0.15f);
    ratioMap.put("C1",0.133f);
    ratioMap.put("C2",0.129f);
    ratioMap.put("C3",0.143f);
    ratioMap.put("C4",0.158f);
    ratioMap.put("D",0.152f);
    ratioMap.put("E",0.142f);
    ratioMap.put("F",0.139f);
    ratioMap.put("C5",0.136f);
    ratioMap.put("P",0.131f);

    carTypeMap.put("A1",10);
    carTypeMap.put("A2",9);
    carTypeMap.put("A3",8);
    carTypeMap.put("B1",7);
    carTypeMap.put("B2",6);
    carTypeMap.put("C1",5);
    carTypeMap.put("C2",4);
  }

  public static boolean coachCarTypeValid(String key1, String key2){
    if (carTypeMap.containsKey(key1) && carTypeMap.containsKey(key2))
      return carTypeMap.get(key1) >= carTypeMap.get(key2);
    return false;
  }

  private static int randomMiles(){
    return new Random().nextInt(100 * 1000) + 300 * 1000;
  }

  /**
   * 公里数新计算方法
   * @param carType
   * @param item
   * @param minutesThisTime
   * @return
   */
  public static int calculateDistance(String carType, byte item, int minutesThisTime){
    if (!timeAndMilesMap.containsKey(carType))
      return 0;
    int[] times = timeAndMilesMap.get(carType);
    if (!ratioMap.containsKey(carType))
      return 0;
    float ratio = ratioMap.get(carType) - 0.035f;

    int totalMiles = randomMiles();

    int classMiles;
    int result;
    int time;

    if (item == (byte)2){
      time = times[0];
      classMiles = (int)(totalMiles * ratio);
    } else {
      time = times[1];
      classMiles = (int)(totalMiles * (1 - ratio));
    }

    result = (minutesThisTime * classMiles) / time;

    return result;
  }

  /**
   * 计算本次行驶里程
   * @param carType
   * @param learnedTime
   * @param item
   * @param learnedMiles
   * @param minutesThisTime
   * @return
   */
  public static int calculateMiles(String carType, int learnedTime, byte item, int learnedMiles, int minutesThisTime){
    Log.e(TAG,"learnedTime is " + learnedTime + ", learnedMiles is " + learnedMiles + ", minutesThisTime is " + minutesThisTime);
    Log.e(TAG,"cartype is " + carType);
    if (!timeAndMilesMap.containsKey(carType))
      return 0;
    int[] times = timeAndMilesMap.get(carType);
    if (!ratioMap.containsKey(carType))
      return 0;
    float ratio = ratioMap.get(carType) - 0.035f;

    Log.e(TAG, "ratio is " + ratio);

    int totalMiles = randomMiles();

    Log.e(TAG, "totalmiles is " + totalMiles);
    //科目二或科目三需要培训的里程
    int classMiles = 0;
    //所需学时
    int time = 0;
    if (item == (byte)2) {
      time = times[0];
      classMiles = (int) (totalMiles * ratio);

      Log.e(TAG, "item is 2");
      Log.e(TAG, "time is " + time);
      Log.e(TAG, "classmiles is " + classMiles);
    }
    else {
      time = times[1];
      classMiles = (int) (totalMiles * (1 - ratio));

      Log.e(TAG, "item is 3");
      Log.e(TAG, "time is " + time);
      Log.e(TAG, "classmiles is " + classMiles);
    }

    if (learnedMiles >= classMiles){
      return new Random().nextInt(10000);
    }

    if (time >= learnedTime){
      float milesPerMinute = (float)(classMiles - learnedMiles) / (time - learnedTime);
      return (int) (minutesThisTime * milesPerMinute);
    } else {
      return new Random().nextInt(10000);
    }
  }

}
