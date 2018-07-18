package com.ubtechinc.alpha.mini.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.entity.Skill;

import java.util.List;

public class SkillAdapter extends RecyclerView.Adapter<SkillAdapter.SkillViewHolder> {

    private Context context;

    private List<Skill> skills;

    public SkillAdapter(Context context, List<Skill> skills) {
        this.skills = skills;
        this.context = context;
    }

    @Override
    public SkillViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_avatar_action, null);
        return new SkillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SkillViewHolder holder, int position) {
        holder.bind(skills.get(position));
    }

    @Override
    public int getItemCount() {
        return skills == null ? 0 : skills.size();
    }

    public static class SkillViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public SkillViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }

        public void bind(Skill skills) {
            textView.setText(skills.getAppName());
        }
    }
}
