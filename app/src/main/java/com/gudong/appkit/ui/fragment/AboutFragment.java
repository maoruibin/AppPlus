package com.gudong.appkit.ui.fragment;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.gudong.appkit.R;
import com.gudong.appkit.ui.base.BaseActivity;
import com.gudong.appkit.ui.control.NavigationManager;
import com.gudong.appkit.utils.DialogUtil;
import com.gudong.appkit.utils.Utils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

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
            findPreference(getString(R.string.preference_key_opinion)).setOnPreferenceClickListener(this);
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
            DialogUtil.showCusotomDialogFillInWebView(mContext, mContext.getSupportFragmentManager(), getString(R.string.preference_title_about), "about.html", "about");
            MobclickAgent.onEvent(mContext, "setting_about");
        }
        if(key.equals(getString(R.string.preference_key_score))){
            NavigationManager.gotoMarket(mContext,getActivity().getPackageName());
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
            DialogUtil.showCusotomDialogFillInWebView(mContext, mContext.getSupportFragmentManager(), getString(R.string.preference_title_license), "license.html", "license");
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