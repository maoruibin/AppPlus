package com.gudong.appkit.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.ThemeSingleton;
import com.gudong.appkit.R;
import com.gudong.appkit.dao.AppInfoEngine;
import com.gudong.appkit.ui.base.BaseActivity;
import com.gudong.appkit.ui.fragment.AppListFragment;
import com.gudong.appkit.ui.fragment.ChangelogDialog;
import com.gudong.appkit.utils.ThemeUtils;
import com.gudong.appkit.utils.Utils;

public class MainActivity extends BaseActivity {
    DrawerLayout mDrawerLayout;
    private static int[]mTitles = new int[]{R.string.tab_recent,R.string.tab_installed};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            checkPermission();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(viewPager);
        }

        versionCheck();
    }

    private void versionCheck() {
        String currentVersion = getAppVersion();
        String localVersionName = Utils.getLocalVersion(this);
        if(!localVersionName.equals(currentVersion)){
            showVersionLogView();
            Utils.setCurrentVersion(this,currentVersion);
        }
    }

    private String getAppVersion(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    /*
      Check if permission enabled
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void checkPermission(){
        AppInfoEngine engine = new AppInfoEngine(this);
        if (engine.getUsageStatsList().isEmpty()){
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this,SettingsActivity.class));
                break;
        }
        return true;
    }


    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.menu_drawer_theme:
                                boolean isDark = ThemeUtils.isDarkMode(MainActivity.this);
                                ThemeUtils.setTheme(MainActivity.this,!isDark);
                                recreate();
                                break;
                            case R.id.menu_drawer_setting:
                                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                                break;
                            case R.id.menu_drawer_opinion:
                                sendOpinion();
                                break;
                        }
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    class Adapter extends FragmentPagerAdapter {

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return AppListFragment.getInstance(position);
        }

        @Override
        public int getCount() {
            return mTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getString(mTitles[position]).toString();
        }
    }

    private void sendOpinion(){
        Intent localIntent = new Intent("android.intent.action.SENDTO", Uri.parse("mailto:" + "1252768410@qq.com"));
        localIntent.putExtra("android.intent.extra.SUBJECT", getString(R.string.title_email_opinion));
        localIntent.putExtra("android.intent.extra.TEXT", Utils.getLog(this));
        startActivity(localIntent);
    }

    private void showVersionLogView() {
        int accentColor = ThemeSingleton.get().widgetColor;
        if (accentColor == 0)
            accentColor = getResources().getColor(R.color.colorAccent);

        ChangelogDialog.create(false, accentColor)
                .show(getSupportFragmentManager(), "changelog");
    }
}
