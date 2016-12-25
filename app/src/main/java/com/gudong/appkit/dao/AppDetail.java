package com.gudong.appkit.dao;

import com.jaredrummler.apkparser.model.UseFeature;

import java.util.List;

/**
 * Created by GuDong on 2016/12/25 19:10.
 * Contact with gudong.name@gmail.com.
 */

public class AppDetail {
    public AppEntity entity;
    List<UseFeature> usesFeatures;
    List<String> requestedPermissions;
}
