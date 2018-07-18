package com.ubtechinc.alpha.mini.avatar.viewutils;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubtechinc.alpha.mini.avatar.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @作者：liudongyang
 * @日期: 18/5/24 19:56
 * @描述:
 */

public class AvatarUserLists {



    private List<ImageView> avatarCount = new ArrayList<>();

    private ImageView ivOne;
    private ImageView ivTwo;
    private ImageView ivThree;
    private ImageView ivFour;
    private ImageView ivFive;
    private ImageView ivSix;
    private TextView tvCount;


    public AvatarUserLists(View view) {
        ivOne = view.findViewById(R.id.iv_user_one);
        ivTwo = view.findViewById(R.id.iv_user_two);
        ivThree = view.findViewById(R.id.iv_user_three);
        ivFour = view.findViewById(R.id.iv_user_four);
        ivFive = view.findViewById(R.id.iv_user_five);
        ivSix = view.findViewById(R.id.iv_user_six);
        avatarCount.add(ivOne);
        avatarCount.add(ivTwo);
        avatarCount.add(ivThree);
        avatarCount.add(ivFour);
        avatarCount.add(ivFive);
        avatarCount.add(ivSix);
        tvCount = view.findViewById(R.id.tv_user_count);
    }
}
