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
import com.taobao.luaview.userdata.list.UDBaseListView;
import com.taobao.luaview.userdata.ui.UDViewGroup;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.List;

/**
 * ListView的方法映射
 * @author song
 */
@LuaViewLib
@Deprecated
public abstract class UIBaseListViewMethodMapper<U extends UDViewGroup> extends UIBaseListOrRecyclerViewMethodMapper<U> {
    private static final String TAG = "UIBaseListViewMethodMapper";
    private static final String[] sMethods = new String[]{
            "header",//0
            "footer",//1
            "dividerHeight"//2
    };

    @Override
    public List<String> getAllFunctionNames() {
        return mergeFunctionNames(TAG, super.getAllFunctionNames(), sMethods);
    }

    @Override
    public Varargs invoke(int code, U target, Varargs varargs) {
        final int optcode = code - super.getAllFunctionNames().size();
        switch (optcode){
            case 0:
                return header(target, varargs);
            case 1:
                return footer(target, varargs);
            case 2:
                return dividerHeight(target, varargs);
        }
        return super.invoke(code, target, varargs);
    }

    //--------------------------------------- API --------------------------------------------------

    public abstract UDBaseListView getUDBaseListView(Varargs varargs);


    @Override
    public UDBaseListOrRecyclerView getUDBaseListOrRecyclerView(Varargs varargs) {
        return getUDBaseListView(varargs);
    }

    /**
     * 设置TableView的头
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue header(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setHeader(view, varargs);
        } else {
            return getHeader(view, varargs);
        }
    }

    public LuaValue setHeader(U view, Varargs varargs) {
        final LuaValue header = varargs.arg(2);
        return getUDBaseListView(varargs).setHeader(header);
    }

    public LuaValue getHeader(U view, Varargs varargs) {
        return getUDBaseListView(varargs).getHeader();
    }

    /**
     * 设置TableView的尾
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue footer(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setFooter(view, varargs);
        } else {
            return getFooter(view, varargs);
        }
    }

    public LuaValue setFooter(U view, Varargs varargs) {
        final LuaValue footer = varargs.arg(2);
        return getUDBaseListView(varargs).setFooter(footer);
    }

    public LuaValue getFooter(U view, Varargs varargs) {
        return getUDBaseListView(varargs).getFooter();
    }


    /**
     * 分隔线高度
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue dividerHeight(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setMiniSpacing(view, varargs);
        } else {
            return getMiniSpacing(view, varargs);
        }
    }
}