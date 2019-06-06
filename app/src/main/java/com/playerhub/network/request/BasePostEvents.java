package com.playerhub.network.request;

import java.util.List;

public class BasePostEvents {

    private String event_name;
    private String opp_team_name;
    private int event_type;
    private String event_location;
    private String event_start_date;
    private String event_end_date;
    private String event_start_time;
    private String event_end_time;
    private String event_description;
    private int team_id;

//    private boolean is_repeat;
    private boolean is_all_day;
//    private List<Integer> repeat_days;

    public int getEvent_type() {
        return event_type;
    }

    public void setEvent_type(int event_type) {
        this.event_type = event_type;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getOpp_team_name() {
        return opp_team_name;
    }

    public void setOpp_team_name(String opp_team_name) {
        this.opp_team_name = opp_team_name;
    }


    public String getEvent_location() {
        return event_location;
    }

    public void setEvent_location(String event_location) {
        this.event_location = event_location;
    }

    public String getEvent_start_date() {
        return event_start_date;
    }

    public void setEvent_start_date(String event_start_date) {
        this.event_start_date = event_start_date;
    }

    public String getEvent_end_date() {
        return event_end_date;
    }

    public void setEvent_end_date(String event_end_date) {
        this.event_end_date = event_end_date;
    }

    public String getEvent_start_time() {
        return event_start_time;
    }

    public void setEvent_start_time(String event_start_time) {
        this.event_start_time = event_start_time;
    }

    public String getEvent_end_time() {
        return event_end_time;
    }

    public void setEvent_end_time(String event_end_time) {
        this.event_end_time = event_end_time;
    }

    public String getEvent_description() {
        return event_description;
    }

    public void setEvent_description(String event_description) {
        this.event_description = event_description;
    }

    public int getTeam_id() {
        return team_id;
    }

    public void setTeam_id(int team_id) {
        this.team_id = team_id;
    }

//    public boolean isIs_repeat() {
//        return is_repeat;
//    }
//
//    public void setIs_repeat(boolean is_repeat) {
//        this.is_repeat = is_repeat;
//    }
//
    public boolean isIs_all_day() {
        return is_all_day;
    }

    public void setIs_all_day(boolean is_all_day) {
        this.is_all_day = is_all_day;
    }
//
//    public List<Integer> getRepeat_days() {
//        return repeat_days;
//    }
//
//    public void setRepeat_days(List<Integer> repeat_days) {
//        this.repeat_days = repeat_days;
//    }
}
