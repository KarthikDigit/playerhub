package com.playerhub.test;

import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.playerhub.R;
import com.playerhub.common.CustomViewPager;
import com.playerhub.notification.Constants;
import com.playerhub.preference.Preferences;
import com.playerhub.ui.dashboard.messages.Conversations;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Main2Activity extends AppCompatActivity implements FingerPrintImpl.FingerprintCallback, Fingerprint {

    private static final String TAG = "Main2Activity";
    private Fingerprint fingerPrintApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);

        fingerPrintApi = new FingerPrintImpl(this, getFragmentManager(), this);

        callFingerprint();


        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CONVERSATION).child(Preferences.INSTANCE.getMsgUserId()).child("-LcKYuy0S_7DGaONVQ4k").child("unread").getRef()

                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        try {
                            Long value = dataSnapshot.getValue(Long.class);
                            Log.e(TAG, "onDataChange: UnRead Count " + value.longValue());
                        } catch (NullPointerException e) {

                            Log.e(TAG, "onDataChange: " + e.getMessage());

                        } catch (DatabaseException e) {
                            Log.e(TAG, "onDataChange: " + e.getMessage());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }


    @Override
    public void onFingerPrintSuccess() {


        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onFingerPrintFailer(String s) {

        Toast.makeText(this, "" + s, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void callFingerprint() {

        fingerPrintApi.callFingerprint();
    }

    @Override
    public void createKey(String keyName, boolean invalidatedByBiometricEnrollment) {

        fingerPrintApi.createKey(keyName, invalidatedByBiometricEnrollment);
    }

    @Override
    public void onPurchased(boolean withFingerprint, @Nullable FingerprintManager.CryptoObject cryptoObject) {

        fingerPrintApi.onPurchased(withFingerprint, cryptoObject);

    }
}
