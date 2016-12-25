package com.gudong.appkit.ui.controller;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gudong.appkit.R;

import java.util.List;

import name.gudong.viewcontroller.ViewController;


public class AppPermissionViewController extends ViewController<List<String>> {

    private LinearLayout llRoot;
    private LinearLayout.LayoutParams params;

    public AppPermissionViewController(Context context) {
        super(context);
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = (int) getContext().getResources().getDimension(R.dimen.dp_6);
    }

    @Override
    protected int resLayoutId() {
        return R.layout.vc_layout_app_permission;
    }

    @Override
    protected void onCreatedView(View view) {
        llRoot = (LinearLayout) view.findViewById(R.id.ll_root);
    }

    @Override
    protected void onBindView(List<String> strings) {
        for(String permission:strings){
            TextView text = new TextView(getContext());
            text.setText(permission);
            llRoot.addView(text,params);
        }
    }

}
