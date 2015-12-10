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
