package com.zfl.recipe.mvp.model;

import com.zfl.recipe.request.RetrofitClient;

import java.util.Map;

import okhttp3.Cache;
import rx.Subscriber;
import rx.Subscription;

/**
 * @Description
 * @Author ZFL
 * @Date 2017/6/12.
 */

public class BaseModel
{
    /**
     * 通用的请求get方法(不带中文参数)
     * @param url
     * @param map
     * @param cache
     * @param subscriber
     * @return
     */
    public Subscription get(String url, Map<String, Object> map, Cache cache, Subscriber subscriber) {
        return RetrofitClient.getInstance().get(url, map, cache).subscribe(subscriber);
    }

    /**
     * 通用的请求post方法(不带中文参数)
     * @param url
     * @param map
     * @param subscriber
     * @return
     */
    public Subscription post(String url, Map<String, Object> map, Subscriber subscriber) {
        return RetrofitClient.getInstance().post(url, map).subscribe(subscriber);
    }

    /**
     * 通用的请求post方法(带中文参数)
     * @param url
     * @param map
     * @param subscriber
     * @return
     */
    public Subscription postWithChParam(String url, Map<String, Object> map, Subscriber subscriber) {
        return RetrofitClient.getInstance().postWithChParam(url, map).subscribe(subscriber);
    }
}
