package com.taobao.luaview.layout;

import android.view.View;

import com.facebook.csslayout.CSSNode;

/**
 * Created by xiekaiwei on 16/3/14.
 */
public class FlexboxNodeView {
    public CSSNode node;
    public View view;

    public FlexboxNodeView(View view, CSSNode node) {
        this.node = node;
        this.view = view;
    }
}
