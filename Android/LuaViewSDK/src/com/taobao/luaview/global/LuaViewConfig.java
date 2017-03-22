/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.global;

import android.content.Context;
import android.os.Build;

import com.taobao.android.luaview.BuildConfig;

/**
 * LuaView 全局设置
 *
 * @author song
 * @date 15/9/9
 */
public class LuaViewConfig {
    private static boolean isDebug = BuildConfig.DEBUG;
    private static boolean isOpenDebugger = isEmulator();//目前只支持模拟器下断点调试Lua，不支持真机，真机环境关闭该功能
    private static boolean isLibsLazyLoad = false;//是否延迟加载Libs，如果延迟加载则只会加载用到的libs，并且只会在用到的时候才加载，不用到不加载
    private static boolean isUseLuaDC = false;//是否使用LuaDC Compiler，直接将lua代码编译成dex文件，能够加速虚拟机执行
    private static boolean isUseNoReflection = false;//是否不使用反射调用接口
    private static boolean isCachePrototype = false;//是否缓存prototype，默认不缓存
    private static boolean isAutoSetupClickEffects = false;//是否自动设置点击效果

    //设备标识
    private static String sTtid = null;

    /**
     * init luaview
     *
     * @param context
     */
    public static void init(Context context) {
        //延迟加载Libs
        LuaViewConfig.setLibsLazyLoad(true);
        //是否使用非反射方式API调用（默认为true)
        LuaViewConfig.setUseNoReflection(true);
    }

    public static boolean isDebug() {
        return isDebug;
    }

    public static boolean isOpenDebugger() {
        return isOpenDebugger;
    }

    public static boolean isLibsLazyLoad() {
        return isLibsLazyLoad;
    }

    public static boolean isUseLuaDC() {
        return isUseLuaDC;
    }

    /**
     * 判断当前设备是否是模拟器。如果返回TRUE，则当前是模拟器，不是返回FALSE
     *
     * @return
     */
    private static boolean isEmulator() {
        return isRunningOnGenymotion() || isRunningOnStockEmulator();
    }

    private static boolean isRunningOnGenymotion() {
        return Build.FINGERPRINT.contains("vbox");
    }

    private static boolean isRunningOnStockEmulator() {
        return Build.FINGERPRINT.contains("generic");
    }

    /**
     * 获取ttid
     *
     * @return
     */
    public static String getTtid() {
        return sTtid != null ? sTtid : "999999@taobao_android_1.0";
    }

    /**
     * 设置 ttid
     *
     * @param ttid
     */
    public static void setTtid(String ttid) {
        sTtid = ttid;
    }

    /**
     * 全局是否debug
     *
     * @param debug
     */
    public static void setDebug(boolean debug) {
        isDebug = debug;
    }

    /**
     * 设置是否开启调试器用于断点调试
     *
     * @param openDebugger
     */
    public static void setOpenDebugger(boolean openDebugger) {
        isOpenDebugger = openDebugger;
    }

    /**
     * 是否延迟加载libs，如果设置为true的话则会在运行的时候才会加载用户lib，而不是初始化虚拟机的时候加载
     *
     * @param lazyLoad
     */
    public static void setLibsLazyLoad(boolean lazyLoad) {
        isLibsLazyLoad = lazyLoad;
    }

    /**
     * 是否使用LuaDC Loader，可以直接lua to dex bytecode
     */
    public static void setUseLuaDC(boolean useLuaDC) {
        isUseLuaDC = useLuaDC;
    }

    /**
     * 设置不使用反射
     *
     * @param useNoReflection
     */
    public static void setUseNoReflection(boolean useNoReflection) {
        isUseNoReflection = useNoReflection;
    }

    public static boolean isUseNoReflection() {
        return isUseNoReflection;
    }

    /**
     * 是否缓存prototype
     */
    public static void setCachePrototype(boolean cachePrototype) {
        isCachePrototype = cachePrototype;
    }

    public static boolean isCachePrototype() {
        return isCachePrototype;
    }

    /**
     * 是否自动设置ripple effects
     *
     * @param isAutoSetupClickEffects
     */
    public static void setAutoSetupClickEffects(boolean isAutoSetupClickEffects) {
        LuaViewConfig.isAutoSetupClickEffects = isAutoSetupClickEffects;
    }

    public static boolean isAutoSetupClickEffects() {
        return isAutoSetupClickEffects;
    }
}
