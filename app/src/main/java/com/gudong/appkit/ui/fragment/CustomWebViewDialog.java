package com.gudong.appkit.ui.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;

import com.gudong.appkit.R;
import com.gudong.appkit.utils.Utils;
import com.gudong.appkit.utils.logger.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author mao
 */
public class CustomWebViewDialog extends DialogFragment {

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
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(dialogTitle)
                .setView(customView)
                .setPositiveButton(android.R.string.ok, null)
                .show();

        final WebView webView = (WebView) customView.findViewById(R.id.webview);
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        try {
            String htmlFileName = getArguments().getString("htmlFileName");
            Logger.i("htmlFileName name " + htmlFileName);
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
}
