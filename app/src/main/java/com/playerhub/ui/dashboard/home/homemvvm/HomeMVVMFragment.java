package com.playerhub.ui.dashboard.home.homemvvm;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.ChangeBounds;
import android.transition.Slide;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.playerhub.R;
import com.playerhub.customview.MultiStateView;
import com.playerhub.network.response.EventListApi.UpcommingEvent;
import com.playerhub.preference.Preferences;
import com.playerhub.ui.base.BaseFragment;
import com.playerhub.ui.dashboard.DashBoardActivity;
import com.playerhub.ui.dashboard.announcement.PostAnnouncementFragment;
import com.playerhub.ui.dashboard.home.EventsAdapter;
import com.playerhub.ui.dashboard.home.HomeFragment;
import com.playerhub.ui.dashboard.home.ParentChild;
import com.playerhub.ui.dashboard.home.ParentChildPagerAdapter;
import com.playerhub.ui.dashboard.home.addevent.AddEventActivity;
import com.playerhub.ui.dashboard.home.eventdetails.EventDetailsActivity;
import com.playerhub.ui.dashboard.notification.NotificationActivity;
import com.playerhub.ui.dashboard.profile.CoachProfileFragment;
import com.playerhub.ui.dashboard.profile.MaterialProfileActivity;
import com.playerhub.ui.dashboard.profile.ProfileDetailsActivity;
import com.rd.PageIndicatorView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeMVVMFragment extends BaseFragment implements ParentChildPagerAdapter.OnItemClicklistener, EventsAdapter.OnItemClickListener<UpcommingEvent> {

    private static final int REQUEST_CODE = 123;

    Unbinder unbinder;
    @BindView(R.id.textView2)
    TextView mNotiCountView;
    @BindView(R.id.myrootView)
    FrameLayout mChildView;
    @BindView(R.id.viewPager)
    ViewPager profileViewPager;
    @BindView(R.id.pageIndicatorView)
    PageIndicatorView pageIndicatorView;
    @BindView(R.id.see_all)
    Button seeAll;
    @BindView(R.id.eventList)
    RecyclerView eventList;
    @BindView(R.id.msg)
    TextView mMsg;
    @BindView(R.id.multiStateView)
    MultiStateView multiStateView;
    @BindView(R.id.floatingActionButton)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.fab_fullView)
    View fabFullView;
    @BindView(R.id.fab_circle_view)
    ImageView fabCircleView;
    @BindView(R.id.shimmer_view_container)
    ShimmerFrameLayout mShimmerViewContainer;
    @BindView(R.id.event_view)
    RelativeLayout mEventViewLayout;

    private FloatingActionMenu actionMenu;

    private ParentChildPagerAdapter parentChildPagerAdapter;
    private EventsAdapter eventsAdapter;


    private HomeViewModel homeViewModel;

    public HomeMVVMFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_mvvm, container, false);
        unbinder = ButterKnife.bind(this, view);

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);


        initView();
        setFabCircleView();
        loadData();

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmerAnimation();
        homeViewModel.getNotificationCount();
    }

    @Override
    public void onPause() {
        mShimmerViewContainer.stopShimmerAnimation();
        super.onPause();
    }

    private void initView() {

        parentChildPagerAdapter = new ParentChildPagerAdapter(getContext(), new ArrayList<ParentChild>());
        profileViewPager.setAdapter(parentChildPagerAdapter);
        profileViewPager.setOffscreenPageLimit(parentChildPagerAdapter.getCount());
        parentChildPagerAdapter.setOnItemClicklistener(this);

//        profileViewPager.setPageTransformer(false, new HingeTransformation());

        eventsAdapter = new EventsAdapter(getContext(), new ArrayList<UpcommingEvent>(), this);
        eventList.setLayoutManager(new LinearLayoutManager(getContext()));
        eventList.setAdapter(eventsAdapter);

    }


//    public class HingeTransformation implements ViewPager.PageTransformer {
//        @Override
//        public void transformPage(View page, float position) {
//
//            page.setTranslationX(-position * page.getWidth());
//            page.setPivotX(0);
//            page.setPivotY(0);
//
//
//            if (position < -1) {    // [-Infinity,-1)
//                // This page is way off-screen to the left.
//                page.setAlpha(0);
//
//            } else if (position <= 0) {    // [-1,0]
//                page.setRotation(90 * Math.abs(position));
//                page.setAlpha(1 - Math.abs(position));
//
//            } else if (position <= 1) {    // (0,1]
//                page.setRotation(0);
//                page.setAlpha(1);
//
//            } else {    // (1,+Infinity]
//                // This page is way off-screen to the right.
//                page.setAlpha(0);
//
//            }
//
//
//        }
//    }


    private void loadData() {


        homeViewModel.getCheckIsLoggedAsCoach().observe(this, new Observer<Boolean>() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {

                if (aBoolean != null && aBoolean) {

                    floatingActionButton.setVisibility(View.VISIBLE);

                } else {

                    floatingActionButton.setVisibility(View.GONE);

                }
            }
        });


        homeViewModel.getNotificationCount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer notiCount) {

                if (notiCount != null && notiCount > 0) {

                    mNotiCountView.setVisibility(View.VISIBLE);
                    mNotiCountView.setText(String.format(Locale.ENGLISH, "%d", notiCount));

                } else {

                    mNotiCountView.setVisibility(View.GONE);

                }

            }
        });

        homeViewModel.getParentChildList().observe(this, new Observer<List<ParentChild>>() {
            @Override
            public void onChanged(@Nullable List<ParentChild> parentChildren) {

                if (parentChildren != null && !parentChildren.isEmpty()) {

                    parentChildPagerAdapter.addAll(parentChildren);
                    parentChildPagerAdapter.notifyDataSetChanged();
                    mChildView.setVisibility(View.VISIBLE);

                } else {

                    mChildView.setVisibility(View.GONE);

                }

            }
        });


        homeViewModel.getEventList().observe(this, new Observer<List<UpcommingEvent>>() {
            @Override
            public void onChanged(@Nullable List<UpcommingEvent> list) {

                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);

                if (list != null && !list.isEmpty()) {

                    mEventViewLayout.setVisibility(View.VISIBLE);
                    eventList.setVisibility(View.VISIBLE);
                    mMsg.setVisibility(View.GONE);
                    eventsAdapter.updateList(list);

                } else {

                    mEventViewLayout.setVisibility(View.GONE);
                    eventList.setVisibility(View.GONE);
                    mMsg.setVisibility(View.VISIBLE);

                }

            }
        });

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.noti_img, R.id.textView2})
    public void onClick(View view) {

        startActivity(new Intent(getContext(), NotificationActivity.class));

    }

    @OnClick(R.id.see_all)
    public void onSeeAllClicked() {

        if (getActivity() instanceof DashBoardActivity) {

            ((DashBoardActivity) getActivity()).showAllEvent();

        }

    }

    @Override
    public void OnItemClick(View view, ParentChild parentChild, int position) {

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

                startActivityForResult(i, REQUEST_CODE, transitionActivityOptions.toBundle());

            } else {

                startActivityForResult(i, REQUEST_CODE);

            }


        } else if (parentChild.getType() == ParentChild.TYPE.COACH) {

            CoachProfileFragment coachProfileFragment = new CoachProfileFragment();

            coachProfileFragment.show(getFragmentManager(), "Coach Profile");

        } else {

            Log.e(TAG, "OnItemClick: not UserType");

        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void OnItemClick(View view, UpcommingEvent upcommingEvent, int position) {

        Intent i = EventDetailsActivity.getIntent(getContext(), upcommingEvent.getId());

        ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity());

        startActivity(i, transitionActivityOptions.toBundle());

    }


    private void setFabCircleView() {

        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(getActivity());
// repeat many times:
        ImageView itemIcon = new ImageView(getActivity());
        itemIcon.setBackgroundColor(Color.TRANSPARENT);
        itemIcon.setBackgroundResource(R.drawable.ic_iconfinder_announcement_2742787);
        itemIcon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white), PorterDuff.Mode.SRC_IN);

        ImageView itemIcon1 = new ImageView(getActivity());
        itemIcon1.setBackgroundColor(Color.TRANSPARENT);
        itemIcon1.setBackgroundResource(R.drawable.ic_small_calendar1);
//        itemIcon1.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        itemIcon1.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white), PorterDuff.Mode.SRC_IN);


        SubActionButton button1 = itemBuilder.setContentView(itemIcon).build();
        SubActionButton button2 = itemBuilder.setContentView(itemIcon1).build();
        button1.setBackgroundColor(Color.TRANSPARENT);
        button2.setBackgroundColor(Color.TRANSPARENT);

        int redius = (int) getResources().getDimension(R.dimen._50sdp);

        actionMenu = new FloatingActionMenu.Builder(getActivity())
                .addSubActionView(button1)
                .addSubActionView(button2)
                .attachTo(floatingActionButton)
                .setRadius(redius)
                .build();

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addEvent();
//                Toast.makeText(v.getContext(), "Button2", Toast.LENGTH_SHORT).show();
                actionMenu.close(true);
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addAnnouncement();

//                Toast.makeText(v.getContext(), "Button2", Toast.LENGTH_SHORT).show();
                actionMenu.close(true);

            }
        });


        actionMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu floatingActionMenu) {
                fabCircleView.setVisibility(View.VISIBLE);
                fabFullView.setVisibility(View.VISIBLE);
                fabCircleView.animate().alpha(0).start();
//                fabCircleView.setAlpha(0);
                floatingActionButton.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(floatingActionButton, pvhR);
                animation.start();


                PropertyValuesHolder pvhR1 = PropertyValuesHolder.ofFloat(View.ALPHA, .8f);
                ObjectAnimator animation1 = ObjectAnimator.ofPropertyValuesHolder(fabCircleView, pvhR1);
                animation1.start();

            }

            @Override
            public void onMenuClosed(FloatingActionMenu floatingActionMenu) {
                fabFullView.setVisibility(View.GONE);
                floatingActionButton.setRotation(45);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(floatingActionButton, pvhR);
                animation.start();

                PropertyValuesHolder pvhR1 = PropertyValuesHolder.ofFloat(View.ALPHA, 0);
                ObjectAnimator animation1 = ObjectAnimator.ofPropertyValuesHolder(fabCircleView, pvhR1);
                animation1.start();
                animation1.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        fabCircleView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            }
        });

        fabFullView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (actionMenu.isOpen()) actionMenu.close(true);

            }
        });

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void addEvent() {

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity());
        startActivity(new Intent(getContext(), AddEventActivity.class), options.toBundle());

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void addAnnouncement() {

        PostAnnouncementFragment addEventFragment = new PostAnnouncementFragment();
        addEventFragment.setSharedElementEnterTransition(new ChangeBounds());
        addEventFragment.setEnterTransition(new Slide());
        addEventFragment.setExitTransition(new Slide());
        addEventFragment.setSharedElementReturnTransition(new ChangeBounds());
//        addEventFragment.setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        addEventFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
        addEventFragment.show(getChildFragmentManager(), "PostAnnouncement");

    }
}
