package com.taobao.luaview.userdata.constants;

import com.taobao.luaview.userdata.base.BaseLuaTable;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

/**
 * Canvas.Paint 用户数据封装
 *
 * @author song
 * @date 15/9/6
 */
public class UDPaintStyle extends BaseLuaTable {

    public UDPaintStyle(Globals globals, LuaValue metatable) {
        super(globals, metatable);
        init();
    }

    private void init() {
        set("FILL", 0);//Paint.Style.FILL
        set("STROKE", 1);//Paint.Style.STROKE
    }
}
