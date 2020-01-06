package com.serenegiant.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by zxj on 2017/8/8.
 */

public class MyToast {
    public static void show(Context ctx, String msg){
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }
}
