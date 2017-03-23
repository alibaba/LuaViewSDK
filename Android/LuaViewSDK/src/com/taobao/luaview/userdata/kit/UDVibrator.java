/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.kit;

import android.content.Context;
import android.os.Vibrator;

import com.taobao.luaview.userdata.base.BaseUserdata;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * Vibrate 用户数据封装
 *
 * @author song
 * @date 15/9/6
 */
public class UDVibrator extends BaseUserdata {
    private Vibrator mVibrator;

    public UDVibrator(Globals globals, LuaValue metatable, Varargs varargs) {
        super(globals, metatable, varargs);
    }


    public synchronized Vibrator getVibrator() {
        if (mVibrator == null && getContext() != null) {
            mVibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        }
        return mVibrator;
    }

    /**
     * 是否有震动组件
     * @return
     */
    public boolean hasVibrator() {
        return getVibrator() != null && getVibrator().hasVibrator();
    }

    /**
     * 震动
     *
     * @param time
     * @return
     */
    public UDVibrator vibrate(final long time) {
        final Vibrator vibrator = getVibrator();
        if (vibrator != null) {
            vibrator.vibrate(time);
        }
        return this;
    }

    /**
     * 震动
     *
     * @param patternTable
     * @param repeat
     * @return
     */
    public UDVibrator vibrate(final LuaTable patternTable, final Integer repeat) {
        if (patternTable != null) {
            final Vibrator vibrator = getVibrator();
            if (vibrator != null) {
                if (patternTable.length() > 1) {
                    long[] pattern = new long[patternTable.length()];
                    for (int i = 0; i < patternTable.length(); i++) {
                        pattern[i] = (long) (patternTable.get((i + 1)).optdouble(1) * 1000);
                    }
                    vibrator.vibrate(pattern, repeat);
                } else {
                    vibrator.vibrate((long) (patternTable.get(1).optdouble(1) * 1000));
                }
            }
        }
        return this;
    }

    public UDVibrator cancel() {
        final Vibrator vibrator = getVibrator();
        if (vibrator != null) {
            vibrator.cancel();
        }
        return this;
    }
}
