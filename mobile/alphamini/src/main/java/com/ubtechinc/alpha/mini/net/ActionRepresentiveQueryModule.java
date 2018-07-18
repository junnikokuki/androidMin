package com.ubtechinc.alpha.mini.net;

import android.support.annotation.Keep;

import com.ubtechinc.nets.http.Url;

import java.io.Serializable;

/**
 * @Date: 2017/12/6.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] : 获取当前用户的代表动作
 */

public class ActionRepresentiveQueryModule {
    @Url("alpha2-web/recommendaction/query")
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
        ActionRepresentiveQueryModule.Data data;

        public ActionRepresentiveQueryModule.Data getData() {
            return data;
        }

        public void setData(ActionRepresentiveQueryModule.Data data) {
            this.data = data;
        }
    }
    @Keep
    public class Data {
        private ActionRepresentiveQueryModule.RepresentiveAction result;

        public RepresentiveAction getResult() {
            return result;
        }

        public void setResult(RepresentiveAction result) {
            this.result = result;
        }
    }


    @Keep
    public class RepresentiveAction implements Serializable {

        /**
         * name : dance
         * description : 跳舞
         * id : 2
         */

        private String name;
        private String description;
        private int id;

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

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }



}
