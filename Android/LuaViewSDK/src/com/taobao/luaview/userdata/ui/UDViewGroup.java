package com.taobao.luaview.userdata.ui;

import android.view.View;
import android.view.ViewGroup;

import com.facebook.csslayout.CSSNode;
import com.taobao.luaview.layout.FlexboxCSSParser;
import com.taobao.luaview.util.LuaUtil;
import com.taobao.luaview.util.LuaViewUtil;
import com.taobao.luaview.view.interfaces.ILVViewGroup;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;

import java.util.ArrayList;

/**
 * 容器类
 *
 * @author song
 * @date 15/8/20
 */
public class UDViewGroup<T extends ViewGroup> extends UDView<T> {
    private LuaValue mOnShow;
    private LuaValue mOnHide;
    private LuaValue mOnBack;

    public UDViewGroup(T view, Globals globals, LuaValue metatable, LuaValue initParams) {
        super(view, globals, metatable, initParams);
        init();
    }

    private void init() {

    }

    @Override
    public UDViewGroup setCallback(LuaValue callbacks) {
        super.setCallback(callbacks);
        if (this.mCallback != null) {
            mOnShow = LuaUtil.getFunction(mCallback, "onShow", "OnShow");
            mOnHide = LuaUtil.getFunction(mCallback, "onHide", "OnHide");
            mOnBack = LuaUtil.getFunction(mCallback, "onBack", "OnBack");
            //是否需要统一成OnShow, OnHide?
        }
        return this;
    }

    public UDViewGroup setOnShowCallback(final LuaValue callback) {
        mOnShow = callback;
        return this;
    }

    public UDViewGroup setOnHideCallback(final LuaValue callback) {
        mOnHide = callback;
        return this;
    }

    public UDViewGroup setOnBackCallback(final LuaValue callback) {
        mOnBack = callback;
        return this;
    }

    public LuaValue callOnShow() {
        return LuaUtil.callFunction(mOnShow);
    }

    public LuaValue callOnHide() {
        return LuaUtil.callFunction(mOnHide);
    }

    public LuaValue callOnBack() {
        return LuaUtil.callFunction(mOnBack);
    }

    /**
     * add a subview
     *
     * @param subView
     * @return
     */
    public UDViewGroup addView(UDView subView) {
        final ViewGroup viewGroup = getView();
        if (viewGroup != null && subView != null && subView.getView() != null) {
            final View view = subView.getView();
            if (viewGroup instanceof ILVViewGroup) {
                ((ILVViewGroup) viewGroup).addLVView(view, null);
            } else {
                final ViewGroup.LayoutParams layoutParams = LuaViewUtil.getOrCreateLayoutParams(view);
                viewGroup.addView(view, layoutParams);
            }
        }
        return this;
    }

    /**
     * remove a subview
     *
     * @param subView
     * @return
     */
    public UDViewGroup removeView(UDView subView) {
        final ViewGroup viewGroup = getView();
        if (viewGroup != null && subView != null && subView.getView() != null) {
            final View view = subView.getView();
            LuaViewUtil.removeView(viewGroup, view);//TODO 这里需要排查一下，是否直接移除没有问题？
        }
        return this;
    }

    /**
     * 移除所有view
     *
     * @return
     */
    public UDViewGroup removeAllViews() {
        final ViewGroup viewGroup = getView();
        LuaViewUtil.removeAllViews(viewGroup);
        return this;
    }

    /**
     * 提供View的构造环境，注意，callback里面不能有异步操作，否则操作的时序上会混乱
     *
     * @param callback
     * @return
     */
    public UDViewGroup children(LuaFunction callback) {
        if (getView() instanceof ILVViewGroup) {
            getGlobals().saveContainer((ILVViewGroup) getView());
            LuaUtil.callFunction(callback, this);
            getGlobals().restoreContainer();
        }
        return this;
    }



    public UDViewGroup setChildNodeViews(ArrayList<UDView> childNodeViews) {
        final ViewGroup viewGroup = getView();
        if (viewGroup != null && childNodeViews != null) {
            if (viewGroup instanceof ILVViewGroup) {
                ((ILVViewGroup) viewGroup).setChildNodeViews(childNodeViews);
            }
        }

        return this;
    }
}
