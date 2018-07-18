package com.ubtechinc.alpha.mini.ui.strategy.circledialog;

import android.content.DialogInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;

import com.ubtechinc.alpha.mini.ui.strategy.circledialog.callback.OnInputClickListener;
import com.ubtechinc.alpha.mini.ui.strategy.circledialog.callback.OnRvItemClickListener;
import com.ubtechinc.alpha.mini.ui.strategy.circledialog.param.ButtonParams;
import com.ubtechinc.alpha.mini.ui.strategy.circledialog.param.DialogParams;
import com.ubtechinc.alpha.mini.ui.strategy.circledialog.param.InputParams;
import com.ubtechinc.alpha.mini.ui.strategy.circledialog.param.ItemsParams;
import com.ubtechinc.alpha.mini.ui.strategy.circledialog.param.ProgressParams;
import com.ubtechinc.alpha.mini.ui.strategy.circledialog.param.SubTitleParams;
import com.ubtechinc.alpha.mini.ui.strategy.circledialog.param.TextParams;
import com.ubtechinc.alpha.mini.ui.strategy.circledialog.param.TitleParams;


/**
 * Created by hupei on 2017/3/30.
 */

public class CircleParams implements Parcelable {

    /**
     * 确定按钮点击事件
     */
    public View.OnClickListener clickPositiveListener;

    /**
     * 中间按钮点击事件
     */
    public View.OnClickListener clickNeutralListener;

    /**
     * 取消按钮点击事件
     */
    public View.OnClickListener clickNegativeListener;

    /**
     * 输入框确定事件
     */
    public OnInputClickListener inputListener;

    /**
     * RecyclerView Item点击事件
     */
    public OnRvItemClickListener rvItemListener;

    /**
     * item 点击事件
     */
    public AdapterView.OnItemClickListener itemListener;

    /**
     * dialog 关闭事件
     */
    public DialogInterface.OnDismissListener dismissListener;

    /**
     * dialog 取消事件
     */
    public DialogInterface.OnCancelListener cancelListener;

    /**
     * dialog 显示事件
     */
    public DialogInterface.OnShowListener showListener;

    public DialogParams dialogParams;
    public TitleParams titleParams;
    public SubTitleParams subTitleParams;
    public TextParams textParams;
    public ButtonParams negativeParams;
    public ButtonParams positiveParams;
    public ItemsParams itemsParams;
    public ProgressParams progressParams;
    public InputParams inputParams;
    public ButtonParams neutralParams;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.dialogParams, flags);
        dest.writeParcelable(this.titleParams, flags);
        dest.writeParcelable(this.subTitleParams, flags);
        dest.writeParcelable(this.textParams, flags);
        dest.writeParcelable(this.negativeParams, flags);
        dest.writeParcelable(this.positiveParams, flags);
        dest.writeParcelable(this.itemsParams, flags);
        dest.writeParcelable(this.progressParams, flags);
        dest.writeParcelable(this.inputParams, flags);
        dest.writeParcelable(this.neutralParams, flags);
    }

    public CircleParams() {
    }

    protected CircleParams(Parcel in) {
        this.dialogParams = in.readParcelable(DialogParams.class.getClassLoader());
        this.titleParams = in.readParcelable(TitleParams.class.getClassLoader());
        this.subTitleParams = in.readParcelable(SubTitleParams.class.getClassLoader());
        this.textParams = in.readParcelable(TextParams.class.getClassLoader());
        this.negativeParams = in.readParcelable(ButtonParams.class.getClassLoader());
        this.positiveParams = in.readParcelable(ButtonParams.class.getClassLoader());
        this.itemsParams = in.readParcelable(ItemsParams.class.getClassLoader());
        this.progressParams = in.readParcelable(ProgressParams.class.getClassLoader());
        this.inputParams = in.readParcelable(InputParams.class.getClassLoader());
        this.neutralParams = in.readParcelable(ButtonParams.class.getClassLoader());
    }

    public static final Parcelable.Creator<CircleParams> CREATOR = new Parcelable
            .Creator<CircleParams>() {
        @Override
        public CircleParams createFromParcel(Parcel source) {
            return new CircleParams(source);
        }

        @Override
        public CircleParams[] newArray(int size) {
            return new CircleParams[size];
        }
    };
}
