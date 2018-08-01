package com.zfl.recipe.recipe_detail;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Outline;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lsxiao.apollo.core.Apollo;
import com.zfl.recipe.R;
import com.zfl.recipe.entity.RecipeDetail;
import com.zfl.recipe.entity.RecipeFavorBean;
import com.zfl.recipe.entity.recipe_detail.RecipeDetailBean;
import com.zfl.recipe.mvp.view.BaseActivity;
import com.zfl.recipe.recipe_detail.recipe_method.RecipeDetailMethodFragment;
import com.zfl.recipe.utils.ApiUtil;
import com.zfl.recipe.utils.ConstantUtil;
import com.zfl.recipe.utils.ScreenUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @Description 菜谱详细做法页面
 * @Author ZFL
 * @Date 2017/7/14.
 */

public class RecipeDetailActivity extends BaseActivity
{

    @Bind(R.id.ivRecipeDetailBg)
    ImageView mIvRecipeDetailBg;
    @Bind(R.id.tbRecipeDetail)
    Toolbar mTbRecipeDetail;
    @Bind(R.id.vpRecipeMethod)
    ViewPager mVpRecipeMethod;
    @Bind(R.id.ivRecipeCollect)
    ImageView mIvRecipeCollect;

    private String mMenuId;

    private ProgressDialog mLoadingDialog;

    private RecipeDetailBean mDetailBean;

    private List<Fragment> mRecipeMethodFrgs;

    private TextView mTvToolBarTitle;

    private String mToolBarTitle = "";

    private boolean mIsFavor = false;//判断该菜谱是否为收藏菜谱

    @Override
    protected int getLayoutResID()
    {
        return R.layout.activity_recipe_detail;
    }

    @Override
    protected void initData()
    {
        mMenuId = getIntent().getStringExtra(ConstantUtil.RECIPE_MENU_ID);
        mRecipeMethodFrgs = new ArrayList<>();
    }

    @Override
    protected void initView()
    {
        mTbRecipeDetail.setTitle(mToolBarTitle);
        //取得toolbar中titleTextView的引用
        for (int i = 0; i < mTbRecipeDetail.getChildCount(); i++)
        {
            View v = mTbRecipeDetail.getChildAt(i);
            if (v != null && v instanceof TextView)
            {
                TextView tv = (TextView) v;
                if (mToolBarTitle.equals(tv.getText().toString()))
                {
                    mTvToolBarTitle = tv;
                }
            }
        }

        setSupportActionBar(mTbRecipeDetail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTbRecipeDetail.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });

    }

    @Override
    protected void start()
    {
        //进行数据请求
        mPresenter.get(ApiUtil.URL_RECIPE_DETAIL, RecipeDetailBean.class, true, true);

        ViewOutlineProvider viewOutlineProvider = new ViewOutlineProvider()
        {
            @Override
            public void getOutline(View view, Outline outline)
            {
                outline.setOval(0, 0, ScreenUtil.dp2px(mContext, 6f), ScreenUtil.dp2px(mContext,
                        6f));
            }
        };
        mIvRecipeCollect.setOutlineProvider(viewOutlineProvider);
    }

    public void updateRecipeDetail(RecipeDetailBean bean)
    {
        mDetailBean = bean;
        //获取图片
        Glide.with(this).load(mDetailBean.result.recipe.img).placeholder(R.mipmap
                .main_bg_default5).into(mIvRecipeDetailBg);
        //先添加主要MainFragment
        RecipeDetailMainFragment mainFragment = new RecipeDetailMainFragment();
        mainFragment.setDetailBean(mDetailBean);
        mRecipeMethodFrgs.add(mainFragment);

        if (!TextUtils.isEmpty(mDetailBean.result.recipe.method))
        {
            //对methodBean进行转换
            mDetailBean.result.recipe.convertMethodBean();
            //如果有方法步骤，再根据方法步骤，添加方法fragment
            for (RecipeDetail.MethodBean.MethodDetailBean bean1 : mDetailBean.result.recipe
                    .methodBean.list)
            {
                RecipeDetailMethodFragment methodFragment = new RecipeDetailMethodFragment();
                methodFragment.setDetailBean(bean1);
                mRecipeMethodFrgs.add(methodFragment);
            }
        }

        //在fragment添加到ViewPager上
        mVpRecipeMethod.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager())
        {
            @Override
            public Fragment getItem(int position)
            {
                return mRecipeMethodFrgs.get(position);
            }

            @Override
            public int getCount()
            {
                return mRecipeMethodFrgs.size();
            }
        });
        mVpRecipeMethod.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {

            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });
        //全部加载
        mVpRecipeMethod.setOffscreenPageLimit(mRecipeMethodFrgs.size());

        //判断该Recipe是否为收藏Recipe
        mPresenter.get(ApiUtil.URL_IS_FAVOR_RECIPE, RecipeFavorBean.class, false, false);
    }

    public void recipeEventDeliver(RecipeFavorBean bean) {
        switch (bean.recipeEventType) {
            case 1://添加收藏成功
                addRecipeSuccess(bean);
                break;
            case 2://删除收藏成功
                deleteRecipeSuccess(bean);
                break;
            case 3://判断是否是收藏菜谱
                isFavorRecipe(bean);
                break;
        }
    }

    @Override
    public Map<Class, String> getInvokeCallBackMap()
    {
        Map<Class, String> map = new HashMap<>();
        map.put(RecipeDetailBean.class, "updateRecipeDetail");
        map.put(RecipeFavorBean.class, "recipeEventDeliver");
        return map;
    }

    @Override
    public Map<Class, Map<String, Object>> getParamsMap()
    {
        Map<Class, Map<String, Object>> map = new HashMap<>();
        Map<String, Object> detailMap = new HashMap<>();
        detailMap.put(ConstantUtil.KEY, ApiUtil.APPKEY);
        detailMap.put(ConstantUtil.ID, mMenuId);
        map.put(RecipeDetailBean.class, detailMap);
        Map<String, Object> recipeEventMap = new HashMap<>();
        recipeEventMap.put(ConstantUtil.MENU_ID, mMenuId);
        if (mDetailBean != null) {
            recipeEventMap.put(ConstantUtil.CTG_TITLES, mDetailBean.result.ctgTitles);
            recipeEventMap.put(ConstantUtil.THUMBNAIL, mDetailBean.result.thumbnail);
            recipeEventMap.put(ConstantUtil.NAME, mDetailBean.result.name);
        }
        map.put(RecipeFavorBean.class, recipeEventMap);
        return map;
    }

    @Override
    public Context getContext()
    {
        return mContext;
    }

    @Override
    public void showLoading()
    {
        mLoadingDialog = ProgressDialog.show(mContext, "请求菜谱详细内容", "请求中......", true, false);
    }

    @Override
    public void hideLoading()
    {
        if (mLoadingDialog != null && mLoadingDialog.isShowing())
        {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

    private void showFavorIcon(boolean isFavor)
    {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 1f, Animation
                .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(300);
        mIvRecipeCollect.clearAnimation();
        mIvRecipeCollect.startAnimation(scaleAnimation);
        mIvRecipeCollect.setVisibility(View.VISIBLE);
        if (isFavor)
        {
            mIvRecipeCollect.setImageResource(R.mipmap.menu_collected);
        } else
        {
            mIvRecipeCollect.setImageResource(R.mipmap.menu_uncollect);
        }
    }

    private void hideFavorIcon()
    {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 0f, 1f, 0f, Animation
                .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(300);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {

            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                //至少让动画完整播放
                if (mIsFavor)
                {
                    mPresenter.post(ApiUtil.URL_DELETE_FAVOR_RECIPE, RecipeFavorBean.class, false, true);
                } else
                {
                    mPresenter.post(ApiUtil.URL_ADD_FAVOR_RECIPE, RecipeFavorBean.class, false, true);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }
        });
        mIvRecipeCollect.clearAnimation();
        mIvRecipeCollect.startAnimation(scaleAnimation);
        mIvRecipeCollect.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.ivRecipeCollect)
    public void onViewClicked()
    {
        //点击后进行菜谱收藏或者菜谱删除
        if (mDetailBean == null)
            return;
        //同时进行收藏按钮动画
        hideFavorIcon();
    }

    @Override
    public void error(String msg, int code)
    {
        super.error(msg, code);
        if (!mIvRecipeCollect.isShown()) {
            showFavorIcon(mIsFavor);
        }
    }

    public void addRecipeSuccess(RecipeFavorBean bean)
    {
        if (!mDetailBean.result.menuId.equals(bean.menuId)) return;
        Toast.makeText(mContext, "菜谱: " + mDetailBean.result.name + " 收藏成功", Toast.LENGTH_SHORT)
                .show();
        //收藏成功
        mIsFavor = true;
        showFavorIcon(mIsFavor);
        //发送收藏菜谱列表更新的通知
        Apollo.emit(ConstantUtil.FAVOR_RECIPE_LIST_UPDATE);
    }


    public void deleteRecipeSuccess(RecipeFavorBean bean)
    {
        if (!mDetailBean.result.menuId.equals(bean.menuId)) return;
        Toast.makeText(mContext, "菜谱: " + mDetailBean.result.name + " 移出收藏列表成功", Toast
                .LENGTH_SHORT).show();
        //删除收藏成功
        mIsFavor = false;
        showFavorIcon(mIsFavor);
        //发送收藏菜谱列表更新的通知
        Apollo.emit(ConstantUtil.FAVOR_RECIPE_LIST_UPDATE);
    }

    public void isFavorRecipe(RecipeFavorBean bean)
    {
        if (!mDetailBean.result.menuId.equals(bean.menuId)) return;
        mIsFavor = bean.isFavor;
        if (!mIsFavor)
        {
            //不是收藏菜谱，收藏图标设置为白色
            mIvRecipeCollect.setImageResource(R.mipmap.menu_uncollect);
        } else
        {
            //是收藏菜谱，收藏图标设置为红色
            mIvRecipeCollect.setImageResource(R.mipmap.menu_collected);

        }
    }
}
