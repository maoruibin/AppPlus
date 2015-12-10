package com.gudong.appkit.ui.fragment;

/**
 * the type of list
 */
public enum EListType {
    /**
     * recent apps list
     */
    TYPE_RUNNING("运行中"),
    /**
     * all apps list
     */
    TYPE_ALL("已安装"),
    /**
     * search results list
     */
    TYPE_SEARCH("");

    private String title;

    EListType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}