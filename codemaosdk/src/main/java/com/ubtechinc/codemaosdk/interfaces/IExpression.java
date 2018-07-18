package com.ubtechinc.codemaosdk.interfaces;

import com.ubtechinc.codemaosdk.bean.Expression;

import java.util.List;

/**
 * @Deseription
 * @Author tanghongyu
 * @Time 2018/4/3 9:19
 */

public interface IExpression {

    interface GetExpressionList {
        void onGetExpressionList(List<Expression> expressionList);
        void onError(int code);
    }

    public interface ControlCallback {
        void onSuccess();

        void onError(int code);
    }
}
