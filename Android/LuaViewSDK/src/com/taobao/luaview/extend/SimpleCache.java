package com.taobao.luaview.extend;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple Reference Cache
 *
 * @author song
 * @date 16/1/27
 */
public class SimpleCache {
    //全局静态cache
    private static Map<String, SimpleCache> mCachePool;

    private Map<Object, Object> mCache;

    private SimpleCache() {
        mCache = new HashMap<Object, Object>();
    }

    /**
     * get a named cache
     *
     * @param cacheName
     * @return
     */
    public static SimpleCache getCache(String cacheName) {
        if (mCachePool == null) {
            mCachePool = new HashMap<String, SimpleCache>();
        }
        if (!mCachePool.containsKey(cacheName)) {
            final SimpleCache weakReferenceCache = new SimpleCache();
            mCachePool.put(cacheName, weakReferenceCache);
            return weakReferenceCache;
        }
        return mCachePool.get(cacheName);
    }

    /**
     * should call when LuaView is destroy
     */
    public static void clear() {
        if (mCachePool != null) {
            mCachePool.clear();
        }
    }

    /**
     * get from cache
     *
     * @param key
     * @param <T>
     * @return
     */
    public <T> T get(final Object key) {
        if (mCache != null && mCache.get(key) != null) {
            return (T) mCache.get(key);
        }
        return null;
    }

    /**
     * update cache
     *
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    public <T> T put(final Object key, T value) {
        if (mCache != null) {
            mCache.put(key, value);
        }
        return value;
    }
}
