package com.playerhub.ui.dashboard.home.paidevent;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.transition.Slide;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.playerhub.R;
import com.playerhub.ui.base.MultiStateViewActivity;
import com.playerhub.utils.ImageUtility;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Response;

public class PaidEventDetailsActivity extends MultiStateViewActivity {
    private static final String KEY_EVENT_ID = "event_id";
    @BindView(R.id.event_name)
    TextView eventName;
    @BindView(R.id.price)
    TextView price;
    @BindView(R.id.date_time)
    TextView dateTime;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.location)
    TextView location;
    @BindView(R.id.location_layout)
    LinearLayout locationLayout;

    @BindView(R.id.content)
    LinearLayout content;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.bottomLayout)
    Button bottomLayout;

    public static Intent getIntent(Context context, int id) {

        Intent intent = new Intent(context, PaidEventDetailsActivity.class);
        intent.putExtra(KEY_EVENT_ID, id);

        return intent;

    }

    @Override
    public int getLayoutByID() {
        return R.layout.activity_paid_event_details;
    }


    @Override
    protected void initViews() {


        setupWindowAnimation();
        int id = getIntent().getIntExtra(KEY_EVENT_ID, 0);

        onRetryOrCallApi();

        ImageUtility.loadImage( image,  "https://www.playerhub.io/image/event/180/180/55d21b111ecd1bc1e6fe33b190492dc535_1567057692.jpg");

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

    @Override
    protected void onRetryOrCallApi() {

    }

    @Override
    public void onManuallyParseError(Response<?> response, boolean isToastMsg) {

    }


    @OnClick(R.id.back)
    public void onViewClicked() {

        super.onBackPressed();

    }

    @OnClick(R.id.bottomLayout)
    public void onViewRegistraionClicked() {

        String url = "https://www.playerhub.io/event/35/payment";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);

    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        // TODO: add setContentView(...) invocation
//        ButterKnife.bind(this);
//    }
}
