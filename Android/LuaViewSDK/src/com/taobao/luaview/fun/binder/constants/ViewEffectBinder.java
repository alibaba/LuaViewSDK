/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.fun.binder.constants;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.userdata.constants.UDViewEffect;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.LibFunction;

/**
 * View Effect
 *
 * @author song
 * @date 16/8/15
 * 主要功能描述
 * 修改描述
 * 下午4:03 song XXX
 */
public class ViewEffectBinder extends BaseFunctionBinder {

    public ViewEffectBinder() {
        super("ViewEffect");
    }

    @Override
    public Class<? extends LibFunction> getMapperClass() {
        return null;
    }

    @Override
    public LuaValue createCreator(LuaValue env, final LuaValue metaTable) {
        return new UDViewEffect(env.checkglobals(), metaTable);
    }
}
