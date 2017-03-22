/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.fun.mapper.list;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.list.UDBaseListOrRecyclerView;
import com.taobao.luaview.userdata.list.UDBaseRecyclerView;

import org.luaj.vm2.Varargs;

import java.util.List;

/**
 * RecyclerView的方法映射
 * @author song
 */
@LuaViewLib
public abstract class UIBaseRecyclerViewMethodMapper<U extends UDBaseRecyclerView> extends UIBaseListOrRecyclerViewMethodMapper<U> {
    private  static final String TAG = "UIBaseRecyclerViewMethodMapper";

    @Override
    public List<String> getAllFunctionNames() {
        return mergeFunctionNames(TAG, super.getAllFunctionNames(), new String[]{});
    }

    @Override
    public Varargs invoke(int code, U target, Varargs varargs) {
        final int optcode = code - super.getAllFunctionNames().size();
        switch (optcode){
            //TODO
        }
        return super.invoke(code, target, varargs);
    }

    //--------------------------------------- API --------------------------------------------------

    public abstract UDBaseRecyclerView getUDBaseRecyclerView(Varargs varargs);

    @Override
    public UDBaseListOrRecyclerView getUDBaseListOrRecyclerView(Varargs varargs) {
        return getUD(varargs);
    }
}