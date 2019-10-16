package com.playerhub.ui.dashboard.home.addevent;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.playerhub.R;
import com.playerhub.trans.EventDetailsTransition;
import com.playerhub.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddEventActivity extends BaseActivity {

    @BindView(R.id.content)
    FrameLayout viewRoot;
    @BindView(R.id.targetView)
    RelativeLayout targetView;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        ButterKnife.bind(this);

        setBackButtonEnabledAndTitle("Add Event");
        setAnimation();

        Fragment fragment = new AddEventFragment();

        fragment.setSharedElementEnterTransition(new EventDetailsTransition());
        fragment.setEnterTransition(new Explode());
        fragment.setExitTransition(new Slide());
        fragment.setSharedElementReturnTransition(new EventDetailsTransition());


        getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();


//        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.changebounds_with_arcmotion);
//        getWindow().setSharedElementEnterTransition(transition);
//        transition.addListener(new Transition.TransitionListener() {
//            @Override
//            public void onTransitionStart(Transition transition) {
//
//            }
//
//            @Override
//            public void onTransitionEnd(Transition transition) {
//                animateRevealShow(viewRoot);
////                animateButtonsIn();
//
////                int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
////                int cy = viewRoot.getTop();
////                int finalRadius = Math.max(viewRoot.getWidth(), viewRoot.getHeight());
////
////                Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, 0, finalRadius);
////                viewRoot.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
////
////                anim.setDuration(1000);
////                anim.setInterpolator(new AccelerateInterpolator());
////                anim.start();
//
//            }
//
//            @Override
//            public void onTransitionCancel(Transition transition) {
//
//            }
//
//            @Override
//            public void onTransitionPause(Transition transition) {
//
//            }
//
//            @Override
//            public void onTransitionResume(Transition transition) {
//
//            }
//
//
//        });
////
//        TransitionManager.beginDelayedTransition(targetView, transition);
//
//
//        int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
//        int cy = viewRoot.getTop();
//        int finalRadius = Math.max(viewRoot.getWidth(), viewRoot.getHeight());
//
//        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, 0, finalRadius);
//        viewRoot.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
//
//        anim.setDuration(1500);
//        anim.setInterpolator(new AccelerateInterpolator());
//        anim.start();


//        animateRevealShow(viewRoot);

    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void animateRevealShow(View viewRoot) {


        Toast.makeText(this, "Called", Toast.LENGTH_SHORT).show();

        int centerX = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
        int centerY = (viewRoot.getTop() + viewRoot.getBottom()) / 2;
        float finalRadius = (float) Math.hypot((double) centerX, (double) centerY);
        Animator mCircularReveal = ViewAnimationUtils.createCircularReveal(
                targetView, centerX, centerY, 0, finalRadius);
        targetView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        mCircularReveal.setDuration(1500);
        mCircularReveal.setInterpolator(new AccelerateInterpolator());
        mCircularReveal.start();
//        int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
//        int cy = (viewRoot.getTop() + viewRoot.getBottom()) / 2;
//        int finalRadius = Math.max(viewRoot.getWidth(), viewRoot.getHeight());
//
//        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, 0, finalRadius);
//        viewRoot.setVisibility(View.VISIBLE);
//        anim.setDuration(1000);
//        anim.setInterpolator(new AccelerateInterpolator());
//        anim.start();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setAnimation() {

        Slide explode = new Slide();

//        explode.setDuration(500);

        getWindow().setEnterTransition(explode);


        Fade explode1 = new Fade();

//        explode1.setDuration(100);

        getWindow().setExitTransition(explode1);


    }
}
