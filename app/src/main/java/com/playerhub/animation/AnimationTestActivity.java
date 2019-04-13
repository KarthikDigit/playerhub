package com.playerhub.animation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.playerhub.R;
import com.playerhub.ui.base.BaseActivity;

import java.util.Base64;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AnimationTestActivity extends BaseActivity {

    @BindView(R.id.imageView4)
    ImageView imageView4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation_test);
        ButterKnife.bind(this);

        setAnimation();
    }


    private void setAnimation() {


        Animation animation = AnimationUtils.loadAnimation(this, R.anim.blink);

        animation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {

                showToast("Hello World");

            }
        });
        imageView4.setAnimation(animation);

    }


    protected static abstract class AnimationListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

}
