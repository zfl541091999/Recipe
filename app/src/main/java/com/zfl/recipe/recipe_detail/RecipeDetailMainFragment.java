package com.zfl.recipe.recipe_detail;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zfl.recipe.R;
import com.zfl.recipe.entity.recipe_detail.RecipeDetailBean;
import com.zfl.recipe.mvp.view.BaseFragment;
import com.zfl.recipe.utils.BGUtil;
import com.zfl.recipe.utils.ScreenUtil;

import java.util.Map;

import butterknife.Bind;

/**
 * @Description 这个Fragment主要介绍菜谱的类别，简介
 * @Author ZFL
 * @Date 2017/7/14.
 */

public class RecipeDetailMainFragment extends BaseFragment
{

    @Bind(R.id.tvRecipeDetailTitle)
    TextView mTvRecipeDetailTitle;
    @Bind(R.id.llRecipeDetailLabelArea)
    LinearLayout mLlRecipeDetailLabelArea;
    @Bind(R.id.tvRecipeDetailSummary)
    TextView mTvRecipeDetailSummary;
    @Bind(R.id.tvRecipeDetailIngredients)
    TextView mTvRecipeDetailIngredients;

    private RecipeDetailBean mDetailBean;

    public void setDetailBean(RecipeDetailBean bean)
    {
        mDetailBean = bean;

    }

    @Override
    protected void lazyLoad()
    {
        //开始使用数据对界面进行初始化
        mTvRecipeDetailTitle.setText(mDetailBean.result.recipe.title);
        mTvRecipeDetailSummary.setText("简介：" + mDetailBean.result.recipe.sumary);
        //去除多余字符
        mTvRecipeDetailIngredients.setText("食材：" + mDetailBean.result.recipe.ingredients.replace("[\"", "").replace("\"]", ""));
        //插入标签
        String[] labels = mDetailBean.result.ctgTitles.split(",");
        for (int i = 0; i < labels.length; i++)
        {
            addLabelView(labels[i]);
        }
    }

    private void addLabelView(String text)
    {
        TextView textView = new TextView(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams
                .WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(ScreenUtil.dp2px(getActivity(), 5f), 0, 0, 0);
        textView.setLayoutParams(lp);
        int leftOrRightPadding = ScreenUtil.dp2px(getActivity(), 5f);
        int topOrBottomPadding = ScreenUtil.dp2px(getActivity(), 1f);
        textView.setPadding(leftOrRightPadding, topOrBottomPadding, leftOrRightPadding,
                topOrBottomPadding);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        textView.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
        textView.setText(text);
        textView.setBackground(BGUtil.gradientDr(getActivity(), ScreenUtil.dp2px(getActivity(), 0.5f), 10f, R.color.colorPrimary, R.color.white));
        mLlRecipeDetailLabelArea.addView(textView);
    }

    @Override
    protected int getLayoutResID()
    {
        return R.layout.fragment_recipe_detail_main;
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
