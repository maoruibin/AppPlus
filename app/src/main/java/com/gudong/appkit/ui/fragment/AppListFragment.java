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

package com.gudong.appkit.ui.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gudong.appkit.App;
import com.gudong.appkit.R;
import com.gudong.appkit.adapter.AppInfoListAdapter;
import com.gudong.appkit.dao.AppEntity;
import com.gudong.appkit.dao.DBHelper;
import com.gudong.appkit.event.EEvent;
import com.gudong.appkit.event.EventCenter;
import com.gudong.appkit.event.Subscribe;
import com.gudong.appkit.ui.activity.AppActivity;
import com.gudong.appkit.ui.control.NavigationManager;
import com.gudong.appkit.ui.helper.AppItemAnimator;
import com.gudong.appkit.utils.ActionUtil;
import com.gudong.appkit.utils.Utils;
import com.gudong.appkit.utils.logger.Logger;
import com.gudong.appkit.view.DividerItemDecoration;
import com.jaredrummler.android.processes.ProcessManager;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mao on 15/7/8.
 */
public class AppListFragment extends Fragment implements AppInfoListAdapter.IClickPopupMenuItem, AppInfoListAdapter.IClickListItem, Subscribe {

    public static final String KEY_TYPE = "type";


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.obj != null && msg.obj instanceof List) {
                List<AppEntity> result = (List<AppEntity>) msg.obj;
                loadingFinish();
                setData(result, msg.what);
            }
        }
    };;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AppInfoListAdapter mAdapter;
    /**
     * Fragment列表的类型变量，小于0表示是搜索结果对应的列表Fragment，大于等于0，则是正常的用于显示App的列表Fragment
     **/
    private int mType = 0;

    protected int initLayout(){
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
        EventCenter.getInstance().registerEvent(EEvent.RECENT_LIST_IS_SHOW_SELF_CHANGE,this);
        EventCenter.getInstance().registerEvent(EEvent.UNINSTALL_APPLICATION_FROM_SYSTEM,this);
        EventCenter.getInstance().registerEvent(EEvent.INSTALL_APPLICATION_FROM_SYSTEM,this);
        EventCenter.getInstance().registerEvent(EEvent.PREPARE_FOR_ALL_INSTALLED_APP_FINISH,this);
        EventCenter.getInstance().registerEvent(EEvent.LIST_ITEM_BRIEF_MODE_CHANGE,this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventCenter.getInstance().unregisterEvent(EEvent.RECENT_LIST_IS_SHOW_SELF_CHANGE,this);
        EventCenter.getInstance().unregisterEvent(EEvent.UNINSTALL_APPLICATION_FROM_SYSTEM,this);
        EventCenter.getInstance().unregisterEvent(EEvent.INSTALL_APPLICATION_FROM_SYSTEM,this);
        EventCenter.getInstance().unregisterEvent(EEvent.PREPARE_FOR_ALL_INSTALLED_APP_FINISH,this);
        EventCenter.getInstance().unregisterEvent(EEvent.LIST_ITEM_BRIEF_MODE_CHANGE,this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(initLayout(), container, false);
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
        //if the list type is search result,the mSwipeRefreshLayout will unable
        if(mType == 3){
            mSwipeRefreshLayout.setEnabled(false);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        if(mType == 3)return;
        final Snackbar errorSnack = Snackbar.make(mRecyclerView, emptyInfo,Snackbar.LENGTH_LONG);
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
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL_LIST));
//        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setItemAnimator(new AppItemAnimator());
        //every item's height is fix so use this method
        //RecyclerView can perform several optimizations
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new AppInfoListAdapter(getActivity(),Utils.isBriefMode(getActivity()));
        mAdapter.setClickPopupMenuItem(this);
        mAdapter.setClickListItem(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private synchronized void fillData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<AppEntity> list = null;
                switch (mType) {
                    case 0:
                        list = getRunningAppEntity(getActivity());
                        break;
                    case 1:
                        list = App.sDb.query(AppEntity.class);
                        break;
                }
                mHandler.sendMessage(mHandler.obtainMessage(mType, list));
            }
        }).start();
    }

    private  List<AppEntity>getRunningAppEntity(Context ctx){
        List<ActivityManager.RunningAppProcessInfo> runningList = ProcessManager.getRunningAppProcessInfo(ctx);
        List<AppEntity>list = new ArrayList<>();
        for (ActivityManager.RunningAppProcessInfo processInfo : runningList){
            String packageName = processInfo.processName;
            if (isNotShowSelf(ctx,packageName)) continue;
            AppEntity entity = DBHelper.getAppByPackageName(packageName);
            if(entity == null)continue;
            list.add(entity);
        }
        return list;
    }

    /**
     * check running list should show AppPlus or not
     * @param packagename
     * @return true if show else false
     */
    private static boolean isNotShowSelf(Context ctx, String packagename){
        return !Utils.isShowSelf(ctx) && packagename.equals(ctx.getPackageName());
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
        mAdapter.update(new ArrayList<AppEntity>());
    }

    @Override
    public void onClickMenuItem(int itemId, AppEntity entity) {
        switch (itemId) {
            case R.id.pop_share:
                ActionUtil.shareApk(getActivity(),entity);
                MobclickAgent.onEvent(getActivity(), "pop_share");
                break;
            case R.id.pop_export:
                ActionUtil.exportApk(getActivity(),entity);
                MobclickAgent.onEvent(getActivity(), "pop_export");
                break;
            case R.id.pop_detail:
                NavigationManager.openAppDetail(getActivity(),entity.getPackageName());
                MobclickAgent.onEvent(getActivity(), "pop_detail");
                break;
        }
    }


    @Override
    public void onClickListItemContent(View view,AppEntity entity) {
        Intent intent = new Intent(getContext(), AppActivity.class);
        intent.putExtra(AppActivity.EXTRA_APP_ENTITY, entity);
        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                getActivity(),
                new Pair<View, String>(view.findViewById(R.id.iv_icon),
                        AppActivity.VIEW_NAME_HEADER_IMAGE));

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



    private String getErrorInfo(int type) {
        if(type == 0){
            return getString(R.string.app_list_error_recent);
        }else if(type == 1){
            return getString(R.string.app_list_error_all);
        }else{
            return getString(R.string.app_list_error_all);
        }
    }

    private String getEmptyInfo(int type) {
        if(type == 0){
            return getString(R.string.app_list_empty_recent);
        }else if(type == 1){
            return getString(R.string.app_list_empty_all);
        }else{
            return getString(R.string.app_list_empty_search);
        }
    }

    @Override
    public void update(EEvent event,Bundle data) {
        List<AppEntity>list = mAdapter.getListData();
        switch (event){
            case RECENT_LIST_IS_SHOW_SELF_CHANGE:
                if(mType == 0){
                    boolean isShowSelf = !Utils.isShowSelf(getActivity());
                    AppEntity appPlus = DBHelper.getAppPlusEntity(getActivity());
                    if(isShowSelf){
                        mAdapter.addItem(0,appPlus);
                    }else{
                        mAdapter.removeItem(appPlus);
                    }
                    if(!mAdapter.getListData().isEmpty()){
                        mRecyclerView.scrollToPosition(0);
                    }
                }
                break;
            case LIST_ITEM_BRIEF_MODE_CHANGE:
                if(Utils.isBriefMode(getActivity())){
                    MobclickAgent.onEvent(getActivity(), "setting_brief_is_true");
                }
                mAdapter.setBriefMode(!Utils.isBriefMode(getActivity()));
                break;
            case UNINSTALL_APPLICATION_FROM_SYSTEM:
                AppEntity uninstalledEntity = data.getParcelable("entity");
                Logger.i("now we found the "+uninstalledEntity.getPackageName()+" has uninstalled by user ");
                if(list.contains(uninstalledEntity)){
                    Logger.i("list find "+uninstalledEntity.getPackageName()+" exist list ,now need remove it and update lsit");
                    list.remove(uninstalledEntity);
                    mAdapter.update(list);
                }else{
                    Logger.i("list not contain "+uninstalledEntity.getPackageName()+" so do nothing" );
                }
                break;
            case INSTALL_APPLICATION_FROM_SYSTEM:
                AppEntity installedEntity = data.getParcelable("entity");
                if(mType == 1 && !list.contains(installedEntity)){
                    list.add(installedEntity);
                    mAdapter.update(list);
                    Logger.i("this is all type and list not contain "+installedEntity.getAppName()+"now add it" );
                }
                break;
            case PREPARE_FOR_ALL_INSTALLED_APP_FINISH:
                mRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        fillData();
                    }
                });
                break;
        }
    }
}

