package com.zfl.recipe.widget.adpter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;

/**
 * @Description
 * @Author MoseLin
 * @Date 2016/7/21.
 */

public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder
{
    protected Context context;
    private CommonRecyclerViewAdapter adapter;
    protected Observable<Object> observable;
    protected Subscriber<Object> sub;
    public BaseViewHolder(View itemView)
    {
        super(itemView);
        ButterKnife.bind(this,itemView);//bind 注解
        context = itemView.getContext();

    }
    protected void createObservable(){
        observable = Observable.create(new Observable.OnSubscribe<Object>()
        {
            @Override
            public void call(Subscriber<? super Object> subscriber)
            {
                sub = subscriber;
            }

        });
    }

    public abstract void bindViewHolder(T data, int position);

    public void setAdapter(CommonRecyclerViewAdapter adapter)
    {
        this.adapter = adapter;
    }

    public  Observable<Object> getObservable(){
        return observable;
    }
}
