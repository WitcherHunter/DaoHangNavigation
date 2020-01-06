package com.serenegiant.http;

import android.app.ProgressDialog;
import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zxj on 2017/8/30.
 */

public class OkHttpClientManager {

    private OkHttpClient mOkHttpClient;

    private OkHttpClientManager(){
        mOkHttpClient = new OkHttpClient();
    }

    //在静态内部类初始化数据时，JVM已经隐含的执行了同步。
    //利用了classloader的机制来保证初始化instance时只有一个线程，所以也是线程安全的
    private static class OkHttpClientManagerHolder{
         private static final OkHttpClientManager client = new OkHttpClientManager();
    }

    public static OkHttpClientManager getInstance(){
        return OkHttpClientManagerHolder.client; //只有在调用getInstance之后，内部类的实例才会真正被加载。这就实现了懒加载。
    }

    public interface ResultCallBack{
        void sucCallBack(Response response);
        void failCallBack(Exception e);
    }

    /**
     * 无参数的post请求
     * @param url
     * @param callBack
     */
    public void postAysn(String url, final ResultCallBack callBack){
        Request mRequest = new Request.Builder()
                .url(url)
                .build();
        mOkHttpClient.newCall(mRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                callBack.failCallBack(e);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                callBack.sucCallBack(response);
            }
        });
    }

    /**
     * 异步下载文件
     * @param url 下载路径
     * @param destFilePath 本地文件存储的文件夹
     * @param fileName 文件名
     * @param callBack
     */
    public void downAysn(final String url, final String destFilePath, final String fileName, final ProgressDialog pd, final ResultCallBack callBack){
        Request mRequest = new Request.Builder()
                .url(url)
                .build();
        mOkHttpClient.newCall(mRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                callBack.failCallBack(e);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                int total = 0;
                try
                {
                    //TODO TEMP 先写死，随后看下okhttp的下载进度设置
                    pd.setMax(17 * 1024 * 1024);
//                    long fileLength = response.body().contentLength();
                    is = response.body().byteStream();
                    File fileFa = new File(destFilePath);
                    if(!fileFa.exists()){
                        fileFa.mkdirs();
                    }
                    File file = new File(destFilePath, fileName);
                    fos = new FileOutputStream(file);
                    //is.read(byte[] b) : 在输入流中读取一定量的字节,并将其存储在b中，以整数形式返回实际读取的字节数。
                    while ((len = is.read(buf)) != -1)
                    {
                        fos.write(buf, 0, len);
                        total += len;
                        //获取当前下载量
                        pd.setProgress(total);
                    }
                    fos.flush();
                    Log.i("zxj", "success download callback");
                    callBack.sucCallBack(response);
                } catch (IOException e)
                {
                    callBack.failCallBack(e);
                } finally
                {
                    try
                    {
                        if (is != null) is.close();
                    } catch (IOException e)
                    {
                    }
                    try
                    {
                        if (fos != null) fos.close();
                    } catch (IOException e)
                    {
                    }
                }

            }
        });
    }

}
