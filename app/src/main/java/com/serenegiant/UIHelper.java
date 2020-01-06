package com.serenegiant;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.navigation.timerterminal.R;
import com.serenegiant.view.CustomDialog;


public class UIHelper {
    /**
     * 初始加载
     */
    public final static int LISTVIEW_ACTION_INIT = 0x01;
    /**
     * 下拉刷新
     */
    public final static int LISTVIEW_ACTION_REFRESH = 0x02;
    /**
     * 滚动加载更多
     */
    public final static int LISTVIEW_ACTION_SCROLL = 0x03;
    public final static int LISTVIEW_ACTION_CHANGE_CATALOG = 0x04;

    public final static int LISTVIEW_DATA_MORE = 0x01;
    public final static int LISTVIEW_DATA_LOADING = 0x02;
    public final static int LISTVIEW_DATA_FULL = 0x03;
    public final static int LISTVIEW_DATA_EMPTY = 0x04;

    public final static int LISTVIEW_DATATYPE_ORDERLIST = 0x01;

    private static long exitTime = 0;
    private static long showSetTime = 0;// 显示设置时间间隔
    private static long showSetCount = 0;// 显示设置点击次数

    public static Toast getToast() {
        return mToast;
    }

    private static Toast mToast;
    /**
     * 全局web样式
     */
    public final static String WEB_STYLE = "<style> #artTitle1 {text-align:center;font-size:14px; color: #666; font-weight:normal; line-height:150%; float:left; width:100%; padding: 5px 0; margin:0 auto;}#artTitle {text-align:center; font-size:20px; color: #009; font-weight:normal; float:left; width:100%; padding:3px 0; margin:0 auto;}#artTitle3 {text-align:center; font-size:14px; color: #666; font-weight:normal; line-height:150%; float:left;width:100%;  padding: 5px 0; margin:0 auto;} p {color:#333;} a {color:#3E62A6;} img {max-width:310px;} "
            + "img.alignleft {float:left;max-width:120px;margin:0 10px 5px 0;border:1px solid #ccc;background:#fff;padding:2px;} "
            + "pre {font-size:9pt;line-height:12pt;font-family:Courier New,Arial;border:1px solid #ddd;border-left:5px solid #6CE26C;background:#f6f6f6;padding:5px;} "
            + "a.tag {font-size:15px;text-decoration:none;background-color:#bbd6f3;border-bottom:2px solid #3E6D8E;border-right:2px solid #7F9FB6;color:#284a7b;margin:2px 2px 2px 0;padding:2px 4px;white-space:nowrap;}</style>";

    /**
     * 弹出Toast消息
     *
     * @param msg
     */
    public static void ToastMessage(Context cont, String msg) {
        buildToast(cont, msg, Toast.LENGTH_SHORT);
        Log.i("UIHelper", "ToastMessage() msg=" + msg);
//        CustToast.MakeText(cont, msg, Toast.LENGTH_SHORT).show();
    }

    public static void ToastMessage(Context cont, int msg) {
        buildToast(cont, cont.getString(msg), Toast.LENGTH_SHORT);

//        CustToast.MakeText(cont, cont.getString(msg), Toast.LENGTH_SHORT).show();
    }

    public static void ToastMessage(Context cont, String msg, int time) {
        buildToast(cont, msg, time);
//        CustToast.MakeText(cont, msg, time).show();
    }


    public static void buildToast(Context context, String msg, int time) {

        if (mToast == null) {
            mToast = Toast.makeText(context, "Toast", Toast.LENGTH_SHORT);
        }

        mToast.setDuration(time);
        View view = mToast.getView();
        TextView text = (TextView) view.findViewById(R.id.ivory_toast_text);


        if (text == null) {
            View toastview = LayoutInflater.from(context).inflate(
                    R.layout.toast_view, null);
            text = (TextView) toastview.findViewById(R.id.ivory_toast_text);
            ViewGroup.LayoutParams params = text.getLayoutParams();


            Display display = AppManager.getAppManager().currentActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            params.width = size.x;


            text.setLayoutParams(params);
            mToast.setView(toastview);
            mToast.setGravity(Gravity.TOP, 0, dip2px(context, 0));

        }


        text.setText(msg);
        mToast.show();

    }


    public static int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (scale * dipValue + 0.5f);
    }





    /**
     * 退出程序
     *
     * @param cont
     */
    public static void Exit(final Context cont) {
        AlertDialog.Builder builder = new AlertDialog.Builder(cont);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle(R.string.app_menu_surelogout);
        builder.setPositiveButton(R.string.sure,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // 退出
                        AppManager.getAppManager().AppExit(cont);
                    }
                });
        builder.setNegativeButton(R.string.cancle,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();

    }


    /**
     * 打开浏览器
     *
     * @param context
     * @param url
     */
    public static void openBrowser(Context context, String url) {
        try {
            Uri uri = Uri.parse(url);
            Intent it = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(it);
        } catch (Exception e) {
            e.printStackTrace();
            ToastMessage(context, "无法浏览此网页", 500);
        }
    }

    /**
     * 获取webviewClient对象
     *
     * @return
     */
    public static WebViewClient getWebViewClient() {
        return new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                showUrlRedirect(view.getContext(), url);
                return true;
            }

        };
    }

    /**
     * url跳转
     *
     * @param context
     * @param url
     */
    public static void showUrlRedirect(Context context, String url) {

        openBrowser(context, url);
    }



    /**
     * 点击返回监听事件
     *
     * @param activity
     * @return
     */
    public static View.OnClickListener finish(final Activity activity) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                activity.finish();
            }
        };
    }

    /**
     * 清除app缓存
     *
     * @param activity
     */
    public static void clearAppCache(Activity activity) {
        final AppContext ac = (AppContext) activity.getApplication();
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    ToastMessage(ac, "缓存清除成功");
                } else {
                    ToastMessage(ac, "缓存清除失败");
                }
            }
        };
        new Thread() {
            public void run() {
                Message msg = new Message();
                try {
                    ac.clearAppCache();
                    msg.what = 1;
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = -1;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    /**
     * 显示提示消息
     *
     * @param act
     * @param text
     * @param duration
     * @param image
     */
    public static void showMessage(Activity act, String text, int duration,
                                   int image) {
        showMessage(act, text, duration, image, Gravity.CENTER);
    }

    /**
     * 显示提示消息
     *
     * @param act
     * @param text
     * @param duration
     * @param image
     * @param gravity
     */
    public static void showMessage(Activity act, String text, int duration,
                                   int image, int gravity) {
        Toast toast = Toast.makeText(act, "   " + text, duration);
        toast.setGravity(gravity, 0, 0);
        LinearLayout toastView = (LinearLayout) toast.getView();
        ImageView imageCodeProject = new ImageView(act);
        imageCodeProject.setImageResource(image);

        toastView.setOrientation(LinearLayout.HORIZONTAL);
        toastView.setGravity(Gravity.CENTER_VERTICAL);
        toastView.addView(imageCodeProject, 0);
        toast.show();
    }

    /**
     * 显示弹出框消息
     *
     * @param act
     * @param titleInt
     * @param message
     * @param iconInt
     */
    public static void alert(Activity act, int titleInt, String message,
                             int iconInt) {
        try {
            CustomDialog.Builder builder = new CustomDialog.Builder(act);
            builder.setTitle(titleInt);
            builder.setMessage(message);
            builder.setIcon(iconInt);

            builder.setNegativeButton(R.string.close, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示弹出框消息
     *
     * @param act
     * @param titleInt
     * @param messageInt
     * @param iconInt
     */
    public static void alert(Activity act, int titleInt, int messageInt,
                             int iconInt) {
        try {
            CustomDialog.Builder builder = new CustomDialog.Builder(act);
            builder.setTitle(titleInt);
            builder.setMessage(messageInt);
            builder.setIcon(iconInt);

            builder.setNegativeButton(R.string.close, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送App异常崩溃报告
     *
     * @param cont
     * @param crashReport
     */
    public static void sendAppCrashReport(final Context cont,
                                          final String crashReport) {
        AlertDialog.Builder builder = new AlertDialog.Builder(cont);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle(R.string.app_error);
        builder.setMessage(R.string.app_error_message);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

}
