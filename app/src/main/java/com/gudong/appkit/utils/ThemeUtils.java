package com.gudong.appkit.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.TypedValue;

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
//        boolean darkTheme = isDarkMode(mContext);
//        boolean isChange = darkTheme != darkMode;
//        darkMode = darkTheme;
//        return isChange;
        return Utils.getBooleanPreference(mContext,"pref_theme_change");
    }

    public int getCurrent(){
        if(darkMode){
            return R.style.Theme_AppPlus_Dark;
        }else{
            return R.style.Theme_AppPlus;
        }
    }

    public int getTheme(Context context){
        return Utils.getIntPreference(context,"theme",R.style.Theme_AppPlus);
    }

    public void setTheme(int theme){
        setThemeChange(mContext,true);
        Utils.putIntPreference(mContext, "theme", theme);
    }

    public void setThemePosition(int position){
        Utils.putIntPreference(mContext, "themePosition", position);
    }

    public int getThemePosition(){
        return Utils.getIntPreference(mContext,"themePosition",4);
    }

    public static void setThemeChange(Context context,boolean change){
        Utils.putBooleanPreference(context, "pref_theme_change", change);
    }

    /**
     * 判断当前主题是不是暗色主题
     * @return
     */
    public boolean isDarkMode() {
        return Utils.getBooleanPreference(mContext,"dark_mode");
    }

    public void setIsDarkTheme(boolean isDark){
        Utils.putBooleanPreference(mContext, "dark_mode", isDark);
        if (isDark){
            setTheme(R.style.Theme_AppPlus_Dark);
        }else{
            setTheme(R.style.Theme_AppPlus);
        }
    }

    public static int[][]themeArr(){
        return new int[][]{
                {R.style.LightRed,R.style.DarkRed},
                {R.style.LightPink,R.style.DarkPink},
                {R.style.LightPurple,R.style.DarkPurple},
                {R.style.LightDeepPurple,R.style.DarkDeepPurple},
                {R.style.LightIndigo,R.style.DarkIndigo},
                {R.style.LightBlue,R.style.DarkBlue},
                {R.style.LightLightBlue,R.style.DarkLightBlue},
                {R.style.LightCyan,R.style.DarkCyan},
                {R.style.LightTeal,R.style.DarkTeal},
                {R.style.LightGreen,R.style.DarkGreen},
                {R.style.LightLightGreen,R.style.DarkLightGreen},
                {R.style.LightLime,R.style.DarkLime},
                {R.style.LightYellow,R.style.DarkYellow},
                {R.style.LightAmber,R.style.DarkAmber},
                {R.style.LightOrange,R.style.DarkOrange},
                {R.style.LightDeepOrange,R.style.DarkDeepOrange},
                {R.style.LightBrown,R.style.DarkBrown},
                {R.style.LightGrey,R.style.DarkGrey},
                {R.style.LightBlueGrey,R.style.DarkBlueGrey},
        };
    }

}
