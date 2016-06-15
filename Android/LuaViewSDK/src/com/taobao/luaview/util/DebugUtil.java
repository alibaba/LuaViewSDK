package com.taobao.luaview.util;

import android.os.Debug;

/**
 * Debug performance
 *
 * @author song
 * @date 16/6/14
 * 主要功能描述
 * 修改描述
 * 下午10:51 song XXX
 */
public class DebugUtil {
    private static long tsTime;

    /**
     * time start
     */
    public static void ts(String method) {
        tsTime = Debug.threadCpuTimeNanos();
    }

    /**
     * time end
     * @param method
     */
    public static void te(String method) {
        LogUtil.d("[Debug-time]", method, Debug.threadCpuTimeNanos() - tsTime);
    }
}
