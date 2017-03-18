package com.taobao.luaview.userdata.constants;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.base.BaseLuaTable;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

/**
 * Created by tuoli on 11/7/16.
 *
 * 标记可吸顶的列表行
 */
@LuaViewLib(revisions = {"20170306已对标"})
public class UDPinned extends BaseLuaTable {
    public static final int PINNED_YES = 1;
    public static final int PINNED_NO = 0;

    public UDPinned(Globals globals, LuaValue metatable) {
        super(globals, metatable);
        init();
    }

    private void init() {
        this.set("YES", PINNED_YES);
        this.set("NO", PINNED_NO);
    }
}
