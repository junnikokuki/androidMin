package com.ubtechinc.alpha.mini.entity;

import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.text.TextUtils;

import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.database.EntityManagerHelper;
import com.ubtechinc.alpha.mini.utils.ResourceUtils;
import com.ubtechinc.alpha.mini.utils.TimeUtils;
import com.ubtechinc.framework.db.annotation.Column;
import com.ubtechinc.framework.db.annotation.Id;
import com.ubtechinc.framework.db.annotation.Table;

import java.io.Serializable;
import java.util.Date;

import static com.ubtechinc.framework.db.annotation.GenerationType.ASSIGN;
import static com.ubtechinc.framework.db.annotation.GenerationType.AUTO_INCREMENT;
import static com.ubtechinc.framework.db.annotation.GenerationType.UUID;


/**
* @Description 编程作品
* @Author tanghongyu
* @Time  2018/5/19 10:19
*/
@Table(version = EntityManagerHelper.DB_CODING_OPUS_VERSION)
public class CodingOpus implements Serializable {
    @Id(strategy = ASSIGN)
    private String id;
    @Column
    private String idNet;

    @Column
    private String idLocal;

    @Column
    private String userId;

    @Column
    private String productType;

    @Column
    private String opusSource;

    @Column
    private String opusName;

    @Column
    private String opusContent;

    @Column
    private String opusTag;

    @Column
    private String sdkVersion;

    @Column
    private long createTime;

    @Column
    private long updateTime;


    public String getIdNet() {
        return idNet;
    }

    public void setIdNet(String idNet) {
        this.idNet = idNet;
    }

    public String getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(String idLocal) {
        this.idLocal = idLocal;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getOpusSource() {
        return opusSource;
    }

    public void setOpusSource(String opusSource) {
        this.opusSource = opusSource;
    }

    public String getOpusName() {
        return opusName;
    }

    public void setOpusName(String opusName) {
        this.opusName = opusName;
    }

    public String getOpusContent() {
        return opusContent;
    }

    public void setOpusContent(String opusContent) {
        this.opusContent = opusContent;
    }

    public String getOpusTag() {
        return opusTag;
    }

    public void setOpusTag(String opusTag) {
        this.opusTag = opusTag;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    public void setSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof CodingOpus) {
            return ((CodingOpus) obj).getIdLocal().equals(getIdLocal());
        } else {
            return false;
        }
    }
}
