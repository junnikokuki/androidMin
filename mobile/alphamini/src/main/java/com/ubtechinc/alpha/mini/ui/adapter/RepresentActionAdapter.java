package com.ubtechinc.alpha.mini.ui.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.databinding.LayoutRepresentiveItemContentBinding;
import com.ubtechinc.alpha.mini.net.ActionRepresentiveRecomandListModule;

import java.util.ArrayList;
import java.util.List;

/**
 * @Date: 2017/12/1.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] : 机器人设置中，二级页面机器人档案，修改代表动作列表底部弹窗Adapter
 */

public class RepresentActionAdapter extends RecyclerView.Adapter<RepresentActionAdapter.RepresentActionHolder>{

    private String TAG = getClass().getSimpleName();

    private List<ActionRepresentiveRecomandListModule.RecomandAction> representActions = new ArrayList<>();

    private LayoutInflater inflater;

    private int currentPostion = 0;

    public RepresentActionAdapter(Context ctx){
        inflater = LayoutInflater.from(ctx);
    }

    public ActionRepresentiveRecomandListModule.RecomandAction getChooseAction(){
        return representActions.size() > 0 ? representActions.get(currentPostion) : null;
    }

    public void loacteFirstIndexOfAction(String actionName){
        if (TextUtils.isEmpty(actionName)){
            Log.e(TAG, "loacteFirstIndexOfAction: actionName is null!!");
            return;
        }
        for (int i = 0;i < representActions.size();i++){
            ActionRepresentiveRecomandListModule.RecomandAction action = representActions.get(i);
            String description = action.getDescription();
            if (!TextUtils.isEmpty(description) && description.equals(actionName)){
                currentPostion = i;
                break;
            }
        }
        notifyDataSetChanged();
    }

    public void notifyDataChange(List<ActionRepresentiveRecomandListModule.RecomandAction> actions){
        if (actions == null){
            throw new IllegalArgumentException("representive action cannot be null！！");
        }
        representActions.clear();
        representActions.addAll(actions);
        notifyDataSetChanged();
    }

    @Override
    public RepresentActionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutRepresentiveItemContentBinding binding = DataBindingUtil.inflate(inflater, R.layout.layout_representive_item_content, null, false);
        return new RepresentActionHolder(binding);
    }

    @Override
    public void onBindViewHolder(final RepresentActionHolder holder, final int position) {
         holder.binding.tvActionName.setText(representActions.get(position).getDescription());
         if (position == currentPostion){
             holder.binding.setIsChoose(true);
         }else{
             holder.binding.setIsChoose(false);
         }
         holder.binding.rlItemAction.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 notifyItemChanged(currentPostion);
                 currentPostion = position;
                 notifyItemChanged(position);
             }
         });
    }

    @Override
    public int getItemCount() {
        return representActions != null ? representActions.size(): 0;
    }



    public static class RepresentActionHolder extends RecyclerView.ViewHolder{

        private LayoutRepresentiveItemContentBinding binding;

        public RepresentActionHolder(LayoutRepresentiveItemContentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}
