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

import com.gudong.appkit.App;
import com.gudong.appkit.utils.RxUtil;
import com.gudong.appkit.utils.Utils;
import com.gudong.appkit.utils.logger.Logger;
import com.jaredrummler.android.processes.models.AndroidProcess;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;

/**
 * 获取app信息的引擎
 * Created by mao on 15/7/8.
 */
public class AppInfoEngine {
    private Context mContext;
    private PackageManager mPackageManager;

    private static class SingletonHolder{
        private static final AppInfoEngine INSTANCE = new AppInfoEngine(App.sContext);
    }

    public static AppInfoEngine getInstance(){
        return SingletonHolder.INSTANCE;
    }

    private AppInfoEngine(Context context) {
        this.mContext = context;
        mPackageManager = mContext.getApplicationContext().getPackageManager();
    }

    /**
     * get all app info list which is installed by user
     * @return all installed app info list
     */
    public Observable<List<AppEntity>> getInstalledAppList() {
        return RxUtil.makeObservable(new Callable<List<AppEntity>>() {
            @Override
            public List<AppEntity> call() throws Exception {
                List<AppEntity> list = new ArrayList<>();
                List<PackageInfo>packageInfos = mPackageManager.getInstalledPackages(PackageManager.GET_META_DATA);
                for (PackageInfo info:packageInfos){
                    if(!isUserApp(info))continue;
                    AppEntity entity = warpAppEntity(info);
                    if (entity == null)continue;;
                    list.add(entity);
                }
                return list;
            }
        });
    }

    /**
     * get installed AppEntity by packageName
     * @param packageName Application's package name
     * @return installed app if not found package name will return null
     */
    public AppEntity getAppByPackageName(String packageName){
        List<PackageInfo>packageInfos = mPackageManager.getInstalledPackages(PackageManager.GET_META_DATA);
        for (PackageInfo packageInfo : packageInfos) {
            if(!isUserApp(packageInfo))continue;
            if(packageName.equals(packageInfo.packageName)){
                return warpAppEntity(packageInfo);
            }
        }
        return null;
    }

    /**
     * get recent running app list
     * @return recent running app list
     */
    @Deprecated
    public List<AppEntity> getRecentAppList() {
        List<AppEntity> list = new ArrayList<>();
        ActivityManager mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RecentTaskInfo> recentTasks = mActivityManager.getRecentTasks(10, 0);
        for (ActivityManager.RecentTaskInfo taskInfo : recentTasks) {
            Intent intent = taskInfo.baseIntent;
            ResolveInfo resolveInfo = mPackageManager.resolveActivity(intent, 0);
            if (resolveInfo == null)continue;
            String packageName = resolveInfo.activityInfo.packageName;
            if (isSystemApp(packageName)) continue;
            if (isShowSelf(packageName)) continue;
            AppEntity appEntity = DataHelper.getAppByPackageName(packageName);
            if (appEntity == null)continue;
            list.add (appEntity);
        }
        return list;
    }

    /**
     * check running list should show AppPlus or not
     * @param packagename
     * @return true if show else false
     */
    private boolean isShowSelf(String packagename){
        return !Utils.isShowSelf() && packagename.equals(mContext.getPackageName());
    }

    /**
     * check package is system app or not
     * @param packageName
     * @return if package is system app return true else return false
     */
    private boolean isSystemApp(String packageName){
        try {
            PackageInfo packageInfo = mPackageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            if(applicationInfo == null)return false;
            return ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    boolean isUserApp(PackageInfo packageInfo) {
        if(packageInfo == null)return false;
        int mask = ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP;
        return (packageInfo.applicationInfo.flags & mask) == 0;
    }

    private boolean isSelf(String packageName) {
        return packageName.equals(mContext.getPackageName());
    }

    //////////////////////// Android L ///////////////////////////////////
    @Deprecated
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

    @Deprecated
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public List<AppEntity> getRecentAppInfo(){
        List<UsageStats> usageStatsList = getUsageStatsList();
        List<AppEntity> list = new ArrayList<>();
        for (UsageStats u : usageStatsList){
            String packageName = u.getPackageName();
            ApplicationInfo applicationInfo = getAppInfo(packageName);
            //system app will not appear recent list
            //if(isSystemApp(packageName))continue;
            if (isShowSelf(packageName)) continue;
            AppEntity entity = DataHelper.getAppByPackageName(packageName);
            if (entity == null)continue;
            list.add (entity);
        }
        return list;
    }

    @SuppressWarnings("ResourceType")
    @Deprecated
    private UsageStatsManager getUsageStatsManager(){
        UsageStatsManager usm = (UsageStatsManager) mContext.getSystemService("usagestats");
        return usm;
    }

    /**
     * make PackageInfo warp to AppEntity
     * @param packageInfo PackageInfo
     * @return AppEntity
     */
    private AppEntity warpAppEntity(PackageInfo packageInfo){
        if (packageInfo == null)return  null;
        AppEntity entity = new AppEntity();
        entity.setAppName(mPackageManager.getApplicationLabel(packageInfo.applicationInfo).toString());
        entity.setPackageName(packageInfo.packageName);
        Bitmap iconBitmap = drawableToBitmap(mPackageManager.getApplicationIcon(packageInfo.applicationInfo));
        entity.setAppIconData(formatBitmapToBytes(iconBitmap));
        entity.setSrcPath(packageInfo.applicationInfo.sourceDir);
        entity.setVersionName(packageInfo.versionName);
        entity.setVersionCode(packageInfo.versionCode);
        entity.setUid(packageInfo.applicationInfo.uid);
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

    public List<AppEntity> getRecentAppListV1() {
        List<AppEntity> list = new ArrayList<>();
        ActivityManager mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RecentTaskInfo> recentTasks = mActivityManager.getRecentTasks(10, 0);
        for (ActivityManager.RecentTaskInfo taskInfo : recentTasks) {
            Intent intent = taskInfo.baseIntent;
            ResolveInfo resolveInfo = mPackageManager.resolveActivity(intent, 0);
            if (resolveInfo == null)continue;

            if (isSystemApp(resolveInfo.resolvePackageName)) continue;

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

    public void getRunningProcesses(){
        List<AndroidProcess>list = new ArrayList<>();
        File[]files = new File("/proc").listFiles();
        for (File file:files){
            if(file.isDirectory()){
                int pid;
                try{
                    pid = Integer.parseInt(file.getName());
                    AndroidProcess process = new AndroidProcess(pid);
                    Logger.i("pid is "+file.getName() +" name is "+process.name);
                    list.add(process);
                }catch (NumberFormatException e){
                    continue;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
