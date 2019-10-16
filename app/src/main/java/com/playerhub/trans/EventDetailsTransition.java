package com.playerhub.trans;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.Transition;
import android.transition.TransitionSet;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class EventDetailsTransition extends TransitionSet {

    public EventDetailsTransition() {

        addTransition(new ChangeBounds())
                .addTransition(new ChangeTransform())
                .addTransition(new ChangeImageTransform());

    }
}
