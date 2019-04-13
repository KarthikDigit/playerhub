package com.playerhub.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SMSRetriver implements OnSuccessListener<Void>, OnFailureListener {

    private static final String TAG = "SMSRetriver";

    private Receiver broadcastReceiver;
    private Context context;
    private CallBack callBack;

    public SMSRetriver(@NonNull Context context, CallBack callBack) {
//            super(context);

        this.context = context;
        this.callBack = callBack;
        broadcastReceiver = new Receiver(this, this.callBack);

    }

    public void startSmsRetriever() {
        SmsRetriever.getClient(context /* context */).startSmsRetriever().addOnSuccessListener(this).addOnFailureListener(this);
    }

    @Override
    public void onSuccess(Void aVoid) {

//        if (callBack != null) callBack.onSuccess(aVoid);

        unRegisterBroadcast();
        registerBroadcast();
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        unRegisterBroadcast();
//        if (callBack != null) callBack.onFailure(e);
    }

    private boolean isRegistered = false;

    public void registerBroadcast() {

//        unRegisterBroadcast();
        if (!isRegistered) {

            context.registerReceiver(broadcastReceiver, new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION));
            isRegistered = true;
        }
    }

    public void unRegisterBroadcast() {


//        Log.e(TAG, "unRegisterBroadcast: called");

        if (isRegistered) {
            context.unregisterReceiver(broadcastReceiver);
            isRegistered = false;
        }

//        try {
//            context.unregisterReceiver(broadcastReceiver);
//        } catch (IllegalArgumentException e) {
//            Log.e(TAG, "unRegisterBroadcast: " + e.getMessage());
//        }

    }

    public interface CallBack extends OnFailureListener {

        void showOTPCode(String code);
    }

    private static class Receiver extends BroadcastReceiver {

        private SMSRetriver smsRetriver;
        private CallBack callBack;

        private Receiver(SMSRetriver smsRetriver, CallBack callBack) {
            this.smsRetriver = smsRetriver;
            this.callBack = callBack;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {

//                Toast.makeText(context, "Local Called", Toast.LENGTH_SHORT).show();

                Bundle extras = intent.getExtras();
                Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

//            Log.e(TAG, "onReceive: " + status.getStatusCode());
                if (status != null) {
                    switch (status.getStatusCode()) {
                        case CommonStatusCodes.SUCCESS:
                            // Get SMS message contents
                            String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);


                            if (callBack != null) callBack.showOTPCode(parseCode(message));

//                            smsRetriver.unRegisterBroadcast();

//                            Log.e(TAG, "onReceive: content " + message + " : Code Is  : " + parseCode(message));
                            // Extract one-time code from the message and complete verification
                            // by sending the code back to your server.
                            break;
                        case CommonStatusCodes.TIMEOUT:
                            // Waiting for SMS timed out (5 minutes)
                            // Handle the error ...

                            if (callBack != null)
                                callBack.onFailure(new Exception("Time out error"));

//                            smsRetriver.unRegisterBroadcast();
                            break;
                    }
                }
            }
        }
    }


//    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//            if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
//
////                Toast.makeText(context, "Local Called", Toast.LENGTH_SHORT).show();
//
//                Bundle extras = intent.getExtras();
//                Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
//
////            Log.e(TAG, "onReceive: " + status.getStatusCode());
//                if (status != null) {
//                    switch (status.getStatusCode()) {
//                        case CommonStatusCodes.SUCCESS:
//                            // Get SMS message contents
//                            String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
//
//
//                            if (callBack != null) callBack.showOTPCode(parseCode(message));
//
//                            unRegisterBroadcast();
//
////                            Log.e(TAG, "onReceive: content " + message + " : Code Is  : " + parseCode(message));
//                            // Extract one-time code from the message and complete verification
//                            // by sending the code back to your server.
//                            break;
//                        case CommonStatusCodes.TIMEOUT:
//                            // Waiting for SMS timed out (5 minutes)
//                            // Handle the error ...
//
//                            if (callBack != null)
//                                callBack.onFailure(new Exception("Time out error"));
//
//                            unRegisterBroadcast();
//                            break;
//                    }
//                }
//            }
//
//        }
//    };


    private static String parseCode(String message) {
        Pattern p = Pattern.compile("\\b\\d{6}\\b");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find()) {
            code = m.group(0);
        }
        return code;
    }
}
