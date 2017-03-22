/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.fun.mapper.ui;

import com.taobao.luaview.fun.mapper.LuaViewApi;
import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.global.VmVersion;
import com.taobao.luaview.userdata.ui.UDCustomView;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.List;

/**
 * CustomView 接口封装
 * 有onDraw方法处理
 *
 * @param <U>
 * @author song
 */
@LuaViewLib(revisions = {"20170306已对标"})
public class UICustomViewMethodMapper<U extends UDCustomView> extends UIViewMethodMapper<U> {

    private static final String TAG = "UICustomViewMethodMapper";
    private static final String[] sMethods = new String[]{
            "onDraw"//0
    };

    @Override
    public List<String> getAllFunctionNames() {
        return mergeFunctionNames(TAG, super.getAllFunctionNames(), sMethods);
    }

    @Override
    public Varargs invoke(int code, U target, Varargs varargs) {
        final int optcode = code - super.getAllFunctionNames().size();
        switch (optcode) {
            case 0:
                return onDraw(target, varargs);
        }
        return super.invoke(code, target, varargs);
    }

    //--------------------------------------- API --------------------------------------------------

    /**
     * onDraw
     *
     * @param view
     * @param varargs
     * @return
     */
    @LuaViewApi(since = VmVersion.V_550)
    public LuaValue onDraw(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setOnDraw(view, varargs);
        } else {
            return getOnDraw(view, varargs);
        }
    }

    public LuaValue setOnDraw(U view, Varargs varargs) {
        final LuaValue callbacks = varargs.optvalue(2, NIL);
        return view.setOnDrawCallback(callbacks);
    }

    public LuaValue getOnDraw(U view, Varargs varargs) {
        return view.getOnDrawCallback();
    }

}