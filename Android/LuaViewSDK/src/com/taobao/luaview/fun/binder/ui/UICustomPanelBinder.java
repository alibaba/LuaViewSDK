/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.fun.binder.ui;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.fun.base.BaseVarArgUICreator;
import com.taobao.luaview.fun.mapper.ui.UICustomPanelMethodMapper;
import com.taobao.luaview.view.interfaces.ILVView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.LibFunction;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * 自定义面板，支持将其他自定义View加入该面板并跟Lua交互
 * @author song
 */
public class UICustomPanelBinder extends BaseFunctionBinder {
    private Class mPanelClazz;

    public UICustomPanelBinder(final Class clazz, final String bindName) {
        super(bindName != null ? bindName : (clazz != null ? clazz.getSimpleName() : null));
        this.mPanelClazz = clazz;
    }

    @Override
    public Class<? extends LibFunction> getMapperClass() {
        return UICustomPanelMethodMapper.class;
    }

    @Override
    public LuaValue createCreator(LuaValue env, LuaValue metaTable) {
        return new BaseVarArgUICreator(env.checkglobals(), metaTable, getMapperClass()) {
            @Override
            public ILVView createView(Globals globals, LuaValue metaTable, Varargs varargs) {
                if (mPanelClazz != null) {
                    try {
                        final Constructor constructor = mPanelClazz.getConstructor(Globals.class, LuaValue.class, Varargs.class);
                        return (ILVView) constructor.newInstance(globals, metaTable, varargs);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        };
    }
}