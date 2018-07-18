package com.ubtechinc.alpha.mini.net;

import android.support.annotation.Keep;

import com.ubtechinc.nets.http.Url;

import java.util.List;
import java.util.Map;

/**
 * @ClassName ChangeRobotEquipmentIdModule
 * @date 7/5/2018
 * @author shiyi.wu
 * @Description 通过机器人流水号获取机器人序列号
 * @modifier
 * @modify_time
 */
@Keep
public class ChangeRobotEquipmentIdModule {
    @Url("/equipment/equipment/listBySerialNum")
    @Keep
    public class Request {
        private String serialNum;

        public String getSerialNum() {
            return serialNum;
        }

        public void setSerialNum(String serialNum) {
            this.serialNum = serialNum;
        }
    }

    @Keep
    public class Response {

        private boolean status;

        private String info;

        private List<Model> models;

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public List<Model> getModels() {
            return models;
        }

        public void setModels(List<Model> models) {
            this.models = models;
        }
    }

    @Keep
    public class Model{

        private String serialNum;

        private String seqNum;

        public String getSerialNum() {
            return serialNum;
        }

        public void setSerialNum(String serialNum) {
            this.serialNum = serialNum;
        }

        public String getSeqNum() {
            return seqNum;
        }

        public void setSeqNum(String seqNum) {
            this.seqNum = seqNum;
        }
    }


}
