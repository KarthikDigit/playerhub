package com.playerhub.ui.dashboard.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;

import com.playerhub.R;
import com.playerhub.network.RetrofitAdapter;
import com.playerhub.network.request.UpdateKidDetail;
import com.playerhub.network.response.KidDetailsUpdatedResponse;
import com.playerhub.preference.Preferences;
import com.playerhub.ui.base.BaseActivity;
import com.playerhub.ui.base.BaseView;
import com.playerhub.utils.ProgressUtils;
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

    @OnClick({R.id.cancel, R.id.update_profile})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cancel:

                finish();

                break;
            case R.id.update_profile:

                updateKidDetails();

                break;
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
