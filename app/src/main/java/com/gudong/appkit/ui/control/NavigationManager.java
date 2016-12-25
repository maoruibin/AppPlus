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

package com.gudong.appkit.ui.control;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import com.gudong.appkit.R;
import com.gudong.appkit.ui.activity.AppInfoActivity;
import com.gudong.appkit.ui.activity.MainActivity;

import java.io.File;

/**
 * Created by mao on 8/4/15.
 */
public class NavigationManager {
    public static final int UNINSTALL_REQUEST_CODE = 1;

    private static final String SCHEME = "package";
    private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
    private static final String APP_PKG_NAME_22 = "pkg";
    private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
    private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";


    /**
     * 给作者发送邮件 反馈意见
     *
     * @param context
     */
    public static void gotoSendOpinion(final Activity context) {

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"gudong.name@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.title_email_opinion));

        try {
            context.startActivity(emailIntent);
        } catch (ActivityNotFoundException e) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.title_point)
                    .setMessage("意见反馈需要你安装邮件客户端，检测到你的手机尚未使用任何邮件客户端，你可以立即去配置手机自带的邮件应用，也可以通过访问我个人主页的方式，跟我取得联系，再次感谢你对 AppPlus 支持！\n\n我的主页地址：gudong.name")
                    .setPositiveButton(R.string.dialog_know,null)
                    .setNegativeButton(R.string.dialog_cancel,null)
                    .setNeutralButton(context.getString(R.string.action_visit_host), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            NavigationManager.openUrl(context,"http://gudong.name/");
                        }
                    })
                    .show();
        }
    }

    public static void openUrl(Activity activity,String url){
        Uri uri=Uri.parse(url);   //指定网址
        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_VIEW);           //指定Action
        intent.setData(uri);                            //设置Uri
        activity.startActivity(intent);
    }

    /**
     * 去评分
     *
     * @param activity
     */
    public static void gotoMarket(Activity activity, String packageName) {
        Uri uri = Uri.parse("market://details?id=" + packageName);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    /**
     * 给朋友分享
     */
    public static void gotoShare(Activity activity) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("text/*");
        sendIntent.putExtra(Intent.EXTRA_TEXT, "App+，一款不错的App管理应用");
        activity.startActivity(sendIntent);
    }

    public static void openApp(Context context, String packageName) throws Exception {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        context.startActivity(intent);
    }

    public static void uninstallApp(Activity activity, String packageName) {
        Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
        intent.setData(Uri.parse("package:" + packageName));
        intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
        activity.startActivityForResult(intent, UNINSTALL_REQUEST_CODE);
    }

    /**
     * 浏览文件夹
     *
     * @param file
     */
    public static void browseFile(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "file/*");
        context.startActivity(intent);
    }

    /**
     * open app detail info
     *
     * @param packageName
     */
    public static void openAppDetail(Context context, String packageName) {
        Intent intent = new Intent();
        final int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= 9) {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Uri uri = Uri.fromParts(SCHEME, packageName, null);
            intent.setData(uri);
        } else {
            final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22
                    : APP_PKG_NAME_21);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName(APP_DETAILS_PACKAGE_NAME,
                    APP_DETAILS_CLASS_NAME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(appPkgName, packageName);
        }
        context.startActivity(intent);
    }

    public static void openAppInfo(Context context, String packageName) {
        Intent intent = new Intent(context,AppInfoActivity.class);
        intent.putExtra("package",packageName);
        context.startActivity(intent);

    }

    public static void gotoMainActivityFromSplashView(Activity context) {
        context.startActivity(new Intent(context, MainActivity.class));
        context.finish();
    }
}
