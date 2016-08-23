package com.taobao.luaview.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * App级别的引用cache，不会在退出的时候清空
 *
 * @author song
 * @date 16/1/27
 */
public class AppCache {
    //全局静态cache
    private static Map<String, AppCache> mCachePool;

    private Map<Object, Object> mCache;

    private AppCache() {
        mCache = new HashMap<Object, Object>();
    }

    /**
     * get a named cache
     *
     * @param cacheName
     * @return
     */
    public static AppCache getCache(String cacheName) {
        if (mCachePool == null) {
            mCachePool = new HashMap<String, AppCache>();
        }
        if (!mCachePool.containsKey(cacheName)) {
            final AppCache weakReferenceCache = new AppCache();
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
     * get cache
     * @return
     */
    public Map<Object, Object> get(){
        return mCache;
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
