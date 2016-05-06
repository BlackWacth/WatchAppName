package com.hua.watchappname.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<FragmentModel> mList;

    public ViewPagerAdapter(FragmentManager fm, List<FragmentModel> list) {
        super(fm);
        this.mList = list;
    }

    @Override
    public Fragment getItem(int position) {
        return mList.get(position).fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mList.get(position).title;
    }

    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;
    }

    public static class FragmentModel {
        Fragment fragment;
        String title;

        public FragmentModel(Fragment fragment, String title) {
            this.fragment = fragment;
            this.title = title;
        }
    }
}
