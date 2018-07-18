package com.ubtechinc.alpha.mini.ui.albums;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.databinding.AlbumItemLayoutImageBinding;
import com.ubtechinc.alpha.mini.databinding.AlbumItemLayoutTimeBinding;
import com.ubtechinc.alpha.mini.entity.AlbumItem;
import com.ubtechinc.alpha.mini.ui.PageRouter;
import com.ubtechinc.alpha.mini.utils.AlbumsClassifyUtils;
import com.ubtechinc.alpha.mini.utils.Tools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @作者：liudongyang
 * @日期: 18/3/19 11:07
 * @描述: 相册列表的Adapter
 *
 * @实现: Item分为两类，顶部的时间标题与照片的内容（4个一行), 维护一个时间标题的位置容器, 剩余的自行摆放
 * mPhoto存放所有的照片Item和对应的时间Item,时间Item作为一个new AlbumItem()空的数据
 *
 */


public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.AlbumsItemHolder> {

    public static final int ITEM_TIME_TYPE  = 10001;
    public static final int ITEM_IMAGE_TYPE = 10002;

    private Context              mCtx;
    private LayoutInflater       mLayoutInflater;

    private Set<AlbumItem>       mCheckedPhotos   = new HashSet<>();    //编辑时当前勾选的所有照片

    private List<Integer>        mTimeItemIndex   = new ArrayList<>();  //顶部时间的Item的位置

    private SparseArray<String>  mTimeItemStrings = new SparseArray<>();//顶部时间的Item的时间字符串

    private ArrayList<AlbumItem> mAlbumItems      = new ArrayList<>();  //所有的Item包括时间和照片

    private boolean              mIsEditingPhotos;

    private IPhotoCheckedChangeListener mListener;


    public AlbumsAdapter(Context ctx, IPhotoCheckedChangeListener listener) {
        this.mCtx = ctx;
        this.mListener = listener;
        mLayoutInflater = LayoutInflater.from(ctx);
    }

    public int pullRefreshPhotos(List<AlbumItem> albumItems){
        mTimeItemIndex.clear();
        mTimeItemStrings.clear();
        List<AlbumItem> newItems = new ArrayList<>();
        int refreshCount = 0;
        for (AlbumItem item: albumItems){
            if (!mAlbumItems.contains(item)){
                refreshCount++;
            }
            newItems.add(item);
        }
        mAlbumItems.clear();
        mAlbumItems.addAll(newItems);
        mergeTimeItemToAlbums();
        notifyDataSetChanged();
        return refreshCount;
    }

    public int addPartPhotos(List<AlbumItem> albumItems){
        removeTimeItemFromAlbumItems();
        mAlbumItems.addAll(albumItems);
        mListener.onAlbumsTotalnumChange(mAlbumItems.size());
        mergeTimeItemToAlbums();
        notifyDataSetChanged();
        return albumItems.size();
    }

    public void deleteCheckedPhotos(){
        removeTimeItemFromAlbumItems();
        for (AlbumItem item : mCheckedPhotos){//移除选中的Item
            if (mAlbumItems.contains(item)){
                mAlbumItems.remove(item);
            }
        }
        mCheckedPhotos.clear();
        mListener.onAlbumsTotalnumChange(mAlbumItems.size());
        mergeTimeItemToAlbums();
        notifyDataSetChanged();
    }

    public void selectAllOrDeselectAll(boolean isDoSelectAll){//4.全选和取消全选的时候
        for (int i = 0; i < mAlbumItems.size() ; i++){
            AlbumItem item = mAlbumItems.get(i);
            if (item.getItemType() == ITEM_TIME_TYPE){
                continue;
            }
            mAlbumItems.get(i).setChecked(isDoSelectAll);
            if (isDoSelectAll){
                mCheckedPhotos.add(mAlbumItems.get(i));
            }else{
                mCheckedPhotos.remove(mAlbumItems.get(i));
            }
        }
        mListener.onCheckedPhotoSizeChange(mCheckedPhotos.size());
        notifyDataSetChanged();
    }

    public void setEditingState(boolean isEditingPhotos){
        this.mIsEditingPhotos = isEditingPhotos;
        clearCheckedPhotos();
        notifyDataSetChanged();
    }


    /**
     * 点击下载时候，内部列表UI需要刷新
     */
    public void notifyUIChangeWhenDownloading(){
        for (AlbumItem albumItem : mCheckedPhotos) {
            albumItem.setDownloading(true);
        }
        mCheckedPhotos.clear();
        notifyDataSetChanged();
    }


    public void notifyItemDownloadFinish(String fileName){
        if (TextUtils.isEmpty(fileName)){
            Log.e("0411", "notifyItemDownloadFinish: " + fileName);
            return;
        }
        for (int i = 0; i < mAlbumItems.size(); i++){
            AlbumItem item = mAlbumItems.get(i);
            String imageId = item.getImageId();
            if (fileName.equalsIgnoreCase(imageId)){
                item.setDownloading(false);
                item.setChecked(false);
                mCheckedPhotos.remove(item);
                notifyItemChanged(i);
                break;
            }
        }
    }



    public List<AlbumItem> cloneCheckedPhotos() {
        List<AlbumItem> items = new ArrayList<>();
        items.addAll(mCheckedPhotos);
        return items;
    }

    public List<Integer> getTimeIndex() {
        return mTimeItemIndex;
    }

    private void clearCheckedPhotos(){
        mCheckedPhotos.clear();//对应情况2
        mListener.onCheckedPhotoSizeChange(mCheckedPhotos.size());
        Iterator<AlbumItem> iterator = mAlbumItems.iterator();
        while (iterator.hasNext()){
            AlbumItem albumItem = iterator.next();
            albumItem.setChecked(false);
        }
    }


    private void removeTimeItemFromAlbumItems(){
        List<AlbumItem> totalItem = new ArrayList<>();
        totalItem.addAll(mAlbumItems);
        mAlbumItems.clear();

        for (AlbumItem item: totalItem){
            if (item.getItemType() == ITEM_IMAGE_TYPE){
                mAlbumItems.add(item);//对比对象去删除
            }
        }
        mTimeItemIndex.clear();
        mTimeItemStrings.clear();
    }

    private void mergeTimeItemToAlbums() {
        //计算出TimeItem的位置
        Map<String, List<AlbumItem>> photoMap = AlbumsClassifyUtils.classifyPhotos(mAlbumItems);
        Iterator<Map.Entry<String, List<AlbumItem>>> iterator = photoMap.entrySet().iterator();

        int currentIndex = 0;
        while (iterator.hasNext()) {
            Map.Entry<String, List<AlbumItem>> entry = iterator.next();
            List<AlbumItem> albumItems = entry.getValue();
            mTimeItemIndex.add(currentIndex);
            mTimeItemStrings.put(currentIndex, albumItems.get(0).getImageUploadTime());
            currentIndex += (entry.getValue().size() + 1);//下一个时间头部的index ＝ 前面一组的时间头部Item ＋ 图片Item
        }

        //把每个TimeItem添加到AlbumItem中
        for (Integer integer : mTimeItemIndex) {
            AlbumItem timeItem = new AlbumItem();
            timeItem.setItemType(ITEM_TIME_TYPE);
            timeItem.setImageId(String.valueOf(ITEM_TIME_TYPE));
            mAlbumItems.add(integer, timeItem);
        }
    }


    @Override
    public AlbumsItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == ITEM_TIME_TYPE) {
            view = mLayoutInflater.inflate(R.layout.album_item_layout_time, null);
        } else {
            view = mLayoutInflater.inflate(R.layout.album_item_layout_image, null);
        }
        return new AlbumsItemHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(AlbumsItemHolder holder, int position) {
        switch (holder.getItemType()) {
            case ITEM_IMAGE_TYPE:
                handlePhotoItem(holder, position);
                break;
            case ITEM_TIME_TYPE:
                handleTimeItem(holder, mTimeItemStrings.get(position));
                break;
        }
    }

    private void handleTimeItem(AlbumsItemHolder holder, String timeString) {
        if (Tools.IsToday(timeString)){
            holder.mTimeBinding.tvStamp.setText("今天");
        }else{
            holder.mTimeBinding.tvStamp.setText(Tools.getDate(timeString));
        }
    }

    private void handlePhotoItem(final AlbumsItemHolder holder, final int position) {
        if (mAlbumItems.get(position).isDownloading()){
            holder.mImageBinding.setIsEditingPhotos(false);
            holder.mImageBinding.setIsDownloading(mAlbumItems.get(position).isDownloading());
        }else{
            holder.mImageBinding.setIsDownloading(false);
            holder.mImageBinding.setIsEditingPhotos(mIsEditingPhotos);
            holder.mImageBinding.cbPhoto.setChecked(mAlbumItems.get(position).isChecked());
            if (mAlbumItems.get(position).isChecked()){
                holder.mImageBinding.vBlack.setVisibility(View.VISIBLE);
            }else{
                holder.mImageBinding.vBlack.setVisibility(View.GONE);
            }
        }

        String path = mAlbumItems.get(position).getImageThumbnailUrl();

        File file = new File(path);

        Glide.with(mCtx).load(file).asBitmap().dontAnimate()
                .signature(new MediaStoreSignature("image/jpeg", file.lastModified(), 0)).error(R.drawable.no_photo)
                .into(holder.mImageBinding.imgPhoto);

        holder.mImageBinding.cbPhoto.setClickable(false);//如果不为false，则点击到了CheckBox的时候，事件被消费掉了，造成rootview不能响应事件
        holder.mImageBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsEditingPhotos){//正常显示状态
                    PageRouter.toHDAlbums(mCtx, mAlbumItems, mAlbumItems.get(position));
                    return;
                }
                //处于编辑状态
                boolean newCheckedState = !mAlbumItems.get(position).isChecked();
                if (newCheckedState){

                    mCheckedPhotos.add(mAlbumItems.get(position));
                }else{
                    mCheckedPhotos.remove(mAlbumItems.get(position));
                }
                mListener.onCheckedPhotoSizeChange(mCheckedPhotos.size());
                mListener.onSyncStateOfSelectAll(mCheckedPhotos.size() == (mAlbumItems.size() - mTimeItemIndex.size()));
                mAlbumItems.get(position).setChecked(newCheckedState);//和当前状态相反
                notifyItemChanged(position);
            }
        });

    }


    @Override
    public int getItemCount() {
        return mAlbumItems.size();
    }


    @Override
    public int getItemViewType(int position) {
        for (Integer integer : mTimeItemIndex) {
            if (position == integer) {
                return ITEM_TIME_TYPE;
            }
        }
        return ITEM_IMAGE_TYPE;
    }


    public static class AlbumsItemHolder extends RecyclerView.ViewHolder{
        private int mType;

        private AlbumItemLayoutImageBinding mImageBinding;

        private AlbumItemLayoutTimeBinding  mTimeBinding;

        public AlbumsItemHolder(View itemView, int viewType) {
            super(itemView);
            switch (viewType) {
                case ITEM_IMAGE_TYPE:
                    if (mImageBinding == null) {
                        mImageBinding = DataBindingUtil.bind(itemView);
                    }
                    break;
                case ITEM_TIME_TYPE:
                    if (mTimeBinding == null) {
                        mTimeBinding = DataBindingUtil.bind(itemView);
                    }
                    break;
            }
            this.mType = viewType;
        }

        public int getItemType() {
            return mType;
        }

    }

    interface IPhotoCheckedChangeListener{
        void onCheckedPhotoSizeChange(int checkCount);

        void onSyncStateOfSelectAll(boolean isSelectAll);

        void onAlbumsTotalnumChange(int totalAlbumsCount);
    }

}
