package com.playerhub.common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

public class CustomViewPager extends ViewPager {

    public CustomViewPager(@NonNull Context context) {
        super(context);
        initPageChangeListener();
    }

    public CustomViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPageChangeListener();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int height = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), heightMeasureSpec);
            int h = child.getMeasuredWidth();
            if (h > height) height = h;
        }

//        int heightMeasureSpec1 = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        super.onMeasure(MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY), heightMeasureSpec);
    }

    private void initPageChangeListener() {
//        addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
//            @Override
//            public void onPageSelected(int position) {
//                requestLayout();
//            }
//        });
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        View child = getChildAt(getCurrentItem());
//        if (child != null) {
//            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
//            int h = child.getMeasuredHeight();
//            heightMeasureSpec = MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY);
//        }
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    }


//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//
//        try {
//            int numChildren = getChildCount();
//            for (int i = 0; i < numChildren; i++) {
//                View child = getChildAt(i);
//                if (child != null) {
//                    child.measure(widthMeasureSpec / 2, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
//                    int h = child.getMeasuredHeight();
//                    heightMeasureSpec = Math.max(heightMeasureSpec, MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    }
}