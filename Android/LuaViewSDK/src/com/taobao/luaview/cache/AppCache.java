/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.cache;

import android.app.Application;
import android.support.v4.util.LruCache;

import java.util.HashMap;
import java.util.Map;

/**
 * App级别的引用cache，不会在退出的时候清空
 *
 * @author song
 * @date 16/1/27
 */
public class AppCache {
    public static final int DEFAULT_LRU_CACHE_PROTOTYPE_SIZE = (int) (1.5 * 1024 * 1024);//1.5M
    public static final int DEFAULT_LRU_CACHE_SIZE = 5;//5个
    public static final String CACHE_METHODS = "cache_methods";//方法名称缓存
    public static final String CACHE_PUBLIC_KEY = "cache_public_key";//公钥
    public static final String CACHE_METATABLES = "cache_metatables";//metatable缓存
    public static final String CACHE_SCRIPTS = "cache_scripts";//脚本文件缓存，占用空间2%
    public static final String CACHE_PROTOTYPE = "cache_prototype";//lua中间码缓存，占用空间3%

    //全局静态cache
    private static Map<String, AppCache> mCachePool;

    //simple cache
    private Map<Object, Object> mCache;

    //lru cache
    private LuaLruCache mLruCache;


    /**
     * on received memory warning
     *
     * @param level
     */
    public static void onTrimMemory(int level) {
        switch (level) {
            case Application.TRIM_MEMORY_COMPLETE:// = 80; clear all
                clear();
                break;
            case Application.TRIM_MEMORY_MODERATE:// = 60;
                clear(CACHE_PROTOTYPE, CACHE_SCRIPTS);
                break;
            case Application.TRIM_MEMORY_BACKGROUND:// = 40;
                clear(CACHE_SCRIPTS);
                break;
            case Application.TRIM_MEMORY_UI_HIDDEN:// = 20;
                clear(CACHE_SCRIPTS);
                break;
            case Application.TRIM_MEMORY_RUNNING_CRITICAL:// = 15;
                break;
            case Application.TRIM_MEMORY_RUNNING_LOW:// = 10;
                break;
            case Application.TRIM_MEMORY_RUNNING_MODERATE:// = 5;
                break;
        }
    }

    private AppCache() {
        this(DEFAULT_LRU_CACHE_SIZE);
    }

    private AppCache(int size) {
        mCache = new HashMap<Object, Object>();
        if (size > 0) {
            mLruCache = new LuaLruCache(size);
        }
    }

    /**
     * get a named cache
     *
     * @return
     */
    public static AppCache getPrototpyeCache() {
        return getCache(CACHE_PROTOTYPE, DEFAULT_LRU_CACHE_PROTOTYPE_SIZE);
    }

    public static AppCache getCache(String cacheName) {
        return getCache(cacheName, DEFAULT_LRU_CACHE_SIZE);
    }

    public static AppCache getCache(String cacheName, int size) {
        if (mCachePool == null) {
            mCachePool = new HashMap<String, AppCache>();
        }
        if (!mCachePool.containsKey(cacheName)) {
            final AppCache appCache = new AppCache(size);
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
            AppCache appCache = null;
            for (String key : keys) {
                if (mCachePool.containsKey(key)) {
                    appCache = mCachePool.remove(key);
                    if (appCache != null) {
                        if (appCache.mCache != null) {
                            appCache.mCache.clear();
                        }
                        if (appCache.mLruCache != null) {
                            appCache.mLruCache.evictAll();
                        }
                    }
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
     * get from cache
     *
     * @param key
     * @param <T>
     * @return
     */
    public <T> T getLru(final Object key) {
        if (mLruCache != null && mLruCache.get(key) != null) {
            return (T) mLruCache.getWrap(key);
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

    /**
     * update cache
     *
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    public <T> T putLru(final Object key, T value, Integer size) {
        if (mLruCache != null) {
            mLruCache.putWrap(key, value, size);
        }
        return value;
    }

    public <T> T putLru(final Object key, T value) {
        if (mLruCache != null) {
            mLruCache.putWrap(key, value, null);
        }
        return value;
    }

    //----------------------------------------extend lru object-------------------------------------
    static class LuaLruCache extends LruCache<Object, Object> {
        public LuaLruCache(int maxSize) {
            super(maxSize);
        }

        public void putWrap(Object key, Object value, Integer size) {
            if (key != null && value != null) {
                if (size != null) {
                    super.put(key, new WrapLruObject(value, size));
                } else {
                    super.put(key, value);
                }
            }
        }

        public Object getWrap(Object key) {
            if (key != null) {
                Object result = super.get(key);
                if (result instanceof WrapLruObject) {
                    return ((WrapLruObject) result).obj;
                } else {
                    return result;
                }
            } else {
                return null;
            }
        }

        @Override
        protected int sizeOf(Object key, Object value) {
            if (value instanceof WrapLruObject) {
                return ((WrapLruObject) value).size;
            }
            return super.sizeOf(key, value);
        }
    }

    static class WrapLruObject extends Object {
        Object obj;
        int size;

        WrapLruObject(Object obj, int size) {
            this.obj = obj;
            this.size = size;
        }
    }
}
