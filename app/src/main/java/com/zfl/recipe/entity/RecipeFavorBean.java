package com.zfl.recipe.entity;

import com.zfl.recipe.entity.common.CommonResultBean;

/**
 * @Description 专门用于查询是否是已经加入收藏菜谱的bean
 * @Author ZFL
 * @Date 2017/8/12.
 */
public class RecipeFavorBean extends CommonResultBean
{
    public boolean isFavor;
//    public String name;
    public int recipeEventType;
    public String menuId;
}
