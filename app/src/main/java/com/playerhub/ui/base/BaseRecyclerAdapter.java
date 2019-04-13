package com.playerhub.ui.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<T> list;

    public BaseRecyclerAdapter(Context mContext, List<T> list) {
        this.mContext = mContext;
        this.list = list;
    }

    protected abstract int getLayoutRowId();

    public void addNewList(List<T> list) {

        this.list = new ArrayList<>();
        this.list.addAll(list);

        notifyDataSetChanged();

    }

    public void add(T o) {

        if (this.list != null) {
            this.list.add(o);

            notifyItemInserted(this.list.size() - 1);
        }

    }

    public void update(T o, final int position, View view) {

        if (this.list != null) {

            this.list.set(position, o);


            view.post(new Runnable() {
                @Override
                public void run() {
                    notifyItemChanged(position);
                }
            });


        }

    }


    public T getItem(int position) {

        return this.list != null && !this.list.isEmpty() ? this.list.get(position) : null;
    }

    public Context getmContext() {
        return mContext;
    }

    public List<T> getList() {
        return list;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        View view = inflater.inflate(getLayoutRowId(), viewGroup, false);

        return getHolder(view);
    }

    protected abstract <VH extends RecyclerView.ViewHolder> VH getHolder(View view);

    @Override
    public int getItemCount() {
        return this.list != null ? this.list.size() : 0;
    }
}
