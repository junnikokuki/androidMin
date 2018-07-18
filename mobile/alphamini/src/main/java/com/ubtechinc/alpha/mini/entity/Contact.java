package com.ubtechinc.alpha.mini.entity;


import android.text.TextUtils;

import com.ubtechinc.alpha.mini.entity.bean.BasePinYinTagBean;
import com.ubtechinc.alpha.mini.utils.ChineseToPinYinUtils;
import com.ubtechinc.alpha.mini.database.EntityManagerHelper;
import com.ubtechinc.framework.db.annotation.Column;
import com.ubtechinc.framework.db.annotation.Id;
import com.ubtechinc.framework.db.annotation.Table;

import static com.ubtechinc.framework.db.annotation.GenerationType.ASSIGN;

/**
 * @Date: 2018/1/30.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] : 联系人
 */

@Table(version = EntityManagerHelper.DB_CONTACT_TABLE_VERSION)
public class Contact extends BasePinYinTagBean {

    @Column
    private String name;
    /**
     * 是否已经被添加到mini通讯录中, 本地字段，不需要同步至其他端
     **/
    private boolean isAdded;
    /**
     * 是否选中, 本地字段，不需要同步至其他端
     **/
    private boolean isSelected;
    /**
     * 姓名是否非法, 本地字段，不需要同步至其他端
     **/
    private boolean nameInvalide;

    @Id(strategy = ASSIGN)
    private String id;

    @Column
    private String robotId;

    @Column
    private long contactId;

    @Column
    private String phone;

    @Column
    private int isDeleted;

    @Column
    private String namePy; //名称的全拼音字段

    public boolean isNameInvalide() {
        return nameInvalide;
    }

    public void setNameInvalide(boolean nameInvalide) {
        this.nameInvalide = nameInvalide;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean added) {
        isAdded = added;
    }

    public Contact(String name) {
        this.name = name;
    }

    public Contact() {

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.namePy = ChineseToPinYinUtils.chineseToPinYin(name);
        if (!TextUtils.isEmpty(namePy)) {
            String firstLetter = namePy.substring(0, 1).toUpperCase();
            if (firstLetter.matches("[A-Z]")) {//如果是A-Z字母开头
                setFirstLetterPy(firstLetter);
            } else {//特殊字母这里统一用#处理
                setFirstLetterPy("#");
            }
        }
    }

    @Override
    public String getTarget() {
        return name;
    }

    public String getRobotId() {
        return robotId;
    }

    public void setRobotId(String robotId) {
        this.robotId = robotId;
    }

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int isDeleted() {
        return isDeleted;
    }

    public void setDeleted(int deleted) {
        isDeleted = deleted;
    }

    public String getNamePy() {
        if (namePy == null) {
            namePy = ChineseToPinYinUtils.chineseToPinYin(name);
            if (!TextUtils.isEmpty(namePy)) {
                String firstLetter = namePy.substring(0, 1).toUpperCase();
                if (firstLetter.matches("[A-Z]")) {//如果是A-Z字母开头
                    setFirstLetterPy(firstLetter);
                } else {//特殊字母这里统一用#处理
                    setFirstLetterPy("#");
                }
            }
        }
        return namePy;
    }

    public void setNamePy(String namePy) {
        this.namePy = namePy;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "name='" + name + '\'' +
                ", isAdded=" + isAdded +
                ", isSelected=" + isSelected +
                ", id='" + id + '\'' +
                ", robotId='" + robotId + '\'' +
                ", contactId=" + contactId +
                ", phone='" + phone + '\'' +
                ", isDeleted=" + isDeleted +
                ", namePy='" + namePy + '\'' +
                '}';
    }

    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Contact) {
            if (getId() == null) {
                return super.equals(obj);
            }
            return ((Contact) obj).getId().equals(getId());
        } else {
            return false;
        }
    }

    @Override
    public String getFirstLetterPy() {
        if(super.getFirstLetterPy() == null){
            if (!TextUtils.isEmpty(namePy)) {
                String firstLetter = namePy.substring(0, 1).toUpperCase();
                if (firstLetter.matches("[A-Z]")) {//如果是A-Z字母开头
                    setFirstLetterPy(firstLetter);
                } else {//特殊字母这里统一用#处理
                    setFirstLetterPy("#");
                }
            }else{
                getNamePy();
            }
            if(super.getFirstLetterPy() == null){
                setFirstLetterPy("#");
            }
        }
        return super.getFirstLetterPy();
    }
}
