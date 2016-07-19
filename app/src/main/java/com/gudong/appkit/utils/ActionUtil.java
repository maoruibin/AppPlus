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

package com.gudong.appkit.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gudong.appkit.R;
import com.gudong.appkit.dao.AppEntity;
import com.gudong.appkit.event.EEvent;
import com.gudong.appkit.event.RxBus;
import com.gudong.appkit.event.RxEvent;
import com.gudong.appkit.ui.activity.MainActivity;
import com.gudong.appkit.ui.control.NavigationManager;
import com.gudong.appkit.view.CircularProgressDrawable;

import java.io.File;
import java.io.IOException;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 常用操作工具类 如传送APK 导出APK等操作
 * Created by GuDong on 12/7/15 17:47.
 * Contact with 1252768410@qq.com.
 */
public class ActionUtil {
    /**
     * 传送安装包
     * @param entity
     */
    public static void shareApk(Activity activity, AppEntity entity) {
        final File srcFile = new File(entity.getSrcPath());
        if(!srcFile.exists()){
            Snackbar.make(activity.getWindow().getDecorView(),String.format(activity.getString(R.string.fail_share_app),entity.getAppName()),Snackbar.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(new File(entity.getSrcPath())));
        intent.setType("application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(Intent.createChooser(intent, FormatUtil.warpChooserTitle(activity,entity.getAppName())));
    }

    /**
     * 安装APK
     * @param entity
     */
    public static void installApp(Activity activity, AppEntity entity) {
        final File srcFile = new File(entity.getSrcPath());
        if(!srcFile.exists()){
            Snackbar.make(activity.getWindow().getDecorView(),String.format(activity.getString(R.string.fail_install_app),entity.getAppName()),Snackbar.LENGTH_LONG).show();
            return;
        }

        Intent mIntent = new Intent();
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mIntent.setAction(Intent.ACTION_VIEW);
        mIntent.setDataAndType(Uri.fromFile(srcFile),
                "application/vnd.android.package-archive");
        activity.startActivity(mIntent);
    }
    /**
     * export apk file
     * @param entity
     */
    public static void exportApk(final Activity activity,AppEntity entity) {
        //判断sd卡是否挂载
        if (!FileUtil.isSdCardOnMounted()) {
            DialogUtil.showSinglePointDialog(activity, activity.getString(R.string.dialog_message_no_sdcard));
            return;
        }

        final File srcFile = new File(entity.getSrcPath());
        if(!srcFile.exists()){
            Snackbar.make(activity.getWindow().getDecorView(),String.format(activity.getString(R.string.fail_export_app),entity.getAppName()),Snackbar.LENGTH_LONG).show();
            return;
        }
        File exportParentFile = FileUtil.createDir(FileUtil.getSDPath(),FileUtil.KEY_EXPORT_DIR);

        String exportFileName = entity.getAppName().concat("_").concat(entity.getVersionName()).concat(".apk");
        final File exportFile = new File(exportParentFile, exportFileName);
        String contentInfo = String.format(activity.getString(R.string.dialog_message_file_exist), exportFileName, exportFile.getParentFile().getAbsolutePath());
        if (exportFile.exists()) {
            new AlertDialog.Builder(activity)
                    .setTitle(R.string.title_point)
                    .setMessage(contentInfo)
                    .setPositiveButton(R.string.dialog_action_exist_not_override, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setNegativeButton(R.string.dialog_action_exist_override, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            copyFile(activity,srcFile, exportFile);
                        }
                    })
                    .setNeutralButton(R.string.dialog_action_exist_watch_now, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            NavigationManager.browseFile(activity,exportFile.getParentFile());
                        }
                    })
                    .show();
        } else {
            String pointInfo = String.format(activity.getString(R.string.dialog_message_export),entity.getAppName(),exportFile.getParentFile().getAbsolutePath());
            new AlertDialog.Builder(activity)
                    .setTitle(R.string.title_point)
                    .setMessage(pointInfo)
                    .setPositiveButton(R.string.dialog_confirm_export, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            copyFile(activity,srcFile, exportFile);
                        }
                    })
                    .setNegativeButton(R.string.dialog_cancel,null)
                    .show();

        }
    }

    public static void deleteApkFile(final Activity activity, final AppEntity entity){
        String pointInfo = String.format(activity.getString(R.string.dialog_message_delete),entity.getAppName());
        new AlertDialog.Builder(activity)
                .setTitle(R.string.title_point)
                .setMessage(pointInfo)
                .setPositiveButton(R.string.dialog_confirm_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(FileUtil.deleteExportedFile(entity)){
                            Bundle data = new Bundle();
                            data.putParcelable("entity",entity);
                            RxBus.getInstance().send(new RxEvent(EEvent.DELETE_SINGLE_EXPORT_FILE_SUC,data));
                        }else{
                            RxBus.getInstance().send(RxEvent.get(EEvent.DELETE_SINGLE_EXPORT_FILE_FAIL));
                        }
                    }
                })
                .setNegativeButton(R.string.dialog_cancel,null)
                .show();
    }

    private static void copyFile(final Activity activity,File srcFile, final File exportFile) {
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_progress, null);
        ProgressBar progressBar = (ProgressBar) view.findViewById(android.R.id.progress);
        TextView textView = (TextView) view.findViewById(R.id.content);

        //改变Progress的背景为MaterialDesigner规范的样式
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            progressBar.setIndeterminateDrawable(new CircularProgressDrawable(Utils.getColorWarp(activity, R.color.colorAccent), activity.getResources().getDimension(R.dimen.loading_border_width)));
        }

        final AlertDialog progressDialog = DialogUtil.getProgressDialog(activity,activity.getString(R.string.title_point),activity.getString(R.string.please_wait));
        progressDialog.show();
        try {
            FileUtil.copyFileUsingFileChannelsAsyn(srcFile, exportFile)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<Boolean>() {
                @Override
                public void call(Boolean aBoolean) {
                    progressDialog.dismiss();
                    String contentInfo = String.format(activity.getString(R.string.dialog_message_export_finish), exportFile.getName(), exportFile.getParentFile().getAbsolutePath());
                    new AlertDialog.Builder(activity)
                            .setTitle(R.string.title_export_finish)
                            .setMessage(contentInfo)
                            .setPositiveButton(R.string.dialog_confirm_watch, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    RxBus.getInstance().send(new RxEvent(EEvent.OPEN_EXPORT_DIR));

                                    if(!(activity instanceof MainActivity)){
                                        activity.finish();
                                    }
                                }
                            })
                            .setNegativeButton(R.string.dialog_cancel_watch, null)
                            .show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
