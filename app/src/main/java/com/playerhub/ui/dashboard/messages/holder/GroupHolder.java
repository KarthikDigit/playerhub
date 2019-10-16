package com.playerhub.ui.dashboard.messages.holder;

import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.playerhub.R;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class GroupHolder extends GroupViewHolder {

    private TextView genreTitle;
    private ImageView arrow;

    public GroupHolder(View itemView) {
        super(itemView);

        genreTitle = itemView.findViewById(R.id.genre_title);
        arrow = itemView.findViewById(R.id.icon);

    }

    public void setGenreTitle(ExpandableGroup group) {
        genreTitle.setText(group.getTitle());
    }

    @Override
    public void expand() {
        super.expand();
//        animateExpand();
        arrow.setRotation(180);
    }

    @Override
    public void collapse() {
        super.collapse();
//        animateCollapse();
        arrow.setRotation(360);
    }

    private void animateExpand() {
        RotateAnimation rotate =
                new RotateAnimation(360, 180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
    }

    private void animateCollapse() {
        RotateAnimation rotate =
                new RotateAnimation(180, 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
    }
}
