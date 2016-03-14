package com.taobao.luaview.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.webkit.URLUtil;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.taobao.luaview.global.LuaResourceFinder;
import com.taobao.luaview.view.imageview.BaseImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 图片处理
 *
 * @author song
 * @date 15/10/26
 */
public class ImageUtil {

    public interface LoadCallback {
        void onLoadResult(Map<String, Drawable> drawables);
    }

    /**
     * 调整图片的frame
     *
     * @param imageView
     */
    public static void adjustSize(ImageView imageView) {
        if (imageView != null && imageView.getLayoutParams() != null && imageView.getDrawable() != null) {
            imageView.getLayoutParams().width = (imageView.getDrawable()).getIntrinsicWidth();
            imageView.getLayoutParams().height = (imageView.getDrawable()).getIntrinsicHeight();
        }
    }

    /**
     * 获取一个图片，并调用回调
     *
     * @param context
     * @param url
     * @param callback
     */
    public static void fetch(Context context, final LuaResourceFinder finder, final String url, final BaseImageView.LoadCallback callback) {
        if (context != null && !TextUtils.isEmpty(url)) {
            if (URLUtil.isNetworkUrl(url)) {//network
                Glide.with(context).load(url).listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        if (callback != null) {
                            callback.onLoadResult(null);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        if (callback != null) {
                            callback.onLoadResult(resource);
                        }
                        return false;
                    }
                }).preload();
            } else {//local
                if (callback != null && finder != null) {
                    callback.onLoadResult(finder.findDrawable(url));
                }
            }
        }
    }

    /**
     * 获取多个图片，并在全部完成的时候回调
     *
     * @param context
     * @param urls
     * @param callback
     */
    public static void fetch(final Context context, final LuaResourceFinder finder, final String[] urls, final LoadCallback callback) {
        if (context != null && urls != null && urls.length > 0) {
            final AtomicInteger count = new AtomicInteger(urls.length);
            final Map<String, Drawable> result = new HashMap<String, Drawable>();
            for (final String url : urls) {
                if (URLUtil.isNetworkUrl(url)) {//network
                    Glide.with(context).load(url).listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            result.put(url, null);
                            callCallback(count, callback, result);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            result.put(url, resource);
                            callCallback(count, callback, result);
                            return false;
                        }
                    }).preload();
                } else {//TODO 优化成异步
                    if (finder != null) {
                        result.put(url, finder.findDrawable(url));
                        callCallback(count, callback, result);
                    }
                }
            }
        }
    }

    /**
     * 加载本地
     *
     * @param count
     * @param callback
     * @param result
     */
    private static void callCallback(final AtomicInteger count, final LoadCallback callback, final Map<String, Drawable> result) {
        synchronized (count) {
            if (count.decrementAndGet() <= 0) {//全部下载完毕的时候回调
                if (callback != null) {
                    callback.onLoadResult(result);
                }
            }
        }
    }
}
