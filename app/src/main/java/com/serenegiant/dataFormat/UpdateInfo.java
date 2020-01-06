package com.serenegiant.dataFormat;

import android.app.ProgressDialog;
import android.util.Xml;

import com.serenegiant.AppConfig;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2016-10-23.
 */
public class UpdateInfo {

    /*
 * 用pull解析器解析服务器返回的xml文件 (xml封装了版本号)
 */
    public static UpdateInfo getUpdataInfo(InputStream is) throws Exception{
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(is, "utf-8");//设置解析的数据源
        int type = parser.getEventType();
        UpdateInfo info = new UpdateInfo();//实体
        while(type != XmlPullParser.END_DOCUMENT ){
            switch (type) {
                case XmlPullParser.START_TAG:
                    if ("version_code".equals(parser.getName()))
                        info.setVersionCode(Integer.valueOf(parser.nextText()));
                    if("version_name".equals(parser.getName())){
                        info.setVersionName(parser.nextText());	//获取版本号
                    }else if ("url".equals(parser.getName())){
                        info.setUrl(parser.nextText());	//获取要升级的APK文件
                    }else if ("description".equals(parser.getName())){
                        info.setDescription(parser.nextText());	//获取该文件的信息
                    }
                    break;
            }
            type = parser.next();
        }
        return info;
    }

    public static File getFileFromServer(String path, ProgressDialog pd) throws Exception{
        //如果相等的话表示当前的sdcard挂载在手机上并且是可用的
            URL url = new URL(path);
            HttpURLConnection conn =  (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            //获取到文件的大小
            pd.setMax(conn.getContentLength());
            InputStream is = conn.getInputStream();
            File dir = new File(AppConfig.UPDATE_FILE_CACHE_DIRECTORY);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(AppConfig.UPDATE_FILE_CACHE_DIRECTORY, "release.apk");
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len ;
            int total=0;
            while((len =bis.read(buffer))!=-1){
                fos.write(buffer, 0, len);
                total+= len;
                //获取当前下载量
                pd.setProgress(total);
            }
            fos.close();
            bis.close();
            is.close();
            return file;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    private int versionCode;
    private String versionName;
    private String url;
    private String description;
}