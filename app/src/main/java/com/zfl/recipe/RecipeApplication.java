package com.zfl.recipe;

import android.app.Application;

import com.lsxiao.apllo.Apollo;
import com.lsxiao.apollo.generate.SubscriberBinderImplement;
import com.zfl.recipe.request.RetrofitClient;

import rx.android.schedulers.AndroidSchedulers;

/**
 * @Description
 * @Author ZFL
 * @Date 2017/6/21.
 */

public class RecipeApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        RetrofitClient.init(this);
        Apollo.get().init(SubscriberBinderImplement.instance(), AndroidSchedulers.mainThread());
        //初始化GreenDao(现在从网络获取数据库)
//        GreenDaoUtil.init(this);
    }
}
