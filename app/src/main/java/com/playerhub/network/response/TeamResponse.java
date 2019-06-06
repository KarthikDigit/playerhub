package com.playerhub.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class TeamResponse implements Serializable {

    @SerializedName("success")
    @Expose
    public Boolean success;
    @SerializedName("data")
    @Expose
    public Data data;
    @SerializedName("message")
    @Expose
    public String message;


    public static class Data implements Serializable {

        @SerializedName("teams")
        @Expose
        public List<Team> teams = null;

        public static class Team implements Serializable{

            public boolean isSelect = false;

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
