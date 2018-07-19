package com.zfl.recipe;

import android.app.Application;

import com.lsxiao.apollo.core.Apollo;
import com.zfl.recipe.request.RetrofitClient;

import io.reactivex.android.schedulers.AndroidSchedulers;


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
//        Apollo.get().init(SubscriberBinderImplement.instance(), AndroidSchedulers.mainThread());
        Apollo.init(AndroidSchedulers.mainThread(), this);
        //初始化GreenDao(现在从网络获取数据库)
//        GreenDaoUtil.init(this);
    }
}
