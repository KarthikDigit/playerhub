package com.playerhub.common;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.playerhub.network.APIError;
import com.playerhub.network.APIErrorUtil;
import com.playerhub.utils.ProgressUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import java.util.List;

import io.reactivex.observers.DisposableObserver;
import okhttp3.ResponseBody;

public abstract class CallbackWrapper<T> extends DisposableObserver<T> {

    //Reference link https://medium.com/mindorks/rxjava2-and-retrofit2-error-handling-on-a-single-place-8daf720d42d6
    //BaseView is just a reference of a View in MVP
    private WeakReference<Context> weakReference;

    public CallbackWrapper(Context context) {
        this.weakReference = new WeakReference<>(context);
        ProgressUtils.showProgress(context, "Loading");
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

        String msg;

        if (e instanceof HttpException) {

            APIError apiError = APIErrorUtil.parseError(((HttpException) e).response());

            msg = apiError.getMessage();


        } else if (e instanceof SocketTimeoutException) {

//            view.onTimeout();
            msg = "Time out";
        }
//        else if (e instanceof IOException) {
//            view.onNetworkError();
//        }
        else {
            APIError apiError =  APIErrorUtil.getDefaultError(null);

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
}