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
import com.taobao.luaview.userdata.ui.UDLoadingView;
import com.taobao.luaview.util.ColorUtil;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.List;

/**
 * Loading View 接口封装
 *
 * @param <U>
 * @author song
 */
@LuaViewLib(revisions = {"20170306已对标"})
public class UILoadingViewMethodMapper<U extends UDLoadingView> extends UIViewGroupMethodMapper<U> {

    private static final String TAG = "UILoadingViewMethodMapper";
    private static final String[] sMethods = new String[]{
            "start",//0
            "isStart",//1
            "startAnimating",//2
            "isAnimating",//3
            "stop",//4
            "stopAnimating",//5
            "color"//6
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
                return start(target, varargs);
            case 1:
                return isStart(target, varargs);
            case 2:
                return startAnimating(target, varargs);
            case 3:
                return isAnimating(target, varargs);
            case 4:
                return stop(target, varargs);
            case 5:
                return stopAnimating(target, varargs);
            case 6:
                return color(target, varargs);
        }
        return super.invoke(code, target, varargs);
    }

    //--------------------------------------- API --------------------------------------------------


    /**
     * 菊花开始转动
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue start(U view, Varargs varargs) {
        return view.show();
    }

    @Deprecated
    public LuaValue isStart(U view, Varargs varargs) {
        return valueOf(view.isShow());
    }

    /**
     * 菊花开始转动
     *
     * @param view
     * @param varargs
     * @return
     */
    @Deprecated
    public LuaValue startAnimating(U view, Varargs varargs) {
        return view.show();
    }

    public LuaValue isAnimating(U view, Varargs varargs) {
        return valueOf(view.isShow());
    }


    /**
     * 菊花停止转动
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue stop(U view, Varargs varargs) {
        return view.hide();
    }

    @Deprecated
    public LuaValue stopAnimating(U view, Varargs varargs) {
        return view.hide();
    }

    /**
     * 颜色
     *
     * @param view
     * @param varargs
     * @return
     */
    @LuaViewApi(revisions = {"支持alpha"})
    public LuaValue color(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setColor(view, varargs);
        } else {
            return getColor(view, varargs);
        }
    }

    public LuaValue setColor(U view, Varargs varargs) {
        final Integer color = ColorUtil.parse(LuaUtil.getInt(varargs, 2));
        return view.setColor(color);
    }

    public LuaValue getColor(U view, Varargs varargs) {
        return valueOf(ColorUtil.getHexColor(view.getColor()));
    }

}