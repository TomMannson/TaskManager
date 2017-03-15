package pl.tommmannson.taskqueue.utils;


import android.util.Log;

import pl.tommmannson.taskqueue.BuildConfig;


/**
 * Created by tomasz.krol on 2016-01-27.
 */

public class Logger {

    private static final String EXCEPTION_MESSAGE = "TaskQueue";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    public static void i(String tag, String msg) {
        if (DEBUG) {
            Log.i(tag, msg);
        }
    }

    public static void i(Class<?> clazz, String msg) {

        if (DEBUG) {
            Log.i(clazz.getName(), msg);
        }
    }

    public static void d(String tag, String msg) {
        if (DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void d(Class<?> clazz, String msg) {
        if (DEBUG) {
            Log.d(clazz.getName(), msg);
        }
    }

    public static void e(String tag, String msg) {
        if (DEBUG) {
            Log.e(tag, msg);
        }
    }

    public static void e(Class<?> clazz, String msg) {
        if (DEBUG) {
            Log.e(clazz.getName(), msg);
        }
    }

    public static void e(String tag, Throwable throwable) {
        e(tag, EXCEPTION_MESSAGE, throwable);
    }

    public static void e(Class<?> clazz, Throwable throwable) {
        e(clazz, EXCEPTION_MESSAGE, throwable);
    }

    public static void e(String tag, String msg, Throwable throwable) {
        if (DEBUG) {
            Log.e(tag, msg, throwable);
        }
    }

    public static void e(Class<?> clazz, String msg, Throwable throwable) {
        e(clazz.getName(), msg, throwable);
    }

    public static void v(String tag, String msg) {
        if (DEBUG) {
            Log.v(tag, msg);
        }
    }

    public static void v(Class<?> clazz, String msg) {
        if (DEBUG) {
            Log.v(clazz.getName(), msg);
        }
    }

    public static void w(String tag, String msg) {
        if (DEBUG) {
            Log.w(tag, msg);
        }
    }

    public static void w(Class<?> clazz, String msg) {
        if (DEBUG) {
            Log.w(clazz.getName(), msg);
        }
    }

    public static void i(boolean forceLog, String tag, String msg) {
        if (DEBUG || forceLog) {
            Log.i(tag, msg);
        }
    }

    public static void i(boolean forceLog, Class<?> clazz, String msg) {

        if (DEBUG || forceLog) {
            Log.i(clazz.getName(), msg);
        }
    }

    public static void d(boolean forceLog, String tag, String msg) {
        if (DEBUG || forceLog) {
            Log.d(tag, msg);
        }
    }

    public static void d(boolean forceLog, Class<?> clazz, String msg) {
        if (DEBUG || forceLog) {
            Log.d(clazz.getName(), msg);
        }
    }

    public static void e(boolean forceLog, String tag, String msg) {
        if (DEBUG || forceLog) {
            Log.e(tag, msg);
        }
    }

    public static void e(boolean forceLog, Class<?> clazz, String msg) {
        if (DEBUG || forceLog) {
            Log.e(clazz.getName(), msg);
        }
    }


    public static void e(boolean forceLog, String tag, String msg, Throwable throwable) {
        if (DEBUG || forceLog) {
            Log.e(tag, msg, throwable);
        }
    }

    public static void e(boolean forceLog, Class<?> clazz, String msg, Throwable throwable) {
        e(clazz.getName(), msg, throwable);
    }

    public static void v(boolean forceLog, String tag, String msg) {
        if (DEBUG || forceLog) {
            Log.v(tag, msg);
        }
    }

    public static void v(boolean forceLog, Class<?> clazz, String msg) {
        if (DEBUG || forceLog) {
            Log.v(clazz.getName(), msg);
        }
    }

    public static void w(boolean forceLog, String tag, String msg) {
        if (DEBUG || forceLog) {
            Log.w(tag, msg);
        }
    }

    public static void w(boolean forceLog, Class<?> clazz, String msg) {
        if (DEBUG || forceLog) {
            Log.w(clazz.getName(), msg);
        }
    }
}


