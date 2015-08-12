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
     * 最近列表是否显示App+
     * @param context
     * @return
     */
    public static boolean isShowSelf(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.switch_preference_show_self_key), false);
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
     * 获得品牌名称
     * @return
     */
    public static String getBrand(){
        return android.os.Build.BRAND;
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

    public static float getDensity(Context context){
        float scale = context.getResources().getDisplayMetrics().density;
        return scale;
    }

    public static int convertDiptoPix(Context context,int dip){
        float scale = getDensity(context);
        return (int) (dip * scale + 0.5f);
    }

    // -------------------    SharePreference Util    -------------------  //

    public static void putIntPreference(Context context,String key,int value){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt(key, value).commit();
    }

    public static int getIntPreference(Context context,String key,int def){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(key, def);
    }

    public static void putBooleanPreference(Context context,String key,boolean value){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(key, value).commit();
    }

    public static boolean getBooleanPreference(Context context,String key){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(key,false);
    }

    public static class Setting{
        public static void setDoNotShowPointForSumBug(Context context){
            putBooleanPreference(context,"do_not_show_point",true);
        }

        /**
         *
         * @param context
         * @return def retrun false
         */
        public static boolean isNotShowPointForSumBug(Context context){
            return getBooleanPreference(context,"do_not_show_point");
        }
    }
}

