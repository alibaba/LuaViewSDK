package com.taobao.luaview.view.recyclerview.layout;

import android.support.v7.widget.GridLayoutManager;

import com.taobao.luaview.view.LVRecyclerView;

/**
 * Span for RecyclerView
 * @author song
 * @date 15/12/3
 */
public class LVGridSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {
    private LVRecyclerView mRecyclerView;

    public LVGridSpanSizeLookup(LVRecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
    }

    @Override
    public int getSpanSize(int position) {
//        LogUtil.d("yesong", "getSpanSize", position, mRecyclerView != null ? mRecyclerView.getSpanSize(position) : 0);
        return mRecyclerView != null ? mRecyclerView.getSpanSize(position) : 0;
    }
}
