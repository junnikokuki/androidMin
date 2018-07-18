package com.ubtechinc.alpha.mini.ui.car;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.databinding.CarRecycleItemAuthwordsBinding;
import com.ubtechinc.alpha.mini.widget.ProgressImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @作者：liudongyang
 * @日期: 18/7/5 14:26
 * @描述: 官方台词适配器
 */
public class AuthWordsAdapter extends RecyclerView.Adapter<AuthWordsAdapter.AuthWordsViewHolder> {

    private String TAG = getClass().getSimpleName();

    private List<AuthWords> mAuthWordsLists;

    private LayoutInflater mInflater;

    private Context mCtx;

    private boolean mIsPoliceMode;

    private int[] background = new int[]{
            R.drawable.car_auth_words_yellow_bg,
            R.drawable.car_auth_words_pink_bg,
            R.drawable.car_auth_words_green_bg,
            R.drawable.car_auth_words_light_blue_bg,
            R.drawable.car_auth_words_blue_dark_bg,
            R.drawable.car_auth_words_puple_bg,
    };

    public AuthWordsAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mCtx = context;
        genratorWords();
    }

    public void setMode(boolean isPoliceMode){
        this.mIsPoliceMode = isPoliceMode;
        notifyDataSetChanged();
    }

    private void genratorWords() {
        mAuthWordsLists = new ArrayList<>();
        AuthWords liaoren = new AuthWords();
        liaoren.setContent("打扰你一下，请问你的心怎么走?");
        liaoren.setSayWords("\"撩人\"");
        liaoren.setPlayTime(5000);
        liaoren.setAvatar(R.drawable.emotion_car_liao);
        mAuthWordsLists.add(liaoren);

        AuthWords laugh = new AuthWords();
        laugh.setContent("哈哈哈哈哈哈哈哈嗝！哈哈哈哈哈哈！");
        laugh.setSayWords("\"嘲笑\"");
        laugh.setPlayTime(5000);
        laugh.setAvatar(R.drawable.emotion_car_laugh);
        mAuthWordsLists.add(laugh);

        AuthWords greetings = new AuthWords();
        greetings.setContent("你好呀，我的车是不是很酷！是不是很想坐？可我是不会载你的!");
        greetings.setSayWords("\"打招呼\"");
        laugh.setPlayTime(5000);
        greetings.setAvatar(R.drawable.emotion_car_greetings);
        mAuthWordsLists.add(greetings);
    }

    @Override
    public AuthWordsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.car_recycle_item_authwords, null);
        return new AuthWordsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AuthWordsViewHolder holder, final int position) {
        if (mIsPoliceMode){
            holder.binding.rlAuthWordsItem.setBackground(ContextCompat.getDrawable(mCtx, R.drawable.bg_car_dialog));
        }else{
            holder.binding.rlAuthWordsItem.setBackground(ContextCompat.getDrawable(mCtx, background[generatorNumber(position)]));
        }
        holder.binding.ivAvatar.setImageResource(mAuthWordsLists.get(position).getAvatar());
        holder.binding.tvSayWords.setText(mAuthWordsLists.get(position).getSayWords());
        holder.binding.tvContentWords.setText(mAuthWordsLists.get(position).getContent());
        setPlayWords(holder, position);
    }

    private void setPlayWords(final AuthWordsViewHolder holder, final int position) {
        if (!mAuthWordsLists.get(position).isRunning()){
            holder.binding.pbPlayWords.cancel();
        }
        holder.binding.pbPlayWords.setMode(mIsPoliceMode);
        holder.binding.rlAuthWordsItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mAuthWordsLists.get(position).isRunning()){
                    cancelAllAnimation();
                    mAuthWordsLists.get(position).setRunning(true);
                    holder.binding.pbPlayWords.startPlay();
                    notifyDataSetChanged();
                    String words = holder.binding.tvSayWords.getText().toString();
                    CarMsgManager.robotControlCmd(CarMsgManager.CUSTOM_CHAT_MODE, words);
                }
            }
        });
        holder.binding.pbPlayWords.setCallback(new ProgressImageView.AnimationListener() {
            @Override
            public void onAnmiationEnd() {
                mAuthWordsLists.get(position).setRunning(false);
                holder.binding.pbPlayWords.stopPlay();
            }

            @Override
            public void onAnmiationCancel() {
                // TODO: 18/7/10 被打断可能要执行的业务操作

            }
        });
    }

    private int generatorNumber(int position){
        return position % 6;
    }

    public void cancelAllAnimation(){
        for (AuthWords words:mAuthWordsLists){
            words.setRunning(false);
        }
    }

    @Override
    public int getItemCount() {
        return mAuthWordsLists != null ? mAuthWordsLists.size() : 0;
    }

    public static class AuthWordsViewHolder extends RecyclerView.ViewHolder {

        public CarRecycleItemAuthwordsBinding binding;

        public AuthWordsViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }

}
