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

package com.gudong.appkit.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;

import com.gudong.appkit.App;
import com.gudong.appkit.R;
import com.gudong.appkit.ui.control.ThemeControl;

import java.util.Locale;

/**
 * the util class for app
 * Created by mao on 7/21/15.
 */
public class Utils {
    private static final String KEY_IS_REFUSE_ANDROID_N_GET_RESENT_APP_LIST = "is_refuse_android_n_get_resent_app_list";
    private static final String KEY_DONT_SHOW_ANDROID_N_PERMISSION = "key_dont_show_android_n_get_resent_app_list";
    public static void setCurrentVersion(Context context, String version) {
        putStringPreference(context, "current_version", version);
    }

    public static String getLocalVersion(Context context) {
        return getStringPreference(context, "current_version", "");
    }

    public static void addAndroidNRefuseFlag(Context context) {
        putBooleanPreference(context, KEY_IS_REFUSE_ANDROID_N_GET_RESENT_APP_LIST, true);
    }

    public static boolean isResuleAndroidNPermission(Context context){
        return getBooleanPreference(context,KEY_IS_REFUSE_ANDROID_N_GET_RESENT_APP_LIST);
    }

    public static void addNotShowAndroidNRefuseFlag(Context context) {
        putBooleanPreference(context, KEY_DONT_SHOW_ANDROID_N_PERMISSION, true);
    }

    public static boolean dontShowAndroidNPermission(Context context){
        return getBooleanPreference(context,KEY_DONT_SHOW_ANDROID_N_PERMISSION);
    }

    /**
     * 获取主题强调色
     *
     * @param context
     * @return
     */
    public static int getAccentColor(Context context) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(R.attr.theme_accent_color, typedValue, true);
        return typedValue.data;
    }

    /**
     * 获取当前主题对应的暗色调
     *
     * @return
     */
    public static int getThemePrimaryDarkColor(Context context) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(R.attr.theme_color_dark, typedValue, true);
        return typedValue.data;
    }

    /**
     * 获取当前主题色对应色值
     *
     * @param context
     * @return
     */
    public static int getThemePrimaryColor(Context context) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(R.attr.theme_color, typedValue, true);
        return typedValue.data;
    }

    /**
     * 获取当前设置的主题
     *
     * @param context
     * @return
     */
    public static int getCurrentTheme(Context context) {
        int position = Utils.getIntPreference(context, "themePosition", 4);
        return ThemeControl.themeArr()[position];
    }

    /**
     * 获取color对应的int值
     *
     * @param context Activity
     * @param color   资源颜色id
     * @return 对应的int value
     */
    public static int getColorWarp(Activity context, @ColorRes int color) {
        return ContextCompat.getColor(context, color);
    }

    /**
     * running list is show AppPlus or not
     * @return return true if recent list view need show appplus
     */
    public static boolean isShowSelf() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(App.sContext);
        return prefs.getBoolean(App.sContext.getString(R.string.switch_preference_key_show_self), false);
    }

    /**
     * list item is brief mode or not
     * @return return true if brief mode else not
     */
    public static boolean isBriefMode() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(App.sContext);
        return prefs.getBoolean(App.sContext.getString(R.string.switch_preference_key_list_item_brief_mode), true);
    }

    public static boolean isAutoCheckWeChat() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(App.sContext);
        return prefs.getBoolean(App.sContext.getString(R.string.preference_key_wechat_helper_auto_check), false);
    }


    /**
     * get app version info
     *
     * @param context context
     * @return app version info if occur exception return unknow
     */
    public static String getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return context.getString(R.string.unknow);
        }
    }

    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static int convertDensityPix(Context context, int dip) {
        float scale = getDensity(context);
        return (int) (dip * scale + 0.5f);
    }

    public static boolean isOwnApp(Activity activity, String packageName) {
        return activity.getPackageName().equals(packageName);
    }

    // ------------------- Language Info ------------------------------------------------

    /**
     * check current language is chinese or not
     *
     * @return true if it is else return false
     */
    public static boolean isChineseLanguage() {
        String language = getLanguageEnv();
        if (language != null && (language.trim().equals("zh-CN") || language.trim().equals("zh-TW")))
            return true;
        else
            return false;
    }


    private static String getLanguageEnv() {
        Locale l = Locale.getDefault();
        String language = l.getLanguage();
        String country = l.getCountry().toLowerCase();
        if ("zh".equals(language)) {
            if ("cn".equals(country)) {
                language = "zh-CN";
            } else if ("tw".equals(country)) {
                language = "zh-TW";
            }
        } else if ("pt".equals(language)) {
            if ("br".equals(country)) {
                language = "pt-BR";
            } else if ("pt".equals(country)) {
                language = "pt-PT";
            }
        }
        return language;
    }

    // -------------------    SharePreference Util Begin   -------------------  //

    public static void removeKey(Context context, String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().remove(key).apply();
    }

    public static void putStringPreference(Context context, String key, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(key, value).apply();
    }

    public static String getStringPreference(Context context, String key, String def) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(key, def);
    }

    public static void putIntPreference(Context context, String key, int value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt(key, value).apply();
    }

    public static int getIntPreference(Context context, String key, int def) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(key, def);
    }

    public static void putBooleanPreference(Context context, String key, boolean value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(key, value).apply();
    }

    public static boolean getBooleanPreference(Context context, String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(key, false);
    }

    public static boolean isAndroidN(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }
}

