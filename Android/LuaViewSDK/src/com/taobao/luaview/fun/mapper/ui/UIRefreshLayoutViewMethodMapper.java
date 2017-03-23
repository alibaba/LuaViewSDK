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
import com.taobao.luaview.userdata.ui.UDRefreshLayout;

import org.luaj.vm2.Lua;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.List;

/**
 * Created by tuoli on 12/20/16.
 */
@LuaViewLib(revisions = {"20170306已对标", "iOS无"})
public class UIRefreshLayoutViewMethodMapper <U extends UDRefreshLayout> extends UIViewGroupMethodMapper<U> {

    private static final String TAG = "UIRefreshLayoutViewMethodMapper";
    private static final String[] sMethods = new String[] {
            "stopRefreshing",
            "setRefreshingOffset"
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
                return stopRefreshing(target, varargs);
            case 1:
                return setRefreshingOffset(target, varargs);
        }
        return super.invoke(code, target, varargs);
    }

    //--------------------------------------- API --------------------------------------------------

    public LuaValue stopRefreshing(U view, Varargs varargs) {
        view.stopRefreshing();
        return this;
    }

    @LuaViewApi(revisions = {"名称去掉set"})
    public LuaValue setRefreshingOffset(U view, Varargs varargs) {
        final LuaValue offset = varargs.optvalue(2, NIL);
        view.setRefreshingOffset(offset.tofloat());
        return this;
    }

}
