package com.playerhub.ui.dashboard.contact;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.playerhub.R;
import com.playerhub.customview.CustomEditTextViewSpinner;
import com.playerhub.network.RetrofitAdapter;
import com.playerhub.network.request.Feedback;
import com.playerhub.network.response.ContactUsApi;
import com.playerhub.preference.Preferences;
import com.playerhub.ui.base.BaseActivity;
import com.playerhub.utils.ProgressUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ContactActivity extends BaseActivity {

    @BindView(R.id.player_spinner)
    CustomEditTextViewSpinner<Topic> playerSpinner;
    @BindView(R.id.message)
    EditText message;
    @BindView(R.id.cancel)
    Button cancel;
    @BindView(R.id.send)
    Button createSubaccount;
    @BindView(R.id.bottom_lay)
    CardView bottomLay;
    @BindView(R.id.rootview)
    RelativeLayout mRootView;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
//        Window window = getWindow();
//        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        setContentView(R.layout.activity_contact);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ButterKnife.bind(this);
        setBackButtonEnabledAndTitleBold("Contact Us");

        playerSpinner.setList(getTopics());

    }

    @OnClick({R.id.cancel, R.id.send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cancel:

                finish();

                break;
            case R.id.send:

                sendContatsUs();

                break;
        }
    }


    private void sendContatsUs() {

        int selected = playerSpinner.getSelectedPosition();
//
//        playerSpinner.setError("");
//        playerSpinner.setErrorEnabled(false);
//        message.setError(null);
//        message.setErrorEnabled(false);

        if (!(selected > -1)) {

            showSnackBar("Please select any topic");

            return;
        }

        String msg = message.getText().toString();

        if (TextUtils.isEmpty(msg)) {


            showSnackBar("Please enter your message");

            return;

        }


        Feedback feedback = new Feedback();

        feedback.setFeedback(msg);
        feedback.setTopic(playerSpinner.getItem().getEmail());


        mProgressBar.setVisibility(View.VISIBLE);

        RetrofitAdapter.getNetworkApiServiceClient().postFeedback(Preferences.INSTANCE.getAuthendicate(), feedback)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ContactUsApi>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ContactUsApi contactUsApi) {
                        mProgressBar.setVisibility(View.GONE);

                        showToast(contactUsApi.getMessage());

                        onBackPressed();


                    }

                    @Override
                    public void onError(Throwable e) {
                        mProgressBar.setVisibility(View.GONE);
                        loge(e.getMessage());

                        showToast(e.getMessage());
//                        ProgressUtils.hideProgress();
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }


    private void showSnackBar(String msg) {


        Snackbar.make(mRootView, msg, Snackbar.LENGTH_LONG).show();

    }


    private static List<Topic> getTopics() {

        List<Topic> list = new ArrayList<>();

        list.add(new Topic("finance@barcanypro.com", "Finance - Questions related to academy registration, subscription, uniforms, financial aid, etc."));
        list.add(new Topic("store@barcanypro.com", "Store - Returns, or lost packages"));
        list.add(new Topic("tournaments@barcanypro.com", "Tournaments - Logistics"));
        list.add(new Topic("trips@barcanypro.com", "Trips - Questions related to any academy destination trip"));
        list.add(new Topic("camps@barcanypro.com", "Camps - Questions related to any of the seasonal camps"));
        list.add(new Topic("college@barcanypro.com", "College - Questions related to college assistance"));
        list.add(new Topic("sporting@barcanypro.com", "Sporting - Questions related to players or team placement"));
        list.add(new Topic("support@barcanypro.com", "Others"));

        return list;

    }
}
