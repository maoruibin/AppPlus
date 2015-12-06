package com.gudong.appkit.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.gudong.appkit.App;
import com.gudong.appkit.R;
import com.gudong.appkit.dao.AppInfoEngine;
import com.gudong.appkit.entity.AppEntity;
import com.gudong.appkit.ui.base.BaseActivity;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.List;

public class SplashActivity extends BaseActivity {
    private AppInfoEngine mEngine;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.iv_icon).postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
                finish();
            }
        },2000);
        mEngine = new AppInfoEngine(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<AppEntity> list =  mEngine.getInstalledAppList();
                for(AppEntity entity:list){
                    if(!isThisPackageExist(entity.getPackageName())){
                        App.sDb.insert(entity);
                    }
                }
            }
        }).start();
    }

    /**
     * check packname has exist in db
     * @param packname
     * @return if exist return true else return false
     */
    private boolean isThisPackageExist(String packname){
        QueryBuilder queryBuilder = new QueryBuilder(AppEntity.class);
        queryBuilder = queryBuilder.whereEquals("packageName ",packname);
        return App.sDb.query(queryBuilder).size()>0;
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_splash;
    }


}
