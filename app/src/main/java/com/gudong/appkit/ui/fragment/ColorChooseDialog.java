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
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.gudong.appkit.R;
import com.gudong.appkit.utils.Utils;

/**
 * Created by mao on 8/8/15.
 */
public class ColorChooseDialog extends DialogFragment implements View.OnClickListener {
    int[] mColors;
    IClickColorSelectCallback mCallback;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int preselect = getArguments().getInt("preselect");
        View view = View.inflate(getActivity(), R.layout.dialog_color_chooser, null);
        mColors = initColors();

        GridLayout gridLayout = (GridLayout) view.findViewById(R.id.grid);

        for (int i = 0; i < mColors.length; i++) {
            gridLayout.addView(getColorItemView(getActivity(), i, i == preselect));
        }
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.color_chooser)
                .setView(view)
                .show();
        return dialog;
    }

    private int[] initColors() {
        final TypedArray ta = getActivity().getResources().obtainTypedArray(R.array.material_designer_colors);
        int[] mColors = new int[ta.length()];
        for (int i = 0; i < ta.length(); i++)
            mColors[i] = ta.getColor(i, 0);
        ta.recycle();
        return mColors;
    }

    public void show(AppCompatActivity context, int preselect) {
        Bundle args = new Bundle();
        args.putInt("preselect", preselect);
        setArguments(args);
        show(context.getSupportFragmentManager(), "COLOR_SELECTOR");
    }


    private void setBackgroundCompat(View view, Drawable d) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(d);
        } else {
            //noinspection deprecation
            view.setBackgroundDrawable(d);
        }
    }

    private View getColorItemView(final Context context, int position, boolean isSelect) {
        int color = mColors[position];
        int widthImageCheckView = Utils.convertDensityPix(context, 24);
        int widthColorView = Utils.convertDensityPix(context, 56);
        int widthMargin = Utils.convertDensityPix(context, 4);

        ImageView imageView = new ImageView(context);
        imageView.setImageResource(R.drawable.ic_done_white_24dp);

        FrameLayout.LayoutParams ivParams = new FrameLayout.LayoutParams(widthImageCheckView, widthImageCheckView);
        ivParams.gravity = Gravity.CENTER;
        imageView.setLayoutParams(ivParams);
        imageView.setVisibility(isSelect ? View.VISIBLE : View.INVISIBLE);

        FrameLayout frameLayout = new FrameLayout(context);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams(new FrameLayout.LayoutParams(widthColorView, widthColorView));
        params.setGravity(Gravity.CENTER);
        params.setMargins(widthMargin, widthMargin, widthMargin, widthMargin);
        frameLayout.setLayoutParams(params);

        setBackgroundSelector(frameLayout, color);

        frameLayout.addView(imageView);
        frameLayout.setOnClickListener(this);
        frameLayout.setTag(position);
        return frameLayout;
    }

    /**
     * 给View设置背景色
     *
     * @param view
     * @param color
     */
    private void setBackgroundSelector(View view, int color) {
        Drawable selector = createSelector(color);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int[][] states = new int[][]{
                    new int[]{-android.R.attr.state_pressed},
                    new int[]{android.R.attr.state_pressed}
            };
            int[] colors = new int[]{
                    shiftColor(color),
                    color
            };
            ColorStateList rippleColors = new ColorStateList(states, colors);
            setBackgroundCompat(view, new RippleDrawable(rippleColors, selector, null));
        } else {
            setBackgroundCompat(view, selector);
        }
    }


    private int shiftColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.9f; // value component
        return Color.HSVToColor(hsv);
    }

    private Drawable createSelector(int color) {
        ShapeDrawable coloredCircle = new ShapeDrawable(new OvalShape());
        coloredCircle.getPaint().setColor(color);
        ShapeDrawable darkerCircle = new ShapeDrawable(new OvalShape());
        darkerCircle.getPaint().setColor(shiftColor(color));

        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{-android.R.attr.state_pressed}, coloredCircle);
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, darkerCircle);
        return stateListDrawable;
    }


    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        if (mCallback != null) {
            mCallback.onClickSelectCallback(position, mColors[position]);
            dismiss();
        }

    }

    public void setColorSelectCallback(IClickColorSelectCallback callback) {
        mCallback = callback;
    }

    public interface IClickColorSelectCallback {
        void onClickSelectCallback(int position, int color);
    }
}
