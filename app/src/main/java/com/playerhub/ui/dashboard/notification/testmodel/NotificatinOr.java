package com.playerhub.ui.dashboard.notification.testmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificatinOr extends NotiData {


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

    @Override
    public String toString() {
        return "NotificatinOr{" +
                "id=" + id +
                ", eventId=" + eventId +
                ", paidEventId=" + paidEventId +
                ", announcementId=" + announcementId +
                ", description='" + description + '\'' +
                ", seen=" + seen +
                ", type='" + type + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", timeago='" + timeago + '\'' +
                '}';
    }

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

    public String getType() {
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
}
