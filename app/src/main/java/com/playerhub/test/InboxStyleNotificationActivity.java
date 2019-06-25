package com.playerhub.test;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.playerhub.R;
import com.playerhub.notification.MyNotificationManager;
import com.playerhub.preference.Preferences;

public class InboxStyleNotificationActivity extends AppCompatActivity {


    private String title = "Test Notification";
    private String body = "Test Notification ";
    private String type = "Chat ";
    private String id = "23";
    private static int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox_style_notification);


        if (getIntent() != null) {

            if (getIntent().hasExtra("id")) {

                String id = getIntent().getStringExtra("id");

                if (id != null) {

                    Preferences.INSTANCE.saveNotification(id, null);

                }

            }
        }

    }

    public void onCallNotification(View view) {


        count++;

        body = body + " " + count;

        MyNotificationManager.getInstance(this).displayNotification(title, body, type, id);

    }

    @Override
    protected void onResume() {
        super.onResume();


// Clear all notification
//        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        if (nMgr != null) {
//            nMgr.cancelAll();
//        }
    }
}
