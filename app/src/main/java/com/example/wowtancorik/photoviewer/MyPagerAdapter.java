package com.example.wowtancorik.photoviewer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Адаптер для ViewPager. Работает с фрагментом отображающим большую фотографию
 *
 * * Create by Aleksandr Karpachev
 */
public class MyPagerAdapter extends FragmentStatePagerAdapter{

    private int mCount;

    public MyPagerAdapter(FragmentManager fm, int count) {
        super(fm);
        mCount = count;
    }

    @Override
    public Fragment getItem(int position) {
        return PageFragment.newInstance(position);
    }


    @Override
    public int getCount() {
        return mCount;
    }
}
