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
import com.ubtechinc.alpha.mini.databinding.ItemMiniFriendBinding;
import com.ubtechinc.alpha.mini.ui.PageRouter;
import com.ubtechinc.alpha.mini.ui.friend.AlphaMiniFriendActivity;
import com.ubtechinc.alpha.mini.ui.friend.FriendFaceInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @Date: 2017/12/18.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] : 机器人好友的列表
 */

public class MiniFriendAdapter extends RecyclerView.Adapter<MiniFriendAdapter.FriendHolder> {

    private String TAG = getClass().getSimpleName();

    private static final int REQUEST_CODE = 10001;

    private Context mCtx;
    /**所有的人脸信息**/
    private List<FriendFaceInfo> mFriends = new ArrayList<>();

    /**被勾选的人脸信息Ids**/
    private Set<String> selectes = new HashSet<>();

    @AlphaMiniFriendActivity.MiniFriendActivityMode
    private static int mCurrentMode;
    /**跳转人脸详情的下标值**/
    private int indexOf2MiniInfo = -1;

    /**监听选中所有的状态回调接口**/
    IOnItemSelectedListener listener;

    public int getIndexOf2MiniInfo() {
        return indexOf2MiniInfo;
    }

    private void setIndexOf2MiniInfo(int index){
        indexOf2MiniInfo = index;
    }

    public void resetIndexOf2MiniInfo() {
        indexOf2MiniInfo = -1;
    }

    public MiniFriendAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public void addSelectedAllListener(IOnItemSelectedListener listener){
        this.listener = listener;
    }

    public void setActivityCurrentMode(@AlphaMiniFriendActivity.MiniFriendActivityMode int currentMode){
        mCurrentMode = currentMode;
        notifyDataSetChanged();
    }

    public void refreshFaceInfos(List<FriendFaceInfo> friends){
        if (friends == null){
            Log.e(TAG, "refreshFaceInfos , but you have set null list of friends, it won't refresh");
            return;
        }
        mFriends.clear();
        mFriends.addAll(friends);
        notifyDataSetChanged();
    }

    public void addFaceInfos(List<FriendFaceInfo> friends){
        if (friends == null){
            Log.e(TAG, "addFaceInfos , but you have set null list of friends, it won't refresh");
            return;
        }
        mFriends.addAll(friends);
        notifyDataSetChanged();
    }

    /**获取处于编辑状态中，当前被选中的人脸信息**/
    public Set<String> getSelecteFaceInfoIds(){
        return selectes;
    }

    /**获取处于编辑状态中，选中或者取消所有人选中的状态**/
    public void selectAllFaceInfo(boolean selectAll){
        Iterator<FriendFaceInfo> iterator = mFriends.iterator();
        while (iterator.hasNext()){
            FriendFaceInfo faceInfo = iterator.next();
            faceInfo.setChecked(selectAll);
            if (selectAll){
                selectes.add(faceInfo.getId());
            }else{
                selectes.remove(faceInfo.getId());
            }
        }
        listener.checkSelectedSize(selectes.size());
        notifyDataSetChanged();
    }

    public void clearSelectedData(){
        selectes.clear();
        listener.checkSelectedSize(selectes.size());
    }

    public void notifyItemNameChanged(int index, String name){
        String originName = mFriends.get(index).getName();
        if (!TextUtils.isEmpty(name) && name.equalsIgnoreCase(originName)){
            notifyItemChanged(index);
        }
    }

    @Override
    public FriendHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemMiniFriendBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mCtx), R.layout.item_mini_friend, null, false);
        return new FriendHolder(binding);
    }

    @Override
    public void onBindViewHolder(final FriendHolder holder, final int position) {
        setFriendName(holder, position);
        holder.binding.setIsEdit(mCurrentMode == AlphaMiniFriendActivity.MINI_FREIDN_ACITIVY_EDIT_MODE ? true : false);
        holder.binding.setIsChecked(mFriends.get(position).isChecked());
        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentMode == AlphaMiniFriendActivity.MINI_FREIDN_ACITIVY_SIMPLE_MODE){
                    setIndexOf2MiniInfo(position);
                    PageRouter.toMiniFriendInfoActivityNeedResult((AlphaMiniFriendActivity)mCtx, mFriends.get(position),REQUEST_CODE );
                }else if(mCurrentMode == AlphaMiniFriendActivity.MINI_FREIDN_ACITIVY_EDIT_MODE){
                    boolean isChecked = mFriends.get(position).isChecked();
                    mFriends.get(position).setChecked(!isChecked);
                    holder.binding.setIsChecked(mFriends.get(position).isChecked());
                    refreshSelectList(mFriends.get(position));
                    listener.checkAllSelected(selectes.size() == mFriends.size());
                }
            }
        });
    }

    private void setFriendName(FriendHolder holder, int position) {
        String name = mFriends.get(position).getName();
        if (!TextUtils.isEmpty(name) && name.length() > 6){
            StringBuffer buffer = new StringBuffer();
            buffer.append(name.substring(0, 6)).append("...");
            Log.i(TAG, "setFriendName: buffer : " + buffer.toString());
            holder.binding.setFriendName(buffer.toString());
        }else{
            Log.i(TAG, "setFriendName:  " + mFriends.get(position).getName());
            holder.binding.setFriendName(mFriends.get(position).getName());
        }
    }

    private void refreshSelectList(FriendFaceInfo faceInfo) {
        if (faceInfo.isChecked()){
            selectes.add(faceInfo.getId());
        }else{
            selectes.remove(faceInfo.getId());
        }
        listener.checkSelectedSize(selectes.size());
    }

    @Override
    public int getItemCount() {
        return mFriends.size();
    }

    public interface IOnItemSelectedListener {
        void checkAllSelected(boolean isSelected);
        void checkSelectedSize(int size);
    }

    public static class FriendHolder extends RecyclerView.ViewHolder{

        private ItemMiniFriendBinding binding;

        public FriendHolder(ItemMiniFriendBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
