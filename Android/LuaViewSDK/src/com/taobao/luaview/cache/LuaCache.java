/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.cache;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 所有LuaView级别的cache管理
 *
 * @author song
 * @date 16/4/11
 * 主要功能描述
 * 修改描述
 * 上午11:32 song XXX
 */
public class LuaCache {
    //缓存的数据，需要在退出的时候清空
    private Map<Class, List<WeakReference<CacheableObject>>> mCachedObjects;

    //缓存数据管理器
    public interface CacheableObject {
        void onCacheClear();
    }

    /**
     * clear cache
     */
    public static void clear() {
        SimpleCache.clear();
        WeakCache.clear();
    }

    /**
     * 缓存对象
     *
     * @param type
     * @param obj
     */
    public void cacheObject(Class type, CacheableObject obj) {
        if (mCachedObjects == null) {
            mCachedObjects = new HashMap<Class, List<WeakReference<CacheableObject>>>();
        }
        List<WeakReference<CacheableObject>> cache = mCachedObjects.get(type);
        if (cache == null) {
            cache = new ArrayList<WeakReference<CacheableObject>>();
            mCachedObjects.put(type, cache);
        }

        if (!cache.contains(obj)) {
            cache.add(new WeakReference<CacheableObject>(obj));
        }
    }

    /**
     * 清理所有缓存的对象
     * TODO 需要在onShow的时候恢复所有cache后的对象
     */
    public void clearCachedObjects() {
        if (mCachedObjects != null && mCachedObjects.size() > 0) {
            for (final Class type : mCachedObjects.keySet()) {
                List<WeakReference<CacheableObject>> cache = mCachedObjects.get(type);
                if (cache != null) {
                    for (int i = 0; i < cache.size(); i++) {
                        final WeakReference<CacheableObject> obj = cache.get(i);
                        if (obj != null && obj.get() != null) {
                            obj.get().onCacheClear();
                        }
                        cache.set(i, null);
                    }
                }
                mCachedObjects.put(type, null);
            }
            mCachedObjects.clear();
        }
        mCachedObjects = null;
    }
}
