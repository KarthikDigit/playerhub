package com.playerhub.ui.dashboard.messages.models;

import com.playerhub.network.response.ContactListApi;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class GroupName extends ExpandableGroup<ContactListApi.Datum> {

    private boolean isExpand = true;

    public GroupName(String title, List<ContactListApi.Datum> items) {
        super(title, items);
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }
}
