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

package com.gudong.appkit;

import android.app.Application;
import android.content.Context;

import com.gudong.appkit.utils.logger.LogLevel;
import com.gudong.appkit.utils.logger.Logger;
import com.litesuits.orm.LiteOrm;

import jonathanfinerty.once.Once;

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
        sDb = LiteOrm.newSingleInstance(this, DB_NAME);
        sContext = this;
        Once.initialise(this);
        Logger.init("AppPlusLog").setLogLevel(BuildConfig.IS_DEBUG?LogLevel.FULL:LogLevel.NONE);
        sDb.setDebugged(BuildConfig.IS_DEBUG);
    }
}
