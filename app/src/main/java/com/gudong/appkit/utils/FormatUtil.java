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

    /**
     * some application's version name is too long,like WeChat etc,
     * now if version name include more than two point,we only keep two point like this
     * WeChat 6.3.7.51_dafdaa12 after format(keep two point) 6.3.7
     * @param versionName app's version name
     * @return formated version name
     */
    public static String formatVersionName(String versionName){
        return "(".concat(versionName).concat(")");
//        String[]array = versionName.split(".");
//        if(array.length<=3){
//            return versionName;
//        }
//        return array[0].concat(".").concat(array[1]).concat(".").concat(array[2]);
    }
}
