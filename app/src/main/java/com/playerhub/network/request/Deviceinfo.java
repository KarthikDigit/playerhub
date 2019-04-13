package com.playerhub.network.request;

public class Deviceinfo {

    private String user_id;
    private String device_id;
    private String device_model;
    private String push_token_id;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getDevice_model() {
        return device_model;
    }

    public void setDevice_model(String device_model) {
        this.device_model = device_model;
    }

    public String getPush_token_id() {
        return push_token_id;
    }

    public void setPush_token_id(String push_token_id) {
        this.push_token_id = push_token_id;
    }
}
