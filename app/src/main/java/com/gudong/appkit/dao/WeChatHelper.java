package com.gudong.appkit.dao;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.AlertDialog;

import com.gudong.appkit.R;
import com.gudong.appkit.event.EEvent;
import com.gudong.appkit.event.RxBus;
import com.gudong.appkit.event.RxEvent;
import com.gudong.appkit.ui.activity.BaseActivity;
import com.gudong.appkit.utils.DialogUtil;
import com.gudong.appkit.utils.FileUtil;
import com.gudong.appkit.utils.logger.Logger;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by GuDong on 7/19/16 23:11.
 * Contact with gudong.name@gmail.com.
 */
public class WeChatHelper {

    private Context mContext;

    public WeChatHelper(Context context) {
        mContext = context;
    }

    public void checkDownloadListDialog(boolean isCheckDataBeforeShowList) {
        List<File> list = listTencentDownloads();
        if (isCheckDataBeforeShowList) {
            if (list != null && !list.isEmpty()) {
                showDownloadListDialog(list);
            }
        } else {
            showDownloadListDialog(list);
        }
    }

    private void showDownloadListDialog(final List<File> listTencentDownloads) {
        if (listTencentDownloads != null) {
            if (listTencentDownloads.isEmpty()) {
                DialogUtil.showSinglePointDialog(mContext, mContext.getString(R.string.dialog_point_no_file));
            } else {
                CharSequence[] nameList = new CharSequence[listTencentDownloads.size()];
                boolean[] nameListCheckInit = new boolean[listTencentDownloads.size()];


                for (int i = 0; i < listTencentDownloads.size(); i++) {
                    nameList[i] = listTencentDownloads.get(i).getName();
                    nameListCheckInit[i] = true;
                }
                final boolean[] nameListCheckByUser = nameListCheckInit;
                new AlertDialog.Builder(mContext)
                        .setTitle(R.string.title_prepare_move)
                        .setPositiveButton(mContext.getString(R.string.start_copy_and_rename), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                new AlertDialog.Builder(mContext)
                                        .setTitle(R.string.title_point)
                                        .setMessage(mContext.getString(R.string.dialog_message_copy_and_rename))
                                        .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                moveOrCopyFileList(nameListCheckByUser, listTencentDownloads,true);
                                                MobclickAgent.onEvent(mContext, "moveCopy");
                                            }
                                        })
                                        .setNegativeButton(R.string.dialog_cancel, null)
                                        .setNeutralButton(mContext.getString(R.string.copy_with_delete), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                moveOrCopyFileList(nameListCheckByUser, listTencentDownloads,false);
                                                MobclickAgent.onEvent(mContext, "moveDelete");
                                            }
                                        })
                                        .show();

                            }
                        })
                        .setNegativeButton(R.string.dialog_cancel, null)
                        .setMultiChoiceItems(nameList, nameListCheckInit, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                nameListCheckByUser[which] = isChecked;
                                Logger.i("which " + which + " isChecked " + isChecked);
                            }
                        })
                        .show();
            }
        } else {
            DialogUtil.showSinglePointDialog(mContext, mContext.getString(R.string.file_check_wechat));
        }

    }

    private void moveOrCopyFileList(boolean[] nameListCheckByUser, List<File> listTencentDownloads,boolean isKeepSource) {
        if (operateFileList(nameListCheckByUser, listTencentDownloads,isKeepSource)) {
            new AlertDialog.Builder(mContext)
                    .setTitle(R.string.title_point)
                    .setMessage(mContext.getString(R.string.dialog_message_copy_success))
                    .setPositiveButton(R.string.dialog_open, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            RxBus.getInstance().send(new RxEvent(EEvent.OPEN_EXPORT_DIR));
                            BaseActivity activity = (BaseActivity) mContext;
                            activity.finish();
                        }
                    })
                    .setNegativeButton(R.string.dialog_cancel, null)
                    .show();
        }
    }

    private boolean operateFileList(boolean[] nameListCheckByUser, List<File> listTencentDownloads,boolean isKeepSource) {
        for (int i = 0; i < nameListCheckByUser.length; i++) {
            if (nameListCheckByUser[i]) {
                File selectFile = listTencentDownloads.get(i);
                String selectFileName = selectFile.getName();
                File exportParentFile = FileUtil.createDir(FileUtil.getSDPath(), FileUtil.KEY_EXPORT_DIR);
                String newName = selectFileName.substring(0, selectFileName.lastIndexOf("."));
                final File exportFile = new File(exportParentFile, newName);
                try {
                    FileUtil.copyFileUsingFileChannels(selectFile, exportFile);
                    if(!isKeepSource){
                        selectFile.delete();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }

    public File getWeChatDownloadDir() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "tencent/MicroMsg/Download");
        }
        return null;
    }

    public List<File> listTencentDownloads() {
        File wechatDownload = getWeChatDownloadDir();
        if(wechatDownload == null){
            return null;
        }
        if (wechatDownload.exists()) {
            File[] originList = wechatDownload.listFiles();
            if(originList == null){
                return Collections.EMPTY_LIST;
            }
            List<File> apkList = new ArrayList<>();
            for (File file : originList) {
                if (file.getName().contains("apk")) {
                    apkList.add(file);
                }
            }
            return apkList;
        } else {
            return null;
        }
    }
}
