package com.gudong.appkit.ui.fragment;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.gudong.appkit.R;
import com.gudong.appkit.ui.base.BaseActivity;
import com.gudong.appkit.ui.control.ThemeControl;
import com.gudong.appkit.utils.Utils;
import com.jenzz.materialpreference.PreferenceCategory;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;


public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener, ColorChooseDialog.IClickColorSelectCallback, Preference.OnPreferenceChangeListener {
    private BaseActivity mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = (BaseActivity) getActivity();
        addPreferencesFromResource(R.xml.prefs_setting);

        //设置点击监听
        findPreference(getString(R.string.preference_key_theme_primary)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.switch_preference_show_self_key)).setOnPreferenceChangeListener(this);
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
        if (key.equals(getString(R.string.switch_preference_show_self_key))) {
            //用户的点击计数
            MobclickAgent.onEvent(mContext, "setting_show_self");

            //判断用户的选择行为
            Map<String, String> map_value = new HashMap<>();
            map_value.put("is_show_self", "yes_or_not");
            int flag = Utils.isShowSelf(getActivity()) ? 1 : 0;
            MobclickAgent.onEventValue(getActivity(), "show_self_or_no", map_value, flag);
        }

        return true;
    }
}