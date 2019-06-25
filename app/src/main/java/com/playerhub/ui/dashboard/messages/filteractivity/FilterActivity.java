package com.playerhub.ui.dashboard.messages.filteractivity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.playerhub.R;
import com.playerhub.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FilterActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.applyFilter)
    Button applyFilter;


    private FastItemAdapter<FilterOption> fastItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        ButterKnife.bind(this);

        setBackButtonEnabledAndTitle("Team wise filter");


        fastItemAdapter = new FastItemAdapter<>();
        fastItemAdapter.withSelectable(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(fastItemAdapter);


        for (int i = 1; i <= 10; i++) {

            fastItemAdapter.add(new FilterOption("Team " + i));
        }


//        //configure our fastAdapter
//        fastItemAdapter.withOnClickListener(new OnClickListener<FilterOption>() {
//            @Override
//            public boolean onClick(View v, IAdapter<FilterOption> adapter, @NonNull FilterOption item, int position) {
//                Toast.makeText(v.getContext(), item.filter_name, Toast.LENGTH_LONG).show();
//                return false;
//            }
//        });
//
//        fastItemAdapter.withOnPreClickListener(new OnClickListener<FilterOption>() {
//            @Override
//            public boolean onClick(View v, IAdapter<FilterOption> adapter, @NonNull FilterOption item, int position) {
//                // consume otherwise radio/checkbox will be deselected
//                return true;
//            }
//        });


        fastItemAdapter.withEventHook(new FilterOption.RadioButtonClickEvent());
//restore selections (this has to be done after the items were added
        fastItemAdapter.withSavedInstanceState(savedInstanceState);


        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @OnClick(R.id.applyFilter)
    public void onViewClicked() {


        List<FilterOption> filterOptions = new ArrayList<>(fastItemAdapter.getSelectedItems());

        showToast("Called  " + filterOptions.size() + " " + fastItemAdapter.getSelections());
    }
}
