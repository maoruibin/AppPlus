package com.gudong.appkit.utils;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gudong.appkit.R;

/**
 * tool for dialog
 * Created by mao on 7/19/15.
 */
public class DialogUtil {
    /**
     * show a dialog which it contain one point message only
     * @param context context
     */
    public static void showSinglePointDialog(Context context, String message){
        new MaterialDialog.Builder(context)
                .title(context.getString(R.string.title_point))
                .content(message)
                .positiveText(context.getString(R.string.dialog_know))
                .show();
    }
}
