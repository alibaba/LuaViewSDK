package com.taobao.luaview.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.taobao.luaview.extend.WeakCache;

/**
 * drawable相关的util
 *
 * @author song
 * @date 15/9/9
 */
public class DrawableUtil {
    private static final String TAG = DrawableUtil.class.getSimpleName();

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
            } catch (Exception e) {
                LogUtil.e("[DrawableUtil-getByPath Failed]", e);
            }
        }
        return WeakCache.getCache(TAG).put(filePath, drawable);
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
                }
            } catch (Exception e) {
                LogUtil.e("[DrawableUtil-getAssetByPath Failed]", e);
            }
        }
        return WeakCache.getCache(TAG).put(filePath, drawable);
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
                drawable = resources.getDrawable(resourceId);
            }
        }
        return WeakCache.getCache(TAG).put(name, drawable);
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
}
