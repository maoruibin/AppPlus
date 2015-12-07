package com.gudong.appkit.dao;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import com.gudong.appkit.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 获取app信息的引擎
 * Created by mao on 15/7/8.
 */
public class AppInfoEngine {
    private Context mContext;
    private PackageManager mPackageManager;
//    private static AppInfoEngine mInstance;
    public AppInfoEngine(Context context) {
        this.mContext = context;
        mPackageManager = mContext.getApplicationContext().getPackageManager();
        Log.i("----","new mPackageManager");
    }

//    public synchronized static AppInfoEngine getInstance(Context context){
//        if(mInstance == null){
//            mInstance = new AppInfoEngine(context);
//        }
//        return mInstance;
//    }

    /**
     * 获取已安装的App信息list
     * @return
     */
    public List<AppEntity> getInstalledAppList() {
        List<AppEntity> list = new ArrayList<>();
        List<ApplicationInfo> installedApplications = null;
        installedApplications = mPackageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo appInfo : installedApplications) {
            if(!isUserApp(appInfo))continue;
            AppEntity entity = warpAppEntity(appInfo);
            if (entity == null)continue;;
            list.add(entity);
        }
        return list;
    }

    /**
     * 获取最近运行的程序
     * @return
     */
    public List<AppEntity> getRecentAppList() {
        List<AppEntity> list = new ArrayList<>();
        ActivityManager mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RecentTaskInfo> recentTasks = mActivityManager.getRecentTasks(10, 0);
        for (ActivityManager.RecentTaskInfo taskInfo : recentTasks) {

            Intent intent = taskInfo.baseIntent;
            ResolveInfo resolveInfo = mPackageManager.resolveActivity(intent, 0);
            if (resolveInfo == null)continue;

            if (isSystemApp(resolveInfo)) continue;

            ActivityInfo activityInfo = resolveInfo.activityInfo;
            if(activityInfo==null)continue;

            if (isShowSelf(activityInfo.packageName)) continue;
            AppEntity entity = new AppEntity();
            Bitmap bitmap = drawableToBitmap(resolveInfo.loadIcon(mPackageManager));
            entity.setAppIconData(formatBitmapToBytes(bitmap));
            entity.setAppName(resolveInfo.loadLabel(mPackageManager).toString());
            entity.setPackageName(activityInfo.packageName);

            ApplicationInfo applicationInfo = activityInfo.applicationInfo;
            if (applicationInfo == null)continue;

            if(applicationInfo.publicSourceDir!= null){
                entity.setSrcPath(applicationInfo.publicSourceDir);
            }
            list.add(entity);
        }
        return list;
    }

    private byte[] formatBitmapToBytes(Bitmap bitmap){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,bos);
        try {
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }

    /**
     * 判断是不是在最近列表显示App+
     * @param packagename
     * @return 如果显示返回false 否则返回true
     */
    private boolean isShowSelf(String packagename){
        return !Utils.isShowSelf(mContext) && isSelf(packagename);
    }

    public boolean isSystemApp(ResolveInfo resolveInfo) {
        if (resolveInfo == null) return false;
        try {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            if (activityInfo == null) return false;

            PackageInfo packageInfo = mPackageManager.getPackageInfo(activityInfo.packageName, PackageManager.GET_ACTIVITIES);
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            if(applicationInfo == null)return false;

            return ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
        } catch (PackageManager.NameNotFoundException e) {
            //e.printStackTrace();
        }
        return false;
    }

    boolean isUserApp(ApplicationInfo ai) {
        if(ai == null)return false;
        int mask = ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP;
        return (ai.flags & mask) == 0;
    }

    private boolean isSelf(String packageName) {
        return packageName.equals(getPackName());
    }

    private String getPackName() {
        return mContext.getPackageName();
    }

    //////////////////////// Android L ///////////////////////////////////
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public List<UsageStats> getUsageStatsList(){
        UsageStatsManager usm = getUsageStatsManager();
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH, -1);
        long startTime = calendar.getTimeInMillis();
        List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,startTime,endTime);
        return usageStatsList;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public List<AppEntity> getRecentAppInfo(){
        List<UsageStats> usageStatsList = getUsageStatsList();
        List<AppEntity> list = new ArrayList<>();
        for (UsageStats u : usageStatsList){
            String packageName = u.getPackageName();
            ApplicationInfo applicationInfo = getAppInfo(packageName);
            //系统引用不加入最近列表
            if(!isUserApp(applicationInfo))continue;
            //如果系统设置了不显示自身 则自己也不需要加入最近列表
            if (isShowSelf(packageName)) continue;
            AppEntity entity = warpAppEntity(applicationInfo);
            if (entity == null)continue;
            list.add (entity);
        }
        return list;
    }

    @SuppressWarnings("ResourceType")
    private UsageStatsManager getUsageStatsManager(){
        UsageStatsManager usm = (UsageStatsManager) mContext.getSystemService("usagestats");
        return usm;
    }

    // 将ApplicationInfo转化为AppEntity
    private AppEntity warpAppEntity(ApplicationInfo appInfo){
        if (appInfo == null)return  null;
        AppEntity entity = new AppEntity();
        entity.setAppName(appInfo.loadLabel(mPackageManager).toString());
        entity.setPackageName(appInfo.packageName);
        Bitmap iconBitmap = drawableToBitmap(appInfo.loadIcon(mPackageManager));
        entity.setAppIconData(formatBitmapToBytes(iconBitmap));
        entity.setSrcPath(appInfo.sourceDir);
        return entity;
    }

    //根据包名获取对应的ApplicationInfo 信息
    private ApplicationInfo getAppInfo(String packageName){
        ApplicationInfo appInfo = null;
        try {
            appInfo = mPackageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return appInfo;
    }

    /**
     * 将Drawable转化为Bitmap
     * @param drawable
     * @return
     */
    private static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
