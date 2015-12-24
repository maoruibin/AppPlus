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

package com.gudong.appkit.dao;

import android.content.Context;
import android.text.TextUtils;

import com.gudong.appkit.App;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.List;

/**
 * Created by GuDong on 12/8/15 12:11.
 * Contact with 1252768410@qq.com.
 */
public class DBHelper {
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
     * get AppEntity for Application of AppPlus
     * @param context
     * @return AppEntity
     */
    public static AppEntity getAppPlusEntity(Context context){
        return getAppByPackageName(context.getPackageName());
    }
}
