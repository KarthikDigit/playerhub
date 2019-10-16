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
import android.widget.RelativeLayout;

import com.playerhub.R;
import com.playerhub.preference.Preferences;
import com.playerhub.ui.base.BaseFragment;
import com.playerhub.ui.dashboard.profile.ProfileDetailsActivity;
import com.playerhub.ui.login.LoginActivity;

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
    CircleImageView profileImg;
    @BindView(R.id.downloadimageSwitch)
    SwitchCompat downloadimageSwitch;

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

        downloadimageSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

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

    @OnClick({R.id.logout, R.id.profile})
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

        }


    }
}
