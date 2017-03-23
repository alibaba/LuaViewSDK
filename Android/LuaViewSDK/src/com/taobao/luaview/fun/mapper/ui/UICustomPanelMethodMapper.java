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
import com.taobao.luaview.userdata.ui.UDCustomPanel;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.List;

/**
 * 自定义面板
 *
 * @param <U>
 * @author song
 */
@LuaViewLib(revisions = {"20170306已对标", "增加调用Lua方法"})
public class UICustomPanelMethodMapper<U extends UDCustomPanel> extends UIViewGroupMethodMapper<U> {

    private static final String TAG = "UICustomPanelMethodMapper";
    private static final String[] sMethods = new String[]{
            "nativeView",//0
            "getNativeView"//1
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
                return nativeView(target, varargs);
            case 1:
                return getNativeView(target, varargs);
        }
        return super.invoke(code, target, varargs);
    }

    //--------------------------------------- API --------------------------------------------------

    /**
     * 获取native view
     *
     * @param customPanel
     * @param varargs
     * @return
     */
    @Deprecated
    @LuaViewApi(since = VmVersion.V_500, revisions = {"移动到 UIViewMethodMapper（V510）"})
    public LuaValue nativeView(U customPanel, Varargs varargs) {
        return getNativeView(customPanel, varargs);
    }

    @Deprecated
    @LuaViewApi(since = VmVersion.V_500, revisions = {"移动到 UIViewMethodMapper（V510）"})
    public LuaValue getNativeView(U customPanel, Varargs varargs) {
        return customPanel.getNativeView();
    }
}