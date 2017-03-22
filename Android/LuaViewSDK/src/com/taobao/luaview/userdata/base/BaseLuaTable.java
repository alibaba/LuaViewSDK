/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.base;

import android.content.Context;

import com.taobao.luaview.global.LuaResourceFinder;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.io.Serializable;

/**
 * 基础Table
 *
 * @author song
 * @date 15/8/21
 */
public class BaseLuaTable extends LuaTable implements Serializable {
    private Globals mGlobals;
    private LuaValue mMetatable;
    public Varargs mVarargs;

    public BaseLuaTable(Globals globals, LuaValue metatable) {
        this(globals, metatable, NIL);
    }

    public BaseLuaTable(Globals globals, LuaValue metatable, Varargs varargs) {
        this.mGlobals = globals;
        this.mMetatable = metatable;
        this.mVarargs = varargs;
    }

    public Globals getGlobals() {
        return mGlobals;
    }

    public LuaResourceFinder getLuaResourceFinder() {
        return mGlobals != null ? mGlobals.getLuaResourceFinder() : null;
    }

    public Context getContext() {
        return getGlobals() != null ? getGlobals().getContext() : null;
    }
}
