package com.playerhub.ui.login;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.playerhub.R;
import com.playerhub.common.CallbackWrapper;
import com.playerhub.common.SMSRetriver;
import com.playerhub.network.RetrofitAdapter;
import com.playerhub.network.response.OTPValidateApi;
import com.playerhub.preference.Preferences;
import com.playerhub.ui.dashboard.DashBoardActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class ValidateOtpFragment extends DialogFragment implements SMSRetriver.CallBack {


    private static final String EXTRA_MOBILE = "mobile_number";
    private static final String TAG = "ValidateOtpFragment";
    @BindView(R.id.otp)
    TextInputLayout otp;
    @BindView(R.id.validate_otp)
    Button validateOtp;
    Unbinder unbinder;
    @BindView(R.id.otp_info)
    TextView otpInfo;

//    private SMSRetriver smsRetriver;

    public ValidateOtpFragment() {
        // Required empty public constructor
    }


    public static ValidateOtpFragment getInstance(String mobile_number) {

        ValidateOtpFragment validateOtpFragment = new ValidateOtpFragment();

        Bundle bundle = new Bundle();

        bundle.putString(EXTRA_MOBILE, mobile_number);

        validateOtpFragment.setArguments(bundle);

        return validateOtpFragment;

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getContext() != null) {
//            smsRetriver = new SMSRetriver(getContext(), this);
//        }
        setCancelable(true);


    }

    @Override
    public void onStart() {
        super.onStart();
//        if (smsRetriver != null)
//            smsRetriver.registerBroadcast();
        Dialog d = getDialog();
        if (d != null) {
            d.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_validate_otp, container, false);
        unbinder = ButterKnife.bind(this, view);
//        if (smsRetriver != null)
//            smsRetriver.startSmsRetriever();

        return view;
    }


    @Override
    public void onStop() {
        super.onStop();

//        if (smsRetriver != null)
//            smsRetriver.unRegisterBroadcast();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.validate_otp)
    public void onViewClicked(View view) {


        if (getArguments() != null && otp.getEditText() != null) {
            String mobile_number = getArguments().getString(EXTRA_MOBILE);


            String o = otp.getEditText().getText().toString();

            long code = Long.parseLong(o);
            long mo = Long.parseLong(mobile_number);

            Map<String, Object> params = new HashMap<>();
            params.put("mobile_no", mo);
            params.put("flag", 2);
            params.put("otp", code);

            Log.e(TAG, "validateOtp: " + new JSONObject(params).toString());


            RetrofitAdapter.getNetworkApiServiceClient().validateOtp(new JSONObject(params).toString())
                    .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new CallbackWrapper<OTPValidateApi>(getContext()) {
                        @Override
                        protected void onSuccess(OTPValidateApi otpValidateApi) {

                            if (otpValidateApi.getSuccess()) {
                                Preferences.INSTANCE.putUserLoggedInStatus(true);

                                startActivity(new Intent(getContext(), DashBoardActivity.class));

                                dismiss();

                                getActivity().finish();

                            } else {
                                Toast.makeText(getContext(), "OTP validation is failed. Please try again", Toast.LENGTH_SHORT).show();
                            }

                        }


                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);

                            Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getContext(), "There is no mobile number", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void showOTPCode(String code) {

        if (otp.getEditText() != null) {
            otp.getEditText().setText(code);
            otpInfo.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFailure(@NonNull Exception e) {

        Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
