package com.taobao.luaview.userdata.ui;


import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.webkit.URLUtil;
import android.widget.ImageView;

import com.taobao.android.luaview.R;
import com.taobao.luaview.util.ImageUtil;
import com.taobao.luaview.util.LuaUtil;
import com.taobao.luaview.util.LuaViewUtil;
import com.taobao.luaview.view.imageview.BaseImageView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;

/**
 * Image 数据封装
 *
 * @param <T>
 * @author song
 */
public class UDImageView<T extends BaseImageView> extends UDView<T> {
    private AnimationDrawable mFrameAnimation;

    public UDImageView(T view, Globals globals, LuaValue metatable, LuaValue initParams) {
        super(view, globals, metatable, initParams);
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
                    imageView.setTag(R.id.lv_tag_url, urlOrName);//需要设置tag，防止callback在回调的时候调用错误
                    imageView.loadUrl(urlOrName, callback == null ? null : new BaseImageView.LoadCallback() {
                        @Override
                        public void onLoadResult(Drawable drawable) {
                            if (callback != null) {
                                if (imageView != null && urlOrName != null && urlOrName.equals(imageView.getTag(R.id.lv_tag_url))) {//异步回调，需要checktag
                                    LuaUtil.callFunction(callback, drawable != null ? LuaBoolean.TRUE : LuaBoolean.FALSE);
                                }
                            }
                        }
                    });
                } else {
                    imageView.setTag(R.id.lv_tag_url, null);
                    Drawable drawable = null;
                    if (getLuaResourceFinder() != null) {
                        drawable = getLuaResourceFinder().findDrawable(urlOrName);
                        imageView.setImageDrawable(drawable);
                    }

                    if (callback != null) {//本地图片直接调用callback
                        LuaUtil.callFunction(callback, drawable != null ? LuaBoolean.TRUE : LuaBoolean.FALSE);
                    }
                }
            } else {//设置null
                imageView.loadUrl(null, null);//如果不设置null是否可以被调用 TODO
                if (callback != null) {//本地图片直接调用callback
                    LuaUtil.callFunction(callback, LuaBoolean.TRUE);
                }
            }
        }
        return this;
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
                    for (Drawable frame : frames) {
                        mFrameAnimation.addFrame(frame, duration);
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

