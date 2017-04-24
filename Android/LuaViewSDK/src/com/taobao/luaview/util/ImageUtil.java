/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.webkit.URLUtil;
import android.widget.ImageView;

import com.taobao.luaview.global.LuaResourceFinder;
import com.taobao.luaview.global.LuaView;
import com.taobao.luaview.provider.ImageProvider;
import com.taobao.luaview.view.imageview.BaseImageView;
import com.taobao.luaview.view.imageview.DrawableLoadCallback;

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
            final int width = (imageView.getDrawable()).getIntrinsicWidth();
            final int height = (imageView.getDrawable()).getIntrinsicHeight();
            if (width != imageView.getLayoutParams().width || height != imageView.getLayoutParams().height) {
                imageView.getLayoutParams().width = width;
                imageView.getLayoutParams().height = height;
                imageView.requestLayout();
            }
        }
    }

    /**
     * 获取一个图片，并调用回调
     *
     * @param context
     * @param url
     * @param callback
     */
    public static void fetch(Context context, final LuaResourceFinder finder, final String url, final DrawableLoadCallback callback) {
        if (context != null && !TextUtils.isEmpty(url)) {
            if (URLUtil.isNetworkUrl(url)) {//network
                final ImageProvider provider = LuaView.getImageProvider();
                if (provider != null) {
                    provider.preload(context, url, callback);
                }
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
                    final ImageProvider imageProvider = LuaView.getImageProvider();
                    if (imageProvider != null) {
                        imageProvider.preload(context, url, new DrawableLoadCallback() {
                            @Override
                            public void onLoadResult(Drawable drawable) {
                                result.put(url, drawable);
                                callCallback(count, callback, result);
                            }
                        });
                    }
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
