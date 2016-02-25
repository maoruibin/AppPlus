package com.gudong.appkit.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import com.gudong.appkit.R;
import com.gudong.appkit.adapter.AppFileListAdapter;
import com.gudong.appkit.dao.AppEntity;
import com.gudong.appkit.dao.DataHelper;
import com.gudong.appkit.event.RxBus;
import com.gudong.appkit.event.RxEvent;
import com.gudong.appkit.ui.activity.BaseActivity;
import com.gudong.appkit.utils.ActionUtil;
import com.gudong.appkit.utils.Utils;
import com.gudong.appkit.view.DividerItemDecoration;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * Created by GuDong on 2/4/16 22:25.
 * Contact with 1252768410@qq.com.
 */
public class AppFileListFragment extends Fragment implements AppFileListAdapter.IClickPopupMenuItem, AppFileListAdapter.IClickListItem{

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AppFileListAdapter mAdapter;

//    MaterialCab mCab;

    private BaseActivity mParent;

    protected int initLayout() {
        return R.layout.fragment_app_list;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subscribeEvents();
        mParent = (BaseActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(initLayout(), container, false);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        toolbar.setTitle(R.string.app_name);

        toolbar.setSubtitle(R.string.tab_exported);

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

    private void setupRecyclerView(View rootView) {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        //every item's height is fix so use this method
        //RecyclerView can perform several optimizations
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new AppFileListAdapter(getActivity());
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
        Observable<List<AppEntity>> listObservable = DataHelper.getExportedAppEntity();

        if (listObservable == null) return;

        Subscriber<List<AppEntity>> subscriber = new Subscriber<List<AppEntity>>() {
            @Override
            public void onNext(List<AppEntity> appEntities) {
                loadingFinish();
                setData(appEntities);
            }

            @Override
            public void onCompleted() {
                loadingFinish();
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }
        };

        listObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

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

    /**
     * 为列表设置数据
     */
    public void setData(List<AppEntity> result) {
        if (result == null) {
            loadingDataError("");
            return;
        }
        if (result.isEmpty()) {
            loadingDataEmpty("您目前尚未导出过任何 APK 文件");
        }

        mAdapter.update(result);
    }

    private void loadingDataEmpty(String emptyInfo) {
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

    @Override
    public void onClickMenuItem(int itemId, AppEntity entity) {
        switch (itemId) {
            case R.id.pop_file_delete:
                ActionUtil.deleteApkFile(getActivity(), entity);
                MobclickAgent.onEvent(getActivity(), "pop_file_delete");
                break;
            case R.id.pop_file_install:
                ActionUtil.installApp(getActivity(), entity);
                MobclickAgent.onEvent(getActivity(), "pop_file_install");
                break;
        }
    }

    private void subscribeEvents() {
        RxBus.getInstance()
                .toObservable()
                .subscribe(new Action1() {
                    @Override
                    public void call(Object o) {
                        if(o instanceof RxEvent){
                            RxEvent msg = (RxEvent) o;
                            switch (msg.getType()){
                                case DELETE_SINGLE_EXPORT_FILE_SUC:
                                    AppEntity entity = msg.getData().getParcelable("entity");
                                    if(entity != null){
                                     mAdapter.removeItem(entity);
                                    }
                                    break;
                                case DELETE_SINGLE_EXPORT_FILE_FAIL:
                                    Toast.makeText(getActivity(), "删除失败", Toast.LENGTH_SHORT).show();
                                    break;
                            }

                        }
                    }
                });
    }

    @Override
    public void onClickListItemContent(View view, AppEntity entity) {
        ActionUtil.installApp(getActivity(),entity);
    }

    @Override
    public void onClickListItemIcon(View iconView, AppEntity entity) {

    }

    @Override
    public void onLongClickListItem(View iconView, AppEntity entity) {
    }
}
