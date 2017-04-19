/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.fun.base;

import android.view.View;

import com.taobao.luaview.global.LuaViewConfig;
import com.taobao.luaview.global.LuaViewManager;
import com.taobao.luaview.util.LuaViewUtil;
import com.taobao.luaview.view.interfaces.ILVView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.LibFunction;
import org.luaj.vm2.lib.VarArgFunction;

/**
 * 零参数函数
 *
 * @author song
 * @date 15/8/14
 */
public abstract class BaseVarArgUICreator extends VarArgFunction {
    public Globals globals;
    public LuaValue metatable;
    public Class<? extends LibFunction> libClass;

    public BaseVarArgUICreator(Globals globals, LuaValue metatable) {
        this(globals, metatable, null);
    }

    public BaseVarArgUICreator(Globals globals, LuaValue metatable, Class<? extends LibFunction> libClass) {
        this.globals = globals;
        this.metatable = metatable;
        this.libClass = libClass;
    }

    public Varargs invoke(Varargs args) {
        if (LuaViewConfig.isLibsLazyLoad()) {
            metatable = LuaViewManager.createMetatable(libClass);
        }

        ILVView view = createView(globals, metatable, args);
        if (globals.container != null && view instanceof View && ((View) view).getParent() == null) {
            LuaViewUtil.addView(globals.container, (View) view, args);
        }
        return view.getUserdata();
    }

    public abstract ILVView createView(Globals globals, LuaValue metaTable, Varargs varargs);
}
