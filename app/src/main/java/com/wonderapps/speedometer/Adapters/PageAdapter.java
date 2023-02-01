package com.wonderapps.speedometer.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.wonderapps.speedometer.Fragments.DigitalFragment;
import com.wonderapps.speedometer.Fragments.GraphFragment;
import com.wonderapps.speedometer.Fragments.MapFragment;


public class PageAdapter extends FragmentPagerAdapter {

    private int numOfTabs;

    public PageAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new DigitalFragment();
            case 1:
                return new MapFragment();
            case 2:
                return new GraphFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
