package com.gudong.appkit.ui.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gudong.appkit.R;
import com.gudong.appkit.dao.AppMetaCompat;

import name.gudong.viewcontroller.ViewController;


public class AppBasicViewController extends ViewController<AppMetaCompat> {

    private android.widget.ImageView ivIcon;
    private android.widget.TextView tvName;
    private android.widget.TextView tvVersion;
    private android.widget.TextView tvVersionCode;
    private android.widget.TextView tvPackage;
    private TextView tvMaxSdkVersion;
    private TextView tvMinSdkVersion;
    private TextView tvTargetSdkVersion;
    private TextView tvInstallLocation;

    public AppBasicViewController(Context context) {
        super(context);
    }

    @Override
    protected int resLayoutId() {
        return R.layout.vc_layout_app_basic;
    }

    @Override
    protected void onCreatedView(View view) {
        ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
        tvName = (TextView) view.findViewById(R.id.tv_name);
        tvPackage = (TextView) view.findViewById(R.id.tv_package);
        tvVersion = (TextView) view.findViewById(R.id.tv_version);
        tvVersionCode = (TextView) view.findViewById(R.id.tv_version_code);

        tvMaxSdkVersion = (TextView) view.findViewById(R.id.tv_maxSdkVersion);
        tvMinSdkVersion = (TextView) view.findViewById(R.id.tv_minSdkVersion);
        tvTargetSdkVersion = (TextView) view.findViewById(R.id.tv_targetSdkVersion);
        tvInstallLocation = (TextView) view.findViewById(R.id.tv_installLocation);
    }

    @Override
    protected void onBindView(AppMetaCompat apkMeta) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(apkMeta.appEntity.getAppIconData(),0,apkMeta.appEntity.getAppIconData().length);
        ivIcon.setImageBitmap(bitmap);
        tvName.setText(apkMeta.appEntity.getAppName());

        tvVersion.setText(apkMeta.appEntity.getVersionName());
        tvPackage.setText(apkMeta.appEntity.getPackageName());

        tvVersionCode.setText("versionCode: "+apkMeta.appEntity.getVersionCode());
        if(TextUtils.isEmpty(apkMeta.maxSdkVersion)){
            tvMaxSdkVersion.setVisibility(View.GONE);
        }else{
            tvMaxSdkVersion.setText("maxSdkVersion: "+apkMeta.maxSdkVersion);
        }
        tvMinSdkVersion.setText("minSdkVersion: "+apkMeta.minSdkVersion);
        tvTargetSdkVersion.setText("targetSdkVersion: "+apkMeta.targetSdkVersion);
        tvInstallLocation.setText("installLocation: "+apkMeta.installLocation);
    }

}
