package com.playerhub.ui.dashboard.profile;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Visibility;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.playerhub.R;
import com.playerhub.common.CallbackWrapper;
import com.playerhub.network.RetrofitAdapter;
import com.playerhub.network.response.KidInfoResponse;
import com.playerhub.network.response.ProfileDetails;
import com.playerhub.preference.Preferences;
import com.playerhub.ui.base.BaseActivity;
import com.playerhub.ui.base.MultiStateViewActivity;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import retrofit2.Response;

public class ProfileDetailsActivity extends MultiStateViewActivity {


    public static final String EXTRA_LOGO = "logo";

    @BindView(R.id.profile_image)
    CircleImageView profileImage;
    @BindView(R.id.fullImage)
    ImageView fullImage;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.email)
    TextView email;
    @BindView(R.id.phone_number)
    TextView phoneNumber;

    @BindView(R.id.actionBar)
    RelativeLayout mActionBar;

    @Override
    public int getLayoutByID() {
        return R.layout.activity_profile_details;
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
////        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
////        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setContentView(R.layout.activity_profile_details);
//        ButterKnife.bind(this);
//
//
//    }

    @Override
    protected void initViews() {
        showViewContent();


        fetchProfileDetails();
        setupWindowAnimation();

    }

    private void setupWindowAnimation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {


            Slide fade = new Slide();
//            fade.setMode(Visibility.MODE_IN);
            fade.setDuration(200);
            getWindow().setEnterTransition(fade);

            Explode fadeOut = new Explode();
//            fadeOut.setMode(Visibility.MODE_OUT);
            fadeOut.setDuration(100);

            getWindow().setReturnTransition(fadeOut);

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
        fetchProfileDetails();
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

        mActionBar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));

    }

    @Override
    public void onManuallyParseError(Response<?> response, boolean isToastMsg) {

    }


    private void fetchProfileDetails() {

        String logo = null;

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(EXTRA_LOGO)) {

            logo = intent.getStringExtra(EXTRA_LOGO);
        }

        if (logo != null) {

            Picasso.get().load(logo).error(R.drawable.avatar_mini).into(profileImage);
            Picasso.get().load(logo).error(R.drawable.avatar_mini).into(fullImage);
        }

        final String finalLogo = logo;
        RetrofitAdapter.getNetworkApiServiceClient().fetchUserDetails(Preferences.INSTANCE.getAuthendicate())

                .compose(MaterialProfileActivity.<ProfileDetails>apply())
                .subscribe(new MyCallBack<ProfileDetails>(this, this, true, false) {
                    @Override
                    public void onSuccess(ProfileDetails profileDetails) {

                        showViewContent();

                        if (profileDetails != null && profileDetails.getData() != null) {

                            setUpData(profileDetails, finalLogo);

                        } else {

                            showViewEmpty("There is no profile data ");

                        }
                    }
                });

//        Observable<ProfileDetails> observable = RetrofitAdapter.getNetworkApiServiceClient().fetchUserDetails(Preferences.INSTANCE.getAuthendicate());
//
//        setObservableAndObserver(observable, new CallbackWrapper<ProfileDetails>(this) {
//            @Override
//            protected void onSuccess(ProfileDetails profileDetails) {
//
//                setUpData(profileDetails);
//            }
//
//        });


    }

    private void setUpData(ProfileDetails details, String finalLogo) {

        if (details.getSuccess()) {

            ProfileDetails.Data data = details.getData();

            setText(name, data.getName());

            setText(email, data.getEmail());

            setText(phoneNumber, "(" + getString(data.getCountryCode()) + ") " + getString(data.getPhone()));

            if (finalLogo == null) {
                Picasso.get().load(data.getLogo()).error(R.drawable.avatar_mini).into(profileImage);
                Picasso.get().load(data.getLogo()).error(R.drawable.avatar_mini).into(fullImage);
            }

        } else {


            showToast("There is no details ");
        }


    }

    public void onBack(View view) {

        onBackPressed();
    }


}
