package com.playerhub.ui.dashboard.home.moreevent;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.playerhub.R;
import com.playerhub.common.ActivityStats;
import com.playerhub.network.RetrofitAdapter;
import com.playerhub.network.response.EventListApi.EventListResponseApi;
import com.playerhub.network.response.EventListApi.UpcommingEvent;
import com.playerhub.preference.Preferences;
import com.playerhub.recyclerHelper.SimpleDividerItemDecoration;
import com.playerhub.ui.base.BaseFragment;
import com.playerhub.ui.dashboard.DashBoardActivity;
import com.playerhub.ui.dashboard.home.EventsAdapter;
import com.playerhub.ui.dashboard.home.eventdetails.EventDetailsFragment;
import com.vlonjatg.progressactivity.ProgressRelativeLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;

public class MoreEventsFragment extends BaseFragment implements EventsAdapter.OnItemClickListener<UpcommingEvent> {

    private static final String TAG = "MoreEventsActivity";

    @BindView(R.id.eventsView)
    RecyclerView eventsView;
    @BindView(R.id.progressActivity)
    ProgressRelativeLayout progressActivity;
    Unbinder unbinder;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.swipetoReferesh)
    SwipeRefreshLayout swipetoReferesh;


    private EventsAdapter eventsAdapter;


    private static final String isBackEnable = "isBackEnable";

    public static MoreEventsFragment getInstance(boolean isEnabled) {

        MoreEventsFragment fragment = new MoreEventsFragment();

        Bundle bundle = new Bundle();

        bundle.putBoolean(isBackEnable, isEnabled);

        fragment.setArguments(bundle);

        return fragment;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_more_events, container, false);

        unbinder = ButterKnife.bind(this, view);


        eventsAdapter = new EventsAdapter(getContext(), new ArrayList<UpcommingEvent>(), this);

        eventsView.setLayoutManager(new LinearLayoutManager(getContext()));

        eventsView.setAdapter(eventsAdapter);

        eventsView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));

        if (getArguments() != null) {
            boolean b = getArguments().getBoolean(isBackEnable, false);

            back.setVisibility(!b ? View.VISIBLE : View.INVISIBLE);
        }
        swipetoReferesh.setEnabled(false);
        swipetoReferesh.setColorSchemeColors(getColor(R.color.colorPrimary));
        swipetoReferesh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callEventListApi();
            }
        });

//
//        eventsView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                int topRowVerticalPosition = (recyclerView == null || recyclerView.getChildCount() == 0) ?
//                        0 : recyclerView.getChildAt(0).getTop();
//                LinearLayoutManager linearLayoutManager1 = (LinearLayoutManager) recyclerView.getLayoutManager();
//                int firstVisibleItem = linearLayoutManager1.findFirstVisibleItemPosition();
//                testImage.setVisibility(firstVisibleItem == 0 && topRowVerticalPosition >= 0 ? View.GONE : View.VISIBLE);
//
//            }
//        });


        callEventListApi();

        return view;
    }


    public void showHideBackButton(boolean isHide) {

        back.setVisibility(!isHide ? View.VISIBLE : View.INVISIBLE);
    }


    private int getColor(int color) {
        return ContextCompat.getColor(getContext(), color);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.eventsView, R.id.progressActivity})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.eventsView:
                break;
            case R.id.progressActivity:
                break;
        }
    }

    private void callEventListApi() {

        swipetoReferesh.setRefreshing(true);
        setProgressActivity(ActivityStats.LOADING);

        Observable observable = RetrofitAdapter.getNetworkApiServiceClient().fetchAllEvents(Preferences.INSTANCE.getAuthendicate());

        setObservableAndObserver(observable, new Observers<EventListResponseApi>(getContext(), false) {

            @Override
            protected void onSuccess(EventListResponseApi response) {
                setProgressActivity(ActivityStats.CONTENT);
                setData(response);
                swipetoReferesh.setRefreshing(false);
            }

            @Override
            protected void onFail(Throwable e) {
                showErrors(e.getMessage());
                swipetoReferesh.setRefreshing(false);
            }
        });

    }

    private void setData(EventListResponseApi response) {

        if (response.getSuccess()) {

            List<UpcommingEvent> todayEvent = response.getData().getTodayEvents();
            List<UpcommingEvent> upcommingEvents = response.getData().getUpcommingEvents();
            List<UpcommingEvent> eventList = new ArrayList<>();
            eventList.addAll(todayEvent);
            eventList.addAll(upcommingEvents);

            if (!eventList.isEmpty())
                eventsAdapter.updateList(eventList);
            else setProgressActivity(ActivityStats.EMPTY);

        } else {
            setProgressActivity(ActivityStats.EMPTY);
        }
    }

    @Override
    public void OnItemClick(View view, UpcommingEvent datum, int position) {
//        startActivity(EventDetailsActivity.getIntent(getContext(), datum.getId()));

        if (getActivity() instanceof DashBoardActivity) {

            ((DashBoardActivity) getActivity()).callFragmentFromOutSide(EventDetailsFragment.getInstance(datum.getId()));
        }

    }


    private void setProgressActivity(ActivityStats activityStats) {


        switch (activityStats) {
            case LOADING:
                showProgress(progressActivity);
                break;
            case EMPTY:
                showEmpty(progressActivity,
                        "No Data",
                        "There is no event available");

                break;
            case ERROR:
                showError(progressActivity,
                        "Error",
                        "",
                        "Try Again", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                callEventListApi();
                            }
                        });

                break;
            case CONTENT:
                showContent(progressActivity);
                break;
        }

    }



    public void showErrors(String msg){

        showError(progressActivity,
                msg,
                "",
                "Try Again", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        callEventListApi();
                    }
                });
    }



    @OnClick(R.id.back)
    public void onViewClicked() {

        if (getActivity() != null)
            getActivity().onBackPressed();

    }
}
