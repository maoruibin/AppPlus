/*
 *     Copyright (c) 2015 GuDong
 *
 *     Permission is hereby granted, free of charge, to any person obtaining a copy
 *     of this software and associated documentation files (the "Software"), to deal
 *     in the Software without restriction, including without limitation the rights
 *     to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *     copies of the Software, and to permit persons to whom the Software is
 *     furnished to do so, subject to the following conditions:
 *
 *     The above copyright notice and this permission notice shall be included in all
 *     copies or substantial portions of the Software.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *     IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *     FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *     AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *     LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *     OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *     SOFTWARE.
 */

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

    public static void d(String tag,String message){
        log(Log.DEBUG,tag, message);
    }

    public static void w(String tag,String message){
        log(Log.WARN,tag, message);
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
            case Log.ERROR:
                Log.e(finalTag,msg);
                break;
            case Log.WARN:
                Log.w(finalTag,msg);
                break;
            case Log.DEBUG:
                Log.d(finalTag,msg);
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
