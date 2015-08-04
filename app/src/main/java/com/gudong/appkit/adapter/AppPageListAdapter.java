package com.gudong.appkit.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gudong.appkit.ui.fragment.AppListFragment;

public class AppPageListAdapter extends FragmentPagerAdapter {
        int[]mTitles;
        Context mContext;
        public AppPageListAdapter(FragmentManager fm, Context context, int[] titles) {
            super(fm);
            this.mTitles = titles;
            this.mContext = context;

        }

        @Override
        public Fragment getItem(int position) {
            return AppListFragment.getInstance(position);
        }

        @Override
        public int getCount() {
            return mTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mContext.getString(mTitles[position]).toString();
        }
    }