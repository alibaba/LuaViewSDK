package com.taobao.luaview.userdata.constants;

import android.view.Gravity;

import com.taobao.luaview.userdata.base.BaseLuaTable;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

/**
 * Gravity 文本布局
 *
 * @author song
 * @date 15/9/6
 */
public class UDGravity extends BaseLuaTable {

    public UDGravity(Globals globals, LuaValue metatable) {
        super(globals, metatable);
        init();
    }

    private void init() {
        initGravity();
    }

    /**
     * 设置gravity
     */
    private void initGravity() {
        set("LEFT", Gravity.LEFT);
        set("TOP", Gravity.TOP);
        set("RIGHT", Gravity.RIGHT);
        set("BOTTOM", Gravity.BOTTOM);
        set("START", Gravity.START);
        set("END", Gravity.END);
        set("CENTER", Gravity.CENTER);
        set("H_CENTER", Gravity.CENTER_HORIZONTAL);
        set("V_CENTER", Gravity.CENTER_VERTICAL);
        set("FILL", Gravity.FILL);
        set("H_FILL", Gravity.FILL_HORIZONTAL);
        set("V_FILL", Gravity.FILL_VERTICAL);
    }

}
