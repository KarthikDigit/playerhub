package com.playerhub.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class KidsAndCoaches implements Serializable {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("message")
    @Expose
    private String message;
    private final static long serialVersionUID = 6762520643198914788L;

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

        @SerializedName("kids")
        @Expose
        private List<Kid> kids = null;
        @SerializedName("coaches")
        @Expose
        private List<Coach> coaches = null;
        private final static long serialVersionUID = 4403709481706826618L;

        public List<Kid> getKids() {
            return kids;
        }

        public void setKids(List<Kid> kids) {
            this.kids = kids;
        }

        public List<Coach> getCoaches() {
            return coaches;
        }

        public void setCoaches(List<Coach> coaches) {
            this.coaches = coaches;
        }

    }

}