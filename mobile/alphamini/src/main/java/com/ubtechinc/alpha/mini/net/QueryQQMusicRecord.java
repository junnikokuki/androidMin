package com.ubtechinc.alpha.mini.net;

import com.ubtechinc.nets.http.Url;

import java.util.List;

/**
 * @author htoall
 * @Description:
 * @date 2018/4/21 下午9:21
 * @copyright TCL-MIE
 */
public class QueryQQMusicRecord {
    @Url("alpha2-web/getservicerecord/query")
    public static class Request {
        private String serviceType;

        public Request(String serviceType) {
            this.serviceType = serviceType;
        }
    }

    public static class Response {

        /**
         * success : true
         * msg : success
         * resultCode : 200
         * data : {"result":[{"id":2,"userId":804102,"userType":"WX","nickName":"志强","equipmentId":"060100KFK1801200024","productType":"ALPHAMINI","serviceType":"QQMUSIC","serviceBeginTime":1523980800000,"serviceEndTime":1534521600000,"createTime":1524036860000,"updateTime":1524036903000},{"id":12,"userId":804102,"userType":"WX","nickName":"志强","equipmentId":"060100KFK1801200024","productType":"ALPHAMINI","serviceType":"QQMUSIC","serviceBeginTime":1523980800000,"serviceEndTime":1534521600000,"createTime":1524036868000,"updateTime":1524036911000}]}
         */

        private boolean success;
        private String msg;
        private int resultCode;
        private DataBean data;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public int getResultCode() {
            return resultCode;
        }

        public void setResultCode(int resultCode) {
            this.resultCode = resultCode;
        }

        public DataBean getData() {
            return data;
        }

        public void setData(DataBean data) {
            this.data = data;
        }

        public static class DataBean {
            private List<ResultBean> result;

            public List<ResultBean> getResult() {
                return result;
            }

            public void setResult(List<ResultBean> result) {
                this.result = result;
            }

            public static class ResultBean {
                /**
                 * id : 2
                 * userId : 804102
                 * userType : WX
                 * nickName : 志强
                 * equipmentId : 060100KFK1801200024
                 * productType : ALPHAMINI
                 * serviceType : QQMUSIC
                 * serviceBeginTime : 1523980800000
                 * serviceEndTime : 1534521600000
                 * createTime : 1524036860000
                 * updateTime : 1524036903000
                 */

                private int id;
                private int userId;
                private String userType;
                private String nickName;
                private String equipmentId;
                private String productType;
                private String serviceType;
                private long serviceBeginTime;
                private long serviceEndTime;
                private long createTime;
                private long updateTime;

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

                public String getUserType() {
                    return userType;
                }

                public void setUserType(String userType) {
                    this.userType = userType;
                }

                public String getNickName() {
                    return nickName;
                }

                public void setNickName(String nickName) {
                    this.nickName = nickName;
                }

                public String getEquipmentId() {
                    return equipmentId;
                }

                public void setEquipmentId(String equipmentId) {
                    this.equipmentId = equipmentId;
                }

                public String getProductType() {
                    return productType;
                }

                public void setProductType(String productType) {
                    this.productType = productType;
                }

                public String getServiceType() {
                    return serviceType;
                }

                public void setServiceType(String serviceType) {
                    this.serviceType = serviceType;
                }

                public long getServiceBeginTime() {
                    return serviceBeginTime;
                }

                public void setServiceBeginTime(long serviceBeginTime) {
                    this.serviceBeginTime = serviceBeginTime;
                }

                public long getServiceEndTime() {
                    return serviceEndTime;
                }

                public void setServiceEndTime(long serviceEndTime) {
                    this.serviceEndTime = serviceEndTime;
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
                public String toString() {
                    return "ResultBean{" +
                            "id=" + id +
                            ", userId=" + userId +
                            ", userType='" + userType + '\'' +
                            ", nickName='" + nickName + '\'' +
                            ", equipmentId='" + equipmentId + '\'' +
                            ", productType='" + productType + '\'' +
                            ", serviceType='" + serviceType + '\'' +
                            ", serviceBeginTime=" + serviceBeginTime +
                            ", serviceEndTime=" + serviceEndTime +
                            ", createTime=" + createTime +
                            ", updateTime=" + updateTime +
                            '}';
                }
            }

            @Override
            public String toString() {
                return "DataBean{" +
                        "result=" + result +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "Response{" +
                    "success=" + success +
                    ", msg='" + msg + '\'' +
                    ", resultCode=" + resultCode +
                    ", data=" + data +
                    '}';
        }
    }
}
