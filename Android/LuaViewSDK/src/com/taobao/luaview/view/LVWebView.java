package com.taobao.luaview.view;

import android.view.KeyEvent;
import android.webkit.WebView;

import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.userdata.ui.UDWebView;
import com.taobao.luaview.view.interfaces.ILVView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * Created by tuoli on 10/9/16.
 */

public class LVWebView extends WebView implements ILVView {

    private UDView mLuaUserdata;

    public LVWebView(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals.getContext());
        this.mLuaUserdata = new UDWebView(this, globals, metaTable, varargs);
        init();
    }

    private void init() {
        this.getSettings().setJavaScriptEnabled(true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 使点击返回键时可以回退网页,而不是关闭Browser
        if ((keyCode == KeyEvent.KEYCODE_BACK) && this.canGoBack()) {
            this.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public UDView getUserdata() {
        return mLuaUserdata;
    }
}
