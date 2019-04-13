package com.playerhub.ui.dashboard.home.eventdetails;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.playerhub.R;
import com.playerhub.common.CallbackWrapper;
import com.playerhub.network.RetrofitAdapter;
import com.playerhub.network.response.EventDetailsApi;
import com.playerhub.preference.Preferences;
import com.playerhub.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class EventDetailsActivity extends BaseActivity implements OnMapReadyCallback {

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
    @BindView(R.id.event_repeat)
    TextView eventRepeat;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.location)
    TextView location;
    @BindView(R.id.msg)
    TextView msg;
    @BindView(R.id.content)
    LinearLayout content;


    private GoogleMap map;

    public static Intent getIntent(Context context, int id) {

        Intent intent = new Intent(context, EventDetailsActivity.class);
        intent.putExtra(KEY_EVENT_ID, id);

        return intent;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_event_details);
        ButterKnife.bind(this);


        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        callEventDetailsApi();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
//        LatLng latLng = new LatLng(37.4233438, -122.0728817);
//
//        View markerView = ((LayoutInflater) this
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
//                .inflate(R.layout.map_circle_text, null);
//
//        googleMap.addMarker(new MarkerOptions()
//                .position(latLng)
//                .title(getString(R.string.barca_ny_04_pro))
//                .snippet(getString(R.string.barca_ny_04_pro))
//                .icon(BitmapDescriptorFactory
//                        .fromBitmap(createDrawableFromView(
//                                this,
//                                markerView))));

//        googleMap.addMarker(createMarker(this, latLng, 5));

//        googleMap.addMarker(new MarkerOptions()
//                .position(new LatLng(37.4629101, -122.2449094))
//                .title("Facebook")
//                .snippet("Facebook HQ: Menlo Park"));
//
//        googleMap.addMarker(new MarkerOptions()
//                .position(new LatLng(37.3092293, -122.1136845))
//                .title("Apple"));

//        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
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

        int id = getIntent().getIntExtra(KEY_EVENT_ID, 0);


        RetrofitAdapter.getNetworkApiServiceClient().fetchEventDetailsById(Preferences.INSTANCE.getAuthendicate(), id)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CallbackWrapper<EventDetailsApi>(this) {
                    @Override
                    protected void onSuccess(EventDetailsApi res) {

                        if (res.getSuccess()) {

                            EventDetailsApi.Data data = res.getData();

//                            title.setText(getString(data.getEventType()));

                            eventName.setText(getString(data.getEventName()));

                            teamName.setText(getString(data.getTeamName()));

                            eventRepeat.setText(getString(data.getEventRepeat()));

                            description.setText(getString(data.getDescription()));


//                            String des = "All the players to be assembled on time at the " + data.getLocation();
//
//                            mdes.setText(des);


//                            Date date = Utils.convertStringToDate(data.getStartDate(), "dd-MM-yyyy");
//                            Date enddate = Utils.convertStringToDate(data.getStartDate(), "dd-MM-yyyy");
//
//                            String d = Utils.convertDateToString(date, "EEE, MMM dd, yyyy");
//
//                            Date startTime = Utils.convertStringToDate(data.getStartTime(), "hh:mm:ss");
//                            Date endTime = Utils.convertStringToDate(data.getEndTime(), "hh:mm:ss");
////
//                            String st = Utils.convertDateToString(startTime, "hh:mm a");
//                            String et = Utils.convertDateToString(endTime, "hh:mm a");
//
//                            String timinging = d + " " + st + " - " + et;

                            String timinging = data.getStartDate() + " " + data.getStartTime() + " - " + data.getEndTime();

                            dateTime.setText(timinging);

//
//                            String repeat = "Repeat every " + Utils.convertDateToString(date, "EEEE") + " untill " + Utils.convertDateToString(enddate, "MMMM dd, yyyy");
//
//                            mdescription.setText(repeat);


                            location.setText(getString(data.getLocation()));

                            GeocodingLocation.getAddressFromLocation(data.getLocation(),
                                    getApplicationContext(), new GeocoderHandler(data.getLocation()));

//                            LatLng latLng = new LatLng(37.4233438, -122.0728817);
////                            loadMap(Double.parseDouble(data.getLatitude()), Double.parseDouble(data.getLongitude()), data.getName());
//                            loadMap(latLng.latitude, latLng.longitude, data.getName());


                        } else {

                            showToast("There is no event details");
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);

                        content.setVisibility(View.GONE);
                        msg.setVisibility(View.VISIBLE);


                    }
                });


    }


//    private String getString(String s) {
//
//        return s != null && s.length() > 0 ? s + " " : "";
//    }

    private static final String TAG = "EventDetailsActivity";

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

//            View markerView = ((LayoutInflater) this
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
//                    .inflate(R.layout.map_circle_text, null);

//            map.addMarker(new MarkerOptions()
//                    .position(latLng)
//                    .title(title)
////                    .snippet(getString(R.string.barca_ny_04_pro))
//                    .icon(BitmapDescriptorFactory
//                            .fromBitmap(createDrawableFromView(
//                                    this,
//                                    markerView, title))));

            map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(title));

            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        }

    }

//    public static MarkerOptions createMarker(Context context, LatLng point, int bedroomCount) {
//        MarkerOptions marker = new MarkerOptions();
//        marker.position(point);
//        int px = context.getResources().getDimensionPixelSize(R.dimen.map_marker_diameter);
//        View markerView = LayoutInflater.from(context).inflate(R.layout.map_circle_text, null);
//        markerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        markerView.layout(0, 0, px, px);
//        markerView.buildDrawingCache();
//        TextView bedNumberTextView = (TextView) markerView.findViewById(R.id.bed_num_text_view);
//        Bitmap mDotMarkerBitmap = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(mDotMarkerBitmap);
////        bedNumberTextView.setText(bedroomCount);
//        markerView.draw(canvas);
//        marker.icon(BitmapDescriptorFactory.fromBitmap(mDotMarkerBitmap));
//        return marker;
//    }

    public void onBack(View view) {


        onBackPressed();

    }
}
