/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.util;


import com.taobao.luaview.global.Constants;

import org.luaj.vm2.LuaValue;

/**
 * Dimension
 *
 * @author song
 */
public class DimenUtil {

    /**
     * convert a value to px，返回给Android系统的必须是整数
     *
     * @param value
     * @return
     */
    public static int dpiToPx(LuaValue value) {
        if (value != null && value.isnumber()) {
            return (int) (value.optdouble(0.0f) * Constants.sScale + 0.5f);//向上取整数
        }
        return 0;
    }

    /**
     * convert a value to px，返回给Android系统的必须是整数
     *
     * @param value
     * @return
     */
    public static int dpiToPx(LuaValue value, int defaultValue) {
        if (value != null && value.isnumber()) {
            return (int) (value.optdouble(0.0f) * Constants.sScale + 0.5f);//向上取整数
        }
        return defaultValue;
    }

    public static Integer dpiToPx(LuaValue value, Integer defaultValue) {
        if (value != null && value.isnumber()) {
            return (int) (value.optdouble(0.0f) * Constants.sScale + 0.5f);//向上取整数
        }
        return defaultValue;
    }


    /**
     * convert dpi to px，返回给Android系统的必须是整数
     *
     * @param dpi
     * @return
     */
    public static int dpiToPx(float dpi) {
        return (int) (dpi * Constants.sScale);
    }

    /**
     * convert dpi to px，返回给Android系统的必须是整数
     *
     * @param dpi
     * @return
     */
    public static float dpiToPxF(float dpi) {
        return (dpi * Constants.sScale);
    }

    /**
     * convert dpi to px，返回给Android系统的必须是整数
     *
     * @param dpi
     * @return
     */
    public static float[] dpiToPxF(float[] dpi) {
        if (dpi != null) {
            final float[] result = new float[dpi.length];
            for (int i = 0; i < dpi.length; i++) {
                result[i] = (dpi[i] * Constants.sScale);
            }
            return result;
        }
        return null;
    }

    /**
     * convert px to dpi ，返回给Lua层的调用，必须是浮点数
     *
     * @param px
     * @return
     */
    public static float pxToDpi(float px) {
        return px / Constants.sScale;
    }


    /**
     * 将sp值转换为px值，保证文字大小不变，给Android系统整数
     *
     * @param spValue（DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int spToPx(LuaValue spValue) {
        return spToPx(spValue != null ? (float) spValue.optdouble(0.0f) : 0);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变，给Android系统整数
     *
     * @param spValue（DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int spToPx(float spValue) {
        return (int) (spValue * Constants.sScale);
    }

    /**
     * 将px转成sp值，返回给Lua层的调用，必须是浮点数
     *
     * @param pxValue
     * @return
     */
    public static float pxToSp(LuaValue pxValue) {
        return pxToSp(pxValue != null ? (float) pxValue.optdouble(0.0f) : 0);
    }

    /**
     * 将px转成sp值，返回给Lua层的调用，必须是浮点数
     *
     * @param pxValue
     * @return
     */
    public static float pxToSp(float pxValue) {
        return pxValue / Constants.sScale;
    }

}
