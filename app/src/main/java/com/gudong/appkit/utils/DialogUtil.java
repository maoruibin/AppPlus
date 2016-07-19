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

package com.gudong.appkit.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gudong.appkit.R;
import com.gudong.appkit.ui.fragment.CustomWebViewDialog;
import com.gudong.appkit.view.CircularProgressDrawable;

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
        showSinglePointDialog(context,message,context.getString(R.string.dialog_confirm),null);
    }

    public static void showSinglePointDialog(Context context, String message, String positiveButtonText, DialogInterface.OnClickListener positiveButtonCallback){
        new AlertDialog.Builder(context)
                .setTitle(R.string.title_point)
                .setMessage(message)
                .setPositiveButton(positiveButtonText, positiveButtonCallback)
                .show();
    }


    public static void showCustomDialogFillInWebView(Context context, FragmentManager fragmentManager, String dialogTitle, String htmlFileName, String tag) {
        int accentColor = Utils.getAccentColor(context);
        CustomWebViewDialog.create(dialogTitle, htmlFileName, accentColor)
                .show(fragmentManager, tag);
    }

    public static void showCustomDialogWithTwoAction(
            Context context, FragmentManager fragmentManager,
            String dialogTitle, String htmlFileName, String tag,
            String positiveText, DialogInterface.OnClickListener positiveListener,
            String neutralText, DialogInterface.OnClickListener neutralListener) {
        int accentColor = Utils.getAccentColor(context);
        CustomWebViewDialog.create(dialogTitle, htmlFileName, accentColor,positiveText,positiveListener,neutralText,neutralListener)
                .show(fragmentManager, tag);
    }

    public static AlertDialog getProgressDialog(Activity context,String title, String message){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_progress, null);
        ProgressBar progressBar = (ProgressBar) view.findViewById(android.R.id.progress);
        TextView textView = (TextView) view.findViewById(R.id.content);

        //改变Progress的背景为MaterialDesigner规范的样式
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            progressBar.setIndeterminateDrawable(new CircularProgressDrawable(Utils.getColorWarp(context, R.color.colorAccent), context.getResources().getDimension(R.dimen.loading_border_width)));
        }

        final AlertDialog progressDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(view).create();
        //设置显示文字
        textView.setText(message);

        return progressDialog;
    }
}
