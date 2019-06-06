package com.playerhub.ui.dashboard.profile;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.playerhub.R;
import com.playerhub.common.CallbackWrapper;
import com.playerhub.network.RetrofitAdapter;
import com.playerhub.network.response.ProfileDetails;
import com.playerhub.preference.Preferences;
import com.playerhub.ui.base.BaseActivity;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;

public class ProfileDetailsActivity extends BaseActivity {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile_details);
        ButterKnife.bind(this);
        fetchProfileDetails();

    }


    private void fetchProfileDetails() {

        Observable<ProfileDetails> observable = RetrofitAdapter.getNetworkApiServiceClient().fetchUserDetails(Preferences.INSTANCE.getAuthendicate());

        setObservableAndObserver(observable, new CallbackWrapper<ProfileDetails>(this) {
            @Override
            protected void onSuccess(ProfileDetails profileDetails) {

                setUpData(profileDetails);
            }

        });


    }

    private void setUpData(ProfileDetails details) {

        if (details.getSuccess()) {

            ProfileDetails.Data data = details.getData();

            setText(name, data.getName());

            setText(email, data.getEmail());

            setText(phoneNumber, "(" + getString(data.getCountryCode()) + ") " + getString(data.getPhone()));

            Picasso.get().load(data.getLogo()).error(R.drawable.avatar_mini).into(profileImage);
            Picasso.get().load(data.getLogo()).error(R.drawable.avatar_mini).into(fullImage);

        } else {


            showToast("There is no details ");
        }


    }

    public void onBack(View view) {

        onBackPressed();
    }


}
