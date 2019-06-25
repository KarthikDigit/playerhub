package com.playerhub.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.firebase.database.DatabaseError;
import com.playerhub.R;
import com.playerhub.utils.NetworkHelper;
import com.playerhub.utils.ProgressUtils;
import com.vlonjatg.progressactivity.ProgressLayout;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public abstract class BaseFragment extends Fragment {

    public static final String TAG = "BaseFragment";
    protected CompositeDisposable disposable = new CompositeDisposable();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void showToast(String msg) {


        Toast.makeText(getContext(), "" + msg, Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }

    public boolean getNetWorkStatus() {

        if (NetworkHelper.isNetworkAvailable(getContext())) {

            return true;
        } else {

            showToast(getString(R.string.no_internet));

            return false;
        }

    }

    public void showLoading() {
        ProgressUtils.showProgress(getContext(), "Loading");
    }

    public void hideLoading() {
        ProgressUtils.hideProgress();
    }

    public void loge(String msg) {
        Log.e(TAG, msg);
    }

    protected String getString(String s) {

        return s != null && s.length() > 0 ? s : "";
    }


    protected <T> void setObservableAndObserver(Observable<T> observable, Observer observer) {

        observable.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);

    }


    protected static abstract class Observers<T> implements Observer<T> {

        private boolean isLoading;

        protected Observers(Context context, boolean isLoading) {

            this.isLoading = isLoading;

            if (isLoading)
                ProgressUtils.showProgress(context, "Loading");
        }

        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onNext(T value) {

            if (isLoading)
                ProgressUtils.hideProgress();

            onSuccess(value);

        }

        @Override
        public void onError(Throwable e) {
            if (isLoading)
                ProgressUtils.hideProgress();

            onFail(e);
        }

        protected abstract void onSuccess(T response);

        protected abstract void onFail(Throwable e);

        @Override
        public void onComplete() {

            ProgressUtils.hideProgress();

        }
    }


    public void showProgress(ProgressLayout progressLayout) {
        progressLayout.showLoading();
    }

    public void showContent(ProgressLayout progressLayout) {
        progressLayout.showContent();
    }

    public void showError(ProgressLayout progressLayout, String title, String des, String buttonTxt, View.OnClickListener onClickListener) {

        progressLayout.showError(null,
                title,
                des,
                buttonTxt, onClickListener);

    }

    public void showEmpty(ProgressLayout progressLayout, String title, String des) {

        progressLayout.showEmpty(null,
                title,
                des);

    }


    protected abstract class ValueEventListener implements com.google.firebase.database.ValueEventListener {

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

            Log.e(TAG, "onCancelled: " + databaseError.getMessage());
        }
    }
}
