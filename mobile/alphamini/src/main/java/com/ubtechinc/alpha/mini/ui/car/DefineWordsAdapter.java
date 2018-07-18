package com.ubtechinc.alpha.mini.ui.car;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.databinding.CarRecycleItemDefinewordsBinding;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;
import com.ubtechinc.alpha.mini.widget.ProgressImageView;
import com.ubtechinc.alpha.mini.widget.WordsPopWindow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @作者：liudongyang
 * @日期: 18/7/5 17:48
 * @描述: 自定义的台词
 */
public class DefineWordsAdapter extends RecyclerView.Adapter<DefineWordsAdapter.DefineWordsViewHolder> {

    private String TAG = getClass().getSimpleName();

    private List<DefineWords> mOwnWords = new ArrayList<>();

    private Context mCtx;

    private LayoutInflater mInflater;

    private boolean mIsPoliceMode;

    private int[] background = new int[]{
            R.drawable.car_auth_words_yellow_bg,
            R.drawable.car_auth_words_pink_bg,
            R.drawable.car_auth_words_green_bg,
            R.drawable.car_auth_words_light_blue_bg,
            R.drawable.car_auth_words_blue_dark_bg,
            R.drawable.car_auth_words_puple_bg,
    };

    public DefineWordsAdapter(Context context) {
        this.mCtx = context;
        List<String> strings = Arrays.asList(context.getResources().getStringArray(R.array.defineOwnWords));
        generatorWords(strings);
        mInflater = LayoutInflater.from(context);
    }

    private void generatorWords(List<String> strings) {
        for (String string:strings){
            DefineWords words = new DefineWords();
            words.setPlayTime(5000);
            words.setContent(string);
            mOwnWords.add(words);
        }
    }

    public void setMode(boolean isPoliceMode){
        this.mIsPoliceMode = isPoliceMode;
        notifyDataSetChanged();
    }

    public void addDefineWords(String ownWords){
        DefineWords words = new DefineWords();
        words.setContent(ownWords);
        words.setPlayTime(5000);
        mOwnWords.add(words);
        notifyDataSetChanged();
    }

    @Override
    public DefineWordsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.car_recycle_item_definewords, null);
        return new DefineWordsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DefineWordsViewHolder holder, final int position) {
        if (mIsPoliceMode){
            holder.binding.rlDefineWordsItem.setBackground(ContextCompat.getDrawable(mCtx, R.drawable.bg_car_dialog));
        }else{
            holder.binding.rlDefineWordsItem.setBackground(ContextCompat.getDrawable(mCtx, background[generatorNumber(position)]));
        }
        holder.binding.tvDefineWords.setText(mOwnWords.get(position).getContent() + "    ");
        if (!mOwnWords.get(position).isRunning()){
            holder.binding.ivPlayerControl.cancel();
        }
        holder.binding.ivPlayerControl.setMode(mIsPoliceMode);
        holder.binding.rlDefineWordsItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mOwnWords.get(position).isRunning()){
                    cancelAllAnimation();
                    mOwnWords.get(position).setRunning(true);
                    holder.binding.ivPlayerControl.startPlay();
                    notifyDataSetChanged();
                    String words = holder.binding.tvDefineWords.getText().toString();
                    CarMsgManager.robotControlCmd(CarMsgManager.CUSTOM_CHAT_MODE, words);
                }
            }
        });
        holder.binding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.i(TAG, "root onLongClick: " );
                popDeleteItemDialog(holder.binding.getRoot(),position);
                return true;
            }
        });
        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "root click: " );
            }
        });
        holder.binding.ivPlayerControl.setCallback(new ProgressImageView.AnimationListener() {
            @Override
            public void onAnmiationEnd() {
                mOwnWords.get(position).setRunning(false);
                holder.binding.ivPlayerControl.stopPlay();
            }

            @Override
            public void onAnmiationCancel() {
                // TODO: 18/7/10 被打断可能要执行的业务操作
            }
        });
    }

    private void cancelAllAnimation(){
        for (DefineWords words:mOwnWords){
            words.setRunning(false);
        }
    }

    private int generatorNumber(int position) {
        return position % 6;
    }

    private void popDeleteItemDialog(View view, int deletePosi){
        WordsPopWindow window = new WordsPopWindow(mCtx, deletePosi, new WordsPopWindow.IDeleteWordsCallback() {
            @Override
            public void onDelete(int deleteIndex) {
                deleteWords(deleteIndex);
            }
        });
        window.setMode(mIsPoliceMode);
        window.show(view, view.getWidth()/2, 0);
    }

    private void deleteWords(int position){
        if (position < mOwnWords.size()){
            mOwnWords.remove(position);
        }
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return mOwnWords != null ? mOwnWords.size() : 0;
    }

    public class DefineWordsViewHolder extends RecyclerView.ViewHolder {

        public CarRecycleItemDefinewordsBinding binding;

        public DefineWordsViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }

}
