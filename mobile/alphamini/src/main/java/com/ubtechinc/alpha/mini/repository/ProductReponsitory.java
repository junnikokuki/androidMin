package com.ubtechinc.alpha.mini.repository;

import android.support.annotation.NonNull;

import com.ubtechinc.alpha.mini.component.http.CookieInterceptor;
import com.ubtechinc.alpha.mini.net.AddProductModule;
import com.ubtechinc.alpha.mini.net.DeleteProductModule;
import com.ubtechinc.alpha.mini.net.GetProductContentModule;
import com.ubtechinc.alpha.mini.net.GetProductListModule;
import com.ubtechinc.alpha.mini.net.UploadProductModule;
import com.ubtechinc.alpha.mini.repository.datasource.IProductCallback;
import com.ubtechinc.alpha.mini.utils.ErrorParser;
import com.ubtechinc.codemaosdk.Statics;
import com.ubtechinc.nets.ResponseListener;
import com.ubtechinc.nets.http.HttpProxy;
import com.ubtechinc.nets.http.ThrowableWrapper;

import java.util.HashMap;
import java.util.List;

/**
 * @Deseription
 * @Author tanghongyu
 * @Time 2018/4/25 21:29
 */

public class ProductReponsitory {

    private static ProductReponsitory INSTANCE = null;
    public static final int TYPE_GRAPH = 3;
    public static final String PRODUCT_CODE = "10201";
    private HashMap<String, String> mHeaders;
    private ProductReponsitory() {
        mHeaders = new HashMap<>();
    }

    public static ProductReponsitory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ProductReponsitory();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    /**
     * 获取作品列表
     * @param callback
     */
    public void loadProductList(final @NonNull IProductCallback.GetProductList callback) {
        final GetProductListModule.Request request = new GetProductListModule().new Request();
        HttpProxy.get().doGet(request ,new ResponseListener<GetProductListModule.Response>() {

            @Override
            public void onError(ThrowableWrapper e) {

                callback.onDataNotAvailable(e);
            }

            @Override
            public void onSuccess(GetProductListModule.Response response) {

                if(ErrorParser.get().isSuccess(response.getResultCode())) {
                    callback.onLoadProductList(response.getData().getResult());

                }else {
                    callback.onDataNotAvailable(ErrorParser.get().getThrowableWrapper());
                }

            }
        });
    }

    public void getProductContent(String id,final @NonNull IProductCallback.GetProductContent callback) {
        final GetProductContentModule.Request request = new GetProductContentModule().new Request();
        request.setOpusId(id);
        HttpProxy.get().doGet(request ,new ResponseListener<GetProductContentModule.Response>() {

            @Override
            public void onError(ThrowableWrapper e) {

                callback.onDataNotAvailable(e);
            }

            @Override
            public void onSuccess(GetProductContentModule.Response response) {

                if(ErrorParser.get().isSuccess(response.getResultCode())) {
                    callback.onLoadProductContent(response.getData().getProduct());

                }else {
                    callback.onDataNotAvailable(ErrorParser.get().getThrowableWrapper());
                }

            }
        });
    }

    public void uploadProduct(String coverUrl, String description, List<UploadProductModule.Files> files,
                              String name, String title, int type, String userId, final IProductCallback.UploadProductCallback uploadProductCallback) {
        final UploadProductModule.Request request = new UploadProductModule().new Request();
        request.setCoverUrl(coverUrl);
        request.setDescription(description);
        request.setFiles(files);
        request.setName(name);
        request.setTitle(title);
        request.setType(type);
        request.setUserId(userId);
        mHeaders.put("product", PRODUCT_CODE);
        mHeaders.put(CookieInterceptor.AUTHORIZATION, CookieInterceptor.get().getToken());
        HttpProxy.get().doPost(request, mHeaders, new ResponseListener<UploadProductModule.Response>() {

            @Override
            public void onSuccess(UploadProductModule.Response response) {
                if (response.getStatus()) {
                    uploadProductCallback.onUploadSuccess();
                } else {
                    uploadProductCallback.onDataNotAvailable(ErrorParser.get().getThrowableWrapper());
                }
            }

            @Override
            public void onError(ThrowableWrapper e) {
                uploadProductCallback.onDataNotAvailable(e);
            }
        });

    }

    public void addProduct(String content, String name, final @NonNull IProductCallback.ControlProductCallback callback) {

        final AddProductModule.Request request = new AddProductModule().new Request();
        request.setOpusContent(content);
        request.setOpusName(name);
        request.setSdkVersion(Statics.SDK_VERSION);
        HttpProxy.get().doPost(request ,new ResponseListener<AddProductModule.Response>() {

            @Override
            public void onError(ThrowableWrapper e) {

                callback.onFail(e);
            }

            @Override
            public void onSuccess(AddProductModule.Response response) {

                if(ErrorParser.get().isSuccess(response.getResultCode())) {
                    callback.onSuccess();

                }else {
                    callback.onFail(ErrorParser.get().getThrowableWrapper());
                }

            }
        });
    }

    public void delProduct(String opusIds, final @NonNull IProductCallback.ControlProductCallback callback) {

        final DeleteProductModule.Request request = new DeleteProductModule().new Request();
        request.setOpusIds(opusIds);
        HttpProxy.get().doPost(request ,new ResponseListener<DeleteProductModule.Response>() {

            @Override
            public void onError(ThrowableWrapper e) {
                callback.onFail(e);
            }

            @Override
            public void onSuccess(DeleteProductModule.Response response) {

                if(ErrorParser.get().isSuccess(response.getResultCode())) {
                    callback.onSuccess();

                }else {
                    callback.onFail(ErrorParser.get().getThrowableWrapper());
                }

            }
        });
    }


}
