package com.playerhub.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class KidInfoResponse implements Serializable {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("message")
    @Expose
    private String message;
    private final static long serialVersionUID = -4834224531577787831L;

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

        @SerializedName("kidinfo")
        @Expose
        private Kidinfo kidinfo;
        private final static long serialVersionUID = 8509701783018579746L;

        public Kidinfo getKidinfo() {
            return kidinfo;
        }

        public void setKidinfo(Kidinfo kidinfo) {
            this.kidinfo = kidinfo;
        }

    }


    public static class Kidinfo implements Serializable {

        @SerializedName("team")
        @Expose
        private String team;
        @SerializedName("coach")
        @Expose
        private String coach;
        @SerializedName("organization")
        @Expose
        private String organization;
        @SerializedName("firstname")
        @Expose
        private String firstname;
        @SerializedName("lastname")
        @Expose
        private String lastname;
        @SerializedName("phone")
        @Expose
        private String phone;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("parent_name")
        @Expose
        private String parentName;
        @SerializedName("joined_on")
        @Expose
        private String joinedOn;
        @SerializedName("birthday")
        @Expose
        private String birthday;

        @SerializedName("avatar_image")
        @Expose
        private String avatar_image;


        private final static long serialVersionUID = 7393493466145939459L;

        public String getTeam() {
            return team;
        }

        public String getAvatar_image() {
            return avatar_image;
        }

        public void setAvatar_image(String avatar_image) {
            this.avatar_image = avatar_image;
        }

        public void setTeam(String team) {
            this.team = team;

        }

        public String getCoach() {
            return coach;
        }

        public void setCoach(String coach) {
            this.coach = coach;
        }

        public String getOrganization() {
            return organization;
        }

        public void setOrganization(String organization) {
            this.organization = organization;
        }

        public String getFirstname() {
            return firstname;
        }

        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }

        public String getLastname() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getParentName() {
            return parentName;
        }

        public void setParentName(String parentName) {
            this.parentName = parentName;
        }

        public String getJoinedOn() {
            return joinedOn;
        }

        public void setJoinedOn(String joinedOn) {
            this.joinedOn = joinedOn;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

    }

}