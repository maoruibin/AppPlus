package com.gudong.appkit.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.TypedValue;

import com.gudong.appkit.R;
import com.gudong.appkit.utils.logger.Logger;

/**
 * 主题相关的工具类
 * Created by mao on 7/16/15.
 */
public class ThemeUtils {
    private Context mContext;
    private int mCurrentTheme;
    public ThemeUtils(Context context){
        this.mContext = context;
        isChanged(); // invalidate stored booleans

    }

    /**
     * 获取当前主题对应的暗色调
     * @return
     */
    public int getThemePrimaryDarkColor(Context context){
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(R.attr.theme_color_dark, typedValue, true);
        return typedValue.data;
    }

    /**
     * 获取当前主题色对应色值
     * @param context
     * @return
     */
    public int getThemePrimaryColor(Context context){
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(R.attr.theme_color, typedValue, true);
        return typedValue.data;
    }

    public int getTheme(Context context){
        return Utils.getIntPreference(context,"theme",R.style.Theme_AppPlus);
    }

    public void setTheme(int theme){
        Utils.putIntPreference(mContext, "theme", theme);
    }

    /**
     * 记住用户选择的主题颜色对应的position
     * @param position 用户已选择position
     */
    public void setThemePosition(int position){
        Utils.putIntPreference(mContext, "themePosition", position);
    }

    public int getThemePosition(){
        return Utils.getIntPreference(mContext,"themePosition",4);
    }

    public boolean isChanged() {
        int currentTheme = getTheme(mContext);
        boolean isChange = mCurrentTheme != currentTheme;
        Logger.i(isChange?"主题改变了":" 主题未改变");
        mCurrentTheme = currentTheme;
        return isChange;
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

    /**
     * 自定义的主题数组，每一种颜色对应了夜间模式和日间模式
     * 目前夜间模式已经不做了，所以对应的主题在目前项目中是用不到的
     * @return
     */
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
