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

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.gudong.appkit.R;
import com.gudong.appkit.ui.activity.BaseActivity;
import com.gudong.appkit.ui.control.NavigationManager;
import com.gudong.appkit.utils.DialogUtil;
import com.gudong.appkit.utils.Utils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import hotchemi.android.rate.AppRate;

/**
 * 关于页面
 */
public class AboutFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener{
    private BaseActivity mContext;
        @Override public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mContext = (BaseActivity) getActivity();
            addPreferencesFromResource(R.xml.prefs_about);

            //设置点击监听
            findPreference(getString(R.string.preference_key_about)).setOnPreferenceClickListener(this);
            findPreference(getString(R.string.preference_key_score)).setOnPreferenceClickListener(this);
//            findPreference(getString(R.string.preference_key_opinion)).setOnPreferenceClickListener(this);
            findPreference(getString(R.string.preference_key_check_update)).setOnPreferenceClickListener(this);
            findPreference(getString(R.string.preference_key_license)).setOnPreferenceClickListener(this);

            //关于item要显示版本号信息，这里动态获取版本号信息 并显示
            findPreference(getString(R.string.preference_key_about)).setSummary(String.format(mContext.getString(R.string.current_version_info), Utils.getAppVersion(getActivity())));
        }

    @Override
    public boolean onPreferenceClick(android.preference.Preference preference) {
        String key = preference.getKey();
        //用if判断 效率不会很好 待改善
        if(key.equals(getString(R.string.preference_key_about))){
            String htmlFileName =  Utils.isChineseLanguage()?"about_ch.html":"about.html";
            DialogUtil.showCustomDialogFillInWebView(mContext, mContext.getSupportFragmentManager(), getString(R.string.preference_title_about), htmlFileName, "about");
            MobclickAgent.onEvent(mContext, "setting_about");
        }
        if(key.equals(getString(R.string.preference_key_score))){
//            NavigationManager.gotoMarket(mContext,getActivity().getPackageName());
            AppRate.with(getActivity()).showRateDialog(getActivity());
            MobclickAgent.onEvent(mContext, "setting_market");
        }
        if(key.equals(getString(R.string.preference_key_opinion))){
            NavigationManager.gotoSendOpinion(getActivity());
            MobclickAgent.onEvent(getActivity(), "send_email");
        }
        if(key.equals(getString(R.string.preference_key_check_update))){
            UmengUpdateAgent.setUpdateListener(new CheckUmengUpdateListener());
            UmengUpdateAgent.forceUpdate(mContext);
            MobclickAgent.onEvent(mContext, "setting_check_update");
        }
        if(key.equals(getString(R.string.preference_key_license))){
            DialogUtil.showCustomDialogFillInWebView(mContext, mContext.getSupportFragmentManager(), getString(R.string.preference_title_license), "license.html", "license");
            MobclickAgent.onEvent(mContext, "setting_license");
        }
        return false;
    }

    private class CheckUmengUpdateListener implements UmengUpdateListener {
        @Override
        public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
            switch (updateStatus) {
                case UpdateStatus.Yes: // has update
                    UmengUpdateAgent.showUpdateDialog(mContext, updateInfo);
                    break;
                case UpdateStatus.No: // has no update
                    DialogUtil.showSinglePointDialog(mContext,mContext.getString(R.string.update_point_no_update));
                    break;
                case UpdateStatus.NoneWifi: // none wifi
                    DialogUtil.showSinglePointDialog(mContext, mContext.getString(R.string.update_point_no_wifi));
                    break;
                case UpdateStatus.Timeout: // time out
                    DialogUtil.showSinglePointDialog(mContext, mContext.getString(R.string.update_point_time_out));
                    break;
            }
        }
    }

}