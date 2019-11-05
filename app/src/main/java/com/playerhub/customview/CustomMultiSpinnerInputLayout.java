package com.playerhub.customview;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;

import com.playerhub.network.response.TeamResponse;
import com.playerhub.ui.dashboard.announcement.MultiSelectFragment;

import java.util.ArrayList;
import java.util.List;

public class CustomMultiSpinnerInputLayout extends TextInputLayout implements MultiSelectFragment.OnMultiSelectListener {

    private List<TeamResponse.Data.Team> teams;
    private List<TeamResponse.Data.Team> selectedTeam;

    public CustomMultiSpinnerInputLayout(Context context) {
        super(context);

        init(null);
    }

    public CustomMultiSpinnerInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomMultiSpinnerInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {


        setFocusable(false);

        if (getEditText() != null) getEditText().setFocusable(false);


    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();


        setFocusable(false);

        if (getEditText() != null) getEditText().setFocusable(false);
    }

    public void showPopUp(final List<TeamResponse.Data.Team> teams, FragmentManager fragmentManager) {

        this.teams = teams;

        List<TeamResponse.Data.Team> teamList = new ArrayList<>(teams);

        MultiSelectFragment fragment = MultiSelectFragment.getInstance(teamList);

        fragment.show(fragmentManager, "Multiselect");

        fragment.setOnMultiSelectListener(this);


    }


    @Override
    public void OnMultiSelectItems(List<TeamResponse.Data.Team> teamList, String s) {

        if (teamList != null && !teamList.isEmpty()) {

            selectedTeam = teamList;

            getEditText().setText(s);

        } else {
            getEditText().setText("Select a team");
            selectedTeam = null;
        }
    }


    public List<TeamResponse.Data.Team> getSelectedTeam() {
        return selectedTeam;
    }
}
