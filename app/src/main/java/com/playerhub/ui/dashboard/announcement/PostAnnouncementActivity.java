package com.playerhub.ui.dashboard.announcement;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.transition.Explode;

import com.playerhub.R;
import com.playerhub.ui.base.BaseActivity;

public class PostAnnouncementActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_announcement);
        setBackButtonEnabledAndTitle("Create Announcement");

        getSupportFragmentManager().beginTransaction().replace(R.id.content, new PostAnnouncementFragment()).commit();
        setAnimation();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setAnimation() {

        Explode explode = new Explode();

        explode.setDuration(500);

        getWindow().setEnterTransition(explode);


        Explode explode1 = new Explode();

        explode1.setDuration(500);

        getWindow().setExitTransition(explode1);


    }
}
