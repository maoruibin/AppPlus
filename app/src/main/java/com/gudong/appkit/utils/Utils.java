package com.gudong.appkit.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import com.gudong.appkit.R;

/**
 * Created by mao on 7/21/15.
 */
public class Utils {
    public static void setCurrentVersion(Context context,String version){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString("current_version", version).commit();
    }

    public static String getLocalVersion(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString("current_version", "");
    }

    /**
     * 是否是开发者模式
     * @param context
     * @return
     */
    public static boolean isDevelopMode(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.switch_preference_develop_key), false);
    }
    /**
     * 发送邮件的头信息
     * @param activity
     * @return
     */
    public static String getLog(Activity activity) {
        PackageManager pm = activity.getPackageManager();
        StringBuffer sb = new StringBuffer();
        try {
            PackageInfo info = pm.getPackageInfo(activity.getPackageName(), 0);
            sb.append("versionName:" + info.versionName).append("\n");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String brand = android.os.Build.BRAND;
        String device = android.os.Build.DEVICE;
        String product = android.os.Build.PRODUCT;
        String hardware = android.os.Build.HARDWARE;
        String SDK = android.os.Build.VERSION.SDK;
        String androidv = android.os.Build.VERSION.RELEASE;
        sb.append("ANDROID:" + androidv).append("\n");
        sb.append("BRAND:" + brand).append("\n");
        sb.append("DEVICE:" + device).append("\n");
        sb.append("HARDWARE:" + hardware).append("\n");
        sb.append("SDK:" + SDK).append("\n");

        return sb.toString();
    }

    /**
     * 获取当前App版本号
     * @param context
     * @return
     */
    public static String getAppVersion(Context context){
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}

