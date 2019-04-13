
package com.playerhub.network.response.EventListApi;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EventsCat implements Serializable
{

    @SerializedName("today_events")
    @Expose
    private Integer todayEvents;
    @SerializedName("upcomming_events")
    @Expose
    private Integer upcommingEvents;
    private final static long serialVersionUID = -4326884860733286934L;

    public Integer getTodayEvents() {
        return todayEvents;
    }

    public void setTodayEvents(Integer todayEvents) {
        this.todayEvents = todayEvents;
    }

    public Integer getUpcommingEvents() {
        return upcommingEvents;
    }

    public void setUpcommingEvents(Integer upcommingEvents) {
        this.upcommingEvents = upcommingEvents;
    }

}
