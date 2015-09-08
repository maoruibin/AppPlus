package com.gudong.appkit.ui.activity;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gudong.appkit.R;
import com.gudong.appkit.adapter.AppPageListAdapter;
import com.gudong.appkit.dao.AppInfoEngine;
import com.gudong.appkit.entity.AppEntity;
import com.gudong.appkit.ui.base.BaseActivity;
import com.gudong.appkit.ui.control.NavigationManager;
import com.gudong.appkit.ui.fragment.AppListFragment;
import com.gudong.appkit.utils.DialogUtil;
import com.gudong.appkit.utils.Utils;
import com.gudong.appkit.utils.logger.Logger;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    AppBarLayout mAppBar;
    DrawerLayout mDrawerLayout;
    TabLayout mTabLayout;
    ViewPager mViewPager;
    FrameLayout mFlSearchResult;
    AppListFragment mSearchResultFragment;
    List<AppEntity>mListInstalled;
    AppPageListAdapter mFragmentAdapter;
    RelativeLayout mLayoutMainRoot;
    AppInfoEngine mEngine;
    private static int[]mTitles = new int[]{R.string.tab_recent,R.string.tab_installed};

    @Override
    protected int initLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEngine = new AppInfoEngine(getApplicationContext());
        //友盟检查更新
        checkAutoUpdateByUmeng();

        //如果是5.0以上设备 需要请求查看最近任务的权限 让用户同意
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            checkPermission();
        }

        mLayoutMainRoot = (RelativeLayout) findViewById(R.id.layoutMainRoot);

        mAppBar = (AppBarLayout) findViewById(R.id.appbar);

        ActionBar actionBar = getSupportActionBar();
//        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(false);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        if (mViewPager != null) {
            setupViewPager(mViewPager);
        }

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        if (mTabLayout != null) {
            mTabLayout.setupWithViewPager(mViewPager);
        }

        mFlSearchResult = (FrameLayout) findViewById(R.id.fl_contain_search_result);
        initSearchContent();

        versionCheck();
    }


    private void initSearchContent(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        mSearchResultFragment = AppListFragment.getInstance(AppListFragment.KEY_SEARCH);
        fragmentTransaction.add(R.id.fl_contain_search_result,mSearchResultFragment);
        fragmentTransaction.commit();
    }

    private void versionCheck() {
        //本地版本检测 如果版本不一致 弹出版本更新日志信息框
        String currentVersion = Utils.getAppVersion(this);
        String localVersionName = Utils.getLocalVersion(this);
        if(!localVersionName.equals(currentVersion)){
            DialogUtil.showCusotomDialogFillInWebView(this, getSupportFragmentManager(), getString(R.string.change_log), "changelog.html", "changelog");
            Utils.setCurrentVersion(this, currentVersion);
        }

        //0.2.1版本产生的主题问题 之前的版本可能因为已经设置了夜间模式 这里需要强制改为白天模式
        forceUpdateLightThemeForVersion021();
    }

    private void checkAutoUpdateByUmeng() {
        UmengUpdateAgent.update(this);
    }

    private void forceUpdateLightThemeForVersion021() {
        if(getThemeUtils().isDarkMode()){
            getThemeUtils().setIsDarkTheme(false);
            reload();
        }
    }

    /*
      Check if permission enabled
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void checkPermission(){
        if (mEngine.getUsageStatsList().isEmpty()){
            if(Utils.Setting.isNotShowPointForSumBug(getBaseContext()))return;
            if(Utils.getBrand().contains("sam") || Utils.getBrand().contains("lg")){
                new MaterialDialog.Builder(this)
                        .negativeText(R.string.dialog_cancel)
                        .positiveText(R.string.dialog_go_setting_safe)
                        .neutralText(R.string.dialog_do_not_point)
                        .content(R.string.dialog_message_sam_usage)
                        .title(R.string.title_point)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                                intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$SecuritySettingsActivity"));
                                startActivity(intent);
                            }

                            @Override
                            public void onNeutral(MaterialDialog dialog) {
                                super.onNeutral(dialog);
                                Utils.Setting.setDoNotShowPointForSumBug(getBaseContext());
                            }
                        })
                        .show();
            }else{
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                startActivity(intent);
            }
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        mFragmentAdapter = new AppPageListAdapter(getSupportFragmentManager(),this,mTitles);
        viewPager.setAdapter(mFragmentAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.search_app_hint));
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mTabLayout.setVisibility(View.GONE);
                    mViewPager.setVisibility(View.GONE);
                    mFlSearchResult.setVisibility(View.VISIBLE);
                    //设置toolbar的scrollFlag 让他不响应RecycleView的滑动事件
                    AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) getToolbar().getLayoutParams();
                    params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
                    MobclickAgent.onEvent(MainActivity.this, "search");
                } else {
                    mTabLayout.setVisibility(View.VISIBLE);
                    mViewPager.setVisibility(View.VISIBLE);
                    mFlSearchResult.setVisibility(View.GONE);
                    mSearchResultFragment.clearData();
                    Logger.i("hasFocus  失去焦点");
                    AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) getToolbar().getLayoutParams();
                    params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
                    //收起searchView  这里不要使用searchView
                    searchItem.collapseActionView();
                }
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    mSearchResultFragment.clearData();
                } else {
                    List<AppEntity> result = searchApp(getAllInstalledApp(), newText);
                    mSearchResultFragment.setData(result,AppListFragment.KEY_SEARCH);
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.action_settings:
                Intent intentSetting = new Intent(MainActivity.this, SimpleContainerActivity.class);
                intentSetting.putExtra(SimpleContainerActivity.KEY_TYPE,SimpleContainerActivity.FragmentType.SETTING);
                startActivity(intentSetting);
                MobclickAgent.onEvent(this, "setting_entry");
                break;
            case R.id.action_about:
                Intent intentAbout = new Intent(MainActivity.this, SimpleContainerActivity.class);
                intentAbout.putExtra(SimpleContainerActivity.KEY_TYPE,SimpleContainerActivity.FragmentType.ABOUT);
                startActivity(intentAbout);
                MobclickAgent.onEvent(this, "setting_about");
                break;
        }
        return true;
    }


    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.menu_drawer_theme:
                                boolean isDark = getThemeUtils().isDarkMode();
                                getThemeUtils().setIsDarkTheme(!isDark);
                                reload();
                                break;
                            case R.id.menu_drawer_setting:
                                Intent intent = new Intent(MainActivity.this, SimpleContainerActivity.class);
                                intent.putExtra(SimpleContainerActivity.KEY_TYPE,SimpleContainerActivity.FragmentType.SETTING);
                                startActivity(intent);
                                MobclickAgent.onEvent(MainActivity.this, "setting_entry");
                                break;
                            case R.id.menu_drawer_opinion:
                                NavigationManager.gotoSendOpinion(MainActivity.this);
                                break;
                        }
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    private List<AppEntity>getAllInstalledApp(){
        if(mListInstalled == null){
            mListInstalled = mEngine.getInstalledAppList();
        }
        return mListInstalled;
    }

    /**
     * 根据关键字搜索App
     * @param list
     * @param key
     * @return
     */
    private List<AppEntity>searchApp(List<AppEntity>list,String key){
        if(TextUtils.isEmpty(key)){
            return list;
        }
        List<AppEntity>resultList = new ArrayList<>();
        for(AppEntity entity:list){
            String appName = entity.getAppName();
            if(!TextUtils.isEmpty(appName) && (appName.contains(key) || appName.contains(key.toUpperCase()) || appName.contains(key.toLowerCase()))){
                resultList.add(entity);
            }
        }
        return resultList;
    }
    private long lastTime = 0;
    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis()-lastTime<2000){
            super.onBackPressed();
        }else {
            lastTime = System.currentTimeMillis();
            Snackbar.make(mLayoutMainRoot,getString(R.string.exit_point),Snackbar.LENGTH_SHORT).show();
        }
    }
}
