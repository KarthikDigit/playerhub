package com.playerhub.network.service;

public class KidsRequest {

    private long kid_id;

    private long event_id;

    private long type;


    public long getKid_id() {
        return kid_id;
    }

    public void setKid_id(long kid_id) {
        this.kid_id = kid_id;
    }

    public long getEvent_id() {
        return event_id;
    }

    public void setEvent_id(long event_id) {
        this.event_id = event_id;
    }

    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }
}
