package com.zfl.recipe.recipe;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.lsxiao.apollo.core.annotations.Receive;
import com.zfl.recipe.R;
import com.zfl.recipe.entity.recipe.RecipeCategoryBean;
import com.zfl.recipe.mvp.view.BaseActivity;
import com.zfl.recipe.mvp.view.IBaseView;
import com.zfl.recipe.recipe.recipe_list.RecipeListFragment;
import com.zfl.recipe.recipe_about.RecipeAboutActivity;
import com.zfl.recipe.recipe_favor.RecipeFavorActivity;
import com.zfl.recipe.utils.ApiUtil;
import com.zfl.recipe.utils.ConstantUtil;
import com.zfl.recipe.utils.RandomUtil;
import com.zfl.recipe.widget.CollapsingToolbarLayoutListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

/**
 * @Description
 * @Author ZFL
 * @Date 2017/6/21.
 */

public class RecipeActivity extends BaseActivity implements IBaseView
{
    @Bind(R.id.ivRecipeMainBg)
    ImageView mIvRecipeMainBg;
    @Bind(R.id.tlRecipeCategory)
    TabLayout mTlRecipeCategory;
    @Bind(R.id.vpRecipeList)
    ViewPager mVpRecipeList;
    @Bind(R.id.nvRecipeCategory)
    NavigationView mNvRecipeCategory;
    @Bind(R.id.dlRecipe)
    DrawerLayout mDlRecipe;
    @Bind(R.id.tbRecipeMain)
    Toolbar mTbRecipeMain;
    @Bind(R.id.ctlRecipeMain)
    CollapsingToolbarLayout mCtlRecipeMain;
    @Bind(R.id.vTlBackground)
    View mVTlBackground;
    @Bind(R.id.ablRecipeMain)
    AppBarLayout mAblRecipeMain;
    @Bind(R.id.ivRecipeLoading)
    ImageView mIvRecipeLoading;

    private AnimationDrawable mLoadingDrawable;

    private RecipeCategoryBean mCategoryBean;

    //记录之前的选中项来清除Checked状态
    private MenuItem mPreMenuItem;

    //为了生成随机而不重复的菜单图标
    private SparseArray<Integer> mMenuIconResPositions = new SparseArray<>();

    //TabLayout的背景View是否出现
    private boolean mIsAppBarLayoutBgShow = true;

    //16个菜单图标合集
    private int[] MenuIconRes = new int[]{
            R.mipmap.nav_recipe_category_01,
            R.mipmap.nav_recipe_category_02,
            R.mipmap.nav_recipe_category_03,
            R.mipmap.nav_recipe_category_04,
            R.mipmap.nav_recipe_category_05,
            R.mipmap.nav_recipe_category_06,
            R.mipmap.nav_recipe_category_07,
            R.mipmap.nav_recipe_category_08,
            R.mipmap.nav_recipe_category_09,
            R.mipmap.nav_recipe_category_10,
            R.mipmap.nav_recipe_category_11,
            R.mipmap.nav_recipe_category_12,
            R.mipmap.nav_recipe_category_13,
            R.mipmap.nav_recipe_category_14,
            R.mipmap.nav_recipe_category_15,
            R.mipmap.nav_recipe_category_16};

    private List<Fragment> mRecipeListFrgs;
    //用于适配TabLayout的Tab和相关内容的List
    private List<RecipeCategoryBean.ChildsBean> mRecipeTabList;

    //用于设置动画效果
    private TextView mTvToolBarTitle;

    private String mToolBarTitle = "选择你喜欢的类别哦";

    //加载的进度对话框
    private ProgressDialog mLoadingDialog;

    @Override
    protected int getLayoutResID()
    {
        return R.layout.activity_recipe;
    }

    @Override
    protected void initData()
    {
        mRecipeListFrgs = new ArrayList<>();
        mRecipeTabList = new ArrayList<>();
        mTlRecipeCategory.setupWithViewPager(mVpRecipeList);

    }

    @Override
    protected void initView()
    {
        mTbRecipeMain.setTitle(mToolBarTitle);
        setSupportActionBar(mTbRecipeMain);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDlRecipe, mTbRecipeMain,
                0, 0)
        {
            @Override
            public void onDrawerClosed(View drawerView)
            {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView)
            {
                super.onDrawerOpened(drawerView);
            }
        };
        toggle.syncState();
        mDlRecipe.addDrawerListener(toggle);

        //获取ToolBar的TitleTextView，以便设置动画效果
        for (int i = 0; i < mTbRecipeMain.getChildCount(); i++)
        {
            View v = mTbRecipeMain.getChildAt(i);
            if (v != null && v instanceof TextView)
            {
                TextView tv = (TextView) v;
                if (mToolBarTitle.equals(tv.getText().toString()))
                {
                    mTvToolBarTitle = tv;
                }
            }
        }
        //一开始默认titleTextView是隐藏的
        mTvToolBarTitle.setVisibility(View.INVISIBLE);

        mCtlRecipeMain.setContentScrimColor(mContext.getResources().getColor(R.color.colorPrimary));
        mCtlRecipeMain.setStatusBarScrimColor(mContext.getResources().getColor(R.color
                .colorPrimary));
        mCtlRecipeMain.setTitleEnabled(false);
        //        mCtlRecipeMain.setTitle();
        //        //展开时字体设置为透明
        //        mCtlRecipeMain.setExpandedTitleColor(mContext.getResources().getColor(android.R
        // .color.transparent));
        //        mCtlRecipeMain.setCollapsedTitleTextColor(Color.WHITE);
        //控制TabLayout的背景出现
        mAblRecipeMain.addOnOffsetChangedListener(new CollapsingToolbarLayoutListener()
        {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state, int verticalOffset)
            {
                double totalScrollRange = appBarLayout.getTotalScrollRange();
                double scale = totalScrollRange / (Math.abs((double) verticalOffset));
                if (scale <= 3.2)
                {
                    showOrHideAppBarLayoutBg(false);
                }
                if (scale >= 3.3)
                {
                    showOrHideAppBarLayoutBg(true);
                }
            }
        });


        mNvRecipeCategory.setItemIconTintList(null);

        mNvRecipeCategory.setNavigationItemSelectedListener(new NavigationView
                .OnNavigationItemSelectedListener()

        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                if (null != mPreMenuItem)
                {
                    mPreMenuItem.setChecked(false);
                }
                item.setChecked(true);
                mPreMenuItem = item;
                mDlRecipe.closeDrawers();
                updateRecipeListFrg(item.getItemId());
                return false;
            }
        });


        mLoadingDrawable = (AnimationDrawable) mIvRecipeLoading.getDrawable();

    }

    @Override
    protected void start()
    {
        mPresenter.get(ApiUtil.URL_RECIPE_CATEGORY, RecipeCategoryBean.class, true, true);
    }

    private void showOrHideAppBarLayoutBg(final boolean show)
    {
        if (mIsAppBarLayoutBgShow == show)
            return;//如果状态相同，不执行动画
        AlphaAnimation tabAnimation;
        AlphaAnimation titleAnimation;
        if (show)
        {
            tabAnimation = new AlphaAnimation(0, 1);
            titleAnimation = new AlphaAnimation(1, 0);
        } else
        {
            tabAnimation = new AlphaAnimation(1, 0);
            titleAnimation = new AlphaAnimation(0, 1);
        }
        tabAnimation.setDuration(700);
        titleAnimation.setDuration(700);
        tabAnimation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {

            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                if (show)
                {
                    mVTlBackground.setVisibility(View.VISIBLE);
                    mTvToolBarTitle.setVisibility(View.INVISIBLE);
                } else
                {
                    mVTlBackground.setVisibility(View.INVISIBLE);
                    mTvToolBarTitle.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }
        });
        mVTlBackground.clearAnimation();
        mTvToolBarTitle.clearAnimation();
        mVTlBackground.startAnimation(tabAnimation);
        mTvToolBarTitle.startAnimation(titleAnimation);
        mIsAppBarLayoutBgShow = show;
    }

    @Receive(ConstantUtil.UPDATE_MAIN_BACKGROUND)
    public void receiveUpdateMainBg(final String bgUrl)
    {
        mIvRecipeLoading.setVisibility(View.VISIBLE);
        mLoadingDrawable.start();
        mIvRecipeMainBg.setImageDrawable(null);
        mIvRecipeMainBg.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if (!TextUtils.isEmpty(bgUrl))
                {
                    Glide.with(mContext)
                            .load(bgUrl)
                            .error(R.mipmap.main_bg_default5)
                            .into(new GlideDrawableImageViewTarget(mIvRecipeMainBg)
                            {
                                @Override
                                public void onResourceReady(GlideDrawable resource, GlideAnimation<?
                                        super GlideDrawable> animation)
                                {   //加载成功监听
                                    mLoadingDrawable.stop();
                                    mIvRecipeLoading.setVisibility(View.INVISIBLE);
                                    super.onResourceReady(resource, animation);
                                }

                                @Override
                                public void onLoadFailed(Exception e, Drawable errorDrawable)
                                {
                                    //加载失败监听
                                    mLoadingDrawable.stop();
                                    mIvRecipeLoading.setVisibility(View.INVISIBLE);
                                    super.onLoadFailed(e, errorDrawable);
                                }
                            });
                }
            }
        }, 1000);
    }

    public void updateCategory(RecipeCategoryBean bean)
    {
        mMenuIconResPositions.clear();
        mCategoryBean = bean;
        List<Integer> menuIconRes = RandomUtil.random(MenuIconRes, mCategoryBean.result.childs
                .size());
        for (int i = 0; i < mCategoryBean.result.childs.size(); i++)
        {
            mNvRecipeCategory.getMenu().add(0, i, 0, mCategoryBean.result.childs.get(i)
                    .categoryInfo.name).setIcon(menuIconRes.get(i));
        }
        //TODO 在这里开始更新fragment
        //默认第一个选中
        mPreMenuItem = mNvRecipeCategory.getMenu().getItem(0).setChecked(true);
        updateRecipeListFrg(0);
    }

    //更新顶部图片以及tab和fragment
    private void updateRecipeListFrg(int index)
    {
        RecipeCategoryBean.ChildsBean bean = mCategoryBean.result.childs.get(index);
        mRecipeTabList.clear();
        mRecipeListFrgs.clear();
        mRecipeTabList.addAll(bean.childs);
        for (RecipeCategoryBean.ChildsBean bean1 : mRecipeTabList)
        {
            RecipeListFragment frg = new RecipeListFragment();
            frg.setRecipeId(bean1.categoryInfo.ctgId);
            frg.setRecipeLabel(bean1.categoryInfo.name);//设置主标签
            mRecipeListFrgs.add(frg);
        }
        mVpRecipeList.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager())
        {
            @Override
            public Fragment getItem(int position)
            {
                return mRecipeListFrgs.get(position);
            }

            @Override
            public int getCount()
            {
                return mRecipeListFrgs.size();
            }

            @Override
            public CharSequence getPageTitle(int position)
            {
                return mRecipeTabList.get(position).categoryInfo.name;
            }

            @Override
            public long getItemId(int position)
            {
                return Long.parseLong(mRecipeTabList.get(position).categoryInfo.ctgId);
            }
        });
        //至少4页
        mVpRecipeList.setOffscreenPageLimit(4);
        mTlRecipeCategory.getTabAt(0).select();
    }


    @Override
    public Map<Class, String> getInvokeCallBackMap()
    {
        Map<Class, String> map = new HashMap<>();
        map.put(RecipeCategoryBean.class, "updateCategory");
        return map;
    }

    @Override
    public Map<Class, Map<String, Object>> getParamsMap()
    {
        Map<Class, Map<String, Object>> map = new HashMap<>();
        Map<String, Object> categoryParamMap = new HashMap<>();
        categoryParamMap.put(ConstantUtil.KEY, ApiUtil.APPKEY);
        map.put(RecipeCategoryBean.class, categoryParamMap);
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
        mLoadingDialog = ProgressDialog.show(mContext, "请求菜谱类别", "请求中......", true, false);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_recipe_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent intent = null;
        switch (item.getItemId())
        {
            case R.id.menuMyCollect:
                //前往收藏菜谱界面
                intent = new Intent(mContext, RecipeFavorActivity.class);
                mContext.startActivity(intent);
                break;
            case R.id.menuAbout:
                //前往“关于Recipe”的Activity
                intent = new Intent(mContext, RecipeAboutActivity.class);
                mContext.startActivity(intent);
                break;
        }
        return true;
    }
}
