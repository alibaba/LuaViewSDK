package com.taobao.luaview.userdata.constants;

import android.view.Gravity;

import com.taobao.luaview.userdata.base.BaseLuaTable;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

/**
 * TextAlign 文本布局
 *
 * @author song
 * @date 15/9/6
 */
public class UDTextAlign extends BaseLuaTable {

    public UDTextAlign(Globals globals, LuaValue metatable) {
        super(globals, metatable);
        init();
    }

    private void init() {
        initTextViewAlignment();
    }


    /**
     * 文字的排版
     */
    private void initTextViewAlignment() {
        set("LEFT", Gravity.LEFT | Gravity.CENTER_VERTICAL);//默认竖直居中，ios不支持居上
        set("CENTER", Gravity.CENTER);
        set("RIGHT", Gravity.RIGHT | Gravity.CENTER_VERTICAL);//默认竖直居中，ios不支持居上


        //以下在两端统一后开启
        /*
        set("LEFT", Gravity.LEFT);
        set("RIGHT", Gravity.RIGHT);
        set("TOP", Gravity.TOP);
        set("BOTTOM", Gravity.BOTTOM);
        set("CENTER", Gravity.CENTER);
        set("HCENTER", Gravity.CENTER_HORIZONTAL);
        set("VCENTER", Gravity.CENTER_VERTICAL);
        */
    }

}
