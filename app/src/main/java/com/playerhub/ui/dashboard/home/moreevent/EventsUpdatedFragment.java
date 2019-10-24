package com.playerhub.ui.dashboard.home.moreevent;


import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.playerhub.R;
import com.playerhub.network.RetrofitAdapter;
import com.playerhub.network.response.EventListApi.EventListResponseApi;
import com.playerhub.network.response.EventListApi.UpcommingEvent;
import com.playerhub.preference.Preferences;
import com.playerhub.recyclerHelper.SimpleDividerItemDecorationFullLine;
import com.playerhub.ui.base.BaseFragment;
import com.playerhub.ui.dashboard.home.EventsAdapterUpdated;
import com.playerhub.ui.dashboard.home.eventdetails.EventDetailsActivity;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;
import com.southernbox.nestedcalendar.behavior.CalendarBehavior;

import org.threeten.bp.LocalDate;
import org.threeten.bp.temporal.WeekFields;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsUpdatedFragment extends BaseFragment implements EventsAdapterUpdated.OnItemClickListener<UpcommingEvent> {


    @BindView(R.id.calendar)
    MaterialCalendarView calendarView;
    @BindView(R.id.recycler_view)
    RecyclerView rv;
    Unbinder unbinder;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.month_display)
    TextView monthDisplay;
    @BindView(R.id.calendar_go_back)
    ImageView calendarGoBack;
    @BindView(R.id.calendar_forward)
    ImageView calendarForward;
    @BindView(R.id.noevents)
    TextView noevents;
    @BindView(R.id.showAllEvents)
    ImageView showAllEvents;
    @BindView(R.id.calendar_back_forward_view)
    CardView calendarBackForwardView;
    @BindView(R.id.today_date)
    TextView todayDate;
    //    @BindView(R.id.root)
//    MotionLayout root;
    private CalendarBehavior calendarBehavior;
    private int dayOfWeek;
    private int year;
    private int dayOfMonth;
    private EventsAdapterUpdated eventsAdapter;
    private List<UpcommingEvent> eventList = new ArrayList<>();
    private String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    private boolean isFullEvents = true;

    private CalendarDay calendarDay1 = CalendarDay.from(LocalDate.now());

    private Map<CalendarDay, List<UpcommingEvent>> filtered = new LinkedHashMap<>();

    public EventsUpdatedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_events_updated, container, false);
        unbinder = ButterKnife.bind(this, view);

        initCalendarView();
        initRecyclerView();
        return view;
    }


    private void initCalendarView() {
        calendarView.setTopbarVisible(false);
//        CoordinatorLayout.Behavior behavior =
//                ((CoordinatorLayout.LayoutParams) calendarView.getLayoutParams()).getBehavior();
//        if (behavior instanceof CalendarBehavior) {
//            calendarBehavior = (CalendarBehavior) behavior;
//        }
        Calendar calendar = Calendar.getInstance();
        calendarView.setSelectedDate(LocalDate.now());
        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
//        setTitle((calendar.get(Calendar.MONTH) + 1) + "月");

        String mon = months[(calendar.get(Calendar.MONTH))];

        year = calendar.get(Calendar.YEAR);

        monthDisplay.setText(String.format("%s %d", mon, year));

//        try {
//            String month = getMonth(calendar.getTime().toString());
//            monthDisplay.setText(month);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }


//        List<CalendarDay> calendarDays = new ArrayList<>();
//        calendarDays.add(CalendarDay.from(LocalDate.now()));
////        calendarDays.add(CalendarDay.today());
//        calendarDays.add(CalendarDay.from(2019, 2, 28));
//
//        CalendarDay.from(LocalDate.now());
//
//        calendarView.addDecorator(new EventDecorator(Color.RED, calendarDays));


//        if (calendarBehavior != null) {


        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget,
                                       @NonNull CalendarDay calendarDay,
                                       boolean selected) {
                calendarDay1 = calendarDay;
                LocalDate localDate = calendarDay.getDate();
                WeekFields weekFields = WeekFields.of(Locale.getDefault());
//                    calendarBehavior.setWeekOfMonth(localDate.get(weekFields.weekOfMonth()));
                if (selected) {
                    dayOfWeek = localDate.getDayOfWeek().getValue();
                    dayOfMonth = localDate.getDayOfMonth();
                    year = localDate.getYear();
                }

//                try {
//                    todayDate.setText(getMonthDay(calendarDay.getDate().toString()));
//                } catch (ParseException e) {
////                    e.printStackTrace();
//                }

                showDataByDate(calendarDay1);
            }
        });
        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay calendarDay) {
//                    if (calendarBehavior.getCalendarMode() == null) {
//                        return;
//                    }
                LocalDate localDate = calendarDay.getDate();
                LocalDate newDate;
                if (calendarView.getCalendarMode() == CalendarMode.WEEKS) {
                    newDate = localDate.plusDays(dayOfWeek - 1);
                    dayOfMonth = newDate.getDayOfMonth();
                } else {
                    int monthDays = localDate.getMonth().length(localDate.isLeapYear());
                    if (dayOfMonth > monthDays) {
                        dayOfMonth = monthDays;
                    }
                    newDate = localDate.plusDays(dayOfMonth - 1);
                    dayOfWeek = newDate.getDayOfWeek().getValue();
                }
                widget.setSelectedDate(newDate);
                WeekFields weekFields = WeekFields.of(Locale.getDefault());
//                    calendarBehavior.setWeekOfMonth(newDate.get(weekFields.weekOfMonth()));
//                    setTitle(newDate.getMonth().getValue() + "月");
                String mon = months[newDate.getMonth().getValue() - 1];
                year = localDate.getYear();


                monthDisplay.setText(String.format("%s %d", mon, year));

            }
        });
//        }


//        root.setTransitionListener(new MotionLayout.TransitionListener() {
//
//
//            @Override
//            public void onTransitionChange(MotionLayout motionLayout, int i, int i1, final float v) {
//
//
////                Log.e(TAG, "onTransitionChange: " + v + "  " + i + "   " + i1);
//
//
////                if (v == 0.0f) {
////                    calendarView.state().edit()
////                            .setCalendarDisplayMode(CalendarMode.MONTHS)
////                            .commit();
////                } else
//
//
////                motionLayout.post(new Runnable() {
////                    @Override
////                    public void run() {
//                if (v >= 0.7f) {
//
//
//                    calendarView.state().edit()
//                            .setCalendarDisplayMode(CalendarMode.WEEKS)
//                            .commit();
//
//
//                } else if (v <= 0.3f) {
//
//                    calendarView.state().edit()
//                            .setCalendarDisplayMode(CalendarMode.MONTHS)
//                            .commit();
//                }
////                    }
////                });
//
//
//            }
//
//            @Override
//            public void onTransitionCompleted(MotionLayout motionLayout, int i) {
//
//
//            }
//
//
//        });
    }

    private boolean isViewChanged = false;
    private boolean isViewChanged1 = false;

    private static String getMonth(String date) throws ParseException {

        Date d = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);

        return new SimpleDateFormat("MMM").format(cal.getTime());
//        return new SimpleDateFormat("MMMM").format(cal.getTime());
    }

    private static String getMonthDay(String date) throws ParseException {

        Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);

        return new SimpleDateFormat("EEEE MMMM d, yyyy").format(cal.getTime());
//        return new SimpleDateFormat("MMMM").format(cal.getTime());
    }


    private void initRecyclerView() {
//        rv.setLayoutManager(new LinearLayoutManager(getContext()));
//        String[] names = getResources().getStringArray(R.array.query_suggestions);
//        List<String> mList = new ArrayList<>();
//        Collections.addAll(mList, names);
//        rv.setAdapter(new ListAdapter(getContext(), mList));
//        RecyclerView.ItemDecoration itemDecoration =
//                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
//        rv.addItemDecoration(itemDecoration);

//        eventsAdapter = new EventsAdapter(getContext(), new ArrayList<UpcommingEvent>(), this);
//
//        rv.setLayoutManager(new LinearLayoutManager(getContext()));
//        rv.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
//        rv.setAdapter(eventsAdapter);


        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.addItemDecoration(new SimpleDividerItemDecorationFullLine(getContext()));

        eventsAdapter = new EventsAdapterUpdated(getContext(), new ArrayList<UpcommingEvent>(), this);

        rv.setAdapter(eventsAdapter);

        callEventListApi();


    }


//    @Override
//    public void onHiddenChanged(boolean hidden) {
//        super.onHiddenChanged(hidden);
//
//        if (!hidden) callEventListApi();
//    }

    private void callEventListApi() {


        RetrofitAdapter.getNetworkApiServiceClient().fetchAllEvents(Preferences.INSTANCE.getAuthendicate())
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())

                .subscribe(new Observer<EventListResponseApi>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(EventListResponseApi response) {
                        setData(response);
                    }

                    @Override
                    public void onError(Throwable e) {

                        showToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }


    private void filterBaseOnDate(List<UpcommingEvent> list) {

        Set<CalendarDay> dateOnly = new HashSet<>();

        for (UpcommingEvent upcommingEvent : list
                ) {

            dateOnly.add(upcommingEvent.getLocalDate());
        }


        List<CalendarDay> localDateList = new ArrayList<>(dateOnly);

        calendarView.addDecorator(new EventDecorator(Color.RED, localDateList));

        for (int i = 0; i < localDateList.size(); i++) {

            CalendarDay localDate = localDateList.get(i);

            List<UpcommingEvent> newList = new ArrayList<>();

            for (int j = 0; j < list.size(); j++) {

                UpcommingEvent upcommingEvent = list.get(j);

                if (localDate.equals(upcommingEvent.getLocalDate())) {

                    newList.add(upcommingEvent);

                }

            }

            filtered.put(localDate, newList);

        }

//        showDataByDate(calendarDay1);

    }


    private void showDataByDate(CalendarDay calendarDay) {

        try {
            todayDate.setText(getMonthDay(calendarDay.getDate().toString()).toUpperCase());
        } catch (ParseException e) {
//                    e.printStackTrace();
        }


        List<UpcommingEvent> upcommingEventList = new ArrayList<>();

        if (filtered.get(calendarDay) != null) {

            upcommingEventList.addAll(filtered.get(calendarDay));

            noevents.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);


            if (!upcommingEventList.isEmpty()) {
//                showViewContent();
//                eventsAdapter.updateList(upcommingEventList);
                UpcommingEvent upcommingEvent = new UpcommingEvent();
                upcommingEvent.setEmptyView(true);
                upcommingEventList.add(upcommingEvent);

                eventsAdapter.updateList(upcommingEventList);

            } else {
                noevents.setVisibility(View.VISIBLE);
                rv.setVisibility(View.GONE);
            }
        } else {


            noevents.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);


        }
    }

    private void setData(EventListResponseApi response) {


//        Log.e(TAG, "setData: " + new Gson().toJson(response));

        if (response.getSuccess()) {


            eventList.clear();
            List<UpcommingEvent> todatList = response.getData().getTodayEvents();
            List<UpcommingEvent> upcommingList = response.getData().getUpcommingEvents();

            if (todatList != null && !todatList.isEmpty()) {
                eventList.addAll(todatList);
            }
            if (upcommingList != null && !upcommingList.isEmpty()) {
                eventList.addAll(upcommingList);
            }

            filterBaseOnDate(eventList);


//            if (!eventList.isEmpty()) {
////                showViewContent();
////                eventsAdapter.updateList(eventList);
//
//                UpcommingEvent upcommingEvent = new UpcommingEvent();
//                upcommingEvent.setEmptyView(true);
//                eventList.add(upcommingEvent);
//
//                eventsAdapter.updateList(eventList);
//
//            } else {
//
//                noevents.setVisibility(View.VISIBLE);
//                rv.setVisibility(View.GONE);
//
//            }


//            Log.e(TAG, "setData: size " + eventList.size() + "  " + eventsAdapter.getItemCount());


//            todayEventList = response.getData().getTodayEvents();
//            upcommingEventList = response.getData().getUpcommingEvents();
//
//            boolean isToday = false;
//            if (getArguments() != null) {
//                isToday = getArguments().getBoolean(isTodayEnable, true);
//            }
//
//
//            if (isToday) {
//
//                if (!todayEventList.isEmpty()) {
//                    showViewContent();
//                    eventsAdapter.updateList(todayEventList);
//                } else showViewEmpty();
//            } else {
//                if (!upcommingEventList.isEmpty()) {
//                    showViewContent();
//                    eventsAdapter.updateList(upcommingEventList);
//                } else showViewEmpty();
//            }
//
//
//        } else {
//
//            showViewEmpty();
        }

        showFullOrCalendarEvents();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.back)
    public void onViewClicked() {
    }

    @OnClick({R.id.calendar_go_back, R.id.calendar_forward})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.calendar_go_back:

                calendarView.goToPrevious();

                break;
            case R.id.calendar_forward:

                calendarView.goToNext();

                break;
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void OnItemClick(View view, UpcommingEvent upcommingEvent, int position) {

//        if (getActivity() instanceof DashBoardActivity) {
//
//            ((DashBoardActivity) getActivity()).callFragmentFromOutSide(EventDetailsFragment.getInstance(upcommingEvent.getId()));
//
//        }

        Intent i = EventDetailsActivity.getIntent(getContext(), upcommingEvent.getId());

        ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity());

        startActivity(i, transitionActivityOptions.toBundle());


    }

    @OnClick(R.id.showAllEvents)
    public void onShowAllEvents() {

        showFullOrCalendarEvents();

    }


    private void showFullOrCalendarEvents() {

        isFullEvents = !isFullEvents;

        if (isFullEvents) {

            todayDate.setText("All Events".toUpperCase());
            calendarView.setVisibility(View.GONE);
            calendarBackForwardView.setVisibility(View.GONE);

            if (!eventList.isEmpty()) {

                noevents.setVisibility(View.GONE);
                rv.setVisibility(View.VISIBLE);

                eventsAdapter.updateList(eventList);

            } else {

                noevents.setVisibility(View.VISIBLE);
                rv.setVisibility(View.GONE);

            }

            showAllEvents.setImageResource(R.drawable.ic_small_calendar);


//            showAllEvents.setBackgroundColor(Color.TRANSPARENT);
//            showAllEvents.setPadding(0, 0, 0, 0);
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//
//            params.height = getResources().getDimensionPixelSize(R.dimen._24sdp);
//            params.width = getResources().getDimensionPixelSize(R.dimen._24sdp);

//            showAllEvents.setLayoutParams(params);

        } else {
            calendarView.setVisibility(View.VISIBLE);
            calendarBackForwardView.setVisibility(View.VISIBLE);

            if (calendarDay1 != null) {
                showDataByDate(calendarDay1);
            } else {
                if (!eventList.isEmpty()) {
                    noevents.setVisibility(View.GONE);
                    rv.setVisibility(View.VISIBLE);
                    eventsAdapter.updateList(eventList);

                } else {

                    noevents.setVisibility(View.VISIBLE);
                    rv.setVisibility(View.GONE);

                }
            }

            showAllEvents.setImageResource(R.drawable.ic_view_list_black_24dp);


//            showAllEvents.setBackgroundColor(Color.RED);

//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//
//            params.height = getResources().getDimensionPixelSize(R.dimen._12sdp);
//            params.width = getResources().getDimensionPixelSize(R.dimen._12sdp);
//
//            showAllEvents.setLayoutParams(params);
//            showAllEvents.setPadding(10, 10, 10, 10);

        }

    }


    public class EventDecorator implements DayViewDecorator {

        private final int color;
        private final HashSet<CalendarDay> dates;

        public EventDecorator(int color, Collection<CalendarDay> dates) {
            this.color = color;
            this.dates = new HashSet<>(dates);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new DotSpan(8, color));
        }
    }
}
