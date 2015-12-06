package com.gudong.appkit.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by GuDong on 12/5/15 16:09.
 * Contact with 1252768410@qq.com.
 */
public class DBHelper extends SQLiteOpenHelper{
    private static final String KEY_DB_NAME = "appplus.db";
    private static final int KEY_DB_VERSION = 1;
    public DBHelper(Context context) {
        super(context, KEY_DB_NAME, null, KEY_DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table tb_apps("
        + "id integer primary key autoincrement,"
        + "appName text ,"
        + "packageName text ,"
        + "versionName text ,"
        + "versionCode integer ,"
        + "appIcon blob ,"
        + "srcPath text"
        + ");";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
