package com.ubtechinc.alpha.mini.avatar.widget;

import java.util.ArrayList;

/**
 * Created by panxiaohe on 16/3/20.
 * 继承
 */
public class AvatarActionModel extends FavoritesItem {

    /**
     * 动作英文名
     */
    private String actionNameEN;
    /**
     * 动作中文名
     */
    private String actionNameCN;

    /**
     * 显示名称
     */
    private String actionNameAlias;
    /**
     * 动作类型普通还是阿凡达
     */
    private String type;
    /**
     * 动作类型
     */
    private String actionType;
    /**
     * 图片链接
     */
    private String actionPackageUrl;
    /**
     * 动作版本号
     */
    private String version;
    /**
     * 动作ID
     */
    private String actionId;

    /**
     * 正常图片地址
     */
    private String lightImageUrl;


    private String shadowImageUrl;
    private ArrayList<AvatarActionModel> menuList;


    private String actionMD5;

    private String productType;


    private String modeType;


    private String dottedImageUrl;

    private int actionDuration;

    /**
     * 正常图片地址
     */
    private int lightImageIconId;


    private int shadowImageIconId;


    public void setActionNameEN(String actionNameEN) {
        this.actionNameEN = actionNameEN;
    }

    public String getActionNameEN() {
        return actionNameEN;
    }


    @Override
    public boolean isFolder() {
        return menuList != null;
    }

    @Override
    public void addSubButton(FavoritesItem item, String defaultFolderName) {

        if (menuList == null || menuList.size() == 0) {
            menuList = new ArrayList<>();
            AvatarActionModel newItem = new AvatarActionModel();
            newItem.copy(this);
            menuList.add(newItem);
            setActionId("-1");
            setActionNameEN(defaultFolderName);
        }
        menuList.add((AvatarActionModel) item);
    }

    @Override
    protected void addSubItem(int position, FavoritesItem item) {
        menuList.add(position, (AvatarActionModel) item);
    }

    @Override
    protected AvatarActionModel removeSubItem(int position) {
        return menuList.remove(position);
    }

    @Override
    public void removeSubButton(int position) {
        menuList.remove(position);

        if (menuList.size() == 1) {
            copy(menuList.get(0));
            menuList = null;
        }
    }

    private void copy(AvatarActionModel avatarActionModel) {
        this.actionNameEN = avatarActionModel.actionNameEN;
        this.actionId = avatarActionModel.actionId;
    }

    @Override
    public int getSubItemCount() {
        return menuList == null ? 0 : menuList.size();
    }

    @Override
    public AvatarActionModel getSubItem(int position) {
        if (menuList != null) {
            if (menuList.size() > position) {
                return menuList.get(position);
            }

        }
        throw new IllegalStateException("按钮列表是空");
    }


    public AvatarActionModel(String actionId, String actionEnName, int icon) {
        this.actionNameEN = actionEnName;
        this.actionId = actionId;
    }

    public AvatarActionModel() {
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getActionPackageUrl() {
        return actionPackageUrl;
    }

    public void setActionPackageUrl(String actionPackageUrl) {
        this.actionPackageUrl = actionPackageUrl;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String getActionId() {
        return actionId;
    }

    @Override
    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public String getLightImageUrl() {
        return lightImageUrl;
    }

    public void setLightImageUrl(String lightImageUrl) {
        this.lightImageUrl = lightImageUrl;
    }

    public String getShadowImageUrl() {
        return shadowImageUrl;
    }

    public void setShadowImageUrl(String shadowImageUrl) {
        this.shadowImageUrl = shadowImageUrl;
    }

    public ArrayList<AvatarActionModel> getMenuList() {
        return menuList;
    }

    public void setMenuList(ArrayList<AvatarActionModel> menuList) {
        this.menuList = menuList;
    }

    public String getActionNameCN() {
        return actionNameCN;
    }

    public void setActionNameCN(String actionNameCN) {
        this.actionNameCN = actionNameCN;
    }


    public String getActionMD5() {
        return actionMD5;
    }

    public void setActionMD5(String actionMD5) {
        this.actionMD5 = actionMD5;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getModeType() {
        return modeType;
    }

    public void setModeType(String modeType) {
        this.modeType = modeType;
    }

    public String getDottedImageUrl() {
        return dottedImageUrl;
    }

    public void setDottedImageUrl(String dottedImageUrl) {
        this.dottedImageUrl = dottedImageUrl;
    }

    public int getActionDuration() {
        return actionDuration;
    }

    public void setActionDuration(int actionDuration) {
        this.actionDuration = actionDuration;
    }

    @Override
    public String toString() {
        return "AvatarActionModel{" +
                "actionNameEN='" + actionNameEN + '\'' +
                ", actionNameCN='" + actionNameCN + '\'' +
                ", actionNameAlias='" + actionNameAlias + '\'' +
                ", type='" + type + '\'' +
                ", actionType='" + actionType + '\'' +
                ", actionPackageUrl='" + actionPackageUrl + '\'' +
                ", version='" + version + '\'' +
                ", actionId='" + actionId + '\'' +
                ", lightImageUrl='" + lightImageUrl + '\'' +
                ", shadowImageUrl='" + shadowImageUrl + '\'' +
                ", menuList=" + menuList +
                ", actionMD5='" + actionMD5 + '\'' +
                ", productType='" + productType + '\'' +
                ", modeType='" + modeType + '\'' +
                ", dottedImageUrl='" + dottedImageUrl + '\'' +
                ", actionDuration=" + actionDuration +
                '}';
    }

    public String getActionNameAlias() {
        return actionNameAlias;
    }

    public void setActionNameAlias(String actionNameAlias) {
        this.actionNameAlias = actionNameAlias;
    }

    public int getLightImageIconId() {
        return lightImageIconId;
    }

    public void setLightImageIconId(int lightImageIconId) {
        this.lightImageIconId = lightImageIconId;
    }

    public int getShadowImageIconId() {
        return shadowImageIconId;
    }

    public void setShadowImageIconId(int shadowImageIconId) {
        this.shadowImageIconId = shadowImageIconId;
    }
}
