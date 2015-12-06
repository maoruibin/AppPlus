package com.gudong.appkit.ui.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gudong.appkit.App;
import com.gudong.appkit.R;
import com.gudong.appkit.adapter.AppInfoListAdapter;
import com.gudong.appkit.dao.AppInfoEngine;
import com.gudong.appkit.entity.AppEntity;
import com.gudong.appkit.utils.DialogUtil;
import com.gudong.appkit.utils.FileUtil;
import com.gudong.appkit.utils.FormatUtil;
import com.gudong.appkit.utils.Utils;
import com.gudong.appkit.view.CircularProgressDrawable;
import com.gudong.appkit.view.DividerItemDecoration;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mao on 15/7/8.
 */
public class AppListFragment extends Fragment implements AppInfoListAdapter.IClickPopupMenuItem, AppInfoListAdapter.IClickListItem {

    public static final String KEY_TYPE = "type";

    private static final String SCHEME = "package";
    private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
    private static final String APP_PKG_NAME_22 = "pkg";
    private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
    private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";

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
        mEngine = new AppInfoEngine(getActivity().getApplicationContext());
        mType = (EListType) getArguments().getSerializable(KEY_TYPE);
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
        final Snackbar errorSnack = Snackbar.make(mRecyclerView, emptyInfo,Snackbar.LENGTH_INDEFINITE);
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
        mAdapter = new AppInfoListAdapter(getActivity(), new ArrayList<AppEntity>());
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
            case R.id.pop_export:
                onClickExport(entity);
                MobclickAgent.onEvent(getActivity(), "pop_export");
                break;
            case R.id.pop_detail:
                showInstalledAppDetails(entity);
                MobclickAgent.onEvent(getActivity(), "pop_detail");
                break;
            case R.id.pop_open:
                try {
                    Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(entity.getPackageName());
                    startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                    Snackbar.make(mRecyclerView,String.format(getString(R.string.app_list_error_all))"打开"+entity.getAppName()+"失败",Snackbar.LENGTH_LONG).show();
                }

                MobclickAgent.onEvent(getActivity(), "pop_open");
                break;
        }
    }


    @Override
    public void onClickListItemContent(AppEntity entity) {
        onTransferClick(entity);
        MobclickAgent.onEvent(getActivity(), "item_click_share");
    }

    /**
     * 打开App详情界面
     *
     * @param appEntity
     */
    private void showInstalledAppDetails(AppEntity appEntity) {
        Intent intent = new Intent();
        final int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= 9) {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Uri uri = Uri.fromParts(SCHEME, appEntity.getPackageName(), null);
            intent.setData(uri);
        } else {
            final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22
                    : APP_PKG_NAME_21);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName(APP_DETAILS_PACKAGE_NAME,
                    APP_DETAILS_CLASS_NAME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(appPkgName, appEntity.getPackageName());
        }
        startActivity(intent);
    }

    /**
     * 传送安装包
     *
     * @param entity
     */
    private void onTransferClick(AppEntity entity) {
        PackageManager pm = getActivity().getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
        List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
        for (int i = 0; i < resInfo.size(); i++) {
            ResolveInfo ri = resInfo.get(i);
            String packageName = ri.activityInfo.packageName;
            if (packageName.contains("tencent") || packageName.contains("blue")) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));

                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(entity.getSrcPath())));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
            }
        }

        LabeledIntent[] extraIntents = intentList.toArray(new LabeledIntent[intentList.size()]);

        Intent openInChooser = Intent.createChooser(intentList.remove(0), FormatUtil.warpChooserTitle(getActivity(),entity.getAppName()));
        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
        startActivity(openInChooser);
    }

    private void onClickExport(AppEntity entity) {
        //判断sd卡是否挂载
        if (!FileUtil.isSdCardOnMounted()) {
            DialogUtil.showSinglePointDialog(getActivity(), getString(R.string.dialog_message_no_sdcard));
            return;
        }

        final File srcFile = new File(entity.getSrcPath());
        File exportParentFile = new File(FileUtil.getSDPath(), "App+导出目录");
        if (!exportParentFile.exists()) {
            exportParentFile.mkdir();
        }

        String exportFileName = entity.getAppName() + ".apk";
        final File exportFile = new File(exportParentFile, exportFileName);
        String contentInfo = String.format(getString(R.string.dialog_message_file_exist), exportFileName, exportFile.getParentFile().getAbsolutePath());
        if (exportFile.exists()) {
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.title_export)
                    .setMessage(contentInfo)
                    .setPositiveButton(R.string.dialog_confirm_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            copyFile(srcFile, exportFile);
                        }
                    })
                    .setNegativeButton(R.string.dialog_now_watch, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            browseFile(exportFile.getParentFile());
                        }
                    })
                    .show();
        } else {
            copyFile(srcFile, exportFile);
        }
    }

    private void copyFile(File srcFile, final File exportFile) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_progress, null);
        ProgressBar progressBar = (ProgressBar) view.findViewById(android.R.id.progress);
        TextView textView = (TextView) view.findViewById(R.id.content);

        //改变Progress的背景为MaterialDesigner规范的样式
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            progressBar.setIndeterminateDrawable(new CircularProgressDrawable(Utils.getColorWarp(getActivity(), R.color.colorAccent), getResources().getDimension(R.dimen.loading_border_width)));
        }

        final AlertDialog progressDialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.title_export)
                .setView(view).create();
        //设置显示文字
        textView.setText(R.string.please_wait);

        new AsyncTask<File, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                progressDialog.dismiss();
                String contentInfo = String.format(getString(R.string.dialog_message_export_finish), exportFile.getName(), exportFile.getParentFile().getAbsolutePath());
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.title_export_finish)
                        .setMessage(contentInfo)
                        .setPositiveButton(R.string.dialog_confirm_watch, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                browseFile(exportFile.getParentFile());
                            }
                        })
                        .setNegativeButton(R.string.dialog_cancel_watch, null)
                        .show();
            }

            @Override
            protected Boolean doInBackground(File... params) {
                //导出速度太快了 给人工降个速 可以让用户看到有一个导出的进度条，这样更舒服点
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                File srcFile = params[0];
                File exportFile = params[1];
                try {
                    FileUtil.copyFileUsingFileChannels(srcFile, exportFile);
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        }.execute(srcFile, exportFile);
    }

    /**
     * 浏览文件夹
     *
     * @param file
     */
    void browseFile(File file) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "file/*");
        startActivity(intent);
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
        }else if(type == EListType.TYPE_RECENT.ordinal()){
            return getString(R.string.app_list_empty_all);
        }else{
            return getString(R.string.app_list_empty_search);
        }
    }
}

