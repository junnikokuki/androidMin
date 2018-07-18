package com.ubtechinc.alpha.mini.net;

import android.support.annotation.Keep;

import com.ubtechinc.alpha.mini.entity.Product;
import com.ubtechinc.nets.http.Url;

import java.util.List;


@Keep
public class GetProductListModule {


    @Url("alpha2-web/opus/list")
    @Keep
    public class Request {

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
        private List<Product> result;

        public List<Product> getResult() {
            return result;
        }

        public void setResult(List<Product> result) {
            this.result = result;
        }
    }
}
