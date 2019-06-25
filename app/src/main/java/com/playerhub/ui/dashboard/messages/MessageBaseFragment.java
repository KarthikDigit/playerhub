package com.playerhub.ui.dashboard.messages;

import android.view.View;

import com.playerhub.preference.Preferences;
import com.playerhub.ui.base.BaseFragment;
import com.playerhub.ui.base.MultiStateViewFragment;

public abstract class MessageBaseFragment extends MultiStateViewFragment {


    public abstract void refreshData();


    public abstract void searchData(String s);

    public void showFabGroupCreateButton(View view) {
        try {

            if (Preferences.INSTANCE.getUserType().toLowerCase().equalsIgnoreCase("coach".toLowerCase())) {

                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.GONE);
            }

        } catch (NullPointerException e) {

        }

    }


}
