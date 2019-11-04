package com.playerhub.ui.dashboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.playerhub.R;
import com.playerhub.listener.Observer;
import com.playerhub.listener.Subject;
import com.playerhub.notification.Constants;
import com.playerhub.preference.Preferences;
import com.playerhub.trans.EventDetailsTransition;
import com.playerhub.ui.base.BaseActivity;
import com.playerhub.ui.dashboard.home.HomeFragment;
import com.playerhub.ui.dashboard.home.eventdetails.EventDetailsActivity;
import com.playerhub.ui.dashboard.home.eventdetails.EventDetailsFragment;
import com.playerhub.ui.dashboard.home.announcement.MoreAnnouncementFragment;
import com.playerhub.ui.dashboard.home.moreevent.EventsFragment;
import com.playerhub.ui.dashboard.home.moreevent.EventsUpdatedFragment;
import com.playerhub.ui.dashboard.home.moreevent.MoreEventsFragment;
import com.playerhub.ui.dashboard.messages.Conversations;
import com.playerhub.ui.dashboard.messages.MessagesFragment;
import com.playerhub.ui.dashboard.settings.SettingsFragment;
import com.playerhub.ui.welcome.WelcomeActivity;
import com.playerhub.utils.NetworkHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class DashBoardActivity extends BaseActivity implements Subject {

    private static final String TAG = "DashBoardActivity";
    private IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");

    private Animation blink;
    private BottomNavigationView navigation;
    private TextView notificationsBadge;
    private String currentVersion = "";
    private FragmentManger manger;
    private BottomNavigationMenuView bottomNavigationMenuView;
    private static boolean isFirstTimeUpdateCall = true;

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

                case R.id.navigation_announcement:
                    manger.showFragment(4);
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


        try {

            currentVersion = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0).versionName;

            Log.e("Current Version", "::" + currentVersion);

            if (isFirstTimeUpdateCall) {

                new GetVersionCode(currentVersion, this).execute();

                isFirstTimeUpdateCall = false;
            }

        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();

            Log.e(TAG, "onCreate: " + e.getMessage());
        }


        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        bottomNavigationMenuView =
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


//        setTargetView();


//        if (getIntent() != null) {
//
//            String type = getIntent().getStringExtra("type");
//
//
//            showToast("Notification type  " + type);
//
//            Log.e(TAG, "onCreate:Notification "+type );
//
//        }

        clearNotification();
        setupWindowAnimation();
    }


    private void setupWindowAnimation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Explode fade = new Explode();
            fade.setDuration(1000);
            getWindow().setEnterTransition(fade);

//            Slide slide = new Slide();
//            slide.setSlideEdge(Gravity.BOTTOM);
//            slide.setDuration(1000);
//            getWindow().setReturnTransition(slide);
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        clearNotification();
    }

    private void setTargetView() {

        new TapTargetSequence(this)
                .targets(
                        TapTarget.forView(navigation.findViewById(R.id.navigation_store), "Events", "You can see all your schedules and upcoming events"),
                        TapTarget.forView(navigation.findViewById(R.id.navigation_message), "Messages", "You can send msgs to coaches and parents from here").cancelable(false)

                ).start();


    }


    private void clearNotification() {


//        showToast("Called");

//        boolean b = (getIntent().getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) == 0;
//
//        if (b) {

        Intent intent = getIntent();

//        setIntent(new Intent());

//        Log.e(TAG, "clearNotification: " + intent.hasExtra("id") + "  " + intent.hasExtra("type")+" "+intent.getStringExtra("type"));

        if (intent != null && intent.hasExtra("id") && intent.hasExtra("type")) {

            String id = intent.getStringExtra("id");
            String type = intent.getStringExtra("type");

//            Log.e(TAG, "clearNotification: " + id + "   " + type);

//            showToast(type + "   " + id);


            if (id != null && !TextUtils.isEmpty(id) && !TextUtils.isEmpty(type)) {

                Preferences.INSTANCE.saveNotification(id, null);


                if (type.toLowerCase().equalsIgnoreCase("chat")) {

                    manger.showFragment(2);

                    navigation.setSelectedItemId(R.id.navigation_message);

                } else if (type.toLowerCase().equalsIgnoreCase("event")) {
//                                getIntent().removeExtra("type");
//                                getIntent().removeExtra("id");
//                                setIntent(new Intent());

//
//                                getIntent().putExtra("type", "");
//                                getIntent().putExtra("id", "");
                    startActivity(EventDetailsActivity.getIntent(this, Integer.parseInt(id)));

                }


            }

        }
//        }


    }


    public void showAnnouncement() {

        manger.showFragment(4);

        navigation.getMenu().findItem(R.id.navigation_announcement).setChecked(true);


    }

    public void showAllEvent() {

        showMoreEventDetails(true);

        navigation.getMenu().findItem(R.id.navigation_store).setChecked(true);


    }


    public void addFragment() {


        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {

            getSupportFragmentManager().popBackStack();

        }


        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new HomeFragment());
//        fragmentList.add(MoreEventsFragment.getInstance(true, true));
//        fragmentList.add(new EventsFragment());
        fragmentList.add(new EventsUpdatedFragment());
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


    public void showMoreEventDetails(boolean isBackEnabled) {

        manger.showFragment(1);

        navigation.getMenu().findItem(R.id.navigation_store).setChecked(true);

//        if (manger.getActive() instanceof MoreEventsFragment) {
//            ((MoreEventsFragment) manger.getActive()).showHideBackButton(isBackEnabled);
//        }
//        if (manger.getActive() instanceof EventsFragment) {
//            ((EventsFragment) manger.getActive());
//        }
//
//        showToast("Called " + isBackEnabled);

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void callFragmentFromOutSide(Fragment fragment) {

        fragment.setSharedElementEnterTransition(new EventDetailsTransition());
        fragment.setEnterTransition(new Slide());
        fragment.setExitTransition(new Slide());
        fragment.setSharedElementReturnTransition(new EventDetailsTransition());

        if (fragment instanceof MoreEventsFragment) {

//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .add(R.id.content, fragment)
//                    .setTransition(FragmentTransaction.TRANSIT_ENTER_MASK)
//                    .addToBackStack(fragment.getClass().getName())
//                    .commit();
//

            getSupportFragmentManager()
                    .beginTransaction()
//                    .setCustomAnimations(R.anim.slide_out_down, R.anim.slide_in_down)
                    .add(R.id.content, fragment)

                    .addToBackStack(fragment.getClass().getName())
                    .commit();


            ((MoreEventsFragment) fragment).showHideBackButton(false);
//            showMoreEventDetails(false);
        } else if (fragment instanceof EventDetailsFragment) {
            getSupportFragmentManager().beginTransaction()
//                    .setCustomAnimations(R.anim.slide_out_down, R.anim.slide_in_down)
                    .add(R.id.content, fragment)
                    .addToBackStack(fragment.getClass().getName()).commit();
        } else
            manger.showFragment(4);
//            getSupportFragmentManager().beginTransaction().add(R.id.content, fragment).addToBackStack(fragment.getClass().getName()).commit();

    }

    public void callFragmentFromOutSideWithAnimation(Fragment fragment) {

        if (fragment instanceof MoreEventsFragment) {

            // Defines enter transition for all fragment views
//            Slide slideTransition = new Slide(Gravity.RIGHT);
//            slideTransition.setDuration(1000);
//            sharedElementFragment2.setEnterTransition(slideTransition);
//
//            // Defines enter transition only for shared element
//            ChangeBounds changeBoundsTransition = TransitionInflater.from(this).inflateTransition(R.transition.change_bounds);
//            fragmentB.setSharedElementEnterTransition(changeBoundsTransition);

            getSupportFragmentManager().beginTransaction().add(R.id.content, fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack(fragment.getClass().getName()).commit();

            ((MoreEventsFragment) fragment).showHideBackButton(false);
//            showMoreEventDetails(false);
        } else if (fragment instanceof EventDetailsFragment) {
            getSupportFragmentManager().beginTransaction().add(R.id.content, fragment).addToBackStack(fragment.getClass().getName()).commit();
        } else
            manger.showFragment(4);
//            getSupportFragmentManager().beginTransaction().add(R.id.content, fragment).addToBackStack(fragment.getClass().getName()).commit();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void callFragmentFromOutSide(Fragment fragment, boolean isToday) {

        fragment.setSharedElementEnterTransition(new EventDetailsTransition());
        fragment.setEnterTransition(new Slide());
        fragment.setExitTransition(new Slide());
        fragment.setSharedElementReturnTransition(new EventDetailsTransition());

        if (fragment instanceof MoreEventsFragment) {

            showMoreEventDetails(false, isToday);
        } else if (fragment instanceof EventDetailsFragment) {
//            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_out_down, R.anim.slide_in_down).add(R.id.content, fragment).addToBackStack(fragment.getClass().getName()).commit();
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


//    @Override
//    public void onStart() {
//        super.onStart();
//
//
//        registerReceiver(broadcastReceiver, intentFilter);
//
////        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, intentFilter);
//    }
//
//
//    @Override
//    public void onStop() {
//        super.onStop();
//
//
//        unregisterReceiver(broadcastReceiver);
//
////        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
//    }
//
//    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            final String action = intent.getAction();
//            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
//
//                if (NetworkHelper.isNetworkAvailable(context)) {
//                    networkConnected();
//                }
//            }
//        }
//    };

    private void networkConnected() {

        notifyObservers();

    }

    private List<Observer> observers = new ArrayList<>();

    @Override
    public void register(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void unregister(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (final Observer observer : observers) {
            observer.networkConnected();
        }
    }


    private static class GetVersionCode extends AsyncTask<Void, String, String> {

        private String currentVersion;
        private WeakReference<Context> mContext;

        private GetVersionCode(String currentVersion, Context mContext) {
            this.currentVersion = currentVersion;
            this.mContext = new WeakReference<>(mContext);
        }

        @Override
        protected String doInBackground(Void... voids) {

            String newVersion = null;

            try {
                Document document = Jsoup.connect("https://play.google.com/store/apps/details?id=com.playerhub&hl=en")
//                Document document = Jsoup.connect("https://play.google.com/store/apps/details?id=" + context.getPackageName() + "&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get();
                if (document != null) {
                    Elements element = document.getElementsContainingOwnText("Current Version");
                    for (Element ele : element) {
                        if (ele.siblingElements() != null) {
                            Elements sibElemets = ele.siblingElements();
                            for (Element sibElemet : sibElemets) {
                                newVersion = sibElemet.text();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "doInBackground: " + e.getMessage());
            }
            return newVersion;

        }


        @Override

        protected void onPostExecute(String onlineVersion) {

            super.onPostExecute(onlineVersion);

            if (onlineVersion != null && !onlineVersion.isEmpty()) {

                if (onlineVersion.equals(currentVersion)) {

                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(mContext.get()).create();
                    alertDialog.setTitle("Update");
                    alertDialog.setIcon(R.mipmap.ic_launcher);
                    alertDialog.setMessage("New Update is available");

                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Update", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                mContext.get().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.playerhub")));
//                                mContext.get().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + mContext.get().getPackageName())));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                mContext.get().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.playerhub")));
//                                mContext.get().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName())));
                            }
                        }
                    });

                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    alertDialog.show();
                }

            }

            Log.e(TAG, "Current version " + currentVersion + "playstore version " + onlineVersion);

        }
    }
}
