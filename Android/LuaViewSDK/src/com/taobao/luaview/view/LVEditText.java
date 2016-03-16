package com.taobao.luaview.view;

import android.widget.EditText;

import com.taobao.luaview.userdata.constants.UDFontSize;
import com.taobao.luaview.userdata.ui.UDEditText;
import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.view.interfaces.ILVView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * LuaView-EditText
 *
 * @author song
 * @date 15/8/20
 */
public class LVEditText extends EditText implements ILVView {
    private UDView mLuaUserdata;

    public LVEditText(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals.context);
        this.mLuaUserdata = new UDEditText(this, globals, metaTable, varargs != null ? varargs.arg1() : null);
        this.setTextSize(UDFontSize.FONTSIZE_SMALL);
    }

    @Override
    public UDView getUserdata() {
        return mLuaUserdata;
    }
}
