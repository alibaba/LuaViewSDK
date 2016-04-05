package com.taobao.luaview.view;

import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import com.taobao.luaview.userdata.ui.UDHorizontalScrollView;
import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.util.LuaViewUtil;
import com.taobao.luaview.view.interfaces.ILVViewGroup;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.ArrayList;

/**
 * LuaView - HorizontalScrollView
 *
 * @author song
 * @date 15/8/20
 */
public class LVHorizontalScrollView extends HorizontalScrollView implements ILVViewGroup {
    public Globals mGlobals;
    private UDHorizontalScrollView mLuaUserdata;

    //root view
    private LVViewGroup mContainer;

    public LVHorizontalScrollView(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals.context);
        this.mGlobals = globals;
        this.mLuaUserdata = new UDHorizontalScrollView(this, globals, metaTable, varargs != null ? varargs.arg1() : null);
        init();
    }

    private void init() {
        this.setHorizontalScrollBarEnabled(false);//不显示滚动条
        mContainer = new LVViewGroup(mGlobals, mLuaUserdata.initParams.getmetatable(), null);
        addView(mContainer, LuaViewUtil.createRelativeLayoutParamsMM());
    }

    @Override
    public UDView getUserdata() {
        return mLuaUserdata;
    }

    @Override
    public void addLVView(final View view, Varargs varargs) {
        final ViewGroup.LayoutParams layoutParams = LuaViewUtil.getOrCreateLayoutParams(view);
        mContainer.addView(LuaViewUtil.removeFromParent(view), layoutParams);
    }

    @Override
    public void setChildNodeViews(ArrayList<UDView> childNodeViews) {

    }
}
