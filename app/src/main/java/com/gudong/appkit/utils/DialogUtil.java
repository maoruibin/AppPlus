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

import android.content.Context;
import android.content.DialogInterface;
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

    public static void showCustomDialogWithTwoAction(
            Context context, FragmentManager fragmentManager,
            String dialogTitle, String htmlFileName, String tag,
            String positiveText, DialogInterface.OnClickListener positiveListener,
            String neutralText, DialogInterface.OnClickListener neutralListener) {
        int accentColor = Utils.getAccentColor(context);
        CustomWebViewDialog.create(dialogTitle, htmlFileName, accentColor,positiveText,positiveListener,neutralText,neutralListener)
                .show(fragmentManager, tag);
    }
}
