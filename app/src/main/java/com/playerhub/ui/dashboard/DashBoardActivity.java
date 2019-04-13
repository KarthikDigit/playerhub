package com.playerhub.ui.dashboard;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.google.gson.Gson;
import com.playerhub.R;
import com.playerhub.notification.Constants;
import com.playerhub.preference.Preferences;
import com.playerhub.ui.base.BaseActivity;
import com.playerhub.ui.dashboard.home.HomeFragment;
import com.playerhub.ui.dashboard.home.eventdetails.EventDetailsFragment;
import com.playerhub.ui.dashboard.home.moreevent.MoreAnnouncementFragment;
import com.playerhub.ui.dashboard.home.moreevent.MoreEventsFragment;
import com.playerhub.ui.dashboard.messages.Conversations;
import com.playerhub.ui.dashboard.messages.MessagesFragment;
import com.playerhub.ui.dashboard.settings.SettingsFragment;
import com.playerhub.ui.dashboard.store.StoreFragment;
import com.playerhub.ui.dashboard.videos.VideosFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Optional;

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


    }


    private void addFragment() {

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new HomeFragment());
        fragmentList.add(MoreEventsFragment.getInstance(true));
        fragmentList.add(new MessagesFragment());
        fragmentList.add(new SettingsFragment());
        fragmentList.add(new MoreAnnouncementFragment());
        manger.addFragment(fragmentList);


    }

//
//    public void callFragmentFromOutSide(int id) {
//
//
//        switch (id) {
//
//            case R.id.navigation_settings:
//
//                if (activeFragment != settingsFragment)
//                    fm.beginTransaction().show(settingsFragment).commit();
//                else
//                    fm.beginTransaction().hide(activeFragment).show(settingsFragment).commit();
//                activeFragment = homeFragment;
//
//                navigation.setSelectedItemId(R.id.navigation_settings);
//
//                break;
//
//        }
//
//
//    }


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


    private void remove() {

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content);

        if (fragment instanceof EventDetailsFragment) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    public void callFragmentFromOutSide(Fragment fragment) {

        if (fragment instanceof MoreEventsFragment) {

            showMoreEventDetails(false);
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

                try {
                    long count = 0;

                    for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {
                        Conversations value = dataSnapshotChild.getValue(Conversations.class);
                        long v = value != null ? value.getUnread() : 0;
                        count = v + count;
                    }

                    setNotificationCount(count);


                    if (totalCount != count) {

                        Fragment fragment = manger.getActive();

                        if (fragment instanceof MessagesFragment) {

                            ((MessagesFragment) fragment).updateAdapter();
                        }
                    }


                    totalCount = count;
                } catch (DatabaseException e) {

                    Log.e(TAG, "onDataChange: " + e.getMessage());
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


                Log.e(TAG, "onCancelled: message count " + databaseError.getMessage());

            }
        });


    }


}