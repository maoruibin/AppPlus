package com.gudong.appkit.utils;

import android.content.Context;
import android.content.res.Resources;

/**
 * Created by mao on 8/5/15.
 */
public class SystemBarUtils {
    private static final String STATUS_BAR_HEIGHT_RES_NAME = "status_bar_height";
    private static final String NAV_BAR_HEIGHT_RES_NAME = "navigation_bar_height";

    public static int getStatusBarHeight(Context context) {
        return getInternalDimensionSize(context.getResources(), STATUS_BAR_HEIGHT_RES_NAME);
    }

    private static int getInternalDimensionSize(Resources res, String key) {
        int result = 0;
        int resourceId = res.getIdentifier(key, "dimen", "android");
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
