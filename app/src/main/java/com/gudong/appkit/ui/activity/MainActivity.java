/*
 *     Copyright (c) 2015 Maoruibin
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

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gudong.appkit.R;
import com.gudong.appkit.adapter.AppPageListAdapter;
import com.gudong.appkit.event.EEvent;
import com.gudong.appkit.event.EventCenter;
import com.gudong.appkit.event.Subscribe;
import com.gudong.appkit.ui.fragment.EListType;
import com.gudong.appkit.utils.DialogUtil;
import com.gudong.appkit.utils.Utils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

public class MainActivity extends BaseActivity implements Subscribe {
    TabLayout mTabLayout;
    ViewPager mViewPager;
    AppPageListAdapter mFragmentAdapter;
    RelativeLayout mLayoutMainRoot;
    private long lastTime = 0;

    @Override
    protected int initLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventCenter.getInstance().registerEvent(EEvent.RECENT_LIST_IS_SHOW_SELF_CHANGE,this);

        checkAutoUpdateByUmeng();

        mLayoutMainRoot = (RelativeLayout) findViewById(R.id.layoutMainRoot);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(mViewPager);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        versionCheck();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventCenter.getInstance().unregisterEvent(EEvent.RECENT_LIST_IS_SHOW_SELF_CHANGE,this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                MobclickAgent.onEvent(this, "search");
                break;
            case R.id.action_settings:
                Intent intentSetting = new Intent(MainActivity.this, SimpleContainerActivity.class);
                intentSetting.putExtra(SimpleContainerActivity.KEY_TYPE, SimpleContainerActivity.FragmentType.SETTING);
                startActivity(intentSetting);
                MobclickAgent.onEvent(this, "setting_entry");
                break;
            case R.id.action_about:
                Intent intentAbout = new Intent(MainActivity.this, SimpleContainerActivity.class);
                intentAbout.putExtra(SimpleContainerActivity.KEY_TYPE, SimpleContainerActivity.FragmentType.ABOUT);
                startActivity(intentAbout);
                MobclickAgent.onEvent(this, "setting_about");
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - lastTime < 2000) {
            super.onBackPressed();
        } else {
            lastTime = System.currentTimeMillis();
            Toast.makeText(MainActivity.this, getString(R.string.exit_point), Toast.LENGTH_SHORT).show();
        }
    }

    private void versionCheck() {
        //本地版本检测 如果版本不一致 弹出版本更新日志信息框
        String currentVersion = Utils.getAppVersion(this);
        String localVersionName = Utils.getLocalVersion(this);
        if (!localVersionName.equals(currentVersion)) {
            DialogUtil.showCustomDialogFillInWebView(this, getSupportFragmentManager(), getString(R.string.change_log), "changelog.html", "changelog");
            Utils.setCurrentVersion(this, currentVersion);
        }
    }

    private void checkAutoUpdateByUmeng() {
        UmengUpdateAgent.update(this);
    }

    private void setupViewPager(ViewPager viewPager) {
        mFragmentAdapter = new AppPageListAdapter(getSupportFragmentManager(), this, new EListType[]{EListType.TYPE_RUNNING,EListType.TYPE_ALL});
        viewPager.setAdapter(mFragmentAdapter);
    }

    @Override
    public void update(EEvent event, Bundle data) {
        switch (event){
            case RECENT_LIST_IS_SHOW_SELF_CHANGE:
                mTabLayout.getTabAt(0).select();
                break;
        }
    }
}
