package com.gudong.appkit.ui.base;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.gudong.appkit.R;
import com.gudong.appkit.utils.ThemeUtils;
import com.gudong.appkit.utils.logger.Logger;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by mao on 7/16/15.
 */
public abstract class BaseActivity extends AppCompatActivity {
    private ThemeUtils mThemeUtils;
    private Toolbar mToolbar;
    // 防止reload方法递归调用
    private boolean hasRecreate = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mThemeUtils = new ThemeUtils(this);
        // 设置当前主题
        setTheme(mThemeUtils.getTheme(this));
        hasRecreate = true;
        super.onCreate(savedInstanceState);
        // 设置是否对日志信息进行加密, true 加密
        AnalyticsConfig.enableEncrypt(true);
        // 设置布局
        setContentView(initLayout());
        // 初始化跟toolbar
        initToolBar();
        // 对Android4.4已上设备设置沉浸效果
        setTintLayout();
    }

    private void initToolBar(){
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar!=null){
            setSupportActionBar(mToolbar);
        }
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    public ThemeUtils getThemeUtils() {
        return mThemeUtils;
    }

    /**
     * 设置toolbar属性
     * @param title 标题
     * @param showHome 是否返回
     */
    public void setupToolBar(int title,boolean showHome){
        setTitle(getString(title));
        setDisplayHomeEnable(showHome);
    }

    private void setDisplayHomeEnable(boolean showHome){
        getSupportActionBar().setDisplayShowHomeEnabled(showHome);
        getSupportActionBar().setDisplayHomeAsUpEnabled(showHome);
    }

    private void setTitle(String title){
        if(mToolbar!=null){
            mToolbar.setTitle(title);
        }
    }

    /**
     * 为Android 4.4以上设备使用沉浸时效果
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setTintLayout() {
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setTintColor(mThemeUtils.getThemePrimaryDarkColor(this));
    }

    /**
     * 指定对应的布局文件
     * @return 布局id
     */
    protected abstract int initLayout();

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        //点击主题颜色切换 并且 是第一次进入这个界面 才需要执行 reload，如果不使用hasRecreate这个
        //标志位控制的话 会导致reload方法循环执行 从而使得程序崩溃
        if (mThemeUtils.isChanged() && !hasRecreate) {
            reload();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        hasRecreate = false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 重启Activity，重新执行一次Activity的生命周期
     */
    public void reload() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);

//      recreate();
    }
}
