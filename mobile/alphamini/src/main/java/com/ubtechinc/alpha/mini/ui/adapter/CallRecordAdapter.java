package com.ubtechinc.alpha.mini.ui.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.databinding.ItemCallRecordBinding;
import com.ubtechinc.alpha.mini.entity.Message;
import com.ubtechinc.alpha.mini.entity.CallRecord;

import java.util.List;

public class CallRecordAdapter extends RecyclerView.Adapter<CallRecordAdapter.CallRecordViewHolder> {

    private Context context;

    private List<CallRecord> phoneHistories;

    private OnItemClickListener onItemClickListener;

    public CallRecordAdapter(Context context, List<CallRecord> phoneHistories) {
        this.phoneHistories = phoneHistories;
        this.context = context;
    }

    @Override
    public CallRecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ItemCallRecordBinding binding = DataBindingUtil.inflate(inflater,R.layout.item_call_record,parent,false);
        return new CallRecordViewHolder(binding, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(CallRecordViewHolder holder, int position) {
        holder.bind(phoneHistories.get(position));

    }

    @Override
    public int getItemCount() {
        return phoneHistories == null ? 0 : phoneHistories.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public static class CallRecordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private OnItemClickListener onItemClickListener;

        private ItemCallRecordBinding binding;

        public CallRecordViewHolder(ItemCallRecordBinding binding, OnItemClickListener onItemClickListener) {
            super(binding.getRoot());
            this.binding = binding;
            this.onItemClickListener = onItemClickListener;
        }

        public void bind(CallRecord callRecord){
            binding.setCallRecord(callRecord);
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {

            }
        }
    }

    public void updateList(List<CallRecord> phoneHistories) {
        this.phoneHistories = phoneHistories;
        notifyDataSetChanged();
    }

    public void add(List<CallRecord> phoneHistories) {
        this.phoneHistories.addAll(phoneHistories);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {

        public void onItemClick(Message msg);
    }
}
