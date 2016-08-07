package com.taobao.luaview.fun.mapper.kit;

import com.taobao.luaview.fun.base.BaseMethodMapper;
import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.kit.UDVibrator;
import com.taobao.luaview.util.DateUtil;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.List;

/**
 * Vibrate 接口封装，声音处理
 *
 * @author song
 * @date 15/8/21
 */
@LuaViewLib
public class VibratorMethodMapper<U extends UDVibrator> extends BaseMethodMapper<U> {
    private static final String TAG = VibratorMethodMapper.class.getSimpleName();

    @Override
    public List<String> getAllFunctionNames() {
        return mergeFunctionNames(TAG, super.getAllFunctionNames(), new String[]{
                "hasVibrator",//0
                "vibrate",//1
                "cancel"//2
        });
    }

    @Override
    public Varargs invoke(int code, U target, Varargs varargs) {
        final int optcode = code - super.getAllFunctionNames().size();
        switch (optcode){
            case 0:
                return hasVibrator(target, varargs);
            case 1:
                return vibrate(target, varargs);
            case 2:
                return cancel(target, varargs);
        }
        return super.invoke(code, target, varargs);
    }

    //--------------------------------------- API --------------------------------------------------

    /**
     * 是否存在震动组件
     * @param vibrator
     * @param varargs
     * @return
     */
    public LuaValue hasVibrator(U vibrator, Varargs varargs){
        return valueOf(vibrator.hasVibrator());
    }

    /**
     * 开始震动
     *
     * @param vibrator
     * @param varargs
     * @return
     */
    public LuaValue vibrate(U vibrator, Varargs varargs) {
        if (varargs.narg() > 2 || (varargs.narg() > 1 && varargs.istable(2))) {
            final LuaTable luaTable = LuaUtil.getTable(varargs, 2);
            final Integer repeat = LuaUtil.toJavaInt(varargs.arg(3));
            return vibrator.vibrate(luaTable, repeat);
        } else {
            final Double time = LuaUtil.getDouble(varargs, 2);
            return vibrator.vibrate((long) (time != null ? time * DateUtil.ONE_SECOND : DateUtil.ONE_SECOND));
        }
    }

    /**
     * 取消震动
     *
     * @param vibrator
     * @param varargs
     * @return
     */
    public LuaValue cancel(U vibrator, Varargs varargs) {
        return vibrator.cancel();
    }

}
