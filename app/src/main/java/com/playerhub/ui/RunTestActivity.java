package com.playerhub.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.playerhub.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RunTestActivity extends AppCompatActivity {

    @BindView(R.id.floatingActionButton)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.fab_circle_view)
    ImageView fabCircleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_test);
        ButterKnife.bind(this);


        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
// repeat many times:
        ImageView itemIcon = new ImageView(this);
        itemIcon.setBackgroundColor(Color.TRANSPARENT);
        itemIcon.setBackgroundResource(R.drawable.ic_iconfinder_announcement_2742787);
        itemIcon.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN);

        ImageView itemIcon1 = new ImageView(this);
        itemIcon1.setBackgroundColor(Color.TRANSPARENT);
        itemIcon1.setBackgroundResource(R.drawable.ic_small_calendar1);
//        itemIcon1.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        itemIcon1.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN);


        SubActionButton button1 = itemBuilder.setContentView(itemIcon).build();
        SubActionButton button2 = itemBuilder.setContentView(itemIcon1).build();
        button1.setBackgroundColor(Color.TRANSPARENT);
        button2.setBackgroundColor(Color.TRANSPARENT);


        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(RunTestActivity.this, "Button2", Toast.LENGTH_SHORT).show();

            }
        });

        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(button1)
                .addSubActionView(button2)
                .attachTo(floatingActionButton)
                .setRadius(180)
                .build();


        actionMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu floatingActionMenu) {
                fabCircleView.setVisibility(View.VISIBLE);
                fabCircleView.animate().alpha(0).start();
//                fabCircleView.setAlpha(0);
                floatingActionButton.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(floatingActionButton, pvhR);
                animation.start();

                PropertyValuesHolder pvhR1 = PropertyValuesHolder.ofFloat(View.ALPHA, 1);
                ObjectAnimator animation1 = ObjectAnimator.ofPropertyValuesHolder(fabCircleView, pvhR1);
                animation1.start();


//                int x = fabCircleView.getRight();
//                int y = fabCircleView.getBottom();
//
//                int startRadius = 0;
//                int endRadius = (int) Math.hypot(fabCircleView.getWidth(), fabCircleView.getHeight());
//
//                Animator anim = null;
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                    anim = ViewAnimationUtils.createCircularReveal(fabCircleView, x, y, startRadius, endRadius);
//                    anim.start();
//                }

//                fabCircleView.setVisibility(View.VISIBLE);


//                animation1.addListener(new Animator.AnimatorListener() {
//                    @Override
//                    public void onAnimationStart(Animator animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationCancel(Animator animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animator animation) {
//
//                    }
//                });

            }

            @Override
            public void onMenuClosed(FloatingActionMenu floatingActionMenu) {

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

    }
}
