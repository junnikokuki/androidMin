package com.ubtechinc.alpha.mini.net;

import android.support.annotation.Keep;

import com.ubtechinc.alpha.mini.entity.Product;
import com.ubtechinc.nets.http.Url;


@Keep
public class GetProductContentModule {


    @Url("alpha2-web/opus/getcontent")
    @Keep
    public class Request {
        private String opusId;

        public String getOpusId() {
            return opusId;
        }

        public void setOpusId(String opusId) {
            this.opusId = opusId;
        }
    }

    @Keep
    public class Response extends BaseResponse{



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
        private Product result;

        public Product getProduct() {
            return result;
        }

        public void setProduct(Product product) {
            this.result = product;
        }
    }
}
