package com.taobao.luaview.fun.mapper.kit;

import com.taobao.luaview.fun.base.BaseMethodMapper;
import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.kit.UDTimer;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * timer 接口封装
 *
 * @author song
 * @date 15/8/21
 */
@LuaViewLib
public class TimerMethodMapper<U extends UDTimer> extends BaseMethodMapper<U> {

    /**
     * 取消定时器
     *
     * @param udTimer
     * @param varargs
     * @return
     */
    public LuaValue start(U udTimer, Varargs varargs) {
        final long interval = varargs.optlong(2, 1) * 1000;//lua传过来的是秒，这里转成毫秒
        final boolean repeat = varargs.optboolean(3, false);
        return udTimer.start(interval, repeat);
    }

    /**
     * 取消定时器
     *
     * @param udTimer
     * @param varargs
     * @return
     */
    public LuaValue callback(U udTimer, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setCallback(udTimer, varargs);
        } else {
            return getCallback(udTimer, varargs);
        }
    }

    public LuaValue setCallback(U udTimer, Varargs varargs) {
        final LuaFunction callback = varargs.optfunction(2, null);
        return udTimer.setCallback(callback);
    }

    public LuaValue getCallback(U udTimer, Varargs varargs) {
        return udTimer.getCallback();
    }

    /**
     * 取消定时器
     *
     * @param udTimer
     * @param varargs
     * @return
     */
    public LuaValue cancel(U udTimer, Varargs varargs) {
        return udTimer.cancel();
    }
}
