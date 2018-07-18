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
 * [A brief description] :推荐口头禅列表
 */

public class PetphraseRecomandListModule {
    @Url("alpha2-web/petphrase/list")
    @Keep
    public class Request {

    }

    @Keep
    public class Response extends BaseResponse {

        private PetphraseRecomandListModule.Data data;

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }
    }

    @Keep
    public class Data {
        private List<RecomandPhrase> result;

        public List<RecomandPhrase> getResult() {
            return result;
        }

        public void setResult(List<RecomandPhrase> result) {
            this.result = result;
        }
    }

    @Keep
    public class RecomandPhrase implements Serializable {

        /**
         * id : 2
         * content : 哈哈哈
         * createTime : 1512466454000
         * updateTime : 1512466456000
         */

        private int id;
        private String content;
        private long createTime;
        private long updateTime;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
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
