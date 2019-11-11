package com.playerhub.ui.dashboard.settings;


import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.playerhub.R;
import com.playerhub.preference.Preferences;
import com.playerhub.ui.base.BaseFragment;
import com.playerhub.ui.dashboard.addsubaccount.AddSubAccountActivity;
import com.playerhub.ui.dashboard.changecreditcard.ChangeCreditCardActivity;
import com.playerhub.ui.dashboard.contact.ContactActivity;
import com.playerhub.ui.dashboard.profile.ProfileDetailsActivity;
import com.playerhub.ui.login.LoginActivity;
import com.suke.widget.SwitchButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends BaseFragment {


    @BindView(R.id.logout)
    RelativeLayout logout;
    Unbinder unbinder;
    @BindView(R.id.profile_img)
    ImageView profileImg;
    @BindView(R.id.downloadimageSwitch)
    SwitchButton downloadimageSwitch;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        unbinder = ButterKnife.bind(this, view);


        downloadimageSwitch.setChecked(Preferences.INSTANCE.getAutoImageDownload());


        downloadimageSwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton buttonView, boolean isChecked) {

                Preferences.INSTANCE.putAutoImageDownload(isChecked);
            }
        });


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.logout, R.id.profile, R.id.create_creditcard, R.id.contact, R.id.addsubaccount})
    public void onViewClicked(View view) {

        switch (view.getId()) {


            case R.id.logout:

                Preferences.INSTANCE.clearPreference();

                startActivity(new Intent(getContext(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

                break;

            case R.id.profile:

//                startActivity(new Intent(getContext(), ProfileDetailsActivity.class));

                Intent i = new Intent(getContext(), ProfileDetailsActivity.class);

//            View sharedView = blueIconImageView;
                String transitionName = getString(R.string.transition_image);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity());//ActivityOptions.makeSceneTransitionAnimation(getActivity(), profileImg, transitionName);

                    startActivity(i, transitionActivityOptions.toBundle());
                } else {

                    startActivity(i);

                }

                break;

            case R.id.create_creditcard:

//                startActivity(new Intent(getContext(), ProfileDetailsActivity.class));

                Intent i1 = new Intent(getContext(), ChangeCreditCardActivity.class);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity());//ActivityOptions.makeSceneTransitionAnimation(getActivity(), profileImg, transitionName);

                    startActivity(i1, transitionActivityOptions.toBundle());
                } else {

                    startActivity(i1);

                }

                break;

            case R.id.addsubaccount:

//                startActivity(new Intent(getContext(), ProfileDetailsActivity.class));

                Intent i2 = new Intent(getContext(), AddSubAccountActivity.class);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity());//ActivityOptions.makeSceneTransitionAnimation(getActivity(), profileImg, transitionName);

                    startActivity(i2, transitionActivityOptions.toBundle());
                } else {

                    startActivity(i2);

                }

                break;

            case R.id.contact:

//                startActivity(new Intent(getContext(), ProfileDetailsActivity.class));

                Intent i3 = new Intent(getContext(), ContactActivity.class);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity());//ActivityOptions.makeSceneTransitionAnimation(getActivity(), profileImg, transitionName);

                    startActivity(i3, transitionActivityOptions.toBundle());
                } else {

                    startActivity(i3);

                }

                break;

        }


    }
}
