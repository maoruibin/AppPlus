package com.gudong.appkit.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.gudong.appkit.App;
import com.gudong.appkit.R;
import com.gudong.appkit.dao.AppEntity;
import com.gudong.appkit.dao.AppInfoEngine;
import com.gudong.appkit.utils.FileUtil;
import com.gudong.appkit.utils.logger.Logger;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SplashActivity extends BaseActivity {
    private AppInfoEngine mEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.iv_icon).postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, 2000);
        mEngine = new AppInfoEngine(this);
        //TODO use RxJava
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<AppEntity> list = mEngine.getInstalledAppList();
                for (AppEntity entity : list) {
                    if (!isThisPackageExist(entity.getPackageName())) {
                        App.sDb.insert(entity);
                    }
                }
            }
        }).start();

        checkExportDirectoryIsChange();
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
        Logger.i("发现"+files.length+"个文件");
        final File nowExportDir = FileUtil.createDir(FileUtil.getSDPath(), FileUtil.KEY_EXPORT_DIR);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(File file:files){
                    File dest = new File(nowExportDir,file.getName());
                    try {
                        FileUtil.copyFileUsingFileChannels(file,dest);
                        file.delete();
                        Logger.i("拷贝文件"+file.getName()+"完成 删除文件");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Logger.i("拷贝所有文件完成 删除文件夹");
                oldExportDir.delete();
            }
        }).start();

    }

    /**
     * check packname has exist in db
     *
     * @param packname
     * @return if exist return true else return false
     */
    private boolean isThisPackageExist(String packname) {
        QueryBuilder queryBuilder = new QueryBuilder(AppEntity.class);
        queryBuilder = queryBuilder.whereEquals("packageName ", packname);
        return App.sDb.query(queryBuilder).size() > 0;
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_splash;
    }


}
