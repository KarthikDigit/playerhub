package com.playerhub.ui.dashboard.messages;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.playerhub.R;
import com.playerhub.network.response.ContactListApi;
import com.playerhub.preference.Preferences;
import com.playerhub.ui.base.BaseFragment;
import com.playerhub.ui.dashboard.chat.ChatActivity;
import com.playerhub.ui.dashboard.messages.filteractivity.FilterActivity;
import com.playerhub.viewpageadapter.ViewPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessagesFragment extends BaseFragment implements SearchView.OnQueryTextListener {


    Unbinder unbinder;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @BindView(R.id.filter)
    ImageView mFilter;
    @BindView(R.id.searchView)
    SearchView mSearchView;

    @BindView(R.id.filter_layout)
    LinearLayout mFilterLayout;

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

//                if (i == 1) mFilterLayout.setVisibility(View.VISIBLE);
//                else mFilterLayout.setVisibility(View.GONE);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });


        mSearchView.setOnQueryTextListener(this);

        loadChatActivity();


        return view;
    }


    @OnClick({R.id.filter})
    public void onFilterClick(View view) {


        startActivity(new Intent(view.getContext(), FilterActivity.class));

    }


    private void loadChatActivity() {


        if (getActivity() != null) {

            if (getActivity().getIntent() != null) {

                String id = getActivity().getIntent().getStringExtra("id");
                String name = getActivity().getIntent().getStringExtra("title");

                if (id != null && name != null) {

                    ContactListApi.Datum item = new ContactListApi.Datum();
                    item.setId(Integer.valueOf(id));
                    item.setName(name);

                    Intent intent = new Intent(getContext(), ChatActivity.class);
                    intent.putExtra("user", item);
                    startActivity(intent);


                }

            }

        }


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

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {


        if (viewPager != null) {
//            pagerAdapter.notifyDataSetChanged();
            int pos = viewPager.getCurrentItem();

            MessageBaseFragment fragment = (MessageBaseFragment) pagerAdapter.getItem(pos);

            fragment.searchData(s);
        }


        return true;
    }
}
