package com.gudong.appkit.utils;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gudong.appkit.R;

/**
 * Created by mao on 7/19/15.
 */
public class DialogUtil {
    /**
     * 只含有一个按钮的提示框
     * @param context
     */
    public static void showSingleChoice(Context context,String title, String message, String positive){
        new MaterialDialog.Builder(context)
                .title(title)
                .content(message)
                .positiveText(positive)
                .show();
    }
}
