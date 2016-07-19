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
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.gudong.appkit.R;
import com.gudong.appkit.dao.WeChatHelper;
import com.gudong.appkit.ui.activity.BaseActivity;
import com.gudong.appkit.utils.DialogUtil;

import java.io.File;

import jonathanfinerty.once.Once;

/**
 * 微信助手页面
 */
public class WechatHelperFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
    private BaseActivity mContext;
    private WeChatHelper mHelper;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = (BaseActivity) getActivity();
        mHelper = new WeChatHelper(getActivity());
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
                        mHelper.checkDownloadListDialog(false);
                    }
                });
            }else{
                mHelper.checkDownloadListDialog(false);
            }


        }
        if (key.equals(getString(R.string.preference_key_open_wechat_download))) {

        }
        return false;
    }



    private void copyFileToAppPlus(File file){

    }


}