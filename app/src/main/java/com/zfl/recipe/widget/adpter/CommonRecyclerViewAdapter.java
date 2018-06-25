package com.zfl.recipe.widget.adpter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Map;

import rx.Observable;

/**
 * @Description
 * @Author ZFL
 * @Date 2017/6/27.
 */

public class CommonRecyclerViewAdapter extends RecyclerView.Adapter<BaseViewHolder>
{
    protected List mDatas;
    protected Context mContext;
    protected AdapterTemplate mTemplate;
    protected AdapterObservable mAdapterObservable;
    protected OnItemClick mClick;

    /**
     * 构造方法，需要响应式监听数据
     * @param context 上下文
     * @param datas 数据集
     * @param template item模板接口
     * @param adapterObservable 回调给RxAndroid的接口
     */
    public CommonRecyclerViewAdapter(Context context, List datas, AdapterTemplate template,AdapterObservable adapterObservable){
        mContext = context;
        mDatas = datas;
        mTemplate = template;
        mAdapterObservable = adapterObservable;
    }

    @Override
    public int getItemViewType(int position)
    {
        return mTemplate.getItemViewType().get(mDatas.get(position).getClass());
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        try
        {
            return mTemplate.getViewHolder().get(viewType).getConstructor(View.class).newInstance
                    (LayoutInflater.from(mContext).inflate(viewType,parent,false));
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final BaseViewHolder holder, int position)
    {
        holder.bindViewHolder(mDatas.get(position), position);
        holder.setAdapter(this);
        if (mAdapterObservable != null) {
            mAdapterObservable.observable(holder.getObservable());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(mClick != null)
                    mClick.onItemClick(holder.getAdapterPosition());
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                if(mClick != null)
                    mClick.onItemLongClick(holder.getAdapterPosition());
                return false;
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return mDatas.size();
    }

    public void setClick(OnItemClick click) {
        mClick = click;
    }


    public interface AdapterTemplate
    {
        /**
         *
         * @return map, class is entity class , integer is layoutId
         */
        Map<Class<?>,Integer> getItemViewType();

        /**
         *
         * @return SparseArray, class is what extents BaseViewHolder
         */
        SparseArray<Class<? extends BaseViewHolder>> getViewHolder();
    }
    public interface AdapterObservable
    {
        /**
         * Rx Observable, Integer was position
         */
        void observable(Observable<Object> observable);

    }

    public interface OnItemClick
    {
        /**
         * 单击事件
         * @param position 列表的位置
         */
        void onItemClick(int position);

        /**
         * 长按事件
         * @param position 列表的位置
         */
        void onItemLongClick(int position);
    }

}
