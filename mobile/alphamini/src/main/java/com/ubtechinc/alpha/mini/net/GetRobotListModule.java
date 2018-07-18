package com.ubtechinc.alpha.mini.net;

import android.support.annotation.Keep;

import com.ubtechinc.alpha.mini.entity.RobotInfo;
import com.ubtechinc.nets.http.Url;

import java.util.List;

/**
 * @ClassName GetRobotListModule
 * @date 1/11/2017
 * @author shiyi.wu
 * @Description 获取机器人列表
 * @modifier
 * @modify_time
 */
@Keep
public class GetRobotListModule {
    @Url("alpha2-web/relation/getRobots")
    @Keep
    public class Request {

    }

    @Keep
    public class Response extends BaseResponse {

        private Data data;

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }
    }

    @Keep
    public class Data {
        private List<RobotInfo> result;

        public List<RobotInfo> getResult() {
            return result;
        }

        public void setResult(List<RobotInfo> result) {
            this.result = result;
        }
    }

}
