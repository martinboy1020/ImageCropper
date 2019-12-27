package com.martinboy.imagecropper.utils;

import com.martinboy.imagecropper.BuildConfig;

public class LogUtils {

    static final boolean LOG = BuildConfig.DEBUG;

    public static void i(String tag, String string) {
        if (LOG) android.util.Log.i(tag, string);
    }
    public static void e(String tag, String string) {
        if (LOG) android.util.Log.e(tag, string);
    }
    public static void e(String tag, String msg, Throwable tr) {
        if (LOG) android.util.Log.e(tag, msg, tr);
    }
    public static void d(String tag, String string) {
        if (LOG) android.util.Log.d(tag, string);
    }
    public static void v(String tag, String string) {
        if (LOG) android.util.Log.v(tag, string);
    }
    public static void w(String tag, String string) {
        if (LOG) android.util.Log.w(tag, string);
    }

}
