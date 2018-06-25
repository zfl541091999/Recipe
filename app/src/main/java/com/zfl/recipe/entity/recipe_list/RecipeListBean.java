package com.zfl.recipe.entity.recipe_list;

import com.zfl.recipe.entity.RecipeInfo;
import com.zfl.recipe.entity.common.CommonResultBean;

import java.util.List;

/**
 * @Description
 * @Author ZFL
 * @Date 2017/6/22.
 */

public class RecipeListBean extends CommonResultBean
{
    public RecipeListResultBean result;

    public static class RecipeListResultBean {
        public int curPage;
        public int total;
        public List<RecipeInfo> list;
    }

}
