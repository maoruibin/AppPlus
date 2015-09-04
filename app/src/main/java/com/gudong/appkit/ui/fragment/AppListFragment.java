package com.gudong.appkit.ui.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
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
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.progress.CircularProgressDrawable;
import com.gudong.appkit.R;
import com.gudong.appkit.adapter.AppInfoListAdapter;
import com.gudong.appkit.dao.AppInfoEngine;
import com.gudong.appkit.entity.AppEntity;
import com.gudong.appkit.utils.DialogUtil;
import com.gudong.appkit.utils.FileUtil;
import com.gudong.appkit.utils.ThemeUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mao on 15/7/8.
 */
public class AppListFragment extends Fragment implements AppInfoListAdapter.IClickPopupMenuItem, AppInfoListAdapter.IClickListItem {
    /**最近列表**/
    public static final int KEY_RECENT = 0;
    /**所有app列表**/
    public static final int KEY_ALL = 1;
    /**搜索结果列表**/
    public static final int KEY_SEARCH = -1;
    private static final String SCHEME = "package";
    private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
    private static final String APP_PKG_NAME_22 = "pkg";
    private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
    private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";

    private AppInfoEngine mEngine;
    private Handler mHandler;
    private RecyclerView mRecyclerView;

    private RelativeLayout mRlLoadLayout;
    private ProgressBar mPbLoading;
    private TextView mTvPoint;
    private AppInfoListAdapter mAdapter;
    /**Fragment列表的类型变量，小于0表示是搜索结果对应的列表Fragment，大于等于0，则是正常的用于显示App的列表Fragment**/
    private int mType = KEY_RECENT;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEngine =  AppInfoEngine.getInstance(getActivity());
        mType = getArguments().getInt("type");
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                List<AppEntity> result = (List<AppEntity>) msg.obj;
                setData(result,msg.what);
            }
        };
    }

    private String getErrorInfo(int type){
        switch (type){
            case KEY_RECENT:
                return getString(R.string.app_list_error_recent);
            case KEY_ALL:
                return getString(R.string.app_list_error_recent);
            case KEY_SEARCH:
                return getString(R.string.app_list_error_all);
            default:
                return getString(R.string.app_list_error_recent);
        }
    }

    private String getEmptyInfo(int type){
        switch (type){
            case KEY_RECENT:
                return getString(R.string.app_list_empty_recent);
            case KEY_ALL:
                return getString(R.string.app_list_empty_all);
            case KEY_SEARCH:
                return getString(R.string.app_list_empty_search);
            default:
                return getString(R.string.app_list_empty_recent);
        }
    }

    public  static AppListFragment getInstance(int type){
        AppListFragment fragment = new AppListFragment();
        Bundle bundle = new Bundle(1);
        bundle.putInt("type",type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_app_list, container, false);
        setupRecyclerView(rootView);
        setupLoadLayout(rootView);
        return rootView;
    }

    private void setupLoadLayout(View rootView){
        mRlLoadLayout = (RelativeLayout) rootView.findViewById(R.id.rl_app_list_load);
        mPbLoading = (ProgressBar) rootView.findViewById(R.id.pb_app_list_loading);
        mTvPoint = (TextView) rootView.findViewById(R.id.tv_app_list_point);

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP){
            mPbLoading.setIndeterminateDrawable(new CircularProgressDrawable(getResources().getColor(R.color.colorAccent), getResources().getDimension(R.dimen.loading_border_width)));
        }
        mRlLoadLayout.setVisibility(View.GONE);
    }

    private void loadingData(String loadingInfo){
        mRecyclerView.setVisibility(View.GONE);
        mRlLoadLayout.setVisibility(View.VISIBLE);
        mTvPoint.setText(loadingInfo);
    }

    private void loadingDataEmpty(String emptyInfo){
        mRecyclerView.setVisibility(View.GONE);
        mRlLoadLayout.setVisibility(View.VISIBLE);
        mTvPoint.setText(emptyInfo);
        mPbLoading.setVisibility(View.GONE);
    }

    private void loadingDataError(String errorInfo){
        mRecyclerView.setVisibility(View.GONE);
        mRlLoadLayout.setVisibility(View.VISIBLE);
        mTvPoint.setText(errorInfo);
        mPbLoading.setVisibility(View.GONE);
    }

    private void loadingFinish(){
        mRecyclerView.setVisibility(View.VISIBLE);
        mRlLoadLayout.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mType>=0){
            fillData();
        }
    }

    private void setupRecyclerView(View rootView) {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new AppInfoListAdapter(getActivity(),new ArrayList<AppEntity>());
        mAdapter.setClickPopupMenuItem(this);
        mAdapter.setClickListItem(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private synchronized void fillData(){
        loadingData(getString(R.string.app_list_loading));
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<AppEntity>list = null;
                switch (mType){
                    case KEY_RECENT:
                        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP){
                            list = mEngine.getRecentAppList();
                        }else{
                            list = mEngine.getRecentAppInfo();
                        }
                        break;
                    case KEY_ALL:
                        list = mEngine.getInstalledAppList();
                        break;
                }
                mHandler.sendMessage(mHandler.obtainMessage(mType,list));
            }
        }).start();
    }

    /**
     * 为列表设置数据
     */
    public void setData(List<AppEntity>result,int type){
        if(result == null){
            loadingDataError(getErrorInfo(type));
            return;
        }
        if(result.isEmpty()){
            loadingDataEmpty(getEmptyInfo(type));
            return;
        }
        mAdapter.update(result);
        loadingFinish();
    }

    /**
     * 清空列表数据
     */
    public void clearData(){
        mAdapter.update(new ArrayList<AppEntity>());
    }

    @Override
    public void onClickMenuItem(int itemId, AppEntity entity) {
        switch (itemId){
            case R.id.pop_export:
                onClickExport(entity);
                MobclickAgent.onEvent(getActivity(), "pop_export");
                break;
            case R.id.pop_share:
                onTransferClick(entity);
                MobclickAgent.onEvent(getActivity(), "pop_share");
                break;
            case R.id.pop_detail:
                showInstalledAppDetails(entity);
                MobclickAgent.onEvent(getActivity(), "pop_detail");
                break;
            case R.id.pop_open:
                onOpenClick(entity);
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
            if(packageName.contains("tencent") || packageName.contains("blue")) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(entity.getSrcPath())));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
            }
        }

        LabeledIntent[] extraIntents = intentList.toArray(new LabeledIntent[intentList.size()]);

        Intent openInChooser = Intent.createChooser(intentList.remove(0), warpChooserTitle(entity.getAppName()));
        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
        startActivity(openInChooser);
    }

    /**
     * warp choose title and make app title accent
     * @param appName app name
     * @return warped chooser title
     */
    private SpannableStringBuilder warpChooserTitle(String appName){
        String title = String.format(getString(R.string.select_transfer_way_apk, appName));
        ForegroundColorSpan fontSpanRed = new ForegroundColorSpan(getResources().getColor(R.color.colorAccent));
        int start = 2;
        int end = start+appName.length()+3;
        SpannableStringBuilder mSpannableBuilder = new SpannableStringBuilder(title);
        mSpannableBuilder.setSpan(fontSpanRed,start,end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return mSpannableBuilder;
    }

    private void onClickExport(AppEntity entity){
        //判断sd卡是否挂载
        if(!FileUtil.isSdCardOnMounted()){
            DialogUtil.showSinglePointDialog(getActivity(), getString(R.string.dialog_message_no_sdcard));
            return;
        }

        final File srcFile = new File(entity.getSrcPath());
        File exportParentFile = new File(FileUtil.getSDPath(),"App+导出目录");
        if(!exportParentFile.exists()){
            exportParentFile.mkdir();
        }

        String exportFileName = entity.getAppName()+".apk";
        final File exportFile = new File(exportParentFile,exportFileName);
        String contentInfo = String.format(getString(R.string.dialog_message_file_exist), exportFileName, exportFile.getParentFile().getAbsolutePath());
        if(exportFile.exists()){
            new MaterialDialog.Builder(getActivity())
                    .title(R.string.title_export)
                    .content(contentInfo)
                    .positiveText(R.string.dialog_confirm_yes)
                    .negativeText(R.string.dialog_now_watch)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                            copyFile(srcFile, exportFile);
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            super.onNegative(dialog);
                            browseFile(exportFile.getParentFile());
                        }
                    })
                    .show();
        }else{
            copyFile(srcFile, exportFile);
        }
    }


    private void onOpenClick(AppEntity entity) {
//        Intent intent = new Intent();
//        intent.setClassName("package.name", entity.getPackageName());
//        startActivity(intent);

//        Intent startApp = new Intent();
//        ComponentName component = new ComponentName(
//                entity.getPackageName(),
//                rowClicked.getClassName());
//        startApp.setComponent(component);
//        startApp.setAction(Intent.ACTION_MAIN);
//
//        startActivity(startApp);
    }

    private void copyFile(File srcFile, final File exportFile) {

        final MaterialDialog progressDialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.title_export)
                .content(R.string.please_wait)
                .progress(true, 0).build();


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
                String contentInfo = String.format(getString(R.string.dialog_message_export_finish),exportFile.getName(),exportFile.getParentFile().getAbsolutePath());
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.title_export_finish)
                        .content(contentInfo)
                        .positiveText(R.string.dialog_confirm_watch)
                        .negativeText(R.string.dialog_cancel_watch)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                browseFile(exportFile.getParentFile());
                            }
                        })
                        .show();
            }

            @Override
            protected Boolean doInBackground(File... params) {
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
        ObjectAnimator animatorRotation = ObjectAnimator.ofFloat(iconView,"rotation",0,360);
        ObjectAnimator scaleRotationX = ObjectAnimator.ofFloat(iconView,"scaleX",0,1F);
        ObjectAnimator scaleRotationY = ObjectAnimator.ofFloat(iconView,"scaleY",0,1F);
        AnimatorSet animationSet = new AnimatorSet();
        animationSet.playTogether(animatorRotation, scaleRotationY, scaleRotationX);
        animationSet.setDuration(500);
        animationSet.start();
    }
}

