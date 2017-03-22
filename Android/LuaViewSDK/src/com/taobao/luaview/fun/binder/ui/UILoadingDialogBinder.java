/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.fun.binder.ui;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.fun.base.BaseVarArgCreator;
import com.taobao.luaview.fun.mapper.LuaViewApi;
import com.taobao.luaview.fun.mapper.ui.UILoadingDialogMethodMapper;
import com.taobao.luaview.userdata.ui.UDLoadingDialog;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.LibFunction;

/**
 * 菊花（对话框）
 * @author song
 */
@Deprecated
@LuaViewApi(revisions = {"iOS 无"})
public class UILoadingDialogBinder extends BaseFunctionBinder {

    public UILoadingDialogBinder() {
        super("LoadingDialog");
    }

    @Override
    public Class<? extends LibFunction> getMapperClass() {
        return UILoadingDialogMethodMapper.class;
    }

    @Override
    public LuaValue createCreator(LuaValue env, LuaValue metaTable) {
        return new BaseVarArgCreator(env.checkglobals(), metaTable, getMapperClass()) {
            @Override
            public LuaValue createUserdata(Globals globals, LuaValue metaTable, Varargs varargs) {
                return new UDLoadingDialog(globals, metaTable, varargs);
            }
        };
    }

}
