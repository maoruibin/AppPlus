package com.gudong.appkit;

import android.app.Application;
import android.content.Context;

import com.litesuits.orm.LiteOrm;
import com.squareup.leakcanary.LeakCanary;

/**
 * 应用程序入口
 * Created by mao on 7/16/15.
 */
public class App extends Application {
    private static final String DB_NAME = "appplus.db";
    public static LiteOrm sDb;
    public static Context sContext;
    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
        sDb = LiteOrm.newSingleInstance(this, DB_NAME);
        sContext = this;
    }
}
