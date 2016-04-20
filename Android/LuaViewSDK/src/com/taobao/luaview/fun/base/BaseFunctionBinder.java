package com.taobao.luaview.fun.base;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.fun.mapper.ui.NewIndexFunction;
import com.taobao.luaview.global.LuaViewManager;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.LibFunction;
import org.luaj.vm2.lib.TwoArgFunction;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * 基础两参数函数对象
 *
 * @author song
 * @date 15/8/14
 */
public abstract class BaseFunctionBinder extends TwoArgFunction {
    private static final String TAG = BaseFunctionBinder.class.getSimpleName();
    public String[] luaNames;

    public BaseFunctionBinder(String... name) {
        this.luaNames = name;
    }

    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        return call(env, getMapperClass());
    }

    private LuaValue call(LuaValue env, Class<? extends LibFunction> libClass) {
        LuaTable methodMapper = LuaViewManager.bind(libClass, getMapperMethods(libClass));
        if (luaNames != null) {
            for (String name : luaNames) {
                env.set(name, createCreator(env, addNewIndex(methodMapper)));
            }
        }
        return methodMapper;
    }

    public abstract Class<? extends LibFunction> getMapperClass();

    /**
     * 默认返回metatable，如果要使用对象方式调用，则返回一个LuaFunction
     *
     * @param env
     * @param metaTable
     * @return
     */
    public LuaValue createCreator(LuaValue env, LuaValue metaTable) {
        return metaTable;
    }

    /**
     * 获取所有方法
     *
     * @param clazz
     * @return
     */
    private List<Method> getMapperMethods(final Class clazz) {
        /*final Method[] methods = clazz != null ? clazz.getMethods() : null;
        if (methods != null && methods.length > 0) {
            final List<Method> result = new ArrayList<Method>();
            for (final Method method : methods) {
                if (method.getDeclaringClass() != null && method.getDeclaringClass().isAnnotationPresent(LuaViewLib.class)) {//XXXMapper
                    result.add(method);
                }
            }
            return result;
        }
        return null;*/
        final List<Method> methods = new ArrayList<Method>();
        getMapperMethodsByClazz(methods, clazz);
        return methods.size() > 0 ? methods : null;
    }

    private void getMapperMethodsByClazz(final List<Method> result, final Class clazz) {
        if (clazz != null && clazz.isAnnotationPresent(LuaViewLib.class)) {//XXXMapper
            getMapperMethodsByClazz(result, clazz.getSuperclass());//处理super
            final Method[] methods = clazz.getDeclaredMethods();
            if (methods != null && methods.length > 0) {
                for (final Method method : methods) {//add self
                    if (method.getModifiers() == Modifier.PUBLIC) {//public 方法才行
                        result.add(method);
                    }
                }
            }
        }
    }

    private LuaValue addNewIndex(LuaTable t) {
        return tableOf(new LuaValue[]{INDEX, t, NEWINDEX, new NewIndexFunction(t)});
    }
}
