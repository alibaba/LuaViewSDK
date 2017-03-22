/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.vm.extend.luadc;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Prototype;

import java.io.IOException;

/**
 * Lua Dex Compiler
 * 功能同LuaJC，只不过处理的是Android的dex文件
 *
 * @author song
 * @date 16/7/6
 * 主要功能描述
 * 修改描述
 */
public class LuaDC implements Globals.Loader{

    public static final LuaDC instance = new LuaDC();

    public LuaDC(){}

    /**
     * Install the compiler as the main Globals.Loader to use in a set of globals.
     * Will fall back to the LuaC prototype compiler.
     */
    public static final void install(Globals G) {
        G.loader = instance;
    }


    @Override
    public LuaFunction load(Prototype prototype, String chunkname, LuaValue env) throws IOException {
        //TODO
        return null;
    }
}
