/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.base;

import com.taobao.luaview.cache.LuaCache;
import com.taobao.luaview.global.LuaView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * Cacheable UserData
 *
 * @author song
 * @date 16/1/21
 * 下午5:14 song XXX
 */
public abstract class BaseCacheUserdata extends BaseUserdata implements LuaCache.CacheableObject {

    public BaseCacheUserdata(Globals globals, LuaValue metatable) {
        super(globals, metatable);
        cacheObject();
    }

    public BaseCacheUserdata(Globals globals, LuaValue metatable, Varargs varargs) {
        super(globals, metatable, varargs);
        cacheObject();
    }

    public BaseCacheUserdata(Object obj, Globals globals, LuaValue metatable) {
        super(obj, globals, metatable);
        cacheObject();
    }

    public BaseCacheUserdata(Object obj, Globals globals, LuaValue metatable, Varargs varargs) {
        super(obj, globals, metatable, varargs);
        cacheObject();
    }

    private void cacheObject() {
        final Globals globals = getGlobals();
        final LuaCache luaCache = globals != null ? globals.getLuaCache() : null;
        if(luaCache != null){
            luaCache.cacheObject(getClass(), this);
        }
    }

}
