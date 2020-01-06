package com.serenegiant.services;

import android.content.Context;
import android.util.Log;

import com.amap.api.fence.GeoFence;
import com.amap.api.fence.GeoFenceClient;
import com.amap.api.fence.GeoFenceListener;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.DPoint;
import com.google.gson.Gson;
import com.navigation.timerterminal.R;
import com.serenegiant.entiy.GPSInfo;
import com.serenegiant.entiy.GeoFenceModel;
import com.serenegiant.entiy.WarningFlag;
import com.serenegiant.http.HttpConfig;
import com.serenegiant.http.OkHttpClientManager;
import com.serenegiant.utils.IUtil;
import com.serenegiant.utils.SharedPreferencesUtil;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NewLocationService implements GeoFenceListener, AMapLocationListener {
    public static final String GEOFENCE_BROADCAST_ACTION = "com.location.apis.geofencedemo.broadcast";

    private Context mContext;
    private AMapLocationClient mLocationClient;
    private String[] locationTypes;
    private GeoFenceClient geoFenceClient;

    public NewLocationService(Context context) {
        mContext = context;
        mLocationClient = new AMapLocationClient(context);
        mLocationClient.setLocationListener(this);
        mLocationClient.setLocationOption(getLocationOption());

        locationTypes = context.getResources().getStringArray(R.array.locationTypes);

        geoFenceClient = new GeoFenceClient(context);
        geoFenceClient.setActivateAction(GeoFenceClient.GEOFENCE_IN | GeoFenceClient.GEOFENCE_OUT);
        geoFenceClient.setGeoFenceListener(this);
        geoFenceClient.createPendingIntent(GEOFENCE_BROADCAST_ACTION);
    }

    /**
     * 封装定位选项
     *
     * @return
     */
    private AMapLocationClientOption getLocationOption() {
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setInterval(5000);
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //可通过设备传感器来计算速度，角度和海拔
        option.setSensorEnable(true);
        return option;
    }

    /**
     * 开始定位
     */
    public void startLocation(final String imei) {
        if (mLocationClient != null)
            mLocationClient.startLocation();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                getGenFencePoints(imei);
            }
        }, 0, 1000 * 60 * 2);
    }

    /**
     * 停止定位，停止围栏
     */
    public void stopLocation() {
        if (mLocationClient != null)
            mLocationClient.stopLocation();
        if (geoFenceClient != null)
            geoFenceClient.removeGeoFence();
    }

    /**
     * 创建电子围栏
     *
     * @param points 围栏边界点
     * @param id     自定义围栏id
     */
    public void createGeoFence(List<DPoint> points, String id) {
        geoFenceClient.addGeoFence(points, id);
    }

    /**
     * 调用接口获取电子围栏
     */
    public void getGenFencePoints(String imei) {
        OkHttpClientManager.getInstance()
                .postAysn(HttpConfig.locations + imei, new OkHttpClientManager.ResultCallBack() {
                    @Override
                    public void sucCallBack(Response response) {
                        System.out.println("NewLocationService: 电子围栏获取成功");
                        if (response.isSuccessful() && response.code() == 200) {
                            try {
                                GeoFenceModel model = new Gson().fromJson(response.body().string(), GeoFenceModel.class);
                                List<GeoFenceModel.DataBean> data = model.getData();
                                if (data != null && !data.isEmpty()) {
                                    for (GeoFenceModel.DataBean bean : data) {
                                        if (bean.getPolygon() != null && bean.getSubject() != 0)
                                            createGeoFence(separatePointString(bean.getPolygon()), String.valueOf(bean.getSubject()));
                                    }
                                } else {
                                    geoFenceClient.removeGeoFence();
                                    WarningFlag.clearOutOfRangeWarning();
                                    System.out.println("NewLocationService: 电子围栏已停用");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void failCallBack(Exception e) {
                        System.out.println("NewLocationService: 电子围栏获取失败");
                        WarningFlag.clearOutOfRangeWarning();
                    }
                });
    }

    /**
     * 拆分坐标点
     *
     * @param pointString 格式为"108.93628,34.27621;108.93879,34.27617;108.93881,34.27496;108.93627,34.27498"
     * @return 转换后的坐标list
     */
    private List<DPoint> separatePointString(String pointString) {
        List<DPoint> list = new ArrayList<>();
        String[] temp1 = pointString.split(";");
        for (String str : temp1) {
            String[] point = str.split(",");
            list.add(new DPoint(Double.parseDouble(point[1]), Double.parseDouble(point[0])));
        }
        return list;
    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
//                EventBus.getDefault().post(new LocationMessage(aMapLocation.getLatitude(), aMapLocation.getLongitude(), aMapLocation.getSpeed()));
                String log = "经度：" + aMapLocation.getLongitude() + ", 纬度：" + aMapLocation.getLatitude()
                        + ",速度：" + aMapLocation.getSpeed() + ",定位类型：" + locationTypes[aMapLocation.getLocationType()] + "\n";
//                System.out.println(log);

                if (aMapLocation.getLatitude() != 0 && aMapLocation.getLongitude() != 0)
                    WarningFlag.setWarning(WarningFlag.WARNING_NOT_LOCATED);
                else
                    WarningFlag.clearWarning(WarningFlag.WARNING_NOT_LOCATED);

                double[] location = gcj02towgs84(aMapLocation.getLongitude(), aMapLocation.getLatitude());
                double distance = getLong(location[1],location[0]);

                if (distance > 10000)
                    return;

                if (IUtil.mDistance)
                    IUtil.Distance += distance;

                GPSInfo.setSpeed(distance / 18);

                GPSInfo.setLatitude(location[1]);
                GPSInfo.setLongitude(location[0]);
                GPSInfo.setSpeed(aMapLocation.getSpeed());
                GPSInfo.setAltitude(aMapLocation.getAltitude());

                int maxSpeed = SharedPreferencesUtil.getInstance(mContext).getsharepreferencesInt(IUtil.MAX_SPEED);
                if (maxSpeed <= 0)
                    return;
                if (GPSInfo.getSpeed() > maxSpeed) {
                    WarningFlag.setWarning(WarningFlag.WARNING_OVER_SPEED);
                } else
                    WarningFlag.clearWarning(WarningFlag.WARNING_OVER_SPEED);

            } else {
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }

    @Override
    public void onGeoFenceCreateFinished(List<GeoFence> list, int errorCode, String errorMessage) {
        if (errorCode == GeoFence.ADDGEOFENCE_SUCCESS) {
            System.out.println("LocationService: 创建围栏成功");
        } else {
            System.out.println("LocationService: 创建围栏失败," + errorMessage);
        }
    }


    /**
     * 高德坐标系转大地坐标系
     */
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

    //计算距离
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
}
