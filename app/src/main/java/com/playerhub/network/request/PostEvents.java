package com.playerhub.network.request;

import java.util.List;

public class PostEvents extends BasePostEvents {


    private boolean is_repeat;
    private List<Integer> repeat_days;

    public boolean isIs_repeat() {
        return is_repeat;
    }

    public void setIs_repeat(boolean is_repeat) {
        this.is_repeat = is_repeat;
    }

    public List<Integer> getRepeat_days() {
        return repeat_days;
    }

    public void setRepeat_days(List<Integer> repeat_days) {
        this.repeat_days = repeat_days;
    }
}
