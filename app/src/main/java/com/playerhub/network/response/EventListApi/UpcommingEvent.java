
package com.playerhub.network.response.EventListApi;

import java.io.Serializable;
import java.util.Date;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.playerhub.utils.Utils;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;

public class UpcommingEvent implements Serializable {


    private boolean isEmptyView = false;

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("team_id")
    @Expose
    private Integer teamId;
    @SerializedName("event_type_id")
    @Expose
    private Integer eventTypeId;
    @SerializedName("opp_team_id")
    @Expose
    private Object oppTeamId;
    @SerializedName("opp_team_name")
    @Expose
    private String oppTeamName;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private Object description;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("start_date")
    @Expose
    private String startDate;
    @SerializedName("end_date")
    @Expose
    private String endDate;
    @SerializedName("start_time")
    @Expose
    private String startTime;
    @SerializedName("end_time")
    @Expose
    private String endTime;
    @SerializedName("all_day")
    @Expose
    private Integer allDay;
    @SerializedName("is_repeat")
    @Expose
    private Integer isRepeat;
    @SerializedName("repeat_days")
    @Expose
    private String repeatDays;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("deleted_at")
    @Expose
    private Object deletedAt;
    @SerializedName("team")
    @Expose
    private Team team;


    public boolean isEmptyView() {
        return isEmptyView;
    }

    public void setEmptyView(boolean emptyView) {
        isEmptyView = emptyView;
    }

    @Override
    public String toString() {
        return "UpcommingEvent{" +
                "id=" + id +
                ", userId=" + userId +
                ", teamId=" + teamId +
                ", eventTypeId=" + eventTypeId +
                ", oppTeamId=" + oppTeamId +
                ", oppTeamName='" + oppTeamName + '\'' +
                ", name='" + name + '\'' +
                ", description=" + description +
                ", location='" + location + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", allDay=" + allDay +
                ", isRepeat=" + isRepeat +
                ", repeatDays='" + repeatDays + '\'' +
                ", status=" + status +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", deletedAt=" + deletedAt +
                ", team=" + team +
                ", oppteam=" + oppteam +
                ", type=" + type +
                '}';
    }

    @SerializedName("oppteam")
    @Expose
    private Object oppteam;
    @SerializedName("type")
    @Expose
    private Type type;
    private final static long serialVersionUID = 8935330916759939514L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public Integer getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(Integer eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public Object getOppTeamId() {
        return oppTeamId;
    }

    public void setOppTeamId(Object oppTeamId) {
        this.oppTeamId = oppTeamId;
    }

    public String getOppTeamName() {
        return oppTeamName;
    }

    public void setOppTeamName(String oppTeamName) {
        this.oppTeamName = oppTeamName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getDescription() {
        return description;
    }

    public void setDescription(Object description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getAllDay() {
        return allDay;
    }

    public void setAllDay(Integer allDay) {
        this.allDay = allDay;
    }

    public Integer getIsRepeat() {
        return isRepeat;
    }

    public void setIsRepeat(Integer isRepeat) {
        this.isRepeat = isRepeat;
    }

    public String getRepeatDays() {
        return repeatDays;
    }

    public void setRepeatDays(String repeatDays) {
        this.repeatDays = repeatDays;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Object getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Object deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Object getOppteam() {
        return oppteam;
    }

    public void setOppteam(Object oppteam) {
        this.oppteam = oppteam;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getContertedDate() {

        Date date = Utils.convertStringToDate(getStartDate(), "yyyy-MM-dd");

        return Utils.convertDateToString(date, "yyyy-MM-dd");
    }

    public CalendarDay getLocalDate() {

        Date input = Utils.convertStringToDate(getStartDate(), "yyyy-MM-dd");


//        LocalDateTime conv=LocalDateTime.ofInstant(input.toInstant(), ZoneId.systemDefault());
//        LocalDate convDate=conv.toLocalDate();

//        return LocalDate.from(Instant.ofEpochMilli(input.getTime()).atZone(ZoneId.systemDefault()));

        return CalendarDay.from(LocalDate.from(Instant.ofEpochMilli(input.getTime()).atZone(ZoneId.systemDefault())));

    }

}
