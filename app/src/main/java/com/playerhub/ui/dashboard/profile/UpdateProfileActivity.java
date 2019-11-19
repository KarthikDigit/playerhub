package com.playerhub.ui.dashboard.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.transition.ArcMotion;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.playerhub.R;
import com.playerhub.morpdialog.MorphDialogToFab;
import com.playerhub.morpdialog.MorphFabToDialog;
import com.playerhub.morpdialog.MorphTransition;
import com.playerhub.network.RetrofitAdapter;
import com.playerhub.network.request.UpdateKidDetail;
import com.playerhub.network.response.KidDetailsUpdatedResponse;
import com.playerhub.preference.Preferences;
import com.playerhub.ui.base.BaseActivity;
import com.playerhub.ui.base.BaseView;
import com.playerhub.utils.TextInputUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class UpdateProfileActivity extends BaseActivity implements BaseView {


    private static final String EXTRA_ID = "id";
    public static final String EXTRA_FIRSTNAME = "firstname";
    public static final String EXTRA_LASTNAME = "lastname";
    @BindView(R.id.firstName)
    TextInputLayout mFirstName;
    @BindView(R.id.lastName)
    TextInputLayout mLastName;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.container)
    RelativeLayout container;
//    @BindView(R.id.cancel)
//    Button cancel;
//    @BindView(R.id.update_profile)
//    Button updateProfile;


    public static void startActivity(Context context, int kidId, String firstname, String lastname) {

        Intent intent = new Intent(context, UpdateProfileActivity.class);
        intent.putExtra(EXTRA_ID, kidId);
        intent.putExtra(EXTRA_FIRSTNAME, firstname);
        intent.putExtra(EXTRA_LASTNAME, lastname);
        context.startActivity(intent);

    }

    public static Intent getInstance(Context context, int kidId, String firstname, String lastname) {

        Intent intent = new Intent(context, UpdateProfileActivity.class);
        intent.putExtra(EXTRA_ID, kidId);
        intent.putExtra(EXTRA_FIRSTNAME, firstname);
        intent.putExtra(EXTRA_LASTNAME, lastname);
        return intent;

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
//        Window window = getWindow();
//        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        setContentView(R.layout.activity_update_profile);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ButterKnife.bind(this);
        setBackButtonEnabledAndTitleBold("Update Profile");


//        setupSharedEelementTransitions1();

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(EXTRA_FIRSTNAME) && intent.hasExtra(EXTRA_LASTNAME)) {

            String firstname = intent.getStringExtra(EXTRA_FIRSTNAME);
            String lastname = intent.getStringExtra(EXTRA_LASTNAME);

            TextInputUtil.setText(mFirstName, firstname);
            TextInputUtil.setText(mLastName, lastname);

            mFirstName.setFocusable(true);
            mFirstName.getEditText().setFocusable(true);

        }


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupSharedEelementTransitions1() {

        ArcMotion arcMotion = new ArcMotion();
        arcMotion.setMinimumHorizontalAngle(50f);
        arcMotion.setMinimumVerticalAngle(50f);

        Interpolator easeInOut = AnimationUtils.loadInterpolator(this, android.R.interpolator.fast_out_slow_in);

        MorphFabToDialog sharedEnter = new MorphFabToDialog();
        sharedEnter.setPathMotion(arcMotion);
        sharedEnter.setInterpolator(easeInOut);

        MorphDialogToFab sharedReturn = new MorphDialogToFab();
        sharedReturn.setPathMotion(arcMotion);
        sharedReturn.setInterpolator(easeInOut);

        if (container != null) {
            sharedEnter.addTarget(container);
            sharedReturn.addTarget(container);
        }
        getWindow().setSharedElementEnterTransition(sharedEnter);
        getWindow().setSharedElementReturnTransition(sharedReturn);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupSharedEelementTransitions2() {

        ArcMotion arcMotion = new ArcMotion();
        arcMotion.setMinimumHorizontalAngle(50f);
        arcMotion.setMinimumVerticalAngle(50f);

        Interpolator easeInOut = AnimationUtils.loadInterpolator(this, android.R.interpolator.fast_out_slow_in);

        //hujiawei 100是随意给的一个数字，可以修改，需要注意的是这里调用container.getHeight()结果为0
        MorphTransition sharedEnter = new MorphTransition(ContextCompat.getColor(this, R.color.fab_color),
                ContextCompat.getColor(this, R.color.dialog_background_color), 100, getResources().getDimensionPixelSize(R.dimen.dialog_corners), true);
        sharedEnter.setPathMotion(arcMotion);
        sharedEnter.setInterpolator(easeInOut);

        MorphTransition sharedReturn = new MorphTransition(ContextCompat.getColor(this, R.color.dialog_background_color),
                ContextCompat.getColor(this, R.color.fab_color), getResources().getDimensionPixelSize(R.dimen.dialog_corners), 100, false);
        sharedReturn.setPathMotion(arcMotion);
        sharedReturn.setInterpolator(easeInOut);

        if (container != null) {
            sharedEnter.addTarget(container);
            sharedReturn.addTarget(container);
        }
        getWindow().setSharedElementEnterTransition(sharedEnter);
        getWindow().setSharedElementReturnTransition(sharedReturn);

    }

    @OnClick({R.id.cancel, R.id.update_profile})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cancel:

                onBackPressed();

                break;
            case R.id.update_profile:

                updateKidDetails();

                break;
        }
    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else {

            finish();
        }

    }

    private void updateKidDetails() {


        UpdateKidDetail detail = new UpdateKidDetail();
        detail.setFirstname(TextInputUtil.getText(mFirstName));
        detail.setLastname(TextInputUtil.getText(mLastName));

        int id = getKidId();

        progressBar.setVisibility(View.VISIBLE);

        RetrofitAdapter.getNetworkApiServiceClient().updateKidDetails(Preferences.INSTANCE.getAuthendicate(), detail, id)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyCallBack<KidDetailsUpdatedResponse>(this, this, false, true) {
                    @Override
                    public void onSuccess(KidDetailsUpdatedResponse response) {
                        progressBar.setVisibility(View.GONE);
                        if (response != null) {

                            showToast(response.getMessage());

                            Intent intent = new Intent();

                            intent.putExtra(EXTRA_FIRSTNAME, TextInputUtil.getText(mFirstName));
                            intent.putExtra(EXTRA_LASTNAME, TextInputUtil.getText(mLastName));

                            setResult(RESULT_OK, intent);

                            finish();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        progressBar.setVisibility(View.GONE);
                    }
                });

    }

    private int getKidId() {
        return getIntent().getIntExtra(EXTRA_ID, 0);
    }

    @Override
    public void serverError(Response<?> response, boolean isToastMsg) {

        showToast(response.errorBody().toString());

    }

    @Override
    public void onTimeout(boolean isToastMsg) {
        showToast("Timeout");
    }

    @Override
    public void onNetworkError(boolean isToastMsg) {

        showToast("No network");

    }

    @Override
    public void onUnknownError(String message, boolean isToastMsg) {

        showToast(message);

    }
}
