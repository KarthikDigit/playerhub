package com.playerhub.databindingtest;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.playerhub.R;
import com.playerhub.databinding.ActivityDataBindingTestBinding;
import com.playerhub.databinding.DataBindingRowBinding;
import com.playerhub.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class DataBindingTestActivity extends DataBindingBase {

    private static final String TAG = "DataBindingTestActivity";


    private ActivityDataBindingTestBinding bindingt;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_data_binding_test;
    }

    @Override
    protected void init(ViewDataBinding binding) {
        bindingt = (ActivityDataBindingTestBinding) binding;

        SpinnerValue spinnerValue = new SpinnerValue();
        spinnerValue.setName(null);
        spinnerValue.setPassword(null);
        bindingt.setSp(spinnerValue);
        bindingt.setActivity(this);
//        bindingt.setEmptyCheck(new StringUtils());


        bindingt.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bindingt.recyclerView.setAdapter(new Adapter(getList()));

    }


    private List<ModelValues> getList() {

        List<ModelValues> list = new ArrayList<>();

        for (int i = 0; i < 100; i++) {

            ModelValues values = new ModelValues();
            values.setName("Name " + i);

            list.add(values);

        }

        return list;


    }


    public void onSaveClick(View view, SpinnerValue spinnerValue) {


        Log.e(TAG, "onSaveClick: " + new Gson().toJson(spinnerValue));


        Toast.makeText(view.getContext(), "Ok " + spinnerValue.getSpinnerList().get(spinnerValue.getSelectedPosition()), Toast.LENGTH_SHORT).show();
    }


    protected class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        private List<ModelValues> list;

        public Adapter(List<ModelValues> list) {

            this.list = list;

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

            ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.data_binding_row, viewGroup, false);

            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

            ((DataBindingRowBinding) viewHolder.binding).setModel(list.get(i));


        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        protected class ViewHolder extends RecyclerView.ViewHolder {

            private ViewDataBinding binding;

            public ViewHolder(@NonNull ViewDataBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }


        }

    }


}
