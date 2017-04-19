/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.cache;

import android.util.SparseArray;

/**
 * Cache using sparse array
 *
 * @author song
 * @date 16/2/23
 */
public class SparseCache<T extends Object> {

    private SparseArray<T> mCache;

    public SparseCache() {
        mCache = new SparseArray<T>();
    }

    public boolean contains(int key) {
        return mCache != null && mCache.get(key) != null;
    }

    public T get(final int key) {
        if (mCache != null) {
            return mCache.get(key);
        }
        return null;
    }

    public T get(final int key, T defaultValueIfNotFound) {
        if (mCache != null) {
            return mCache.get(key, defaultValueIfNotFound);
        }
        return null;
    }

    public void put(final int key, T value) {
        if (mCache != null) {
            mCache.put(key, value);
        }
    }

}
