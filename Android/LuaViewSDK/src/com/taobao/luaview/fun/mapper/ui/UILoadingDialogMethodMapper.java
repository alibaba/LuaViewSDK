/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.fun.mapper.ui;

import com.taobao.luaview.fun.base.BaseMethodMapper;
import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.ui.UDLoadingDialog;
import com.taobao.luaview.util.ColorUtil;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.List;

@LuaViewLib(revisions = {"20170306已对标"})
public class UILoadingDialogMethodMapper<U extends UDLoadingDialog> extends BaseMethodMapper<U> {

    private static final String TAG = "UILoadingDialogMethodMapper";
    private static final String[] sMethods = new String[]{
            "show",//0
            "isShow",//1
            "start",//2
            "isStart",//3
            "startAnimating",//4
            "isAnimating",//5
            "hide",//6
            "stop",//7
            "stopAnimating",//8
            "color"//9
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
                return show(target, varargs);
            case 1:
                return isShow(target, varargs);
            case 2:
                return start(target, varargs);
            case 3:
                return isStart(target, varargs);
            case 4:
                return startAnimating(target, varargs);
            case 5:
                return isAnimating(target, varargs);
            case 6:
                return hide(target, varargs);
            case 7:
                return stop(target, varargs);
            case 8:
                return stopAnimating(target, varargs);
            case 9:
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
    @Deprecated
    public LuaValue show(U view, Varargs varargs) {
        return view.startAnimating();
    }

    @Deprecated
    public LuaValue isShow(U view, Varargs varargs) {
        return valueOf(view.isAnimating());
    }


    /**
     * 菊花开始转动
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue start(U view, Varargs varargs) {
        return view.startAnimating();
    }

    @Deprecated
    public LuaValue isStart(U view, Varargs varargs) {
        return valueOf(view.isAnimating());
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
        return view.startAnimating();
    }

    public LuaValue isAnimating(U view, Varargs varargs) {
        return valueOf(view.isAnimating());
    }


    /**
     * 菊花停止转动
     *
     * @param view
     * @param varargs
     * @return
     */
    @Deprecated
    public LuaValue hide(U view, Varargs varargs) {
        return view.stopAnimating();
    }

    public LuaValue stop(U view, Varargs varargs) {
        return view.stopAnimating();
    }

    @Deprecated
    public LuaValue stopAnimating(U view, Varargs varargs) {
        return view.stopAnimating();
    }


    /**
     * 获取菊花颜色
     *
     * @param view
     * @param varargs
     * @return
     */
    public Varargs color(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setColor(view, varargs);
        } else {
            return getColor(view, varargs);
        }
    }

    public LuaValue setColor(U view, Varargs varargs) {
        final Integer color = ColorUtil.parse(LuaUtil.getInt(varargs, 2));
        final Double alpha = LuaUtil.getDouble(varargs, 3);
        return view.setColorAndAlpha(color, alpha);
    }

    public Varargs getColor(U view, Varargs varargs) {
        return varargsOf(valueOf(ColorUtil.getHexColor(view.getColor())), valueOf(view.getAlpha()));
    }


}