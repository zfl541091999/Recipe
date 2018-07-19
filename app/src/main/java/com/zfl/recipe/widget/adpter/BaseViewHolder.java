package com.zfl.recipe.widget.adpter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;

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
    protected ObservableEmitter<Object> emi;
    public BaseViewHolder(View itemView)
    {
        super(itemView);
        ButterKnife.bind(this,itemView);//bind 注解
        context = itemView.getContext();

    }
    protected void createObservable(){

        observable = Observable.create(new ObservableOnSubscribe<Object>()
        {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Exception
            {
                emi = emitter;
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
