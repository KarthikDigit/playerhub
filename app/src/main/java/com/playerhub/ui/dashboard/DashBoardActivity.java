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
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.playerhub.R;
import com.playerhub.notification.Constants;
import com.playerhub.preference.Preferences;
import com.playerhub.ui.base.BaseActivity;
import com.playerhub.ui.dashboard.home.HomeFragment;
import com.playerhub.ui.dashboard.home.eventdetails.EventDetailsFragment;
import com.playerhub.ui.dashboard.home.announcement.MoreAnnouncementFragment;
import com.playerhub.ui.dashboard.home.moreevent.MoreEventsFragment;
import com.playerhub.ui.dashboard.messages.Conversations;
import com.playerhub.ui.dashboard.messages.MessagesFragment;
import com.playerhub.ui.dashboard.settings.SettingsFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class DashBoardActivity extends BaseActivity {

    private static final String TAG = "DashBoardActivity";

    private Animation blink;
    private BottomNavigationView navigation;
    TextView notificationsBadge;

    private FragmentManger manger;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            remove();
            switch (item.getItemId()) {

                case R.id.navigation_home:
                    manger.showFragment(0);
                    return true;
                case R.id.navigation_message:
                    manger.showFragment(2);
                    return true;
                case R.id.navigation_store:
                    showMoreEventDetails(true);
                    return true;
                case R.id.navigation_settings:
                    manger.showFragment(3);
                    return true;

            }
            return false;
        }
    };


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

        manger = new FragmentManger(getSupportFragmentManager(), R.id.content);

        addFragment();

        phoneStatePermission();

        getMessageCountFromFirebaseDatabase();

        clearNotification();
    }


    private void clearNotification() {

        if (getIntent() != null) {

            if (getIntent().hasExtra("id")) {

                String id = getIntent().getStringExtra("id");

                if (id != null) {
                    Preferences.INSTANCE.saveNotification(id, null);

                    if (getIntent().hasExtra("type")) {

                        String type = getIntent().getStringExtra("type");

                        if (type.toLowerCase().equalsIgnoreCase("chat")) {

                            manger.showFragment(2);
                        }
                    }
                }

            }
        }

    }


    public void addFragment() {


        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {

            getSupportFragmentManager().popBackStack();
        }


        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new HomeFragment());
        fragmentList.add(MoreEventsFragment.getInstance(true, true));
        fragmentList.add(new MessagesFragment());
        fragmentList.add(new SettingsFragment());
        fragmentList.add(new MoreAnnouncementFragment());
        manger.addFragment(fragmentList);


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

        } else if (!manger.checkActive()) {
            manger.showFragment(0);
            navigation.setSelectedItemId(R.id.navigation_home);
        } else {

            super.onBackPressed();
        }


    }


    private void showMoreEventDetails(boolean isBackEnabled) {

        manger.showFragment(1);

        if (manger.getActive() instanceof MoreEventsFragment) {
            ((MoreEventsFragment) manger.getActive()).showHideBackButton(isBackEnabled);
        }

    }

    private void showMoreEventDetails(boolean isBackEnabled, boolean isToday) {

        manger.showFragment(1);

        if (manger.getActive() instanceof MoreEventsFragment) {
            ((MoreEventsFragment) manger.getActive()).showHideBackButton(isBackEnabled);

            ((MoreEventsFragment) manger.getActive()).enableToday(isToday);
        }

    }


    private void remove() {

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content);

        if (fragment instanceof EventDetailsFragment) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    public void callFragmentFromOutSide(Fragment fragment) {

        if (fragment instanceof MoreEventsFragment) {

            getSupportFragmentManager().beginTransaction().add(R.id.content, fragment).addToBackStack(fragment.getClass().getName()).commit();

            ((MoreEventsFragment) fragment).showHideBackButton(false);
//            showMoreEventDetails(false);
        } else if (fragment instanceof EventDetailsFragment) {
            getSupportFragmentManager().beginTransaction().add(R.id.content, fragment).addToBackStack(fragment.getClass().getName()).commit();
        } else
            manger.showFragment(4);
//            getSupportFragmentManager().beginTransaction().add(R.id.content, fragment).addToBackStack(fragment.getClass().getName()).commit();

    }

    public void callFragmentFromOutSide(Fragment fragment, boolean isToday) {

        if (fragment instanceof MoreEventsFragment) {

            showMoreEventDetails(false, isToday);
        } else if (fragment instanceof EventDetailsFragment) {
            getSupportFragmentManager().beginTransaction().add(R.id.content, fragment).addToBackStack(fragment.getClass().getName()).commit();
        } else
            manger.showFragment(4);
//            getSupportFragmentManager().beginTransaction().add(R.id.content, fragment).addToBackStack(fragment.getClass().getName()).commit();

    }


    public void setNotificationCount(final long count) {

        if (notificationsBadge != null) {
            notificationsBadge.setVisibility(View.VISIBLE);
            String c = count >= 10 ? "9+" : count + "";
            notificationsBadge.setText(c);

            notificationsBadge.setVisibility(count > 0 ? View.VISIBLE : View.GONE);

//            blink.setAnimationListener(new Animation.AnimationListener() {
//                @Override
//                public void onAnimationStart(Animation animation) {
//
//                }
//
//                @Override
//                public void onAnimationEnd(Animation animation) {
//
//                }
//
//                @Override
//                public void onAnimationRepeat(Animation animation) {
//
//                }
//            });
//
//            notificationsBadge.startAnimation(blink);
        } else {
            Log.e(TAG, "setNotificationCount: notificationsBadge not init ");
        }
    }


    private long totalCount = 0;

    private void getMessageCountFromFirebaseDatabase() {


        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();


        databaseReference
                .child(Constants.ARG_CONVERSATION)
                .child(Preferences.INSTANCE.getMsgUserId())
                .getRef()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                        getNotificationCount(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }


    private void getNotificationCount(DataSnapshot dataSnapshot) {

//        try {
        long count = 0;

        for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {
            try {

                Conversations value = dataSnapshotChild.getValue(Conversations.class);
                long v = value != null ? value.getUnread() : 0;
                count = v + count;

            } catch (DatabaseException e) {

                Log.e(TAG, "getNotificationCount: " + e.getMessage());
            }

        }

        setNotificationCount(count);


        if (totalCount != count) {

            Fragment fragment = manger.getActive();

            if (fragment instanceof MessagesFragment) {

                ((MessagesFragment) fragment).updateAdapter();
            }
        }


        totalCount = count;
//        } catch (DatabaseException e) {
//            setNotificationCount(0);
//            Log.e(TAG, "onDataChange: " + e.getMessage());
//        }

    }


}
