package com.zfl.recipe.mvp.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.lsxiao.apollo.core.Apollo;
import com.lsxiao.apollo.core.contract.ApolloBinder;
import com.zfl.recipe.mvp.presenter.BasePresenter;
import com.zfl.recipe.utils.LogUtil;

import butterknife.ButterKnife;

/**
 * @Description
 * @Author ZFL
 * @Date 2017/6/12.
 */

public abstract class BaseActivity extends AppCompatActivity implements IBaseView
{

    protected BasePresenter mPresenter;

    protected Context mContext;
    //start函数通常只调用一次，此布尔值是为了保证解锁，切换应用时start函数不被调用多次
    protected boolean mStartAlready = false;
    //Apollo通信的binder
    private ApolloBinder mBinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        bindContentView(getLayoutResID());
        mContext = this;
        mPresenter = new BasePresenter(this);
        mBinder = Apollo.bind(this);
        initData();
        initView();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (!mStartAlready)
        {
            start();
            mStartAlready = true;
        }
    }

    protected void bindContentView(int resId)
    {
        setContentView(resId);
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        hideLoading();
        mPresenter.unRegRx();
        ButterKnife.unbind(this);
        mBinder.unbind();
    }

    @Override
    public void error(String msg, int code)
    {
        Toast.makeText(mContext, "msg:" + msg + "\n code:" + code, Toast.LENGTH_SHORT).show();
        LogUtil.e("msg:" + msg + "\n code:" + code);
    }

    /**
     * 获取该Activity的界面视图id
     *
     * @return
     */
    protected abstract int getLayoutResID();

    /**
     * 初始化data，由具体Activity实现
     */
    protected abstract void initData();

    /**
     * 初始化view，由具体Activity实现
     */
    protected abstract void initView();

    /**
     * 在onResume函数中调用，由具体Activity实现<br>
     * 由具体页面决定，view搭建完成后，要去请求数据或者其他工作
     */
    protected abstract void start();

}
