package com.playerhub.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.playerhub.R;
import com.playerhub.customview.MultiStateView;
import com.playerhub.network.APIErrorUtil;
import com.playerhub.network.BaseApiError;

import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Response;

public abstract class MultiStateViewActivity extends BaseActivity implements BaseView {

    protected static final String TAG = "MultiStateViewActivity";

    private MultiStateView multiStateView;


    public abstract int getLayoutByID();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutByID());
        multiStateView = findViewById(R.id.multiStateView);
        ButterKnife.bind(this);
        initViews();

    }

    protected abstract void initViews();

    public void showViewLoading() {

        multiStateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
    }

    public void showViewContent() {

        multiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
    }

    public void showViewError() {

        multiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
        View view = multiStateView.getView(MultiStateView.VIEW_STATE_ERROR);
        Button retry = view.findViewById(R.id.retry);

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRetryOrCallApi();
            }
        });
    }

    public void showViewError(String errorMsg) {

        multiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);

        View view = multiStateView.getView(MultiStateView.VIEW_STATE_ERROR);

        TextView eMsgView = view.findViewById(R.id.errorMsg);
        Button retry = view.findViewById(R.id.retry);

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRetryOrCallApi();
            }
        });
        eMsgView.setText(errorMsg);

    }

    public void showViewEmpty() {

        multiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
    }

    public void showViewEmpty(String emptyMsg) {

        multiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
        View view = multiStateView.getView(MultiStateView.VIEW_STATE_EMPTY);

        TextView empty = view.findViewById(R.id.emptyMsg);
        empty.setText(emptyMsg);
    }

    protected abstract void onRetryOrCallApi();


    public void serverError(Response<?> response, boolean isToastMsg) {


        ResponseBody baseResponse = APIErrorUtil.cloneResponseBody(response);
        BaseApiError baseApiError = APIErrorUtil.parseErrorTest(BaseApiError.class, baseResponse);
        if (baseApiError != null) {

            if (isToastMsg)
                showToast(baseApiError.getMessage());

        }
        onManuallyParseError(response, isToastMsg);
    }

    public abstract void onManuallyParseError(Response<?> response, boolean isToastMsg);

    public void onTimeout(boolean isToastMsg) {

        if (isToastMsg) {

            showToast("Timeout");
        } else {
            showViewError("Timeout");
        }

    }

    public void onNetworkError(boolean isToastMsg) {

        if (isToastMsg) {

            showToast("There is no internet connection");

        } else {

            showViewError("There is no internet connection");

        }

    }

    public void onUnknownError(String message, boolean isToastMsg) {

        if (isToastMsg) {

            showToast(message);
        } else {
            showViewError(message);
        }


    }
}
