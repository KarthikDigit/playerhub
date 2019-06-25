package com.playerhub.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.playerhub.notification.Constants;
import com.playerhub.notification.MyNotificationManager;

import static android.support.constraint.Constraints.TAG;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        Log.e(TAG, "onMessageReceived: " + new Gson().toJson(remoteMessage.getNotification()));

        if (remoteMessage.getData().size() > 0) {
            //handle the data message here

            Log.e(TAG, "onMessageReceived: " + new Gson().toJson(remoteMessage.getData()));


            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");
            String type = remoteMessage.getData().get("type");
            String id = remoteMessage.getData().get("id");

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_NAME, importance);
                mChannel.setDescription(Constants.CHANNEL_DESCRIPTION);
                mChannel.enableLights(true);
                mChannel.setLightColor(Color.RED);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                if (mNotificationManager != null) {
                    mNotificationManager.createNotificationChannel(mChannel);
                }
            }

            /*https://gadgets.ndtv.com/samsung-galaxy-on8-2018-5569
             * Displaying a notification locally
             */
            if (!TextUtils.isEmpty(type) && !TextUtils.isEmpty(id)) {
                if (type.toLowerCase().equalsIgnoreCase("singlechat")) {

                    MyNotificationManager.getInstance(this).displayNotificationInBoxStyle(title, body, type, id);

                } else {
//                    MyNotificationManager.getInstance(this).displayNotificationInBoxStyle(title, body, type, id);

                    MyNotificationManager.getInstance(this).displayNotification(title, body, "", "");
                }
            } else {
                MyNotificationManager.getInstance(this).displayNotification(title, body, "", "");
            }

        }

//        //getting the title and the body
//        String title = remoteMessage.getNotification().getTitle();
//        String body = remoteMessage.getNotification().getBody();
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            NotificationManager mNotificationManager =
//                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            int importance = NotificationManager.IMPORTANCE_HIGH;
//            NotificationChannel mChannel = new NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_NAME, importance);
//            mChannel.setDescription(Constants.CHANNEL_DESCRIPTION);
//            mChannel.enableLights(true);
//            mChannel.setLightColor(Color.RED);
//            mChannel.enableVibration(true);
//            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
//            mNotificationManager.createNotificationChannel(mChannel);
//        }
//
//        /*
//         * Displaying a notification locally
//         */
//        MyNotificationManager.getInstance(this).displayNotification(title, body);


    }
}
