package com.taobao.luaview.fun.mapper.ui;

import com.taobao.luaview.fun.base.BaseMethodMapper;
import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.ui.UDAnimation;
import com.taobao.luaview.util.ParamUtil;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * Animation 接口封装
 *
 * @author song
 * @date 15/8/21
 */
@Deprecated
@LuaViewLib
public class UIAnimationMethodMapper<U extends UDAnimation> extends BaseMethodMapper<U> {


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
