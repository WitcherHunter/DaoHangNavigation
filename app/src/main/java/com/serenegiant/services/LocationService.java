package com.serenegiant.services;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.amap.api.fence.GeoFenceClient;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.DPoint;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.serenegiant.entiy.GPSInfo;
import com.serenegiant.entiy.ObdDataModel;
import com.serenegiant.http.HttpConfig;
import com.serenegiant.http.OkHttpClientManager;
import com.serenegiant.net.CommonInfo;
import com.serenegiant.net.FenceDataStruct;
import com.serenegiant.ui.SetActivity;
import com.serenegiant.utils.IUtil;
import com.serenegiant.utils.SharedPreferencesUtil;
import com.serenegiant.utils.SoundManage;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by SXDH on 2017/10/18.
 */

public class LocationService extends Service{

    private String TAG = "LocationService";
    String location = "108.88469,34.199202;108.881374,34.195342;108.887683,34.193487;108.889936,34.197232";
    List<DPoint> list = new ArrayList<>();
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new MyAMapLocationListener();

    private AMapLocationClientOption option = new AMapLocationClientOption();
    private List<FenceDataStruct> myFenceData;

    /**
     * GCJ02 转换为 WGS84
     * @param lng
     * @param lat
     * @returns {*[]}
     */
    double x_PI = 3.14159265358979324 * 3000.0 / 180.0;
    double PI = 3.1415926535897932384626;
    double a = 6378245.0;
    double ee = 0.00669342162296594323;
    double[] dd = new double[2];
    private double[] gcj02towgs84(double lng, double lat) {
        double dlat = transformlat(lng - 105.0, lat - 35.0);
        double dlng = transformlng(lng - 105.0, lat - 35.0);
        double radlat = lat / 180.0 * PI;
        double magic = Math.sin(radlat);
        magic = 1 - ee * magic * magic;
        double sqrtmagic = Math.sqrt(magic);
        dlat = (dlat * 180.0) / ((a * (1 - ee)) / (magic * sqrtmagic) * PI);
        dlng = (dlng * 180.0) / (a / sqrtmagic * Math.cos(radlat) * PI);
        double mglat = lat + dlat;
        double mglng = lng + dlng;
        dd[0] = lng * 2 - mglng;
        dd[1] = lat * 2 - mglat;
        return dd;
    }
    private double transformlat(double lng, double lat) {
        double ret = (long) (-100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat + 0.2 * Math.sqrt(Math.abs(lng)));
        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lat * PI) + 40.0 * Math.sin(lat / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(lat / 12.0 * PI) + 320 * Math.sin(lat * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private double transformlng(double lng, double lat) {
        double ret = (long) (300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * Math.sqrt(Math.abs(lng)));
        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lng * PI) + 40.0 * Math.sin(lng / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(lng / 12.0 * PI) + 300.0 * Math.sin(lng / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    GeoFenceClient mGeoFenceClient;
    private int MaxSpeed;
    private double Speed;

    @Override
    public void onCreate() {
        super.onCreate();
        getLocations();

        Log.e("获取经度信息","启动");
        option.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Sport);
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        option.setOnceLocationLatest(true);
        option.setInterval(5000);
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        mLocationClient.setLocationOption(option);
        mLocationClient.startLocation();
//        //实例化地理围栏客户端
//        mGeoFenceClient = new GeoFenceClient(getApplicationContext());

//        mGeoFenceClient.setActivateAction(1|2|4);
        //添加围栏
//        mGeoFenceClient.addGeoFence(list,"5555");
//        mGeoFenceClient.setGeoFenceListener(fenceListenter);
//        IntentFilter filter = new IntentFilter(
//                ConnectivityManager.CONNECTIVITY_ACTION);
//        filter.addAction(GEOFENCE_BROADCAST_ACTION);
//        registerReceiver(mGeoFenceReceiver, filter);
    }

    /*
    * 进出围栏报警
    * */
    public static final long GPS_WARNING_OVER_ZONE = 0x00100000;
    /*
   * 超速报警
   * */
    public static final long GPS_WARNING_OVER_SPEED = 0x00000002;
    /*
   * 正常数据
   * */
    public static final long GPS_TRUE = 0x00000000;
    /*
    * 定位回调
    * */
    public class MyAMapLocationListener implements AMapLocationListener {
        @Override
        public void onLocationChanged(AMapLocation location) {
            double longitude = location.getLongitude();    //获取经度信息
            double latitude = location.getLatitude();    //获取纬度信息
            double[] lat = gcj02towgs84(longitude,latitude);
            double l = getLong(lat[1],lat[0]);

//            ObdDataModel.setTotalMiles((int) l);

            if(l > 10000){
                return;
            }
            GPSInfo.setLongitude(lat[0]);
            GPSInfo.setLatitude(lat[1]);

            Log.e("LocationService","longitude: " + longitude + ", latitude: " + latitude);
//            Log.e("获取纬度信息",""+latitude);
//            Log.e("获取经度信息",""+longitude);
//            Log.e("获取纬度信息",""+lat[1]);
//            Log.e("获取经度信息",""+lat[0]);


            /*
            * 距离
            * */
            if(IUtil.mDistance){
                IUtil.Distance += l;
            }
            /*
            * 速度  5秒 一定为， 除以18 是以km/h 存储
            * */
            GPSInfo.setSpeed(l/18);
            Speed = (int) (l/18); // KM/h

            MaxSpeed =  SharedPreferencesUtil.getInstance(getApplication()).getsharepreferencesInt(IUtil.MAX_SPEED);
            if (MaxSpeed < 1)
                IUtil.SpeedOut = false;
            else {
                if (Speed > MaxSpeed) {
                    IUtil.SpeedOut = true;
                } else {
                    IUtil.SpeedOut = false;
                }
            }
            /*
            * 退出教学取消报警
            * */
            if(myFenceData == null){
                getLocations();
                return;
            }
            /*
            * 退出教学取消报警
            * */
            if(!IUtil.OnClass){
                CommonInfo.setGpsWarningFlag(GPS_TRUE);
                return;
            }
            Log.e(TAG,"" +GPSInfo.getLatitude() + "&&&" +  GPSInfo.getLongitude());
            //34.2301620000,108.9296940000
//            String fenceID = findValidZone((long) (34.230162 * 1000000), (long) (108.929694 * 1000000));
            String fenceID = findValidZone((long) (GPSInfo.getLatitude() * 1000000), (long) (GPSInfo.getLongitude() * 1000000));
            if (fenceID == null) {
                IUtil.EnclosureOut = true;
//                SoundManage.ttsPlaySound(LocationService.this, "超出培训区域");
                Log.e(TAG, "超出培训区域");
                if (Speed <= MaxSpeed){ //超速判断 如果超速 或
                    CommonInfo.setGpsWarningFlag(GPS_WARNING_OVER_ZONE );
                }else{
                    CommonInfo.setGpsWarningFlag(GPS_WARNING_OVER_ZONE | GPS_WARNING_OVER_SPEED);
                }
            } else {
                IUtil.EnclosureOut = false;
                Log.e(TAG, "在培训区域内");
                if (Speed <= MaxSpeed){
                    CommonInfo.setGpsWarningFlag(GPS_TRUE );
                }else{
//                    CommonInfo.setGpsWarningFlag(GPS_TRUE);
                    CommonInfo.setGpsWarningFlag(GPS_WARNING_OVER_SPEED);
                }
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
        double mm = BDGPSService.GetDistance(lat,lon,this.lat,this.lon);
        this.lat = lat;
        this.lon = lon;
        return mm;
    }

    /*
    * 拆分电子围栏
    * */
    private Map<String,long[]> getDpoint(String locationd){
        Map<String,long[]> map = new HashMap<>();
        String[] ss = locationd.split(";");
        long[] listlo = new long[ss.length];
        long[] listla = new long[ss.length];
            for(int i = 0 ; i < ss.length ; i ++){
                String s = ss[i];
                String[] point = s.split(",");
//                list.add(new DPoint(Double.parseDouble(point[1]),Double.parseDouble(point[0])));
                double d1 = Double.parseDouble(point[1]);
                double d0 = Double.parseDouble(point[0]);

                long l1 = (long) (d1 * 1000000);
                long l0 = (long) (d0 * 1000000);
                listlo[i] = (l1);
                listla[i] = (l0);
            }
        map.put("listlo", listlo);
        map.put("listla", listla);

        return map;
    }


    /*
       * 判断点是否在围栏内
       * */
    private String findValidZone(long latitude, long longtitude) {
        for (int i = 0; i < myFenceData.size(); i++) {
            FenceDataStruct fencedata = myFenceData.get(i);
            if (fencedata.getMode() == 0 || fencedata.getMode() == CommonInfo.getCurItem()) {
                if (fencedata.getCnt() >= 3)//只少3个点
                {
                    long[] lat = fencedata.getLatitude();
                    long[] longt = fencedata.getLongtitude();
                    int pointsNumber;
                    pointsNumber = fencedata.getCnt();
                    int sum = 0;
                    long x0;
                    long y0;
                    long x1;
                    long y1;
                    long x;
                    long y;
                    y = latitude;
                    for (int index = 0; index < pointsNumber; index++) {
                        if (index == pointsNumber - 1) {
                            x0 = longt[index];
                            y0 = lat[index];
                            x1 = longt[0];
                            y1 = lat[0];
                        } else {
                            x0 = longt[index];
                            y0 = lat[index];
                            x1 = longt[index + 1];
                            y1 = lat[index + 1];
                        }
                        if (y0 != y1) {
                            if ((y0 > y && y1 < y) || (y0 < y && y1 > y)) {
                                if (longtitude < x0 && longtitude < x1) //相交
                                {
                                    sum++;
                                } else if (longtitude > x0 && longtitude > x1) //不相交
                                {
                                } else //进一步判定
                                {
                                    x = (long) ((float) (x0 - x1) / (float) (y0 - y1) * (y - y0)) + x0;
                                    if (x > longtitude)
                                        sum++;
                                }
                            }
                        }
                    }
                    if ((sum & 0x01) == 0x01) {
                        return fencedata.getId();
                    }
                }
            }
        }
        return null;
    }
    OkHttpClientManager mOkHttpClientManager;
    public void getLocations(){


        mOkHttpClientManager = OkHttpClientManager.getInstance();
        mOkHttpClientManager.postAysn(HttpConfig.locations + SetActivity.imei, new OkHttpClientManager.ResultCallBack() {
            @Override
            public void sucCallBack(Response response) {
                try {
                        Log.i(TAG, "" + response.toString());
                    JSONObject mResult = null;
                    try {
                        mResult = new JSONObject(response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    int errorcode = mResult.optInt("result");
                    Log.i(TAG,  mResult.toString());

                    String message = mResult.optString("message");
                    if(errorcode == 200){
                        if(myFenceData == null){
                            myFenceData = new ArrayList<FenceDataStruct>();
                        }
                        JSONArray array = (JSONArray) mResult.get("data");

                        for(int i = 0 ; i < array.length() ; i ++){
                            JSONObject object = (JSONObject) array.get(i);
                            FenceDataStruct fenceDataStruct = new FenceDataStruct();
                            fenceDataStruct.setId(object.getString("regionId"));
                            String s = object.getString("polygon");
                            Map<String,long[]> map = getDpoint(s);
                            fenceDataStruct.setLongtitude(map.get("listla"));
                            fenceDataStruct.setLatitude(map.get("listlo"));
                            fenceDataStruct.setCnt(map.get("listla").length);
                            myFenceData.add(fenceDataStruct);
                        }
                    } else {
                        Log.i("zxj", message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void failCallBack(Exception e) {
                Log.i(TAG, e.toString());
            }
        });

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

