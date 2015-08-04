package com.gudong.appkit.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gudong.appkit.R;
import com.gudong.appkit.ui.control.NavigationManager;
import com.jenzz.materialpreference.Preference;


public class SettingsFragment extends PreferenceFragment implements android.preference.Preference.OnPreferenceClickListener {
        @Override public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.prefs);

            findPreference(getString(R.string.preference_key_about)).setOnPreferenceClickListener(this);
            findPreference(getString(R.string.preference_key_score)).setOnPreferenceClickListener(this);
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
        return false;
    }


}