package com.taobao.luaview.global;

import android.os.Build;

import com.taobao.android.luaview.BuildConfig;

/**
 * LuaView 系统设置，设置是否debug，是否可以调试等
 *
 * @author song
 * @date 15/9/9
 */
public class LuaViewConfig {
    private static boolean isDebug = BuildConfig.DEBUG;
    private static boolean isOpenDebugger = isEmulator();//目前只支持模拟器下断点调试Lua，不支持真机，真机环境关闭该功能
    private static String sTtid = null;
    private static boolean isRefreshEnabled = true;

    public static boolean isDebug() {
        return isDebug;
    }

    public static boolean isOpenDebugger() {
        return isOpenDebugger;
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

    public static boolean isRefreshEnabled(){
        return isRefreshEnabled;
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
     * 是否可以刷新
     * @param enable
     */
    public static void setRefreshEnable(boolean enable){
        isRefreshEnabled = enable;
    }
}
