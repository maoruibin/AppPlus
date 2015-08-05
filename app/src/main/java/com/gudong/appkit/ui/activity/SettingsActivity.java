package com.gudong.appkit.ui.activity;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.gudong.appkit.R;
import com.gudong.appkit.ui.base.BaseActivity;
import com.gudong.appkit.ui.fragment.SettingsFragment;

public class SettingsActivity extends BaseActivity {
    @Override
    protected int initLayout() {
        return R.layout.activity_setting;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupToolBar(R.string.action_settings, true);
        addContent();
    }

    private void addContent(){
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fl_container,new SettingsFragment());
        fragmentTransaction.commit();
    }

}
