package com.playerhub.ui.dashboard.home.addevent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.playerhub.R;
import com.playerhub.ui.base.BaseActivity;
import com.playerhub.ui.base.BaseFragment;

public class AddEventActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        setBackButtonEnabledAndTitle("Add Event");

        getSupportFragmentManager().beginTransaction().replace(R.id.content, new AddEventFragment()).commit();

    }
}
