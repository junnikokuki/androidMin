package com.ubtechinc.alpha.mini.repository.datasource;

import com.ubtechinc.alpha.mini.entity.Product;
import com.ubtechinc.nets.http.ThrowableWrapper;

import java.util.List;

/**
 * @Deseription
 * @Author tanghongyu
 * @Time 2018/4/25 21:31
 */

public interface IProductCallback {

    interface GetProductList {
        void onLoadProductList(List<Product> productList);

        void onDataNotAvailable(ThrowableWrapper e);
    }
    interface GetProductContent {
        void onLoadProductContent(Product product);

        void onDataNotAvailable(ThrowableWrapper e);
    }

    interface UploadProductCallback {
        void onUploadSuccess();

        void onDataNotAvailable(ThrowableWrapper e);
    }
    interface ControlProductCallback {

        void onSuccess();

        void onFail(ThrowableWrapper e);
    }

}
