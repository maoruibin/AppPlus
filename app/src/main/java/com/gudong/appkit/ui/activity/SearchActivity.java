package com.gudong.appkit.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.gudong.appkit.App;
import com.gudong.appkit.R;
import com.gudong.appkit.entity.AppEntity;
import com.gudong.appkit.ui.base.BaseActivity;
import com.gudong.appkit.ui.fragment.AppListFragment;
import com.gudong.appkit.ui.fragment.EListType;
import com.gudong.appkit.utils.logger.Logger;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.List;

public class SearchActivity extends BaseActivity implements SearchView.OnQueryTextListener {
    private AppListFragment mSearchResultFragment;
    SearchView mSearchView;
    private InputMethodManager mImm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        initSearchContent();
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_search;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search,menu);

        mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));

        mSearchView.setOnQueryTextListener(this);
        mSearchView.setQueryHint(getString(R.string.search_app_hint));

        mSearchView.setIconifiedByDefault(false);
        mSearchView.setIconified(false);

        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.menu_search), new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                finish();
                return false;
            }
        });

        menu.findItem(R.id.menu_search).expandActionView();


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        onQueryTextChange(query);
        hideInputManager();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            mSearchResultFragment.clearData();
        } else {
            QueryBuilder queryBuilder = new QueryBuilder(AppEntity.class);
            queryBuilder = queryBuilder.where("appName like ?",new String[]{'%'+newText+'%'});
            List<AppEntity> result = App.sDb.query(queryBuilder);
            Logger.i("搜索结果大小为 "+result.size());
            mSearchResultFragment.setData(result, EListType.TYPE_SEARCH.ordinal());
        }
        return true;
    }

    private void hideInputManager() {
        if (mSearchView != null) {
            if (mImm != null) {
                mImm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
            }
            mSearchView.clearFocus();
        }
    }

    private void initSearchContent() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        mSearchResultFragment = AppListFragment.getInstance(EListType.TYPE_SEARCH);
        fragmentTransaction.replace(R.id.fl_container, mSearchResultFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

}
