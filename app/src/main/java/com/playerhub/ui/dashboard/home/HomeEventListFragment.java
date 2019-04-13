package com.playerhub.ui.dashboard.home;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.playerhub.R;
import com.playerhub.common.CustomViewPager;
import com.playerhub.network.RetrofitAdapter;
import com.playerhub.network.response.AnnouncementApi;
import com.playerhub.network.response.EventListApi.EventListResponseApi;
import com.playerhub.network.response.EventListApi.UpcommingEvent;
import com.playerhub.network.response.NotificationApi;
import com.playerhub.preference.Preferences;
import com.playerhub.recyclerHelper.SimpleDividerItemDecoration;
import com.playerhub.ui.base.BaseFragment;
import com.playerhub.ui.dashboard.DashBoardActivity;
import com.playerhub.ui.dashboard.home.announcement.AnnouncementDialogFragment;
import com.playerhub.ui.dashboard.home.eventdetails.EventDetailsFragment;
import com.playerhub.ui.dashboard.home.moreevent.MoreAnnouncementFragment;
import com.playerhub.ui.dashboard.home.moreevent.MoreEventsFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function3;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeEventListFragment extends BaseFragment implements EventsAdapter.OnItemClickListener<UpcommingEvent>, CardPagerAdapter.OnItemClickListener, AnnouncementAdapter.OnItemClickListener<AnnouncementApi.Datum> {

    private static final String TAG = "HomeEventListFragment";

    @BindView(R.id.eventListContent)
    LinearLayout eventListContent;
    @BindView(R.id.progress_layout)
    RelativeLayout progressLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.msg)
    TextView msg;
    @BindView(R.id.todayEventsView)
    RecyclerView todayEventsView;
    @BindView(R.id.today_event_content)
    CardView todayEventContent;
    @BindView(R.id.upcomingEventView)
    RecyclerView upcomingEventView;
    @BindView(R.id.upcomming_event_content)
    CardView upcommingEventContent;
    @BindView(R.id.msg_today_event)
    TextView msgTodayEvent;
    @BindView(R.id.msg_upcoming_event)
    TextView msgUpcomingEvent;
    @BindView(R.id.announcementEventsView)
    RecyclerView announcementEventsView;
    @BindView(R.id.msg_announcement_event)
    TextView msgAnnouncementEvent;
    @BindView(R.id.announcement_event_content)
    CardView announcementEventContent;
    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;
    private EventsAdapter todayEventAdapter;
    private EventsAdapter upComingEventAdapter;
    private AnnouncementAdapter announcementAdapter;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    Unbinder unbinder;


    public HomeEventListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_event_list, container, false);
        unbinder = ButterKnife.bind(this, view);

//        initView();


        return view;
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
//        mCardAdapter.addCardItem(new CardItem(getString(R.string.sample_date)));
//        mCardAdapter.addCardItem(new CardItem(getString(R.string.sample_date)));
//        mCardAdapter.addCardItem(new CardItem(getString(R.string.sample_date)));

        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);

        mViewPager.setAdapter(mCardAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(3);

        todayEventAdapter = new EventsAdapter(getContext(), new ArrayList<UpcommingEvent>(), this);
        upComingEventAdapter = new EventsAdapter(getContext(), new ArrayList<UpcommingEvent>(), this);
        announcementAdapter = new AnnouncementAdapter(getContext(), new ArrayList<AnnouncementApi.Datum>(), this);

        todayEventsView.setLayoutManager(new LinearLayoutManager(getContext()));
        upcomingEventView.setLayoutManager(new LinearLayoutManager(getContext()));
        announcementEventsView.setLayoutManager(new LinearLayoutManager(getContext()));

        todayEventsView.setAdapter(todayEventAdapter);
        upcomingEventView.setAdapter(upComingEventAdapter);
        announcementEventsView.setAdapter(announcementAdapter);

        todayEventsView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        todayEventsView.setNestedScrollingEnabled(false);

        upcomingEventView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        upcomingEventView.setNestedScrollingEnabled(false);

        announcementEventsView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        announcementEventsView.setNestedScrollingEnabled(false);

        mViewPager.setVisibility(View.GONE);
        callEventListApi();
    }


    private void callEventListApi() {


        showHideLoading(true);
//        RetrofitAdapter.getNetworkApiServiceClient().fetchAllEvents(Preferences.INSTANCE.getAuthendicate())
//                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<EventListResponseApi>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(EventListResponseApi response) {
//
//                        showHideLoading(false);
//                        if (response.getSuccess()) {
//
//
//                            List<UpcommingEvent> list = response.getData().getUpcommingEvents();
//                            int s = list.size();
//                            if (s > 0) {
//
//                                if (s > 5) {
//
//
//                                    for (int n = s - 1; n > 0; n--) {
//                                        list.remove(n);
//                                    }
//                                    eventsAdapter.updateList(list);
//
//                                } else {
//                                    eventsAdapter.updateList(list);
//                                }
//
//
//                            } else showErrorMsg(getString(R.string.no_events));
//
//                        } else {
//
//                            showErrorMsg(getString(R.string.no_events));
//                        }
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        showHideLoading(false);
//                        showErrorMsg(getString(R.string.no_events));
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//
//
//        RetrofitAdapter.getNetworkApiServiceClient().getAllNotification(Preferences.INSTANCE.getAuthendicate())
//                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new CallbackWrapper<NotificationApi>(getActivity()) {
//                    @Override
//                    protected void onSuccess(NotificationApi notificationApi) {
//
//                        if (notificationApi.getSuccess()) {
//                            if (mViewPager != null)
//                                mViewPager.setVisibility(View.VISIBLE);
//                            List<NotificationApi.Datum> list = notificationApi.getData();
//
//
//                            int s = list.size();
//                            if (s > 0) {
//
//                                if (s > 5) {
//
//
//                                    for (int n = s - 1; n > 0; n--) {
//                                        list.remove(n);
//                                    }
//                                    mCardAdapter.updateList(list);
//
//                                } else {
//                                    mCardAdapter.updateList(list);
//                                }
//
//
//                            } else {
//                                if (mViewPager != null)
//                                    mViewPager.setVisibility(View.GONE);
//
//                                Toast.makeText(getContext(), "There is no notification ", Toast.LENGTH_SHORT).show();
//                            }
//
//
//                            Fragment fragment = (Fragment) ((DashBoardActivity) getActivity()).getSupportFragmentManager().findFragmentById(R.id.content);
//
//                            if (fragment instanceof HomeFragment) {
//
//                                ((HomeFragment) fragment).setNotificationCount(list.size());
//                            }
//
//
//                        } else {
//
//                            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//                });


        Observable<NotificationApi> observableNoti = RetrofitAdapter.getNetworkApiServiceClient().getAllNotification(Preferences.INSTANCE.getAuthendicate())
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        Observable<EventListResponseApi> observableEvents = RetrofitAdapter.getNetworkApiServiceClient().fetchAllEvents(Preferences.INSTANCE.getAuthendicate())
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        Observable<AnnouncementApi> observableAnnouncement = RetrofitAdapter.getNetworkApiServiceClient().fetchAnnouncements(Preferences.INSTANCE.getAuthendicate())
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());


//
//        Observable<NotiAndEvents> notiAndEventsObservable = Observable.zip(observableNoti, observableEvents, new BiFunction<NotificationApi, EventListResponseApi, NotiAndEvents>() {
//            @Override
//            public NotiAndEvents apply(NotificationApi notificationApi, EventListResponseApi responseApi) throws Exception {
//                return new NotiAndEvents(notificationApi, responseApi);
//            }
//        });

        disposable.add(Observable.combineLatest(observableNoti, observableEvents, observableAnnouncement, new Function3<NotificationApi, EventListResponseApi, AnnouncementApi, NotiAndEvents>() {
            @Override
            public NotiAndEvents apply(NotificationApi notificationApi, EventListResponseApi responseApi, AnnouncementApi announcementApi) throws Exception {
                return new NotiAndEvents(notificationApi, responseApi, announcementApi);
            }
        }).subscribeWith(new DisposableObserver<NotiAndEvents>() {
            @Override
            public void onNext(NotiAndEvents value) {
                showHideLoading(false);


                if (value != null && value.getNotificationApi() != null) {

                    setNotificationContent(value.getNotificationApi());
                } else {

                    Toast.makeText(getContext(), "There is no notification ", Toast.LENGTH_SHORT).show();
                }


                if (value != null && value.getEventListResponseApi() != null) {

                    setEventListContent(value.getEventListResponseApi());
                } else {

                    showErrorMsg("There is no data");
                }

                if (value != null && value.getAnnouncementApi() != null) {

                    setAnnouncementListContent(value.getAnnouncementApi());
                } else {

                    showErrorMsg("There is no data");
                }

            }

            @Override
            public void onError(Throwable e) {
                showHideLoading(false);
                Log.e(TAG, "onError: " + e.getMessage());

            }

            @Override
            public void onComplete() {

            }
        }));


//        notiAndEventsObservable.subscribeWith(new DisposableObserver<NotiAndEvents>() {
//
//            @Override
//            public void onNext(NotiAndEvents value) {
//
//                showHideLoading(false);
//
//
//                if (value != null && value.getNotificationApi() != null) {
//
//                    setNotificationContent(value.getNotificationApi());
//                } else {
//
//                    Toast.makeText(getContext(), "There is no notification ", Toast.LENGTH_SHORT).show();
//                }
//
//
//                if (value != null && value.getEventListResponseApi() != null) {
//
//                    setEventListContent(value.getEventListResponseApi());
//                } else {
//
//                    showErrorMsg("There is no data");
//                }
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                showHideLoading(false);
//                Log.e(TAG, "onError: " + e.getMessage());
//
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        });

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

//            if (list != null && !list.isEmpty()) {
//                mCardAdapter.updateList(getOnlyFiveInTheList(list));
//
//            } else {
//                if (mViewPager != null)
//                    mViewPager.setVisibility(View.GONE);
//
//                if (getActivity() != null)
//                    Toast.makeText(getContext(), "There is no notification ", Toast.LENGTH_SHORT).show();
//            }


            if (getActivity() != null) {

                final Fragment fragment = (Fragment) ((DashBoardActivity) getActivity()).getSupportFragmentManager().findFragmentById(R.id.content);

                if (fragment instanceof HomeFragment) {

                    ((HomeFragment) fragment).setNotificationCount();
//                    Observable.just(list).map(new Function<List<NotificationApi.Data.Notification>, Integer>() {
//                        @Override
//                        public Integer apply(List<NotificationApi.Data.Notification> notifications) throws Exception {
//
//                            int count = 0;
//
//                            for (NotificationApi.Data.Notification notification : notifications) {
//
//                                if (notification.getSeen() == 0) {
//                                    count++;
//                                }
//                            }
//
//                            return count;
//                        }
//                    }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
//                            .subscribe(new Consumer<Integer>() {
//                                @Override
//                                public void accept(Integer integer) throws Exception {
//                                    ((HomeFragment) fragment).setNotificationCount(integer);
//
//                                }
//                            });

                }
            }


        } else {

            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
        }


    }


    private <T> List<T> getOnlyFiveInTheList(List<T> list) {
        int s = list.size();
        if (s > 5) {

            list.subList(5, list.size()).clear();

//            for (int n = s - 1; n > 4; n--) {
//                list.remove(n);
//            }
        }
        return list;
    }


    private void setTodayEventData(List<UpcommingEvent> todayList) {

        if (todayList != null && !todayList.isEmpty()) {

            todayEventAdapter.updateList(getOnlyFiveInTheList(todayList));


        } else {

            todayEventsView.setVisibility(View.GONE);
            msgTodayEvent.setVisibility(View.VISIBLE);

            showToast("No today events ");
        }


    }

    private void setUpComingEventData(List<UpcommingEvent> upcommingEventList) {

        if (upcommingEventList != null && !upcommingEventList.isEmpty()) {

            upComingEventAdapter.updateList(getOnlyFiveInTheList(upcommingEventList));

        } else {

            upcomingEventView.setVisibility(View.GONE);
            msgUpcomingEvent.setVisibility(View.VISIBLE);
            showToast("No upcoming events ");
        }

    }


    private void setAnnouncementListContent(AnnouncementApi response) {

        if (response != null && response.getSuccess()) {
            if (response.getData() != null) {
                setAnnouncementData(response.getData());

            }

        } else {

//            announcementEventsView.setVisibility(View.GONE);
//            msgAnnouncementEvent.setVisibility(View.VISIBLE);
        }

    }

    private void setAnnouncementData(List<AnnouncementApi.Datum> upcommingEventList) {

        if (upcommingEventList != null && !upcommingEventList.isEmpty()) {

            announcementAdapter.updateList(getOnlyFiveInTheList(upcommingEventList));

            if (mViewPager != null)
                mViewPager.setVisibility(View.VISIBLE);

            if (!upcommingEventList.isEmpty()) {
                mCardAdapter.updateList(getOnlyFiveInTheList(upcommingEventList));

            } else {
                if (mViewPager != null)
                    mViewPager.setVisibility(View.GONE);

//                if (getActivity() != null)
//                    Toast.makeText(getContext(), "There is no notification ", Toast.LENGTH_SHORT).show();
            }

//            announcementEventsView.setVisibility(View.VISIBLE);
//            msgAnnouncementEvent.setVisibility(View.GONE);
//            Log.e(TAG, "setAnnouncementData: " + new Gson().toJson(upcommingEventList));


        } else {

//            announcementEventsView.setVisibility(View.GONE);
//            msgAnnouncementEvent.setVisibility(View.VISIBLE);
//            showToast("No announcements");
        }

    }

    private void setEventListContent(EventListResponseApi response) {

        if (response != null && response.getSuccess()) {
            if (response.getData() != null) {
                setTodayEventData(response.getData().getTodayEvents());
                setUpComingEventData(response.getData().getUpcommingEvents());

//                Log.e(TAG, "setEventListContent: event size " + response.getData().getTodayEvents().size() + "   " + response.getData().getUpcommingEvents().size());

            }

        } else {
//            showErrorMsg(getString(R.string.no_events));

            todayEventsView.setVisibility(View.GONE);
            msgTodayEvent.setVisibility(View.VISIBLE);
            upcomingEventView.setVisibility(View.GONE);
            msgUpcomingEvent.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void OnAnnouncemnetClick(View view, AnnouncementApi.Datum datum, int position) {
        AnnouncementDialogFragment.getInstance(datum, false).show(getChildFragmentManager(), "Announcement");
    }


    @Getter
    @Setter
    @AllArgsConstructor
    private class NotiAndEvents {

        public NotificationApi notificationApi;

        public EventListResponseApi eventListResponseApi;

        public AnnouncementApi announcementApi;

//        public NotiAndEvents(NotificationApi notificationApi, EventListResponseApi eventListResponseApi) {
//            this.notificationApi = notificationApi;
//            this.eventListResponseApi = eventListResponseApi;
//        }
//
//        public NotificationApi getNotificationApi() {
//
//            return notificationApi;
//        }
//
//        public void setNotificationApi(NotificationApi notificationApi) {
//            this.notificationApi = notificationApi;
//        }
//
//        public EventListResponseApi getEventListResponseApi() {
//            return eventListResponseApi;
//        }
//
//        public void setEventListResponseApi(EventListResponseApi eventListResponseApi) {
//            this.eventListResponseApi = eventListResponseApi;
//        }
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

    @OnClick({R.id.load_more_today_events, R.id.load_more_upcoming_events, R.id.load_more_events, R.id.load_more_announcement_events})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.load_more_today_events:
                moveToMoreEventActivity();
                break;
            case R.id.load_more_upcoming_events:
                moveToMoreEventActivity();
                break;
            case R.id.load_more_events:
                moveToMoreEventActivity();
                break;
            case R.id.load_more_announcement_events:
                moveToAnnouncementEventActivity();
                break;
        }
    }


    private void moveToMoreEventActivity() {

//        Intent intent = new Intent(getActivity(), MoreEventsActivity.class);
//
//        startActivity(intent);

        if (getActivity() instanceof DashBoardActivity) {

            ((DashBoardActivity) getActivity()).callFragmentFromOutSide(new MoreEventsFragment());

        }

    }

    private void moveToAnnouncementEventActivity() {

//        Intent intent = new Intent(getActivity(), MoreEventsActivity.class);
//
//        startActivity(intent);

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
