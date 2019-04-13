package com.playerhub.test;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.playerhub.R;

import java.util.List;

public class MyPagerAdapter extends PagerAdapter {

    private Context context;
    private List<PageTest> list;
    private LayoutInflater layoutInflater;

    public MyPagerAdapter(Context context, List<PageTest> list) {
        this.context = context;
        this.list = list;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
//        return super.instantiateItem(container, position);

        int resId = -1;

        if (position == list.size() - 1) {

            resId = R.layout.page2;
        } else {
            resId = R.layout.page1;
        }

        View view = layoutInflater.inflate(resId, container, false);


        if (position == list.size() - 1) {


            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(100, ViewGroup.LayoutParams.WRAP_CONTENT);

            view.setLayoutParams(params);

        }

        PageTest test = list.get(position);

        TextView textView = view.findViewById(R.id.name);

        textView.setText(test.getName());


        ((ViewPager) container).addView(view, 0);
        return view;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }
}
