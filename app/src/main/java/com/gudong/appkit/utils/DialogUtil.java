package com.gudong.appkit.utils;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;

import com.gudong.appkit.R;
import com.gudong.appkit.ui.fragment.CustomWebViewDialog;

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
        new AlertDialog.Builder(context)
                .setTitle(R.string.title_point)
                .setMessage(message)
                .setPositiveButton(R.string.dialog_confirm, null)
                .show();
    }



    public static void showCustomDialogFillInWebView(Context context, FragmentManager fragmentManager, String dialogTitle, String htmlFileName, String tag) {
        int accentColor = Utils.getAccentColor(context);
        CustomWebViewDialog.create(dialogTitle, htmlFileName, accentColor)
                .show(fragmentManager, tag);
    }
}
