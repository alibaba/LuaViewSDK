/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.global;

import com.taobao.luaview.cache.AppCache;
import com.taobao.luaview.fun.base.BaseMethodMapper;
import com.taobao.luaview.fun.binder.constants.AlignBinder;
import com.taobao.luaview.fun.binder.constants.EllipsizeBinder;
import com.taobao.luaview.fun.binder.constants.FontStyleBinder;
import com.taobao.luaview.fun.binder.constants.FontWeightBinder;
import com.taobao.luaview.fun.binder.constants.GravityBinder;
import com.taobao.luaview.fun.binder.constants.InterpolatorBinder;
import com.taobao.luaview.fun.binder.constants.PaintStyleBinder;
import com.taobao.luaview.fun.binder.constants.PinnedBinder;
import com.taobao.luaview.fun.binder.constants.ScaleTypeBinder;
import com.taobao.luaview.fun.binder.constants.TextAlignBinder;
import com.taobao.luaview.fun.binder.constants.TouchEventBinder;
import com.taobao.luaview.fun.binder.constants.ViewEffectBinder;
import com.taobao.luaview.fun.binder.indicator.UICircleViewPagerIndicatorBinder;
import com.taobao.luaview.fun.binder.indicator.UICustomViewPagerIndicatorBinder;
import com.taobao.luaview.fun.binder.kit.ActionBarBinder;
import com.taobao.luaview.fun.binder.kit.AudioBinder;
import com.taobao.luaview.fun.binder.kit.BitmapBinder;
import com.taobao.luaview.fun.binder.kit.DataBinder;
import com.taobao.luaview.fun.binder.kit.FileBinder;
import com.taobao.luaview.fun.binder.kit.JsonBinder;
import com.taobao.luaview.fun.binder.kit.SystemBinder;
import com.taobao.luaview.fun.binder.kit.TimerBinder;
import com.taobao.luaview.fun.binder.kit.UnicodeBinder;
import com.taobao.luaview.fun.binder.kit.VibratorBinder;
import com.taobao.luaview.fun.binder.net.HttpBinder;
import com.taobao.luaview.fun.binder.ui.SpannableStringBinder;
import com.taobao.luaview.fun.binder.ui.UIAlertBinder;
import com.taobao.luaview.fun.binder.ui.UIAnimatorBinder;
import com.taobao.luaview.fun.binder.ui.UIAnimatorSetBinder;
import com.taobao.luaview.fun.binder.ui.UIButtonBinder;
import com.taobao.luaview.fun.binder.ui.UICustomViewBinder;
import com.taobao.luaview.fun.binder.ui.UIEditTextBinder;
import com.taobao.luaview.fun.binder.ui.UIHorizontalScrollViewBinder;
import com.taobao.luaview.fun.binder.ui.UIImageViewBinder;
import com.taobao.luaview.fun.binder.ui.UIListViewBinder;
import com.taobao.luaview.fun.binder.ui.UILoadingDialogBinder;
import com.taobao.luaview.fun.binder.ui.UILoadingViewBinder;
import com.taobao.luaview.fun.binder.ui.UIRecyclerViewBinder;
import com.taobao.luaview.fun.binder.ui.UIRefreshLayoutBinder;
import com.taobao.luaview.fun.binder.ui.UIRefreshListViewBinder;
import com.taobao.luaview.fun.binder.ui.UIRefreshRecyclerViewBinder;
import com.taobao.luaview.fun.binder.ui.UITextViewBinder;
import com.taobao.luaview.fun.binder.ui.UIToastBinder;
import com.taobao.luaview.fun.binder.ui.UIViewGroupBinder;
import com.taobao.luaview.fun.binder.ui.UIViewPagerBinder;
import com.taobao.luaview.fun.binder.ui.UIWebViewBinder;
import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.fun.mapper.ui.NewIndexFunction;
import com.taobao.luaview.scriptbundle.asynctask.SimpleTask;
import com.taobao.luaview.scriptbundle.asynctask.SimpleTask0;
import com.taobao.luaview.vm.extend.luadc.LuaDC;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.LibFunction;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * LuaView Lib管理等
 *
 * @author song
 * @date 15/8/14
 */
public class LuaViewManager {
    private static final String CACHE_METATABLES = AppCache.CACHE_METATABLES;

    public static Globals sStaticGlobals;//使用该全局Globals来加速加载，需要提前创建Globals

    /**
     * pre create globals
     *
     * @return
     */
    public static synchronized void preCreateGlobals() {
        synchronized (LuaViewManager.class) {
            if (sStaticGlobals == null) {
                new SimpleTask0() {
                    @Override
                    public void doTask() {
                        synchronized (LuaViewManager.class) {
                            if (sStaticGlobals == null) {
                                sStaticGlobals = setupGlobals(new Globals());
                            }
                        }
                    }
                }.executeInPool();
            }
        }
    }

    /**
     * 创建Globals
     * 根据是否用lua-to-java bytecode来处理（如果使用LuaJC的话则会使用库bcel 533k)
     *
     * @return
     */
    public static Globals createGlobals() {
        if (sStaticGlobals != null) {
            synchronized (sStaticGlobals) {
                Globals result = sStaticGlobals;
                sStaticGlobals = null;
                return result;
            }
        } else {
            return setupGlobals(new Globals());
        }
    }

    /**
     * 创建Globals
     * 根据是否用lua-to-java bytecode来处理（如果使用LuaJC的话则会使用库bcel 533k)
     *
     * @return
     */
    public static Globals createGlobalsAsync() {
        if (sStaticGlobals != null) {
            synchronized (sStaticGlobals) {
                Globals result = sStaticGlobals;
                sStaticGlobals = null;
                return result;
            }
        } else {
            final Globals globals = new Globals();
            new SimpleTask<Globals>() {
                @Override
                public void doTask(Globals... params) {
                    setupGlobals(params != null && params.length > 0 ? params[0] : null);
                }
            }.executeSerial(globals);//TODO 这里放到serial线程中执行，而不是executeInPool中执行，为了保证后续的执行时序列
            return globals;
        }
    }

    /**
     * setup global values
     *
     * @param globals
     */
    private static Globals setupGlobals(Globals globals) {
        if (globals != null) {
            if (LuaViewConfig.isOpenDebugger()) {
                JsePlatform.debugGlobals(globals);
            } else {
                JsePlatform.standardGlobals(globals);//加载系统libs TODO 性能瓶颈
            }

            if (LuaViewConfig.isUseLuaDC()) {
                LuaDC.install(globals);
            }
            loadLuaViewLibs(globals);//加载用户lib TODO 性能瓶颈
            globals.isInited = true;
        }
        return globals;
    }

    /**
     * tryLazyLoad Android API lib
     * TODO 能否做到按需加载，而不是首次进来加载全部binder
     *
     * @param globals
     */
    private static void loadLuaViewLibs(final Globals globals) {
        //ui
        globals.tryLazyLoad(new UITextViewBinder());
        globals.tryLazyLoad(new UIEditTextBinder());
        globals.tryLazyLoad(new UIButtonBinder());
        globals.tryLazyLoad(new UIImageViewBinder());
        globals.tryLazyLoad(new UIViewGroupBinder());
        globals.tryLazyLoad(new UIListViewBinder());
        globals.tryLazyLoad(new UIRecyclerViewBinder());
        globals.tryLazyLoad(new UIRefreshListViewBinder());
        globals.tryLazyLoad(new UIRefreshRecyclerViewBinder());
        globals.tryLazyLoad(new UIViewPagerBinder());
        globals.tryLazyLoad(new UICustomViewPagerIndicatorBinder());
        globals.tryLazyLoad(new UICircleViewPagerIndicatorBinder());
        globals.tryLazyLoad(new UIHorizontalScrollViewBinder());
        globals.tryLazyLoad(new UIAlertBinder());

        globals.tryLazyLoad(new UILoadingViewBinder());
        globals.tryLazyLoad(new UILoadingDialogBinder());
        globals.tryLazyLoad(new UIToastBinder());
        globals.tryLazyLoad(new SpannableStringBinder());

        globals.tryLazyLoad(new UIWebViewBinder());
        globals.tryLazyLoad(new UICustomViewBinder());
        globals.tryLazyLoad(new UIRefreshLayoutBinder());

        //animation
        globals.tryLazyLoad(new UIAnimatorBinder());
        globals.tryLazyLoad(new UIAnimatorSetBinder());

        //net
        globals.tryLazyLoad(new HttpBinder());

        //kit
        globals.tryLazyLoad(new TimerBinder());
        globals.tryLazyLoad(new SystemBinder());
        globals.tryLazyLoad(new ActionBarBinder());
        globals.tryLazyLoad(new FileBinder());
        globals.tryLazyLoad(new UnicodeBinder());
        globals.tryLazyLoad(new DataBinder());
        globals.tryLazyLoad(new JsonBinder());
        globals.tryLazyLoad(new AudioBinder());
        globals.tryLazyLoad(new VibratorBinder());

        globals.tryLazyLoad(new BitmapBinder());

        //常量
        globals.tryLazyLoad(new AlignBinder());
        globals.tryLazyLoad(new TextAlignBinder());
        globals.tryLazyLoad(new FontWeightBinder());
        globals.tryLazyLoad(new FontStyleBinder());
        globals.tryLazyLoad(new ScaleTypeBinder());
        globals.tryLazyLoad(new GravityBinder());
        globals.tryLazyLoad(new EllipsizeBinder());
        globals.tryLazyLoad(new InterpolatorBinder());
        globals.tryLazyLoad(new ViewEffectBinder());//view特效
        globals.tryLazyLoad(new PinnedBinder());
        globals.tryLazyLoad(new PaintStyleBinder());
        globals.tryLazyLoad(new TouchEventBinder());
    }

    //----------------------------------------bind methods------------------------------------------

    /**
     * bind lua function using methods
     *
     * @param factory
     * @param methods
     */
    public static LuaTable bindMethods(Class<? extends LibFunction> factory, Method[] methods) {
        if (methods != null) {
            return bindMethods(factory, Arrays.asList(methods));
        }
        return new LuaTable();
    }

    /**
     * bind lua functions using method
     *
     * @param factory
     * @param methods
     * @return
     */
    public static LuaTable bindMethods(Class<? extends LibFunction> factory, List<Method> methods) {
        LuaTable env = new LuaTable();
        try {
            if (methods != null) {
                for (int i = 0; i < methods.size(); i++) {
                    LibFunction f = factory.newInstance();
                    f.opcode = -1;
                    f.method = methods.get(i);
                    f.name = methods.get(i).getName();
                    env.set(f.name, f);
                }
            }
        } catch (Exception e) {
            throw new LuaError("[Bind Failed] " + e);
        } finally {
            return env;
        }
    }

    /**
     * bind lua functions using opcode
     *
     * @param factory
     * @param methods
     * @return
     */
    public static LuaTable bind(Class<? extends LibFunction> factory, List<String> methods) {
        LuaTable env = new LuaTable();
        try {
            if (methods != null) {
                for (int i = 0; i < methods.size(); i++) {
                    LibFunction f = factory.newInstance();
                    f.opcode = i;
                    f.method = null;
                    f.name = methods.get(i);
                    env.set(f.name, f);
                }
            }
        } catch (Exception e) {
            throw new LuaError("[Bind Failed] " + e);
        } finally {
            return env;
        }
    }

    /**
     * bind lua functions using opcode
     *
     * @param factory
     * @param methods
     * @return
     */
    public static LuaTable bind(Class<? extends LibFunction> factory, String[] methods) {
        LuaTable env = new LuaTable();
        try {
            if (methods != null) {
                for (int i = 0; i < methods.length; i++) {
                    LibFunction f = factory.newInstance();
                    f.opcode = i;
                    f.method = null;
                    f.name = methods[i];
                    env.set(f.name, f);
                }
            }
        } catch (Exception e) {
            throw new LuaError("[Bind Failed] " + e);
        } finally {
            return env;
        }
    }
    //-----------------------------------------metatable--------------------------------------------

    /**
     * create metatable for libs
     *
     * @return
     */
    public static LuaTable createMetatable(Class<? extends LibFunction> libClass) {
        LuaTable result = AppCache.getCache(CACHE_METATABLES).get(libClass);//get from cache

        if (result == null) {
            LuaTable libTable = null;
            if (LuaViewConfig.isUseNoReflection()) {
                final List<String> methodNames = getMapperMethodNames(libClass);
                libTable = LuaViewManager.bind(libClass, methodNames);
            } else {
                final List<Method> methods = getMapperMethods(libClass);
                libTable = LuaViewManager.bindMethods(libClass, methods);
            }
            result = LuaValue.tableOf(new LuaValue[]{LuaValue.INDEX, libTable, LuaValue.NEWINDEX, new NewIndexFunction(libTable)});

            //update cache
            AppCache.getCache(CACHE_METATABLES).put(libClass, result);
        }
        return result;
    }

    /**
     * 获取所有方法的名字
     *
     * @param clazz
     * @return
     */
    private static List<String> getMapperMethodNames(final Class clazz) {
        try {
            if (clazz != null) {
                Object obj = clazz.newInstance();
                if (obj instanceof BaseMethodMapper) {
                    return ((BaseMethodMapper) obj).getAllFunctionNames();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取所有方法
     *
     * @param clazz
     * @return
     */
    private static List<Method> getMapperMethods(final Class clazz) {
        final List<Method> methods = new ArrayList<Method>();
        getMapperMethodsByClazz(methods, clazz);
        return methods.size() > 0 ? methods : null;
    }

    private static void getMapperMethodsByClazz(final List<Method> result, final Class clazz) {
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

}
