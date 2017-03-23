/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.util;

import android.content.Context;
import android.os.Debug;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.taobao.luaview.global.LuaViewConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Log Util
 *
 * @author song
 */
public class LogUtil {
    private static final String DEFAULT_PREFIX = "[LuaView]";
    private static long time = 0;

    //平均数
    private static Map<String, Map> mAvgTime = new HashMap<String, Map>();
    private static final String LAST_TIME = "last_time";
    private static final String TOTAL_TIME = "total_time";
    private static final String PRINT_INTERVAL = "print_interval";
    private static final String TIMES = "times";

    /**
     * 统计平均数值
     *
     * @param tag
     * @param msg
     */
    public static void avgTimeStart(String tag, long printInterval, Object... msg) {
        if (LuaViewConfig.isDebug()) {
            Map map = mAvgTime.get(tag);
            if (map == null) {//没开始
                map = new HashMap<String, Object>();
                map.put(TOTAL_TIME, 0l);
                map.put(PRINT_INTERVAL, printInterval);
                map.put(TIMES, 0l);
                mAvgTime.put(tag, map);
            }
            map.put(LAST_TIME, Debug.threadCpuTimeNanos());//每次都更新最新时间
        }
    }

    /**
     * 统计平均时常
     *
     * @param tag
     * @param msg
     */
    public static void avgTimeEnd(String tag, Object... msg) {
        if (LuaViewConfig.isDebug()) {
            Map map = mAvgTime.get(tag);
            if (map != null) {//已经开始
                long lastTime = Long.valueOf(String.valueOf(map.get(LAST_TIME)));
                long totalTime = Long.valueOf(String.valueOf(map.get(TOTAL_TIME))) + Debug.threadCpuTimeNanos() - lastTime;
                map.put(TOTAL_TIME, totalTime);//总时间
                long printInterval = Long.valueOf(String.valueOf(map.get(PRINT_INTERVAL)));
                long times = Long.valueOf(String.valueOf(map.get(TIMES))) + 1;//总次数
                if (times >= printInterval) {//可以打印
                    Log.d(DEFAULT_PREFIX, tag + " end " + (double) totalTime / printInterval + " " + getMsg(msg));
                    mAvgTime.put(tag, null);
                } else {
                    map.put(TIMES, times);//总次数
                }
            }
        }
    }

    /**
     * log time start, must used with timeEnd
     */
    public static void timeStart(Object... msg) {
        if (LuaViewConfig.isDebug()) {
            time = Debug.threadCpuTimeNanos();
            Log.d(DEFAULT_PREFIX, "[start] " + getMsg(msg));
        }
    }

    /**
     * log time end, must used with timeStart
     */
    public static void timeEnd(Object... msg) {
        if (LuaViewConfig.isDebug()) {
            Log.d(DEFAULT_PREFIX, "[end] " + (Debug.threadCpuTimeNanos() - time) / 1000000 + " " + (Debug.threadCpuTimeNanos() - time) + " " + getMsg(msg));
        }
    }

    /**
     * log a info message
     *
     * @param msg
     */
    public static void i(Object... msg) {
        if (LuaViewConfig.isDebug()) {
            Log.i(DEFAULT_PREFIX, getMsg(msg));
        }
    }

    /**
     * log a debug message
     *
     * @param msg
     */
    public static void d(Object... msg) {
        if (LuaViewConfig.isDebug()) {
            Log.d(DEFAULT_PREFIX, getMsg(msg));
        }
    }


    /**
     * log a debug message
     *
     * @param msg
     */
    public static void e(Object... msg) {
        if (LuaViewConfig.isDebug()) {
            Log.e(DEFAULT_PREFIX, getMsg(msg));
        }
    }


    /**
     * log a debug message to file
     *
     * @param filePath
     * @param msg
     */
    public static void fd(String filePath, Object... msg) {
        if (LuaViewConfig.isDebug()) {
            toFile(filePath, msg);
        }
    }

    /**
     * show a toast
     *
     * @param context
     * @param msg
     */
    public static void toast(Context context, Object... msg) {
        if (LuaViewConfig.isDebug()) {
            Toast.makeText(context, getMsg(msg), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * get message
     *
     * @param msg
     * @return
     */
    private static String getMsg(Object... msg) {
        StringBuffer sb = new StringBuffer();
        if (msg != null) {
            for (Object s : msg) {
                sb.append(s).append(" ");
            }
        }
        return sb.toString();
    }


    /**
     * log something to file
     *
     * @param filePath
     * @param msg
     */
    private static void toFile(String filePath, Object... msg) {
        File logFile = null;
        try {
            logFile = createFile(filePath, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (logFile != null) {
            try {
                BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                buf.append(getMsg(msg));
                buf.newLine();
                buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * create file
     *
     * @param filepath
     * @param recursion
     * @return
     * @throws java.io.IOException
     */
    private static File createFile(String filepath, boolean recursion) throws IOException {
        File f = null;
        if (!TextUtils.isEmpty(filepath))
            f = new File(filepath);

        if (f != null && !f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                if (!recursion) {
                    throw e;
                }
                File parent = f.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                try {
                    f.createNewFile();
                } catch (IOException e1) {
                    throw e1;
                }
            }
        }
        return f;
    }
}
