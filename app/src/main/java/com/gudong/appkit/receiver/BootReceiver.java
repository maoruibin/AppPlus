package com.gudong.appkit.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.gudong.appkit.App;
import com.gudong.appkit.dao.AppEntity;
import com.gudong.appkit.dao.AppInfoEngine;
import com.gudong.appkit.event.EEvent;
import com.gudong.appkit.event.EventCenter;
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
            AppEntity uninstalledApp = new AppEntity(packageName);
            Logger.i("package remove "+packageName);
            App.sDb.delete(uninstalledApp);
            Bundle data = new Bundle();
            data.putParcelable("entity",uninstalledApp);
            EventCenter.getInstance().triggerEvent(EEvent.UNINSTALL_APPLICATION_FROM_SYSTEM,data);
        // receive install action , now we need add installed app to list
        }else if(Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())){
            AppEntity installedEntity = AppInfoEngine.getInstance().getAppByPackageName(packageName);
            if(installedEntity == null)return;
            App.sDb.delete(installedEntity);
            App.sDb.insert(installedEntity);
            Logger.i("package insert "+installedEntity.getAppName());
            Bundle data = new Bundle();
            data.putParcelable("entity",installedEntity);
            EventCenter.getInstance().triggerEvent(EEvent.INSTALL_APPLICATION_FROM_SYSTEM,data);
        }
    }
}
