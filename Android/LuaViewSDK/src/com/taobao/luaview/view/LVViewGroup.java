package com.taobao.luaview.view;

import android.graphics.Canvas;
import android.graphics.Region;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.userdata.ui.UDViewGroup;
import com.taobao.luaview.util.LuaViewUtil;
import com.taobao.luaview.view.interfaces.ILVViewGroup;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * LuaView-ViewGroup
 * 容器类
 *
 * @author song
 * @date 15/8/20
 */
public class LVViewGroup extends RelativeLayout implements ILVViewGroup {
    public Globals mGlobals;
    private UDViewGroup mLuaUserdata;

    public LVViewGroup(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals.context);
        this.mGlobals = globals;
        this.mLuaUserdata = new UDViewGroup(this, globals, metaTable, (varargs != null ? varargs.arg1() : null));
        //改在UDViewGroup中设置，减少影响面
//        this.setFocusableInTouchMode(true);//需要设置，否则onKeyUp等事件无法监听，排查是否会带来其他问题(点击的时候需要点击两下)
//        this.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        this.setWillNotDraw(true);
        this.setClipChildren(false);
    }

    @Override
    public UDView getUserdata() {
        return mLuaUserdata;
    }

    @Override
    public void addLVView(final View view, Varargs a) {
        final ViewGroup.LayoutParams layoutParams = LuaViewUtil.getOrCreateLayoutParams(view);
        LVViewGroup.this.addView(LuaViewUtil.removeFromParent(view), layoutParams);
    }

    public void show() {
        LVViewGroup.this.setVisibility(View.VISIBLE);
    }

    public void hide() {
        LVViewGroup.this.setVisibility(View.GONE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.clipRect(0, 0, getWidth(), getHeight(), Region.Op.DIFFERENCE);
        super.onDraw(canvas);
    }

    //-------------------------------------------显示回调--------------------------------------------
    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == View.VISIBLE) {
            mLuaUserdata.callOnShow();
        } else {//这里会有 INVISIBLE 和 GONE 两种状态，INVISIBLE 也会调用，从后台进入的时候会调用一次 INVISIBLE 接着调用 VISIBLE
            mLuaUserdata.callOnHide();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            LuaValue result = mLuaUserdata != null ? mLuaUserdata.callOnBack() : LuaValue.FALSE;
            return result != null && result.optboolean(false);
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(mLuaUserdata != null){
            mLuaUserdata.callOnLayout();
        }
    }
}
