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

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

import com.gudong.appkit.App;
import com.gudong.appkit.utils.RxUtil;
import com.gudong.appkit.utils.Utils;
import com.jaredrummler.android.processes.ProcessManager;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;

/**
 * Created by GuDong on 12/8/15 12:11.
 * Contact with 1252768410@qq.com.
 */
public class DataHelper {
    /**
     * check installed package has existed in local or not
     * @param packageName installed app's packageName
     * @return return true if the installed package has existed in local db else return false
     */
    public static boolean installedAppIsExistInLocalDB(String packageName) {
        AppEntity entity = getAppByPackageName(packageName);
        return entity != null;
    }

    public static AppStatus checkEntityStatus(AppEntity installedEntity){
        AppEntity localResult = getAppByPackageName(installedEntity.getPackageName());
        //this app is a new app,now it not exist in my local db
        if(localResult ==null){
            return AppStatus.CREATE;
        }
        // the installed app info is change,so the
        if(!installedEntity.equals(localResult)){
            return AppStatus.CHANGE;
        }
        return AppStatus.NORMAL;
    }

   public static Observable<AppEntity> checkEntityStatusAsyn(final AppEntity installedEntity){
        final AppEntity localResult = getAppByPackageName(installedEntity.getPackageName());
        return RxUtil.makeObservable(new Callable<AppEntity>() {
            @Override
            public AppEntity call() throws Exception {
                //this app is a new app,now it not exist in my local db
                if(localResult ==null){
                    installedEntity.setStatus(AppStatus.CREATE.ordinal());
                // the installed app info is change,so the
                } else if(!installedEntity.equals(localResult)){
                    installedEntity.setStatus(AppStatus.CHANGE.ordinal());
                }else{
                    installedEntity.setStatus(AppStatus.NORMAL.ordinal());
                }
                return installedEntity;
            }
        });
    }

   public static AppEntity checkAndSetAppEntityStatus(final AppEntity installedEntity){
        final AppEntity localResult = getAppByPackageName(installedEntity.getPackageName());
       //this app is a new app,now it not exist in my local db
       if(localResult ==null){
           installedEntity.setStatus(AppStatus.CREATE.ordinal());
           // the installed app info is change,so the
       } else if(!installedEntity.equals(localResult)){
           installedEntity.setStatus(AppStatus.CHANGE.ordinal());
       }else{
           installedEntity.setStatus(AppStatus.NORMAL.ordinal());
       }
       return installedEntity;
    }




    /**
     * query App info by local db
     * @param packageName Application's package name
     * @return return AppEntity if this package name is not exist db will return null
     */
    public static AppEntity getAppByPackageName(String packageName){
        if(TextUtils.isEmpty(packageName))return null;
        QueryBuilder queryBuilder = new QueryBuilder(AppEntity.class);
        queryBuilder = queryBuilder.whereEquals("packageName ", packageName);
        List<AppEntity>result = App.sDb.query(queryBuilder);
        try {
            return result.get(0);
        }catch (IndexOutOfBoundsException e){
            return null;
        }
    }
    /**
     * query App info by local db
     * @param packageName Application's package name
     * @return return AppEntity if this package name is not exist db will return null
     */
    public static Observable<AppEntity> getAppByPackageNameAsyn(final String packageName){
        return RxUtil.makeObservable(new Callable<AppEntity>() {
            @Override
            public AppEntity call() throws Exception {
                if(TextUtils.isEmpty(packageName))return null;
                QueryBuilder queryBuilder = new QueryBuilder(AppEntity.class);
                queryBuilder = queryBuilder.whereEquals("packageName ", packageName);
                List<AppEntity>result = App.sDb.query(queryBuilder);
                try {
                    return result.get(0);
                }catch (IndexOutOfBoundsException e){
                    return null;
                }
            }
        });

    }

    public static Observable<List<AppEntity>>getAllEntityByDbAsyn(){
        return RxUtil.makeObservable(new Callable<List<AppEntity>>() {
            @Override
            public List<AppEntity> call() throws Exception {
                return App.sDb.query(AppEntity.class);
            }
        });
    }

    /**
     * get AppEntity for Application of AppPlus
     * @param context
     * @return AppEntity
     */
    public static AppEntity getAppPlusEntity(Context context){
        return getAppByPackageName(context.getPackageName());
    }

    /**
     * get the running app list info
     * @param ctx
     * @return
     */
    public static Observable<List<AppEntity>> getRunningAppEntity(final Context ctx) {
        return RxUtil.makeObservable(new Callable<List<AppEntity>>() {
            @Override
            public List<AppEntity> call() throws Exception {
                List<ActivityManager.RunningAppProcessInfo> runningList = ProcessManager.getRunningAppProcessInfo(ctx);
                List<AppEntity> list = new ArrayList<>();
                for (ActivityManager.RunningAppProcessInfo processInfo : runningList) {
                    String packageName = processInfo.processName;
                    if (isNotShowSelf(ctx, packageName)) continue;
                    AppEntity entity = DataHelper.getAppByPackageName(packageName);
                    if (entity == null) continue;
                    list.add(entity);
                }
                return list;
            }
        });
    }


    /**
     * check running list should show AppPlus or not
     * @param packagename
     * @return true if show else false
     */
    private static boolean isNotShowSelf(Context ctx, String packagename) {
        return !Utils.isShowSelf(ctx) && packagename.equals(ctx.getPackageName());
    }
}
