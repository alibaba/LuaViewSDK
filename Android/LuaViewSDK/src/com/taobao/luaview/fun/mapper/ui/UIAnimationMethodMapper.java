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
import com.taobao.luaview.userdata.ui.UDAnimation;
import com.taobao.luaview.util.ParamUtil;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.List;

/**
 * Animation 接口封装
 *
 * @author song
 * @date 15/8/21
 */
@Deprecated
@LuaViewLib
public class UIAnimationMethodMapper<U extends UDAnimation> extends BaseMethodMapper<U> {

    private static final String TAG = "UIAnimationMethodMapper";
    private static final String[] sMethods = new String[]{
            "alpha",//0
            "rotate",//1
            "scale",//2
            "translate",//3
            "duration",//4
            "startDelay",//5
            "repeatCount",//6
            "to",//7
            "callback",//8
            "onStartCallback",//9
            "onEndCallback",//10
            "onRepeatCallback"//11
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
                return alpha(target, varargs);
            case 1:
                return rotate(target, varargs);
            case 2:
                return scale(target, varargs);
            case 3:
                return translate(target, varargs);
            case 4:
                return duration(target, varargs);
            case 5:
                return startDelay(target, varargs);
            case 6:
                return repeatCount(target, varargs);
            case 7:
                return to(target, varargs);
            case 8:
                return callback(target, varargs);
            case 9:
                return onStartCallback(target, varargs);
            case 10:
                return onEndCallback(target, varargs);
            case 11:
                return onRepeatCallback(target, varargs);
        }
        return super.invoke(code, target, varargs);
    }

    //--------------------------------------- API --------------------------------------------------


    public LuaValue alpha(U udAnimation, Varargs varargs) {
        return udAnimation.alpha(ParamUtil.getFloatValues(varargs, 2));
    }

    public LuaValue rotate(U udAnimation, Varargs varargs) {
        return udAnimation.rotate(ParamUtil.getFloatValues(varargs, 2));
    }

    public LuaValue scale(U udAnimation, Varargs varargs) {
        return udAnimation.scale(ParamUtil.getFloatValues(varargs, 2));
    }

    public LuaValue translate(U udAnimation, Varargs varargs) {
        return udAnimation.translate(ParamUtil.getFloatValues(varargs, 2));
    }

    public LuaValue duration(U udAnimation, Varargs varargs) {
        final long duration = (long) (varargs.optdouble(2, 0.3f) * 1000);
        return udAnimation.setDuration(duration);
    }

    public LuaValue startDelay(U udAnimation, Varargs varargs) {
        final long delay = (long) (varargs.optdouble(2, 0) * 1000);
        return udAnimation.setStartDelay(delay);
    }

    public LuaValue repeatCount(U udAnimation, Varargs varargs) {
        final int repeatCount = varargs.optint(2, 0);
        return udAnimation.setRepeatCount(repeatCount);
    }

    public LuaValue to(U udAnimation, Varargs varargs) {
        return udAnimation.setValue(ParamUtil.getFloatValues(varargs, 2));
    }

    public LuaValue callback(U udAnimation, Varargs varargs) {
        final LuaTable callback = varargs.opttable(2, null);
        return udAnimation.setCallback(callback);
    }


    public LuaValue onStartCallback(U udAnimation, Varargs varargs) {
        final LuaFunction callback = varargs.optfunction(2, null);
        return udAnimation.setOnStartCallback(callback);
    }

    public LuaValue onEndCallback(U udAnimation, Varargs varargs) {
        final LuaFunction callback = varargs.optfunction(2, null);
        return udAnimation.setOnEndCallback(callback);
    }

    public LuaValue onRepeatCallback(U udAnimation, Varargs varargs) {
        final LuaFunction callback = varargs.optfunction(2, null);
        return udAnimation.setOnRepeatCallback(callback);
    }
}
