package com.gudong.appkit.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gudong.appkit.R;
import com.gudong.appkit.dao.AppEntity;
import com.gudong.appkit.ui.control.NavigationManager;
import com.gudong.appkit.utils.ActionUtil;
import com.gudong.appkit.utils.Utils;
import com.gudong.appkit.utils.logger.Logger;
import com.umeng.analytics.MobclickAgent;


public class AppActivity extends BaseActivity implements View.OnClickListener {
    // View name of the header image. Used for activity scene transitions
    public static final String VIEW_NAME_HEADER_IMAGE = "detail:header:image";

    // View name of the header title. Used for activity scene transitions
    public static final String VIEW_NAME_HEADER_TITLE = "detail:header:title";

    public static final String EXTRA_APP_ENTITY = "APP_ENTITY";

    private ImageView mImageView;
    private TextView mTvAppName;
    private TextView mTvAppVersion;
    private TextView mTvOpen;
    private TextView mTvShare;
    private TextView mTvExport;
    private TextView mTvDetail;

    private AppEntity mAppEntity;


    @Override
    protected int initLayout() {
        return R.layout.activity_app;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupToolBar(R.string.empty, true);
        setupView();
        fillData();
        addListener();
    }

    private void fillData() {
        mAppEntity = (AppEntity) getIntent().getParcelableExtra(EXTRA_APP_ENTITY);
        Bitmap bitmap = BitmapFactory.decodeByteArray(mAppEntity.getAppIconData(), 0, mAppEntity.getAppIconData().length);
        mImageView.setImageBitmap(bitmap);
        mTvAppName.setText(mAppEntity.getAppName());
        mTvAppVersion.setText(mAppEntity.getVersionName());
    }

    private void setupView() {
        mImageView = (ImageView) findViewById(R.id.iv_icon);
        mTvAppName = (TextView) findViewById(android.R.id.text1);
        mTvAppVersion = (TextView) findViewById(android.R.id.text2);
        mTvOpen = (TextView) findViewById(R.id.tv_more);
        mTvExport = (TextView) findViewById(R.id.tv_export);
        mTvDetail = (TextView) findViewById(R.id.tv_detail);
        mTvShare = (TextView) findViewById(R.id.tv_share);

        ViewCompat.setTransitionName(mImageView, VIEW_NAME_HEADER_IMAGE);
    }

    private void addListener() {
        mTvOpen.setOnClickListener(this);
        mTvExport.setOnClickListener(this);
        mTvDetail.setOnClickListener(this);
        mTvShare.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_share:
                ActionUtil.shareApk(this,mAppEntity);
                MobclickAgent.onEvent(this, "action_share");
                break;
            case R.id.tv_export:
                ActionUtil.exportApk(this,mAppEntity);
                MobclickAgent.onEvent(this, "action_export");
                break;
            case R.id.tv_detail:
                NavigationManager.openAppDetail(this,mAppEntity.getPackageName());
                MobclickAgent.onEvent(this, "action_detail");
                break;
            case R.id.tv_more:
                showMoreDialog();
                MobclickAgent.onEvent(this, "action_more");
                break;
        }
    }

    private void showMoreDialog() {
        new AlertDialog.Builder(this).
                setItems(R.array.more_action, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                openApp();
                                break;
                            case 1:
                                installApp();
                                break;
                            case 2:
                                NavigationManager.gotoMarket(AppActivity.this,mAppEntity.getPackageName());
                                break;
                        }
                    }
                })
                .create()
                .show();
    }

    private void installApp() {
        if(Utils.isOwnApp(this,mAppEntity.getPackageName()))return;
        NavigationManager.uninstallApp(AppActivity.this,mAppEntity.getPackageName());
    }

    private void openApp() {
        if(Utils.isOwnApp(this,mAppEntity.getPackageName()))return;
        try {
            NavigationManager.openApp(AppActivity.this,mAppEntity.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(mTvShare,String.format(getString(R.string.fail_open_app),mAppEntity.getAppName()),Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == NavigationManager.UNINSTALL_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                onBackPressed();
            }else if(resultCode == RESULT_CANCELED){
                Logger.i("cancel");
            }
        }
    }
}
