package com.ubtechinc.alpha.mini.widget.recyclerview.item;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.widget.recyclerview.holder.ImageSelectViewManager;
import com.ubtechinc.alpha.mini.widget.recyclerview.holder.MultiViewHolder;
import com.ubtechinc.alpha.mini.widget.recyclerview.holder.ViewHolderParams;
import com.ubtechinc.alpha.mini.widget.viewpage.utils.TimeFormat;


/**
 * @author free46000  2017/03/17
 * @version v1.0
 */
public class ImageViewManager extends ImageSelectViewManager {

    private Context mContext;
    private final int mType;
    public final static int DETAIL = 0;
    public final static int SELECT = 1;

    public ImageViewManager(Context context, int type) {
        mContext = context;
        mType = type;
    }

    @Override
    protected void onCreateViewHolder(@NonNull MultiViewHolder holder) {
        holder.imageView = getView(holder, R.id.image);
        holder.delete = getView(holder, R.id.delete_up_btn);
        holder.duration = getView(holder, R.id.duration);
    }

    @Override
    public void onBindViewHolder(@NonNull final MultiViewHolder holder, @NonNull Image image, @NonNull final ViewHolderParams params) {
        super.onBindViewHolder(holder, image, params);
        //添加定制化监听
        if (holder.delete != null) {
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    params.getClickListener().onItemClick(holder, v);
                }
            });
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MultiViewHolder holder, @NonNull Image data) {
        //ImageView imageView = getView(holder, R.id.image);
        Glide.with(mContext)
                .load(data.getImgPath())
                .into(holder.imageView);
        if (data.getMineType() == Image.VIDEO) {
            holder.duration.setVisibility(View.VISIBLE);
            holder.duration.setText(TimeFormat.getTime(data.getDuration()));
        }
        switch (mType) {
            case DETAIL:
                //select.setVisibility(View.GONE);
                if (data.typeInView == Image.FOOTER) {
                    holder.delete.setVisibility(View.GONE);
                }
                break;
            case SELECT:
                TextView select = getView(holder, R.id.select_mark_tv);
                select.setVisibility(data.typeInView == Image.NORMAL ? View.VISIBLE : View.GONE);
                if (data.getSelectedIndex() != -1) {
                    select.setText(String.valueOf(data.getSelectedIndex()));
                    select.setBackground(mContext.getResources().getDrawable(R.drawable.shape_circle_text_select));
                } else {
                    select.setText("");
                    select.setBackground(mContext.getResources().getDrawable(R.drawable.shape_circle_text_unselect));
                }
                break;
        }
        //TextView textView = getView(holder, R.id.select_mark_tv);
        //textView.setText(String.valueOf(holder.getItemPosition()));
        //imageView.setImageResource(data.getImg());
    }

    @Override
    protected int getItemLayoutId() {
        switch (mType) {
            case SELECT:
                return R.layout.item_image_select;
            case DETAIL:
                return R.layout.item_image_upload;
                default:
                    throw new IllegalStateException("unknown type");
        }

    }

}
