package com.playerhub.ui.dashboard.home;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.playerhub.R;
import com.playerhub.customview.EventView;
import com.playerhub.network.RetrofitAdapter;
import com.playerhub.network.response.AnnouncementApi;
import com.playerhub.network.response.EventListApi.EventListResponseApi;
import com.playerhub.network.response.EventListApi.UpcommingEvent;
import com.playerhub.network.response.NotificationApi;
import com.playerhub.preference.Preferences;
import com.playerhub.ui.base.BaseFragment;
import com.playerhub.ui.base.BaseNetworkCheck;
import com.playerhub.ui.dashboard.DashBoardActivity;
import com.playerhub.ui.dashboard.home.announcement.AnnouncementDialogFragment;
import com.playerhub.ui.dashboard.home.eventdetails.EventDetailsFragment;
import com.playerhub.ui.dashboard.home.announcement.MoreAnnouncementFragment;
import com.playerhub.ui.dashboard.home.moreevent.MoreEventsFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function3;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeEventListFragment extends BaseNetworkCheck implements EventsAdapter.OnItemClickListener<UpcommingEvent>, CardPagerAdapter.OnItemClickListener, View.OnClickListener {

    private static final String TAG = "HomeEventListFragment";

    @BindView(R.id.eventListContent)
    LinearLayout eventListContent;
    @BindView(R.id.progress_layout)
    RelativeLayout progressLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.msg)
    TextView msg;

    @BindView(R.id.today_event_view)
    EventView eventView;

    @BindView(R.id.upcoming_event_view)
    EventView upcomingEventView;

    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;


    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    Unbinder unbinder;


    public HomeEventListFragment() {
        // Required empty public constructor
    }


    @Override
    public int getLayoutByID() {
        return 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_event_list, container, false);
        unbinder = ButterKnife.bind(this, view);


        eventView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                moveToMoreEventActivity(true);
            }
        });
        upcomingEventView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToMoreEventActivity(false);
            }
        });

        eventView.setOnItemClickListener(this);
        upcomingEventView.setOnItemClickListener(this);


        return view;
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void onRetryOrCallApi() {

    }

    @Override
    public void onManuallyParseError(Response<?> response, boolean isToastMsg) {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    private void initView() {

        mCardAdapter = new CardPagerAdapter(this);
//        mCardAdapter.addCardItem(new CardItem(getString(R.string.sample_date)));

        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);

        mViewPager.setAdapter(mCardAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(3);

        mViewPager.setVisibility(View.GONE);
        callEventListApi();
    }


    public void callEventListApi() {


        showHideLoading(true);


        Observable<NotificationApi> observableNoti = RetrofitAdapter.getNetworkApiServiceClient().getAllNotification(Preferences.INSTANCE.getAuthendicate())
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        Observable<EventListResponseApi> observableEvents = RetrofitAdapter.getNetworkApiServiceClient().fetchAllEvents(Preferences.INSTANCE.getAuthendicate())
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        Observable<AnnouncementApi> observableAnnouncement = RetrofitAdapter.getNetworkApiServiceClient().fetchAnnouncements(Preferences.INSTANCE.getAuthendicate())
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());


        disposable.add(Observable.combineLatest(observableNoti, observableEvents, observableAnnouncement, new Function3<NotificationApi, EventListResponseApi, AnnouncementApi, NotiAndEvents>() {
            @Override
            public NotiAndEvents apply(NotificationApi notificationApi, EventListResponseApi responseApi, AnnouncementApi announcementApi) throws Exception {
                return new NotiAndEvents(notificationApi, responseApi, announcementApi);
            }
        }).subscribeWith(new DisposableObserver<NotiAndEvents>() {
            @Override
            public void onNext(NotiAndEvents value) {
                showHideLoading(false);
                setNotiAndEventsData(value);
            }

            @Override
            public void onError(Throwable e) {
                showHideLoading(false);
            }

            @Override
            public void onComplete() {
                showHideLoading(false);
            }

        }));


    }


    private void setNotiAndEventsData(NotiAndEvents value) {


        if (value != null && value.getNotificationApi() != null) {

            setNotificationContent(value.getNotificationApi());

        } else {

            Toast.makeText(getContext(), "There is no notification ", Toast.LENGTH_SHORT).show();
        }


        if (value != null && value.getEventListResponseApi() != null) {

            setEventListContent(value.getEventListResponseApi());
        } else {

            showErrorMsg("There is no event");
        }

        if (value != null && value.getAnnouncementApi() != null) {

            setAnnouncementListContent(value.getAnnouncementApi());

        } else {

            showErrorMsg("There is no event");
        }

    }


    private void setNotificationContent(final NotificationApi notificationApi) {

        if (notificationApi.getSuccess()) {
//            if (mViewPager != null)
//                mViewPager.setVisibility(View.VISIBLE);
            List<NotificationApi.Data.Notification> list = notificationApi.getData().getNotifications() != null ? notificationApi.getData().getNotifications() : new ArrayList<NotificationApi.Data.Notification>();
            int count = 0;

            for (NotificationApi.Data.Notification notification : list) {

                if (notification.getSeen() == 0) {
                    count++;
                }
            }

            Preferences.INSTANCE.putNotiCount(count);


            if (getActivity() != null) {

                final Fragment fragment = (Fragment) ((DashBoardActivity) getActivity()).getSupportFragmentManager().findFragmentById(R.id.content);

                if (fragment instanceof HomeFragment) {

                    ((HomeFragment) fragment).setNotificationCount();

                }
            }


        } else {

            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
        }


    }


    public static <T> List<T> getOnlyFiveInTheList(List<T> list) {
        int s = list.size();
        if (s > 5) {
            list.subList(5, list.size()).clear();
        }
        return list;
    }


    private void setTodayEventData(List<UpcommingEvent> todayList) {

        eventView.setEvent("Today Events", getOnlyFiveInTheList(todayList), getString(R.string.no_today_event));

    }

    private void setUpComingEventData(List<UpcommingEvent> upcommingEventList) {


        upcomingEventView.setEvent("Upcoming Events", getOnlyFiveInTheList(upcommingEventList), getString(R.string.no_upcoming_event));

    }


    private void setAnnouncementListContent(AnnouncementApi response) {

        if (response != null && response.getSuccess()) {
            if (response.getData() != null) {
                setAnnouncementData(response.getData());

            }

        }

    }

    private void setAnnouncementData(List<AnnouncementApi.Datum> upcommingEventList) {

        if (upcommingEventList != null && !upcommingEventList.isEmpty()) {

            if (mViewPager != null)
                mViewPager.setVisibility(View.VISIBLE);

            if (!upcommingEventList.isEmpty()) {
                mCardAdapter.updateList(getOnlyFiveInTheList(upcommingEventList));

            } else {
                if (mViewPager != null)
                    mViewPager.setVisibility(View.GONE);
            }


        }

    }

    private void setEventListContent(EventListResponseApi response) {

        if (response != null && response.getSuccess()) {
            if (response.getData() != null) {
                setTodayEventData(response.getData().getTodayEvents());
                setUpComingEventData(response.getData().getUpcommingEvents());
            }

        }

    }

    @Override
    public void onClick(View v) {


        Log.e(TAG, "onClick: " + v.getId());


        switch (v.getId()) {

            case R.id.load_more_event:
                moveToMoreEventActivity(true);
                break;

            case R.id.upcoming_event_view:

                moveToMoreEventActivity(false);

                break;
        }


    }

    @Override
    public void networkConnected() {

        callEventListApi();
    }


    @Getter
    @Setter
    @AllArgsConstructor
    private class NotiAndEvents {

        public NotificationApi notificationApi;

        public EventListResponseApi eventListResponseApi;

        public AnnouncementApi announcementApi;

    }

    private void showHideLoading(boolean isLoading) {


        if (isLoading) {
            if (eventListContent != null)
                eventListContent.setVisibility(View.GONE);
            if (progressLayout != null)
                progressLayout.setVisibility(View.VISIBLE);
        } else {
            if (eventListContent != null)
                eventListContent.setVisibility(View.VISIBLE);
            if (eventListContent != null)
                progressLayout.setVisibility(View.GONE);
        }

    }


    private void showErrorMsg(String m) {

        if (eventListContent != null)
            eventListContent.setVisibility(View.GONE);
        if (progressLayout != null)
            progressLayout.setVisibility(View.VISIBLE);

        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
        msg.setVisibility(View.VISIBLE);
        msg.setText(m);


    }


    @Override
    public void OnItemClick(View view, UpcommingEvent datum, int position) {

//        startActivity(EventDetailsActivity.getIntent(getContext(), datum.getId()));


        if (getActivity() instanceof DashBoardActivity) {

            ((DashBoardActivity) getActivity()).callFragmentFromOutSide(EventDetailsFragment.getInstance(datum.getId()));

        }


    }

    @OnClick({R.id.load_more_events})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.load_more_events:
                moveToMoreEventActivity(true);
                break;

//            case R.id.today_event_view:
//                moveToMoreEventActivity(true);
//                break;
//
//            case R.id.upcoming_event_view:
//
//                moveToMoreEventActivity(false);
//                break;
        }
    }


    private void moveToMoreEventActivity(boolean isToday) {

        if (getActivity() instanceof DashBoardActivity) {

            ((DashBoardActivity) getActivity()).callFragmentFromOutSide(MoreEventsFragment.getInstance(false, isToday), isToday);

        }

    }

    private void moveToAnnouncementEventActivity() {


        if (getActivity() instanceof DashBoardActivity) {

            ((DashBoardActivity) getActivity()).callFragmentFromOutSide(new MoreAnnouncementFragment());

        }

    }

    @Override
    public void OnItemClick(int pos) {

        if (pos == mCardAdapter.getSize() - 1) {
            moveToAnnouncementEventActivity();
        } else {

            AnnouncementApi.Datum datum = mCardAdapter.getItem(pos);

            AnnouncementDialogFragment.getInstance(datum, false).show(getChildFragmentManager(), "Announcement");

        }

    }

    @Override
    public void OnItemDeleteClick(int pos) {
        mCardAdapter.remove(pos);
        upadateUI();
    }

    @Override
    public void OnItemMoreClick(int pos) {
        moveToAnnouncementEventActivity();
    }

    private void upadateUI() {


        if (!(mCardAdapter.getSize() > 0)) {

            mViewPager.setVisibility(View.GONE);
        } else {
            mViewPager.setVisibility(View.VISIBLE);
        }

    }
}
