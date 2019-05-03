package com.playerhub.myslidingtest;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.playerhub.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MySlidingActivity extends AppCompatActivity {


    private List<String> list = new ArrayList<>();

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    CustomPagerAdapter customPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sliding);
        ButterKnife.bind(this);
        list.add("dsfsdfsjdfjasjfafasfksakdfask");
        list.add("dsfsdfsjdfjasjfafasfksakdfask");
        list.add("dsfsdfsjdfjasjfafasfksakdfask");
        list.add("dsfsdfsjdfjasjfafasfksakdfask");
        list.add("dsfsdfsjdfjasjfafasfksakdfask");
        list.add("dsfsdfsjdfjasjfafasfksakdfask");

        customPagerAdapter = new CustomPagerAdapter(this);

        viewPager.setAdapter(customPagerAdapter);


    }


    public class CustomPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;

        public CustomPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((CardView) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);

            TextView imageView = (TextView) itemView.findViewById(R.id.txt);
            imageView.setText(list.get(position));

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((CardView) object);
        }
    }
}
