package com.playerhub.databindingtest;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.support.annotation.BinderThread;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import com.playerhub.BR;

import java.util.ArrayList;
import java.util.List;

public class SpinnerValue extends BaseObservable {

    private static final String TAG = "SpinnerValue";
    private List<SPValus> spinnerList;


    private String name;

    private String password;

    private Integer selectedPosition = 0;

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }

    @Bindable
    public Integer getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(Integer selectedPosition) {
        this.selectedPosition = selectedPosition;
        notifyPropertyChanged(BR.selectedPosition);
    }

    public SpinnerValue() {
        spinnerList = getList();
    }

    public List<SPValus> getSpinnerList() {
        return spinnerList;
    }

    public void setSpinnerList(List<SPValus> spinnerList) {
        this.spinnerList = spinnerList;

    }

    public static List<SPValus> getList() {

        List<SPValus> list = new ArrayList<>();

        for (int i = 0; i < 10; i++) {

            list.add(new SPValus("Name Test " + i, " Des scri"));
        }

        return list;
    }
//
//    public void onClickView(View view, SpinnerValue spinnerValue) {
//
//        Toast.makeText(view.getContext(), "Ok " + spinnerValue.getSpinnerList().get(spinnerValue.getSelectedPosition()), Toast.LENGTH_SHORT).show();
//    }
//

//    @BindingAdapter("selectedItemPosition")
//    public void setSelectedItemPosition(Spinner spinner, int position) {
//        if (spinner.getSelectedItemPosition() != position) {
//            spinner.setSelection(position);
//            selectedPosition = position;
//            Log.e(TAG, "setSelectedItemPosition: " + position);
//        }
//    }
}
