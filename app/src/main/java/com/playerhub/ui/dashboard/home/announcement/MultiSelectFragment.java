package com.playerhub.ui.dashboard.home.announcement;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.playerhub.R;
import com.playerhub.network.response.TeamResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class MultiSelectFragment extends DialogFragment {

    private static final String ARG_LIST = "arg_list";

    @BindView(R.id.select_all)
    Button selectAll;
    @BindView(R.id.deselect_all)
    Button deselectAll;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    Unbinder unbinder;

    private List<TeamResponse.Data.Team> teamList;
    private SelectDeselectAdapter selectDeselectAdapter;

    private OnMultiSelectListener onMultiSelectListener;

    public MultiSelectFragment() {
        // Required empty public constructor
    }


    public static MultiSelectFragment getInstance(List<TeamResponse.Data.Team> teamList) {


        MultiSelectFragment fragment = new MultiSelectFragment();

        Bundle bundle = new Bundle();

        bundle.putSerializable(ARG_LIST, (Serializable) teamList);

        fragment.setArguments(bundle);

        return fragment;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_multi_select, container, false);
        unbinder = ButterKnife.bind(this, view);

        teamList = (List<TeamResponse.Data.Team>) getArguments().getSerializable(ARG_LIST);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        selectDeselectAdapter = new SelectDeselectAdapter(teamList);

        recyclerView.setAdapter(selectDeselectAdapter);

        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        return view;
    }

    @OnClick({R.id.select_all, R.id.deselect_all, R.id.done, R.id.close})
    public void onSelectDeselect(View view) {

        switch (view.getId()) {


            case R.id.select_all:


                for (int i = 0; i < teamList.size(); i++) {

                    teamList.get(i).isSelect = true;

                }

                selectDeselectAdapter.updateList(teamList);


                break;

            case R.id.deselect_all:

                for (int i = 0; i < teamList.size(); i++) {

                    teamList.get(i).isSelect = false;

                }

                selectDeselectAdapter.updateList(teamList);

                break;

            case R.id.done:

                if (onMultiSelectListener != null) {

                    List<TeamResponse.Data.Team> teamList = selectDeselectAdapter.getSelectedList();

                    onMultiSelectListener.OnMultiSelectItems(teamList, selectDeselectAdapter.getSelectedListString());


                }

                dismiss();


                break;

            case R.id.close:

                dismiss();

                break;


        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void setOnMultiSelectListener(OnMultiSelectListener onMultiSelectListener) {
        this.onMultiSelectListener = onMultiSelectListener;
    }


    private class SelectDeselectAdapter extends RecyclerView.Adapter<SelectDeselectAdapter.RowViewHolder> {

        private List<TeamResponse.Data.Team> teams;

        private SelectDeselectAdapter(List<TeamResponse.Data.Team> teams) {
            this.teams = teams;
        }

        public void updateList(List<TeamResponse.Data.Team> teams) {

            this.teams = new ArrayList<>();
            this.teams.addAll(teams);

            notifyDataSetChanged();

        }

        @NonNull
        @Override
        public RowViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_item_select_deselect, viewGroup, false);


            return new RowViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final RowViewHolder rowViewHolder, final int i) {

            final TeamResponse.Data.Team team = teams.get(i);

            rowViewHolder.name.setText(team.name);

            if (team.isSelect) {

                rowViewHolder.itemView.setBackgroundColor(ContextCompat.getColor(rowViewHolder.itemView.getContext(), R.color.colorPrimary));
            } else {

                rowViewHolder.itemView.setBackgroundColor(ContextCompat.getColor(rowViewHolder.itemView.getContext(), R.color.white));

            }

            rowViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (team.isSelect) {

                        teams.get(i).isSelect = false;
                    } else {
                        teams.get(i).isSelect = true;
                    }

                    if (team.isSelect) {

                        rowViewHolder.itemView.setBackgroundColor(ContextCompat.getColor(rowViewHolder.itemView.getContext(), R.color.colorPrimary));
                    } else {

                        rowViewHolder.itemView.setBackgroundColor(ContextCompat.getColor(rowViewHolder.itemView.getContext(), R.color.white));

                    }

                    notifyItemChanged(i);

                }
            });

        }


        public List<TeamResponse.Data.Team> getSelectedList() {

            List<TeamResponse.Data.Team> teamList = new ArrayList<>();

            for (int i = 0; i < teams.size(); i++) {
                if (teams.get(i).isSelect) {
                    teamList.add(teams.get(i));
                }
            }

            return teamList;

        }

        public String getSelectedListString() {

            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < teams.size(); i++) {
                if (teams.get(i).isSelect) {

                    builder.append(teams.get(i) + ",");

                }
            }

            return builder.toString();

        }


        @Override
        public int getItemCount() {
            return teams.size();
        }

        public class RowViewHolder extends RecyclerView.ViewHolder {

            private TextView name;

            public RowViewHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.name);
            }
        }

    }


    public interface OnMultiSelectListener {

        void OnMultiSelectItems(List<TeamResponse.Data.Team> teamList, String s);

    }

}
