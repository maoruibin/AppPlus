package com.gudong.appkit.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gudong.appkit.R;
import com.gudong.appkit.dao.AppEntity;
import com.gudong.appkit.ui.control.NavigationManager;
import com.gudong.appkit.view.CircularProgressDrawable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(new File(entity.getSrcPath())));
        intent.setType("application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(Intent.createChooser(intent, FormatUtil.warpChooserTitle(activity,entity.getAppName())));

    }

    /**
     * the first version for share apk
     * @param activity
     * @param entity
     */
    public static void shareApk_V1(Activity activity, AppEntity entity){
        PackageManager pm = activity.getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
        List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
        for (int i = 0; i < resInfo.size(); i++) {
            ResolveInfo ri = resInfo.get(i);
            String packageName = ri.activityInfo.packageName;
            if (packageName.contains("tencent") || packageName.contains("blue")) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));

                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(entity.getSrcPath())));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
            }
        }

        LabeledIntent[] extraIntents = intentList.toArray(new LabeledIntent[intentList.size()]);

        Intent openInChooser = Intent.createChooser(intentList.remove(0), FormatUtil.warpChooserTitle(activity,entity.getAppName()));
        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
        activity.startActivity(openInChooser);
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
        File exportParentFile = FileUtil.createDir(FileUtil.getSDPath(),FileUtil.KEY_EXPORT_DIR);

        String exportFileName = entity.getAppName() + ".apk";
        final File exportFile = new File(exportParentFile, exportFileName);
        String contentInfo = String.format(activity.getString(R.string.dialog_message_file_exist), exportFileName, exportFile.getParentFile().getAbsolutePath());
        if (exportFile.exists()) {
            new AlertDialog.Builder(activity)
                    .setTitle(R.string.title_export)
                    .setMessage(contentInfo)
                    .setPositiveButton(R.string.dialog_confirm_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            copyFile(activity,srcFile, exportFile);
                        }
                    })
                    .setNegativeButton(R.string.dialog_now_watch, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
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

    private static void copyFile(final Activity activity,File srcFile, final File exportFile) {
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_progress, null);
        ProgressBar progressBar = (ProgressBar) view.findViewById(android.R.id.progress);
        TextView textView = (TextView) view.findViewById(R.id.content);

        //改变Progress的背景为MaterialDesigner规范的样式
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            progressBar.setIndeterminateDrawable(new CircularProgressDrawable(Utils.getColorWarp(activity, R.color.colorAccent), activity.getResources().getDimension(R.dimen.loading_border_width)));
        }

        final AlertDialog progressDialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.title_export)
                .setView(view).create();
        //设置显示文字
        textView.setText(R.string.please_wait);

        new AsyncTask<File, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                progressDialog.dismiss();
                String contentInfo = String.format(activity.getString(R.string.dialog_message_export_finish), exportFile.getName(), exportFile.getParentFile().getAbsolutePath());
                new AlertDialog.Builder(activity)
                        .setTitle(R.string.title_export_finish)
                        .setMessage(contentInfo)
                        .setPositiveButton(R.string.dialog_confirm_watch, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavigationManager.browseFile(activity,exportFile.getParentFile());
                            }
                        })
                        .setNegativeButton(R.string.dialog_cancel_watch, null)
                        .show();
            }

            @Override
            protected Boolean doInBackground(File... params) {
                //导出速度太快了 给人工降个速 可以让用户看到有一个导出的进度条，这样更舒服点
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                File srcFile = params[0];
                File exportFile = params[1];
                try {
                    FileUtil.copyFileUsingFileChannels(srcFile, exportFile);
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        }.execute(srcFile, exportFile);
    }
}
