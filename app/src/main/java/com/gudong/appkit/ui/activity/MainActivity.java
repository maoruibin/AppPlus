package com.gudong.appkit.ui.activity;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gudong.appkit.R;
import com.gudong.appkit.adapter.AppPageListAdapter;
import com.gudong.appkit.dao.AppInfoEngine;
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
        //友盟检查更新
        checkAutoUpdateByUmeng();

        //如果是5.0以上设备 需要请求查看最近任务的权限 让用户同意
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkPermission();
        }

        mLayoutMainRoot = (RelativeLayout) findViewById(R.id.layoutMainRoot);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(mViewPager);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        //check version
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


    /*
      Check if permission enabled
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void checkPermission() {
        if (AppInfoEngine.getInstance(getApplicationContext()).getUsageStatsList().isEmpty()) {
            if (Utils.Setting.isNotShowPointForSumBug(getBaseContext())) return;
            if (Utils.getBrand().contains("sam") || Utils.getBrand().contains("lg")) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_point)
                        .setMessage(R.string.dialog_message_sam_usage)
                        .setNegativeButton(R.string.dialog_cancel, null)
                        .setPositiveButton(R.string.dialog_go_setting_safe, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                                intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$SecuritySettingsActivity"));
                                startActivity(intent);
                            }
                        })
                        .setNeutralButton(R.string.dialog_do_not_point, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Utils.Setting.setDoNotShowPointForSumBug(getBaseContext());
                            }
                        })
                        .show();
            } else {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                startActivity(intent);
            }
        }
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
