package com.playerhub.ui.dashboard.addsubaccount;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.playerhub.R;
import com.playerhub.customview.CustomEditTextViewSpinner;
import com.playerhub.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddSubAccountActivity extends BaseActivity {

    @BindView(R.id.parent)
    RadioButton parent;
    @BindView(R.id.player)
    RadioButton player;
    @BindView(R.id.guest)
    RadioButton guest;
    @BindView(R.id.profile_group)
    RadioGroup profileGroup;
    @BindView(R.id.player_spinner)
    CustomEditTextViewSpinner playerSpinner;
    @BindView(R.id.firstandlastname)
    LinearLayout firstandlastname;
    @BindView(R.id.cancel)
    Button cancel;
    @BindView(R.id.create_subaccount)
    Button createSubaccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sub_account);
        ButterKnife.bind(this);

        setBackButtonEnabledAndTitleBold("Add Sub Account");


        profileGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {

                    case R.id.parent:

                        showOrHidePlayer(false);
                        break;

                    case R.id.guest:
                        showOrHidePlayer(false);
                        break;

                    case R.id.player:
                        showOrHidePlayer(true);
                        break;


                }

            }
        });


    }

    private void showOrHidePlayer(boolean b) {


        playerSpinner.setVisibility(b ? View.VISIBLE : View.GONE);

        firstandlastname.setVisibility(!b ? View.VISIBLE : View.GONE);

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
