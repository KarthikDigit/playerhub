
package com.playerhub.network.response.EventListApi;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data implements Serializable
{

    @SerializedName("event_type")
    @Expose
    private List<EventType> eventType = null;
    @SerializedName("events_cat")
    @Expose
    private EventsCat eventsCat;
    @SerializedName("today_events")
    @Expose
    private List<UpcommingEvent> todayEvents = null;
    @SerializedName("upcomming_events")
    @Expose
    private List<UpcommingEvent> upcommingEvents = null;
    private final static long serialVersionUID = -1147757148454841070L;

    public List<EventType> getEventType() {
        return eventType;
    }

    public void setEventType(List<EventType> eventType) {
        this.eventType = eventType;
    }

    public EventsCat getEventsCat() {
        return eventsCat;
    }

    public void setEventsCat(EventsCat eventsCat) {
        this.eventsCat = eventsCat;
    }

    public List<UpcommingEvent> getTodayEvents() {
        return todayEvents;
    }

    public void setTodayEvents(List<UpcommingEvent> todayEvents) {
        this.todayEvents = todayEvents;
    }

    public List<UpcommingEvent> getUpcommingEvents() {
        return upcommingEvents;
    }

    public void setUpcommingEvents(List<UpcommingEvent> upcommingEvents) {
        this.upcommingEvents = upcommingEvents;
    }

}
