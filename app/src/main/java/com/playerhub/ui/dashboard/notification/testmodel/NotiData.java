package com.playerhub.ui.dashboard.notification.testmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

class NotiData extends Noti implements Serializable {

    @SerializedName("notifications_count")
    @Expose
    private Integer notificationsCount;
    @SerializedName("notifications")
    @Expose
    private List<NotificatinOr> notifications = null;
    private final static long serialVersionUID = -2867125031968082160L;

    public Integer getNotificationsCount() {
        return notificationsCount;
    }

    public void setNotificationsCount(Integer notificationsCount) {
        this.notificationsCount = notificationsCount;
    }

    public List<NotificatinOr> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<NotificatinOr> notifications) {
        this.notifications = notifications;
    }

}
