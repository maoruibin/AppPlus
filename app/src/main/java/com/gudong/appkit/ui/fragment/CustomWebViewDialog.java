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

package com.gudong.appkit.ui.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;

import com.gudong.appkit.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author mao
 */
public class CustomWebViewDialog extends DialogFragment {
    private DialogInterface.OnClickListener mNeutralClickCallback;
    private DialogInterface.OnClickListener mPositiveClickCallback;
    /**
     * create a custom dialog use web view load layout by html file
     *
     * @param dialogTitle  dialog title
     * @param htmlFileName html file name
     * @param accentColor  accent color
     * @return a instance of CustomWebViewDialog
     */
    public static CustomWebViewDialog create(String dialogTitle, String htmlFileName, int accentColor) {
        CustomWebViewDialog dialog = new CustomWebViewDialog();
        Bundle args = new Bundle();
        args.putString("dialogTitle", dialogTitle);
        args.putString("htmlFileName", htmlFileName);
        args.putInt("accentColor", accentColor);
        dialog.setArguments(args);
        return dialog;
    }

    /**
     * create a CustomWebViewDialog with a neutral button
     * @param dialogTitle
     * @param htmlFileName
     * @param accentColor
     * @param neutralText neutral button text
     * @param neutralListener click listener
     * @return
     */
    public static CustomWebViewDialog create(String dialogTitle, String htmlFileName, int accentColor, String positiveText, DialogInterface.OnClickListener positiveListener, String neutralText, DialogInterface.OnClickListener neutralListener) {
        CustomWebViewDialog dialog = new CustomWebViewDialog();
        Bundle args = new Bundle();
        args.putString("dialogTitle", dialogTitle);
        args.putString("htmlFileName", htmlFileName);
        args.putInt("accentColor", accentColor);

        args.putString("positiveText", positiveText);
        dialog.setPositiveClickCallback(positiveListener);

        args.putString("neutralText", neutralText);
        dialog.setNeutralClickCallback(neutralListener);

        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View customView;
        try {
            customView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_webview, null);
        } catch (InflateException e) {
            throw new IllegalStateException("This device does not support Web Views.");
        }

        String dialogTitle = getArguments().getString("dialogTitle");
        String neutralText = getArguments().getString("neutralText");
        String positiveText = getArguments().getString("positiveText");
        neutralText = TextUtils.isEmpty(neutralText)?"":neutralText;
        positiveText = TextUtils.isEmpty(neutralText)?getString(android.R.string.ok):positiveText;
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(dialogTitle)
                .setView(customView)
                .setNeutralButton(neutralText, mNeutralClickCallback)
                .setPositiveButton(positiveText, mPositiveClickCallback)
                .show();

        final WebView webView = (WebView) customView.findViewById(R.id.webview);
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        try {
            String htmlFileName = getArguments().getString("htmlFileName");
            StringBuilder buf = new StringBuilder();
            InputStream json = getActivity().getAssets().open(htmlFileName);
            BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;
            while ((str = in.readLine()) != null)
                buf.append(str);
            in.close();

            final int accentColor = getArguments().getInt("accentColor");
            String formatLodString = buf.toString()
                    .replace("{style-placeholder}", "body { background-color: #ffffff; color: #000; }")
                    .replace("{link-color}", colorToHex(shiftColor(accentColor, true)))
                    .replace("{link-color-active}", colorToHex(accentColor));
            webView.loadDataWithBaseURL(null, formatLodString, "text/html", "UTF-8", null);
        } catch (Throwable e) {
            webView.loadData("<h1>Unable to load</h1><p>" + e.getLocalizedMessage() + "</p>", "text/html", "UTF-8");
        }
        return dialog;
    }

    private String colorToHex(int color) {
        return Integer.toHexString(color).substring(2);
    }

    private int shiftColor(int color, boolean up) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= (up ? 1.1f : 0.9f); // value component
        return Color.HSVToColor(hsv);
    }

    public void setNeutralClickCallback(DialogInterface.OnClickListener neutralClickCallback) {
        mNeutralClickCallback = neutralClickCallback;
    }

    public void setPositiveClickCallback(DialogInterface.OnClickListener positiveClickCallback) {
        mPositiveClickCallback = positiveClickCallback;
    }
}
