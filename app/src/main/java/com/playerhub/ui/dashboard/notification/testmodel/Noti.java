package com.playerhub.ui.dashboard.notification.testmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.playerhub.network.response.NotificationApi;

import java.io.Serializable;

public class Noti implements Serializable {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private NotiData data;
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

    public NotiData getData() {
        return data;
    }

    public void setData(NotiData data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
