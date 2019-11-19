package com.playerhub.ui.dashboard.settings;


import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.playerhub.R;
import com.playerhub.network.RetrofitAdapter;
import com.playerhub.network.response.Coach;
import com.playerhub.network.response.Kid;
import com.playerhub.network.response.KidsAndCoaches;
import com.playerhub.preference.Preferences;
import com.playerhub.ui.base.BaseFragment;
import com.playerhub.ui.base.OnItemClickListener;
import com.playerhub.ui.dashboard.addsubaccount.AddSubAccountActivity;
import com.playerhub.ui.dashboard.changecreditcard.ChangeCreditCardActivity;
import com.playerhub.ui.dashboard.changecreditcard.ChangeCreditCardWebViewActivity;
import com.playerhub.ui.dashboard.contact.ContactActivity;
import com.playerhub.ui.dashboard.home.HomeFragment;
import com.playerhub.ui.dashboard.home.ParentChild;
import com.playerhub.ui.dashboard.home.ParentChildPagerAdapter;
import com.playerhub.ui.dashboard.myreceipt.MyReceiptActivity;
import com.playerhub.ui.dashboard.profile.PlayerProfileWebViewActivity;
import com.playerhub.ui.dashboard.profile.ProfileDetailsActivity;
import com.playerhub.ui.dashboard.stats.StatsWebViewActivity;
import com.playerhub.ui.login.LoginActivity;
import com.playerhub.utils.Utils;
import com.suke.widget.SwitchButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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


    @BindView(R.id.create_creditcard_view)
    View create_creditcard_view;
    @BindView(R.id.create_creditcard)
    LinearLayout create_creditcard;

    @BindView(R.id.playerprofileListView)
    RecyclerView mPlayerProfileListView;
    private PlayerProfileListAdapter playerProfileListAdapter;

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

        playerProfileListAdapter = new PlayerProfileListAdapter(getContext(), new ArrayList<Kid>());

        mPlayerProfileListView.setLayoutManager(new LinearLayoutManager(getContext()));

        mPlayerProfileListView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        mPlayerProfileListView.setAdapter(playerProfileListAdapter);


        try {

            boolean b = Utils.check(Preferences.INSTANCE.getUserType(), "coach");

            if (b) {
                create_creditcard_view.setVisibility(View.GONE);
                create_creditcard.setVisibility(View.GONE);
                mPlayerProfileListView.setVisibility(View.GONE);
            } else {

                mPlayerProfileListView.setVisibility(View.VISIBLE);
                getKids();

            }

        } catch (NullPointerException e) {

        }


        return view;
    }


    private void getKids() {

        RetrofitAdapter.getNetworkApiServiceClient().fetchKids(Preferences.INSTANCE.getAuthendicate())
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<KidsAndCoaches>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(KidsAndCoaches value) {

                        try {
                            if (value != null && value.getData() != null) {

                                List<Kid> kid = value.getData().getKids();

                                if (kid != null && !kid.isEmpty()) {

                                    playerProfileListAdapter.updateList(kid);

                                }

                            }

                        } catch (Exception e) {

                            Log.e(TAG, "Exception: " + e.getMessage());
                        }


                    }

                    @Override
                    public void onError(Throwable e) {

                        Log.e(TAG, "onNextkids: " + e.getMessage());

                    }

                    @Override
                    public void onComplete() {

                    }
                });


        playerProfileListAdapter.setOnItemClickListener(new OnItemClickListener<Kid>() {
            @Override
            public void onItemClicked(View view, Kid kid, int position) {


                PlayerProfileWebViewActivity.startActivity(getContext(), kid.getId(), kid.getFirstname() + "'s Profile");


            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.logout, R.id.profile, R.id.create_creditcard, R.id.contact, R.id.addsubaccount, R.id.myreceipt, R.id.stats})
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

//                Intent i1 = new Intent(getContext(), ChangeCreditCardActivity.class);
                Intent i1 = new Intent(getContext(), ChangeCreditCardWebViewActivity.class);


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

            case R.id.myreceipt:

//                startActivity(new Intent(getContext(), ProfileDetailsActivity.class));

                Intent i4 = new Intent(getContext(), MyReceiptActivity.class);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity());//ActivityOptions.makeSceneTransitionAnimation(getActivity(), profileImg, transitionName);

                    startActivity(i4, transitionActivityOptions.toBundle());
                } else {

                    startActivity(i4);

                }

                break;

            case R.id.stats:

//                startActivity(new Intent(getContext(), ProfileDetailsActivity.class));

                Intent i7 = new Intent(getContext(), StatsWebViewActivity.class);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity());//ActivityOptions.makeSceneTransitionAnimation(getActivity(), profileImg, transitionName);

                    startActivity(i7, transitionActivityOptions.toBundle());
                } else {

                    startActivity(i7);

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
