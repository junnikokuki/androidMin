package com.ubtechinc.alpha.mini.net;

import android.support.annotation.Keep;

import com.ubtechinc.nets.http.Url;


@Keep
public class AddProductModule {


    @Url("alpha2-web/opus/add")
    @Keep
    public class Request {
//	<<required>> "opusName":"code1",
//                <<required>> "opusContent":"<xml><code>12312312321211231231232121123123123212112312312321211231231232121</code></xml>",
//                <<OPTION>>   "opusTag":"new,good",
//                <<required>> "sdkVersion":"V0.123.123",
//                <<OPTION>> 	 "opusSource":"codemao"
        private String opusName;
        private String opusContent;
        private String opusTag;
        private int sdkVersion;
        private String opusSource;


        public String getOpusName() {
            return opusName;
        }

        public void setOpusName(String opusName) {
            this.opusName = opusName;
        }

        public String getOpusContent() {
            return opusContent;
        }

        public void setOpusContent(String opusContent) {
            this.opusContent = opusContent;
        }

        public String getOpusTag() {
            return opusTag;
        }

        public void setOpusTag(String opusTag) {
            this.opusTag = opusTag;
        }

        public int getSdkVersion() {
            return sdkVersion;
        }

        public void setSdkVersion(int sdkVersion) {
            this.sdkVersion = sdkVersion;
        }

        public String getOpusSource() {
            return opusSource;
        }

        public void setOpusSource(String opusSource) {
            this.opusSource = opusSource;
        }
    }

    @Keep
    public class Response extends BaseResponse{


    }



}
