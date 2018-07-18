package com.ubtechinc.alpha.mini.net;

import android.support.annotation.Keep;

import com.ubtechinc.nets.http.Url;

import java.io.Serializable;

/**
 * @Date: 2017/12/6.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] : 查询个人的口头禅
 */

public class PetphraseQueryModule {

    @Url("alpha2-web/petphrase/query")
    @Keep
    public class Request {
        private String robotUserId;

        public String getRobotUserId() {
            return robotUserId;
        }

        public void setRobotUserId(String robotUserId) {
            this.robotUserId = robotUserId;
        }
    }

    @Keep
    public class Response extends BaseResponse {

        private PetphraseQueryModule.Data data;

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }
    }

    @Keep
    public class Data {
        private UserPhrase result;

        public UserPhrase getResult() {
            return result;
        }

        public void setResult(UserPhrase result) {
            this.result = result;
        }
    }


    @Keep
    public class UserPhrase implements Serializable {
        /**
         * id : 2
         * userId : 745792
         * content : 你真高笑啊大哥
         * equipmentId : 123
         */

        private int id;
        private int userId;
        private String content;
        private String equipmentId;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getEquipmentId() {
            return equipmentId;
        }

        public void setEquipmentId(String equipmentId) {
            this.equipmentId = equipmentId;
        }
    }
}
