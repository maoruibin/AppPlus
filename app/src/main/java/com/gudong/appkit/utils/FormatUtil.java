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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import com.gudong.appkit.R;
import com.gudong.appkit.dao.AppEntity;

import java.text.SimpleDateFormat;
import java.util.Date;

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
     * @param entity app entity
     * @return formated version name
     */
    public static String formatVersionName(AppEntity entity){
        return "(".concat(entity.getVersionName()).concat(")");
    }

    public static String formatTimeToMinute(long time){
        String format = "yyyy-MM-dd HH:mm";
        return new SimpleDateFormat(format).format(new Date(time));
    }
}
