/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.ui;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.webkit.URLUtil;
import android.widget.ImageView;

import com.taobao.luaview.fun.mapper.LuaViewApi;
import com.taobao.luaview.global.Constants;
import com.taobao.luaview.global.LuaResourceFinder;
import com.taobao.luaview.global.VmVersion;
import com.taobao.luaview.scriptbundle.asynctask.SimpleTask1;
import com.taobao.luaview.userdata.kit.UDBitmap;
import com.taobao.luaview.util.ImageUtil;
import com.taobao.luaview.util.LogUtil;
import com.taobao.luaview.util.LuaUtil;
import com.taobao.luaview.util.LuaViewUtil;
import com.taobao.luaview.view.imageview.BaseImageView;
import com.taobao.luaview.view.imageview.DrawableLoadCallback;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * Image 数据封装
 *
 * @param <T>
 * @author song
 */
public class UDImageView<T extends BaseImageView> extends UDView<T> {
    private AnimationDrawable mFrameAnimation;

    public UDImageView(T view, Globals globals, LuaValue metatable, Varargs initParams) {
        super(view, globals, metatable, initParams);
    }

    @LuaViewApi(since = VmVersion.V_540, revisions = {"之前写在View无效果，5.4.0开始，Image支持该方法"})
    @Override
    public UDView setCornerRadius(float radius) {
        final T view = getView();
        if (view != null) {
            view.setCornerRadius(radius);
        }
        return this;
    }

    @LuaViewApi(since = VmVersion.V_540, revisions = {"之前写在View无效果，5.4.0开始，Image支持该方法"})
    @Override
    public float getCornerRadius() {
        final T view = getView();
        if (view != null) {
            return view.getCornerRadius();
        }
        return 0;
    }

    @LuaViewApi(since = VmVersion.V_540, revisions = {"之前写在View无效果，5.4.0开始，Image支持该方法"})
    @Override
    public UDView setBorderWidth(final int borderWidth) {
        final T view = getView();
        if (view != null) {
            view.setStrokeWidth(borderWidth);
        }
        return this;
    }

    @LuaViewApi(since = VmVersion.V_540, revisions = {"之前写在View无效果，5.4.0开始，Image支持该方法"})
    @Override
    public int getBorderWidth() {
        final T view = getView();
        if (view != null) {
            return view.getStrokeWidth();
        }
        return 0;
    }

    @LuaViewApi(since = VmVersion.V_540, revisions = {"之前写在View无效果，5.4.0开始，Image支持该方法"})
    @Override
    public UDView setBorderColor(final Integer borderColor) {
        if (borderColor != null) {
            final T view = getView();
            if (view != null) {
                view.setStrokeColor(borderColor);
            }
        }
        return this;
    }

    @LuaViewApi(since = VmVersion.V_540, revisions = {"之前写在View无效果，5.4.0开始，Image支持该方法"})
    @Override
    public int getBorderColor() {
        final T view = getView();
        if (view != null) {
            return view.getStrokeColor();
        }
        return 0;
    }

    @LuaViewApi(since = VmVersion.V_540, revisions = {"之前写在View无效果，5.4.0开始，Image支持该方法"})
    @Override
    public UDView setBorderDashSize(float dashWidth, float dashGap) {
        final T view = getView();
        if (view != null) {
            view.setBorderDash(dashWidth, dashGap);
        }
        return this;
    }

    @LuaViewApi(since = VmVersion.V_540, revisions = {"之前写在View无效果，5.4.0开始，Image支持该方法"})
    @Override
    public float getBorderDashWidth() {
        final T view = getView();
        if (view != null) {
            return view.getBorderDashWidth();
        }
        return 0;
    }

    @LuaViewApi(since = VmVersion.V_540, revisions = {"之前写在View无效果，5.4.0开始，Image支持该方法"})
    @Override
    public float getBorderDashGap() {
        final T view = getView();
        if (view != null) {
            return view.getBorderDashGap();
        }
        return 0;
    }

    /**
     * set data
     *
     * @param data
     * @return
     */
    public UDImageView setImageBytes(final byte[] data) {
        if (data != null) {
            final T imageView = getView();
            if (imageView != null) {
                new SimpleTask1<Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(Object... params) {
                        return BitmapFactory.decodeByteArray(data, 0, data.length);
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {//TODO 这里的bitmap是不经过缓存的，需要考虑
                        if (bitmap != null) {
                            imageView.setImageBitmap(bitmap);
                        }
                    }
                }.executeInPool();
            }
        }
        return this;
    }

    /**
     * set image bitmap
     *
     * @param bitmap
     * @return
     */
    public UDImageView setImageBitmap(UDBitmap bitmap) {
        final T imageView = getView();
        if (imageView != null) {
            imageView.setImageBitmap(bitmap.getBitmap());
        }
        return this;
    }

    /**
     * 设置图片
     *
     * @param urlOrName
     * @param callback
     * @return
     */
    public UDImageView setImageUrl(final String urlOrName, final LuaFunction callback) {
        final T imageView = getView();
        if (imageView != null) {
            if (!TextUtils.isEmpty(urlOrName)) {
                if (URLUtil.isNetworkUrl(urlOrName)) {//network
                    imageView.setTag(Constants.RES_LV_TAG_URL, urlOrName);//需要设置tag，防止callback在回调的时候调用错误
                    imageView.setIsNetworkMode(true);
                    imageView.loadUrl(urlOrName, callback == null ? null : new DrawableLoadCallback() {
                        @Override
                        public void onLoadResult(Drawable drawable) {
                            if (callback != null) {
                                if (imageView != null && urlOrName != null && urlOrName.equals(imageView.getTag(Constants.RES_LV_TAG_URL))) {//异步回调，需要checktag
                                    LuaUtil.callFunction(callback, drawable != null ? LuaBoolean.TRUE : LuaBoolean.FALSE);
                                }
                            }
                        }
                    });
                } else {
                    imageView.setIsNetworkMode(false);
                    imageView.setTag(Constants.RES_LV_TAG_URL, null);
                    imageView.setUrl(urlOrName);
                    Drawable drawable = null;
                    if (getLuaResourceFinder() != null) {
                        drawable = getLuaResourceFinder().findDrawable(urlOrName);
                        imageView.setImageDrawable(drawable);
                    }
                    if (callback != null) {//本地图片直接调用callback
                        LuaUtil.callFunction(callback, drawable != null ? LuaBoolean.TRUE : LuaBoolean.FALSE);
                    }
//                    setImageUrlAsync(imageView, urlOrName, callback);//异步加载图片，需要改现有代码，先hold
                }
            } else {//设置null
                imageView.setIsNetworkMode(false);
                imageView.loadUrl(null, null);//如果不设置null是否可以被调用 TODO
                if (callback != null) {//本地图片直接调用callback
                    LuaUtil.callFunction(callback, LuaBoolean.TRUE);
                }
            }
        }
        return this;
    }

    private void setImageUrlAsync(final T imageView, final String urlOrName, final LuaFunction callback) {
        if (getLuaResourceFinder() != null) {//异步加载图片
            getLuaResourceFinder().findDrawable(urlOrName, new LuaResourceFinder.DrawableFindCallback() {
                @Override
                public void onStart(String urlOrPath) {
                    if (imageView != null && urlOrName != null) {
                        imageView.setTag(Constants.RES_LV_TAG_URL, urlOrName);
                    }
                }

                @Override
                public void onFinish(Drawable drawable) {
                    if (imageView != null && urlOrName != null && urlOrName.equals(imageView.getTag(Constants.RES_LV_TAG_URL))) {
                        imageView.setImageDrawable(drawable);
                    }

                    if (callback != null) {//本地图片直接调用callback
                        LuaUtil.callFunction(callback, drawable != null ? LuaBoolean.TRUE : LuaBoolean.FALSE);
                    }
                }
            });
        }
    }

    /**
     * 获取图片url
     *
     * @return
     */
    public String getImageUrl() {
        return getView() != null ? getView().getUrl() : "";
    }

    /**
     * 设置图片缩放模式
     *
     * @param scaleType
     * @return
     */
    public UDImageView setScaleType(ImageView.ScaleType scaleType) {
        final T view = getView();
        if (view != null) {
            view.setScaleType(scaleType);
        }
        return this;
    }

    /**
     * 获取图片的scale type，
     *
     * @return
     */
    public String getScaleType() {
        return getView() != null ? getView().getScaleType().name() : ImageView.ScaleType.FIT_XY.name();
    }

    /**
     * 开始帧动画(目前只支持本地动画)
     *
     * @param images
     * @param duration
     * @return
     */
    public UDImageView startAnimationImages(String[] images, int duration, boolean repeat) {
        final T view = getView();
        if (view != null) {
            Drawable[] frames = null;
            if (images != null && images.length > 0) {
                if (getLuaResourceFinder() != null) {
                    frames = new Drawable[images.length];
                    for (int i = 0; i < images.length; i++) {
                        frames[i] = getLuaResourceFinder().findDrawable(images[i]);
                    }
                }
                if (frames != null && frames.length > 0) {
                    mFrameAnimation = new AnimationDrawable();
                    try {
                        for (Drawable frame : frames) {
                            mFrameAnimation.addFrame(frame, duration);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                        LogUtil.e("[LuaView-Error] UDImageView.startAnimationImages failed!");
                    }
                    mFrameAnimation.setOneShot(!repeat);
                    LuaViewUtil.setBackground(view, mFrameAnimation);
                    mFrameAnimation.setVisible(true, true);
                    mFrameAnimation.start();
                }
            }
        }
        return this;
    }

    /**
     * 停止帧动画
     *
     * @return
     */
    public UDImageView stopAnimationImages() {
        if (mFrameAnimation != null) {
            mFrameAnimation.stop();
            mFrameAnimation = null;
        }
        return this;
    }

    /**
     * 是否在播放帧动画
     *
     * @return
     */
    public boolean isAnimationImages() {
        return mFrameAnimation != null && mFrameAnimation.isRunning();
    }

    /**
     * 调整图片大小
     *
     * @return
     */
    @Override
    public UDImageView adjustSize() {
        final T view = getView();
        if (view != null) {
            ImageUtil.adjustSize(view);
        }
        return this;
    }
}

