package com.taobao.luaview.util;

import android.os.Debug;
import android.text.TextUtils;

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
    private static long ttsTime;
    private static long totalTime;
    private static String sMethod;

    /**
     * time start
     */
    public static void ts(String method) {
        tsTime = Debug.threadCpuTimeNanos();
    }

    /**
     * time end
     *
     * @param method
     */
    public static long te(String method) {
        long nanoTime = Debug.threadCpuTimeNanos() - tsTime;
        LogUtil.d("[Debug-time]", method, nanoTime / 1000000, nanoTime);
        return nanoTime;
    }

    /**
     * total time start
     *
     * @param method
     */
    public static void tts(String method) {
        if (!TextUtils.equals(sMethod, method)) {
            totalTime = 0;
            sMethod = method;
        }
        ttsTime = Debug.threadCpuTimeNanos();
    }

    /**
     * total time end
     *
     * @param method
     * @return
     */
    public static long tte(String method) {
        long nanoTime = Debug.threadCpuTimeNanos() - ttsTime;
        totalTime = totalTime + nanoTime;
        LogUtil.d("[Debug-time]", method, totalTime / 1000000, totalTime);
        return totalTime;
    }
}
