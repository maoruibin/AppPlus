package com.gudong.appkit.ui.control;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.TypedValue;

import com.gudong.appkit.R;
import com.gudong.appkit.utils.Utils;
import com.gudong.appkit.utils.logger.Logger;

/**
 * 主题控制类
 * Created by mao on 7/16/15.
 */
public class ThemeControl {
    private Context mContext;
    private int mCurrentTheme;
    public ThemeControl(Context context){
        this.mContext = context;
        isChanged(); // invalidate stored booleans

    }

    public int getTheme(Context context){
        return Utils.getIntPreference(context, "theme", R.style.Theme_AppPlus);
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
        mCurrentTheme = currentTheme;
        return isChange;
    }

    /**
     * 自定义的主题数组，每一种颜色对应了夜间模式和日间模式
     * 目前夜间模式已经不做了，所以对应的主题在目前项目中是用不到的
     * @return
     */
    public static int[]themeArr(){
        return new int[]{
                R.style.LightRed,
                R.style.LightPink,
                R.style.LightPurple,
                R.style.LightDeepPurple,
                R.style.LightIndigo,
                R.style.LightBlue,
                R.style.LightLightBlue,
                R.style.LightCyan,
                R.style.LightTeal,
                R.style.LightGreen,
                R.style.LightLightGreen,
                R.style.LightLime,
                R.style.LightYellow,
                R.style.LightAmber,
                R.style.LightOrange,
                R.style.LightDeepOrange,
                R.style.LightBrown,
                R.style.LightGrey,
                R.style.LightBlueGrey,
        };
    }

}
