package com.serenegiant.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.rscja.deviceapi.BDNavigation;
import com.rscja.deviceapi.entity.BDLocation;
import com.rscja.deviceapi.entity.SatelliteEntity;
import com.rscja.deviceapi.exception.ConfigurationException;
import com.serenegiant.entiy.GPSInfo;
import com.serenegiant.utils.IUtil;
import com.serenegiant.utils.StringUtility;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Administrator on 2017/5/4.
 */

public class BDGPSService extends Service{

    private static final String TAG = "BDGPSService";
    int index=1;
    long BDPosition = System.currentTimeMillis();
    BDNavigation mInstance;
    private double mSpeed;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        /*boolean bool= false;
        try {
            mInstance = BDNavigation.getInstance();
//            mInstance.open("test");	// 10M 不再计录log
            mInstance.open();		// 不记录log
            bool = true;
        } catch (ConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(bool) {
            //mInstance.StartReadThread();
//            mInstance.addTestBDRawDataListener(getResultResultRaw);
            Log.d(TAG, "注册监听次数:" + index++);

            mInstance.addBDStatusListener(mBDStatusListener);
            mInstance.addBDLocationListener(BDNavigation.BDProviderEnum.GPSandBD, mBDLocationListener);
        }*/
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean bool= false;
        try {
            mInstance = BDNavigation.getInstance();
            bool = mInstance.open();
            Log.e(TAG,"初始化值"+bool);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(bool) {
            mInstance.addBDStatusListener(mBDStatusListener);
            mInstance.addBDLocationListener(BDNavigation.BDProviderEnum.GPSandBD, mBDLocationListener);
            mInstance.addTestBDRawDataListener(new BDNavigation.TestResultRawData() {
                @Override
                public void ResultALLRawData(String s) {
                }

                @Override
                public void ResultALLRawData(byte[] bytes, int i) {

                }
            });
        }

        return super.onStartCommand(intent, flags, startId);
    }

    BDNavigation.TestResultRawData getResultResultRaw=new BDNavigation.TestResultRawData(){

        @Override
        public void ResultALLRawData(String arg0) {
            // TODO Auto-generated method stub
            // Log.d(TAG, arg0);
        }

        @Override
        public void ResultALLRawData(byte[] arg0, int arg1) {
                String result = new String(arg0, 0, arg1);
                parseNMEAMsg(result);
        }
    };

    /**
     * 解析NMEA格式消息
     *
     * @return
     */
    private void parseNMEAMsg(String result) {
        if (StringUtility.isEmpty(result)) {
            return;
        }
        int idx = result.indexOf("$");
        if (idx == -1) {
            Log.d(TAG,"定位失败");
            return;
        }
        result = result.substring(idx);
        String[] arrResult = result.split("\r\n");
        for (String msg : arrResult) {
            if (msg.indexOf(",") == -1) {
                break;
            }
            if (msg.split("$").length > 1) {
                break;
            }
            String[] arr = msg.split(",");
            if (arr.length < 2) {
                break;
            }
            if (arr[0].length() < 6) {
                break;
            }
            // 定位标识
            String type = arr[0].substring(1, 3);
            String head = arr[0].substring(3, 6);
            //Log.e(TAG,"      type="+type);
            //Log.e(TAG,"      head="+head);
            if (head.contains("GGA")) {
                if (arr.length < 10) {
                    break;
                }
                if (!type.toUpperCase().trim().equals("GN")) {
                    break;
                }
                // 无效数据
                if (convert2Double(arr[6]) != 1) {
                    break;
                }
                if (StringUtility.isEmpty(arr[2])
                        || StringUtility.isEmpty(arr[4])) {
                    break;
                }
                int UseCount = Integer.parseInt(arr[7]);//可用卫星
                GPSInfo.setUseCount(UseCount);
                String altitude = arr[9];
                double temp_altitude=0;
                if (!StringUtility.isEmpty(altitude)) {
                    temp_altitude = Double.parseDouble(altitude);//海拔
                }
//                double latT = convert2Double(arr[2]) / 100;
//                double lonT = convert2Double(arr[4]) / 100;
//                double lat = (int) latT + (latT - (int) latT) * 100 / 60;
//                double lon = (int) lonT + (lonT - (int) lonT) * 100 / 60;
//                if (!StringUtility.isEmpty(arr[3])
//                        && arr[3].toUpperCase().equals("S")) {
//                    lat = -lat;
//                }
//                if (!StringUtility.isEmpty(arr[5])
//                        && arr[5].toUpperCase().equals("W")) {
//                    lon = -lon;
//                }
//                Log.d(TAG,"可用卫星："+UseCount+",BDGPS经纬度："+lon+","+lat+",海拔："+temp_altitude);
//                GPSInfo.setLongitude(lon);
//                GPSInfo.setLatitude(lat);
                GPSInfo.setAltitude(temp_altitude);
                // loc = new BDLocation(lat, lon, convert2Double(arr[9]), type);
                // satelliteUCount = (int) convert2Double(arr[7]);
            } else if (head.contains("GLL")) {
            } else if (head.contains("GSA")) {
            } else if (head.contains("GSV")) {

            } else if (head.contains("RMC")) {

                if (arr.length < 10) {
                    break;
                }
                if (!(arr[2].equals("A"))) {
                    break;
                }
                double latT = convert2Double(arr[3]) / 100;
                double lonT = convert2Double(arr[5]) / 100;
                double lat = (int) latT + (latT - (int) latT) * 100 / 60;
                double lon = (int) lonT + (lonT - (int) lonT) * 100 / 60;
                if (!StringUtility.isEmpty(arr[4])
                        && arr[3].toUpperCase().equals("S")) {
                    lat = -lat;
                }
                if (!StringUtility.isEmpty(arr[6])
                        && arr[5].toUpperCase().equals("W")) {
                    lon = -lon;
                }
//                Log.d(TAG,"BDGPS经纬度："+lon+","+lat);
                if(lon != 0)
                GPSInfo.setLongitude(lon);
                if(lat != 0)
                GPSInfo.setLatitude(lat);
                String spd = arr[7];
                String orientition = arr[8];
                String GPSDay = arr[9];
                String GPSTime = arr[1];
                double temp_orientition = 0;
                double temp_spd = 0;
                if (!StringUtility.isEmpty(orientition)) {
                    temp_orientition = Double.parseDouble(orientition);//方向
                }
                if (!StringUtility.isEmpty(spd)) {
                    temp_spd = Double.parseDouble(spd) * 1.852;//速度
                }
                GPSInfo.setSpeed(temp_spd/3.6);
                GPSInfo.setAngle((int)temp_orientition);
//                Log.d(TAG,"BDGPS速度："+temp_spd+",方向："+temp_orientition);
                StringBuffer sb = new StringBuffer();
//                if(Integer.parseInt(GPSDay.substring(4))>17)
//                {
//                    sb.append(MessageDefine.GPSYMD);
//                }else {
                    sb.append(GPSDay.substring(4));
                    sb.append(GPSDay.substring(2, 4));
                    sb.append(GPSDay.substring(0, 2));
//                }
                int hh = Integer.parseInt(GPSTime.substring(0,2))+8;
                String str = String.valueOf(hh);
                if(hh<10)
                {
                    str = "0"+str;
                }
                sb.append(str);
                sb.append(GPSTime.substring(2,4));
                sb.append(GPSTime.substring(4,6));
//                Log.d(TAG,"gps时间："+sb.toString());
                GPSInfo.setGPSTime(sb.toString());
            }
        }
    }

    //位移计算
    private double lat = 0;
    private double lon = 0;
    public double getLong(double lat,double lon){
        Log.e("BD",""+lat+"$"+lon);
        if(this.lat == 0 || this.lon == 0){
            this.lat = lat;
            this.lon = lon;
            return 0;
        }
        double mm = GetDistance(lat,lon,this.lat,this.lon);
        this.lat = lat;
        this.lon = lon;
        return mm;
    }

    //经纬度计算距离
    public static double GetDistance(double lat1, double lng1, double lat2, double lng2)
    {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) +
                Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 1000);
        return s;
    }
    private static final double EARTH_RADIUS = 6378.137;//地球半径,单位千米
    private static double rad(double d)
    {
        return d * Math.PI / 180.0;
    }




    private double convert2Double(String value) {
        if (StringUtility.isEmpty(value)) {
            return 0d;
        }

        if (!StringUtility.isNum(value) || value.trim().equals("-")) {
            return 0d;
        }

        return Double.parseDouble(value.trim());
    }

    BDNavigation.BDLocationListener mBDLocationListener = new BDNavigation.BDLocationListener(){

        @Override
        public void onLocationChanged(BDLocation bdLocation) {
            if(bdLocation != null)
                updateView(bdLocation);
        }

        @Override
        public void onDataResult(String s) {
        }
    };

    BDNavigation.BDStatusListener mBDStatusListener = new BDNavigation.BDStatusListener(){

        @Override
        public void onBDSatelliteViewChanged(int i) {
        }

        @Override
        public void onBDSatelliteUsedChanged(int i) {
        }

        @Override
        public void onBDSatelliteChanged(ArrayList<SatelliteEntity> arrayList) {
        }

        @Override
        public void onBDSatelliteFIX(int i) {
        }

        @Override
        public void onBDSatelliteLocating() {
        }

        @Override
        public void onSpeed(double v) {
            mSpeed = v;
        }
    };

    /**
     * 实时更新文本内容
     *
     * @param location
     */
    private void updateView(BDLocation location) {
        if (location != null) {
            if(location.getLon() <1 || location.getLat() <1){
                return;
            }
            GPSInfo.setLongitude(location.getLon());
            GPSInfo.setLatitude(location.getLat());
            SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmss");
            if(mInstance != null){
                Date date = mInstance.getLastUTCDateTime();
                if(date != null){
                    String time = format.format(date);
                    GPSInfo.setGPSTime(time);
                }
            }
            GPSInfo.setSpeed(mSpeed);
            GPSInfo.setMaxSpeed(mSpeed);
            GPSInfo.setAltitude(location.getAltitude());
            if(IUtil.mDistance){
                IUtil.Distance += getLong(location.getLat(),location.getLon());
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        mInstance.addTestBDRawDataListener(null);
//        mInstance.setClosePort();
    }

}
