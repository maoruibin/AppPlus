package com.gudong.appkit.ui.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.gudong.appkit.R;
import com.gudong.appkit.ui.control.ThemeControl;
import com.gudong.appkit.utils.Utils;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by mao on 7/16/15.
 */
public abstract class BaseActivity extends AppCompatActivity {
    private ThemeControl mThemeUtils;
    private Toolbar mToolbar;
    private SystemBarTintManager mBarTintManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mThemeUtils = new ThemeControl(this);
        // 设置当前主题
        setTheme(mThemeUtils.getTheme(this));
//        hasRecreate = true;
        super.onCreate(savedInstanceState);
        // 设置是否对日志信息进行加密, true 加密
        AnalyticsConfig.enableEncrypt(true);
        // 设置布局
        if(initLayout()>0){
            setContentView(initLayout());
        }
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

    public ThemeControl getThemeUtils() {
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

    private void setToolbarTitle(String title){
        if(mToolbar!=null){
            mToolbar.setTitle(title);
        }
    }

    /**
     * 为Android 4.4以上设备使用沉浸时效果
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setTintLayout() {
        mBarTintManager = new SystemBarTintManager(this);
        mBarTintManager.setStatusBarTintEnabled(true);
        mBarTintManager.setNavigationBarTintEnabled(true);
        mBarTintManager.setTintColor(Utils.getThemePrimaryColor(this));
    }

    protected void setStatusBarColorRes(int res) {
        if (mBarTintManager != null)
            mBarTintManager.setStatusBarTintResource(res);
    }

    /**
     * set layout file
     * @return res id of layout,if return value less then zero ,it indicate this activity will not set content view
     */
    protected abstract int initLayout();

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        if (mThemeUtils.isChanged()) {
            reload();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
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
