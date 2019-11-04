package com.playerhub.ui.dashboard.changecreditcard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.playerhub.R;
import com.playerhub.ui.base.BaseActivity;

public class ChangeCreditCardActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_credit_card);

        setBackButtonEnabledAndTitleBold("Change Credit Card");

    }
}
