package com.zfl.recipe.widget.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * @Description 给Glide加载的图片加上一层边框
 * @Author ZFL
 * @Date 2017/7/3.
 */

public class GlideBorderTransform extends BitmapTransformation
{

    private Paint mBorderPaint;
    private int mBorderWidth;

    /**
     * 边框宽度和颜色id不能为0
     * @param context
     * @param borderWidth
     * @param borderColorResId
     */
    public GlideBorderTransform(Context context,int borderWidth, int borderColorResId)
    {
        super(context);
        int borderColor = context.getResources().getColor(borderColorResId);
//        mBorderWidth = Resources.getSystem().getDisplayMetrics().density * borderWidth;

        mBorderPaint = new Paint();
        mBorderPaint.setColor(borderColor);
        mBorderPaint.setStyle(Paint.Style.FILL);
        mBorderPaint.setStrokeWidth(borderWidth);
        mBorderWidth = borderWidth;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight)
    {
        int targetWidth = toTransform.getWidth() + mBorderWidth * 2;
        int targetHeight = toTransform.getHeight() + mBorderWidth * 2;
        Bitmap result = pool.get(targetWidth, targetHeight, toTransform.getConfig());
        if (result == null) {
            result = Bitmap.createBitmap(targetWidth, targetHeight, toTransform.getConfig());
        }
        //创建目标图片的画布
        Canvas canvas = new Canvas(result);
        //先画原始图片在正中央
        Paint paint = new Paint();
        canvas.drawBitmap(toTransform, mBorderWidth, mBorderWidth, paint);
        //画边框(上下左右)
        canvas.drawLine(0, 0, targetWidth, 0, mBorderPaint);
        canvas.drawLine(0, targetHeight, targetWidth, targetHeight, mBorderPaint);
        canvas.drawLine(0, 0, 0, targetHeight, mBorderPaint);
        canvas.drawLine(targetWidth, 0, targetWidth, targetHeight, mBorderPaint);
        return result;
    }

    @Override
    public String getId()
    {
        return getClass().getName();
    }
}
