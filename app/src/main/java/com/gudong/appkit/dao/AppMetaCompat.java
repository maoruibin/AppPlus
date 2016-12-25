package com.gudong.appkit.dao;

/**
 * Created by GuDong on 2016/12/25 21:18.
 * Contact with gudong.name@gmail.com.
 */

public class AppMetaCompat extends AppEntity {
    public AppEntity appEntity;
    public String maxSdkVersion;
    public String minSdkVersion;
    public String targetSdkVersion;
    public String installLocation;
    public AppMetaCompat(AppEntity entity ){
        this.appEntity = entity;
    }
}
