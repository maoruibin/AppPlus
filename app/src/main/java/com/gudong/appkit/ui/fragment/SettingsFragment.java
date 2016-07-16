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
import com.gudong.appkit.event.EEvent;
import com.gudong.appkit.event.RxBus;
import com.gudong.appkit.event.RxEvent;
import com.gudong.appkit.ui.activity.BaseActivity;
import com.gudong.appkit.ui.control.ThemeControl;
import com.gudong.appkit.utils.DialogUtil;
import com.gudong.appkit.utils.Utils;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jonathanfinerty.once.Once;


public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener, ColorChooseDialog.IClickColorSelectCallback, Preference.OnPreferenceChangeListener {
    private BaseActivity mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = (BaseActivity) getActivity();
        addPreferencesFromResource(R.xml.prefs_setting);

        //设置点击监听
        findPreference(getString(R.string.preference_key_theme_primary)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.preference_key_wechat_helper)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.switch_preference_key_show_self)).setOnPreferenceChangeListener(this);
        findPreference(getString(R.string.switch_preference_key_list_item_brief_mode)).setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceClick(android.preference.Preference preference) {
        String key = preference.getKey();
        if (key.equals(getString(R.string.preference_key_theme_primary))) {
            ColorChooseDialog dialog = new ColorChooseDialog();
            dialog.setColorSelectCallback(this);
            dialog.show(mContext, mContext.getThemeUtils().getThemePosition());
            MobclickAgent.onEvent(mContext, "setting_theme_color");
        }
        if(key.equals(getString(R.string.preference_key_wechat_helper))){
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
        return false;
    }

    private void showDownloadListDialog(List<File> listTencentDownloads) {
        if(listTencentDownloads!=null){
            if(listTencentDownloads.isEmpty()){
                DialogUtil.showSinglePointDialog(getActivity(),"在你的微信下载目录没有发现任何安装包文件");
            }else{
                CharSequence[]nameList = new CharSequence[listTencentDownloads.size()];
                boolean[]nameListCheck = new boolean[listTencentDownloads.size()];

                for(int i = 0;i<listTencentDownloads.size();i++){
                    nameList[i] = listTencentDownloads.get(i).getName();
                    nameListCheck[i] = true;
                }
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.title_point)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setNegativeButton(R.string.dialog_cancel,null)
                        .setMultiChoiceItems(nameList, nameListCheck, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                            }
                        })
                        .show();
            }
        }else{
            DialogUtil.showSinglePointDialog(getActivity(),"检测失败");
        }

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

    @Override
    public void onClickSelectCallback(int position, int color) {
        //设置主题 并且让主题立即生效（通过方法 mContext.reload()）
        mContext.getThemeUtils().setTheme(ThemeControl.themeArr()[position]);
        mContext.getThemeUtils().setThemePosition(position);
        mContext.reload();

        //统计用户主题颜色的选取
        Map<String, String> map_value = new HashMap<>();
        map_value.put("theme_color", "select");
        MobclickAgent.onEventValue(getActivity(), "theme_color_select", map_value, position);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        if (key.equals(getString(R.string.switch_preference_key_show_self))) {
            //用户的点击计数
            MobclickAgent.onEvent(mContext, "setting_show_self");
            //判断用户的选择行为
            Map<String, String> map_value = new HashMap<>();
            map_value.put("is_show_self", "yes_or_not");
            int flag = Utils.isShowSelf() ? 1 : 0;
            MobclickAgent.onEventValue(getActivity(), "show_self_or_no", map_value, flag);
            RxBus.getInstance().send(RxEvent.get(EEvent.RECENT_LIST_IS_SHOW_SELF_CHANGE));
        }

        if (key.equals(getString(R.string.switch_preference_key_list_item_brief_mode))) {
            MobclickAgent.onEvent(mContext, "setting_brief");
            RxBus.getInstance().send(RxEvent.get(EEvent.LIST_ITEM_BRIEF_MODE_CHANGE));
        }

        return true;
    }
}