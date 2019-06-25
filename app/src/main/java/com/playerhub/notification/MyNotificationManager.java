package com.playerhub.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;


import com.playerhub.R;
import com.playerhub.preference.Preferences;
import com.playerhub.ui.dashboard.DashBoardActivity;

import java.util.Random;

import static android.content.Context.NOTIFICATION_SERVICE;

public class MyNotificationManager {

    private Context mCtx;
    private static MyNotificationManager mInstance;
    NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

    private MyNotificationManager(Context context) {
        mCtx = context;
    }

    public static synchronized MyNotificationManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MyNotificationManager(context);
        }
        return mInstance;
    }

    public void displayNotificationInBoxStyle(String title, String body, String type, String id) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mCtx, Constants.CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setAutoCancel(true)
                        .setContentText(body);


        String s = Preferences.INSTANCE.getNotificationById(id);

        StringBuilder builder = new StringBuilder();

        if (s != null) {

            String[] split = s.split(",");

            for (String s1 : split
                    ) {
                builder.append(s1);
                builder.append(",");

            }


        }

        builder.append(body);
        builder.append(",");

        Preferences.INSTANCE.saveNotification(id, builder.toString());


        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        inboxStyle.setBigContentTitle(title);
        String s1 = Preferences.INSTANCE.getNotificationById(id);

        if (s1 != null) {

            String[] split = s1.split(",");


            if (split.length > 2) {

                inboxStyle.addLine(split[split.length - 2]);
                inboxStyle.addLine(split[split.length - 1]);
                inboxStyle.setSummaryText("+2 more");

            } else {

                for (String s3 : split
                        ) {
                    inboxStyle.addLine(s3);
                }

            }


        }


//        inboxStyle.setBigContentTitle(title);
//        inboxStyle.addLine(body);
//        inboxStyle.setSummaryText(body);
        mBuilder.setStyle(inboxStyle);

        /*
         *  Clicking on the notification will take us to this intent
         *  Right now we are using the MainActivity as this is the only activity we have in our application
         *  But for your project you can customize it as you want
         * */

        Intent resultIntent = new Intent(mCtx, DashBoardActivity.class);
        resultIntent.putExtra("type", type);
        resultIntent.putExtra("title", title);
        resultIntent.putExtra("body", body);
        resultIntent.putExtra("id", id);
        /*
         *  Now we will create a pending intent
         *  The method getActivity is taking 4 parameters
         *  All paramters are describing themselves
         *  0 is the request code (the second parameter)
         *  We can detect this code in the activity that will open by this we can get
         *  Which notification opened the activity
         * */
        PendingIntent pendingIntent = PendingIntent.getActivity(mCtx, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        /*
         *  Setting the pending intent to notification builder
         * */

        mBuilder.setContentIntent(pendingIntent);

        NotificationManager mNotifyMgr =
                (NotificationManager) mCtx.getSystemService(NOTIFICATION_SERVICE);

        /*
         * The first parameter is the notification id
         * better don't give a literal here (right now we are giving a int literal)
         * because using this id we can modify it later
         * */
        if (mNotifyMgr != null) {

            Random random = new Random();

            int n = random.nextInt(999);

            mNotifyMgr.notify(Integer.parseInt(id), mBuilder.build());
        }
    }

    public void displayNotification(String title, String body, String type, String id) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mCtx, Constants.CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setAutoCancel(true)
                        .setContentText(body);


        /*
         *  Clicking on the notification will take us to this intent
         *  Right now we are using the MainActivity as this is the only activity we have in our application
         *  But for your project you can customize it as you want
         * */

        Intent resultIntent = new Intent(mCtx, DashBoardActivity.class);
        resultIntent.putExtra("type", type);
        resultIntent.putExtra("id", id);
        /*
         *  Now we will create a pending intent
         *  The method getActivity is taking 4 parameters
         *  All paramters are describing themselves
         *  0 is the request code (the second parameter)
         *  We can detect this code in the activity that will open by this we can get
         *  Which notification opened the activity
         * */
        PendingIntent pendingIntent = PendingIntent.getActivity(mCtx, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        /*
         *  Setting the pending intent to notification builder
         * */

        mBuilder.setContentIntent(pendingIntent);

        NotificationManager mNotifyMgr =
                (NotificationManager) mCtx.getSystemService(NOTIFICATION_SERVICE);

        /*
         * The first parameter is the notification id
         * better don't give a literal here (right now we are giving a int literal)
         * because using this id we can modify it later
         * */
        if (mNotifyMgr != null) {

            Random random = new Random();

            int n = random.nextInt(999);

            mNotifyMgr.notify(n, mBuilder.build());
        }
    }
}
