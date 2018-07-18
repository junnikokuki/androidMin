package com.ubtechinc.alpha.mini.net;

import android.support.annotation.Keep;

import com.ubtechinc.nets.http.Url;

import java.io.Serializable;
import java.util.List;

/**
 * @Date: 2017/12/6.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] : 获取代表动作列表
 */

public class ActionRepresentiveRecomandListModule{

    @Url("alpha2-web/recommendaction/list")
    @Keep
    public class Request {

    }

    @Keep
    public class Response extends BaseResponse {
        ActionRepresentiveRecomandListModule.Data data;

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }
    }

    @Keep
    public class Data {
        private List<ActionRepresentiveRecomandListModule.RecomandAction> result;

        public List<RecomandAction> getResult() {
            return result;
        }

        public void setResult(List<RecomandAction> result) {
            this.result = result;
        }
    }

    @Keep
    public class RecomandAction implements Serializable {
        /**
         * id : 2
         * name : dance
         * description : 跳舞
         * isDefault : 1
         * createTime : 1512466943000
         * updateTime : 1512466944000
         */

        private int id;
        private String name;
        private String description;
        private int isDefault;
        private long createTime;
        private long updateTime;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getIsDefault() {
            return isDefault;
        }

        public void setIsDefault(int isDefault) {
            this.isDefault = isDefault;
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
    }


}
