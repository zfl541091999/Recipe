package com.zfl.recipe.recipe.recipe_list;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.widget.Toast;

import com.lsxiao.apllo.Apollo;
import com.zfl.recipe.R;
import com.zfl.recipe.entity.RecipeInfo;
import com.zfl.recipe.entity.recipe_list.RecipeListBean;
import com.zfl.recipe.mvp.view.BaseFragment;
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
import rx.Observable;

/**
 * @Description
 * @Author ZFL
 * @Date 2017/6/22.
 */

public class RecipeListFragment extends BaseFragment implements BGARefreshLayout.BGARefreshLayoutDelegate,
        CommonRecyclerViewAdapter.AdapterTemplate, CommonRecyclerViewAdapter.AdapterObservable, BaseFragment.OnUserVisibleHintListener
{

    @Bind(R.id.rvRecipeList)
    RecyclerView mRvRecipeList;
    @Bind(R.id.bgaRlRecipe)
    BGARefreshLayout mBgaRlRecipe;

    private String mRecipeId;//菜单分类ID

    private int mCurPage = 1;//当前页数

    private int mTotalPage = 1;//总页数，要进行计算

    private int mSize = 20;//每页的数据条数，默认20

    private CommonRecyclerViewAdapter mAdapter;

    private boolean mIsRefresh = false;//判断用户是下拉刷新还是上拉加载更多

    private List<RecipeInfo> mRecipeList;

    private boolean mIsLazyLoad = false;//判断是否是懒加载

    private boolean mIsUpdateMainBgSend = false;//避免重复发送多次更新主页背景图的请求

    private String mRecipeLabel;//主标签，显示在最前排

    /**
     * 设置菜单id以便进行请求
     * @param id
     */
    public void setRecipeId(String id) {
        mRecipeId = id;
        //这个id也用于识别Fragment，来让Fragment进行刷新
    }

    /**
     * 设置主标签以便进行显示
     * @param label
     */
    public void setRecipeLabel(String label) {
        mRecipeLabel = label;
    }

    @Override
    protected void lazyLoad()
    {
        mIsLazyLoad = true;
        mBgaRlRecipe.beginRefreshing();
    }

    @Override
    protected int getLayoutResID()
    {
        return R.layout.fragment_recipe_list;
    }

    @Override
    protected void initView()
    {
        mBgaRlRecipe.setDelegate(this);
        mBgaRlRecipe.setRefreshViewHolder(new RecipeStyleRefreshViewHolder(getActivity(), true));
        mRvRecipeList.setLayoutManager(new LinearLayoutManager(getActivity()));
        //添加监听Fragment可见不可见的listener
        setVisibleHintListener(this);
    }

    @Override
    protected void initData()
    {
        mRecipeList = new ArrayList<>();
        mAdapter = new CommonRecyclerViewAdapter(getActivity(), mRecipeList, this, this);
        mAdapter.setClick(new CommonRecyclerViewAdapter.OnItemClick()
        {
            @Override
            public void onItemClick(int position)
            {
                //前往菜谱详细页面
                Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
                intent.putExtra(ConstantUtil.RECIPE_MENU_ID, mRecipeList.get(position).menuId);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(int position)
            {

            }
        });
        mRvRecipeList.setAdapter(mAdapter);
    }


    public void updateRecipeList(RecipeListBean bean) {
        //计算总页数
        if (bean.result.total % mSize == 0) {
            mTotalPage = bean.result.total / mSize;
        } else {
            mTotalPage = bean.result.total / mSize + 1;
        }
        if (mIsRefresh) {
            mRecipeList.clear();
            mBgaRlRecipe.endRefreshing();
        } else {
            mBgaRlRecipe.endLoadingMore();
        }
        for (RecipeInfo info : bean.result.list) {
            //设置主标签
            info.mainLabel = mRecipeLabel;
            mRecipeList.add(info);
        }
        mAdapter.notifyDataSetChanged();
        //在请求成功后，不管是不是懒加载，都可以将mIsLazyLoad布尔值设置为false，因为懒加载只执行一次
        mIsLazyLoad = false;
        //请求成功后，如果此Fragment处于可见状态，发送主页更新背景图片的请求
        //发送请求因素：
        //1.Fragment处于可见状态
        //2.数据是下拉刷新获得的
        //3.之前还没有发送过更新
        String mainBgUrl = getMainBgUrl();
        if (getUserVisibleHint() && mIsRefresh && (!mIsUpdateMainBgSend)) {
            Apollo.get().send(ConstantUtil.UPDATE_MAIN_BACKGROUND, mainBgUrl);
            mIsUpdateMainBgSend = true;
        }
    }

    /**
     * 在刷新成功后，获取主页背景图的Url
     */
    private String getMainBgUrl() {
        String mainBgUrl = null;
        for (RecipeInfo info : mRecipeList) {
            if (!TextUtils.isEmpty(info.recipe.img)) {
                mainBgUrl = info.recipe.img.trim();
                break;
            }
        }

        return mainBgUrl;
    }

    @Override
    public Map<Class, String> getInvokeCallBackMap()
    {
        Map<Class, String> map = new HashMap<>();
        map.put(RecipeListBean.class, "updateRecipeList");
        return map;
    }

    @Override
    public Map<Class, Map<String, Object>> getParamsMap()
    {
        Map<Class, Map<String, Object>> paramsMap = new HashMap<>();
        Map<String, Object> recipeListMap = new HashMap<>();
        recipeListMap.put(ConstantUtil.KEY, ApiUtil.APPKEY);
        recipeListMap.put("cid", mRecipeId);
        recipeListMap.put("page", mCurPage);
        recipeListMap.put("size", mSize);
        paramsMap.put(RecipeListBean.class, recipeListMap);
        return paramsMap;
    }

    @Override
    public void error(String msg, int code)
    {
        Toast.makeText(getActivity(), msg + "\ncode:" + code, Toast.LENGTH_SHORT).show();
        if (mIsRefresh) mBgaRlRecipe.endRefreshing();
        else mBgaRlRecipe.endLoadingMore();
    }

    @Override
    public void showLoading()
    {
        //do nothing
    }

    @Override
    public void hideLoading()
    {
        //do nothing
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        mIsUpdateMainBgSend = false;
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout)
    {
        mIsRefresh = true;
        mCurPage = 1;
        if (mIsLazyLoad) {
            mBgaRlRecipe.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    mPresenter.get(ApiUtil.URL_RECIPE_LIST, RecipeListBean.class, true, false);
                }
            }, 1500);//延迟1.5秒（至少让用户能看见完整的动画啊）
        } else {
            mPresenter.get(ApiUtil.URL_RECIPE_LIST, RecipeListBean.class, true, false);
        }

    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout)
    {
        if (mCurPage == mTotalPage) {
            Toast.makeText(getActivity(), "已经是最后一页", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            mCurPage++;
            mIsRefresh = false;
            mPresenter.get(ApiUtil.URL_RECIPE_LIST, RecipeListBean.class, false, false);
            return true;
        }
    }

    @Override
    public void observable(Observable<Object> observable)
    {

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

    @Override
    public void onUserVisibleHint(boolean isVisibleToUser)
    {
        if (isVisibleToUser) {
            //表示滑动到这个Fragment了，如果已经拥有数据，可以发送更新背景图的请求
            if (mRecipeList != null && mRecipeList.size() != 0 && !mIsUpdateMainBgSend) {
                String mainBgUrl = getMainBgUrl();
                Apollo.get().send(ConstantUtil.UPDATE_MAIN_BACKGROUND, mainBgUrl);
                mIsUpdateMainBgSend = true;
            }
        } else {
            //滑动到其他Fragment后，首页图片也会进行更换。
            //将此布尔值置为false，以便用户再滑动回来后再次发送更新主页背景图的请求
            mIsUpdateMainBgSend = false;
        }
    }
}
