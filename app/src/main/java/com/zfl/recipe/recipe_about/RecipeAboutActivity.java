package com.zfl.recipe.recipe_about;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.zfl.recipe.R;
import com.zfl.recipe.mvp.view.BaseActivity;

import java.util.Map;

import butterknife.Bind;

/**
 * @Description "关于菜谱"
 * @Author ZFL
 * @Date 2017/7/17.
 */

public class RecipeAboutActivity extends BaseActivity
{

    @Bind(R.id.tbRecipeAbout)
    Toolbar mTbRecipeAbout;
    @Bind(R.id.ctlRecipeAbout)
    CollapsingToolbarLayout mCtlRecipeAbout;

    @Override
    protected int getLayoutResID()
    {
        return R.layout.activity_recipe_about;
    }

    @Override
    protected void initData()
    {

    }

    @Override
    protected void initView()
    {
        setSupportActionBar(mTbRecipeAbout);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTbRecipeAbout.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });
        mCtlRecipeAbout.setTitle("关于Recipe");
        mCtlRecipeAbout.setContentScrimColor(mContext.getResources().getColor(R.color.colorPrimary));
        mCtlRecipeAbout.setStatusBarScrimColor(mContext.getResources().getColor(R.color
                .colorPrimary));

        //展开时字体设置为透明
        mCtlRecipeAbout.setExpandedTitleColor(mContext.getResources().getColor(android.R.color.transparent));
        mCtlRecipeAbout.setCollapsedTitleTextColor(Color.WHITE);

    }

    @Override
    protected void start()
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
    public Context getContext()
    {
        return null;
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
