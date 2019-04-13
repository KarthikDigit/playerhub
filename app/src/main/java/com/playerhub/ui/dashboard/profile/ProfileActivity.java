package com.playerhub.ui.dashboard.profile;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.playerhub.R;
import com.playerhub.ui.base.BaseActivity;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class ProfileActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_profile);


    }

    public void onBack(View view) {

        onBackPressed();
    }


}
