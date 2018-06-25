package com.zfl.recipe.mvp.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zfl.recipe.mvp.presenter.BasePresenter;

import butterknife.ButterKnife;

/**
 * @Description 一个基类Fragment，尝试实现懒加载
 * @Author ZFL
 * @Date 2017/6/12.
 */

public abstract class BaseFragment extends Fragment implements IBaseView
{

    private boolean isPrepared = false;//证明fragment界面元素是否已经加载完毕

    private boolean isFirstVisible = true;//判断是否是第一次可见，避免重复加载数据

    protected BasePresenter mPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState)
    {
        View baseView = inflater.inflate(getLayoutResID(), null);
        ButterKnife.bind(this, baseView);
        mPresenter = new BasePresenter(this);
        initView();
        return baseView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        isPrepared = true;
        initData();
        //处理第一个fragment的逻辑。必须在界面搭建完成后才去请求数据，不然会造成空指针错误
        if (getUserVisibleHint()) {
            //这里代表是第一个fragment
            lazyLoad();
            isFirstVisible = false;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isPrepared && isFirstVisible) {
            //当页面搭建完成，处于可见状态并且是第一次处于可见状态，开始加载数据
            lazyLoad();
            isFirstVisible = false;
        }
        //子类Fragment或者其他类想处理Fragment可见不可见逻辑，请实现此接口
        if (mVisibleHintListener != null) mVisibleHintListener.onUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        ButterKnife.unbind(this);
        mPresenter.unRegRx();
        isPrepared = false;
        isFirstVisible = true;
    }

    /**
     * 懒加载的函数，具体实现看具体页面而定
     */
    protected abstract void lazyLoad();

    /**
     * 获取该Fragment的界面视图id
     * @return
     */
    protected abstract int getLayoutResID();

    /**
     * 初始化data，由具体Fragment实现
     */
    protected abstract void initData();

    /**
     * 初始化view，由具体Fragment实现
     */
    protected abstract void initView();

    private OnUserVisibleHintListener mVisibleHintListener;

    public void setVisibleHintListener(OnUserVisibleHintListener listener) {
        mVisibleHintListener = listener;
    }
    /**
     * 基类Fragment可见或者不可见时触发的回调接口<br>
     * 子类Fragment如果需要处理可见不可见逻辑，实现此接口
     */
    public interface OnUserVisibleHintListener{
        void onUserVisibleHint(boolean isVisibleToUser);
    }

}
