package com.playerhub.ui.dashboard.home.eventdetails;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.playerhub.R;
import com.playerhub.network.RetrofitAdapter;
import com.playerhub.network.response.EventDetailsApi;
import com.playerhub.network.service.KidsRequest;
import com.playerhub.preference.Preferences;
import com.playerhub.ui.base.MultiStateViewFragment;
import com.playerhub.ui.dashboard.DashBoardActivity;
import com.playerhub.ui.dashboard.profile.MyCallBack;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class EventDetailsFragment extends MultiStateViewFragment implements OnMapReadyCallback, KidsViewAdapter.OnItemChangeListener {


    private static final String KEY_EVENT_ID = "event_id";

    SupportMapFragment mapFragment;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.event_name)
    TextView eventName;
    @BindView(R.id.team2)
    TextView team2;
    @BindView(R.id.team_name)
    TextView teamName;
    @BindView(R.id.event_type)
    TextView eventType;
    @BindView(R.id.date_time)
    TextView dateTime;
    @BindView(R.id.event_time)
    TextView eventTime;
    @BindView(R.id.event_repeat)
    TextView eventRepeat;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.location)
    TextView location;
    @BindView(R.id.content)
    LinearLayout content;
    @BindView(R.id.description_layout)
    LinearLayout descriptionLayout;
    @BindView(R.id.location_layout)
    LinearLayout locationLayout;

    @BindView(R.id.actionBar)
    RelativeLayout mActionBar;
    Unbinder unbinder;
    @BindView(R.id.share_button)
    ImageView shareButton;
    @BindView(R.id.kidsView)
    RecyclerView kidsView;

    private KidsViewAdapter kidsViewAdapter;

    private GoogleMap map;

    private long event_id = 0;


    public static EventDetailsFragment getInstance(int id) {

        EventDetailsFragment fragment = new EventDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_EVENT_ID, id);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getLayoutByID() {
        return R.layout.activity_event_details;
    }


    @Override
    protected void initViews() {

        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }


        kidsView.setLayoutManager(new LinearLayoutManager(getContext()));

        kidsViewAdapter = new KidsViewAdapter(getContext(), new ArrayList<EventDetailsApi.Data.Kid>(), this);

        kidsView.setAdapter(kidsViewAdapter);

        callEventDetailsApi();

    }


    @Override
    protected void onRetryOrCallApi() {

        callEventDetailsApi();
    }


    @Override
    public void showViewError() {
        super.showViewError();
        setActionBarColor();
    }

    @Override
    public void showViewError(String errorMsg) {
        super.showViewError(errorMsg);
        setActionBarColor();
    }

    @Override
    public void showViewContent() {
        super.showViewContent();
        mActionBar.setBackgroundColor(Color.TRANSPARENT);

    }

    private void setActionBarColor() {

        mActionBar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));

    }

    @Override
    public void onManuallyParseError(Response<?> response, boolean isToastMsg) {

        showToast("Something went wrong");
    }


    @OnClick(R.id.back)
    public void onBackClick(View view) {

        if (getActivity() instanceof DashBoardActivity || getActivity() instanceof EventDetailsActivity) {

            getActivity().onBackPressed();
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }


    public static Bitmap createDrawableFromView(Context context, View view, String title) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(displayMetrics);
        TextView bedNumberTextView = view.findViewById(R.id.bed_num_text_view);
        bedNumberTextView.setText(title);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels,
                displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);


        return bitmap;
    }


    private void callEventDetailsApi() {

        int id = 0;

        if (getArguments() != null) {
            id = getArguments().getInt(KEY_EVENT_ID, 0);
        }


        RetrofitAdapter.getNetworkApiServiceClient().fetchEventDetailsById(Preferences.INSTANCE.getAuthendicate(), id)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyCallBack<EventDetailsApi>(getContext(), this, true, false) {
                    @Override
                    public void onSuccess(EventDetailsApi res) {


                        if (res.getSuccess()) {

                            EventDetailsApi.Data data = res.getData();

                            event_id = data.getId();

                            if (data.getKids() != null) {
                                kidsViewAdapter.update(data.getKids());
                            }

                            Log.e(TAG, "Event Details onSuccess: " + new Gson().toJson(data));

//                            title.setText(getString(data.getEventType()));

                            eventName.setText(getString(data.getEventName()));

                            teamName.setText(getString(data.getTeamName()));

                            if (getString(data.getEventRepeat()).length() > 0) {

                                eventRepeat.setText(getString(data.getEventRepeat()));
                                eventRepeat.setVisibility(View.VISIBLE);

                            } else {

                                eventRepeat.setVisibility(View.GONE);

                            }

                            if (!TextUtils.isEmpty(data.getDescription())) {
                                descriptionLayout.setVisibility(View.VISIBLE);
                                description.setText(getString(data.getDescription()));
                            } else {
                                descriptionLayout.setVisibility(View.GONE);
                            }


                            String timinging = data.getStartDate();

                            dateTime.setText(timinging);

                            eventTime.setText(String.format("%s - %s", data.getStartTime(), data.getEndTime()));


                            if (!TextUtils.isEmpty(data.getLocation())) {
                                locationLayout.setVisibility(View.VISIBLE);
                                location.setText(getString(data.getLocation()));
                            } else {
                                locationLayout.setVisibility(View.GONE);
                            }


                            GeocodingLocation.getAddressFromLocation(data.getLocation(),
                                    getContext(), new GeocoderHandler(data.getLocation()));

                            showViewContent();

                        } else {

                            showViewEmpty("There is no event details");
                        }


                    }
                });


//        RetrofitAdapter.getNetworkApiServiceClient().fetchEventDetailsById(Preferences.INSTANCE.getAuthendicate(), id)
//                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new CallbackWrapper<EventDetailsApi>(getContext()) {
//                    @Override
//                    protected void onSuccess(EventDetailsApi res) {
//
//
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        super.onError(e);
//
//                        content.setVisibility(View.GONE);
//                        msg.setVisibility(View.VISIBLE);
//
//
//                    }
//                });


    }


    private static final String TAG = "EventDetailsActivity";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.share_button)
    public void onShareEvent() {


        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, "Playerhub Event");
        share.putExtra(Intent.EXTRA_TEXT, "https://www.playerhub.io/");
        startActivity(Intent.createChooser(share, "Share link!"));

    }

    @Override
    public void onItemChange(EventDetailsApi.Data.Kid kid, String value) {

        postKidsAttend(kid, value);

//        Toast.makeText(getContext(), "" + value, Toast.LENGTH_SHORT).show();

    }


    private void postKidsAttend(EventDetailsApi.Data.Kid kid, String value) {


        if (!(event_id > 0)) {

            return;

        }


        KidsRequest kidsRequest = new KidsRequest();

        kidsRequest.setEvent_id(event_id);
        kidsRequest.setKid_id(kid.getId());

        if (value.toLowerCase().equalsIgnoreCase("yes")) {

            kidsRequest.setType(1);

        } else {
            kidsRequest.setType(0);
        }


        RetrofitAdapter.getNetworkApiServiceClient().postKidsEvent(Preferences.INSTANCE.getAuthendicate(), kidsRequest)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyCallBack<String>(getContext(), this, true, false) {

                    @Override
                    public void onSuccess(String s) {


                        Log.e(TAG, "onSuccess: " + s);

                    }
                });
    }

    private class GeocoderHandler extends Handler {

        private String name;

        private GeocoderHandler(String name) {
            this.name = name;
        }


        @Override
        public void handleMessage(Message message) {

            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    Double lat = bundle.getDouble("lat");
                    Double lng = bundle.getDouble("lng");
                    LatLng latLng = new LatLng(lat, lng);
//                            loadMap(Double.parseDouble(data.getLatitude()), Double.parseDouble(data.getLongitude()), data.getName());
                    loadMap(latLng.latitude, latLng.longitude, name);

                    break;
                default:
            }

        }
    }

    private void loadMap(double lat, double lng, String title) {

        if (map != null) {

            LatLng latLng = new LatLng(lat, lng);

            map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(title));

            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        }

    }
}
