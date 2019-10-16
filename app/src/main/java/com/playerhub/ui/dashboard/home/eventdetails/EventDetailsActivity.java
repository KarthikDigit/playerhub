package com.playerhub.ui.dashboard.home.eventdetails;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Visibility;
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
import com.playerhub.trans.EventDetailsTransition;
import com.playerhub.ui.base.BaseActivity;
import com.playerhub.ui.dashboard.DashBoardActivity;
import com.playerhub.ui.dashboard.home.moreevent.MoreEventsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class EventDetailsActivity extends BaseActivity {

    private static final String KEY_EVENT_ID = "event_id";

//    SupportMapFragment mapFragment;
//    @BindView(R.id.title)
//    TextView title;
//    @BindView(R.id.event_name)
//    TextView eventName;
//    @BindView(R.id.team2)
//    TextView team2;
//    @BindView(R.id.team_name)
//    TextView teamName;
//    @BindView(R.id.event_time)
//    TextView eventTime;
//    @BindView(R.id.event_type)
//    TextView eventType;
//    @BindView(R.id.date_time)
//    TextView dateTime;
//    @BindView(R.id.event_repeat)
//    TextView eventRepeat;
//    @BindView(R.id.description)
//    TextView description;
//    @BindView(R.id.location)
//    TextView location;
//    @BindView(R.id.msg)
//    TextView msg;
//    @BindView(R.id.content)
//    LinearLayout content;
//    @BindView(R.id.description_layout)
//    LinearLayout descriptionLayout;
//    @BindView(R.id.location_layout)
//    LinearLayout locationLayout;
//
//
//    private GoogleMap map;

//
//    FrameLayout ;
//    @BindView(R.id.event_name)

    public static Intent getIntent(Context context, int id) {

        Intent intent = new Intent(context, EventDetailsActivity.class);
        intent.putExtra(KEY_EVENT_ID, id);

        return intent;

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_event_details_activity);
        ButterKnife.bind(this);

        int id = getIntent().getIntExtra(KEY_EVENT_ID, 0);


        Fragment fragment = EventDetailsFragment.getInstance(id);

        fragment.setSharedElementEnterTransition(new EventDetailsTransition());
        fragment.setEnterTransition(new Explode());
        fragment.setExitTransition(new Explode());
        fragment.setSharedElementReturnTransition(new EventDetailsTransition());

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, fragment)
                .commit();


//        mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//
//
//        callEventDetailsApi();

        setupWindowAnimation();
    }

    private void setupWindowAnimation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {


//            Fade fade = new Explode();
//            fade.setMode(Visibility.MODE_IN);
//            fade.setDuration(1000);
            getWindow().setEnterTransition(new Slide());

//            Fade fadeOut = new Fade();
//            fadeOut.setMode(Visibility.MODE_OUT);
//            fadeOut.setDuration(1000);
            getWindow().setReturnTransition(new Slide());

//            Slide slide = new Slide();
//            slide.setSlideEdge(Gravity.BOTTOM);
//            slide.setDuration(1000);
//            getWindow().setReturnTransition(slide);


//            Explode fade = new Explode();
//            fade.setDuration(1000);
//            getWindow().setEnterTransition(fade);

//            Slide slide = new Slide();
//            slide.setSlideEdge(Gravity.BOTTOM);
//            slide.setDuration(1000);
//            getWindow().setReturnTransition(slide);
        }

    }


//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//
//        map = googleMap;
//
//    }
//
//    public static Bitmap createDrawableFromView(Context context, View view, String title) {
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        ((Activity) context).getWindowManager().getDefaultDisplay()
//                .getMetrics(displayMetrics);
//        TextView bedNumberTextView = view.findViewById(R.id.bed_num_text_view);
//        bedNumberTextView.setText(title);
//        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
//                RelativeLayout.LayoutParams.WRAP_CONTENT));
//        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
//        view.layout(0, 0, displayMetrics.widthPixels,
//                displayMetrics.heightPixels);
//        view.buildDrawingCache();
//        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
//                view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
//
//        Canvas canvas = new Canvas(bitmap);
//        view.draw(canvas);
//
//        return bitmap;
//    }
//
//
//    private void callEventDetailsApi() {
//
//        int id = getIntent().getIntExtra(KEY_EVENT_ID, 0);
//
//
//        RetrofitAdapter.getNetworkApiServiceClient().fetchEventDetailsById(Preferences.INSTANCE.getAuthendicate(), id)
//                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new CallbackWrapper<EventDetailsApi>(this) {
//                    @Override
//                    protected void onSuccess(EventDetailsApi res) {
//
//                        if (res.getSuccess()) {
//
//                            EventDetailsApi.Data data = res.getData();
//
////                            title.setText(getString(data.getEventType()));
//
//                            eventName.setText(getString(data.getEventName()));
//
//                            teamName.setText(getString(data.getTeamName()));
//
//                            if (getString(data.getEventRepeat()).length() > 0) {
//
//                                eventRepeat.setText(getString(data.getEventRepeat()));
//                                eventRepeat.setVisibility(View.VISIBLE);
//
//                            } else {
//
//                                eventRepeat.setVisibility(View.GONE);
//
//                            }
//
//                            if (!TextUtils.isEmpty(data.getDescription())) {
//                                descriptionLayout.setVisibility(View.VISIBLE);
//                                description.setText(getString(data.getDescription()));
//                            } else {
//                                descriptionLayout.setVisibility(View.GONE);
//                            }
//
//
//                            String timinging = data.getStartDate();
//
//                            dateTime.setText(timinging);
//
//                            eventTime.setText(String.format("%s - %s", data.getStartTime(), data.getEndTime()));
//
//
//                            if (!TextUtils.isEmpty(data.getLocation())) {
//                                locationLayout.setVisibility(View.VISIBLE);
//                                location.setText(getString(data.getLocation()));
//                            } else {
//                                locationLayout.setVisibility(View.GONE);
//                            }
//
//
//                            GeocodingLocation.getAddressFromLocation(data.getLocation(),
//                                    getApplicationContext(), new GeocoderHandler(data.getLocation()));
//
//
//                        } else {
//
//                            showToast("There is no event details");
//                        }
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
//
//
//    }
//
//
////    private String getString(String s) {
////
////        return s != null && s.length() > 0 ? s + " " : "";
////    }
//
//    private static final String TAG = "EventDetailsActivity";
//
//    private class GeocoderHandler extends Handler {
//
//        private String name;
//
//        private GeocoderHandler(String name) {
//            this.name = name;
//        }
//
//
//        @Override
//        public void handleMessage(Message message) {
//
//            switch (message.what) {
//                case 1:
//                    Bundle bundle = message.getData();
//                    Double lat = bundle.getDouble("lat");
//                    Double lng = bundle.getDouble("lng");
//                    LatLng latLng = new LatLng(lat, lng);
////                            loadMap(Double.parseDouble(data.getLatitude()), Double.parseDouble(data.getLongitude()), data.getName());
//                    loadMap(latLng.latitude, latLng.longitude, name);
//
//                    break;
//                default:
//            }
//
//        }
//    }
//
//    private void loadMap(double lat, double lng, String title) {
//
//        if (map != null) {
//
//            LatLng latLng = new LatLng(lat, lng);
//
////            View markerView = ((LayoutInflater) this
////                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
////                    .inflate(R.layout.map_circle_text, null);
//
////            map.addMarker(new MarkerOptions()
////                    .position(latLng)
////                    .title(title)
//////                    .snippet(getString(R.string.barca_ny_04_pro))
////                    .icon(BitmapDescriptorFactory
////                            .fromBitmap(createDrawableFromView(
////                                    this,
////                                    markerView, title))));
//
//            map.addMarker(new MarkerOptions()
//                    .position(latLng)
//                    .title(title));
//
//            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
//        }
//
//    }
//
////    public static MarkerOptions createMarker(Context context, LatLng point, int bedroomCount) {
////        MarkerOptions marker = new MarkerOptions();
////        marker.position(point);
////        int px = context.getResources().getDimensionPixelSize(R.dimen.map_marker_diameter);
////        View markerView = LayoutInflater.from(context).inflate(R.layout.map_circle_text, null);
////        markerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
////        markerView.layout(0, 0, px, px);
////        markerView.buildDrawingCache();
////        TextView bedNumberTextView = (TextView) markerView.findViewById(R.id.bed_num_text_view);
////        Bitmap mDotMarkerBitmap = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888);
////        Canvas canvas = new Canvas(mDotMarkerBitmap);
//////        bedNumberTextView.setText(bedroomCount);
////        markerView.draw(canvas);
////        marker.icon(BitmapDescriptorFactory.fromBitmap(mDotMarkerBitmap));
////        return marker;
////    }


//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//
//        finish();
//    }

    public void onBack(View view) {


        onBackPressed();

    }
//
//
//    @Override
//    public void onBackPressed() {
////        super.onBackPressed();
//
//
//
//        Intent intent = new Intent(EventDetailsActivity.this, DashBoardActivity.class);
//        startActivity(intent);
//
//        finish();
//    }
}
