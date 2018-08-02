package com.zfl.recipe.recipe_favor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.View;

import com.lsxiao.apollo.core.annotations.Receive;
import com.zfl.recipe.R;
import com.zfl.recipe.entity.RecipeInfo;
import com.zfl.recipe.entity.recipe_list.RecipeListBean;
import com.zfl.recipe.mvp.view.BaseActivity;
import com.zfl.recipe.recipe.recipe_list.holder.RecipeListItemHolder;
import com.zfl.recipe.recipe_detail.RecipeDetailActivity;
import com.zfl.recipe.utils.ApiUtil;
import com.zfl.recipe.utils.ConstantUtil;
import com.zfl.recipe.widget.adpter.BaseViewHolder;
import com.zfl.recipe.widget.adpter.CommonRecyclerViewAdapter;
import com.zfl.recipe.widget.bga_refresh.RecipeStyleRefreshViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import io.reactivex.Observable;

/**
 * @Description
 * @Author ZFL
 * @Date 2017/7/22.
 */

public class RecipeFavorActivity extends BaseActivity implements BGARefreshLayout.BGARefreshLayoutDelegate,
        CommonRecyclerViewAdapter.AdapterTemplate, CommonRecyclerViewAdapter.AdapterEmitter
{

    @Bind(R.id.tbRecipeFavor)
    Toolbar mTbRecipeFavor;
    @Bind(R.id.rvFavorRecipeList)
    RecyclerView mRvFavorRecipeList;
    @Bind(R.id.bgaRlFavorRecipe)
    BGARefreshLayout mBgaRlFavorRecipe;

    //适配器
    private CommonRecyclerViewAdapter mAdapter;

    private List<RecipeInfo> mList;

    private ProgressDialog mLoadingDialog;

    @Override
    protected int getLayoutResID()
    {
        return R.layout.activity_recipe_favor;
    }

    @Override
    protected void initData()
    {
        mList = new ArrayList<>();
        mAdapter = new CommonRecyclerViewAdapter(mContext, mList, this, this);
        mAdapter.setClick(new CommonRecyclerViewAdapter.OnItemClick()
        {
            @Override
            public void onItemClick(int position)
            {
                //前往菜谱详细页面
                Intent intent = new Intent(mContext, RecipeDetailActivity.class);
                intent.putExtra(ConstantUtil.RECIPE_MENU_ID, mList.get(position).menuId);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(int position)
            {

            }
        });
    }

    @Override
    protected void initView()
    {
        mTbRecipeFavor.setTitle("收藏菜谱");
        setSupportActionBar(mTbRecipeFavor);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTbRecipeFavor.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mBgaRlFavorRecipe.setDelegate(this);
        //不能上拉加载更多
        mBgaRlFavorRecipe.setRefreshViewHolder(new RecipeStyleRefreshViewHolder(mContext, false));
        mRvFavorRecipeList.setLayoutManager(new LinearLayoutManager(mContext));
        mRvFavorRecipeList.setAdapter(mAdapter);

    }

    @Override
    protected void start()
    {
        mBgaRlFavorRecipe.beginRefreshing();
    }

    public void updateFavorList(RecipeListBean bean)
    {
        mList.clear();
        mList.addAll(bean.result.list);
        mAdapter.notifyDataSetChanged();
        mBgaRlFavorRecipe.endRefreshing();
    }

    @Override
    public Map<Class, String> getInvokeCallBackMap()
    {
        Map<Class, String> map = new HashMap<>();
        map.put(RecipeListBean.class, "updateFavorList");
        return map;
    }

    @Override
    public Map<Class, Map<String, Object>> getParamsMap()
    {
        Map<Class, Map<String, Object>> map = new HashMap<>();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("testCNStr", "中文字符串测试");
        map.put(RecipeListBean.class, paramMap);
        return map;
    }

    @Override
    public Context getContext()
    {
        return mContext;
    }

    @Override
    public void error(String msg, int code)
    {
        super.error(msg, code);
        mBgaRlFavorRecipe.endRefreshing();
    }

    @Override
    public void showLoading()
    {
        mLoadingDialog = ProgressDialog.show(mContext, "请求收藏菜谱数据", "请求中......", true, false);
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
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout)
    {
        if (!mStartAlready) {
            //从网络获取收藏菜谱
            mBgaRlFavorRecipe.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    mPresenter.get(ApiUtil.URL_GET_FAVOR_RECIPE_LIST, RecipeListBean.class, false, false);
                }
            }, 1500);//延迟1.5秒（至少让用户能看见完整的动画啊）
        } else {
            mPresenter.get(ApiUtil.URL_GET_FAVOR_RECIPE_LIST, RecipeListBean.class, false, false);
        }
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout)
    {
        return false;
    }

    @Override
    public Map<Class<?>, Integer> getItemViewType()
    {
        Map<Class<?>, Integer> map = new HashMap<>();
        map.put(RecipeInfo.class, R.layout.item_recipe_list);
        return map;
    }

    @Override
    public SparseArray<Class<? extends BaseViewHolder>> getViewHolder()
    {
        SparseArray<Class<? extends BaseViewHolder>> sparseArray = new SparseArray<>();
        sparseArray.put(R.layout.item_recipe_list, RecipeListItemHolder.class);
        return sparseArray;
    }


    @Receive(ConstantUtil.FAVOR_RECIPE_LIST_UPDATE)
    public void updateReceived() {
        //来接收收藏菜谱更新的通知
        mPresenter.get(ApiUtil.URL_GET_FAVOR_RECIPE_LIST, RecipeListBean.class, false, false);
    }

    @Override
    public void emitter(Observable<Object> observable)
    {
        //do nothing
    }
}
