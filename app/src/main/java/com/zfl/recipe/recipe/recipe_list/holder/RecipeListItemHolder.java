package com.zfl.recipe.recipe.recipe_list.holder;

import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zfl.recipe.R;
import com.zfl.recipe.entity.RecipeInfo;
import com.zfl.recipe.utils.BGUtil;
import com.zfl.recipe.utils.ScreenUtil;
import com.zfl.recipe.widget.adpter.BaseViewHolder;
import com.zfl.recipe.widget.glide.GlideRoundTransform;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @Description
 * @Author ZFL
 * @Date 2017/6/28.
 */

public class RecipeListItemHolder extends BaseViewHolder<RecipeInfo>
{

    @Bind(R.id.ivRecipeListItemThumbnail)
    ImageView mIvRecipeListItemThumbnail;
    @Bind(R.id.tvRecipeListItemName)
    TextView mTvRecipeListItemName;
    @Bind(R.id.llRecipeListItemLabelArea)
    LinearLayout mLlRecipeListItemLabelArea;

    private static final int MAX_LABEL_NUM = 5;//最多添加5个标签


    public RecipeListItemHolder(View itemView)
    {
        super(itemView);
        createObservable();
    }

    @Override
    public void bindViewHolder(RecipeInfo data, int position)
    {
        Glide.with(context).load(data.thumbnail).transform(new GlideRoundTransform(context,
                ScreenUtil.dp2px(context, 3), ScreenUtil.dp2px(context, 0.5f), R.color.colorPrimary)).placeholder(R.mipmap.default_recipe_img).into(mIvRecipeListItemThumbnail);
        mTvRecipeListItemName.setText(data.name);
        //为其添加标签
        mLlRecipeListItemLabelArea.removeAllViews();
        String[] recipeTypes = data.ctgTitles.split(",");
        //目前的标签数量
        int curLabelNum = 0;
        //如果有主标签，添加主标签
        if (!TextUtils.isEmpty(data.mainLabel)) {
            addLabelView(data.mainLabel);
            curLabelNum = 1;//主标签已经占据了一个位置
        }
        for (int i = 0; i < recipeTypes.length; i++) {
            if (!TextUtils.isEmpty(recipeTypes[i])) {
                if (!recipeTypes[i].equals(data.mainLabel)) {
                    addLabelView(recipeTypes[i]);
                    curLabelNum++;
                }
            }
            if (curLabelNum == MAX_LABEL_NUM) break;//退出循环
        }
    }

    private void addLabelView(String text) {
        TextView textView = new TextView(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(ScreenUtil.dp2px(context, 5f), 0, 0, 0);
        textView.setLayoutParams(lp);
        int leftOrRightPadding = ScreenUtil.dp2px(context, 5f);
        int topOrBottomPadding = ScreenUtil.dp2px(context, 1f);
        textView.setPadding(leftOrRightPadding, topOrBottomPadding, leftOrRightPadding, topOrBottomPadding);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        textView.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        textView.setText(text);
        textView.setBackground(BGUtil.gradientDr(context, ScreenUtil.dp2px(context, 0.5f), 10f, R.color.colorPrimary, R.color.white));
        mLlRecipeListItemLabelArea.addView(textView);
    }

    @OnClick({R.id.ivRecipeListItemThumbnail})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivRecipeListItemThumbnail:
                Map<String, Object> map = new HashMap<>();
                map.put("position", getAdapterPosition());
                emi.onNext(map);
                break;
        }
    }
}
