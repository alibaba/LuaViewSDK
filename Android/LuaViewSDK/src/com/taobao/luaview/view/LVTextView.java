package com.taobao.luaview.view;

import android.view.Gravity;
import android.widget.TextView;

import com.taobao.luaview.userdata.constants.UDFontSize;
import com.taobao.luaview.userdata.ui.UDTextView;
import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.view.interfaces.ILVView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * LuaView-TextView
 *
 * @author song
 * @date 15/8/20
 */
public class LVTextView extends TextView implements ILVView {
    private UDView mLuaUserdata;

    public LVTextView(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals.context);
        this.mLuaUserdata = new UDTextView(this, globals, metaTable, varargs != null ? varargs.arg1() : null);
        this.setIncludeFontPadding(false);//设置默认TextView不包含字体的padding，否则adjustSize的时候拿到的高度有问题
        this.setGravity(Gravity.CENTER_VERTICAL);//默认竖直居中
        this.setLines(1);//默认一行
        this.setTextSize(UDFontSize.FONTSIZE_SMALL);
//        this.setEllipsize(TextUtils.TruncateAt.END);//默认在最后有3个点
    }

    @Override
    public UDView getUserdata() {
        return mLuaUserdata;
    }
}
