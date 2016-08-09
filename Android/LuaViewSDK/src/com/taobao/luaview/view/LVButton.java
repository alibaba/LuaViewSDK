package com.taobao.luaview.view;

import android.view.Gravity;

import com.taobao.luaview.userdata.constants.UDFontSize;
import com.taobao.luaview.userdata.ui.UDButton;
import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.view.foreground.ForegroundButton;
import com.taobao.luaview.view.interfaces.ILVView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * LuaView-Button
 *
 * @author song
 * @date 15/8/20
 */
public class LVButton extends ForegroundButton implements ILVView {
    private UDView mLuaUserdata;

    public LVButton(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals.getContext());
        this.mLuaUserdata = new UDButton(this, globals, metaTable, varargs != null ? varargs.arg1() : null);
        this.setTextSize(UDFontSize.FONTSIZE_SMALL);
        this.setGravity(Gravity.CENTER);//默认居中
    }

    @Override
    public UDView getUserdata() {
        return mLuaUserdata;
    }
}
