package com.taobao.luaview.view.indicator;

import android.support.v4.view.ViewPager;

import com.taobao.luaview.userdata.indicator.UDCircleViewPagerIndicator;
import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.view.indicator.circle.CirclePageIndicator;
import com.taobao.luaview.view.interfaces.ILVView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;


/**
 * LuaView-CircleViewPagerIndicator
 * 容器类
 *
 * @author song
 * @date 15/8/20
 */
public class LVCircleViewPagerIndicator extends CirclePageIndicator implements ILVView {
    public Globals mGlobals;
    private LuaValue mInitParams;
    private ViewPager mViewPager;
    private UDCircleViewPagerIndicator mLuaUserdata;

    public LVCircleViewPagerIndicator(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals.context);
        this.mGlobals = globals;
        this.mInitParams = varargs != null ? varargs.arg1() : null;
        this.mLuaUserdata = new UDCircleViewPagerIndicator(this, globals, metaTable, this.mInitParams);
        this.setPadding(0, 2, 0, 0);
    }

    @Override
    public void setViewPager(ViewPager view) {
        mViewPager = view;
        super.setViewPager(view);
    }

    @Override
    public void setViewPager(ViewPager view, int initialPosition) {
        mViewPager = view;
        super.setViewPager(view, initialPosition);
    }

    @Override
    public UDView getUserdata() {
        return mLuaUserdata;
    }
}
