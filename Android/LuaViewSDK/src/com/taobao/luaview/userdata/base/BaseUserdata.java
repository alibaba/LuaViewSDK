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
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.io.Serializable;
import java.lang.ref.WeakReference;

/**
 * 基础用户数据
 *
 * @author song
 * @date 15/8/21
 */
public class BaseUserdata extends LuaUserdata implements Serializable {
    private Globals mGlobals;
    public Varargs initParams = LuaValue.NIL;

    public BaseUserdata(Globals globals, LuaValue metatable) {
        this(globals, metatable, NIL);
    }

    public BaseUserdata(Object obj, Globals globals, LuaValue metatable) {
        this(obj, globals, metatable, NIL);
    }

    public BaseUserdata(Globals globals, LuaValue metatable, Varargs varargs) {
        super(globals, metatable);
        this.initParams = varargs;
    }

    public BaseUserdata(Object obj, Globals globals, LuaValue metatable, Varargs varargs) {
        super(new WeakReference<Object>(obj), metatable);
        this.mGlobals = globals;
        this.initParams = varargs;
    }

    public LuaValue setInitParams(Varargs initParams) {
        this.initParams = initParams;
        return this;
    }

    public Varargs getInitParams() {
        return initParams;
    }

    public LuaValue getInitParam1() {
        return getInitParam1(LuaValue.NIL);
    }

    public LuaValue getInitParam2() {
        return getInitParam2(LuaValue.NIL);
    }

    public LuaValue getInitParam1(LuaValue defaultValue) {
        return getInitParam(1, defaultValue);
    }

    public LuaValue getInitParam2(LuaValue defaultValue) {
        return getInitParam(2, defaultValue);
    }

    public LuaValue getInitParam(int index, LuaValue defaultValue) {
        return initParams != null && initParams.narg() >= index ? initParams.arg(index) : defaultValue;
    }

    public LuaValue getInitParam1(Varargs varargs) {
        return getInitParam1(1, varargs, LuaValue.NIL);
    }

    public LuaValue getInitParam1(int index, Varargs varargs, LuaValue defaultValue) {
        return varargs != null && varargs.narg() >= index ? varargs.arg(index) : defaultValue;
    }

    public int getInitParamsCount() {
        return initParams != null ? initParams.narg() : 0;
    }

    @Override
    public Object userdata() {
        Object obj = super.userdata();
        if (obj instanceof WeakReference) {
            return ((WeakReference) obj).get();
        } else {
            return obj;
        }
    }

    public Globals getGlobals() {
        return userdata() instanceof Globals ? (Globals) userdata() : mGlobals;
    }

    public LuaResourceFinder getLuaResourceFinder() {
        return getGlobals() != null ? getGlobals().getLuaResourceFinder() : null;
    }

    public Context getContext() {
        return getGlobals() != null ? getGlobals().getContext() : null;
    }

    /**
     * 销毁的时候置空
     */
    public void onDestroy() {
        m_instance = null;
    }

    @Override
    public String tojstring() {
        return String.valueOf(userdata());
    }
}
