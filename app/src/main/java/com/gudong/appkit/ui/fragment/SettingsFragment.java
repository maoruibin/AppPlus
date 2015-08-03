package com.gudong.appkit.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gudong.appkit.R;
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
                            sendOpinion();
                        }
                    })
                    .show();
        }
        if(key.equals(getString(R.string.preference_key_score))){
            gotoScore();
        }
        return false;
    }

    private void sendOpinion(){
        Intent localIntent = new Intent("android.intent.action.SENDTO", Uri.parse("mailto:" + "1252768410@qq.com"));
        localIntent.putExtra("android.intent.extra.SUBJECT", getString(R.string.title_email_opinion));
        localIntent.putExtra("android.intent.extra.TEXT", "");
        startActivity(localIntent);
    }

    private void gotoScore(){
        Uri uri = Uri.parse("market://details?id="+getActivity().getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void gotoShare(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("text/*");
        sendIntent.putExtra(Intent.EXTRA_TEXT, "App+，一款不错的App管理应用");
        startActivity(sendIntent);
    }
}