package com.zfl.recipe.mvp.presenter;

import com.zfl.recipe.mvp.model.BaseModel;
import com.zfl.recipe.mvp.view.IBaseView;
import com.zfl.recipe.request.Response;
import com.zfl.recipe.utils.OKHttpCacheUtil;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import okhttp3.Cache;

/**
 * @Description
 * @Author ZFL
 * @Date 2017/7/20.
 */

public class BasePresenter
{
    protected IBaseView view;

    //为了能够统一管理正在进行的后台任务
    private CompositeDisposable dis = new CompositeDisposable();

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
        addDis(model.get(url, view.getParamsMap().get(clazz), cache, new Response(clazz, view)));
    }

    /**
     * 较通用的post方法
     * @param url
     * @param clazz
     */
    public void post(String url, Class clazz, boolean isShowLoading, boolean haveChParam) {
        if (isShowLoading) view.showLoading();
        Disposable disposable;
        if (haveChParam) {
            disposable = model.postWithChParam(url, view.getParamsMap().get(clazz), new Response(clazz, view));
        } else {
            disposable = model.post(url, view.getParamsMap().get(clazz), new Response(clazz, view));
        }
        addDis(disposable);
    }


    public void addDis(Disposable disposable) {
        dis.add(disposable);
    }

    /**
     * 取消后台任务，防止内存溢出
     */
    public void unRegRx() {
        dis.clear();
    }
}
