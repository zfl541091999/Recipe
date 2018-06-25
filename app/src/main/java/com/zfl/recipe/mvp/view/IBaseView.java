package com.zfl.recipe.mvp.view;

import android.content.Context;

import java.util.Map;

/**
 * @Description
 * @Author ZFL
 * @Date 2017/6/12.
 */

public interface IBaseView
{

    /**
     * 网络请求成功后，需要invoke回调函数的map.现在以bean的class对应回调的函数名，以后可以用请求的url对应回调的函数名
     * @return
     */
    Map<Class, String> getInvokeCallBackMap();

    /**
     * 网络请求时，需要存储着请求参数map的map。现在以bean的class对应请求参数的map，以后可以用请求的url对应请求参数的mao
     * @return
     */
    Map<Class, Map<String, Object>> getParamsMap();

    /**
     * 获取上下文
     * @return
     */
    Context getContext();

    /**
     * 请求错误时被调用的方法
     * @param msg
     * @param code
     */
    void error(String msg, int code);

    /**
     * 加载中
     */
    void showLoading();

    /**
     * 加载完成（无论成功或者失败）
     */
    void hideLoading();
}
