/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

import com.taobao.luaview.cache.WeakCache;

/**
 * drawable相关的util
 *
 * @author song
 * @date 15/9/9
 */
public class DrawableUtil {
    private static final String TAG = "DrawableUtil";

    /**
     * get drawable by path
     *
     * @param filePath
     * @return
     */
    public static Drawable getByPath(final String filePath) {
        Drawable drawable = WeakCache.getCache(TAG).get(filePath);
        if (drawable == null) {
            try {
                drawable = Drawable.createFromPath(filePath);
                WeakCache.getCache(TAG).put(filePath, drawable);
            } catch (Throwable e){
                LogUtil.e("[DrawableUtil-getByPath Failed]", e);
            }
        }
        return drawable;
    }

    /**
     * 从Asset路径获取Drawable
     *
     * @param context
     * @param filePath
     * @return
     */
    public static Drawable getAssetByPath(final Context context, final String filePath) {
        Drawable drawable = WeakCache.getCache(TAG).get(filePath);
        if (drawable == null) {
            try {
                if (context != null) {
                    drawable = Drawable.createFromStream(context.getAssets().open(filePath), null);
                    WeakCache.getCache(TAG).put(filePath, drawable);
                }
            } catch (Throwable e){
                LogUtil.e("[DrawableUtil-getAssetByPath Failed]", e);
            }
        }
        return drawable;
    }

    /**
     * 根据名字获取drawable
     *
     * @param context
     * @param name
     * @return
     */
    public static Drawable getByName(final Context context, String name) {
        Drawable drawable = WeakCache.getCache(TAG).get(name);
        if (drawable == null) {
            if (context != null && name != null) {
                final Resources resources = context.getResources();
                final int resourceId = resources.getIdentifier(ParamUtil.getFileNameWithoutPostfix(name), "drawable", context.getPackageName());
                try {
                    drawable = resources.getDrawable(resourceId);
                    WeakCache.getCache(TAG).put(name, drawable);
                } catch (Throwable e){
                    LogUtil.e("[DrawableUtil-getByName Failed]", e);
                }
            }
        }
        return drawable;
    }

    /**
     * 根据名字获取drawable 的id
     *
     * @param context
     * @param name
     * @return
     */
    public static int getIdByName(final Context context, final String name) {
        if (context != null && name != null) {
            final Resources resources = context.getResources();
            return resources.getIdentifier(ParamUtil.getFileNameWithoutPostfix(name), "drawable", context.getPackageName());
        }
        return 0;
    }

    /**
     * create state list drawable
     *
     * @param context
     * @param idNormal
     * @param idPressed
     * @param idFocused
     * @param idUnable
     * @return
     */
    public static StateListDrawable createStateListDrawable(Context context, int idNormal, int idPressed, int idFocused, int idUnable) {
        StateListDrawable bg = new StateListDrawable();
        Drawable normal = idNormal == -1 ? null : context.getResources().getDrawable(idNormal);
        Drawable pressed = idPressed == -1 ? null : context.getResources().getDrawable(idPressed);
        Drawable focused = idFocused == -1 ? null : context.getResources().getDrawable(idFocused);
        Drawable unable = idUnable == -1 ? null : context.getResources().getDrawable(idUnable);
        // View.PRESSED_ENABLED_STATE_SET
        bg.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, pressed);
        // View.ENABLED_FOCUSED_STATE_SET
        bg.addState(new int[]{android.R.attr.state_enabled, android.R.attr.state_focused}, focused);
        // View.ENABLED_STATE_SET
        bg.addState(new int[]{android.R.attr.state_enabled}, normal);
        // View.FOCUSED_STATE_SET
        bg.addState(new int[]{android.R.attr.state_focused}, focused);
        // View.WINDOW_FOCUSED_STATE_SET
        bg.addState(new int[]{android.R.attr.state_window_focused}, unable);
        // View.EMPTY_STATE_SET
        bg.addState(new int[]{}, normal);
        return bg;
    }
}
