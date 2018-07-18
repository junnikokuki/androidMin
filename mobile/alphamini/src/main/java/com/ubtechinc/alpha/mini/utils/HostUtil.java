package com.ubtechinc.alpha.mini.utils;

import com.ubtechinc.nets.http.HeaderInterceptor;

/**
 * Created by ubt on 2018/4/16.
 */

public class HostUtil {

    public static void toInnerNet(){

        HeaderInterceptor.URL_HOST = "http://10.10.1.43:8080/";
        HeaderInterceptor.IM_URL_HOST = "http://10.10.1.43:9080/";
        HeaderInterceptor.USER_URL_HOST = "http://10.10.20.71:8010/";
        HeaderInterceptor.XINGE_URL_HOST = "https://test79.ubtrobot.com/";

    }

    public static void toOutterNet(){

        HeaderInterceptor.URL_HOST = "http://210.75.21.109:8080/";
        HeaderInterceptor.IM_URL_HOST = "http://202.170.139.171:9080/";
        HeaderInterceptor.USER_URL_HOST = "http://210.75.21.107:8010/";
        HeaderInterceptor.XINGE_URL_HOST = "http://210.75.21.108:8010/";

    }

}
