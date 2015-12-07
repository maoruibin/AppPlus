package com.gudong.appkit.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gudong.appkit.App;
import com.gudong.appkit.dao.AppEntity;

/**
 * Created by GuDong on 12/7/15 22:49.
 * Contact with 1252768410@qq.com.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if("android.intent.action.PACKAGE_REMOVED".equals(intent.getAction())){
            String packageName = intent.getDataString();
            if(packageName.contains("package:")){
                packageName = packageName.replace("package:","");
            }
            AppEntity entity = new AppEntity();
            entity.setPackageName(packageName);
            App.sDb.delete(entity);
        }
    }
}
