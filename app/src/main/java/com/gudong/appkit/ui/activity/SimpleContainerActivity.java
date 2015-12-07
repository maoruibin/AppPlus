package com.gudong.appkit.ui.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.gudong.appkit.R;
import com.gudong.appkit.ui.fragment.AboutFragment;
import com.gudong.appkit.ui.fragment.SettingsFragment;

import java.io.Serializable;

/**
 * 一个简单的Fragment容器Activity
 */
public class SimpleContainerActivity extends BaseActivity {
    public static final String KEY_TYPE = "FRAGMENT_TYPE";
    @Override
    protected int initLayout() {
        return R.layout.activity_simple_container;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentType type = (FragmentType) getIntent().getSerializableExtra(KEY_TYPE);
        if(type!=null){
            setupToolBar(type.mResTitle, true);
            addContent(getFragmentByType(type));
        }
    }

    private void addContent(Fragment fragment){
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fl_container,fragment);
        fragmentTransaction.commit();
    }

    private Fragment getFragmentByType(FragmentType type){
        Fragment fragment = null;
        switch (type){
            case SETTING:
                fragment = new SettingsFragment();
                break;
            case ABOUT:
                fragment = new AboutFragment();
                break;
        }
        return fragment;
    }

    public enum FragmentType implements Serializable{
        SETTING(R.string.action_settings),
        ABOUT(R.string.action_about);

        int mResTitle;

        FragmentType(int resTitle){
            mResTitle = resTitle;
        }
    }
}
