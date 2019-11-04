package com.playerhub.ui.dashboard.contact;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.playerhub.R;
import com.playerhub.customview.CustomEditTextViewSpinner;
import com.playerhub.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContactActivity extends BaseActivity {

    @BindView(R.id.player_spinner)
    CustomEditTextViewSpinner playerSpinner;
    @BindView(R.id.message)
    EditText message;
    @BindView(R.id.cancel)
    Button cancel;
    @BindView(R.id.create_subaccount)
    Button createSubaccount;
    @BindView(R.id.bottom_lay)
    CardView bottomLay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        ButterKnife.bind(this);
        setBackButtonEnabledAndTitleBold("Contact Us");

    }

    @OnClick({R.id.cancel, R.id.create_subaccount})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                break;
            case R.id.create_subaccount:
                break;
        }
    }
}
