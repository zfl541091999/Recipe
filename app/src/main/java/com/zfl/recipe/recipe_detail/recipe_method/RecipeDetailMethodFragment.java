package com.zfl.recipe.recipe_detail.recipe_method;

import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zfl.recipe.R;
import com.zfl.recipe.entity.RecipeDetail;
import com.zfl.recipe.mvp.view.BaseFragment;

import java.util.Map;

import butterknife.Bind;

/**
 * @Description 菜谱制作步骤
 * @Author ZFL
 * @Date 2017/7/14.
 */

public class RecipeDetailMethodFragment extends BaseFragment
{

    @Bind(R.id.ivRecipeMethod)
    ImageView mIvRecipeMethod;
    @Bind(R.id.tvRecipeMethod)
    TextView mTvRecipeMethod;

    private RecipeDetail.MethodBean.MethodDetailBean mDetailBean;//详细步骤

    public void setDetailBean(RecipeDetail.MethodBean.MethodDetailBean bean) {
        mDetailBean = bean;
    }

    @Override
    protected void lazyLoad()
    {
        Glide.with(getActivity()).load(mDetailBean.img).into(mIvRecipeMethod);
        mTvRecipeMethod.setText(mDetailBean.step);
    }

    @Override
    protected int getLayoutResID()
    {
        return R.layout.fragment_recipe_method;
    }

    @Override
    protected void initData()
    {

    }

    @Override
    protected void initView()
    {

    }

    @Override
    public Map<Class, String> getInvokeCallBackMap()
    {
        return null;
    }

    @Override
    public Map<Class, Map<String, Object>> getParamsMap()
    {
        return null;
    }

    @Override
    public void error(String msg, int code)
    {

    }

    @Override
    public void showLoading()
    {

    }

    @Override
    public void hideLoading()
    {

    }
}
