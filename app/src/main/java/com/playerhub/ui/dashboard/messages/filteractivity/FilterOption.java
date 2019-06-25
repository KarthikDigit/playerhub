package com.playerhub.ui.dashboard.messages.filteractivity;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RadioButton;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.playerhub.R;

import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilterOption extends AbstractItem<FilterOption, FilterOption.ViewHolder> {


    public String filter_name;


    public FilterOption(String filter_name) {

        this.filter_name = filter_name;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.filter_option;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.filter_option;
    }


    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);
        holder.filter_option.setText(filter_name);
        holder.filter_option.setChecked(isSelected());
    }

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
        holder.filter_option.setText(null);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.filter_option)
        RadioButton filter_option;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

//        @Override
//        public void bindView(FilterOption item, List<Object> payloads) {
//            filter_option.setText(item.filter_name);
//        }
//
//        @Override
//        public void unbindView(FilterOption item) {
//
//            filter_option.setText(null);
//        }
    }

    public static class RadioButtonClickEvent extends ClickEventHook<FilterOption> {
        @Override
        public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
            if (viewHolder instanceof FilterOption.ViewHolder) {
                return ((ViewHolder) viewHolder).filter_option;
            }
            return null;
        }

        @Override
        public void onClick(View v, int position, FastAdapter<FilterOption> fastAdapter, FilterOption item) {
            if (!item.isSelected()) {
                Set<Integer> selections = fastAdapter.getSelections();
                if (!selections.isEmpty()) {
                    int selectedPosition = selections.iterator().next();
                    fastAdapter.deselect();
                    fastAdapter.notifyItemChanged(selectedPosition);
                }
                fastAdapter.select(position);
            }
        }
    }
}
