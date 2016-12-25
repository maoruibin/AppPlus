package com.gudong.appkit.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by GuDong on 2016/12/25 18:53.
 * Contact with gudong.name@gmail.com.
 */

public class AppInfoAdapter extends RecyclerView.Adapter<AppInfoListAdapter.ViewHolder> {
    final static int TYPE_BASIC = 0;
    final static int TYPE_PERMISSION = 1;
    final static int TYPE_ACTIVITY = 2;

    @Override
    public AppInfoListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(AppInfoListAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemViewType(int position) {
        switch (position){
            case 0:
                return TYPE_BASIC;
            case 1:
                return TYPE_PERMISSION;
            case 2:
                return TYPE_ACTIVITY;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
