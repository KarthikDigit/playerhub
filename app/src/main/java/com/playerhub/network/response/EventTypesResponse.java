package com.playerhub.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EventTypesResponse {

    @SerializedName("success")
    @Expose
    public Boolean success;
    @SerializedName("data")
    @Expose
    public Data data;
    @SerializedName("message")
    @Expose
    public String message;

    public static class Data {

        @SerializedName("event_types")
        @Expose
        public List<EventType> eventTypes = null;

        public static class EventType {

            @SerializedName("id")
            @Expose
            public Integer id;
            @SerializedName("name")
            @Expose
            public String name;

            @Override
            public String toString() {
                return name;
            }
        }
    }
}
