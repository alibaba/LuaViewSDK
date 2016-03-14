package com.taobao.luaview.extend;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Weak Reference Cache
 *
 * @author song
 * @date 16/1/27
 */
public class WeakCache {
    //全局静态cache
    private static Map<String, WeakCache> mCachePool;

    private Map<Object, WeakReference<Object>> mCache;

    private WeakCache() {
        mCache = new HashMap<Object, WeakReference<Object>>();
    }

    /**
     * get a named cache
     * @param cacheName
     * @return
     */
    public static WeakCache getCache(String cacheName) {
        if (mCachePool == null) {
            mCachePool = new HashMap<String, WeakCache>();
        }
        if (!mCachePool.containsKey(cacheName)) {
            final WeakCache weakReferenceCache = new WeakCache();
            mCachePool.put(cacheName, weakReferenceCache);
            return weakReferenceCache;
        }
        return mCachePool.get(cacheName);
    }

    /**
     * should call when LuaView is destroy
     */
    public static void clear(){
        if(mCachePool != null){
            mCachePool.clear();
        }
    }

    /**
     * get from cache
     * @param key
     * @param <T>
     * @return
     */
    public <T> T get(final Object key) {
        if (mCache != null && mCache.get(key) != null && mCache.get(key).get() != null) {
            return (T) mCache.get(key).get();
        }
        return null;
    }

    /**
     * update cache
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    public <T> T put(final Object key, T value) {
        if (mCache != null) {
            mCache.put(key, new WeakReference<Object>(value));
        }
        return value;
    }
}
