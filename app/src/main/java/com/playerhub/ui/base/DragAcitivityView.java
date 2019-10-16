package com.playerhub.ui.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.playerhub.R;
import com.playerhub.customview.MultiStateView;
import com.playerhub.network.APIErrorUtil;
import com.playerhub.network.BaseApiError;
import com.playerhub.network.RetrofitAdapter;
import com.playerhub.network.request.Deviceinfo;
import com.playerhub.notification.Constants;
import com.playerhub.preference.Preferences;
import com.playerhub.utils.NetworkHelper;
import com.playerhub.utils.ProgressUtils;
import com.vlonjatg.progressactivity.ProgressLayout;

import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Response;
import xyz.klinker.android.drag_dismiss.activity.DragDismissActivity;

public abstract class DragAcitivityView extends DragDismissActivity implements EasyPermissions.PermissionCallbacks,
        EasyPermissions.RationaleCallbacks, BaseView {

    private MultiStateView multiStateView;


    public abstract int getLayoutByID();


//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(getLayoutByID());
//
//
//    }


    @Override
    public View onCreateContent(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        View view = inflater.inflate(getLayoutByID(), parent, false);

        multiStateView = view.findViewById(R.id.multiStateView);
        ButterKnife.bind(this, view);
        initViews();

        return view;
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

    private static final int RC_PHONE_STATS = 12;
    private static final String TAG = "BaseActivity";

    private final CompositeDisposable disposable = new CompositeDisposable();


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(base));
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (Preferences.INSTANCE.isUserLoggedIn()) {
            setChatConnectionEnable(true);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (Preferences.INSTANCE.isUserLoggedIn()) {
            setChatConnectionEnable(false);
        }
    }

    private void setChatConnectionEnable(boolean isEnable) {
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS).child(Preferences.INSTANCE.getMsgUserId()).child("connection").setValue(isEnable ? 1 : new Date().getTime());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        disposable.clear();
    }

    public void showToast(String msg) {


        Toast.makeText(this, "" + msg, Toast.LENGTH_SHORT).show();


    }

    public void setBackButtonEnabledAndTitle(String title) {

        if (getSupportActionBar() != null) {


            getSupportActionBar().setTitle(Html.fromHtml(title));

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//            Spannable text = new SpannableString(getSupportActionBar().getTitle());
//            text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//            getSupportActionBar().setTitle(text);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean getNetWorkStatus() {

        if (NetworkHelper.isNetworkAvailable(this)) {

            return true;
        } else {

            showToast(getString(R.string.no_internet));

            return false;
        }

    }

    public void showLoading() {
        ProgressUtils.showProgress(DragAcitivityView.this, "Loading");
    }

    public void hideLoading() {
        ProgressUtils.hideProgress();
    }

    public void loge(String msg) {
        Log.e(TAG, msg);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
////            String yes = getString(R.string.yes);
////            String no = getString(R.string.no);
////
////            // Do something after user returned from app settings screen, like showing a Toast.
////            Toast.makeText(
////                    this,
////                    getString(R.string.returned_from_app_settings_to_activity,
////                            hasCameraPermission() ? yes : no,
////                            hasLocationAndContactsPermissions() ? yes : no,
////                            hasSmsPermission() ? yes : no),
////                    Toast.LENGTH_LONG)
////                    .show();
//        }
    }

    @Override
    public void onRationaleAccepted(int requestCode) {
        Log.d(TAG, "onRationaleAccepted:" + requestCode);
    }

    @Override
    public void onRationaleDenied(int requestCode) {
        Log.d(TAG, "onRationaleDenied:" + requestCode);
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


    @AfterPermissionGranted(RC_PHONE_STATS)
    protected void phoneStatePermission() {
//        String[] perms = {Manifest.permission.READ_PHONE_STATE};
//        if (EasyPermissions.hasPermissions(this, perms)) {
//            // Already have permission, do the thing

        getFcmTokenAndUpdate();
//            // ...
//        } else {
//            getFcmTokenAndUpdate();
//            // Do not have permissions, request them now
//            EasyPermissions.requestPermissions(this, getString(R.string.phone_state_rationale),
//                    RC_PHONE_STATS, perms);
//        }
    }


    protected void getFcmTokenAndUpdate() {

        String android_id = "";
        android_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        Log.d("Android", "Android ID : " + android_id);

//        Map<String, String> tokenAndDeviceID = dataSource.getTokenAndDeviceID();
//
        final String token = Preferences.INSTANCE.getFCMToken();

        if (!(token != null && token.length() > 0)) {
//            a9cf10a5798da358


            final String finalAndroid_id = android_id;
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(final InstanceIdResult instanceIdResult) {

                    Deviceinfo deviceinfo = new Deviceinfo();

                    deviceinfo.setDevice_id(finalAndroid_id);
                    deviceinfo.setPush_token_id(instanceIdResult.getToken());
                    deviceinfo.setDevice_model("Android");
                    String id = Preferences.INSTANCE.getUserId();
                    deviceinfo.setUser_id(id);


                    loge(new Gson().toJson(deviceinfo));


                    RetrofitAdapter.getNetworkApiServiceClient().postPustId(Preferences.INSTANCE.getAuthendicate(), deviceinfo)
                            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<String>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onNext(String value) {

                                    Log.e(TAG, "onNext: push success " + value);

                                    Preferences.INSTANCE.putFcmToken(token);
                                    Preferences.INSTANCE.putDeviceId(finalAndroid_id);

                                }

                                @Override
                                public void onError(Throwable e) {

                                    Log.e(TAG, "onError: push fail " + e.getMessage());

                                }

                                @Override
                                public void onComplete() {

                                }
                            });

                    if (Preferences.INSTANCE.isUserLoggedIn()) {

                        FirebaseDatabase.getInstance()
                                .getReference(Constants.ARG_USERS)
                                .child(Preferences.INSTANCE.getMsgUserId())
                                .child(Constants.ARG_TOKEN_ID).setValue(instanceIdResult.getToken());

                    }

//
//                    dataSource.postDeviceInfo(dataSource.getAuthendicate(), deviceinfo, new DataListener() {
//                        @Override
//                        public void onSuccess(Object object) {
//
//                            dataSource.saveTokenAndDeviceID(instanceIdResult.getToken(), finalAndroid_id);
//
//                            Log.e(TAG, "onSuccess: fcm success registered " + object.toString());
//                        }
//
//                        @Override
//                        public void onFail(Throwable throwable) {
//
//                            Log.e(TAG, "onFail: " + throwable.getMessage());
//
//                            Log.e(TAG, "onFail: fcm fail to regidtered");
//                        }
//
//                        @Override
//                        public void onNetworkFailure() {
//
//                        }
//                    });


//                    Log.e(TAG, "onSuccess: token " + instanceIdResult.getToken());
//                    Log.e(TAG, "onSuccess: id " + instanceIdResult.getId());


                }
            });
        } else {
            loge("Already registered");
        }


    }


    protected <T extends TextView> void setText(T t, String text) {

        t.setText(getString(text));
    }

    protected String getString(String s) {

        return s != null && s.length() > 0 ? s : "";
    }


    protected <T> void setObservableAndObserver(Observable<T> observable, DisposableObserver<T> observer) {


        disposable.add(observable.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(observer));

    }


    protected static abstract class Observers<T> extends DisposableObserver<T> {

        private boolean isLoading = false;

        protected Observers(Context context, boolean isLoading) {

            this.isLoading = isLoading;

            if (isLoading)
                ProgressUtils.showProgress(context, "Loading");
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

}
