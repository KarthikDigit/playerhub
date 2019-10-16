package com.playerhub.ui.dashboard.home.addevent;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Matrix;
import android.os.Build;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.playerhub.R;
import com.playerhub.ui.base.BaseActivity;
import com.playerhub.ui.dashboard.home.announcement.PostAnnouncementFragment;


public class EventSharingActivity extends BaseActivity {

    private int mDefaultAnimDuration;

    private FrameLayout addEventLayout, announcementLayout;
    private RelativeLayout shareContentLayout;
    private ViewGroup mRootView;
    private ImageView mBackgroundView;
    private View[] mItemViews;


    private enum ThemeType {

        SHARE, ADDEVENT, ADDANNOUNCEMENT

    }

    private ThemeType themeType = ThemeType.SHARE;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        if (themeType.equals(ThemeType.SHARE)) {
//
//            setTheme(R.style.AppTheme_Sharing);
//
//        } else {

        setTheme(R.style.AppTheme_Sharing_test);
//        }


        setContentView(R.layout.activity_event_sharing);


        Transition transition = new ChangeBounds();
        transition.setDuration(150);
        transition.setInterpolator(new LinearInterpolator());

        getWindow().setSharedElementEnterTransition(transition);


        mDefaultAnimDuration = getResources().getInteger(R.integer.default_anim_duration);

        // Find view references
        mRootView = (ViewGroup) findViewById(R.id.content_root);
        addEventLayout = (FrameLayout) findViewById(R.id.addeventlayout);
        announcementLayout = (FrameLayout) findViewById(R.id.createpostlayout);
        shareContentLayout = (RelativeLayout) findViewById(R.id.share_layout);
        mBackgroundView = (ImageView) findViewById(R.id.background);
        mItemViews = new View[]{findViewById(R.id.fab_add_event), findViewById(R.id.fab_add_announcement)};


        getSupportActionBar().hide();

        initViews();
//        mRootView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                onBackPressed();
//            }
//        });
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initViews() {

        if (themeType.equals(ThemeType.ADDEVENT)) {
            getSupportActionBar().show();
            addEventLayout.setVisibility(View.VISIBLE);
            announcementLayout.setVisibility(View.GONE);
            shareContentLayout.setVisibility(View.GONE);
            setBackButtonEnabledAndTitle("Add Event");

            getSupportFragmentManager().beginTransaction().replace(R.id.addeventlayout, new AddEventFragment()).commit();
            getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(this, R.color.white));

        } else if (themeType.equals(ThemeType.ADDANNOUNCEMENT)) {
            getSupportActionBar().show();
            addEventLayout.setVisibility(View.GONE);
            announcementLayout.setVisibility(View.VISIBLE);
            shareContentLayout.setVisibility(View.GONE);
            setBackButtonEnabledAndTitle("Create Announcement");

            getSupportFragmentManager().beginTransaction().replace(R.id.createpostlayout, new PostAnnouncementFragment()).commit();
            getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(this, R.color.white));


        } else {
            getSupportActionBar().hide();
            addEventLayout.setVisibility(View.GONE);
            announcementLayout.setVisibility(View.GONE);
            shareContentLayout.setVisibility(View.VISIBLE);

            // Setup initial states
            mBackgroundView.setVisibility(View.INVISIBLE);
            for (View itemView : mItemViews) {
                itemView.setAlpha(0);
            }


            getWindow().getSharedElementEnterTransition().addListener(new TransitionAdapter() {
                @Override
                public void onTransitionEnd(Transition transition) {
                    getWindow().getSharedElementEnterTransition().removeListener(this);
                    revealTheBackground();
                    showTheItems();
                }
            });

            getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));

        }


    }


    /**
     * Reveal the background
     */
    private void revealTheBackground() {
        mBackgroundView.setVisibility(View.VISIBLE);
        Animator reveal = createRevealAnimator(true);
        reveal.start();
    }

    /**
     * Hide the background
     */
    private void hideTheBackground() {
        Animator hide = createRevealAnimator(false);
        hide.setStartDelay(mDefaultAnimDuration);
        hide.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mBackgroundView.setVisibility(View.INVISIBLE);
                supportFinishAfterTransition();
            }
        });
        hide.start();
    }

    /**
     * Show the items
     */
    private void showTheItems() {
        for (int i = 0; i < mItemViews.length; i++) {
            View itemView = mItemViews[i];
            long startDelay = (mDefaultAnimDuration / mItemViews.length) * (i + 1);
            itemView.animate().alpha(1).setStartDelay(startDelay);
        }
    }

    /**
     * Hide the items
     */
    private void hideTheItems() {

        announcementLayout.setVisibility(View.GONE);
        addEventLayout.setVisibility(View.GONE);

        for (int i = 0; i < mItemViews.length; i++) {
            View itemView = mItemViews[i];
            long startDelay = (mDefaultAnimDuration / mItemViews.length) * (mItemViews.length - i);
            itemView.animate().alpha(0).setStartDelay(startDelay);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private Animator createRevealAnimator(boolean show) {
        final int cx = mBackgroundView.getWidth() / 2;
        final int cy = mBackgroundView.getHeight() / 2;
        // A lit bit more than half the width and half the height because this view is a square
        // and it's not going to perfectly align with a circle.
        final int radius = (int) Math.hypot(cx, cy);
        final Animator animator;
        if (show) {
            animator = ViewAnimationUtils.createCircularReveal(mBackgroundView, cx, cy, 0, radius);
            animator.setInterpolator(new DecelerateInterpolator());
        } else {
            animator = ViewAnimationUtils.createCircularReveal(mBackgroundView, cx, cy, radius, 0);
            animator.setInterpolator(new AccelerateInterpolator());
        }
        animator.setDuration(mDefaultAnimDuration);
        return animator;
    }


    @Override
    public void onBackPressed() {
        themeType = ThemeType.SHARE;
        hideTheItems();
        hideTheBackground();
        super.onBackPressed();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void shareClick(final View view) {


//        callAnotherAcitvity(view);
//
        // Load the transition
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.sharing_item_chosen);
        // Finish this Activity when the transition is ended
        transition.addListener(new TransitionAdapter() {
            @Override
            public void onTransitionEnd(Transition transition) {
                // Finish the activity
//                onBackPressed();

                callAnotherAcitvity(view);
//                finish();
                // Override default transition to fade out
//                overridePendingTransition(0, android.R.anim.fade_out);
            }
        });
//        // Capture current values in the scene root and then post a request to run a transition on the next frame
        TransitionManager.beginDelayedTransition(mRootView, transition);
//
//        // Change view property values
//
//        // 1. Item chosen
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        view.setLayoutParams(layoutParams);

        // 2. Rest of items
        for (View itemView : mItemViews) {
            if (itemView != view) {
                itemView.setVisibility(View.INVISIBLE);
            }
        }

        // 3. Background
        double diagonal = Math.sqrt(mRootView.getHeight() * mRootView.getHeight() + mRootView.getWidth() * mRootView.getWidth());
        float radius = (float) (diagonal / 2f);
        int h = mBackgroundView.getDrawable().getIntrinsicHeight();
        float scale = radius / (h / 2f);
        Matrix matrix = new Matrix(mBackgroundView.getImageMatrix());
        matrix.postScale(scale, scale, mBackgroundView.getWidth() / 2f, mBackgroundView.getHeight() / 2f);
        mBackgroundView.setScaleType(ImageView.ScaleType.MATRIX);
        mBackgroundView.setImageMatrix(matrix);


    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void callAnotherAcitvity(View view) {

        switch (view.getId()) {

            case R.id.fab_add_event:


                themeType = ThemeType.ADDEVENT;
                initViews();
//                recreate();

//                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
//
//                startActivity(new Intent(this, AddEventActivity.class), options.toBundle());

                break;

            case R.id.fab_add_announcement:

//                setTheme(R.style.AppTheme);
                themeType = ThemeType.ADDANNOUNCEMENT;

                initViews();
//                recreate();

//                ActivityOptions options1 = ActivityOptions.makeSceneTransitionAnimation(this);
//
//                startActivity(new Intent(this, PostAnnouncementActivity.class), options1.toBundle());


//                PostAnnouncementFragment addEventFragment = new PostAnnouncementFragment();
////        addEventFragment.setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
//                addEventFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
//                addEventFragment.show(getSupportFragmentManager(), "PostAnnouncement");

                break;


        }

    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public abstract class TransitionAdapter implements Transition.TransitionListener {
        @Override
        public void onTransitionStart(Transition transition) {

        }


        @Override
        public void onTransitionCancel(Transition transition) {

        }

        @Override
        public void onTransitionPause(Transition transition) {

        }

        @Override
        public void onTransitionResume(Transition transition) {

        }
    }
}
