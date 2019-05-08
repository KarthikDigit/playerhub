package com.playerhub.ui.dashboard.messages;

import android.view.View;

import com.playerhub.preference.Preferences;
import com.playerhub.ui.base.BaseFragment;

public abstract class MessageBaseFragment extends BaseFragment {


    public abstract void refreshData();


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
