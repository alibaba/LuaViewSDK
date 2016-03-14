package com.taobao.luaview.util;

import android.graphics.Color;

import org.luaj.vm2.LuaValue;

/**
 * 颜色解析
 *
 * @author song
 * @date 15/10/27
 */
public class ColorUtil {

    /**
     * parse a color value
     *
     * @param colorValue
     * @return
     */
    public static int parse(LuaValue colorValue) {
        if (colorValue != null) {
            if (colorValue.isnumber()) {//必须先判断int再判断string
                int color = colorValue.toint();
                return Color.argb(255, Color.red(color), Color.green(color), Color.blue(color));//去除alpha信息，alpha信息由函数传入
            }
            /*if (LuaUtil.isString(colorValue)) {//TODO 支持字符串
                try {
                    return Color.parseColor(colorValue.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    return -1;
                }
            }*/
        }
        return Color.BLACK;//黑色
    }
}
