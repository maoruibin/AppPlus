package com.gudong.appkit.ui.fragment;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gudong.appkit.R;
import com.gudong.appkit.ui.base.BaseActivity;
import com.gudong.appkit.ui.control.NavigationManager;
import com.gudong.appkit.utils.ThemeUtils;
import com.gudong.appkit.utils.Utils;
import com.jenzz.materialpreference.PreferenceCategory;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;


public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener, ColorChooseDialog.IClickColorSelectCallback, Preference.OnPreferenceChangeListener {
    private BaseActivity mContext;
        @Override public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mContext = (BaseActivity) getActivity();
            addPreferencesFromResource(R.xml.prefs);

            //设置点击监听
            findPreference(getString(R.string.preference_key_check_update)).setOnPreferenceClickListener(this);
            findPreference(getString(R.string.preference_key_about)).setOnPreferenceClickListener(this);
            findPreference(getString(R.string.preference_key_score)).setOnPreferenceClickListener(this);
            findPreference(getString(R.string.preference_key_theme_primary)).setOnPreferenceClickListener(this);

            //动态设置显示内容
            findPreference(getString(R.string.preference_key_about)).setSummary(String.format(mContext.getString(R.string.current_version_info), Utils.getAppVersion(getActivity())));

            findPreference(getString(R.string.switch_preference_show_self_key)).setOnPreferenceChangeListener(this);

            //v0.2.2 不显示开发者选项
            PreferenceCategory advancedCategory = (PreferenceCategory) findPreference(getString(R.string.category_advanced_key));
            advancedCategory.removePreference(findPreference(getString(R.string.switch_preference_develop_key)));
        }

    @Override
    public boolean onPreferenceClick(android.preference.Preference preference) {
        String key = preference.getKey();
        //用if判断 效率不会很好 待改善
        if(key.equals(getString(R.string.preference_key_about))){
            new MaterialDialog.Builder(getActivity()).title(getString(R.string.app_name))
                    .content(R.string.app_about)
                    .neutralText("发邮件")
                    .positiveText(R.string.dialog_know)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onNeutral(MaterialDialog dialog) {
                            super.onNeutral(dialog);
                            NavigationManager.gotoSendOpinion(getActivity());
                        }
                    })
                    .show();
            MobclickAgent.onEvent(mContext, "setting_about");
        }
        if(key.equals(getString(R.string.preference_key_score))){
            NavigationManager.gotoScore(getActivity());
            MobclickAgent.onEvent(mContext, "setting_market");
        }
        if(key.equals(getString(R.string.preference_key_theme_primary))){
            ColorChooseDialog dialog = new ColorChooseDialog();
            dialog.setColorSelectCallback(this);
            dialog.show(mContext, mContext.getThemeUtils().getThemePosition());
            MobclickAgent.onEvent(mContext, "setting_theme_color");
        }
        if(key.equals(getString(R.string.preference_key_check_update))){
            UmengUpdateAgent.forceUpdate(mContext);
            MobclickAgent.onEvent(mContext, "setting_check_update");
        }
        return false;
    }


    @Override
    public void onClickSelectCallback(int position, int color) {
        boolean isDark = mContext.getThemeUtils().isDarkMode();
        int positionArray = isDark?1:0;
        mContext.getThemeUtils().setTheme(ThemeUtils.themeArr()[position][positionArray]);
        mContext.getThemeUtils().setThemePosition(position);
        mContext.reload();

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        if(key.equals(getString(R.string.switch_preference_show_self_key))){
            MobclickAgent.onEvent(mContext, "setting_show_self");
        }
        return false;
    }
}