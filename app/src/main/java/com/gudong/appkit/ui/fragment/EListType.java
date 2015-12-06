package com.gudong.appkit.ui.fragment;

/**
     * the type of list
     */
    public enum EListType{
        /**
         * recent apps list
         */
        TYPE_RECENT("最近打开"),
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