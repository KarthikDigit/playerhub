package com.playerhub.common;

import android.content.Context;
import android.widget.Toast;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.playerhub.network.APIError;
import com.playerhub.network.APIErrorUtil;
import com.playerhub.utils.ProgressUtils;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;

import io.reactivex.observers.DisposableObserver;
import okhttp3.ResponseBody;

public abstract class CallbackWrapperTest<T> extends DisposableObserver<T> {

    enum APIMethod {

        LOGIN, SIGNUP;

    }

    private APIMethod apiMethod;

    //Reference link https://medium.com/mindorks/rxjava2-and-retrofit2-error-handling-on-a-single-place-8daf720d42d6
    //BaseView is just a reference of a View in MVP
    private WeakReference<Context> weakReference;

    public CallbackWrapperTest(Context view, APIMethod apiMethod) {
        this.weakReference = new WeakReference<>(view);
        this.apiMethod = apiMethod;
        ProgressUtils.showProgress(view, "Loading");
    }

    protected abstract void onSuccess(T t);

    @Override
    public void onNext(T t) {
        //You can return StatusCodes of different cases from your API and handle it here. I usually include these cases on BaseResponse and iherit it from every Response
        onSuccess(t);

        ProgressUtils.hideProgress();
    }

    @Override
    public void onError(Throwable e) {
        ProgressUtils.hideProgress();
        Context view = weakReference.get();

        String msg = "";

        if (e instanceof HttpException) {
//            ResponseBody responseBody = ((HttpException) e).response().errorBody();
//            view.onUnknownError(getErrorMessage(responseBody));

            switch (apiMethod) {


                case LOGIN:

                    msg = loginApiError(e);

                    break;

                case SIGNUP:

                    break;

            }


        } else if (e instanceof SocketTimeoutException) {

//            view.onTimeout();
            msg = "Time out";
        } else {
            APIError apiError = APIErrorUtil.getDefaultError(null);

            msg = apiError.getMessage();
//            view.onUnknownError(e.getMessage());
        }


        Toast.makeText(view, "" + msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onComplete() {
        ProgressUtils.hideProgress();

    }

    private String getErrorMessage(ResponseBody responseBody) {
        try {
            JSONObject jsonObject = new JSONObject(responseBody.string());
            return jsonObject.getString("message");
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String loginApiError(Throwable e) {

        String msg;
        APIError apiError = APIErrorUtil.parseError(((HttpException) e).response());

//            msg = apiError.getMessage();

        if (apiError.getData() != null && apiError.getData() instanceof String) {

//                List<String> m = apiError.getData().getEmail();
//                List<String> m1 = apiError.getData().getPassword();
//
//                StringBuilder builder=new StringBuilder();


//                msg = m.get(0) + "  " + m1.get(0);
            msg = apiError.getData().toString();


        } else if (apiError.getErrors() != null && apiError.getErrors() instanceof APIError.Errors) {


            msg = apiError.getErrors().getDesignNo().get(0);

        } else {

            msg = "Something went wrong";

        }

        return msg;
    }
}