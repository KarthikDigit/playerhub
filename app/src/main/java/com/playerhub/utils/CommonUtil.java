package com.playerhub.utils;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.playerhub.notification.Constants;
import com.playerhub.preference.Preferences;
import com.playerhub.ui.dashboard.messages.Conversations;
import com.playerhub.ui.dashboard.messages.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.support.constraint.Constraints.TAG;

public final class CommonUtil {

    private static final String TAG = "CommonUtil";

    public static void checkIsInOnline(final String userdId, final ObserverListener listener) {


        Observable.create(new ObservableOnSubscribe<Boolean>() {

            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) {

                FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS).child(userdId).getRef()
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                try {

                                    User user = dataSnapshot.getValue(User.class);

                                    if (user != null && user.connection == 1 && !user.id.equalsIgnoreCase(Preferences.INSTANCE.getMsgUserId())) {
                                        if (!e.isDisposed()) {
                                            e.onNext(true);
                                        }

                                        if (!e.isDisposed()) {
                                            e.onComplete();
                                        }
                                    } else {
                                        if (!e.isDisposed()) {
                                            e.onNext(false);
                                        }
                                        if (!e.isDisposed()) {
                                            e.onComplete();
                                        }
                                    }

                                } catch (NullPointerException e1) {
                                    if (!e.isDisposed()) {
                                        e.onNext(false);
                                    }
                                    if (!e.isDisposed()) {
                                        e.onComplete();
                                    }
                                    Log.e(TAG, "onDataChange: " + e1.getMessage());

                                } catch (DatabaseException e1) {
                                    if (!e.isDisposed()) {
                                        e.onNext(false);
                                    }
                                    if (!e.isDisposed()) {
                                        e.onComplete();
                                    }
                                    Log.e(TAG, "onDataChange: " + e1.getMessage());
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())

                .subscribe(listener);


    }


    public static void getGroupMessageCount(final String message_id, final CallBackCount callBackCount) {


        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CONVERSATION).child(Preferences.INSTANCE.getMsgUserId()).child(message_id).getRef()

                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        try {
                            Conversations value = dataSnapshot.getValue(Conversations.class);
                            callBackCount.showCount(value.getUnread());
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


    public static void getMessageCount(final String userdId, final ObserverListener listener) {

        Observable.create(new ObservableOnSubscribe<Long>() {

            @Override
            public void subscribe(final ObservableEmitter<Long> e) {

                final List<String> users = new ArrayList<>();
                users.add(userdId);
                users.add(Preferences.INSTANCE.getMsgUserId());

                FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CONVERSATION).child(Preferences.INSTANCE.getMsgUserId()).getRef()

                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                long c = 0;

                                for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {


                                    try {
                                        Conversations value = dataSnapshotChild.getValue(Conversations.class);
                                        if (value != null && !value.getType().toLowerCase().equalsIgnoreCase("group") && value.getUsers().containsAll(users)) {

                                            c = value.getUnread();
                                            break;
                                        }
                                    } catch (NullPointerException e) {

                                        Log.e(TAG, "onDataChange: " + e.getMessage());

                                    } catch (DatabaseException e) {
                                        Log.e(TAG, "onDataChange: " + e.getMessage());
                                    }

                                }


                                if (!e.isDisposed()) {
                                    e.onNext(c);
                                }

                                if (!e.isDisposed()) {
                                    e.onComplete();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(listener);

    }

    public interface CallBackCount {

        void showCount(long count);
    }

    public static abstract class ObserverListener<T> implements Observer<T> {

        @Override
        public void onComplete() {

        }

        @Override
        public void onError(Throwable e) {

            Log.e(TAG, "onError: " + e.getMessage());
        }

        @Override
        public void onSubscribe(Disposable d) {

        }
    }

}
