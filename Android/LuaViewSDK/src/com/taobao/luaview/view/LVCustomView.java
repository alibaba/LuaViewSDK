package com.taobao.luaview.view;

import android.graphics.Canvas;
import android.support.annotation.NonNull;

import com.taobao.luaview.userdata.ui.UDCustomView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * LuaView-Container
 * 容器类
 *
 * @author song
 * @date 15/8/20
 */
public class LVCustomView extends LVViewGroup<UDCustomView> {

    public LVCustomView(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals, metaTable, varargs);
    }

    @NonNull
    public UDCustomView createUserdata(Globals globals, LuaValue metaTable, Varargs varargs) {
        return new UDCustomView(this, globals, metaTable, varargs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mLuaUserdata != null && mLuaUserdata.hasOnDrawCallback()) {
            mLuaUserdata.callOnDraw(this, canvas);
        } else {
            super.onDraw(canvas);
        }
    }

    public void superOnDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
