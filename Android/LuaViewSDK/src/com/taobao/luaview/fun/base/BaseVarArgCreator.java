package com.taobao.luaview.fun.base;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.fun.mapper.ui.NewIndexFunction;
import com.taobao.luaview.global.LuaViewConfig;
import com.taobao.luaview.global.LuaViewManager;
import com.taobao.luaview.util.DebugUtil;
import com.taobao.luaview.util.LogUtil;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.LibFunction;
import org.luaj.vm2.lib.VarArgFunction;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 零参数函数
 *
 * @author song
 * @date 15/8/14
 */
public abstract class BaseVarArgCreator extends VarArgFunction {
    public Globals globals;
    public LuaValue metatable;

    //用于延迟加载&cache
    public Class<? extends LibFunction> libClass;
    private static Map<Class, LuaValue> sMetatables = new HashMap<Class, LuaValue>();

    public BaseVarArgCreator(Globals globals, LuaValue metatable) {
        this(globals, metatable, null);
    }

    public BaseVarArgCreator(Globals globals, LuaValue metatable, Class<? extends LibFunction> libClass) {
        this.globals = globals;
        this.metatable = metatable;
        this.libClass = libClass;
    }

    public Varargs invoke(Varargs args) {
        if(LuaViewConfig.isLibsLazyLoad()){
            if(metatable == null && sMetatables != null && sMetatables.containsKey(getClass())){//先取cache
                metatable = sMetatables.get(getClass());
            }

            if(metatable == null && libClass != null) {
                metatable = LuaViewManager.createMetatable(libClass);
                sMetatables.put(getClass(), metatable);
            }
        }
        return createUserdata(globals, metatable, args);
    }

    /**
     * 获取所有方法
     *
     * @param clazz
     * @return
     */
    private List<Method> getMapperMethods(final Class clazz) {
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

    public abstract LuaValue createUserdata(Globals globals, LuaValue metaTable, Varargs varargs);
}
