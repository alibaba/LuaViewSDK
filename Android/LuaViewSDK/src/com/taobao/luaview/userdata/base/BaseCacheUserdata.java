package com.taobao.luaview.userdata.base;

import com.taobao.luaview.extend.LuaCache;

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
        if (getGlobals() != null && getGlobals().luaView != null) {
            getGlobals().luaView.cacheObject(getClass(), this);
        }
    }

}
