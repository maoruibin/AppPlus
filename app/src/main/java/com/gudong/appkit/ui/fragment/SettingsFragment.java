package com.gudong.appkit.ui.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gudong.appkit.R;
import com.gudong.appkit.ui.base.BaseActivity;
import com.gudong.appkit.ui.control.NavigationManager;
import com.gudong.appkit.utils.ThemeUtils;


public class SettingsFragment extends PreferenceFragment implements android.preference.Preference.OnPreferenceClickListener, ColorChooseDialog.IClickColorSelectCallback {
    private BaseActivity mContext;
        @Override public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mContext = (BaseActivity) getActivity();
            addPreferencesFromResource(R.xml.prefs);

            findPreference(getString(R.string.preference_key_about)).setOnPreferenceClickListener(this);
            findPreference(getString(R.string.preference_key_score)).setOnPreferenceClickListener(this);
            findPreference(getString(R.string.preference_key_theme_primary)).setOnPreferenceClickListener(this);
        }

    @Override
    public boolean onPreferenceClick(android.preference.Preference preference) {
        String key = preference.getKey();
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
        }
        if(key.equals(getString(R.string.preference_key_score))){
            NavigationManager.gotoScore(getActivity());
        }
        if(key.equals(getString(R.string.preference_key_theme_primary))){
            ColorChooseDialog dialog = new ColorChooseDialog();
            dialog.setColorSelectCallback(this);
            dialog.show(mContext, mContext.getThemeUtils().getThemePosition());
        }
        return false;
    }


    @Override
    public void onClickSelectCallback(int position, int color) {
        boolean isDark = ThemeUtils.isDarkMode(mContext);
        int positionArray = isDark?1:0;
        mContext.getThemeUtils().setTheme(ThemeUtils.themeArr()[position][positionArray]);
        mContext.getThemeUtils().setThemePosition(position);
        mContext.reload();

    }
}