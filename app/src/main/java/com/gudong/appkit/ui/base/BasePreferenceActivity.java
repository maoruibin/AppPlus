package com.gudong.appkit.ui.base;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.gudong.appkit.utils.ThemeUtils;

/**
 * Created by mao on 7/23/15.
 */
public class BasePreferenceActivity extends PreferenceActivity {
    private ThemeUtils mThemeUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mThemeUtils = new ThemeUtils(this);
        setTheme(mThemeUtils.getCurrent());
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mThemeUtils.isChanged()) {
            setTheme(mThemeUtils.getCurrent());
            recreate();
        }
    }
}
