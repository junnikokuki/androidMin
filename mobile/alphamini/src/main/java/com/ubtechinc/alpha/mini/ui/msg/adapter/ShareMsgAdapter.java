package com.ubtechinc.alpha.mini.ui.msg.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.databinding.ItemMsgAccountBinding;
import com.ubtechinc.alpha.mini.databinding.ItemMsgBinding;
import com.ubtechinc.alpha.mini.entity.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ShareMsgAdapter extends RecyclerView.Adapter<ShareMsgAdapter.ShareMsgViewHolder> {

    public static final int TYPE_TITLE = 1;
    public static final int TYPE_ITEM = 2;
    public static final int TYPE_PERMISSION_ITEM = 3;

    private String todayTime = new SimpleDateFormat("yyyy年MM月dd日").format(new Date());

    private Context context;

    private Map<String, List<Message>> messagesMap = new TreeMap<>(new Comparator<String>() {
        @Override
        public int compare(String s, String t1) {
            return t1.compareTo(s);
        }
    });

    private List<Message> messages;

    private OnItemClickListener onItemClickListener;

    public ShareMsgAdapter(Context context, List<Message> messages) {
        this.messages = messages;
        this.context = context;
        addMessagesToMsgMap(messages);
    }

    @Override
    public ShareMsgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = null;
        switch (viewType) {
            case TYPE_ITEM:
                view = inflater.inflate(R.layout.item_msg, parent, false);
                break;
            case TYPE_TITLE:
                view = inflater.inflate(R.layout.item_msg_title, parent, false);
                break;
            case TYPE_PERMISSION_ITEM:
                view = inflater.inflate(R.layout.item_msg_account, parent, false);
                break;
            default:
                break;
        }
        return new ShareMsgViewHolder(view, viewType, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(ShareMsgViewHolder holder, int position) {
        int type = getItemViewType(position);
        switch (type) {
            case TYPE_ITEM:
                Message msg = getItem(position);
                holder.bind(msg);
                break;
            case TYPE_TITLE:
                String title = getTitle(position);
                if (title.equals(todayTime)) {
                    holder.bind("今天");
                } else {
                    holder.bind(title);
                }
                break;
            case TYPE_PERMISSION_ITEM:
                Message permissionMsg = getItem(position);
                holder.bindPermissionMsg(permissionMsg);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        int count = 0;
        for (String key : messagesMap.keySet()) {
            if (count == position) {
                return TYPE_TITLE;
            } else {
                int index = position - count - 1;//当前位置等于 Position减去已经计数的count和Title
                count += messagesMap.get(key).size();
                if (position <= count) {
                    return TYPE_ITEM;
                } else {
                    count += 1;
                }
            }
        }
        return TYPE_TITLE;
    }

    @Override
    public int getItemCount() {
        return messages == null ? 0 : messagesMap.size() + messages.size();
    }

    private Message getItem(int position) {
        int count = 0;
        for (String key : messagesMap.keySet()) {
            count++;
            List<Message> messages = messagesMap.get(key);
            for (Message message : messages) {
                if (position == count) {
                    return message;
                } else {
                    count++;
                }
            }
        }
        return null;
    }

    private String getTitle(int position) {
        int count = 0;
        for (String key : messagesMap.keySet()) {
            if (position == count) {
                return key;
            } else {
                List<Message> messages = messagesMap.get(key);
                count += messages.size() + 1;
            }
        }
        return null;

    }

    private void addMessagesToMsgMap(List<Message> messages) {
        if (messages != null) {
            for (Message message : messages) {
                List<Message> tempMessages;
                if (messagesMap.containsKey(message.getDate())) {
                    tempMessages = messagesMap.get(message.getDate());
                } else {
                    tempMessages = new ArrayList<>();
                    messagesMap.put(message.getDate(), tempMessages);
                }
                tempMessages.add(message);
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public static class ShareMsgViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemMsgBinding msgBinding;

        private ItemMsgAccountBinding msgAccountBinding;

        private int type = TYPE_ITEM;

        private TextView titleView;

        private Message message;

        private OnItemClickListener onItemClickListener;

        public ShareMsgViewHolder(View view, int type, OnItemClickListener onItemClickListener) {
            super(view);
            this.onItemClickListener = onItemClickListener;
            this.type = type;
            switch (type) {
                case TYPE_ITEM:
                    if (msgBinding == null) {
                        msgBinding = DataBindingUtil.bind(view);
                    }
                    view.setOnClickListener(this);
                    break;
                case TYPE_TITLE:
                    if (titleView == null) {
                        titleView = view.findViewById(R.id.msg_title_txt);
                    }
                    break;
                case TYPE_PERMISSION_ITEM:
                    if (msgAccountBinding == null) {
                        msgAccountBinding = DataBindingUtil.bind(view);
                    }
                    break;
                default:
                    break;
            }
        }

        public void bind(Message message) {
            if (msgBinding != null) {
                msgBinding.setMsg(message);
            }
            this.message = message;
        }

        public void bind(String title) {
            if (titleView != null) {
                titleView.setText(title);
            }
        }

        public void bindPermissionMsg(Message message) {
            if (msgAccountBinding != null) {
                msgAccountBinding.setMsg(message);
            }
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(message);
            }
        }
    }

    public void updateList(List<Message> messages) {
        this.messages = messages;
        messagesMap.clear();
        addMessagesToMsgMap(messages);
        notifyDataSetChanged();
    }

    public void add(List<Message> messages) {
        this.messages.addAll(messages);
        addMessagesToMsgMap(messages);
        notifyDataSetChanged();
    }

    public void updateMessage(Message msg) {
        for (Message message : messages) {
            if (message.getNoticeId().equals(msg.getNoticeId())) {
                message.setOperation(msg.getOperation());
                message.setIsRead(msg.getIsRead());
                notifyDataSetChanged();
                break;
            }
        }
    }

    public interface OnItemClickListener {

        public void onItemClick(Message msg);
    }
}
