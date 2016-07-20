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
import android.support.v7.app.AlertDialog;

import com.gudong.appkit.R;
import com.gudong.appkit.dao.WeChatHelper;
import com.gudong.appkit.event.EEvent;
import com.gudong.appkit.event.RxBus;
import com.gudong.appkit.event.RxEvent;
import com.gudong.appkit.ui.activity.BaseActivity;
import com.gudong.appkit.ui.control.NavigationManager;
import com.gudong.appkit.ui.control.ThemeControl;
import com.gudong.appkit.utils.Utils;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

import jonathanfinerty.once.Once;


public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener, ColorChooseDialog.IClickColorSelectCallback, Preference.OnPreferenceChangeListener {
    private BaseActivity mContext;
    private WeChatHelper mHelper;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = (BaseActivity) getActivity();
        mHelper = new WeChatHelper(getActivity());
        addPreferencesFromResource(R.xml.prefs_setting);

        //设置点击监听
        findPreference(getString(R.string.preference_key_theme_primary)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.preference_key_wechat_helper)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.preference_key_open_wechat_download)).setOnPreferenceClickListener(this);

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
        //用if判断 效率不会很好 待改善
        if (key.equals(getString(R.string.preference_key_wechat_helper))) {
            MobclickAgent.onEvent(getActivity(), "wechat_helper");
            final String showWhatsNew = "showWhatsWeChatHelper";
            if (!Once.beenDone(Once.THIS_APP_VERSION, showWhatsNew)) {
                new AlertDialog.Builder(getActivity())
                        .setTitle(mContext.getString(R.string.title_about_wechat_helper))
                        .setMessage(R.string.about_wechat_helper)
                        .setPositiveButton(R.string.dialog_know, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Once.markDone(showWhatsNew);
                                mHelper.checkDownloadListDialog(false);
                            }
                        })
                        .show();
            }else{
                mHelper.checkDownloadListDialog(false);
            }
        }
        if (key.equals(getString(R.string.preference_key_open_wechat_download))) {
            NavigationManager.browseFile(getActivity(),mHelper.getWeChatDownloadDir());
            MobclickAgent.onEvent(getActivity(), "open_wechat_download");
        }
        return false;
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