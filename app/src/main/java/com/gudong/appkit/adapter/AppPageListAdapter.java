package com.gudong.appkit.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gudong.appkit.ui.fragment.AppListFragment;
import com.gudong.appkit.ui.fragment.EListType;

public class AppPageListAdapter extends FragmentPagerAdapter {
        EListType[]mTitles;
        Context mContext;
        public AppPageListAdapter(FragmentManager fm, Context context, EListType[] titles) {
            super(fm);
            this.mTitles = titles;
            this.mContext = context;

        }

        @Override
        public Fragment getItem(int position) {
            return AppListFragment.getInstance(mTitles[position]);
        }

        @Override
        public int getCount() {
            return mTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position].getTitle();
        }
    }