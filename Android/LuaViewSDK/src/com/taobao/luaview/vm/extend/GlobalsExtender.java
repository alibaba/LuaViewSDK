/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.vm.extend;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Prototype;

import java.util.HashMap;
import java.util.Map;

/**
 * 对Globals进行扩展
 *
 * @author song
 * @date 16/6/16
 * 主要功能描述
 * 修改描述
 * 下午4:56 song XXX
 */
public class GlobalsExtender {
    //延迟加载的libs
    private Map<String, LuaValue> mLazyLoadLibs;
    private long time = 0;

    public GlobalsExtender() {
        mLazyLoadLibs = new HashMap<String, LuaValue>();
    }

    /**
     * 延迟加载库
     *
     * @param binder
     */
    public void lazyLoad(final LuaValue binder) {
        if (mLazyLoadLibs != null && binder instanceof BaseFunctionBinder) {
            String[] names = ((BaseFunctionBinder) binder).getLuaNames();
            if (names != null) {
                for (String name : names) {
                    mLazyLoadLibs.put(name, binder);
                }
            }
        }
    }

    /**
     * 真正地加载一个库
     *
     * @param globals
     * @param p
     */
    public boolean doLoad(final Globals globals, Prototype p) {
        boolean isAnyLoaded = false;
        if (p != null && p.k != null && p.k.length > 0) {
            StringBuffer sb = new StringBuffer();
            for (LuaValue name : p.k) {
                if (LuaUtil.isString(name)) {
                    sb.append(name).append(" ");
                    isAnyLoaded = (doLoad(globals, name.checkjstring()) != null) || isAnyLoaded;
                }
            }
        }
        return isAnyLoaded;
    }

    /**
     * 真正加载一个库
     *
     * @param globals
     * @param name
     * @return
     */
    public LuaValue doLoad(final Globals globals, final String name) {
        if (mLazyLoadLibs != null && mLazyLoadLibs.containsKey(name)) {
            final LuaValue lib = mLazyLoadLibs.get(name);
            LuaValue result = globals.load(lib);
            mLazyLoadLibs.remove(name);
            return result;
        }
        return null;
    }
}
