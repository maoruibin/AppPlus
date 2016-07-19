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

package com.gudong.appkit.ui.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.gudong.appkit.R;
import com.gudong.appkit.ui.fragment.AboutFragment;
import com.gudong.appkit.ui.fragment.SettingsFragment;
import com.gudong.appkit.ui.fragment.WechatHelperFragment;

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
            case WECHAT_HELPER:
                fragment = new WechatHelperFragment();
                break;
        }
        return fragment;
    }

    public enum FragmentType implements Serializable{
        SETTING(R.string.action_settings),
        ABOUT(R.string.action_about),
        WECHAT_HELPER(R.string.action_wechat_helper);

        int mResTitle;

        FragmentType(int resTitle){
            mResTitle = resTitle;
        }
    }
}
