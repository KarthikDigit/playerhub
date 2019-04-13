package com.playerhub.ui.dashboard.messages;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.playerhub.R;
import com.playerhub.utils.CommonUtil;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationsLayout extends AbstractItem<ConversationsLayout, ConversationsLayout.ConViewHolder> implements Serializable {

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    private List<String> users = new ArrayList<>();

    private Conversations.Is_Typing is_typing;
    private Conversations.Last_Conversation last_conversation;

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

    public Conversations.Is_Typing getIs_typing() {
        return is_typing;
    }

    public void setIs_typing(Conversations.Is_Typing is_typing) {
        this.is_typing = is_typing;
    }

    public Conversations.Last_Conversation getLast_conversation() {
        return last_conversation;
    }

    public void setLast_conversation(Conversations.Last_Conversation last_conversation) {
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

    public String getTypeName() {
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

//    public static class Last_Conversation implements Serializable{
//
//        private String content;
//        private String sender;
//        private boolean status;
//        private long timestamp;
//        private String type;
//
//        public String getContent() {
//            return content;
//        }
//
//        public void setContent(String content) {
//            this.content = content;
//        }
//
//        public String getSender() {
//            return sender;
//        }
//
//        public void setSender(String sender) {
//            this.sender = sender;
//        }
//
//        public boolean isStatus() {
//            return status;
//        }
//
//        public void setStatus(boolean status) {
//            this.status = status;
//        }
//
//        public long getTimestamp() {
//            return timestamp;
//        }
//
//        public void setTimestamp(long timestamp) {
//            this.timestamp = timestamp;
//        }
//
//        public String getType() {
//            return type;
//        }
//
//        public void setType(String type) {
//            this.type = type;
//        }
//    }

//
//    public static class Is_Typing implements Serializable{
//
//        private String id;
//        private String name;
//
//        public String getId() {
//            return id;
//        }
//
//        public void setId(String id) {
//            this.id = id;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//    }


    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.root;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.message_item;
    }

    @Override
    public ConViewHolder getViewHolder(@NonNull View v) {
        return new ConViewHolder(v);
    }


    /**
     * our ViewHolder
     */
    protected static class ConViewHolder extends FastAdapter.ViewHolder<ConversationsLayout> {

        @BindView(R.id.icon)
        CircleImageView icon;
        @BindView(R.id.date_time)
        TextView dateTime;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.description)
        TextView description;
        @BindView(R.id.count)
        TextView count;
        @BindView(R.id.count_lay)
        RelativeLayout countLay;
        @BindView(R.id.root)
        RelativeLayout root;
        @BindView(R.id.useris_online)
        View userIsOnline;

        public ConViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        public void bindView(final ConversationsLayout item, List<Object> payloads) {

//            StringHolder.applyToOrHide(item.description, description);

//            description.setText(item.team);
            count.setText(String.format(Locale.ENGLISH, "%d", item.unread));

            countLay.setVisibility(View.VISIBLE);
            if (item.unread == 0) {
                countLay.setVisibility(View.INVISIBLE);
            }

            if (item.getTitle() != null && !TextUtils.isEmpty(item.getTitle())) {

                name.setText(item.getTitle());
            } else {

                name.setText("Undefined Group");
            }
//            dateTime.setText(item.date_time);

            countLay.setVisibility(View.INVISIBLE);

            Picasso.get().load(R.mipmap.ic_launcher).placeholder(R.mipmap.ic_launcher).resize(120, 120).into(icon);


            CommonUtil.getGroupMessageCount(item.getMessage_id(), new CommonUtil.CallBackCount() {
                @Override
                public void showCount(long counta) {
                    item.setUnread(counta);
                    count.setText(String.format(Locale.ENGLISH, "%d", counta));
                    countLay.setVisibility(View.VISIBLE);
                    if (item.unread == 0) {
                        countLay.setVisibility(View.INVISIBLE);
                    }
                }
            });


        }

        @Override
        public void unbindView(ConversationsLayout item) {
            description.setText(null);
            count.setText(null);
            dateTime.setText(null);
            name.setText(null);
        }
    }
}
