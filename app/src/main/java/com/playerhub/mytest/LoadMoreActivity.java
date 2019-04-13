package com.playerhub.mytest;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.mikepenz.fastadapter_extensions.items.ProgressItem;
import com.mikepenz.fastadapter_extensions.scroll.EndlessRecyclerOnScrollListener;
import com.playerhub.R;

import java.util.ArrayList;
import java.util.List;

import static com.mikepenz.fastadapter.adapters.ItemAdapter.items;

public class LoadMoreActivity extends AppCompatActivity {
    //save our FastAdapter
    private FastItemAdapter<SimpleItem> fastItemAdapter;
    private ItemAdapter footerAdapter;
    private EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_more);

        //create our FastAdapter which will manage everything
        fastItemAdapter = new FastItemAdapter<>();
        fastItemAdapter.withSelectable(true);

        //create our FooterAdapter which will manage the progress items
        footerAdapter = items();
        fastItemAdapter.addAdapter(1, footerAdapter);

////        //configure our fastAdapter
//        fastItemAdapter.withOnClickListener(new OnClickListener<SimpleItem>() {
//            @Override
//            public boolean onClick(View v, IAdapter<SimpleItem> adapter, @NonNull SimpleItem item, int position) {
//                Toast.makeText(v.getContext(), (item).name.getText(v.getContext()), Toast.LENGTH_LONG).show();
//                return false;
//            }
//        });


        fastItemAdapter.withEventHook(new ClickEventHook<SimpleItem>() {
            @Override
            public void onClick(View v, int position, FastAdapter<SimpleItem> fastAdapter, SimpleItem item) {


                switch (v.getId()) {

                    case R.id.material_drawer_description:
                        Toast.makeText(v.getContext(), (item).description.getText(v.getContext()), Toast.LENGTH_LONG).show();
                        break;

                    case R.id.material_drawer_name:
                        Toast.makeText(v.getContext(), (item).name.getText(v.getContext()), Toast.LENGTH_LONG).show();
                        break;
                }

            }


        });
//        fastItemAdapter.withEventHook(new ClickEventHook<SimpleItem>() {
//            @Override
//            public void onClick(View v, int position, FastAdapter<SimpleItem> fastAdapter, SimpleItem item) {
//
//
//            }
//        });

        //get our recyclerView and do basic setup
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(fastItemAdapter);
        endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(footerAdapter) {
            @Override
            public void onLoadMore(final int currentPage) {
                footerAdapter.clear();
                footerAdapter.add(new ProgressItem().withEnabled(false));
                //simulate networking (2 seconds)
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        footerAdapter.clear();
                        for (int i = 1; i < 16; i++) {
                            fastItemAdapter.add(fastItemAdapter.getAdapterItemCount(), new SimpleItem().withName("Item " + i + " Page " + currentPage).withDescription(" hello howw  " + currentPage));
                        }
                    }
                }, 2000);
            }
        };
        recyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);


        //fill with some sample data (load the first page here)
        List<SimpleItem> items = new ArrayList<>();
        for (int i = 1; i < 16; i++) {
            items.add(new SimpleItem().withName("Item " + i + " Page 0").withDescription(" hello howw  "));
        }
        fastItemAdapter.add(items);

    }
}
