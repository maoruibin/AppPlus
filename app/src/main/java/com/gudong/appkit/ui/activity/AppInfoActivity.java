package com.gudong.appkit.ui.activity;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.gudong.appkit.R;
import com.gudong.appkit.dao.AppEntity;
import com.gudong.appkit.dao.AppMetaCompat;
import com.gudong.appkit.dao.DataHelper;
import com.gudong.appkit.ui.controller.AppComponentViewController;
import com.gudong.appkit.ui.controller.AppBasicViewController;
import com.gudong.appkit.ui.controller.AppPermissionViewController;
import com.jaredrummler.apkparser.ApkParser;
import com.jaredrummler.apkparser.model.AndroidComponent;
import com.jaredrummler.apkparser.model.AndroidManifest;
import com.jaredrummler.apkparser.model.ApkMeta;
import com.jaredrummler.apkparser.model.UseFeature;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class AppInfoActivity extends BaseActivity {
    private LinearLayout llContainer;
    private AppBasicViewController basicViewController;
    private AppPermissionViewController permissionViewController;
    private AppComponentViewController activityViewController;
    private AppComponentViewController serviceViewController;
    private AppComponentViewController receiverViewController;
    private AppComponentViewController providerViewController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupToolBar(R.string.action_package, true);
        initView();
        basicViewController = new AppBasicViewController(this);
        permissionViewController = new AppPermissionViewController(this);
        activityViewController = new AppComponentViewController(this,AppComponentViewController.KEY_ACTIVITY);
        serviceViewController = new AppComponentViewController(this,AppComponentViewController.KEY_SERVICE);
        receiverViewController = new AppComponentViewController(this,AppComponentViewController.KEY_RECEIVER);
        providerViewController = new AppComponentViewController(this,AppComponentViewController.KEY_PROVIDER);

        basicViewController.attachRoot(llContainer);
        permissionViewController.attachRoot(llContainer);
        activityViewController.attachRoot(llContainer);

        String packageName = getIntent().getStringExtra("package");
        fillData(packageName);
    }

    private void fillData(String packageName) {
        PackageManager pm = getPackageManager();
        ApplicationInfo appInfo = null;
        try {
            appInfo = pm.getApplicationInfo(packageName, 0);
            AppEntity entity = DataHelper.getAppByPackageName(packageName);
            AppMetaCompat appMetaCompat =  new AppMetaCompat(entity);

            ApkParser apkParser = ApkParser.create(appInfo);
            ApkMeta meta = apkParser.getApkMeta();
            appMetaCompat.maxSdkVersion = meta.maxSdkVersion;
            appMetaCompat.minSdkVersion = meta.minSdkVersion;
            appMetaCompat.targetSdkVersion = meta.targetSdkVersion;
            appMetaCompat.installLocation = meta.installLocation;
            List<UseFeature> usesFeatures = meta.usesFeatures;
            List<String> requestedPermissions = meta.usesPermissions;

            AndroidManifest androidManifest = apkParser.getAndroidManifest();
            List<AndroidComponent> activities = androidManifest.activities;
            List<AndroidComponent> services = androidManifest.services;
            List<AndroidComponent> reveivers = androidManifest.receivers;
            List<AndroidComponent> providers = androidManifest.providers;

            basicViewController.fillData(appMetaCompat);
            permissionViewController.fillData(requestedPermissions);

            activityViewController.fillData(activities);
//            serviceViewController.fillData(services);
//            receiverViewController.fillData(reveivers);
//            providerViewController.fillData(providers);



        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_app_info;
    }

    private void initView() {
        llContainer = (LinearLayout) findViewById(R.id.ll_container);
    }
}
