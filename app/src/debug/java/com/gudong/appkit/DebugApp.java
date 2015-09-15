package com.gudong.appkit;

import com.gudong.appkit.utils.Utils;

/**
 * Created by mao on 8/16/15.
 */
public class DebugApp extends App {
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.isSetDebugMode(this, true);
    }
}
