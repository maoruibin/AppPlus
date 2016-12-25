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

package com.gudong.appkit.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import com.gudong.appkit.App;
import com.gudong.appkit.R;
import com.gudong.appkit.dao.AppEntity;
import com.gudong.appkit.dao.AppInfoEngine;
import com.gudong.appkit.dao.AppStatus;
import com.gudong.appkit.dao.DataHelper;
import com.gudong.appkit.event.EEvent;
import com.gudong.appkit.event.RxBus;
import com.gudong.appkit.event.RxEvent;
import com.gudong.appkit.ui.control.NavigationManager;
import com.gudong.appkit.utils.FileUtil;
import com.gudong.appkit.utils.UStats;
import com.gudong.appkit.utils.Utils;
import com.gudong.appkit.utils.logger.Logger;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SplashActivity extends BaseActivity {
    private static final int GRUNT_RECENT_PREMISSION = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // make view full screen
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setStatusBarColorRes(R.color.colorPrimary);
        checkAndUpdateLocalDb();
        checkExportDirectoryIsChange();
        if(Utils.isAndroidN()){
            if (UStats.getUsageStatsList(this).isEmpty() && !Utils.dontShowAndroidNPermission(this)){
                new AlertDialog.Builder(this)
                        .setMessage("检测到您使用的是 Android N 设备，为了获取到最近运行的 App 列表，你需要授予 AppPlus 相关的权限，是否授予？")
                        .setPositiveButton(R.string.action_grunt, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                                startActivityForResult(intent,GRUNT_RECENT_PREMISSION);
                                MobclickAgent.onEvent(SplashActivity.this, "android_N_and_grant_permission");
                            }
                        })
                        .setNegativeButton(R.string.action_refuse, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Utils.addAndroidNRefuseFlag(SplashActivity.this);
                                gotoMainActivity(false);
                            }
                        })
                        .setNeutralButton(R.string.dialog_do_not_point, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Utils.addNotShowAndroidNRefuseFlag(SplashActivity.this);
                                gotoMainActivity(false);
                            }
                        })
                        .setCancelable(false)
                        .show();
            }else{
                gotoMainActivity(true);
            }
        }else{
            gotoMainActivity(true);
        }

    }

    private void gotoMainActivity(boolean withDelayTime) {
        if(withDelayTime){
            //delay 1500 mill and enter MainActivity
            Observable.timer(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                    .map(new Func1<Long, Object>() {
                        @Override
                        public Object call(Long aLong) {
                            NavigationManager.gotoMainActivityFromSplashView(SplashActivity.this);
                            return null;
                        }
                    })
                    .subscribe();
        }else{
            NavigationManager.gotoMainActivityFromSplashView(SplashActivity.this);
        }

    }

    /**
     * check local db data,because the installed App info will be changed or removed
     * so,we need check local data and update it before enter MainActivity
     */
    private void checkAndUpdateLocalDb(){
        AppInfoEngine.getInstance().getInstalledAppList()
                .subscribeOn(Schedulers.io())
                //将获取到的安装应用列表 appEntities 转换为单个的 AppEntity 对象
                .flatMap(new Func1<List<AppEntity>, Observable<AppEntity>>() {
                    @Override
                    public Observable<AppEntity> call(List<AppEntity> appEntities) {
                        List<AppEntity>listDB = App.sDb.query(AppEntity.class);
                        //
                        for(AppEntity entity : listDB) {
                            if (!appEntities.contains(entity)) {
                                App.sDb.delete(entity);
                                Logger.e("installed list has not " + entity.getAppName() + " now delete it in local db.");
                            }
                        }
                        return Observable.from(appEntities);
                    }
                })
                //对每个已安装的 AppEntity 对象与存储在本地的 AppEntity 对象做对比 看这个对象是不是有什么变化
                .map(new Func1<AppEntity, AppEntity>() {
                    @Override
                    public AppEntity call(AppEntity entity) {
                        return DataHelper.checkAndSetAppEntityStatus(entity);
                    }
                })
                .subscribe(new Action1<AppEntity>() {
                    @Override
                    public void call(AppEntity entity) {
                        int status = entity.getStatus();
                        if (status == AppStatus.CHANGE.ordinal()) {
                            AppEntity localChange = DataHelper.getAppByPackageName(entity.getPackageName());
                            entity.setId(localChange.getId());
                            if (App.sDb.update(entity) > 0) {
                                Logger.e("rx", entity.getAppName() + " has change now update success");
                            } else {
                                Logger.e("rx", entity.getAppName() + " has change but now update fail");
                            }
                        } else if (status == AppStatus.CREATE.ordinal()) {
                            App.sDb.insert(entity);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Logger.e("is error "+throwable.getMessage());
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        //NavigationManager.gotoMainActivityFromSplashView(SplashActivity.this);
                        RxBus.getInstance().send(RxEvent.get(EEvent.PREPARE_FOR_ALL_INSTALLED_APP_FINISH));
                        Logger.i("PREPARE_FOR_ALL_INSTALLED_APP_FINISH");
                    }
                });
    }

    /**
     * check the directory which used to store export apk file has some file ,if the old dir has
     * apk file ,move all file to new dir folder.
     * this change is begin with version 3.0, and the new folder name is AppPlus
     */
    private void checkExportDirectoryIsChange() {
        if (!FileUtil.isSdCardOnMounted()){
            return;
        }
        final File oldExportDir = new File(FileUtil.getSDPath(), FileUtil.KEY_EXPORT_DIR_OLDER);
        //user has not use older dir name,this condition is good , we need not deal
        if (!oldExportDir.exists()) {
            return;
        }
        final File[]files = oldExportDir.listFiles();
        if(files.length<=0){
            oldExportDir.delete();
            return;
        }

        final File nowExportDir = FileUtil.createDir(FileUtil.getSDPath(), FileUtil.KEY_EXPORT_DIR);
        Observable.from(files)
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<File>() {
                    @Override
                    public void call(File file) {
                        File dest = new File(nowExportDir,file.getName());
                        try {
                            FileUtil.copyFileUsingFileChannels(file,dest);
                            file.delete();
                            Logger.i("copy "+file.getName()+" finish");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        oldExportDir.delete();
                    }
                });
    }

    @Override
    protected int initLayout() {
        //splash layout is set by Theme in AndroidManifest file
        return -1;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GRUNT_RECENT_PREMISSION){
            gotoMainActivity(false);
        }
    }
}
