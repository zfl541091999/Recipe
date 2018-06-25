package com.zfl.recipe.widget.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * @author ZFL
 * @desc    通过gilde下载图片转换层圆角图片的方法
 * @time 2016/8/30 0030 11:54
 */
public class GlideRoundTransform extends BitmapTransformation
{

    private float mCornerRadius = 0f;

    private int mBorderWidth;

    private int mBorderColor;

    public GlideRoundTransform(Context context, int cornerRadius, int borderWidth, int borderColorRes) {
        super(context);
        this.mCornerRadius = cornerRadius;
        mBorderColor = context.getResources().getColor(borderColorRes);
        mBorderWidth = borderWidth;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        int targetWidth = toTransform.getWidth() + mBorderWidth * 2;
        int targetHeight = toTransform.getHeight() + mBorderWidth * 2;
        //ARGB_8888可以解决图片显示四个黑角的问题
        Bitmap result = pool.get(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(result);
        //先画透明图案？
        canvas.drawColor(Color.TRANSPARENT);
        //先画圆角矩形
        Paint borderPaint = new Paint();
        borderPaint.setColor(mBorderColor);
        borderPaint.setAntiAlias(true);
        RectF borderRectF = new RectF(0, 0, targetWidth, targetHeight);
        canvas.drawRoundRect(borderRectF, mCornerRadius, mCornerRadius, borderPaint);
        //再画图片
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(toTransform, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        RectF rectF = new RectF(mBorderWidth, mBorderWidth, targetWidth - mBorderWidth, targetHeight - mBorderWidth);
        canvas.drawRoundRect(rectF, mCornerRadius, mCornerRadius, paint);
        return result;
    }

    @Override
    public String getId() {
        return getClass().getName() + Math.round(mCornerRadius);
    }
}
