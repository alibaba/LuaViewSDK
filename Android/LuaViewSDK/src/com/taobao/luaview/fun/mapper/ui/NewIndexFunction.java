/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.fun.mapper.ui;

import com.taobao.luaview.fun.base.BaseMethodMapper;
import com.taobao.luaview.util.LogUtil;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * 属性赋值操作
 * @author song
 */
public class NewIndexFunction extends BaseMethodMapper {

    private LuaValue metatable;

    public NewIndexFunction(LuaValue metatable) {
        this.metatable = metatable;
    }

    @Override
    public Varargs invoke(Varargs args) {
        LuaValue key = args.arg(2);
        Varargs newargs = varargsOf(args.arg(1), args.arg(3));
        LuaValue func = metatable.get(key);
        if (func.isfunction()) {//函数调用
            func.invoke(newargs);
        } else {
            LogUtil.d("[LuaView error]", "property not fount :", key.toString());
        }
        return NONE;
    }

}
