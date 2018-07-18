package com.ubtechinc.alpha.mini.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.ubtech.utilcode.utils.constant.MemoryConstant;
import com.ubtechinc.alpha.mini.database.EntityManagerHelper;
import com.ubtechinc.framework.db.annotation.Column;
import com.ubtechinc.framework.db.annotation.Id;
import com.ubtechinc.framework.db.annotation.Table;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.ubtechinc.framework.db.annotation.GenerationType.ASSIGN;

/**
 * @作者：liudongyang
 * @日期: 18/3/19 11:45
 * @描述: 相册实体
 * 
 */

@Table(version = EntityManagerHelper.DB_ALBUM_TABLE_VERSION)
public class AlbumItem implements Parcelable {

    @Id(strategy = ASSIGN)
    private String mId;                 //mImageId + mUserId
    @Column
    private String mOriginUrl;          //原图的路径
    @Column
    private String mImageThumbnailUrl;  //缩略图的路径
    @Column
    private String mImageHDUrl;         //高清缩略图的路径
    @Column
    private String mImageId;            //每图片的Id
    @Column
    private long   mCreateTime;         //创建时间
    @Column
    private String mImageUploadTime;    //上传的时间
    @Column
    private String mRobotId;            //对应的
    @Column
    private String mPath;               //机器人的路径
    @Column
    private long   mSize;                  //大小

    /**本地字段 start, 用于Adapter的差异化显示**/

    private boolean mIsChecked;

    private boolean mIsDownloading;

    private boolean mIsLoadSucc;//不用被序列化,详情页里面表示是否加载成功

    private int     mType = 0;
    /**本地字段 end**/

    public AlbumItem(){

    }

    public AlbumItem(Parcel in) {
        mId                = in.readString();
        mOriginUrl         = in.readString();
        mImageThumbnailUrl = in.readString();
        mImageId           = in.readString();
        mImageUploadTime   = in.readString();
        mImageHDUrl        = in.readString();
        mRobotId           = in.readString();
        mPath              = in.readString();
        mCreateTime        = in.readLong();
        mType              = in.readInt();
        mSize               = in.readLong();
    }


    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public String getImageId() {
        return mImageId;
    }

    public void setImageId(String mImage_id) {
        this.mImageId = mImage_id;
    }

    public String getImageUploadTime() {
        return mImageUploadTime;
    }

    public void setImageUploadTime(String image_upload_time) {
        if (image_upload_time == null){
            mImageUploadTime = "";
            return;
        }
        mImageUploadTime = image_upload_time;
        Log.i("0327", "setImageUploadTime: " + mImageUploadTime);
    }


    public String getImageHDUrl() {
        return mImageHDUrl;
    }

    public void setImageHDUrl(String mImageHDUrl) {
        this.mImageHDUrl = mImageHDUrl;
    }

    public long getCreateTime() {
        return mCreateTime;
    }

    public void setCreateTime(long mCreateTime) {
        this.mCreateTime = mCreateTime;
    }


    public String getDate() {
        return new SimpleDateFormat("yyyy-MM-dd")
                .format(new Date(getCreateTime()));
    }

    public String getOriginUrl() {
        return mOriginUrl;
    }

    public void setImageOriginUrl(String originUrl) {
        this.mOriginUrl = originUrl;
    }

    public String getRobotId() {
        return mRobotId;
    }

    public void setRobotId(String mRobotId) {
        this.mRobotId = mRobotId;
    }


    public boolean isDownloading() {
        return mIsDownloading;
    }

    public void setDownloading(boolean downloading) {
        mIsDownloading = downloading;
    }

    public boolean isChecked() {
        return mIsChecked;
    }

    public void setChecked(boolean checked) {
        mIsChecked = checked;
    }


    public int getItemType() {
        return mType;
    }

    public void setItemType(int mType) {
        this.mType = mType;
    }

    public long getSize() {
        return mSize;
    }

    public void setSize(long size) {
        this.mSize = size;
    }

    public String getImageThumbnailUrl() {
        return mImageThumbnailUrl;
    }

    public void setImageThumbnailUrl(String imageThumbnailUrl) {
        this.mImageThumbnailUrl = imageThumbnailUrl;
    }


    public String getPath() {
        return mPath;
    }

    public void setPath(String mPath) {
        this.mPath = mPath;
    }

    public boolean isIsLoadSucc() {
        return mIsLoadSucc;
    }

    public void setIsLoadSucc(boolean mIsLoadSucc) {
        this.mIsLoadSucc = mIsLoadSucc;
    }

    public String computeOriginSize(){
        return byteNumerToMemerySize(mSize);
    }

    public long computSize(){
        return mSize;
    }

    public long computeThumbSize(){
        return (long) (mSize * 0.054);
    }

    public String byteNumerToMemerySize(double size){
        if (size < 0) {
            return "shouldn't be less than zero!";
        } else if (size < MemoryConstant.KB) {
            return String.format("%.0fB",  size);
        } else if (size < MemoryConstant.MB) {
            return String.format("%.0fKB", size / MemoryConstant.KB);
        } else if (size < MemoryConstant.GB) {
            return String.format("%.2fMB", size / MemoryConstant.MB);
        } else {
            return String.format("%.2fGB", size / MemoryConstant.GB);
        }
    }


    public static final Creator<AlbumItem> CREATOR = new Creator<AlbumItem>() {
        @Override
        public AlbumItem createFromParcel(Parcel in) {
            return new AlbumItem(in);
        }

        @Override
        public AlbumItem[] newArray(int size) {
            return new AlbumItem[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AlbumItem)) return false;

        AlbumItem item = (AlbumItem) o;

        return getId().equals(item.getId());
    }

    @Override
    public int hashCode() {
        return getImageId().hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mOriginUrl);
        dest.writeString(mImageThumbnailUrl);
        dest.writeString(mImageId);
        dest.writeString(mImageUploadTime);
        dest.writeString(mImageHDUrl);
        dest.writeString(mRobotId);
        dest.writeString(mPath);
        dest.writeLong(mCreateTime);
        dest.writeInt(mType);
        dest.writeLong(mSize);
    }


    @Override
    public String toString() {
        return "AlbumItem{" +
                "mId='" + mId + '\'' +
                ", mOriginUrl='" + mOriginUrl + '\'' +
                ", mImageThumbnailUrl='" + mImageThumbnailUrl + '\'' +
                ", mImageHDUrl='" + mImageHDUrl + '\'' +
                ", mImageId='" + mImageId + '\'' +
                ", mCreateTime=" + mCreateTime +
                ", mImageUploadTime='" + mImageUploadTime + '\'' +
                ", mRobotId='" + mRobotId + '\'' +
                ", mPath='" + mPath + '\'' +
                ", mSize=" + mSize +
                '}';
    }
}
