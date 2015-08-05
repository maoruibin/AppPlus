package com.gudong.appkit.ui.base;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.gudong.appkit.R;
import com.gudong.appkit.utils.ThemeUtils;
import com.readystatesoftware.systembartint.SystemBarTintManager;

/**
 * Created by mao on 7/16/15.
 */
public abstract class BaseActivity extends AppCompatActivity {
    private ThemeUtils mThemeUtils;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mThemeUtils = new ThemeUtils(this);
        // 设置当前主题 （白天模式或者夜间模式）
        setTheme(mThemeUtils.getCurrent());
        super.onCreate(savedInstanceState);
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

    /**
     * 设置toolbar属性
     * @param title 标题
     * @param showHome 是否返回
     */
    public void setupToolBar(int title,boolean showHome){
        setTitle(getString(title));
        setDisplayShowHomeEnabled(showHome);

    }

    private void setDisplayShowHomeEnabled(boolean showHome){
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
    @TargetApi(19)
    private void setTintLayout() {
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setTintColor(getResources().getColor(R.color.colorPrimary));
    }

    /**
     * 指定对应的布局文件
     * @return 布局id
     */
    protected abstract int initLayout();

    @Override
    protected void onResume() {
        super.onResume();
        if (mThemeUtils.isChanged()) {
            setTheme(mThemeUtils.getCurrent());
            recreate();
        }
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
}
