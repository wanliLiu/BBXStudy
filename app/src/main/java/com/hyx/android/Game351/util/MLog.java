package com.hyx.android.Game351.util;

import android.util.Log;

public class MLog {

    /**
     * 是否开启全局打印日志
     */
    public static final boolean EnDebug = true;

    private static final String DEFAULT_TAG = "学英语";

    public static void i(String tag, String msg) {
        if (EnDebug)
            Log.i(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (EnDebug)
            Log.e(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (EnDebug)
            Log.d(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (EnDebug)
            Log.v(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (EnDebug)
            Log.w(tag, msg);
    }

    public static void i(String msg) {
        if (EnDebug)
            Log.i(DEFAULT_TAG, msg);
    }

    public static void e(String msg) {
        if (EnDebug)
            Log.e(DEFAULT_TAG, msg);
    }

    public static void d(String msg) {
        if (EnDebug)
            Log.d(DEFAULT_TAG, msg);
    }

    public static void v(String msg) {
        if (EnDebug)
            Log.v(DEFAULT_TAG, msg);
    }

    public static void w(String msg) {
        if (EnDebug)
            Log.w(DEFAULT_TAG, msg);
    }

}
