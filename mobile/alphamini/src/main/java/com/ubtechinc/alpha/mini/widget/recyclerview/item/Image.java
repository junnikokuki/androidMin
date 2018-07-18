package com.ubtechinc.alpha.mini.widget.recyclerview.item;

/**
 * Created by junsheng.chen on 2018/7/6.
 */
public class Image<T> {

    private T path;

    private long time;

    private String name;

    private int selectedIndex = -1;

    public final int typeInView;

    //public final static int MINE_TYPE = 1;

    public static final int HEADER = 0;
    public static final int NORMAL = 1;
    public static final int FOOTER = 2;

    public static final int VIDEO = 2;
    public static final int IMAGE = 1;

    private final int mineType;

    private long duration;

    public Image(T imgPath, String name, long time, int typeInView, int mineType) {
        this.path = imgPath;
        this.name = name;
        this.time = time;
        this.typeInView = typeInView;
        this.mineType = mineType;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public T getImgPath() {
        return path;
    }

    public void setImgPath(T path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getMineType() {
        return mineType;
    }

    @Override
    public String toString() {
        return path + "";
    }
}
