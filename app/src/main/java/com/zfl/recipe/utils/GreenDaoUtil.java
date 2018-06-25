//package com.zfl.recipe.utils;
//
//import android.content.Context;
//
//import com.zfl.recipe.entity.RecipeInfo;
//import com.zfl.recipe.greendao.gen.DaoMaster;
//import com.zfl.recipe.greendao.gen.DaoSession;
//
//import org.greenrobot.greendao.rx.RxDao;
//
//import java.util.List;
//
//import rx.Observable;
//import rx.android.schedulers.AndroidSchedulers;
//
///**
// * @Description 现在只有去除，存储，读取收藏菜谱的功能(采用RxJava形式)<br>
// *     在使用服务器搭建收藏菜谱后，不再使用GreenDao创建本地的数据库
// * @Author ZFL
// * @Date 2017/7/21.
// */
//@Deprecated
//public class GreenDaoUtil
//{
//
//    private DaoSession mDaoSession;
//
//    private RxDao<RecipeInfo, String> mRecipeInfoDao;
//
//    public GreenDaoUtil(Context context) {
//        DaoMaster.DevOpenHelper openHelper = new DaoMaster.DevOpenHelper(context, ConstantUtil.FAVOR_RECIPE_DB_NAME);
//        DaoMaster master = new DaoMaster(openHelper.getWritableDb());
//        mDaoSession = master.newSession();
//        mRecipeInfoDao = mDaoSession.getRecipeInfoDao().rx();
//    }
//
//    /**
//     * 增
//     */
//    public Observable<RecipeInfo> insert(RecipeInfo info) {
//        return mRecipeInfoDao.insert(info).observeOn(AndroidSchedulers.mainThread());
//    }
//
//    /**
//     * 删
//     */
//    public Observable<Void> delete(RecipeInfo info) {
//        return mRecipeInfoDao.delete(info).observeOn(AndroidSchedulers.mainThread());
//    }
//
//    /**
//     * 改
//     */
//    public Observable<RecipeInfo> update(RecipeInfo info) {
//        return mRecipeInfoDao.update(info).observeOn(AndroidSchedulers.mainThread());
//    }
//
//    /**
//     * 载入所有收藏菜谱
//     * @return
//     */
//    public Observable<List<RecipeInfo>> getAll() {
//        return mRecipeInfoDao.loadAll().observeOn(AndroidSchedulers.mainThread());
//    }
//
//    /**
//     * 查
//     */
//    public Observable<RecipeInfo> isFavor(String menuId) {
//        return mRecipeInfoDao.load(menuId).observeOn(AndroidSchedulers.mainThread());
//    }
//
//    //单例模式
//    private static GreenDaoUtil mUtil;
//
//    public static void init(Context context) {
//        mUtil = new GreenDaoUtil(context);
//    }
//
//    public static GreenDaoUtil getInstance() {
//        if (mUtil == null) {
//            throw new RuntimeException("GreenDaoUtil is not initialized!");
//        }
//        return mUtil;
//    }
//}
