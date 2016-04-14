package com.geekhub.choosehelper.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ImagesViewPagerAdapter extends FragmentPagerAdapter {

    //private final List<String> mFragmentTitleList = new ArrayList<>();
    private final List<Fragment> mFragmentList = new ArrayList<>();

    public ImagesViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList != null ? mFragmentList.size() : 0;
    }

    public void addFragment(Fragment fragment/*, String title*/) {
        mFragmentList.add(fragment);
        //mFragmentTitleList.add(title);
    }

    /*@Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }*/
}
