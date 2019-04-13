package com.playerhub.test;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.playerhub.R;
import com.playerhub.common.CustomViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Main2Activity extends AppCompatActivity {

    @BindView(R.id.pager)
    CustomViewPager pager;

    private MyPagerAdapter myPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);


        myPagerAdapter = new MyPagerAdapter(this, getList());

        pager.setAdapter(myPagerAdapter);

    }


    private List<PageTest> getList() {

        List<PageTest> list = new ArrayList<>();

        for (int i = 0; i < 5; i++) {

            PageTest test = new PageTest();

            test.setName("Test " + i);

            list.add(test);
        }


        return list;

    }
}
