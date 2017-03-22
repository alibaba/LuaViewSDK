/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.view;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.facebook.csslayout.CSSLayoutContext;
import com.facebook.csslayout.CSSNode;
import com.facebook.csslayout.Spacing;
import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.userdata.ui.UDViewGroup;
import com.taobao.luaview.util.LuaViewUtil;
import com.taobao.luaview.view.foreground.ForegroundRelativeLayout;
import com.taobao.luaview.view.interfaces.ILVViewGroup;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.ArrayList;

/**
 * LuaView-ViewGroup
 * 容器类
 *
 * @author song
 * @date 15/8/20
 */
public class LVViewGroup<T extends UDViewGroup> extends ForegroundRelativeLayout implements ILVViewGroup {
    protected T mLuaUserdata;

    /**
     * Flexbox attributes
     */
    private ArrayList<UDView> mChildNodeViews;
    private CSSNode mCSSNode;
    private CSSLayoutContext mLayoutContext;

    public LVViewGroup(Globals globals, LuaValue metaTable, Varargs varargs) {
        this(globals != null ? globals.getContext() : null, globals, metaTable, varargs);
    }

    public LVViewGroup(Context context, Globals globals, LuaValue metaTable, Varargs varargs) {
        super(context);
        this.mLuaUserdata = createUserdata(globals, metaTable, varargs);
        //改在UDViewGroup中设置，减少影响面
//        this.setFocusableInTouchMode(true);//需要设置，否则onKeyUp等事件无法监听，排查是否会带来其他问题(点击的时候需要点击两下)
//        this.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
//        this.setClipChildren(false);
    }


    public CSSNode getCssNode() {
        if (mCSSNode == null) {
            mCSSNode = new CSSNode();
        }

        return mCSSNode;
    }

    private CSSLayoutContext getLayoutContext() {
        if (mLayoutContext == null) {
            mLayoutContext = new CSSLayoutContext();
        }

        return mLayoutContext;
    }

    /**
     * create user data
     *
     * @param globals
     * @param metaTable
     * @param varargs
     * @return
     */
    public T createUserdata(Globals globals, LuaValue metaTable, Varargs varargs) {
        return (T) (new UDViewGroup(this, globals, metaTable, varargs));
    }

    @Override
    public T getUserdata() {
        return mLuaUserdata;
    }

    public void show() {
        LVViewGroup.this.setVisibility(View.VISIBLE);
    }

    public void hide() {
        LVViewGroup.this.setVisibility(View.GONE);
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
        if (mLuaUserdata != null) {
            mLuaUserdata.callOnLayout();
        }
    }

    /**
     * Flexbox account
     */
    public void setChildNodeViews(ArrayList<UDView> childNodeViews) {
        // diff old and new
        if (mChildNodeViews == childNodeViews) {
            return;
        }

        // remove all the old views
        clearChildNodeViews();

        // set the new nodes
        mChildNodeViews = childNodeViews;

        // enum array and add into it
        generateNodeViewTree();
    }

    private void clearChildNodeViews() {
        if (mChildNodeViews == null) {
            return;
        }

        int childNodeViewsCount = mChildNodeViews.size();
        for (int i = 0; i < childNodeViewsCount; i++) {
            UDView nodeView = mChildNodeViews.get(i);
            View view = nodeView.getView();
            removeView(view);
        }

        getCssNode().resetChildren();
    }

    private void generateNodeViewTree() {
        if (mChildNodeViews == null) {
            return;
        }

        int childNodeViewsCount = mChildNodeViews.size();
        for (int i = 0; i < childNodeViewsCount; i++) {
            UDView nodeView = mChildNodeViews.get(i);
            View view = nodeView.getView();

            LuaViewUtil.addView(this, view, null);
            getCssNode().addChild(nodeView.getCssNode());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mChildNodeViews == null
                || mChildNodeViews.size() == 0
                || getCssNode().getParent() != null) {
            return;
        }

        measureChildNode(widthMeasureSpec, heightMeasureSpec);
        getCssNode().calculateLayout(getLayoutContext());
        assignNodeLayoutParams();
    }

    private void measureChildNode(int widthMeasureSpec, int heightMeasureSpec) {
        int childNodeViewsCount = mChildNodeViews.size();
        for (int i = 0; i < childNodeViewsCount; i++) {
            UDView nodeView = mChildNodeViews.get(i);
            View view = nodeView.getView();
            CSSNode node = nodeView.getCssNode();

            if (node.getSizeToFit()) {
                int margins = (int) (node.getMargin().get(Spacing.LEFT) + node.getMargin().get(Spacing.RIGHT));
                measureChild(view, widthMeasureSpec - margins, heightMeasureSpec);

                node.setNoDirtyStyleWidth(view.getMeasuredWidth());
                node.setNoDirtyStyleHeight(view.getMeasuredHeight());
            }

            if (view instanceof LVViewGroup) {
                LVViewGroup viewGroup = (LVViewGroup) view;
                if (viewGroup.getCssNode().getChildCount() > 0) {
                    viewGroup.measureChildNode(widthMeasureSpec, heightMeasureSpec);
                }
            }
        }
    }

    private void assignNodeLayoutParams() {
        int childNodeViewsCount = mChildNodeViews.size();
        for (int i = 0; i < childNodeViewsCount; i++) {
            UDView nodeView = mChildNodeViews.get(i);

            View view = nodeView.getView();
            CSSNode node = nodeView.getCssNode();

            if (view != null && node != null) {
                int x = (int) node.getLayoutX();
                int y = (int) node.getLayoutY();
                int width = (int) node.getLayoutWidth();
                int height = (int) node.getLayoutHeight();

                RelativeLayout.LayoutParams lParams = (LayoutParams) view.getLayoutParams();
                if (lParams == null) {
                    lParams = new RelativeLayout.LayoutParams(width, height);
                } else {
                    lParams.width = width;
                    lParams.height = height;
                }

                lParams.setMargins(x, y, 0, 0);
                view.setLayoutParams(lParams);

                if (view instanceof LVViewGroup) {
                    LVViewGroup viewGroup = (LVViewGroup) view;
                    if (viewGroup.getCssNode().getChildCount() > 0) {
                        viewGroup.assignNodeLayoutParams();
                    }
                }
            }
        }
    }
}
