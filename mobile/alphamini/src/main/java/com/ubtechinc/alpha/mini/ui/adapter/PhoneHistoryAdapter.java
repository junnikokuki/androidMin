package com.ubtechinc.alpha.mini.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.entity.Message;
import com.ubtechinc.alpha.mini.entity.PhoneHistory;

import java.util.List;

public class PhoneHistoryAdapter extends RecyclerView.Adapter<PhoneHistoryAdapter.PhoneHistoryViewHolder> {

    private Context context;

    private List<PhoneHistory> phoneHistories;

    private OnItemClickListener onItemClickListener;

    public PhoneHistoryAdapter(Context context, List<PhoneHistory> phoneHistories) {
        this.phoneHistories = phoneHistories;
        this.context = context;
    }

    @Override
    public PhoneHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_phone_history, parent);
        return new PhoneHistoryViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(PhoneHistoryViewHolder holder, int position) {
        holder.bind(phoneHistories.get(position));

    }

    @Override
    public int getItemCount() {
        return phoneHistories == null ? 0 : phoneHistories.size() + phoneHistories.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public static class PhoneHistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private OnItemClickListener onItemClickListener;

        public PhoneHistoryViewHolder(View view, OnItemClickListener onItemClickListener) {
            super(view);
            this.onItemClickListener = onItemClickListener;
        }

        public void bind(PhoneHistory phoneHistory){

        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {

            }
        }
    }

    public void updateList(List<PhoneHistory> phoneHistories) {
        this.phoneHistories = phoneHistories;
        notifyDataSetChanged();
    }

    public void add(List<PhoneHistory> phoneHistories) {
        this.phoneHistories.addAll(phoneHistories);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {

        public void onItemClick(Message msg);
    }
}
