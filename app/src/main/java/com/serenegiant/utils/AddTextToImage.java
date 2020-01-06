package com.serenegiant.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.serenegiant.AppConfig;
import com.serenegiant.dataFormat.PhotoPrintInfo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2016-09-03.
 */
public class AddTextToImage {
    static final String TAG = "AddTextToImage";
    static final boolean DEBUG = AppConfig.DEBUG_EN;
    public static Bitmap scaleWithWH(Bitmap src, double w, double h) {
        if (w == 0 || h == 0 || src == null) {
            return src;
        } else {
            // 记录src的宽高
            int width = src.getWidth();
            int height = src.getHeight();
            // 创建一个matrix容器
            Matrix matrix = new Matrix();
            // 计算缩放比例
            float scaleWidth = (float) (w / width);
            float scaleHeight = (float) (h / height);
            // 开始缩放
            matrix.postScale(scaleWidth, scaleHeight);
            // 创建缩放后的图片
            return Bitmap.createBitmap(src, 0, 0, width, height, matrix, true);
        }
    }

    public static Bitmap drawTextToBitmap(String filePath, String identify, String deviceID, String location, String speed, String datetime) {
//        Bitmap bitmap = BitmapFactory.decodeResource(resources, gResId);
        File file = new File(filePath);
        if(!file.exists())
        {
            return null;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        bitmap = scaleWithWH(bitmap, 640, 480);
        Bitmap.Config bitmapConfig = bitmap.getConfig();
        // set default bitmap config if none
        if(bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);
        // new antialised Paint
        Paint paint = new Paint(Paint.FAKE_BOLD_TEXT_FLAG);
        // text color - #3D3D3D
        paint.setColor(Color.WHITE);
        paint.setTextSize((int) (22));
//        paint.setDither(true); //获取跟清晰的图像采样
//        paint.setFilterBitmap(true);//过滤一些
        paint.setStrokeWidth(4);                                    //设置描边宽度
        paint.setStyle(Paint.Style.FILL_AND_STROKE); 							//对文字只描边

        Paint paint2 = new Paint(Paint.FAKE_BOLD_TEXT_FLAG);
        //填充空心内容
        paint2.setColor(Color.RED);
        paint2.setTextSize((int) (22));
//        paint.setDither(true); //获取跟清晰的图像采样
//        paint.setFilterBitmap(true);//过滤一些
        paint2.setStyle(Paint.Style.FILL_AND_STROKE);
        paint2.setStrokeWidth(1);                                    //设置描边宽度

        Rect bounds = new Rect();
        int x ;
        int y;

        x = 8;
        y = 8;
        paint.getTextBounds(identify, 0, identify.length(), bounds);
        canvas.drawText(identify, x, y+16, paint);
        canvas.drawText(identify, x, y+16, paint2);
        x = 8;
        y = 34;
        paint.getTextBounds(deviceID, 0, deviceID.length(), bounds);
        canvas.drawText(deviceID, x, y+16, paint);
        canvas.drawText(deviceID, x, y+16, paint2);
        x = 8;
        y = 424;
        paint.getTextBounds(location, 0, location.length(), bounds);
        canvas.drawText(location, x, y+16, paint);
        canvas.drawText(location, x, y+16, paint2);
        x = 200;
        y = 450;
        paint.getTextBounds(speed, 0, speed.length(), bounds);
        canvas.drawText(speed, x, y+16, paint);
        canvas.drawText(speed, x, y+16, paint2);
        x = 8;
        y = 450;
        paint.getTextBounds(datetime, 0, datetime.length(), bounds);
        canvas.drawText(datetime, x, y+16, paint);
        canvas.drawText(datetime, x, y+16, paint2);
        return bitmap;
    }

    public static void drawTextToBitmap(Bitmap bitmap, PhotoPrintInfo pf) {
//        Bitmap bitmap = BitmapFactory.decodeResource(resources, gResId);
        Canvas canvas = new Canvas(bitmap);
        // new antialised Paint
        Paint paint = new Paint(Paint.FAKE_BOLD_TEXT_FLAG);
        // text color - #3D3D3D
        paint.setColor(Color.WHITE);
        paint.setTextSize((int) (15));
        paint.setDither(true); //获取跟清晰的图像采样
//        paint.setFilterBitmap(true);//过滤一些
        paint.setStrokeWidth(3);                                    //设置描边宽度
        paint.setStyle(Paint.Style.FILL_AND_STROKE); 							//对文字只描边

        Paint paint2 = new Paint(Paint.FAKE_BOLD_TEXT_FLAG);
        //填充空心内容
        paint2.setColor(Color.RED);
        paint2.setTextSize((int) (15));
        paint.setDither(true); //获取跟清晰的图像采样
//        paint.setFilterBitmap(true);//过滤一些
        paint2.setStyle(Paint.Style.FILL_AND_STROKE);
        paint2.setStrokeWidth(1);                                    //设置描边宽度

        Rect bounds = new Rect();
        int x ;
        int y;

        x = 8;
        y = 8;
//        paint.getTextBounds(pf.getIdentify(), 0, pf.getIdentify().length(), bounds);
//        canvas.drawText(pf.getIdentify(), x, y+16, paint);
//        canvas.drawText(pf.getIdentify(), x, y+16, paint2);
        paint.getTextBounds(pf.getSchoolName(), 0, pf.getSchoolName().length(), bounds);
        canvas.drawText(pf.getSchoolName(), x, y+16, paint);
        canvas.drawText(pf.getSchoolName(), x, y+16, paint2);
        x = 8;
        y = 28;
        paint.getTextBounds(pf.getCarNum(), 0, pf.getCarNum().length(), bounds);
        canvas.drawText(pf.getCarNum(), x, y+16, paint);
        canvas.drawText(pf.getCarNum(), x, y+16, paint2);
        x = 100;
        y = 28;
        paint.getTextBounds(pf.getDeviceID(), 0, pf.getDeviceID().length(), bounds);
        canvas.drawText(pf.getDeviceID(), x, y+16, paint);
        canvas.drawText(pf.getDeviceID(), x, y+16, paint2);
        x = 8;
        y = 115;
        paint.getTextBounds(pf.getStudentName(), 0, pf.getStudentName().length(), bounds);
        canvas.drawText(pf.getStudentName(), x, y+16, paint);
        canvas.drawText(pf.getStudentName(), x, y+16, paint2);
        x = 8;
        y = 135;
        paint.getTextBounds(pf.getCoachName(), 0, pf.getCoachName().length(), bounds);
        canvas.drawText(pf.getCoachName(), x, y+16, paint);
        canvas.drawText(pf.getCoachName(), x, y+16, paint2);
        x = 8;
        y = 155;
        paint.getTextBounds(pf.getLocation(), 0, pf.getLocation().length(), bounds);
        canvas.drawText(pf.getLocation(), x, y+16, paint);
        canvas.drawText(pf.getLocation(), x, y+16, paint2);
        x = 8;
        y = 175;
        paint.getTextBounds(pf.getSpeed(), 0, pf.getSpeed().length(), bounds);
        canvas.drawText(pf.getSpeed(), x, y+16, paint);
        canvas.drawText(pf.getSpeed(), x, y+16, paint2);
        x = 8;
        y = 195;
        paint.getTextBounds(pf.getDatetime(), 0, pf.getDatetime().length(), bounds);
        canvas.drawText(pf.getDatetime(), x, y+16, paint);
        canvas.drawText(pf.getDatetime(), x, y+16, paint2);
        Log.d("TCP","执行水印完毕");
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
    }


    public static boolean addStuInfoToImage(String filePath, String identify, String deviceID, String location, String speed, String datetime)
    {
        Bitmap bmp = AddTextToImage.drawTextToBitmap(filePath, identify, deviceID, location, speed, datetime);
        if (bmp != null)
        {
            try {
                File f = new File(filePath);
                if (f.exists()) {
                    f.delete();
                }
                FileOutputStream out = new FileOutputStream(f);
                bmp.compress(Bitmap.CompressFormat.JPEG, 60, out);
                out.flush();
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    // 加密长考照片文件
    public static void encryptPhotoFile(File sourceFile,File targetFile)
    {
        if (!sourceFile.exists()) {
            return;
            }
        if (!sourceFile.isFile()) {
        return;
        }
        if (!sourceFile.canRead()) {
        return;
        }
        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }
        if (targetFile.exists()) {
            targetFile.delete();
        }
        // 新建文件输入流并对它进行缓冲
        try {
            FileInputStream input = new FileInputStream(sourceFile);
            BufferedInputStream inBuff = new BufferedInputStream(input);

            // 新建文件输出流并对它进行缓冲
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileOutputStream output = new FileOutputStream(targetFile);
            BufferedOutputStream outBuff = new BufferedOutputStream(output);

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            int sum = 0;
            while ((len = inBuff.read(b)) != -1) {
                for (int i = 0; i < len; i++) {
                    sum += (b[i] & 0xFF);
                }
                sum = sum & 0xFF;
                baos.write(b, 0, len);
            }
            inBuff.close();
            input.close();
            InputStream stream1 = new ByteArrayInputStream(baos.toByteArray());
            if (stream1.read(b, 0, 1) > 0) {
                if ((b[0] & 0xFF) == 0xFF) {
                    outBuff.write(sum & 0xFF);
                    outBuff.write(0xEE);
                }
            }
            while ((len = stream1.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
            //关闭流
            outBuff.close();
            output.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            if (DEBUG) Log.d(TAG, "encrypt examine photo file failure");
        }
    }

    private Bitmap createWaterMaskImage(Context gContext, Bitmap src, Bitmap watermark)
    {
        if (DEBUG) Log.d(TAG, "create a new bitmap");
        if (src == null)
        {
            return null;
        }
        int w = src.getWidth();
        int h = src.getHeight();
        int ww = watermark.getWidth();
        int wh = watermark.getHeight();
        // create the new blank bitmap
        Bitmap newb = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
        Canvas cv = new Canvas(newb);
        // draw src into
        cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src
        // draw watermark into
        cv.drawBitmap(watermark, 20, 20, null);// 在src的右下角画入水印
        // save all clip
        cv.save(Canvas.ALL_SAVE_FLAG);// 保存
        // store
        cv.restore();// 存储
        return newb;
    }
}
