package com.cjs.cjsbridge.tools;

import android.util.Log;


/**
 * 统一日志管理工具类
 * @author JasonChen
 * @email chenjunsen@outlook.com
 * @createTime 2020/8/31 0031 18:06
 */
public class L {
    /**
     * 全局日志开关
     */
    public static boolean isLog = true;

    /**
     * 全局日志标签前缀
     */
    private static final String TAG_PREFIX = "CJSBridge";

    public static void d(String tag, String msg) {
        if (isLog) {
            Log.d(TAG_PREFIX + "-" + tag, msg+"");
        }

    }

    public static void e(String tag, String msg) {
        if (isLog)
            Log.e(TAG_PREFIX + "-" + tag, msg+"");
    }

    public static void w(String tag, String msg) {
        if (isLog)
            Log.w(TAG_PREFIX + "-" + tag, msg+"");
    }

    public static void i(String tag, String msg) {
        if (isLog)
            Log.i(TAG_PREFIX + "-" + tag, msg+"");
    }

    public static void d(String msg) {
        if (isLog)
            Log.d(TAG_PREFIX, msg+"");
    }

    public static void e(String msg) {
        if (isLog)
            Log.e(TAG_PREFIX, msg+"");
    }

    public static void w(String msg) {
        if (isLog)
            Log.w(TAG_PREFIX, msg+"");
    }

    public static void i(String msg) {
        if (isLog)
            Log.i(TAG_PREFIX, msg+"");
    }
}
