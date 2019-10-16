package com.playerhub.ui.dashboard;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Gravity;

import com.playerhub.R;

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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void showFragment(int position) {

        Fragment fragment = fragmentList.get(position);

        Fade slide = new Fade();
        slide.setDuration(200);
//        slide.setSlideEdge(Gravity.END);
//        slide.setMode(Slide.MODE_IN);

        Fade slide_out = new Fade();
        slide_out.setDuration(200);
//        slide_out.setSlideEdge(Gravity.START);
//        slide_out.setMode(Slide.MODE_OUT);

        fragment.setEnterTransition(slide);
        fragment.setExitTransition(slide_out);

//        manger.beginTransaction()
//                .setCustomAnimations(R.anim.sliding_in_left, R.anim.sliding_out_right).hide(active).show(fragment).commit();
        manger.beginTransaction()
                .hide(active).show(fragment).commit();
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
