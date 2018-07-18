package com.ubtechinc.alpha.mini.bindingadapter;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

public class ImageLevelAdapter {

    @BindingAdapter("img_level")
    public static void setImgLevel(ImageView view, int level) {
        view.setImageLevel(level);
    }

}
