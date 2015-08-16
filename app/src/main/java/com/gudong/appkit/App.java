package com.gudong.appkit;

import android.app.Application;

import com.gudong.appkit.utils.ThemeUtils;
import com.gudong.appkit.utils.logger.LogLevel;
import com.gudong.appkit.utils.logger.Logger;

/**
 * Created by mao on 7/16/15.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ThemeUtils.setThemeChange(getApplicationContext(), false);
    }
}
