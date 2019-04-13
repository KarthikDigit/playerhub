package com.playerhub.ui.dashboard.home.moreevent;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.playerhub.R;
import com.playerhub.common.ActivityStats;
import com.playerhub.common.CallbackWrapper;
import com.playerhub.network.RetrofitAdapter;
import com.playerhub.network.response.EventListApi.EventListResponseApi;
import com.playerhub.network.response.EventListApi.UpcommingEvent;
import com.playerhub.preference.Preferences;
import com.playerhub.recyclerHelper.SimpleDividerItemDecoration;
import com.playerhub.ui.base.BaseActivity;
import com.playerhub.ui.dashboard.home.EventsAdapter;
import com.playerhub.ui.dashboard.home.eventdetails.EventDetailsActivity;
import com.vlonjatg.progressactivity.ProgressRelativeLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MoreEventsActivity extends BaseActivity implements EventsAdapter.OnItemClickListener<UpcommingEvent> {

    private static final String TAG = "MoreEventsActivity";

    @BindView(R.id.eventsView)
    RecyclerView eventsView;
    @BindView(R.id.progressActivity)
    ProgressRelativeLayout progressActivity;
    private EventsAdapter eventsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_events);
        ButterKnife.bind(this);

        setBackButtonEnabledAndTitle("Events");


        eventsAdapter = new EventsAdapter(this, new ArrayList<UpcommingEvent>(), this);

        eventsView.setLayoutManager(new LinearLayoutManager(this));

        eventsView.setAdapter(eventsAdapter);

        eventsView.addItemDecoration(new SimpleDividerItemDecoration(this));

        callEventListApi();


    }

    private void callEventListApi() {

        setProgressActivity(ActivityStats.LOADING);

        Observable observable = RetrofitAdapter.getNetworkApiServiceClient().fetchAllEvents(Preferences.INSTANCE.getAuthendicate());

        setObservableAndObserver(observable, new Observers<EventListResponseApi>(this, false) {

            @Override
            protected void onSuccess(EventListResponseApi response) {
                setProgressActivity(ActivityStats.CONTENT);
                setData(response);
            }

            @Override
            protected void onFail(Throwable e) {
                setProgressActivity(ActivityStats.ERROR);
            }
        });

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
//                        setProgressActivity(ActivityStats.CONTENT);
//                        if (response.getSuccess()) {
//
//                            List<UpcommingEvent> todayEvent = response.getData().getTodayEvents();
//                            List<UpcommingEvent> upcommingEvents = response.getData().getUpcommingEvents();
//
//
//                            List<UpcommingEvent> eventList = new ArrayList<>();
//                            eventList.addAll(todayEvent);
//                            eventList.addAll(upcommingEvents);
//
//                            eventsAdapter.updateList(eventList);
//
//                        } else {
//                            setProgressActivity(ActivityStats.EMPTY);
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        setProgressActivity(ActivityStats.ERROR);
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });


    }


    private void setData(EventListResponseApi response) {

        if (response.getSuccess()) {

            List<UpcommingEvent> todayEvent = response.getData().getTodayEvents();
            List<UpcommingEvent> upcommingEvents = response.getData().getUpcommingEvents();


            Log.e(TAG, "setData: " + todayEvent.size() + "  up " + upcommingEvents.size());


            List<UpcommingEvent> eventList = new ArrayList<>();
            eventList.addAll(todayEvent);
            eventList.addAll(upcommingEvents);

            eventsAdapter.updateList(eventList);

        } else {
            setProgressActivity(ActivityStats.EMPTY);
        }
    }

    @Override
    public void OnItemClick(View view, UpcommingEvent datum, int position) {
        startActivity(EventDetailsActivity.getIntent(this, datum.getId()));
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
}
