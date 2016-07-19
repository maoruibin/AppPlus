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
    PREPARE_FOR_ALL_INSTALLED_APP_FINISH,
    /**
     * delete exported file successfully
     */
    DELETE_SINGLE_EXPORT_FILE_FAIL,
    /**
     * delete exported file fail
     */
    DELETE_SINGLE_EXPORT_FILE_SUC,

    UPDATE_ENTITY_FAVORITE_STATUS,

    /**
     * OPEN export dir
     */
    OPEN_EXPORT_DIR;
}
