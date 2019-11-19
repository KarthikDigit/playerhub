package com.playerhub.ui.dashboard.home.addevent;


import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.playerhub.R;
import com.playerhub.customview.CustomSpinnerEditText;
import com.playerhub.customview.CustomSpinnerInputLayout;
import com.playerhub.network.RetrofitAdapter;
import com.playerhub.network.response.EventTypesResponse;
import com.playerhub.network.response.TeamResponse;
import com.playerhub.preference.Preferences;
import com.playerhub.ui.base.BaseFragment;
import com.playerhub.utils.ProgressUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

//import com.google.android.libraries.places.compat.AutocompletePredictionBufferResponse; // New
//import com.google.android.libraries.places.compat.AutocompletePrediction; //New
//import com.google.android.libraries.places.compat.AutocompleteFilter; //New
//import com.google.android.libraries.places.compat.GeoDataClient; //New
//import com.google.android.libraries.places.compat.Place; //New
//import com.google.android.libraries.places.compat.Places; //New
//import com.google.android.libraries.places.compat.PlaceBufferResponse; //New

/**
 * A simple {@link Fragment} subclass.
 */
public class AddEventFragment extends BaseFragment {


    private static final String TAG = "AddEventFragment";

    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    protected CompositeDisposable disposable = new CompositeDisposable();
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.select_team)
    CustomSpinnerEditText selectTeam;
    @BindView(R.id.select_eventtype)
    CustomSpinnerEditText selectEventtype;
    @BindView(R.id.event_name)
    EditText eventName;
    @BindView(R.id.start_date)
    EditText startDate;
    @BindView(R.id.start_time)
    EditText startTime;
    @BindView(R.id.end_date)
    EditText endDate;
    @BindView(R.id.end_time)
    EditText endTime;
    @BindView(R.id.all_day)
    CheckBox allDay;
    @BindView(R.id.repeat_on)
    CheckBox repeatOn;
    @BindView(R.id.sun_day)
    CheckBox sunDay;
    @BindView(R.id.mun_day)
    CheckBox munDay;
    @BindView(R.id.tue_day)
    CheckBox tueDay;
    @BindView(R.id.wed_day)
    CheckBox wedDay;
    @BindView(R.id.thu_day)
    CheckBox thuDay;
    @BindView(R.id.fri_day)
    CheckBox friDay;
    @BindView(R.id.sat_day)
    CheckBox satDay;
    @BindView(R.id.repeat_on_layout)
    FlexboxLayout repeatOnLayout;
    @BindView(R.id.location)
    EditText location;
    @BindView(R.id.description)
    EditText description;
    @BindView(R.id.footerline)
    View footerline;
    @BindView(R.id.close)
    AppCompatButton close;
    @BindView(R.id.cancel)
    AppCompatButton cancel;
    @BindView(R.id.create_event)
    AppCompatButton createEvent;
    @BindView(R.id.footer)
    LinearLayout footer;
    Unbinder unbinder;
    @BindView(R.id.create_event_card)
    CardView createEventCard;
    @BindView(R.id.rootview)
    RelativeLayout rootview;

    private FetchTeamsAndEventTypes fetchTeamsAndEventTypes;

    public AddEventFragment() {
        // Required empty public constructor
    }

//    @NonNull
//    @Override
//    public Dialog onCreateDialog(final Bundle savedInstanceState) {
//
//
////        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
////        LayoutInflater inflater = getActivity().getLayoutInflater();
////        View view = inflater.inflate(R.layout.fragment_add_event, null);
////        builder.setView(view);
////        Dialog dialog = builder.create();
////        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
////
////        return dialog;
//
////        // the content
//        final RelativeLayout root = new RelativeLayout(getActivity());
//        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//
//
//        // creating the fullscreen dialog
//        final Dialog dialog = new Dialog(getActivity());
//
////        dialog.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
//        dialog.setTitle("Add Event");
//        dialog.setContentView(root);
//        dialog.setCancelable(false);
////        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//
//        return dialog;
//    }


    class FetchTeamsAndEventTypes {

        TeamResponse teamResponse;

        EventTypesResponse eventTypesResponse;

    }

    private DisposableObserver<FetchTeamsAndEventTypes> observer = new DisposableObserver<FetchTeamsAndEventTypes>() {
        @Override
        public void onNext(FetchTeamsAndEventTypes value) {

            ProgressUtils.hideProgress();
            fetchTeamsAndEventTypes = value;


        }

        @Override
        public void onError(Throwable e) {
            ProgressUtils.hideProgress();
        }

        @Override
        public void onComplete() {
            ProgressUtils.hideProgress();
        }
    };

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_add_event, container, false);
        unbinder = ButterKnife.bind(this, view);


        Observable<TeamResponse> observableTeam = RetrofitAdapter.getNetworkApiServiceClient().fetchAllTeams(Preferences.INSTANCE.getAuthendicate())
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        Observable<EventTypesResponse> observableEventsTypes = RetrofitAdapter.getNetworkApiServiceClient().fetchAllEventTypes(Preferences.INSTANCE.getAuthendicate())
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        ProgressUtils.showProgress(getContext(), "Loading");

        disposable.add(Observable.combineLatest(observableTeam, observableEventsTypes, new BiFunction<TeamResponse, EventTypesResponse, FetchTeamsAndEventTypes>() {
            @Override
            public FetchTeamsAndEventTypes apply(TeamResponse teamResponse, EventTypesResponse eventTypesResponse) throws Exception {

                FetchTeamsAndEventTypes fetchTeamsAndEventTypes = new FetchTeamsAndEventTypes();
                fetchTeamsAndEventTypes.teamResponse = teamResponse;
                fetchTeamsAndEventTypes.eventTypesResponse = eventTypesResponse;

                return fetchTeamsAndEventTypes;
            }
        }).subscribeWith(observer));


//        final String[] teamList = {"Demo Team ", "2001 Boys Pro", "2005 Boys Pro"};


        selectTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (fetchTeamsAndEventTypes != null) {

                    TeamResponse teamResponse = fetchTeamsAndEventTypes.teamResponse;

                    if (teamResponse != null && teamResponse.data != null && teamResponse.data.teams != null && !teamResponse.data.teams.isEmpty())
                        selectTeam.showPopUp(teamResponse.data.teams);
                    else showToast("There is no team");
                }


            }
        });

//        final String[] eventType = {"Tournament", "Trainning session", "Scrimmage", "Game", "Other"};

        selectEventtype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (fetchTeamsAndEventTypes != null) {

                    EventTypesResponse eventTypesResponse = fetchTeamsAndEventTypes.eventTypesResponse;

                    if (eventTypesResponse != null && eventTypesResponse.data != null && eventTypesResponse.data.eventTypes != null && !eventTypesResponse.data.eventTypes.isEmpty())
                        selectEventtype.showPopUp(eventTypesResponse.data.eventTypes);
                    else showToast("There is no event type");


                }

            }
        });


//        selectTeam.setItemSelectedListener(new CustomSpinnerEditText.ItemSelectedListener() {
//            @Override
//            public void onItemChanged(Object o) {
//                selectTeam.setText(o.toString() + " vs");
//            }
//        });

        selectEventtype.setItemSelectedListener(new CustomSpinnerEditText.ItemSelectedListener() {
            @Override
            public void onItemChanged(Object o) {
                String s = o.toString();

                String[] compare = {"Tournament", "Game"};

                if (Arrays.asList(compare).contains(s)) {

//                    selectedTeam.setVisibility(View.VISIBLE);
                    eventName.setHint("Opponent Team Name");
                    eventName.setText("");

                } else {
                    eventName.setHint("Event Name");
                    eventName.setText("");
//                    selectedTeam.setVisibility(View.GONE);
                }
            }
        });


        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                datePicker(startDate, endDate);
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                datePicker(endDate);
            }
        });


        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                timePicker(startTime, endTime);
            }
        });

        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                timePicker(endTime);

            }
        });


        allDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                startTime.setText("9:00 AM");
                endTime.setText("6:00 PM");

            }
        });


        repeatOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked) {

                    checkAWeek(true, true);

                } else {
                    repeatOnLayout.setVisibility(View.GONE);
                    repeatOn.setChecked(false);
                }


            }
        });


        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

// Set the fields to specify which types of place data to
// return after the user has made a selection.
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

// Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(getActivity());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

            }
        });


        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postNewEvents();

            }
        });

        createEventCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postNewEvents();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });


//
//        Transition transition = TransitionInflater.from(getActivity()).inflateTransition(R.transition.changebounds_with_arcmotion);
//        getActivity().getWindow().setSharedElementEnterTransition(transition);
//        transition.addListener(new Transition.TransitionListener() {
//            @Override
//            public void onTransitionStart(Transition transition) {
//
//            }
//
//            @Override
//            public void onTransitionEnd(Transition transition) {
//                animateRevealShow(rootview);
////                animateButtonsIn();
//
////                int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
////                int cy = viewRoot.getTop();
////                int finalRadius = Math.max(viewRoot.getWidth(), viewRoot.getHeight());
////
////                Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, 0, finalRadius);
////                viewRoot.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
////
////                anim.setDuration(1000);
////                anim.setInterpolator(new AccelerateInterpolator());
////                anim.start();
//
//            }
//
//            @Override
//            public void onTransitionCancel(Transition transition) {
//
//            }
//
//            @Override
//            public void onTransitionPause(Transition transition) {
//
//            }
//
//            @Override
//            public void onTransitionResume(Transition transition) {
//
//            }
//
//
//        });
////
//        TransitionManager.beginDelayedTransition(rootview, transition);


        return view;
    }
//
//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    private void animateRevealShow(View viewRoot) {
//
//        int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
//        int cy = (viewRoot.getTop() + viewRoot.getBottom()) / 2;
//        int finalRadius = Math.max(viewRoot.getWidth(), viewRoot.getHeight());
//
//        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, 0, finalRadius);
//        viewRoot.setVisibility(View.VISIBLE);
//        viewRoot.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
//
//        anim.setDuration(getResources().getInteger(R.integer.anim_duration_long));
//        anim.setInterpolator(new AccelerateInterpolator());
//        anim.start();
//
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    private void postNewEvents() {

        String team = selectTeam.getItem() != null ? selectTeam.getItem().toString() : null;
        String eventtype = selectEventtype.getItem() != null ? selectEventtype.getItem().toString() : null;

        TeamResponse.Data.Team team1 = (TeamResponse.Data.Team) selectTeam.getItem();
        EventTypesResponse.Data.EventType eventType1 = (EventTypesResponse.Data.EventType) selectEventtype.getItem();

        String eventN = eventName.getText().toString();

        String sd = startDate.getText().toString();
        String ed = endDate.getText().toString();

        String st = startTime.getText().toString();
        String et = endTime.getText().toString();

        String loc = location.getText().toString();
        String des = location.getText().toString();

        boolean isallday = allDay.isChecked();
        boolean isrepeat = repeatOn.isChecked();

        List<Integer> integers = new ArrayList<>();

        if (sunDay.isChecked()) integers.add(0);
        if (munDay.isChecked()) integers.add(1);
        if (tueDay.isChecked()) integers.add(2);
        if (wedDay.isChecked()) integers.add(3);
        if (thuDay.isChecked()) integers.add(4);
        if (friDay.isChecked()) integers.add(5);
        if (satDay.isChecked()) integers.add(6);


        if (team1 != null && eventType1 != null && !TextUtils.isEmpty(team) && !TextUtils.isEmpty(eventtype)
                && !TextUtils.isEmpty(eventN) && !TextUtils.isEmpty(sd)
                && !TextUtils.isEmpty(ed) && !TextUtils.isEmpty(st)
                && !TextUtils.isEmpty(et) && !TextUtils.isEmpty(loc)
                ) {

            String[] compare = {"Tournament", "Game"};

            Map<String, Object> parms = new HashMap<>();

            if (Arrays.asList(compare).contains(eventtype)) {
                parms.put("opp_team_name", eventN);
            } else {
                parms.put("event_name", eventN);
            }
            if (repeatOn.isChecked()) {
                parms.put("is_repeat", repeatOn.isChecked());
                parms.put("repeat_days", integers);
            }

            parms.put("team_id", team1.id);
            parms.put("event_type", eventType1.id);
            parms.put("event_location", loc);
            parms.put("event_description", des);
            parms.put("is_all_day", allDay.isChecked());
            parms.put("event_start_date", sd);
            parms.put("event_end_date", ed);
            parms.put("event_start_time", st);
            parms.put("event_end_time", et);

//            BasePostEvents postEvents = new PostEvents();
//
//            if (repeatOn.isChecked()) {
//
//                ((PostEvents) postEvents).setIs_repeat(repeatOn.isChecked());
//                ((PostEvents) postEvents).setRepeat_days(integers);
//            } else {
//                postEvents = new BasePostEvents();
//            }
//
//            postEvents.setTeam_id(team1.id);
//            postEvents.setEvent_description(des);
//            postEvents.setEvent_location(loc);
//            postEvents.setEvent_type(eventType1.id);
//
//
//            if (Arrays.asList(compare).contains(eventtype)) {
//                postEvents.setOpp_team_name(eventN);
//            } else {
//                postEvents.setEvent_name(eventN);
//            }
//
//
//            if (allDay.isChecked()) {
//                postEvents.setIs_all_day(allDay.isChecked());
//            }

//            postEvents.setEvent_start_date(sd);
//            postEvents.setEvent_end_date(ed);
//            postEvents.setEvent_start_time(st);
//            postEvents.setEvent_end_time(et);


            ProgressUtils.showProgress(getContext(), "Loading");

            RetrofitAdapter.getNetworkApiServiceClient().postEvents(Preferences.INSTANCE.getAuthendicate(), parms)
                    .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(String s) {

                            ProgressUtils.hideProgress();


                            Toast.makeText(getContext(), "You successfully posted a new event", Toast.LENGTH_SHORT).show();

//                            Log.e(TAG, "onNext: " + s);

                            getActivity().finish();

                        }

                        @Override
                        public void onError(Throwable e) {
                            ProgressUtils.hideProgress();
                            Log.e(TAG, "onError: " + e.getMessage());
                        }

                        @Override
                        public void onComplete() {
                            ProgressUtils.hideProgress();

                        }
                    });


        } else {

            Toast.makeText(getContext(), "Please fill all the fields ", Toast.LENGTH_SHORT).show();
        }


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());


                location.setText(place.getName());

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

    }

    private void checkAWeek(boolean isToast, boolean isEnable) {

        String st = startDate.getText().toString();
        String et = endDate.getText().toString();

        if (st != null && st.length() > 0 && et != null && et.length() > 0) {

            Date sdate = getDate(st);
            Date edate = getDate(et);

            if (sdate != null && edate != null) {

                long diff = differenceDays(sdate, edate);

                if (diff >= 6) {

                    if (isEnable)
                        repeatOnLayout.setVisibility(View.VISIBLE);

                } else {
                    if (isToast) showInfo();
                    repeatOnLayout.setVisibility(View.GONE);
                    repeatOn.setChecked(false);
                }

            } else {
                if (isToast) showInfo();
                repeatOnLayout.setVisibility(View.GONE);
                repeatOn.setChecked(false);
            }


        } else {
            if (isToast) showInfo();
            repeatOnLayout.setVisibility(View.GONE);
            repeatOn.setChecked(false);
        }

    }

    private void showInfo() {


        Toast.makeText(getContext(), "Please select atleast a week", Toast.LENGTH_SHORT).show();

    }

    private long differenceDays(Date startdate, Date enddate) {

//        long diff = enddate.getTime() - startdate.getTime();
//        long diffSeconds = diff / 1000 % 60;
//        long diffMinutes = diff / (60 * 1000) % 60;
//        long diffHours = diff / (60 * 60 * 1000);
        long diffInDays = TimeUnit.MILLISECONDS.toDays(enddate.getTime() - startdate.getTime());
        ;//(int) diff / (1000 * 60 * 60 * 24);

//        Toast.makeText(getContext(), "" + diffInDays, Toast.LENGTH_SHORT).show();

        return diffInDays;
    }

    private Date getDate(String d) {
        DateFormat format = new SimpleDateFormat("MM-dd-yyyy", Locale.ENGLISH);
        Date date = null;
        try {
            date = format.parse(d);
        } catch (ParseException e) {
//            e.printStackTrace();
        }

        return date;
    }

    private void datePicker(final EditText... editText) {

        final Calendar myCalendar = Calendar.getInstance();
//        myCalendar.add(Calendar.DATE, -1);
        final DatePickerDialog datePickerDialog = new DatePickerDialog(editText[0].getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "MM-dd-yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                editText[0].setText(sdf.format(myCalendar.getTime()));

                if (editText.length > 1) {
                    if (editText[1] != null)
                        editText[1].setText(sdf.format(myCalendar.getTime()));
                }

                checkAWeek(false, false);
            }
        }, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(myCalendar.getTimeInMillis());
        datePickerDialog.show();

    }


    private void timePicker(final EditText... editText) {


        // TODO Auto-generated method stub
//        Calendar mcurrentTime = Calendar.getInstance();
        int hour = 10;//mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = 0;//mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(editText[0].getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

//                String AM_PM;
//                if (selectedHour < 12) {
//                    AM_PM = "AM";
//                } else {
//                    AM_PM = "PM";
//                }

//                editText.setText(String.format("%d:%d %s", selectedHour, selectedMinute, AM_PM));

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                calendar.set(Calendar.MINUTE, selectedMinute);

                String st = getTime(calendar);
                editText[0].setText(st);


                if (editText.length > 1) {
                    calendar.add(Calendar.HOUR_OF_DAY, 1);
                    String et = getTime(calendar);
                    if (editText[1] != null)
                        editText[1].setText(et);
                }


            }
        }, hour, minute, false);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();

    }


    private String getTime(Calendar datetime) {

        String am_pm = "";

//        Calendar datetime = Calendar.getInstance();
//        datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
//        datetime.set(Calendar.MINUTE, minute);

        if (datetime.get(Calendar.AM_PM) == Calendar.AM)
            am_pm = "AM";
        else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
            am_pm = "PM";

        String strHrsToShow = (datetime.get(Calendar.HOUR) == 0) ? "12" : Integer.toString(datetime.get(Calendar.HOUR));


        return strHrsToShow + ":" + datetime.get(Calendar.MINUTE) + " " + am_pm;


    }

}
