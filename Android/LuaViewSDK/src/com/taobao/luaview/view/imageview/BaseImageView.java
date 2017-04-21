/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.view.imageview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;

import com.taobao.luaview.global.LuaView;
import com.taobao.luaview.provider.ImageProvider;
import com.taobao.luaview.view.drawable.LVGradientDrawable;
import com.taobao.luaview.view.foreground.ForegroundImageView;

import java.lang.ref.WeakReference;

/**
 * Base ImageView
 *
 * @author song
 * @date 16/3/9
 */
public abstract class BaseImageView extends ForegroundImageView {
    private LVGradientDrawable mStyleDrawable = null;
    private Path mPath = null;

    private String mUrl;

    protected Boolean mAttachedWindow = null;
    protected boolean isNetworkMode = false;

    public void setIsNetworkMode(boolean isNetworkMode) {
        this.isNetworkMode = isNetworkMode;
    }

    public BaseImageView(Context context) {
        super(context);
        initRecycler(context);
    }

    private void initRecycler(Context context) {
        if (context instanceof Activity) {
            ImageActivityLifeCycle.getInstance(((Activity) context).getApplication()).watch(this);
        }
    }

    public void loadUrl(final String url, final DrawableLoadCallback callback) {
        this.mUrl = url;
        final ImageProvider provider = LuaView.getImageProvider();
        if (provider != null) {
            provider.load(getContext(), new WeakReference<BaseImageView>(this), url, new WeakReference<DrawableLoadCallback>(callback));
        }
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        restoreImage();
        mAttachedWindow = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        releaseBitmap();
        mAttachedWindow = false;
    }


    public void restoreImage() {
        if (isNetworkMode && mAttachedWindow != null) {// 恢复被清空的image，只有已经被加过才恢复
            if (mUrl != null) {
                loadUrl(mUrl, null);
            } else {
                setImageDrawable(null);
            }
        }
    }

    public void releaseBitmap() {// 释放图片内存
        if (isNetworkMode) {//只有被加过才释放
            setImageDrawable(null);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final boolean hasStyle = setupStyleDrawable();

        if (hasStyle && canvas != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {//fix API 11~ API 17, 部分19也会有问题，一并解决
                setLayerType(LAYER_TYPE_SOFTWARE, null);
                try {
                    canvas.clipPath(getClipPath());
                } catch (UnsupportedOperationException e) {
                }
            } else {
                canvas.clipPath(getClipPath());
            }
        }

        super.onDraw(canvas);

        if (hasStyle) {//背景放到上面画，默认为透明颜色
            mStyleDrawable.setColor(Color.TRANSPARENT);
            mStyleDrawable.draw(canvas);
        }
    }

    //-------------------------------------background style-----------------------------------------

    /**
     * get clip path of StyleDrawable
     *
     * @return
     */
    private Path getClipPath() {
        if (mPath == null) {
            mPath = new Path();
        }
        final Rect rect = mStyleDrawable.getBounds();
        final float radius = mStyleDrawable.getCornerRadius();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mPath.addRoundRect(rect.left, rect.top, rect.right, rect.bottom, radius, radius, Path.Direction.CW);
        } else {
            mPath.addCircle(rect.left + radius, rect.top + radius, radius, Path.Direction.CW);
            mPath.addCircle(rect.right - radius, rect.top + radius, radius, Path.Direction.CW);
            mPath.addCircle(rect.right - radius, rect.bottom - radius, radius, Path.Direction.CW);
            mPath.addCircle(rect.left + radius, rect.bottom - radius, radius, Path.Direction.CW);
            mPath.addRect(rect.left + radius, rect.top, rect.right - radius, rect.bottom, Path.Direction.CW);
            mPath.addRect(rect.left, rect.top + radius, rect.right, rect.bottom - radius, Path.Direction.CW);
        }
        return mPath;
    }


    /**
     * 设置好drawable的样式
     *
     * @return
     */
    private boolean setupStyleDrawable() {
        if (/*getDrawable() != null && */mStyleDrawable != null) {
            mStyleDrawable.setBounds(0, 0, getWidth(), getHeight());
            return true;
        }
        return false;
    }

    private synchronized LVGradientDrawable getStyleDrawable() {
        if (mStyleDrawable == null) {
            mStyleDrawable = new LVGradientDrawable();
        }
        return mStyleDrawable;
    }

    /**
     * set corner radius
     *
     * @param radius
     */
    public void setCornerRadius(float radius) {
        getStyleDrawable().setCornerRadius(radius);
    }

    public float getCornerRadius() {
        if (mStyleDrawable != null) {
            return mStyleDrawable.getCornerRadius();
        }
        return 0;
    }

    /**
     * 设置边框宽度
     */
    public void setStrokeWidth(int width) {
        getStyleDrawable().setStrokeWidth(width);
    }

    public int getStrokeWidth() {
        return mStyleDrawable != null ? mStyleDrawable.getStrokeWidth() : 0;
    }

    /**
     * 设置边框颜色
     *
     * @param color
     */
    public void setStrokeColor(int color) {
        getStyleDrawable().setStrokeColor(color);
    }

    public int getStrokeColor() {
        return mStyleDrawable != null ? mStyleDrawable.getStrokeColor() : 0;
    }

    /**
     * Dash size
     *
     * @param dashWidth
     * @param dashGap
     */
    public void setBorderDash(Float dashWidth, Float dashGap) {
        getStyleDrawable().setDashSize(dashWidth, dashGap);
    }

    public float getBorderDashWidth() {
        return mStyleDrawable != null ? mStyleDrawable.getDashWidth() : 0;
    }

    public float getBorderDashGap() {
        return mStyleDrawable != null ? mStyleDrawable.getDashGap() : 0;
    }

}
