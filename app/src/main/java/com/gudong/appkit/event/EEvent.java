package com.gudong.appkit.event;

/**
 * collect all event
 * Created by GuDong on 12/8/15 10:58.
 * Contact with 1252768410@qq.com.
 */
public enum EEvent {
    /**
     * in setting activity,when user clicked Show AppPlus setting item,this Event will be trigger
     */
    RECENT_LIST_IS_SHOW_SELF_CHANGE,
    /**
     * in setting activity,when user clicked Brief mode setting item,this Event will be trigger
     */
    LIST_ITEM_BRIEF_MODE_CHANGE,
    /**
     * uninstall a application from system,the app list need update data right now
     */
    UNINSTALL_APPLICATION_FROM_SYSTEM,
    /**
     * install a new application from system,the all app list need update date right now
     */
    INSTALL_APPLICATION_FROM_SYSTEM,
    /**
     * when enter SplashActivity app will load installed app and store list to local db
     * when load finish, need notify all installed list reload data,otherwise the list will empty
     */
    PREPARE_FOR_ALL_INSTALLED_APP_FINISH;
}
