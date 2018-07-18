package com.ubtechinc.alpha.mini.net;

import android.support.annotation.Keep;

import com.ubtechinc.nets.http.Url;


@Keep
public class DeleteProductModule {


    @Url("alpha2-web/opus/remove")
    @Keep
    public class Request {
//                <<OPTION>> 	 "opusIds":"2,12,22"
        private String opusIds;


        public String getOpusIds() {
            return opusIds;
        }

        public void setOpusIds(String opusIds) {
            this.opusIds = opusIds;
        }
    }

    @Keep
    public class Response extends BaseResponse{



    }



}
