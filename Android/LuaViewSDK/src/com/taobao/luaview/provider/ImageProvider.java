/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.provider;

import android.content.Context;
import android.view.ViewGroup;

import com.taobao.luaview.view.imageview.BaseImageView;
import com.taobao.luaview.view.imageview.BitmapLoadCallback;
import com.taobao.luaview.view.imageview.DrawableLoadCallback;

import java.lang.ref.WeakReference;

/**
 * 提供图片下载功能，用作ImageView相关
 *
 * @author song
 * @date 16/4/11
 * 主要功能描述
 * 修改描述
 * 下午4:48 song XXX
 */
public interface ImageProvider {
    /**
     * 下载图片
     *
     * @param imageView
     * @param url
     * @param callback
     */
    void load(final Context context, final WeakReference<BaseImageView> imageView, final String url, final WeakReference<DrawableLoadCallback> callback);

    /**
     * load a bitmap
     *
     * @param context
     * @param url
     * @param callback
     */
//    void loadBitmap(final Context context, final String url, final WeakReference<BitmapLoadCallback> callback);

    /**
     * 预下载图片
     *
     * @param context
     * @param url
     * @param callback
     */
    void preload(final Context context, final String url, final DrawableLoadCallback callback);

    /**
     * pause all requests
     *
     * @param context
     */
    void pauseRequests(final ViewGroup view, final Context context);

    /**
     * resume all requests
     *
     * @param context
     */
    void resumeRequests(final ViewGroup view, final Context context);


}
