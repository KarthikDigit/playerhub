package com.playerhub.ui.dashboard.messages;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.playerhub.R;
import com.playerhub.utils.CommonUtil;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class User extends AbstractItem<User, User.ViewHolder> implements Serializable, Comparable {

    private static final String TAG = "User";

    public String name;
    public long notification;
    public String team;
    public String type;
    public String token_id;
    //    public String description;
//    public String date_time;
//    public String count;
    public String id;
    public String icon;
    public long connection;

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        if (!super.equals(o)) return false;

        User user = (User) o;

        if (notification != user.notification) return false;
        if (connection != user.connection) return false;
        if (name != null ? !name.equals(user.name) : user.name != null) return false;
        if (team != null ? !team.equals(user.team) : user.team != null) return false;
        if (type != null ? !type.equals(user.type) : user.type != null) return false;
        if (id != null ? !id.equals(user.id) : user.id != null) return false;
        return icon != null ? icon.equals(user.icon) : user.icon == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (int) (notification ^ (notification >>> 32));
        result = 31 * result + (team != null ? team.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (icon != null ? icon.hashCode() : 0);
        result = 31 * result + (int) (connection ^ (connection >>> 32));
        return result;
    }

    public String getTypeName() {

        return type;

    }

    public String getToken_id() {
        return token_id;
    }

    public void setToken_id(String token_id) {
        this.token_id = token_id;
    }

    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.root;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.message_item;
    }

    @Override
    public ViewHolder getViewHolder(@NonNull View v) {
        return new ViewHolder(v);
    }

    @Override
    public int compareTo(@NonNull Object o) {
        User compare = (User) o;

        if (compare.equals(this)) {
            return 0;
        }
        return 1;
    }


    /**
     * our ViewHolder
     */
    protected static class ViewHolder extends FastAdapter.ViewHolder<User> {

        @BindView(R.id.icon)
        CircleImageView icon;
        @BindView(R.id.date_time)
        TextView dateTime;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.description)
        TextView description;
        @BindView(R.id.count)
        TextView count;
        @BindView(R.id.count_lay)
        RelativeLayout countLay;
        @BindView(R.id.root)
        RelativeLayout root;
        @BindView(R.id.useris_online)
        View userIsOnline;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        public void bindView(final User item, List<Object> payloads) {

//            StringHolder.applyToOrHide(item.description, description);

//            description.setText(item.team);
            count.setText(item.notification + "");
            name.setText(item.name);
//            dateTime.setText(item.date_time);

            countLay.setVisibility(View.INVISIBLE);
            if (item.notification == 0) {
                countLay.setVisibility(View.INVISIBLE);
            }

//            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CONVERSATION).child(Preferences.INSTANCE.getMsgUserId()).getRef();
            Handler uiHandler = new Handler(Looper.getMainLooper());
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (item.icon != null && item.icon.length() > 0)
                        Picasso.get().load(item.icon).placeholder(R.mipmap.ic_launcher).resize(120, 120).into(icon);

                }
            });

            CommonUtil.checkIsInOnline(item.id, new CommonUtil.ObserverListener<Boolean>() {
                @Override
                public void onNext(Boolean value) {

                    userIsOnline.setVisibility(value ? View.VISIBLE : View.GONE);

                }

            });


            CommonUtil.getMessageCount(item.id, new CommonUtil.ObserverListener<Long>() {
                @Override
                public void onNext(Long value) {

                    item.notification = value;
                    count.setText(String.format(Locale.ENGLISH, "%d", item.notification));
                    countLay.setVisibility(View.VISIBLE);
                    if (item.notification == 0) {
                        countLay.setVisibility(View.INVISIBLE);
                    }


                }
            });


//            Observable.create(new ObservableOnSubscribe<Long>() {
//
//                @Override
//                public void subscribe(final ObservableEmitter<Long> e) {
//
//                    final List<String> users = new ArrayList<>();
//                    users.add(item.id);
//                    users.add(Preferences.INSTANCE.getMsgUserId());
//                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                            long c = 0;
//
//                            for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {
//                                Conversations value = dataSnapshotChild.getValue(Conversations.class);
//
//                                try {
//                                    if (value != null && value.getUsers().containsAll(users)) {
//
//                                        long v = value.getUnread();
//                                        c += v;
//                                    }
//                                } catch (NullPointerException e) {
//
//                                }
//
//                            }
//
//                            e.onNext(c);
//
//
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//
//                            Log.e(TAG, "onCancelled: message count " + databaseError.getMessage());
//
//                        }
//                    });

//
//                }
//            }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Long>() {
//                @Override
//                public void onSubscribe(Disposable d) {
//
//                }
//
//                @Override
//                public void onNext(final Long value) {
//
//                    count.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            count.setText(String.format(Locale.ENGLISH, "%d", value));
//                            countLay.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    countLay.setVisibility(value > 0 ? View.VISIBLE : View.INVISIBLE);
//                                }
//                            });
//
//                        }
//                    });
//
//                }
//
//                @Override
//                public void onError(Throwable e) {
//
//                }
//
//                @Override
//                public void onComplete() {
//
//                }
//            });


//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//
//                    Handler handler = new Handler(Looper.getMainLooper());
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            final List<String> users = new ArrayList<>();
//                            users.add(item.id);
//                            users.add(Preferences.INSTANCE.getMsgUserId());
//                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                                    long c = 0;
//
//                                    for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {
//                                        Conversations value = dataSnapshotChild.getValue(Conversations.class);
//
//                                        try {
//                                            if (value != null && value.getUsers().containsAll(users)) {
//
//                                                long v = value.getUnread();
//                                                c += v;
//                                            }
//                                        } catch (NullPointerException e) {
//
//                                        }
//
//                                    }
//
//                                    final long finalC = c;
//                                    count.post(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            count.setText(String.format(Locale.ENGLISH, "%d", finalC));
//                                            countLay.post(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    countLay.setVisibility(finalC > 0 ? View.VISIBLE : View.INVISIBLE);
//                                                }
//                                            });
//
//                                        }
//                                    });
//
//
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//
//                                    Log.e(TAG, "onCancelled: message count " + databaseError.getMessage());
//
//                                }
//                            });
//
//
//                        }
//                    });
//
//                }
//            }).start();


        }

        @Override
        public void unbindView(User item) {
            description.setText(null);
            count.setText(null);
            dateTime.setText(null);
            name.setText(null);
        }
    }


}