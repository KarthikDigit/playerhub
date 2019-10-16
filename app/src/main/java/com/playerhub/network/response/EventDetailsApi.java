package com.playerhub.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class EventDetailsApi implements Serializable {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("message")
    @Expose
    private String message;
    private final static long serialVersionUID = 8564616688256086286L;

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

        @SerializedName("kids")
        @Expose
        private List<Kid> kids = null;

        @SerializedName("event_name")
        @Expose
        private String eventName;
        @SerializedName("team_name")
        @Expose
        private String teamName;
        @SerializedName("event_type")
        @Expose
        private String eventType;
        @SerializedName("start_date")
        @Expose
        private String startDate;
        @SerializedName("start_time")
        @Expose
        private String startTime;
        @SerializedName("end_time")
        @Expose
        private String endTime;
        @SerializedName("event_repeat")
        @Expose
        private String eventRepeat;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("location")
        @Expose
        private String location;
        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("user_id")
        @Expose
        private Integer userId;
        private final static long serialVersionUID = 6941220284937824462L;

        public String getEventRepeat() {
            return eventRepeat;
        }

        public void setEventRepeat(String eventRepeat) {
            this.eventRepeat = eventRepeat;
        }

        public String getDateAndTime() {
            return getStartDate() + " " + getStartTime() + " - " + getEndTime();
        }

        public List<Kid> getKids() {
            return kids;
        }

        public void setKids(List<Kid> kids) {
            this.kids = kids;
        }

        public String getEventName() {
            return eventName;
        }

        public void setEventName(String eventName) {
            this.eventName = eventName;
        }

        public String getTeamName() {
            return teamName;
        }

        public void setTeamName(String teamName) {
            this.teamName = teamName;
        }

        public String getEventType() {
            return eventType;
        }

        public void setEventType(String eventType) {
            this.eventType = eventType;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
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

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

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




        public static class Kid implements Serializable
        {

            @SerializedName("id")
            @Expose
            private Integer id;
            @SerializedName("parent_id")
            @Expose
            private Integer parentId;
            @SerializedName("team_id")
            @Expose
            private Integer teamId;
            @SerializedName("2018_team_id")
            @Expose
            private Integer _2018TeamId;
            @SerializedName("coach_id")
            @Expose
            private Integer coachId;
            @SerializedName("organization_id")
            @Expose
            private Integer organizationId;
            @SerializedName("firstname")
            @Expose
            private String firstname;
            @SerializedName("lastname")
            @Expose
            private String lastname;
            @SerializedName("avatar_image")
            @Expose
            private String avatarImage;
            @SerializedName("birthday")
            @Expose
            private Object birthday;
            @SerializedName("height")
            @Expose
            private Object height;
            @SerializedName("weight")
            @Expose
            private Object weight;
            @SerializedName("position")
            @Expose
            private Object position;
            @SerializedName("jersy_no")
            @Expose
            private Object jersyNo;
            @SerializedName("size_id")
            @Expose
            private Integer sizeId;
            @SerializedName("player_id")
            @Expose
            private Object playerId;
            @SerializedName("gender_id")
            @Expose
            private Integer genderId;
            @SerializedName("status")
            @Expose
            private Integer status;
            @SerializedName("update_token")
            @Expose
            private Object updateToken;
            @SerializedName("created_at")
            @Expose
            private String createdAt;
            @SerializedName("updated_at")
            @Expose
            private String updatedAt;
            @SerializedName("yes")
            @Expose
            private Integer yes;
            @SerializedName("no")
            @Expose
            private Integer no;
            private final static long serialVersionUID = -8939639877644970479L;

            public Integer getId() {
                return id;
            }

            public void setId(Integer id) {
                this.id = id;
            }

            public Integer getParentId() {
                return parentId;
            }

            public void setParentId(Integer parentId) {
                this.parentId = parentId;
            }

            public Integer getTeamId() {
                return teamId;
            }

            public void setTeamId(Integer teamId) {
                this.teamId = teamId;
            }

            public Integer get2018TeamId() {
                return _2018TeamId;
            }

            public void set2018TeamId(Integer _2018TeamId) {
                this._2018TeamId = _2018TeamId;
            }

            public Integer getCoachId() {
                return coachId;
            }

            public void setCoachId(Integer coachId) {
                this.coachId = coachId;
            }

            public Integer getOrganizationId() {
                return organizationId;
            }

            public void setOrganizationId(Integer organizationId) {
                this.organizationId = organizationId;
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

            public String getAvatarImage() {
                return avatarImage;
            }

            public void setAvatarImage(String avatarImage) {
                this.avatarImage = avatarImage;
            }

            public Object getBirthday() {
                return birthday;
            }

            public void setBirthday(Object birthday) {
                this.birthday = birthday;
            }

            public Object getHeight() {
                return height;
            }

            public void setHeight(Object height) {
                this.height = height;
            }

            public Object getWeight() {
                return weight;
            }

            public void setWeight(Object weight) {
                this.weight = weight;
            }

            public Object getPosition() {
                return position;
            }

            public void setPosition(Object position) {
                this.position = position;
            }

            public Object getJersyNo() {
                return jersyNo;
            }

            public void setJersyNo(Object jersyNo) {
                this.jersyNo = jersyNo;
            }

            public Integer getSizeId() {
                return sizeId;
            }

            public void setSizeId(Integer sizeId) {
                this.sizeId = sizeId;
            }

            public Object getPlayerId() {
                return playerId;
            }

            public void setPlayerId(Object playerId) {
                this.playerId = playerId;
            }

            public Integer getGenderId() {
                return genderId;
            }

            public void setGenderId(Integer genderId) {
                this.genderId = genderId;
            }

            public Integer getStatus() {
                return status;
            }

            public void setStatus(Integer status) {
                this.status = status;
            }

            public Object getUpdateToken() {
                return updateToken;
            }

            public void setUpdateToken(Object updateToken) {
                this.updateToken = updateToken;
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

            public Integer getYes() {
                return yes;
            }

            public void setYes(Integer yes) {
                this.yes = yes;
            }

            public Integer getNo() {
                return no;
            }

            public void setNo(Integer no) {
                this.no = no;
            }

        }











    }





}