/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.fun.base;

import com.taobao.luaview.cache.AppCache;
import com.taobao.luaview.global.LuaViewConfig;
import com.taobao.luaview.userdata.base.BaseUserdata;
import com.taobao.luaview.util.LogUtil;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 基础多参数函数
 *
 * @author song
 * @date 15/8/14
 */
public abstract class BaseMethodMapper<U extends LuaValue> extends VarArgFunction {
    private static final String CACHE_METHODS = AppCache.CACHE_METHODS;

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
            if (opcode != -1) {
                return invoke(opcode, getUD(args), args);
            } else {
                return (Varargs) method.invoke(this, getUD(args), args);
            }
        } catch (Exception e) {
            if (LuaViewConfig.isDebug()) {
                LogUtil.e("[----Method Invoke Error Start----]");
                LogUtil.e("[Class]", getClass());
                LogUtil.e("[Opcode]", opcode);
                LogUtil.e("[Method]", method != null ? method : getMethodByOpcode(opcode));
                LogUtil.e("[Arguments]", args);
                LogUtil.e("[Target]", getTarget(args));
                LogUtil.e("[Error]", e);
                LogUtil.e("[----Method Invoke Error End----]");
            }
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

    public Object getTarget(Varargs varargs) {
        U target = getUD(varargs);
        if (target instanceof BaseUserdata) {
            return ((BaseUserdata) target).userdata();
        }
        return target;
    }

    //----------------------------------------------------------------------------------------------

    /**
     * merge function names with cache tag
     *
     * @param tag
     * @param supernames
     * @param names
     * @return
     */
    public List<String> mergeFunctionNames(final String tag, final List<String> supernames, final String[] names) {
        List<String> result = AppCache.getCache(CACHE_METHODS).get(tag);
        if (result == null) {
            result = mergeFunctionNames(supernames, names);
            AppCache.getCache(CACHE_METHODS).put(tag, result);
        }
        return result;
    }

    public List<String> mergeFunctionNames(final String tag, final List<String> supernames, final List<String> names) {
        List<String> result = AppCache.getCache(CACHE_METHODS).get(tag);
        if (result == null) {
            result = mergeFunctionNames(supernames, names);
            AppCache.getCache(CACHE_METHODS).put(tag, result);
        }
        return result;
    }

    /**
     * merge function names
     * 将names拼接在supernames之后
     *
     * @param supernames
     * @param names
     * @return
     */
    private List<String> mergeFunctionNames(final List<String> supernames, final String[] names) {
        return mergeFunctionNames(supernames, Arrays.asList(names));
    }

    /**
     * merge FunctionNames
     * 将自己的names拼接在supernames之后
     */
    private List<String> mergeFunctionNames(final List<String> supernames, final List<String> names) {
        final List<String> result = new ArrayList<String>();
        if (supernames != null && supernames.size() > 0) {
            result.addAll(supernames);
        }
        if (supernames != null && names != null) {
            result.addAll(supernames.size(), names);
        }
        return result;
    }

    /**
     * 获取所有函数名称，供子类调用
     *
     * @return
     */
    public List<String> getAllFunctionNames() {
        return new ArrayList<String>();
    }

    /**
     * 根据code获取函数名称
     *
     * @param optcode
     * @return
     */
    public String getMethodByOpcode(int optcode) {
        List<String> allMethods = getAllFunctionNames();
        if (allMethods != null && allMethods.size() > optcode && optcode >= 0) {
            return allMethods.get(optcode);
        }
        return null;
    }

    /**
     * 调用子类
     *
     * @param code
     * @param target
     * @param varargs
     * @return
     */
    public Varargs invoke(int code, U target, Varargs varargs) {
        return LuaValue.NIL;
    }

}