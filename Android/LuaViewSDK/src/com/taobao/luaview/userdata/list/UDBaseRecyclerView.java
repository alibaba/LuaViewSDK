package com.taobao.luaview.userdata.list;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.SparseIntArray;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.taobao.luaview.util.LogUtil;
import com.taobao.luaview.util.LuaUtil;
import com.taobao.luaview.view.LVRecyclerView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

/**
 * UDBaseRecyclerView 封装
 *
 * @author song
 * @date 15/10/23
 */
public abstract class UDBaseRecyclerView<T extends ViewGroup> extends UDBaseListOrRecyclerView<T> {

    private static final int DEFAULT_MAX_SPAN = 1;

    private SparseIntArray mSpanSize;

    public UDBaseRecyclerView(T view, Globals globals, LuaValue metaTable, LuaValue initParams) {
        super(view, globals, metaTable, initParams);
    }

    public abstract LVRecyclerView getLVRecyclerView();

    @Override
    public UDBaseRecyclerView reload() {
        final LVRecyclerView recyclerView = getLVRecyclerView();
        if (recyclerView != null) {
            init();//重新初始化数据
            recyclerView.updateMaxSpanCount();
            if (recyclerView.getLVAdapter() != null) {
                recyclerView.getLVAdapter().notifyDataSetChanged();
            }
        }
        return this;
    }

    @Override
    public void initOnScrollCallback(final T view) {
        if (view instanceof LVRecyclerView) {
            final LVRecyclerView lvRecyclerView = (LVRecyclerView) view;
            if (!mCallback.isnil() || mLazyLoad) {
                lvRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                        updateAllChildScrollState(recyclerView, scrollState);

                        if (!mCallback.isnil()) {
                            switch (scrollState) {
                                case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL: {
                                    final int itemPosition = lvRecyclerView.getFirstVisiblePosition();
                                    final int section = getSectionByPosition(itemPosition);
                                    final int row = getRowInSectionByPosition(itemPosition);
                                    LuaUtil.callFunction(LuaUtil.getFunction(mCallback, "ScrollBegin", "scrollBegin"), LuaUtil.toLuaInt(section), LuaUtil.toLuaInt(row));
                                    break;
                                }
                                case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                                    break;
                                case AbsListView.OnScrollListener.SCROLL_STATE_IDLE: {
                                    final int itemPosition = lvRecyclerView.getFirstVisiblePosition();
                                    final int section = getSectionByPosition(itemPosition);
                                    final int row = getRowInSectionByPosition(itemPosition);
                                    LuaUtil.callFunction(LuaUtil.getFunction(mCallback, "ScrollEnd", "scrollEnd"), LuaUtil.toLuaInt(section), LuaUtil.toLuaInt(row));
                                    break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        if (!mCallback.isnil()) {
                            final int itemPosition = lvRecyclerView.getFirstVisiblePosition();
                            final int section = getSectionByPosition(itemPosition);
                            final int row = getRowInSectionByPosition(itemPosition);
                            LuaUtil.callFunction(LuaUtil.getFunction(mCallback, "Scrolling", "scrolling"), LuaUtil.toLuaInt(section), LuaUtil.toLuaInt(row), valueOf(lvRecyclerView.getVisibleItemCount()));
                        }
                    }
                });
            }
        }
    }

    /**
     * TODO 支持offset
     *
     * @param offset
     * @param animate 是否动画
     * @return
     */
    @Override
    public UDBaseListOrRecyclerView scrollToTop(int offset, boolean animate) {
        final LVRecyclerView lv = getLVRecyclerView();
        if (lv != null) {
            if (animate) {
                if (lv.getFirstVisiblePosition() > 7) {//hack fast scroll
                    lv.scrollToPosition(7);
                }
                lv.smoothScrollToPosition(0);
            } else {
                if (lv.getLayoutManager() instanceof LinearLayoutManager) {
                    ((LinearLayoutManager) lv.getLayoutManager()).scrollToPositionWithOffset(0, offset);
                } else if (lv.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                    ((StaggeredGridLayoutManager) lv.getLayoutManager()).scrollToPositionWithOffset(0, offset);
                } else {
                    lv.scrollToPosition(0);
                }
            }
        }
        return this;
    }


    /**
     * TODO 支持offset
     *
     * @param section
     * @param rowInSection
     * @param offset
     * @param animate
     * @return
     */
    @Override
    public UDBaseListOrRecyclerView scrollToItem(int section, int rowInSection, int offset, boolean animate) {
        final LVRecyclerView recyclerView = getLVRecyclerView();
        if (recyclerView != null) {
            if (animate) {
                recyclerView.smoothScrollToPosition(getPositionBySectionAndRow(section, rowInSection));
            } else {
                if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                    ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(getPositionBySectionAndRow(section, rowInSection), offset);
                } else if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                    ((StaggeredGridLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(getPositionBySectionAndRow(section, rowInSection), offset);
                } else {
                    recyclerView.scrollToPosition(getPositionBySectionAndRow(section, rowInSection));
                }
            }
        }
        return this;
    }

    @Override
    public UDBaseListOrRecyclerView setMiniSpacing(int spacing) {
        final LVRecyclerView view = getLVRecyclerView();
        if (view != null) {
            view.setMiniSpacing(spacing);
        }
        return this;
    }

    @Override
    public int getMiniSpacing() {
        return getView() instanceof LVRecyclerView ? ((LVRecyclerView) getView()).getMiniSpacing() : 0;
    }


    /**
     * 获取最大的spanCount，默认为1
     * 1. 先读取所有cell的size，找到最小cell，能放多少个该cell即为最大列数
     * 2. 初始化每个item的spanSize
     * TODO 使用另外的算法：先找到最小的gap，然后算出spancount的方式。找最小gap的方式需要遍历一遍，找到每行的最小空隙，如果最小空隙为0则找最小的cell为最小的gap。（该方式的问题是填不满的行会有间隙）
     *
     * @return
     */
    public int getMaxSpanCount() {
        if (getWidth() > 0) {
            final int maxWidth = getWidth();
            final int maxSpanCount = Math.max(1, maxWidth);
            initSpanSize(maxSpanCount);
            return maxSpanCount;
        }
        return DEFAULT_MAX_SPAN;
    }

    /**
     * 初始化spanCount，并保存
     *
     * @param maxSpanCount
     */
    private void initSpanSize(int maxSpanCount) {
        mSpanSize = new SparseIntArray();
        final int totalCount = getTotalCount();
        for (int i = 0; i < totalCount; i++) {
            final int[] size = callCellSize(LuaValue.NIL, i);
            final int spanSize = Math.max(0, Math.min(maxSpanCount, size[0]));//0 <= spanSize <= maxSpanCount
            mSpanSize.put(i, spanSize);
        }
    }

    /**
     * get cached span size
     *
     * @param position
     * @return
     */
    public int getSpanSize(int position) {
        if (mSpanSize != null) {
            return mSpanSize.get(position);
        }
        return DEFAULT_MAX_SPAN;
    }


    //------------------------------------has cell size---------------------------------------------

    /**
     * 是否有size的定义
     *
     * @param viewType
     * @return
     */
    public boolean hasCellSize(int viewType) {
        final String id = getItemViewTypeName(viewType);
        if (id != null) {
            return hasCellFunction(id, "Size");
        }
        return false;
    }
}