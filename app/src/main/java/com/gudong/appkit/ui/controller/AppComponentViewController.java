package com.gudong.appkit.ui.controller;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gudong.appkit.R;
import com.jaredrummler.apkparser.model.AndroidComponent;

import java.util.List;

import name.gudong.viewcontroller.ViewController;


public class AppComponentViewController extends ViewController<List<AndroidComponent>> {
    public static final int KEY_ACTIVITY = 1;
    public static final int KEY_SERVICE = 2;
    public static final int KEY_PROVIDER = 3;
    public static final int KEY_RECEIVER = 4;

    private final LinearLayout.LayoutParams params;
    private android.widget.LinearLayout llRoot;
    private TextView tvType;
    private int mType;
    public AppComponentViewController(Context context,int type) {
        super(context);
        this.mType = type;
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = (int) getContext().getResources().getDimension(R.dimen.dp_6);
    }

    @Override
    protected int resLayoutId() {
        return R.layout.vc_layout_app_activity;
    }

    @Override
    protected void onCreatedView(View view) {
        llRoot = (LinearLayout) view.findViewById(R.id.ll_root_activity);
        tvType = (TextView) view.findViewById(R.id.tv_type);
        switch (mType){
            case KEY_ACTIVITY:
                tvType.setText("Activitys");
                break;
            case KEY_SERVICE:
                tvType.setText("Services");
                break;
            case KEY_RECEIVER:
                tvType.setText("Receivers");
                break;
            case KEY_PROVIDER:
                tvType.setText("Providers");
                break;
        }
    }

    @Override
    protected void onBindView(List<AndroidComponent> data) {
        if(data.isEmpty()){
            getView().setVisibility(View.GONE);
        }else{
            for(AndroidComponent component:data){
                TextView text = new TextView(getContext());
                text.setText(component.name);
                llRoot.addView(text,params);
            }
        }

    }

    private void initView(View view) {

    }
}
