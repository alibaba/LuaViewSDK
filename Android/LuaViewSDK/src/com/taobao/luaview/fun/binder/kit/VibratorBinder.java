/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.fun.binder.kit;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.fun.base.BaseVarArgCreator;
import com.taobao.luaview.fun.mapper.kit.VibratorMethodMapper;
import com.taobao.luaview.userdata.kit.UDVibrator;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.LibFunction;

/**
 * Vibrator 接口封装
 *
 * @author song
 * @date 15/8/21
 */
public class VibratorBinder extends BaseFunctionBinder {

    public VibratorBinder() {
        super("Vibrator");
    }

    @Override
    public Class<? extends LibFunction> getMapperClass() {
        return VibratorMethodMapper.class;
    }

    @Override
    public LuaValue createCreator(LuaValue env, final LuaValue metaTable) {
        return new BaseVarArgCreator(env.checkglobals(), metaTable, getMapperClass()) {
            @Override
            public LuaValue createUserdata(Globals globals, LuaValue metaTable, Varargs varargs) {
                return new UDVibrator(globals, metaTable, varargs);
            }
        };
    }
}
