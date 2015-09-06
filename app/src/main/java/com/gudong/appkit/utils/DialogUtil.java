package com.gudong.appkit.utils;

import android.content.Context;
import android.support.v4.app.FragmentManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.ThemeSingleton;
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
        new MaterialDialog.Builder(context)
                .title(context.getString(R.string.title_point))
                .content(message)
                .positiveText(context.getString(R.string.dialog_confirm))
                .show();
    }

    public static void showCusotomDialogFillInWebView(Context context, FragmentManager fragmentManager, String dialogTitle, String htmlFileName, String tag) {
        int accentColor = ThemeSingleton.get().widgetColor;
        if (accentColor == 0)
            accentColor = context.getResources().getColor(R.color.colorAccent);

        CustomWebViewDialog.create(dialogTitle, htmlFileName, accentColor)
                .show(fragmentManager, tag);
    }
}
