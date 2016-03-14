package com.taobao.luaview.fun.base;

import com.taobao.luaview.util.LogUtil;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

/**
 * 基础多参数函数
 *
 * @author song
 * @date 15/8/14
 */
public abstract class BaseMethodMapper<U extends LuaValue> extends VarArgFunction {

    /**
     * 该函数使用反射，调用方法，并且被调用方法签名必须为：fun(UIView, Varargs)格式，否则不被支持
     * 所有的method都是被注册的public方法，使用class.getMethods返回
     * TODO 如果这里性能受限的话，考虑使用其他方式实现(反射性能大概低了20倍)，但是只会在创建的时候消耗，其他情况下不消耗性能
     *
     * @param args the arguments to the function call.
     * @return
     */
    public Varargs invoke(Varargs args) {
        try {
            return (Varargs) method.invoke(this, getUD(args), args);
        } catch (Exception e) {
            LogUtil.e("[----Method Invoke Error Start----]");
            LogUtil.e("[Class]", getClass());
            LogUtil.e("[Method] ", method);
            LogUtil.e("[Arguments] ", args);
            LogUtil.e("[Error] ", e);
            LogUtil.e("[----Method Invoke Error End----]");
            e.printStackTrace();
            return NONE;
        }
    }

    /**
     * 获取userdata
     *
     * @param varargs
     * @return
     */
    public U getUD(Varargs varargs) {
        return (U) varargs.arg1();
    }
}