package com.playerhub.ui.base;


import android.content.Context;

import com.playerhub.listener.Observer;
import com.playerhub.ui.dashboard.DashBoardActivity;

public abstract class BaseNetworkCheck extends MultiStateViewFragment implements Observer {


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (getActivity() instanceof DashBoardActivity) {

            ((DashBoardActivity) getActivity()).register(this);
        }

    }


}
