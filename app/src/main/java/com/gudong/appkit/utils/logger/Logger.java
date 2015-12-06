package com.gudong.appkit.utils.logger;

import android.text.TextUtils;
import android.util.Log;

/**
 * 日志输出类
 * Created by mao on 8/16/15.
 */
public class Logger {
    private static Setting setting;
    public static Setting init(String tag){
        if (tag == null) {
            throw new NullPointerException("tag may not be null");
        }
        if (tag.trim().length() == 0) {
            throw new IllegalStateException("tag may not be empty");
        }
        setting = Setting.getInstance();
        setting.setTag(tag);
        return setting;
    }


    public static void i(String message){
        log(Log.INFO, setting.getDefTag(), message);
    }

    public static void e(String message){
        log(Log.ERROR, setting.getDefTag(), message);
    }

    public static void i(String tag,String message){
        log(Log.INFO,tag, message);
    }

    public static void e(String tag,String message){
        log(Log.ERROR,tag, message);
    }

    private static synchronized void log(int logType, String tag,String msg) {
        if(setting == null){
            throw new NullPointerException("before use Logger ,please init Logger in Application and set param");
        }
        if (setting.getLevel() == LogLevel.NONE) {
            return;
        }
        String finalTag = formatTag(tag);
        switch (logType){
            case Log.INFO:
                Log.i(finalTag,msg);
                break;
        }
    }
    private static String formatTag(String tag) {
        if (!TextUtils.isEmpty(tag) && !TextUtils.equals(setting.getDefTag(), tag)) {
            return setting.getTag() + "-" + tag;
        }
        return setting.getTag();
    }
}
