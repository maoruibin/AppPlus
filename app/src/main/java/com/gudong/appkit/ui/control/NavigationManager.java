package com.gudong.appkit.ui.control;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.gudong.appkit.R;
import com.gudong.appkit.utils.Utils;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by mao on 8/4/15.
 */
public class NavigationManager {
    /**
     * 给作者发送邮件 反馈意见
     * @param context
     */
    public static void gotoSendOpinion(Activity context){
        MobclickAgent.onEvent(context, "send_email");
        Intent localIntent = new Intent("android.intent.action.SENDTO", Uri.parse("mailto:1252768410@qq.com"));
        localIntent.putExtra("android.intent.extra.SUBJECT", context.getString(R.string.title_email_opinion));
        localIntent.putExtra("android.intent.extra.TEXT", Utils.getLog(context));
        context.startActivity(localIntent);
    }

    /**
     * 去评分
     * @param activity
     */
    public static void gotoScore(Activity activity){
        Uri uri = Uri.parse("market://details?id="+activity.getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    /**
     * 给朋友分享
     */
    public static void gotoShare(Activity activity){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("text/*");
        sendIntent.putExtra(Intent.EXTRA_TEXT, "App+，一款不错的App管理应用");
        activity.startActivity(sendIntent);
    }
}
