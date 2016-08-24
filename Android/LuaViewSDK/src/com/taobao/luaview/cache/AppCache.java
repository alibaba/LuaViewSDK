package com.taobao.luaview.cache;

import android.app.Application;

import java.util.HashMap;
import java.util.Map;

/**
 * App级别的引用cache，不会在退出的时候清空
 *
 * @author song
 * @date 16/1/27
 */
public class AppCache {
    public static final String CACHE_METHODS = "cache_methods";//方法名称缓存
    public static final String CACHE_PUBLIC_KEY = "cache_public_key";//公钥
    public static final String CACHE_METATABLES = "cache_metatables";//metatable缓存
    public static final String CACHE_SCRIPTS = "cache_scripts";//脚本文件缓存
    public static final String CACHE_PROTOTYPE = "cache_prototype";//lua中间码缓存

    //全局静态cache
    private static Map<String, AppCache> mCachePool;

    private Map<Object, Object> mCache;


    /**
     * on received memory warning
     * @param level
     */
    public static void onMemoryWarning(int level) {
        switch (level) {
            case Application.TRIM_MEMORY_COMPLETE:// = 80; clear all
                clear();
                break;
            case Application.TRIM_MEMORY_MODERATE:// = 60;
                clear(CACHE_PROTOTYPE, CACHE_SCRIPTS, CACHE_METATABLES, CACHE_PUBLIC_KEY);
                break;
            case Application.TRIM_MEMORY_BACKGROUND:// = 40;
                clear(CACHE_SCRIPTS, CACHE_METATABLES, CACHE_PUBLIC_KEY);
                break;
            case Application.TRIM_MEMORY_UI_HIDDEN:// = 20;
                clear(CACHE_SCRIPTS, CACHE_METATABLES, CACHE_PUBLIC_KEY);
                break;
            case Application.TRIM_MEMORY_RUNNING_CRITICAL:// = 15;
                clear(CACHE_METATABLES, CACHE_PUBLIC_KEY);
                break;
            case Application.TRIM_MEMORY_RUNNING_LOW:// = 10;
                clear(CACHE_METATABLES);
                break;
            case Application.TRIM_MEMORY_RUNNING_MODERATE:// = 5;
                clear(CACHE_PUBLIC_KEY);
                break;
        }
    }


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
            final AppCache appCache = new AppCache();
            mCachePool.put(cacheName, appCache);
            return appCache;
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
     * clear certain cache
     *
     * @param keys
     */
    public static void clear(String... keys) {
        if (mCachePool != null && keys != null) {
            for (String key : keys) {
                if (mCachePool.containsKey(key)) {
                    mCachePool.remove(key);
                }
            }
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
