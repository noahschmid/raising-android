package com.raising.app.util;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class CircleImageDrawable extends Drawable {

    private final RectF mBounds = new RectF();
    private final RectF mDrawableRect = new RectF();
    private Rect mScaledRect = new Rect();
    private Rect mOriginalRect = new Rect();
    private final RectF mBorderRect = new RectF();

    private final Matrix mShaderMatrix = new Matrix();
    private final Paint mBitmapPaint = new Paint();
    private final Paint mBorderPaint = new Paint();
    private final Paint mBackgroundPaint = new Paint();

    private int mBorderColor = Color.BLACK;
    private int mBackgroundColor = Color.TRANSPARENT;
    private int mForegroundColor = Color.BLACK;
    private int mBorderWidth = 0;

    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;
    private int mBitmapWidth;
    private int mBitmapHeight;

    private float mDrawableRadius;
    private float mBorderRadius;

    private final Rect mDstRect = new Rect();

    public CircleImageDrawable(Bitmap bitmap) {
        mBitmap = bitmap;
        mBitmapHeight = mBitmap.getHeight();
        mBitmapWidth = mBitmap.getWidth();
    }

    public CircleImageDrawable(Bitmap bitmap, int borderWidth) {
        mBitmap = bitmap;
        mBitmapHeight = mBitmap.getHeight();
        mBitmapWidth = mBitmap.getWidth();
        mBorderWidth = borderWidth;
    }

    public CircleImageDrawable(Bitmap bitmap, int borderWidth, int bgColor, int fColor) {
        mBitmap = bitmap;
        mBitmapHeight = mBitmap.getHeight();
        mBitmapWidth = mBitmap.getWidth();
        mBorderWidth = borderWidth;
        mBackgroundColor = bgColor;
        mForegroundColor = fColor;
    }

    @Override
    public void draw(Canvas canvas) {
        int width = mBitmap.getWidth() - 1;
        int height = mBitmap.getHeight() - 1;

        int maxSize = (int)(mBorderRadius*1.6);

        if (width > height) {
            float ratio = (float) width / maxSize;
            width = maxSize;
            height = (int)(height / ratio);
        } else if (height > width) {
            float ratio = (float) height / maxSize;
            height = maxSize;
            width = (int)(width / ratio);
        } else {
            height = maxSize;
            width = maxSize;
        }

        int centerX = (int)(mBorderRadius*2 - width)/2;
        int centerY = (int)(mBorderRadius*2 - height)/2;

        centerX = Math.max(0, centerX);
        centerY = Math.max(0, centerY);

        mScaledRect = new Rect(centerX, centerY, width, height);

        canvas.drawCircle(mBounds.width() / 2.0f, mBounds.height() / 2.0f, mBorderRadius, mBackgroundPaint);

        canvas.drawBitmap(mBitmap, null, mScaledRect, mBitmapPaint);
        //canvas.drawCircle(mBounds.width() / 2.0f, mBounds.height() / 2.0f, mDrawableRadius, mBitmapPaint);
        if (mBorderWidth != 0) {
            canvas.drawCircle(mBounds.width() / 2.0f, mBounds.height() / 2.0f, mBorderRadius, mBorderPaint);
        }
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mBounds.set(bounds);
        setup();
    }

    private void setup() {
        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setShader(mBitmapShader);
        mBitmapPaint.setColor(mForegroundColor);

        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);

        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setColor(mBackgroundColor);

        mBorderRect.set(mBounds);
        mBorderRadius = Math.max((mBorderRect.height() - mBorderWidth) / 2f, (mBorderRect.width() - mBorderWidth) / 2f);

        mDrawableRect.set(mBorderRect);
        mDrawableRect.inset(mBorderWidth, mBorderWidth);
        mDrawableRadius = Math.min(mDrawableRect.height() / 2.0f, mDrawableRect.width() / 2.0f);

        updateShaderMatrix();
        invalidateSelf();
    }

    private void updateShaderMatrix() {
        float scale;
        float dx = 0;
        float dy = 0;

        mShaderMatrix.set(null);

        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
            scale = mDrawableRect.height() / (float) mBitmapHeight;
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
        } else {
            scale = mDrawableRect.width() / (float) mBitmapWidth;
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
        }

        mShaderMatrix.setScale(scale, scale);
        mShaderMatrix.postTranslate((int) (dx + 0.5f) + mDrawableRect.left, (int) (dy + 0.5f) + mDrawableRect.top);

        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }

    @Override
    public void setAlpha(int alpha) {
        mBitmapPaint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mBitmapPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }

}