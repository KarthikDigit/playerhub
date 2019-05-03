package com.playerhub.ui.dashboard.home;

import android.content.Intent;

public class ParentChild {

    public enum TYPE {PARENT, CHILD, COACH}

    private TYPE type;

    private Integer id;
    private String name;

    private String imgUrl;

    private String whoIs;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getWhoIs() {
        return whoIs;
    }

    public void setWhoIs(String whoIs) {
        this.whoIs = whoIs;
    }
}
