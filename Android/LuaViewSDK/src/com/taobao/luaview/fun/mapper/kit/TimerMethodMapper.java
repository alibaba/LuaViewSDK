/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

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

import java.util.List;

/**
 * timer 接口封装
 *
 * @author song
 * @date 15/8/21
 */
@LuaViewLib(revisions = {"20170306已对标"})
public class TimerMethodMapper<U extends UDTimer> extends BaseMethodMapper<U> {
    private static final String TAG = "TimerMethodMapper";
    private static final String[] sMethods = new String[]{
            "delay",//0
            "repeat",//1
            "repeatCount",//2
            "interval",//3
            "start",//4
            "callback",//5
            "cancel"//6
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
                return delay(target, varargs);
            case 1:
                return repeat(target, varargs);
            case 2:
                return repeatCount(target, varargs);
            case 3:
                return interval(target, varargs);
            case 4:
                return start(target, varargs);
            case 5:
                return callback(target, varargs);
            case 6:
                return cancel(target, varargs);
        }
        return super.invoke(code, target, varargs);
    }

    //--------------------------------------- API --------------------------------------------------

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
        final long delay = LuaUtil.getTimeLong(varargs, 0f, 2);
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
     * 重复次数
     * TODO 修改次数控制
     * @param udTimer
     * @param varargs
     * @return
     */
    @LuaViewApi(since = VmVersion.V_511)
    @Deprecated
    public LuaValue repeatCount(U udTimer, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setRepeatCount(udTimer, varargs);
        } else {
            return getRepeatCount(udTimer, varargs);
        }
    }

    @LuaViewApi(since = VmVersion.V_511)
    public LuaValue setRepeatCount(U udTimer, Varargs varargs) {
        final int repeat = LuaUtil.getInt(varargs, 0, 2);
        return udTimer.setRepeat(repeat > 0);
    }

    @LuaViewApi(since = VmVersion.V_511)
    public LuaValue getRepeatCount(U udTimer, Varargs varargs) {
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
        final long interval = LuaUtil.getTimeLong(varargs, 1f, 2);
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
        final Long interval = LuaUtil.getTimeLong(varargs, 2);
        final Boolean repeat = LuaUtil.getBoolean(varargs, 3);
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
