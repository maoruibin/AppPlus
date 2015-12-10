package com.gudong.appkit.ui.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Build;
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
import com.gudong.appkit.dao.AppInfoEngine;
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
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mao on 15/7/8.
 */
public class AppListFragment extends Fragment implements AppInfoListAdapter.IClickPopupMenuItem, AppInfoListAdapter.IClickListItem, Subscribe {

    public static final String KEY_TYPE = "type";

    private AppInfoEngine mEngine;
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
    private EListType mType = EListType.TYPE_RECENT;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEngine = AppInfoEngine.getInstance(getActivity().getApplicationContext());
        mType = (EListType) getArguments().getSerializable(KEY_TYPE);
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

    protected int initLayout(){
        return R.layout.fragment_app_list;
    }

    public static AppListFragment getInstance(EListType type) {
        AppListFragment fragment = new AppListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
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
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fillData();
            }
        });
        //if the list type is search result,the mSwipeRefreshLayout will unable
        if(mType == EListType.TYPE_SEARCH){
            mSwipeRefreshLayout.setEnabled(false);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fillData();
    }

    private void loadingDataEmpty(String emptyInfo) {
        if(mType == EListType.TYPE_SEARCH)return;
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

    public void hideRefresh() {
        // 防止刷新消失太快，让子弹飞一会儿. do not use lambda!!
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
        prepareFillData();
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<AppEntity> list = null;
                switch (mType) {
                    case TYPE_RECENT:
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                            list = mEngine.getRecentAppList();
                        } else {
                            list = mEngine.getRecentAppInfo();
                        }
                        break;
                    case TYPE_ALL:
                        list = App.sDb.query(AppEntity.class);
                        break;
                }
                mHandler.sendMessage(mHandler.obtainMessage(mType.ordinal(), list));
            }
        }).start();
    }

    private void prepareFillData(){
        if(!mSwipeRefreshLayout.isRefreshing()){
            mSwipeRefreshLayout.setRefreshing(true);
        }
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
        if(type == EListType.TYPE_RECENT.ordinal()){
            return getString(R.string.app_list_error_recent);
        }else if(type == EListType.TYPE_RECENT.ordinal()){
            return getString(R.string.app_list_error_all);
        }else{
            return getString(R.string.app_list_error_all);
        }
    }

    private String getEmptyInfo(int type) {
        if(type == EListType.TYPE_RECENT.ordinal()){
            return getString(R.string.app_list_empty_recent);
        }else if(type == EListType.TYPE_ALL.ordinal()){
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
                if(mType == EListType.TYPE_RECENT){
                    boolean isShowSelf = !Utils.isShowSelf(getActivity());
                    AppEntity appPlus = DBHelper.getAppPlusEntity(getActivity());
                    if(isShowSelf){
//                        list.add(0,appPlus);
                        mAdapter.addItem(0,appPlus);
                    }else{
                        mAdapter.removeItem(appPlus);
//                        list.remove(appPlus);
                    }
                }
                break;
            case LIST_ITEM_BRIEF_MODE_CHANGE:
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
                if(mType == EListType.TYPE_ALL && !list.contains(installedEntity)){
                    list.add(installedEntity);
                    mAdapter.update(list);
                    Logger.i("this is all type and list not contain "+installedEntity.getAppName()+"now add it" );
                }
                break;
            case PREPARE_FOR_ALL_INSTALLED_APP_FINISH:
                if(viewIsEmpty()){
                    Logger.i(mType.getTitle()+" is empty and fill data");
                    mRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            fillData();
                        }
                    });
                }else{
                    Logger.i(mType.getTitle()+" has data");
                }
                break;
        }
    }

    private boolean viewIsEmpty(){
        return mAdapter.getListData().isEmpty();
    }
}

