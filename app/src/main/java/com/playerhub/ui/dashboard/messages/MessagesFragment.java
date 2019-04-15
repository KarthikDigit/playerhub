package com.playerhub.ui.dashboard.messages;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.playerhub.R;
import com.playerhub.preference.Preferences;
import com.playerhub.ui.base.BaseFragment;
import com.playerhub.viewpageadapter.ViewPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessagesFragment extends BaseFragment {


    Unbinder unbinder;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    private ViewPagerAdapter pagerAdapter;

    public MessagesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) updateAdapter();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        unbinder = ButterKnife.bind(this, view);

        pagerAdapter = new ViewPagerAdapter(getChildFragmentManager());

        pagerAdapter.addFragment(RecentFragment.getInstance("recent"), "Recent");
        pagerAdapter.addFragment(ContactListFragment.getInstance("contacts"), "Contacts");

        viewPager.setAdapter(pagerAdapter);

        tabs.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                updateAdapter();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        return view;
    }


    public void updateAdapter() {

        if (viewPager != null) {
//            pagerAdapter.notifyDataSetChanged();
            int pos = viewPager.getCurrentItem();

            MessageBaseFragment fragment = (MessageBaseFragment) pagerAdapter.getItem(pos);

            fragment.refreshData();
        }


//        if (messageView != null && messageView.getAdapter() != null)
//            messageView.getAdapter().notifyDataSetChanged();

    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.tabs, R.id.viewPager})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tabs:
                break;
            case R.id.viewPager:
                break;
        }
    }
}
