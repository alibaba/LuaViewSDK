package com.taobao.luaview.fun.mapper.kit;

import com.taobao.luaview.fun.base.BaseMethodMapper;
import com.taobao.luaview.fun.mapper.LuaViewApi;
import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.global.VmVersion;
import com.taobao.luaview.userdata.kit.UDTimer;
import com.taobao.luaview.util.LuaUtil;

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
     * 启动延时
     *
     * @param udTimer
     * @param varargs
     * @return
     */
    @LuaViewApi(since = VmVersion.V_500)
    public LuaValue delay(U udTimer, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setDelay(udTimer, varargs);
        } else {
            return getDelay(udTimer, varargs);
        }
    }

    @LuaViewApi(since = VmVersion.V_500)
    public LuaValue setDelay(U udTimer, Varargs varargs) {
        final long delay = LuaUtil.getLong(varargs, 0L, 2) * 1000;
        return udTimer.setDelay(delay);
    }

    @LuaViewApi(since = VmVersion.V_500)
    public LuaValue getDelay(U udTimer, Varargs varargs) {
        return valueOf(udTimer.getDelay());
    }

    /**
     * 是否重复
     *
     * @param udTimer
     * @param varargs
     * @return
     */
    @LuaViewApi(since = VmVersion.V_500)
    public LuaValue repeat(U udTimer, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setRepeat(udTimer, varargs);
        } else {
            return getRepeat(udTimer, varargs);
        }
    }

    @LuaViewApi(since = VmVersion.V_500)
    public LuaValue setRepeat(U udTimer, Varargs varargs) {
        final boolean repeat = LuaUtil.getBoolean(varargs, false, 2);
        return udTimer.setRepeat(repeat);
    }

    @LuaViewApi(since = VmVersion.V_500)
    public LuaValue getRepeat(U udTimer, Varargs varargs) {
        return valueOf(udTimer.isRepeat());
    }

    /**
     * 设置interval
     *
     * @param udTimer
     * @param varargs
     * @return
     */
    @LuaViewApi(since = VmVersion.V_500)
    public LuaValue interval(U udTimer, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setInterval(udTimer, varargs);
        } else {
            return getInterval(udTimer, varargs);
        }
    }

    @LuaViewApi(since = VmVersion.V_500)
    public LuaValue setInterval(U udTimer, Varargs varargs) {
        final long interval = LuaUtil.getLong(varargs, 1L, 2) * 1000;
        return udTimer.setInterval(interval);
    }

    @LuaViewApi(since = VmVersion.V_500)
    public LuaValue getInterval(U udTimer, Varargs varargs) {
        return valueOf(udTimer.getInterval());
    }

    /**
     * 取消定时器
     *
     * @param udTimer
     * @param varargs
     * @return
     */
    public LuaValue start(U udTimer, Varargs varargs) {
        final Long interval = LuaUtil.getLong(varargs, 2);
        final Boolean repeat = LuaUtil.getBoolean(varargs, 3);
        return udTimer.start(interval != null ? interval * 1000 : null, repeat);
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
