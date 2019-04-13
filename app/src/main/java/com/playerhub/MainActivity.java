package com.playerhub;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.playerhub.common.Constant;
import com.playerhub.notification.Constants;
import com.playerhub.preference.Preferences;
import com.playerhub.ui.base.BaseActivity;
import com.playerhub.ui.dashboard.messages.Conversations;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Log.e(TAG, "onCreate: " + Preferences.INSTANCE.getMsgUserId());

        DatabaseReference reference1 = reference.child(Constants.ARG_CONVERSATION).child(Preferences.INSTANCE.getMsgUserId());

        final List<String> users = new ArrayList<>();

        users.add("NzY=");
        users.add("Mjk0");


        reference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.e(TAG, "onDataChange: " + new Gson().toJson(dataSnapshot.getValue()));


                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()
                        ) {


                    Conversations conversations = dataSnapshot1.getValue(Conversations.class);

                    if (conversations != null && conversations.getUsers() != null && conversations.getType().toLowerCase().equalsIgnoreCase("private") && users.containsAll(conversations.getUsers()))
                        Log.e(TAG, "onDataChange: " + new Gson().toJson(dataSnapshot1.getValue()));

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        View slideView = findViewById(R.id.slideView);
//
//
//        final SlideUp slideUp = new SlideUpBuilder(slideView)
////                .withStartState(SlideUp.State.SHOWED)
//                .withStartGravity(Gravity.TOP)
//
//                .withSlideFromOtherView(findViewById(R.id.rootview))
//                .withGesturesEnabled(true)
//                //.withHideSoftInputWhenDisplayed()
//                .withInterpolator(new LinearInterpolator())
//                //.withAutoSlideDuration()
//                .withLoggingEnabled(true)
//                //.withTouchableAreaPx()
////                .withTouchableAreaDp(100)
//                .withListeners()
//                //.withSavedState()
//                .build();


//        findViewById(R.id.rootview).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                slideUp.show();
//            }
//        });

//
//        String string = "24/09";
//        DateFormat format = new SimpleDateFormat("dd/mm", Locale.ENGLISH);
//        try {
//            Date date = format.parse(string);
//
//            date.getDay();
//            Calendar calendar = Calendar.getInstance();
//            calendar.set(Calendar.DAY_OF_MONTH, date.getDay());
//            calendar.set(Calendar.MONTH, date.getMonth());
//
//            Date date1 = new Date();
//            date1.setMonth(date.getMonth());
//
//            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
//            String strDate = dateFormat.format(calendar.getTime());
//
//            Log.e(TAG, "onCreate: date " + strDate);
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }


    }


}
