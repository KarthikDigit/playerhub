package com.playerhub.ui.dashboard.home;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.transition.ChangeBounds;
import android.transition.Slide;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ag.floatingactionmenu.OptionsFabLayout;
import com.google.gson.Gson;
import com.playerhub.R;
import com.playerhub.common.ConnectivityHelper;
import com.playerhub.common.OnPageChangeListener;
import com.playerhub.network.RetrofitAdapter;
import com.playerhub.network.response.AnnouncementApi;
import com.playerhub.network.response.Coach;
import com.playerhub.network.response.Kid;
import com.playerhub.network.response.KidsAndCoaches;
import com.playerhub.preference.Preferences;
import com.playerhub.trans.EventDetailsTransition;
import com.playerhub.ui.base.BaseNetworkCheck;
import com.playerhub.ui.dashboard.DashBoardActivity;
import com.playerhub.ui.dashboard.home.addevent.AddEventActivity;
import com.playerhub.ui.dashboard.home.announcement.AnnouncementDialogFragment;
import com.playerhub.ui.dashboard.home.announcement.MoreAnnouncementFragment;
import com.playerhub.ui.dashboard.home.announcement.PostAnnouncementFragment;
import com.playerhub.ui.dashboard.notification.NotificationActivity;
import com.playerhub.ui.dashboard.profile.CoachProfileFragment;
import com.playerhub.ui.dashboard.profile.MaterialProfileActivity;
import com.playerhub.ui.dashboard.profile.ProfileDetailsActivity;
import com.playerhub.utils.AnimUtils;
import com.rd.PageIndicatorView;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseNetworkCheck implements ParentChildPagerAdapter.OnItemClicklistener, CardPagerAdapter.OnItemClickListener {

    @BindView(R.id.noti_img)
    ConstraintLayout notiImg;
    @BindView(R.id.textView2)
    TextView notificationCount;
    Unbinder unbinder;

    @BindView(R.id.fullImage)
    ImageView mFullImage;

    @BindView(R.id.fab_l)
    OptionsFabLayout mFloatingMenu;

    @BindView(R.id.viewPager)
    ViewPager profileViewPager;
//    @BindView(R.id.viewPager1)
//    ViewPager profileViewPager1;
    @BindView(R.id.pageIndicatorView)
    PageIndicatorView indicatorView;

//
//    @BindView(R.id.announcement_layout)
//    RelativeLayout announcement_layout;

    Unbinder unbinder1;
    @BindView(R.id.topLayout)
    RelativeLayout topLayout;
    @BindView(R.id.bottomLayout)
    LinearLayout bottomLayout;

    private ParentChildPagerAdapter parentChildPagerAdapter;
    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;
    private Animation blink;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public int getLayoutByID() {
        return R.layout.fragment_home;
    }


    @Override
    protected void initViews() {

        blink = AnimationUtils.loadAnimation(getContext(), R.anim.blink);

//        profileViewPager.setClipToPadding(false);
//        profileViewPager.setPadding(60, 10, 60, 10);
//        profileViewPager.setPageMargin(50);
        parentChildPagerAdapter = new ParentChildPagerAdapter(getContext());

        profileViewPager.setAdapter(parentChildPagerAdapter);

        profileViewPager.setOffscreenPageLimit(parentChildPagerAdapter.getCount());
        parentChildPagerAdapter.setOnItemClicklistener(this);

        profileViewPager.addOnPageChangeListener(new OnPageChangelistener(indicatorView) {
            @Override
            public void onPageSelected(int i) {
                super.onPageSelected(i);

//                ParentChild parentChild = parentChildPagerAdapter.getItem(i);
//                ImageUtility.loadImage(mFullImage, parentChild.getImgUrl());

            }
        });


        profileViewPager.setPageTransformer(true, new ZoomOutPageTransformer());

//
//        new TapTargetSequence(getActivity())
//                .targets(
//                        TapTarget.forView(notiImg, "Notifications", "You can see notification alerts here")
//
//
//                ).start();


        mCardAdapter = new CardPagerAdapter(this);
//        mCardAdapter.addCardItem(new CardItem(getString(R.string.sample_date)));

//        mCardShadowTransformer = new ShadowTransformer(profileViewPager1, mCardAdapter);

//        profileViewPager1.setAdapter(mCardAdapter);
////        profileViewPager1.setPageTransformer(false, mCardShadowTransformer);
////        profileViewPager1.setOffscreenPageLimit(3);
//
//        announcement_layout.setVisibility(View.GONE);
//
//        profileViewPager1.setClipChildren(false);
//        profileViewPager1.setPadding(100, 10, 100, 20);
//        profileViewPager1.setPageMargin(0);

//        profileViewPager.setPageTransformer(true, new DepthPageTransformer());

//        addParentProfile();


        mFloatingMenu.setMainFabOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mFloatingMenu.isOptionsMenuOpened()) {
                    mFloatingMenu.closeOptionsMenu();
                }
            }
        });

        mFloatingMenu.setMiniFabsColors(R.color.colorPrimary, R.color.colorPrimary, R.color.colorPrimary);

        mFloatingMenu.setMiniFabSelectedListener(new OptionsFabLayout.OnMiniFabSelectedListener() {
            @Override
            public void onMiniFabSelected(MenuItem fabItem) {

                switch (fabItem.getItemId()) {


                    case R.id.fab_add_event:

                        addEvent();

                        break;

//                    case R.id.fab_add_paid_event:
//                        addEvent();
//                        break;

                    case R.id.fab_add_announcement:

                        addAnnouncement();

                        break;


                }


            }
        });


        try {

            if (Preferences.INSTANCE.getUserType().toLowerCase().equalsIgnoreCase("coach".toLowerCase())) {

                addParentProfile();

                mFloatingMenu.setVisibility(View.VISIBLE);
            } else {
                mFloatingMenu.setVisibility(View.GONE);
            }

        } catch (NullPointerException e) {

        }

        loadData();


        AnimUtils.setSlideDownAnimation(topLayout);
        AnimUtils.setSlideUpAnimation(bottomLayout);

    }


    private void loadData() {


        if (ConnectivityHelper.isConnectedToNetwork(getContext())) {

            getAnnouncement();

            getKids();


            showViewContent();

        } else {

            onNetworkError(false);
        }

    }

    @Override
    protected void onRetryOrCallApi() {


//        loadData();

        if (getActivity() instanceof DashBoardActivity) {

            ((DashBoardActivity) getActivity()).addFragment();
        }

//        loadData();
//
//
//        if (getFragmentManager() != null) {
//            HomeEventListFragment fragment = (HomeEventListFragment) getChildFragmentManager().findFragmentById(R.id.fragment);
//
//            if (fragment != null) {
//                fragment.callEventListApi();
//            }
//        }
    }

    @Override
    public void onManuallyParseError(Response<?> response, boolean isToastMsg) {

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
                                    for (Kid kid1 : kid
                                            ) {


//                                        Log.e(TAG, "onNext kids: " + new Gson().toJson(kid1));

                                        Coach coach = kid1.getCoach();

                                        ParentChild parentChild = new ParentChild();
                                        parentChild.setType(ParentChild.TYPE.CHILD);
                                        parentChild.setWhoIs("Kid");
                                        parentChild.setId(kid1.getId());
                                        parentChild.setImgUrl(kid1.getAvatarImage());
                                        parentChild.setName(kid1.getFirstname() + " " + kid1.getLastname());
                                        parentChild.setCoachName(coach.getFirstname() + " " + coach.getLastname());
                                        parentChild.setCoachImage(coach.getAvatarImage());
                                        parentChild.setTeamName(kid1.getTeamName());

                                        parentChildPagerAdapter.add(parentChild);

                                    }


                                    profileViewPager.setOffscreenPageLimit(parentChildPagerAdapter.getCount());
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
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void addEvent() {

        if (mFloatingMenu.isOptionsMenuOpened()) {
            mFloatingMenu.closeOptionsMenu();
        }
//        AddEventFragment addEventFragment = new AddEventFragment();
////        addEventFragment.setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
//        addEventFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
//        addEventFragment.show(getChildFragmentManager(), "AddFragment");


        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity());


        startActivity(new Intent(getContext(), AddEventActivity.class), options.toBundle());

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void addAnnouncement() {
        if (mFloatingMenu.isOptionsMenuOpened()) {
            mFloatingMenu.closeOptionsMenu();
        }

        PostAnnouncementFragment addEventFragment = new PostAnnouncementFragment();
        addEventFragment.setSharedElementEnterTransition(new ChangeBounds());
        addEventFragment.setEnterTransition(new Slide());
        addEventFragment.setExitTransition(new Slide());
        addEventFragment.setSharedElementReturnTransition(new ChangeBounds());

//        addEventFragment.setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        addEventFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
        addEventFragment.show(getChildFragmentManager(), "PostAnnouncement");

    }


    private void getAnnouncement() {

        RetrofitAdapter.getNetworkApiServiceClient().fetchAnnouncements(Preferences.INSTANCE.getAuthendicate())
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AnnouncementApi>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(AnnouncementApi value) {
                        setAnnouncementData(value.data);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void setAnnouncementData(List<AnnouncementApi.Datum> upcommingEventList) {


//        if (upcommingEventList != null && !upcommingEventList.isEmpty()) {
//
//            if (profileViewPager1 != null)
//                announcement_layout.setVisibility(View.VISIBLE);
//
//            if (!upcommingEventList.isEmpty()) {
//                mCardAdapter.updateList(HomeEventListFragment.getOnlyFiveInTheList(upcommingEventList));
//
//            } else {
//                if (profileViewPager1 != null) {
//                    announcement_layout.setVisibility(View.GONE);
//
//
//                }
//            }
//
//
//        }

    }

    private void addParentProfile() {

        ParentChild parentChild = new ParentChild();
        parentChild.setName(Preferences.INSTANCE.getUserName());
        parentChild.setImgUrl(Preferences.INSTANCE.getLogo());
        parentChild.setWhoIs(getString(Preferences.INSTANCE.getUserType()));
        parentChild.setType(ParentChild.TYPE.PARENT);
        parentChildPagerAdapter.add(parentChild);

//        parentChild = new ParentChild();
//        parentChild.setName(Preferences.INSTANCE.getUserName());
//        parentChild.setImgUrl(Preferences.INSTANCE.getLogo());
//        parentChild.setWhoIs("Child");
//        parentChild.setType(ParentChild.TYPE.CHILD);
//
//        parentChildPagerAdapter.add(parentChild);
//
//        parentChild = new ParentChild();
//        parentChild.setName(Preferences.INSTANCE.getUserName());
//        parentChild.setImgUrl(Preferences.INSTANCE.getLogo());
//        parentChild.setWhoIs("Coach");
//        parentChild.setType(ParentChild.TYPE.COACH);
//
//        parentChildPagerAdapter.add(parentChild);


    }


    @OnClick({R.id.noti_img, R.id.textView2})
    public void onClick(View view) {


        startActivity(new Intent(getContext(), NotificationActivity.class));


    }


    public void setNotificationCount() {

//        Log.e(TAG, "setNotificationCount: ");
//        notificationCount.setVisibility(count > 0 ? View.VISIBLE : View.GONE);

        final int count = Preferences.INSTANCE.getNotCount();
        notificationCount.setVisibility(View.VISIBLE);
        notificationCount.setText(String.format(Locale.ENGLISH, "%d", count));

        notificationCount.startAnimation(blink);

        blink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                notificationCount.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder1.unbind();
    }


//    @OnClick({R.id.imageView3, R.id.back})
//    public void onViewClicked() {
////        if (getActivity() != null && getActivity() instanceof DashBoardActivity)
////            ((DashBoardActivity) getActivity()).callFragmentFromOutSide(R.id.navigation_settings);
//
//
//    }

    @Override
    public void onResume() {
        super.onResume();
        setNotificationCount();

    }

    @Override
    public void OnItemClick(View sharedView, ParentChild parentChild, int position) {


//        MyNotificationManager.getInstance(getContext())
//                .displayNotification("Hello ", "Test", "event", "487");


        if (parentChild.getType() == ParentChild.TYPE.PARENT) {


            Intent i = new Intent(getContext(), ProfileDetailsActivity.class);
            i.putExtra(ProfileDetailsActivity.EXTRA_LOGO, parentChild.getImgUrl());

//            View sharedView = blueIconImageView;
            String transitionName = getString(R.string.transition_image);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity());//ActivityOptions.makeSceneTransitionAnimation(getActivity(), sharedView, transitionName);

                startActivity(i, transitionActivityOptions.toBundle());
            } else {

                startActivity(i);

            }

//            startActivity(new Intent(getContext(), ProfileDetailsActivity.class));

        } else if (parentChild.getType() == ParentChild.TYPE.CHILD) {


            Intent i = MaterialProfileActivity.getInstance(getContext(), parentChild.getId());
            i.putExtra(ProfileDetailsActivity.EXTRA_LOGO, parentChild.getImgUrl());
//            View sharedView = blueIconImageView;
            String transitionName = getString(R.string.transition_image);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity());//ActivityOptions.makeSceneTransitionAnimation(getActivity(), sharedView, transitionName);

                startActivity(i, transitionActivityOptions.toBundle());
            } else {

                startActivity(i);

            }


//            startActivity(KidsProfile.getInstance(getContext(), parentChild.getId()));
//            startActivity(MaterialProfileActivity.getInstance(getContext(), parentChild.getId()));


//            startActivity(new Intent(getContext(), KidsProfile.class));

        } else if (parentChild.getType() == ParentChild.TYPE.COACH) {

            CoachProfileFragment coachProfileFragment = new CoachProfileFragment();

            coachProfileFragment.show(getFragmentManager(), "Coach Profile");

        } else {

            Log.e(TAG, "OnItemClick: not UserType");
        }
    }

    @Override
    public void OnItemClick(int pos) {

        if (pos == mCardAdapter.getSize() - 1) {

            moveToAnnouncementEventActivity();

        } else {

            AnnouncementApi.Datum datum = mCardAdapter.getItem(pos);

            AnnouncementDialogFragment.getInstance(datum, false).show(getChildFragmentManager(), "Announcement");

        }
    }

    @Override
    public void OnItemDeleteClick(int pos) {

    }

    @Override
    public void OnItemMoreClick(int pos) {

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void moveToAnnouncementEventActivity() {


        if (getActivity() instanceof DashBoardActivity) {

            ((DashBoardActivity) getActivity()).callFragmentFromOutSide(new MoreAnnouncementFragment());

        }

    }

    @Override
    public void networkConnected() {

        loadData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder1 = ButterKnife.bind(this, rootView);
        return rootView;
    }


    private static class OnPageChangelistener extends OnPageChangeListener {


        private WeakReference<PageIndicatorView> pageIndicator;

        private OnPageChangelistener(PageIndicatorView pageIndicator) {
            this.pageIndicator = new WeakReference<>(pageIndicator);
        }

        @Override
        public void onPageSelected(int i) {

            pageIndicator.get().setSelection(i);

        }
    }

}
