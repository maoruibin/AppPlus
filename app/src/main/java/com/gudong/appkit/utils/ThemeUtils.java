package com.gudong.appkit.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.gudong.appkit.R;

/**
 * 主题相关的工具类
 * Created by mao on 7/16/15.
 */
public class ThemeUtils {
    private Context mContext;
    private boolean darkMode;

    public ThemeUtils(Context context){
        this.mContext = context;
        isChanged(); // invalidate stored booleans

    }

    public boolean isChanged() {
        boolean darkTheme = isDarkMode(mContext);
        boolean isChange = darkTheme != darkMode;
        darkMode = darkTheme;
        return isChange;
    }

    public int getCurrent(){
        if(darkMode){
            return R.style.Theme_AppPlusDark_NavStatusTranslucent;
        }else{
            return R.style.Theme_AppPlus_NavStatusTranslucent;
        }
    }

    public static void setThemeChange(Context context,boolean change){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean("pref_dark_theme_change",change).commit();
    }

    public static boolean isThemeChange(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("pref_dark_theme_change", false);
    }
    /**
     * 判断当前主题是不是暗色主题
     * @param context
     * @return
     */
    public static boolean isDarkMode(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("dark_mode", false);
    }

    public static void setTheme(Context context,boolean isDark){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean("dark_mode",isDark).commit();
    }
}
