package com.playerhub.network.response;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.playerhub.R;
import com.playerhub.ui.dashboard.notification.NotificationModel;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationApi implements Serializable {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("message")
    @Expose
    private String message;
    private final static long serialVersionUID = 7781015841653165246L;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public static class Data implements Serializable {

        @SerializedName("notifications_count")
        @Expose
        private Integer notificationsCount;
        @SerializedName("notifications")
        @Expose
        private List<Notification> notifications = null;
        private final static long serialVersionUID = -2867125031968082160L;

        public Integer getNotificationsCount() {
            return notificationsCount;
        }

        public void setNotificationsCount(Integer notificationsCount) {
            this.notificationsCount = notificationsCount;
        }

        public List<Notification> getNotifications() {
            return notifications;
        }

        public void setNotifications(List<Notification> notifications) {
            this.notifications = notifications;
        }


        public static class Notification extends AbstractItem<Notification, Notification.ViewHolder> implements Serializable {

            @SerializedName("id")
            @Expose
            private Integer id;
            @SerializedName("event_id")
            @Expose
            private Integer eventId;
            @SerializedName("paid_event_id")
            @Expose
            private Object paidEventId;
            @SerializedName("announcement_id")
            @Expose
            private Object announcementId;
            @SerializedName("description")
            @Expose
            private String description;
            @SerializedName("seen")
            @Expose
            private Integer seen;
            @SerializedName("type")
            @Expose
            private String type;
            @SerializedName("created_at")
            @Expose
            private String createdAt;
            @SerializedName("timeago")
            @Expose
            private String timeago;
            private final static long serialVersionUID = 4399286066126918815L;

            public Integer getId() {
                return id;
            }

            public void setId(Integer id) {
                this.id = id;
            }

            public Integer getEventId() {
                return eventId;
            }

            public void setEventId(Integer eventId) {
                this.eventId = eventId;
            }

            public Object getPaidEventId() {
                return paidEventId;
            }

            public void setPaidEventId(Object paidEventId) {
                this.paidEventId = paidEventId;
            }

            public Object getAnnouncementId() {
                return announcementId;
            }

            public void setAnnouncementId(Object announcementId) {
                this.announcementId = announcementId;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public Integer getSeen() {
                return seen;
            }

            public void setSeen(Integer seen) {
                this.seen = seen;
            }

            public String getNotificationType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getCreatedAt() {
                return createdAt;
            }

            public void setCreatedAt(String createdAt) {
                this.createdAt = createdAt;
            }

            public String getTimeago() {
                return timeago;
            }

            public void setTimeago(String timeago) {
                this.timeago = timeago;
            }


            public String getType1() {
                return type;
            }

            //The unique ID for this type of item
            @Override
            public int getType() {
                return R.id.root;
            }

            //The layout to be used for this type of item
            @Override
            public int getLayoutRes() {
                return R.layout.notification_item;
            }

            @Override
            public ViewHolder getViewHolder(@NonNull View v) {
                return new ViewHolder(v);
            }

            /**
             * our ViewHolder
             */

            protected static class ViewHolder extends FastAdapter.ViewHolder<Notification> {

                @BindView(R.id.description)
                TextView description;

                public ViewHolder(View view) {
                    super(view);
                    ButterKnife.bind(this, view);
                }

                @Override
                public void bindView(Notification item, List<Object> payloads) {

//            StringHolder.applyToOrHide(item.description, description);

                    description.setText(item.getDescription());
                }

                @Override
                public void unbindView(Notification item) {
                    description.setText(null);
                }
            }

        }

    }


}