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

package com.gudong.appkit.ui.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import com.gudong.appkit.App;
import com.gudong.appkit.R;
import com.gudong.appkit.adapter.AppInfoListAdapter;
import com.gudong.appkit.dao.AppEntity;
import com.gudong.appkit.dao.DataHelper;
import com.gudong.appkit.event.PermissionException;
import com.gudong.appkit.event.RxBus;
import com.gudong.appkit.event.RxEvent;
import com.gudong.appkit.ui.activity.AppOperateActivity;
import com.gudong.appkit.ui.activity.BaseActivity;
import com.gudong.appkit.ui.control.NavigationManager;
import com.gudong.appkit.utils.ActionUtil;
import com.gudong.appkit.utils.UStats;
import com.gudong.appkit.utils.Utils;
import com.gudong.appkit.utils.logger.Logger;
import com.gudong.appkit.view.DividerItemDecoration;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by mao on 15/7/8.
 */
public class AppListFragment extends Fragment implements AppInfoListAdapter.IClickPopupMenuItem, AppInfoListAdapter.IClickListItem{
    private static final int GRUNT_RECENT_PREMISSION = 100;
    public static final String KEY_TYPE = "type";
    public static final int TYPE_RECENT = 0;
    public static final int TYPE_INSTALLED = 1;
    public static final int TYPE_FAVORITE = 2;
    public static final int TYPE_SEARCH = 3;


    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AppInfoListAdapter mAdapter;

    private BaseActivity parent;

    /**
     * Fragment列表的类型变量，小于0表示是搜索结果对应的列表Fragment，大于等于0，则是正常的用于显示App的列表Fragment
     **/
    private int mType = 0;

    protected int initLayout() {
        return R.layout.fragment_app_list;
    }

    public static AppListFragment getInstance(int type) {
        AppListFragment fragment = new AppListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType = getArguments().getInt(KEY_TYPE);
        parent = (BaseActivity) getActivity();
        // subscribe event from RxBus
        subscribeEvents();
    }

    private void subscribeEvents() {
        RxBus.getInstance()
                .toObservable()
                .subscribe(new Action1() {
                    @Override
                    public void call(Object o) {
                        if(o instanceof RxEvent){
                            RxEvent msg = (RxEvent) o;
                            List<AppEntity> list = new ArrayList<AppEntity>();
                            if(mAdapter!=null){
                                list = mAdapter.getListData();
                            }
                            dealRxEvent(msg, list);
                        }
                    }
                });
    }

    private void dealRxEvent(RxEvent msg, List<AppEntity> list) {
        switch (msg.getType()) {
            case RECENT_LIST_IS_SHOW_SELF_CHANGE:
                if (mType == AppListFragment.TYPE_RECENT) {
                    boolean isShowSelf = !Utils.isShowSelf();
                    AppEntity appPlus = DataHelper.getAppPlusEntity();
                    if (isShowSelf) {
                        mAdapter.addItem(0, appPlus);
                    } else {
                        mAdapter.removeItem(appPlus);
                    }
                    if (!mAdapter.getListData().isEmpty()) {
                        mRecyclerView.scrollToPosition(0);
                    }
                }
                break;
            case LIST_ITEM_BRIEF_MODE_CHANGE:
                if (getActivity()!=null && Utils.isBriefMode()) {
                    MobclickAgent.onEvent(getActivity(), "setting_brief_is_true");
                }
                mAdapter.setBriefMode(!Utils.isBriefMode());
                break;
            case UNINSTALL_APPLICATION_FROM_SYSTEM:
                AppEntity uninstalledEntity = msg.getData().getParcelable("entity");
                Logger.i("now we found the " + uninstalledEntity.getPackageName() + " has uninstalled by user ");
                if (list.contains(uninstalledEntity)) {
                    Logger.i("list find " + uninstalledEntity.getPackageName() + " exist list ,now need remove it and update lsit");
                    mAdapter.removeItem(uninstalledEntity);
                } else {
                    Logger.i("list not contain " + uninstalledEntity.getPackageName() + " so do nothing");
                }
                break;
            case INSTALL_APPLICATION_FROM_SYSTEM:
                AppEntity installedEntity = msg.getData().getParcelable("entity");
                if (mType == AppListFragment.TYPE_INSTALLED && !list.contains(installedEntity)) {
                    mAdapter.addItem(0,installedEntity);
                    Logger.i("this is all type and list not contain " + installedEntity.getAppName() + "now add it");
                }
                break;
            case UPDATE_ENTITY_FAVORITE_STATUS:
                AppEntity updateEntity = msg.getData().getParcelable("entity");
                //mAdapter.update();
                break;
            case PREPARE_FOR_ALL_INSTALLED_APP_FINISH:
                mRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        fillData();
                        Logger.i("reload ");
                    }
                });
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(initLayout(), container, false);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        toolbar.setTitle(R.string.app_name_inner);

        toolbar.setSubtitle(getTitleString(mType));

        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);

        setupSwipeLayout(rootView);
        setupRecyclerView(rootView);
        return rootView;
    }

    private void setupSwipeLayout(View rootView) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeColors(Utils.getThemePrimaryColor(getActivity()));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fillData();
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //if the list type is search result layout,the mSwipeRefreshLayout will not need show
        if (mType == 3) {
            mSwipeRefreshLayout.setEnabled(false);
            return;
        }
        // make swipeRefreshLayout visible manually
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showRefresh();
            }
        }, 568);
        fillData();
    }

    private void loadingDataEmpty(String emptyInfo) {
        if (mType == 3) return;
        final Snackbar errorSnack = Snackbar.make(mRecyclerView, emptyInfo, Snackbar.LENGTH_LONG);
        errorSnack.setAction(R.string.action_retry, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorSnack.dismiss();
                fillData();
            }
        });
        errorSnack.show();
    }

    private void loadingDataError(String errorInfo) {

    }

    private void loadingFinish() {
        hideRefresh();
    }

    public void showRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    public void hideRefresh() {
        mSwipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        }, 1000);
    }


    private void setupRecyclerView(View rootView) {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        //every item's height is fix so use this method
        //RecyclerView can perform several optimizations
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new AppInfoListAdapter(getActivity(), Utils.isBriefMode());
        mAdapter.setClickPopupMenuItem(this);
        mAdapter.setClickListItem(this);

        SlideInBottomAnimationAdapter slideInLeftAdapter = new SlideInBottomAnimationAdapter(mAdapter);
        slideInLeftAdapter.setDuration(300);
        slideInLeftAdapter.setInterpolator(new AccelerateDecelerateInterpolator());

        mRecyclerView.setAdapter(slideInLeftAdapter);
    }

    private synchronized void fillData() {
        /**
         * should use mvp
         */
        Observable<List<AppEntity>> listObservable = null;
        switch (mType) {
            case TYPE_RECENT:
                if(Utils.isAndroidN()){
                    //Check if permission enabled
                    if (UStats.getUsageStatsList(getActivity()).isEmpty()){
                        listObservable = Observable.error(new PermissionException());
                    }else{
                        listObservable = DataHelper.getAppList(getActivity());
                    }
                }else{
                    listObservable = DataHelper.getRunningAppEntity(getActivity());
                }
                break;
            case TYPE_INSTALLED:
                listObservable = DataHelper.getAllEntityByDbAsyn();
                break;
            case TYPE_FAVORITE:
                listObservable = DataHelper.getFavoriteEntityByDbAsyn();
                break;
        }

        if (listObservable == null) return;

        Subscriber<List<AppEntity>> subscriber = new Subscriber<List<AppEntity>>() {
            @Override
            public void onNext(List<AppEntity> appEntities) {
                loadingFinish();
                setData(appEntities, mType);
            }

            @Override
            public void onCompleted() {
                loadingFinish();
            }

            @Override
            public void onError(Throwable throwable) {
                loadingFinish();
                if(throwable instanceof PermissionException){
                    final Snackbar errorSnack = Snackbar.make(mRecyclerView, "获取最近运行程序列表失败，请授予 AppPlus 相关权限。", Snackbar.LENGTH_INDEFINITE);
                    errorSnack.setAction(R.string.action_grunt, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            errorSnack.dismiss();
                            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                            startActivityForResult(intent,GRUNT_RECENT_PREMISSION);
                        }
                    });
                    errorSnack.show();
                }else{
                    throwable.printStackTrace();
                }

            }
        };

        listObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

    }

    /**
     * 为列表设置数据
     */
    public void setData(List<AppEntity> result, int type) {
        if (result == null) {
            loadingDataError(getErrorInfo(type));
            return;
        }
        if (result.isEmpty()) {
            loadingDataEmpty(getEmptyInfo(type));
        }

        mAdapter.update(result);
    }

    /**
     * 清空列表数据
     */
    public void clearData() {
        mAdapter.clear();
    }

    @Override
    public void onClickMenuItem(int itemId, AppEntity entity) {
        switch (itemId) {
            case R.id.pop_share:
                ActionUtil.shareApk(getActivity(), entity);
                MobclickAgent.onEvent(getActivity(), "pop_share");
                break;
            case R.id.pop_export:
                ActionUtil.exportApk(getActivity(), entity);
                MobclickAgent.onEvent(getActivity(), "pop_export");
                break;
            case R.id.pop_detail:
                NavigationManager.openAppDetail(getActivity(), entity.getPackageName());
                MobclickAgent.onEvent(getActivity(), "pop_detail");
                break;
            case R.id.pop_favorite:
                String point = entity.isFavorite()? "取消收藏"+entity.getAppName() : "收藏"+entity.getAppName()+"成功";
                entity.setFavorite(!entity.isFavorite());
                Toast.makeText(getContext(),point, Toast.LENGTH_SHORT).show();
                App.sDb.update(entity);
                if(!entity.isFavorite()){
                    mAdapter.removeItem(entity);
                }
                MobclickAgent.onEvent(getActivity(), "favoriteInList");
                break;
        }
    }


    @Override
    public void onClickListItemContent(View view, AppEntity entity) {
        Intent intent = new Intent(getContext(), AppOperateActivity.class);
        intent.putExtra(AppOperateActivity.EXTRA_APP_ENTITY, entity);
        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                getActivity(),
                new Pair<View, String>(view.findViewById(R.id.iv_icon),
                        AppOperateActivity.VIEW_NAME_HEADER_IMAGE));

        // Now we can start the Activity, providing the activity options as a bundle
        ActivityCompat.startActivity(getActivity(), intent, activityOptions.toBundle());
    }

    @Override
    public void onClickListItemIcon(View iconView, AppEntity entity) {
        ObjectAnimator animatorRotation = ObjectAnimator.ofFloat(iconView, "rotation", 0, 360);
        ObjectAnimator scaleRotationX = ObjectAnimator.ofFloat(iconView, "scaleX", 0, 1F);
        ObjectAnimator scaleRotationY = ObjectAnimator.ofFloat(iconView, "scaleY", 0, 1F);
        AnimatorSet animationSet = new AnimatorSet();
        animationSet.playTogether(animatorRotation, scaleRotationY, scaleRotationX);
        animationSet.setDuration(500);
        animationSet.start();
    }


    @Override
    public void onLongClickListItem(View iconView, AppEntity entity) {

    }

    private String getErrorInfo(int type) {
        if (type == 0) {
            return getString(R.string.app_list_error_recent);
        } else if (type == 1) {
            return getString(R.string.app_list_error_all);
        } else {
            return getString(R.string.app_list_error_all);
        }
    }

    private String getTitleString(int type) {
        if (type == TYPE_RECENT) {
            return getString(R.string.tab_recent);
        } else if (type == TYPE_INSTALLED) {
            return getString(R.string.tab_installed);
        } else if (type == TYPE_FAVORITE) {
            return getString(R.string.tab_favorite);
        } else {
            return "";
        }
    }

    private String getEmptyInfo(int type) {
        if (type == 0) {
            return getString(R.string.app_list_empty_recent);
        } else if (type == 1) {
            return getString(R.string.app_list_empty_all);
        } else {
            return getString(R.string.app_list_empty_search);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("==== app list","resultCode "+resultCode);
        if(requestCode == GRUNT_RECENT_PREMISSION){
            if(resultCode == 0){
                fillData();
            }
        }
    }
}

