package com.playerhub.utils;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.playerhub.R;

public final class AnimUtils {


    public static void setFadeAnimation(View view) {

        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500);
        view.startAnimation(anim);
    }

    public static void setFadeAnimationtest(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1500);
        view.startAnimation(anim);
    }

    public static void setSlideDownAnimation(View view) {
        Animation zoomIn = AnimationUtils.loadAnimation(view.getContext(), R.anim.top_to_original);// animation file
//        zoomIn.setDuration(1000);
        view.startAnimation(zoomIn);
    }

    public static void setSlideUpAnimation(View view) {
        Animation zoomIn = AnimationUtils.loadAnimation(view.getContext(), R.anim.bottom_to_original);// animation file
//        zoomIn.setDuration(1000);
        view.startAnimation(zoomIn);
    }

}
