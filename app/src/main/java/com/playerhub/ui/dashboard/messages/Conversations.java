package com.playerhub.ui.dashboard.messages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Conversations implements Serializable {

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    private List<String> users = new ArrayList<>();

    private Is_Typing is_typing;
    private Last_Conversation last_conversation;

    private String message_id;
    private String request_flag;
    private boolean status;
    private long timestamp;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String type;
    private String title;
    private long unread;

    public Is_Typing getIs_typing() {
        return is_typing;
    }

    public void setIs_typing(Is_Typing is_typing) {
        this.is_typing = is_typing;
    }

    public Last_Conversation getLast_conversation() {
        return last_conversation;
    }

    public void setLast_conversation(Last_Conversation last_conversation) {
        this.last_conversation = last_conversation;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getRequest_flag() {
        return request_flag;
    }

    public void setRequest_flag(String request_flag) {
        this.request_flag = request_flag;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getUnread() {
        return unread;
    }

    public void setUnread(long unread) {
        this.unread = unread;
    }

    public static class Last_Conversation implements Serializable {

        private String content;
        private String sender;
        private boolean status;
        private long timestamp;
        private String type;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }


    public static class Is_Typing implements Serializable {

        private String id;
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
