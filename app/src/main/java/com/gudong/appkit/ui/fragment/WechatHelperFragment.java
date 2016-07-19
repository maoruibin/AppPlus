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

package com.gudong.appkit.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;

import com.gudong.appkit.R;
import com.gudong.appkit.ui.activity.BaseActivity;
import com.gudong.appkit.utils.DialogUtil;
import com.gudong.appkit.utils.FileUtil;
import com.gudong.appkit.utils.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jonathanfinerty.once.Once;

/**
 * 微信助手页面
 */
public class WechatHelperFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
    private BaseActivity mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = (BaseActivity) getActivity();
        addPreferencesFromResource(R.xml.prefs_wechat_helper);

        //设置点击监听
        findPreference(getString(R.string.preference_key_wechat_helper)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.preference_key_open_wechat_download)).setOnPreferenceClickListener(this);

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        //用if判断 效率不会很好 待改善
        if (key.equals(getString(R.string.preference_key_wechat_helper))) {
            final String showWhatsNew = "showWhatsWeChatHelper";

            if (!Once.beenDone(Once.THIS_APP_VERSION, showWhatsNew)) {
                DialogUtil.showSinglePointDialog(getActivity(), mContext.getString(R.string.about_wechat_helper), mContext.getString(R.string.dialog_know), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Once.markDone(showWhatsNew);
                        showDownloadListDialog(listTencentDownloads());
                    }
                });
            }else{
                showDownloadListDialog(listTencentDownloads());
            }


        }
        if (key.equals(getString(R.string.preference_key_open_wechat_download))) {

        }
        return false;
    }

    private void showDownloadListDialog(final List<File> listTencentDownloads) {
        if(listTencentDownloads!=null){
            if(listTencentDownloads.isEmpty()){
                DialogUtil.showSinglePointDialog(getActivity(),"在你的微信下载目录没有发现任何安装包文件");
            }else{
                CharSequence[]nameList = new CharSequence[listTencentDownloads.size()];
                boolean[]nameListCheckInit = new boolean[listTencentDownloads.size()];


                for(int i = 0;i<listTencentDownloads.size();i++){
                    nameList[i] = listTencentDownloads.get(i).getName();
                    nameListCheckInit[i] = true;
                }
                final boolean[]nameListCheckByUser = nameListCheckInit;
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.title_prepare_move)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for(int i = 0;i<nameListCheckByUser.length;i++){
                                    if(nameListCheckByUser[i]){
                                        File selectFile = listTencentDownloads.get(i);
                                        String selectFileName = selectFile.getName();
                                        File exportParentFile = FileUtil.createDir(FileUtil.getSDPath(),FileUtil.KEY_EXPORT_DIR);
                                        String newName = selectFileName.substring(0,selectFileName.lastIndexOf("."));
                                        final File exportFile = new File(exportParentFile, newName);
                                        try {
                                            FileUtil.copyFileUsingFileChannels(selectFile,exportFile);
                                            selectFile.delete();
                                            Logger.i("移动文件到 app 目录 "+selectFileName);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        })
                        .setNegativeButton(R.string.dialog_cancel,null)
                        .setMultiChoiceItems(nameList, nameListCheckInit, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                nameListCheckByUser[which] = isChecked;
                                Logger.i("which "+which+" isChecked "+isChecked);
                            }
                        })
                        .show();
            }
        }else{
            DialogUtil.showSinglePointDialog(getActivity(),"检测失败");
        }

    }

    private void copyFileToAppPlus(File file){

    }

    private List<File> listTencentDownloads(){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File wechatDownload = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"tencent/MicroMsg/Download");
            if(wechatDownload.exists()){
                File[]originList = wechatDownload.listFiles();
                List<File>apkList = new ArrayList<>();
                for(File file:originList){
                    if(file.getName().contains("apk")){
                        apkList.add(file);
                    }
                }
                return apkList;
            }else{
                return null;
            }
        }else{
            return null;
        }
    }

    private void startWeChatHelper(){
//        for(File file:files){
//            String fileName = file.getName();
//            if(fileName.contains("apk")){
//                String newName = fileName.substring(0,fileName.lastIndexOf("."));
//                File fileTo = new File(file.getParent(),newName);
//                if(file.renameTo(fileTo)){
//                    if(file.delete()){
//                        Toast.makeText(getActivity(), "原始文件已被删除", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        }
        DialogUtil.showSinglePointDialog(getActivity(),"已成功将微信中的 APK 文件重命名！");
    }
}