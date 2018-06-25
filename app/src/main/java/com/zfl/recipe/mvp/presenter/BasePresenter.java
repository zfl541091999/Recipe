package com.zfl.recipe.mvp.presenter;

import com.zfl.recipe.mvp.model.BaseModel;
import com.zfl.recipe.mvp.view.IBaseView;
import com.zfl.recipe.request.ResponseSubscriber;
import com.zfl.recipe.utils.OKHttpCacheUtil;

import okhttp3.Cache;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * @Description
 * @Author ZFL
 * @Date 2017/7/20.
 */

public class BasePresenter
{
    protected IBaseView view;

    //为了能够统一管理正在进行的后台任务
    private CompositeSubscription sub = new CompositeSubscription();

    protected BaseModel model;

    public BasePresenter(IBaseView view)
    {
        this.view = view;
        model = new BaseModel();
    }

    /**
     * 较通用的get方法
     * @param url
     * @param clazz
     * @param needCache
     */
    public void get(String url, Class clazz, boolean needCache, boolean isShowLoading) {
        Cache cache;
        //现在缓存cache由presenter决定
        if (needCache) {
            cache = OKHttpCacheUtil.defaultCache(view.getContext());
        } else {
            cache = OKHttpCacheUtil.noCache();
        }
        if (isShowLoading) view.showLoading();
        addSub(model.get(url, view.getParamsMap().get(clazz), cache, new ResponseSubscriber(clazz, view)));
    }

    /**
     * 较通用的post方法
     * @param url
     * @param clazz
     */
    public void post(String url, Class clazz, boolean isShowLoading, boolean haveChParam) {
        if (isShowLoading) view.showLoading();
        Subscription subscription;
        if (haveChParam) {
            subscription = model.postWithChParam(url, view.getParamsMap().get(clazz), new ResponseSubscriber(clazz, view));
        } else {
            subscription = model.post(url, view.getParamsMap().get(clazz), new ResponseSubscriber(clazz, view));
        }
        addSub(subscription);
    }


    protected void addSub(Subscription subscription) {
        sub.add(subscription);
    }

    /**
     * 取消后台任务，防止内存溢出
     */
    public void unRegRx() {
        if (sub.hasSubscriptions()) {
            sub.unsubscribe();
        }
    }
}
