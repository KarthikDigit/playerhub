package com.playerhub.network.response;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.playerhub.R;
import com.playerhub.notification.Constants;
import com.playerhub.preference.Preferences;
import com.playerhub.ui.dashboard.messages.Conversations;
import com.playerhub.ui.dashboard.messages.User;
import com.playerhub.utils.CommonUtil;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.support.constraint.Constraints.TAG;

public class ContactListApi implements Serializable {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;
    @SerializedName("message")
    @Expose
    private String message;
    private final static long serialVersionUID = -1138728350625785801L;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public static class Datum extends AbstractItem<Datum, Datum.ViewHolder> implements Serializable {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("avatar")
        @Expose
        private String avatar;
        @SerializedName("type")
        @Expose
        private String type1;
        @SerializedName("team")
        @Expose
        private String team;
        @SerializedName("device_model")
        @Expose
        private String deviceModel;
        @SerializedName("token_id")
        @Expose
        private String tokenId;
        @SerializedName("device_id")
        @Expose
        private String deviceId;

        private boolean isChecked=false;

        private long notification = 0;

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }

        public long getNotification() {
            return notification;
        }

        public void setNotification(long notification) {
            this.notification = notification;
        }

        private final static long serialVersionUID = 6006717469236728819L;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public String getDeviceModel() {
            return deviceModel;
        }

        public void setDeviceModel(String deviceModel) {
            this.deviceModel = deviceModel;
        }

        public String getTokenId() {
            return tokenId;
        }

        public void setTokenId(String tokenId) {
            this.tokenId = tokenId;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUserID() {

            byte[] userId = String.valueOf(getId()).getBytes();

            return Base64.encodeToString(userId, Base64.NO_WRAP);

        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getType1() {
            return type1;
        }

        public void setType1(String type1) {
            this.type1 = type1;
        }

        public String getTeam() {
            return team;
        }

        public void setTeam(String team) {
            this.team = team;
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


        /**
         * our ViewHolder
         */
        protected static class ViewHolder extends FastAdapter.ViewHolder<Datum> {

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
            public void bindView(final Datum item, List<Object> payloads) {

//            StringHolder.applyToOrHide(item.description, description);

//            description.setText(item.team);
//                count.setText(item.notification + "");
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
                        if (item.avatar != null && item.avatar.length() > 0)
                            Picasso.get().load(item.avatar).placeholder(R.mipmap.ic_launcher).resize(120, 120).into(icon);

                    }
                });


                CommonUtil.checkIsInOnline(item.getUserID(), new CommonUtil.ObserverListener<Boolean>() {
                    @Override
                    public void onNext(Boolean value) {

                        userIsOnline.setVisibility(value ? View.VISIBLE : View.GONE);

                    }

                });


                CommonUtil.getMessageCount(item.getUserID(), new CommonUtil.ObserverListener<Long>() {
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


            }

            @Override
            public void unbindView(Datum item) {
                description.setText(null);
                count.setText(null);
                dateTime.setText(null);
                name.setText(null);
            }
        }

    }

}