package com.zfl.recipe.request;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.zfl.recipe.mvp.view.IBaseView;
import com.zfl.recipe.utils.JsonUtil;
import com.zfl.recipe.utils.LogUtil;
import com.zfl.recipe.utils.MethodUtil;

import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

/**
 * @Description
 * @Author ZFL
 * @Date 2017/6/14.
 */

public class ResponseSubscriber extends Subscriber<ResponseBody>
{
    //用于转化的实体类
    private Class clazz;

    private Gson gson;

    private IBaseView view;

    private String wrongMsg;

    public ResponseSubscriber(@NonNull Class clazz,@NonNull IBaseView view) {
        this.clazz = clazz;
        this.view = view;
        gson = new Gson();
    }

    @Override
    public void onCompleted()
    {
        //now we do nothing
    }

    @Override
    public void onError(Throwable e)
    {
        if (e instanceof HttpException) {
            HttpException exception = (HttpException) e;
            int code = exception.code();
            wrongMsg = exception.getMessage();
            //后续会添加错误码的具体判断
            view.error(wrongMsg, code);
        } else {
            view.error(e.getMessage(), -1);
        }
        view.hideLoading();
    }

    @Override
    public void onNext(ResponseBody responseBody)
    {
        try
        {
            String json = responseBody.string();
            LogUtil.v(json);
            if (JsonUtil.checkResult(json)) {
                //请求成功
                //进行数据解析，并且将具体数据通过反射机制返回给view
                MethodUtil.getMethod(view, view.getInvokeCallBackMap().get(clazz), clazz).invoke(view, gson.fromJson(json, clazz));
            } else {
                //请求失败
                view.error(JsonUtil.getMessage(json), JsonUtil.getCode(json));
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            view.error("反正又特么出错了:" + e.getMessage(), -1);
        } finally
        {
            view.hideLoading();
        }
    }
}
