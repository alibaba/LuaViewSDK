package com.taobao.luaview.provider;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.taobao.luaview.view.imageview.BaseImageView;

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
     * @param imageView
     * @param url
     * @param callback
     */
    void load(final ImageView imageView, final String url, final BaseImageView.LoadCallback callback);

    /**
     * 预下载图片
     * @param context
     * @param url
     * @param callback
     */
    void preload(final Context context, final String url, final BaseImageView.LoadCallback callback);

    /**
     * pause all requests
     * @param context
     */
    void pauseRequests(final ViewGroup view, final Context context);

    /**
     * resume all requests
     * @param context
     */
    void resumeRequests(final ViewGroup view, final Context context);
}
