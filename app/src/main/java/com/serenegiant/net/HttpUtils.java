package com.serenegiant.net;

/**
 * Created by Administrator on 2016/10/28.
 */

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class HttpUtils {

    private static final String TAG = "HttpUtils";

    /**
     * 发送GET请求
     *
     * @param path
     *            请求路径
     * @param params
     *            请求参数
     * @param encoding
     *            编码
     * @return 请求是否成功
     * @throws Exception
     */
    public static String sendGETRequest(String path,
                                          Map<String, String> params, String encoding) throws Exception {
        StringBuilder url = new StringBuilder(path);
        url.append("?");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            url.append(entry.getKey()).append("=");
            url.append(URLEncoder.encode(entry.getValue(), encoding));// 编码
            url.append('&');
        }
        url.deleteCharAt(url.length() - 1);
        HttpURLConnection connection = (HttpURLConnection) new URL(
                url.toString()).openConnection();
        connection.setConnectTimeout(5000);
        connection.setRequestMethod("GET");
        int responCode = connection.getResponseCode();
        if (responCode == 200) {
            InputStream is = connection.getInputStream();
            byte[] ret = new byte[1024];
            int len = is.read(ret);
            if (is != null) {
                is.close();
            }
            return new String(ret, 0, len);
        }
        else
        {
            return "Error:"+responCode;
        }
    }

}