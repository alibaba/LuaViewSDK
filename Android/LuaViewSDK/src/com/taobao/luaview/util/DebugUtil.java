/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.util;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

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
    private static final Map<String, Long> sTime = new HashMap<String, Long>();

    /**
     * time start
     */
    public static void ts(String method) {
        tsTime = System.nanoTime();
    }

    /**
     * 统计独立的时间
     *
     * @param method
     */
    public static void tsi(String method) {
        sTime.put(method, System.nanoTime());
    }

    /**
     * time end
     *
     * @param method
     */
    public static long te(String method) {
        long nanoTime = System.nanoTime() - tsTime;
        LogUtil.d("[Debug-time]", method, nanoTime / 1000000, nanoTime);
        return nanoTime;
    }

    /**
     * 统计独立的时间
     *
     * @param method
     */
    public static void tei(String method) {
        Long startTime = sTime.get(method);
        if (startTime != null) {
            Long nanoTime = System.nanoTime() - startTime;
            LogUtil.d("[Debug-time]", method, nanoTime / 1000000, nanoTime);
        }
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
        ttsTime = System.nanoTime();
    }

    /**
     * total time end
     *
     * @param method
     * @return
     */
    public static long tte(String method) {
        long nanoTime = System.nanoTime() - ttsTime;
        totalTime = totalTime + nanoTime;
        LogUtil.d("[Debug-time]", method, totalTime / 1000000, totalTime);
        return totalTime;
    }
}
