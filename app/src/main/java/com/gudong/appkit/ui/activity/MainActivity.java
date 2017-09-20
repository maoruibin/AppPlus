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

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gudong.appkit.R;
import com.gudong.appkit.dao.WeChatHelper;
import com.gudong.appkit.event.EEvent;
import com.gudong.appkit.event.RxBus;
import com.gudong.appkit.event.RxEvent;
import com.gudong.appkit.ui.control.NavigationManager;
import com.gudong.appkit.ui.fragment.AppFileListFragment;
import com.gudong.appkit.ui.fragment.AppListFragment;
import com.gudong.appkit.utils.DialogUtil;
import com.gudong.appkit.utils.Utils;
import com.gudong.appkit.utils.logger.Logger;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;
import moe.feng.alipay.zerosdk.AlipayZeroSdk;
import rx.functions.Action1;

public class MainActivity extends BaseActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };


    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;

    TextView songtitle, songartist;

    private long lastTime = 0;

    @Override
    protected int initLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verifyStoragePermissions(this);
        checkAutoUpdateByUmeng();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.draw_layout);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = mNavigationView.inflateHeaderView(R.layout.nav_header);

        songtitle = (TextView) header.findViewById(R.id.song_title);
        songartist = (TextView) header.findViewById(R.id.song_artist);

        setupDrawerContent(mNavigationView);

        selectRecent();

        subscribeEvents();

        checkWeChatDownloadFile();

        versionCheck();
        rateApp();

    }


    private void rateApp() {
        AppRate.with(this)
                .setInstallDays(1) // default 10, 0 means install day.
                .setLaunchTimes(5) // default 10
                .setRemindInterval(2) // default 1
                .setShowLaterButton(true) // default true
                .setOnClickButtonListener(new OnClickButtonListener() { // callback listener.
                    @Override
                    public void onClickButton(int which) {
                        MobclickAgent.onEvent(MainActivity.this, "rate_score_"+which);
                    }
                })
                .monitor();

        // Show a dialog if meets conditions
        AppRate.showRateDialogIfMeetsConditions(this);
    }

    private void checkWeChatDownloadFile() {
        if(Utils.isAutoCheckWeChat()){
            mNavigationView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    new WeChatHelper(MainActivity.this).checkDownloadListDialog(true);
                }
            },5000);
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(final MenuItem menuItem) {
                        updatePosition(menuItem);
                        return true;
                    }
                });
    }

    Fragment currentFragment=null;
    private void updatePosition(final MenuItem menuItem) {
        Fragment fragment = null;
        switch (menuItem.getItemId()) {
            case R.id.nav_recent:
                fragment = AppListFragment.getInstance(AppListFragment.TYPE_RECENT);
                break;
            case R.id.nav_installed:
                fragment = AppListFragment.getInstance(AppListFragment.TYPE_INSTALLED);
                break;
            case R.id.nav_favorite:
                fragment = AppListFragment.getInstance(AppListFragment.TYPE_FAVORITE);
                break;
            case R.id.nav_exported:
                fragment = new AppFileListFragment();
                break;
            case R.id.nav_wechat_helper:
                mDrawerLayout.closeDrawers();
                Intent intentWechatHelper =  new Intent(MainActivity.this, SimpleContainerActivity.class);
                intentWechatHelper.putExtra(SimpleContainerActivity.KEY_TYPE, SimpleContainerActivity.FragmentType.WECHAT_HELPER);
                startActivity(intentWechatHelper);
                MobclickAgent.onEvent(this, "open_wechat_helper");
                break;
            case R.id.nav_donate:
                showDonateDialog();
                MobclickAgent.onEvent(this, "menu_donate");
                break;
            case R.id.nav_settings:
                mDrawerLayout.closeDrawers();
                Intent intentSetting = new Intent(MainActivity.this, SimpleContainerActivity.class);
                intentSetting.putExtra(SimpleContainerActivity.KEY_TYPE, SimpleContainerActivity.FragmentType.SETTING);
                startActivity(intentSetting);
                MobclickAgent.onEvent(this, "setting_entry");
                break;
            case R.id.nav_about:
                mDrawerLayout.closeDrawers();
                Intent intentAbout = new Intent(MainActivity.this, SimpleContainerActivity.class);
                intentAbout.putExtra(SimpleContainerActivity.KEY_TYPE, SimpleContainerActivity.FragmentType.ABOUT);
                startActivity(intentAbout);
                MobclickAgent.onEvent(this, "setting_about");
                break;
            case R.id.nav_opinion:
                mDrawerLayout.closeDrawers();
                NavigationManager.gotoSendOpinion(this);
                MobclickAgent.onEvent(this, "send_email");
                break;
        }

        if (fragment != null) {
            currentFragment=fragment;
            menuItem.setChecked(true);
            mDrawerLayout.closeDrawers();

            FragmentManager fragmentManager = getSupportFragmentManager();
            final FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fl_container, fragment);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    transaction.commitAllowingStateLoss();
                }
            }, 350);

        }
    }

    private void showDonateDialog() {
        String htmlFileName =  Utils.isChineseLanguage()?"donate_ch.html":"donate_ch.html";
        DialogUtil.showCustomDialogWithTwoAction(this, getSupportFragmentManager(), getString(R.string.action_donate),htmlFileName, "donate",
                getString(R.string.action_close),null,
                getString(R.string.action_copy_to_clipboard),new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(AlipayZeroSdk.hasInstalledAlipayClient(MainActivity.this)){
                            AlipayZeroSdk.startAlipayClient(MainActivity.this, "aex07094cljuqa36ku7ml36");
                        }else{
                            Toast.makeText(MainActivity.this,getString(R.string.support_exception_for_alipay), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void selectRecent(){
        mNavigationView.getMenu().findItem(R.id.nav_recent).setChecked(true);
        Fragment fragment = AppListFragment.getInstance(0);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fl_container, fragment).commitAllowingStateLoss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
       if(currentFragment!=null && currentFragment instanceof AppFileListFragment ){
           menu.findItem(R.id.action_search).setVisible(false);
       }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.action_search:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                MobclickAgent.onEvent(this, "search");
                break;
            case R.id.action_opinion:
                NavigationManager.gotoSendOpinion(this);
                MobclickAgent.onEvent(MainActivity.this, "title_send_opinion");
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(Gravity.LEFT)){
            mDrawerLayout.closeDrawers();
            return;
        }
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
            String htmlFileName =  Utils.isChineseLanguage()?"changelog_ch.html":"changelog.html";
            DialogUtil.showCustomDialogFillInWebView(this, getSupportFragmentManager(), getString(R.string.change_log),htmlFileName, "changelog");
            Utils.setCurrentVersion(this, currentVersion);
        }
    }

    private void checkAutoUpdateByUmeng() {
        UmengUpdateAgent.update(this);
    }

    private void subscribeEvents() {
        RxBus.getInstance()
                .toObservable()
                .subscribe(new Action1() {
                    @Override
                    public void call(Object o) {
                        Logger.i("open 1 ");
                        if(o instanceof RxEvent){
                            RxEvent msg = (RxEvent) o;
                            EEvent event = msg.getType();
                            if(event == EEvent.OPEN_EXPORT_DIR){
                                mNavigationView.setCheckedItem(R.id.nav_exported);
                                updatePosition(mNavigationView.getMenu().findItem(R.id.nav_exported));
                                Logger.i("open");
                            }
                        }
                    }
                });
    }
    private void verifyStoragePermissions(Activity activity) {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(this,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
