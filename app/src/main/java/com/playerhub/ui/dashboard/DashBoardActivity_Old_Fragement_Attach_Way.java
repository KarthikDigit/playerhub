package com.playerhub.ui.dashboard;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.playerhub.R;
import com.playerhub.notification.Constants;
import com.playerhub.preference.Preferences;
import com.playerhub.ui.base.BaseActivity;
import com.playerhub.ui.dashboard.home.HomeFragment;
import com.playerhub.ui.dashboard.home.moreevent.MoreEventsFragment;
import com.playerhub.ui.dashboard.messages.Conversations;
import com.playerhub.ui.dashboard.messages.MessagesFragment;
import com.playerhub.ui.dashboard.settings.SettingsFragment;

import butterknife.ButterKnife;

public class DashBoardActivity_Old_Fragement_Attach_Way extends BaseActivity {

    private static final String TAG = "DashBoardActivity";

    private Animation blink;
    private BottomNavigationView navigation;
    TextView notificationsBadge;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    callFragment(new HomeFragment());
                    return true;

                case R.id.navigation_message:

                    callFragment(new MessagesFragment());

//                    setNotificationCount(0);
                    return true;
                case R.id.navigation_store:

                    callFragment(MoreEventsFragment.getInstance(true));

                    return true;
//                case R.id.navigation_videos:
//
//                    callFragment(new VideosFragment());

//                    return true;
                case R.id.navigation_settings:

                    callFragment(new SettingsFragment());

                    return true;

            }
            return false;
        }
    };


    private final Fragment homeFragment = new HomeFragment();
    private final Fragment messagesFragment = new MessagesFragment();
    private final Fragment moreEventsFragment = MoreEventsFragment.getInstance(true);
    private final Fragment settingsFragment = new SettingsFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_dash_board);
        ButterKnife.bind(this);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) navigation.getChildAt(0);
        View v = bottomNavigationMenuView.getChildAt(2);
        BottomNavigationItemView itemView = (BottomNavigationItemView) v;

        View badge = LayoutInflater.from(this)
                .inflate(R.layout.notification_count_item, bottomNavigationMenuView, false);

        notificationsBadge = (TextView) badge.findViewById(R.id.notifications_badge);

        blink = AnimationUtils.loadAnimation(this, R.anim.blink);

        itemView.addView(badge);


//        setNotificationCount(15);

        callFragment(new HomeFragment());


        phoneStatePermission();

        getMessageCountFromFirebaseDatabase();


//        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
//
//        final List<String> users = new ArrayList<>();
//
//        users.add("NzU=");
//        users.add(Preferences.INSTANCE.getMsgUserId());
//
//
//        databaseReference.child(Constants.ARG_CONVERSATION).child(Preferences.INSTANCE.getMsgUserId()).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {
////                    Conversations value = dataSnapshotChild.getValue(Conversations.class);
////////                    messagesList.add(value);
//////                    Log.e(TAG, "onDataChange: " + new Gson().toJson(value));
////
////                    List<String> ne = new ArrayList<>();
////                    ne.add(users.get(1));
////                    ne.add(users.get(0));
////
////                    try {
////                        if (value != null && (value.getUsers().containsAll(users))) {
////
////                            Log.e(TAG, "onDataChange: there ");
////                        }
////                    } catch (NullPointerException e) {
////                        Log.e(TAG, "onDataChange: " + e.getMessage());
////                    }
//
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                hideLoading();
//
//                Log.e(TAG, "onCancelled: create conversation error  " + databaseError.getMessage());
//
//            }
//        });


    }


    public void callFragmentFromOutSide(int id) {


        switch (id) {

            case R.id.navigation_settings:

                callFragment(new SettingsFragment());

                navigation.setSelectedItemId(R.id.navigation_settings);

                break;

        }


    }


    private void clearAllBackStack() {

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {

            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

    }


    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {

            getSupportFragmentManager().popBackStack();
        } else {

            super.onBackPressed();
        }


    }

    public void callFragmentFromOutSide(Fragment fragment) {


        getSupportFragmentManager().beginTransaction().add(R.id.content, fragment).addToBackStack(fragment.getClass().getName()).commit();

    }


    private void callFragment(Fragment fragment) {

        clearAllBackStack();

        if (!fragment.isAdded())
            getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();

    }


    public void setNotificationCount(final long count) {

        if (notificationsBadge != null) {
            String c = count >= 10 ? "9+" : count + "";
            notificationsBadge.setText(c);


            notificationsBadge.startAnimation(blink);

            blink.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    notificationsBadge.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });


        } else {
            Log.e(TAG, "setNotificationCount: notificationsBadge not init ");
        }
    }


    private long totalCount = 0;

    private void getMessageCountFromFirebaseDatabase() {


        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child(Constants.ARG_CONVERSATION).child(Preferences.INSTANCE.getMsgUserId()).getRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                long count = 0;

                for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {
                    Conversations value = dataSnapshotChild.getValue(Conversations.class);
                    long v = value != null ? value.getUnread() : 0;
                    count = v + count;
                }

                setNotificationCount(count);


                if (totalCount != count) {

                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content);

                    if (fragment instanceof MessagesFragment) {

                        ((MessagesFragment) fragment).updateAdapter();
                    }
                }


                totalCount = count;


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


                Log.e(TAG, "onCancelled: message count " + databaseError.getMessage());

            }
        });


    }


}
