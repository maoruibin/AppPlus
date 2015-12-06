package com.gudong.appkit.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import com.gudong.appkit.R;

/**
 * Created by GuDong on 12/6/15 15:36.
 * Contact with 1252768410@qq.com.
 */
public class FormatUtil {
    /**
     * warp choose title and make app title accent
     *
     * @param appName app name
     * @return warped chooser title
     */
    public static SpannableStringBuilder warpChooserTitle(Activity context, String appName) {
        @SuppressLint("StringFormatMatches") String title = String.format(context.getString(R.string.select_transfer_way_apk, appName));
        ForegroundColorSpan fontSpanRed = new ForegroundColorSpan(Utils.getColorWarp(context, R.color.colorAccent));
        int start = 2;
        int end = start + appName.length() + 3;
        SpannableStringBuilder mSpannableBuilder = new SpannableStringBuilder(title);
        mSpannableBuilder.setSpan(fontSpanRed, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return mSpannableBuilder;
    }
}
