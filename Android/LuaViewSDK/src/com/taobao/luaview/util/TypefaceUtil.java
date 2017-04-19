/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.util;

import android.content.Context;
import android.graphics.Typeface;

import com.taobao.luaview.cache.SimpleCache;

/**
 * 字体处理，字体使用SimpleCache，全局缓存
 *
 * @author song
 * @date 15/11/6
 */
public class TypefaceUtil {
    private static final String TAG = "TypefaceUtil";
    private static final String TAG_TYPEFACE_NAME = "TypefaceUtil_NAME";

    /**
     * 未知
     *
     * @param typeface
     * @return
     */
    public static String getTypefaceName(Typeface typeface) {
        final String name = SimpleCache.getCache(TAG_TYPEFACE_NAME).get(typeface);
        return name != null ? name : "unknown";
    }

    /**
     * create typeface
     *
     * @param context
     * @param name
     * @return
     */
    public static Typeface create(final Context context, final String name) {
        Typeface result = SimpleCache.getCache(TAG).get(name);
        if (result == null) {
            final String fontNameOrAssetPathOrFilePath = ParamUtil.getFileNameWithPostfix(name, "ttf");
            result = createFromAsset(context, fontNameOrAssetPathOrFilePath);
            if (result == null) {
                result = createFromFile(fontNameOrAssetPathOrFilePath);
            }
            if (result == null) {
                result = createByName(fontNameOrAssetPathOrFilePath);
            }
        }
        SimpleCache.getCache(TAG_TYPEFACE_NAME).put(result, name);//cache name
        return SimpleCache.getCache(TAG).put(name, result);
    }

    /**
     * create a typeface
     *
     * @param name
     * @return
     */
    public static Typeface create(final String name) {
        Typeface result = SimpleCache.getCache(TAG).get(name);
        if (result == null) {
            final String fontNameOrFilePath = ParamUtil.getFileNameWithPostfix(name, "ttf");
            result = createFromFile(fontNameOrFilePath);
            if (result == null) {
                result = createByName(fontNameOrFilePath);
            }
        }
        SimpleCache.getCache(TAG_TYPEFACE_NAME).put(result, name);//cache name
        return SimpleCache.getCache(TAG).put(name, result);
    }

    /**
     * create typeface by name or path
     *
     * @param fontName
     * @return
     */
    private static Typeface createByName(final String fontName) {
        try {
            final Typeface typeface = Typeface.create(fontName, Typeface.BOLD_ITALIC);
            if (typeface != null && Typeface.BOLD_ITALIC == typeface.getStyle()) {//得到的是默认字体则返回null
                return null;
            }
            return typeface;
        } catch (Exception e) {
            LogUtil.e("create typeface " + fontName + " by name failed", e);
            return null;
        }
    }

    /**
     * create typeface from asset
     *
     * @param context
     * @param assetPath
     * @return
     */
    private static Typeface createFromAsset(final Context context, final String assetPath) {
        try {
            return Typeface.createFromAsset(context.getAssets(), assetPath);
        } catch (Exception e) {
            LogUtil.e("create typeface " + assetPath + " from asset failed", e);
            return null;
        }
    }

    /**
     * create typeface from file path
     *
     * @param filePath
     * @return
     */
    private static Typeface createFromFile(final String filePath) {
        try {
            return Typeface.createFromFile(filePath);
        } catch (Exception e) {
            LogUtil.e("create typeface " + filePath + " from file failed", e);
            return null;
        }
    }

}
