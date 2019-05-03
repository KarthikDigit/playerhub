package com.playerhub.ui.base;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.playerhub.R;
import com.playerhub.network.APIErrorUtil;
import com.playerhub.network.BaseApiError;
import com.vlonjatg.progressactivity.ProgressFrameLayout;

import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Response;

public abstract class MyBaseActivity extends BaseActivity {

    protected static final String TAG = "MyBaseActivity";

    ProgressFrameLayout rootview;

    public abstract int getLayoutID();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        rootview = findViewById(R.id.rootview);

        addView(getLayoutID());
        initViews();
    }

    public void addView(int layoutId) {

        View view = LayoutInflater.from(this).inflate(layoutId, rootview, false);

        rootview.addView(view, view.getLayoutParams());

        ButterKnife.bind(this);

    }

    public ProgressFrameLayout getRootview() {
        return rootview;
    }

    public abstract void initViews();


    public void loading() {

        rootview.showLoading();
    }


    public void serverError(Response<?> response) {


        ResponseBody baseResponse = APIErrorUtil.cloneResponseBody(response);
        BaseApiError baseApiError = APIErrorUtil.parseErrorTest(BaseApiError.class, baseResponse);
        if (baseApiError != null) {
            showToast(baseApiError.getMessage());
            Log.e(TAG, "serverError: " + baseApiError.getMessage());
        }
        onManuallyParseError(response);
    }

    public abstract void onManuallyParseError(Response<?> response);

    public void onTimeout() {

        showToast("Network timeout,Please try again later");
    }

    public void onNetworkError() {

        showToast("There is no internet connection");

    }

    public void onUnknownError(String message) {

        showToast(message);

    }
}
