package com.playerhub.ui.dashboard;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.List;

public class FragmentManger {

    private FragmentManager manger;
    private int layoutId;
    private Fragment active;

    private List<Fragment> fragmentList;

    public FragmentManger(FragmentManager manger, int layoutId) {
        this.manger = manger;
        this.layoutId = layoutId;
    }

    public void addActiveFragment(Fragment fragment) {
        manger.beginTransaction().add(layoutId, fragment).commit();
        active = fragment;
    }

    public void addHideFragment(Fragment fragment) {
        manger.beginTransaction().add(layoutId, fragment).hide(fragment).commit();
    }

    public void addFragment(List<Fragment> fragmentList) {

        this.fragmentList = fragmentList;

        if (fragmentList != null && !fragmentList.isEmpty()) {
            for (int i = 1; i < fragmentList.size(); i++) {

                Fragment fragment = fragmentList.get(i);
                manger.beginTransaction().add(layoutId, fragment).hide(fragment).commit();


            }
            Fragment fragment = fragmentList.get(0);
            manger.beginTransaction().add(layoutId, fragment).commit();

            active = fragment;

        }

    }

    public void showFragment(int position) {

        Fragment fragment = fragmentList.get(position);
        manger.beginTransaction().hide(active).show(fragment).commit();
        active = fragment;
    }

    //Method to hide and show the fragment you need. It is called in the BottomBar click listener.
    public void hideFragment(Fragment hide, Fragment show) {
        manger.beginTransaction().hide(hide).show(show).commit();
        active = show;
    }

    public void hideFragment(Fragment show) {
        manger.beginTransaction().hide(active).show(show).commit();
        active = show;
    }

    public Fragment getActive() {
        return active;
    }

    public boolean checkActive(Fragment fragment) {
        return getActive().equals(fragment);
    }

    public boolean checkActive() {
        return getActive().equals(fragmentList.get(0));
    }
}
