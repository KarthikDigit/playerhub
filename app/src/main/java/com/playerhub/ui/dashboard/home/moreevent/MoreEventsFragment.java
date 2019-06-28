package com.playerhub.ui.dashboard.home.moreevent;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.playerhub.R;
import com.playerhub.network.RetrofitAdapter;
import com.playerhub.network.response.EventListApi.EventListResponseApi;
import com.playerhub.network.response.EventListApi.UpcommingEvent;
import com.playerhub.preference.Preferences;
import com.playerhub.recyclerHelper.SimpleDividerItemDecoration;
import com.playerhub.ui.base.MultiStateViewFragment;
import com.playerhub.ui.dashboard.DashBoardActivity;
import com.playerhub.ui.dashboard.home.EventsAdapter;
import com.playerhub.ui.dashboard.home.eventdetails.EventDetailsFragment;
import com.playerhub.ui.dashboard.profile.MyCallBack;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class MoreEventsFragment extends MultiStateViewFragment implements EventsAdapter.OnItemClickListener<UpcommingEvent>, TabLayout.BaseOnTabSelectedListener {

    private static final String TAG = "MoreEventsActivity";

    @BindView(R.id.eventsView)
    RecyclerView eventsView;
    Unbinder unbinder;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.swipetoReferesh)
    SwipeRefreshLayout swipetoReferesh;
    @BindView(R.id.tabs)
    TabLayout tabs;


    private EventsAdapter eventsAdapter;

    private List<UpcommingEvent> todayEventList = new ArrayList<>();
    private List<UpcommingEvent> upcommingEventList = new ArrayList<>();


    private static final String isBackEnable = "isBackEnable";
    private static final String isTodayEnable = "isTodayEnable";

    public static MoreEventsFragment getInstance(boolean isEnabled, boolean isToday) {

        MoreEventsFragment fragment = new MoreEventsFragment();

        Bundle bundle = new Bundle();

        bundle.putBoolean(isBackEnable, isEnabled);
        bundle.putBoolean(isTodayEnable, isToday);

        fragment.setArguments(bundle);

        return fragment;

    }

    @Override
    public int getLayoutByID() {
        return R.layout.activity_more_events;
    }


    @Override
    protected void initViews() {
        initView();

        callEventListApi();

    }

    @Override
    protected void onRetryOrCallApi() {
        callEventListApi();
    }

    @Override
    public void onManuallyParseError(Response<?> response, boolean isToastMsg) {

        showViewError("Something went wrong");
    }

    private void initView() {


        tabs.addTab(tabs.newTab().setText("Today"), true);
        tabs.addTab(tabs.newTab().setText("Upcoming"), false);

        eventsAdapter = new EventsAdapter(getContext(), new ArrayList<UpcommingEvent>(), this);

        eventsView.setLayoutManager(new LinearLayoutManager(getContext()));

        eventsView.setAdapter(eventsAdapter);

        eventsView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));

        if (getArguments() != null) {
            boolean b = getArguments().getBoolean(isBackEnable, false);

            back.setVisibility(!b ? View.VISIBLE : View.INVISIBLE);

            boolean isToday = getArguments().getBoolean(isTodayEnable, true);


            if (isToday) {
//                tabs.addTab(tabs.newTab().setText("Today"), true);
//                tabs.addTab(tabs.newTab().setText("Upcoming"), false);


                TabLayout.Tab tab = tabs.getTabAt(0);
                if (tab != null) {
                    tab.select();
                }
            } else {
//                tabs.addTab(tabs.newTab().setText("Today"), false);
//                tabs.addTab(tabs.newTab().setText("Upcoming"), true);

                TabLayout.Tab tab = tabs.getTabAt(1);
                if (tab != null) {
                    tab.select();
                }
            }


        }
        tabs.addOnTabSelectedListener(this);
        swipetoReferesh.setEnabled(false);
        swipetoReferesh.setColorSchemeColors(getColor(R.color.colorPrimary));
        swipetoReferesh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callEventListApi();
            }
        });

    }


    public void showHideBackButton(boolean isHide) {

        back.setVisibility(!isHide ? View.VISIBLE : View.INVISIBLE);
    }

    public void enableToday(boolean isToday) {

        if (isToday) {
            TabLayout.Tab tab = tabs.getTabAt(0);
            if (tab != null) {
                tab.select();
            }


        } else {
            TabLayout.Tab tab = tabs.getTabAt(1);
            if (tab != null) {
                tab.select();
            }
        }

        if (eventsAdapter.getItemCount() > 0)
            eventsView.smoothScrollToPosition(0);
    }


    private int getColor(int color) {
        return ContextCompat.getColor(getContext(), color);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }


    private void callEventListApi() {

        swipetoReferesh.setRefreshing(true);


        RetrofitAdapter.getNetworkApiServiceClient().fetchAllEvents(Preferences.INSTANCE.getAuthendicate())
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())

                .subscribe(new MyCallBack<EventListResponseApi>(getContext(), this, true, false) {
                    @Override
                    public void onSuccess(EventListResponseApi response) {
                        setData(response);
                        swipetoReferesh.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        swipetoReferesh.setRefreshing(false);
                    }
                });


    }

    private void setData(EventListResponseApi response) {

        if (response.getSuccess()) {

            todayEventList = response.getData().getTodayEvents();
            upcommingEventList = response.getData().getUpcommingEvents();

            boolean isToday = false;
            if (getArguments() != null) {
                isToday = getArguments().getBoolean(isTodayEnable, true);
            }


            if (isToday) {

                if (!todayEventList.isEmpty()) {
                    showViewContent();
                    eventsAdapter.updateList(todayEventList);
                } else showViewEmpty();
            } else {
                if (!upcommingEventList.isEmpty()) {
                    showViewContent();
                    eventsAdapter.updateList(upcommingEventList);
                } else showViewEmpty();
            }


        } else {
            showViewEmpty();
        }
    }

    @Override
    public void OnItemClick(View view, UpcommingEvent datum, int position) {

        if (getActivity() instanceof DashBoardActivity) {
            ((DashBoardActivity) getActivity()).callFragmentFromOutSide(EventDetailsFragment.getInstance(datum.getId()));
        }

    }


    @OnClick(R.id.back)
    public void onViewClicked() {

        if (getActivity() != null)
            getActivity().onBackPressed();

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        int position = tabs.getSelectedTabPosition();

        if (position == 0) {
            if (!todayEventList.isEmpty()) {
                showViewContent();
                eventsAdapter.updateList(todayEventList);
            } else showViewEmpty();
        } else {
            if (!upcommingEventList.isEmpty()) {
                showViewContent();
                eventsAdapter.updateList(upcommingEventList);
            } else showViewEmpty();
        }


    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {


    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


}
