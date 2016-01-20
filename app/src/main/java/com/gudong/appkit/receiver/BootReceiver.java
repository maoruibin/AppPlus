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

package com.gudong.appkit.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.gudong.appkit.App;
import com.gudong.appkit.dao.AppEntity;
import com.gudong.appkit.dao.AppInfoEngine;
import com.gudong.appkit.dao.DataHelper;
import com.gudong.appkit.event.EEvent;
import com.gudong.appkit.event.RxBus;
import com.gudong.appkit.event.RxEvent;
import com.gudong.appkit.utils.logger.Logger;

/**
 * Created by GuDong on 12/7/15 22:49.
 * Contact with 1252768410@qq.com.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String packageName = intent.getDataString();
        if(TextUtils.isEmpty(packageName))return;
        if(packageName.contains("package:")){
            packageName = packageName.replace("package:","");
        }
        // receive uninstall action , now we need remove uninstalled app from list
        if(Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())){
//            AppEntity uninstalledApp = new AppEntity(packageName);
            AppEntity uninstalledApp = DataHelper.getAppByPackageName(packageName);
            Logger.i("package remove "+packageName);
            App.sDb.delete(uninstalledApp);
            Bundle data = new Bundle();
            data.putParcelable("entity",uninstalledApp);
            RxBus.getInstance().send(new RxEvent(EEvent.UNINSTALL_APPLICATION_FROM_SYSTEM,data));
        // receive install action , now we need add installed app to list
        }else if(Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())){
            AppEntity installedEntity = AppInfoEngine.getInstance().getAppByPackageName(packageName);
            if(installedEntity == null)return;
            App.sDb.delete(installedEntity);
            App.sDb.insert(installedEntity);
            Logger.i("package insert "+installedEntity.getAppName());
            Bundle data = new Bundle();
            data.putParcelable("entity",installedEntity);
            RxBus.getInstance().send(new RxEvent(EEvent.INSTALL_APPLICATION_FROM_SYSTEM,data));
        }
    }
}
