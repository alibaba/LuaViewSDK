package com.taobao.luaview.userdata.list;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.RelativeLayout;

import com.taobao.android.luaview.R;
import com.taobao.luaview.userdata.constants.UDPinned;
import com.taobao.luaview.util.LogUtil;
import com.taobao.luaview.util.LuaUtil;
import com.taobao.luaview.view.LVRecyclerView;
import com.taobao.luaview.view.LVRefreshRecyclerView;
import com.taobao.luaview.view.recyclerview.LVRecyclerViewHolder;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * UDBaseRecyclerView 封装
 *
 * @author song
 * @date 15/10/23
 */
public abstract class UDBaseRecyclerView<T extends ViewGroup> extends UDBaseListOrRecyclerView<T> {

    private static final int DEFAULT_MAX_SPAN = 1;

    private SparseIntArray mSpanSize;

    private ViewGroup mRecyclerViewParent;

    private int mCurrentPinnedPosition = -1;

    private View mCurrentPinnedView;

    private boolean mHasPinnedItemView = false;

    //////// public variable

    // 缓存所有itemView是否被Pinned标记的列表
    public List<Boolean> mIsItemViewPinnedList = new ArrayList<Boolean>();
    // 哈希被pinned标记的viewType, position
    public HashMap<Integer, Integer> mPinnedViewTypePositionMaps = new HashMap<Integer, Integer>();
    // 哈希被pinned标记的position,Holder
    public HashMap<Integer, LVRecyclerViewHolder> mPinnedPositionHolderMaps = new HashMap<Integer, LVRecyclerViewHolder>();

    public UDBaseRecyclerView(T view, Globals globals, LuaValue metaTable, Varargs initParams) {
        super(view, globals, metaTable, initParams);
    }

    public abstract LVRecyclerView getLVRecyclerView();

    /**
     * notify data changed (section, row) in java
     *
     * @param section
     * @param row
     * @return
     */
    @Override
    public UDBaseRecyclerView reload(Integer section, Integer row) {
        mHasPinnedItemView = false;
        final LVRecyclerView recyclerView = getLVRecyclerView();
        if (recyclerView != null) {
            final RecyclerView.Adapter adapter = recyclerView.getLVAdapter();
            if (adapter != null) {
                int diffSectionCount = getDiffSectionCount();

                if (section == null || diffSectionCount != 0) {//如果 section无值，或者section数量变动则更新所有
                    refreshState(recyclerView);
                    adapter.notifyDataSetChanged();
                } else {//如果传了section，row，则表示要更新部分数据
                    int diffTotalCount = getDiffTotalCount();//total count diff
                    boolean isChanged = diffTotalCount == 0;//数据变更，数量未变更
                    boolean isInserted = diffTotalCount > 0;//数量增加
                    boolean isRemoved = diffTotalCount < 0;//数量减少

                    if (row == null) {//row is null, notify whole section
                        int start = getPositionBySectionAndRow(section, 0);
                        int currentRowCount = getRowCount(section);
                        if (isChanged) {//更新整个section，count不变，数据变
                            refreshState(recyclerView);
                            adapter.notifyItemRangeChanged(start, currentRowCount);
                        } else if (isInserted) {//更新整个section，count增加
                            int newRowCount = getRawRowCount(section);
                            int count = Math.abs(newRowCount - currentRowCount);//新增count
                            refreshState(recyclerView);
                            adapter.notifyItemRangeInserted(start, count);
                        } else if (isRemoved) {//更新整个section，count减少
                            int newRowCount = getRawRowCount(section);
                            int count = Math.abs(newRowCount - currentRowCount);//新增count
                            refreshState(recyclerView);
                            adapter.notifyItemRangeRemoved(start, count);
                        }
                    } else {//row not null, notify row
                        int pos = getPositionBySectionAndRow(section, row);
                        refreshState(recyclerView);
                        if (isChanged) {//更新某个元素
                            adapter.notifyItemChanged(pos);
                        } else if (isInserted) {//插入一个元素
                            adapter.notifyItemInserted(pos);
                        } else if (isRemoved) {//减少一个元素
                            adapter.notifyItemRemoved(pos);
                        }
                    }
                }
            }
        }
        return this;
    }

    /**
     * 更新列表状态，重新更新数据，元素间隔等
     *
     * @param recyclerView
     */
    private void refreshState(LVRecyclerView recyclerView) {
        init();//重新初始化数据
        recyclerView.updateMaxSpanCount();
    }

    @Override
    public void initOnScrollCallback(final T view) {
        if (view instanceof LVRecyclerView) {
            final LVRecyclerView lvRecyclerView = (LVRecyclerView) view;
            if (LuaUtil.isValid(mCallback) || mLazyLoad) {
                lvRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                        updateAllChildScrollState(recyclerView, scrollState);

                        if (LuaUtil.isValid(mCallback)) {
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
                        if (LuaUtil.isValid(mCallback)) {
                            final int itemPosition = lvRecyclerView.getFirstVisiblePosition();
                            final int section = getSectionByPosition(itemPosition);
                            final int row = getRowInSectionByPosition(itemPosition);
                            LuaUtil.callFunction(LuaUtil.getFunction(mCallback, "Scrolling", "scrolling"), LuaUtil.toLuaInt(section), LuaUtil.toLuaInt(row), valueOf(lvRecyclerView.getVisibleItemCount()));
                        }

                        layoutPinnedView(lvRecyclerView);
                    }
                });
            }
        }
    }

    private void layoutPinnedView(LVRecyclerView lvRecyclerView) {
        if (!mHasPinnedItemView)
            return;

        if (mRecyclerViewParent == null) {
            mRecyclerViewParent = (ViewGroup) lvRecyclerView.getParent();
        }

        int firstVisiblePosition = findFirstVisiblePosition(lvRecyclerView.getLayoutManager());
        int pinnedViewPosition = findPinnedViewPositionDecrease(firstVisiblePosition);
        if (pinnedViewPosition >= 0 && mCurrentPinnedPosition != pinnedViewPosition) {
            ViewGroup itemView = (ViewGroup)mPinnedPositionHolderMaps.get(pinnedViewPosition).itemView;
            View child = itemView.getChildAt(0);
            if (child != null) {
                itemView.getLayoutParams().width = child.getLayoutParams().width;
                itemView.getLayoutParams().height = child.getLayoutParams().height;
                itemView.removeView(child);
                mRecyclerViewParent.addView(child);
                mCurrentPinnedView = child;
                mCurrentPinnedPosition = pinnedViewPosition;
            } else {
                View subview = mRecyclerViewParent.getChildAt(mRecyclerViewParent.getChildCount()-1);
                mRecyclerViewParent.removeView(subview);
                int nextPinnedPosition = findPinnedViewPositionIncrease(pinnedViewPosition+1);
                ViewGroup parentView = (ViewGroup)mPinnedPositionHolderMaps.get(nextPinnedPosition).itemView;
                parentView.addView(subview);
                mCurrentPinnedView = mRecyclerViewParent.getChildAt(mRecyclerViewParent.getChildCount()-1);
                mCurrentPinnedPosition = findPinnedViewPositionDecrease(firstVisiblePosition-1);
            }
        }

        // 列表的第一个PinnedCell解除Pinned的时候
        if (pinnedViewPosition == -1 && mCurrentPinnedPosition != -1) {
            View subview = mRecyclerViewParent.getChildAt(mRecyclerViewParent.getChildCount()-1);
            mRecyclerViewParent.removeView(subview);
            int nextPinnedPosition = findPinnedViewPositionIncrease(0);
            ViewGroup parentView = (ViewGroup)mPinnedPositionHolderMaps.get(nextPinnedPosition).itemView;
            parentView.addView(subview);
            mCurrentPinnedView = null;
            mCurrentPinnedPosition = -1;
        }

        if (mCurrentPinnedView != null) {
            View targetView = lvRecyclerView.findChildViewUnder(mCurrentPinnedView.getMeasuredWidth() / 2, mCurrentPinnedView.getMeasuredHeight() + 1);
            if (targetView != null) {
                boolean isPinned = ((Boolean) targetView.getTag(R.id.lv_tag_model)).booleanValue();
                if (isPinned) {
                    if (targetView.getTop() > 0) {
                        if (pinnedViewPosition != -1) {
                            int deltaY = targetView.getTop() - mCurrentPinnedView.getMeasuredHeight();
                            mCurrentPinnedView.setTranslationY(deltaY);
                        }
                    } else {
                        mCurrentPinnedView.setTranslationY(0);
                    }
                } else {
                    mCurrentPinnedView.setTranslationY(0);
                }
            }
        }
    }

    /**
     * 从传入位置递增找出标签的位置
     *
     * @param fromPosition
     * @return int
     */
    private int findPinnedViewPositionIncrease(int fromPosition) {
        for (int position = fromPosition; position < this.mIsItemViewPinnedList.size(); position++) {
            // 位置递减，只要查到位置是标签，立即返回此位置
            if (this.mIsItemViewPinnedList.get(position).booleanValue()) {
                return position;
            }
        }

        return -1;
    }

    /**
     * 从传入位置递减找出标签的位置
     *
     * @param fromPosition
     * @return int
     */
    private int findPinnedViewPositionDecrease(int fromPosition) {
        for (int position = fromPosition; position >= 0; position--) {
            // 位置递减，只要查到位置是标签，立即返回此位置
            if (this.mIsItemViewPinnedList.get(position).booleanValue()) {
                return position;
            }
        }

        return -1;
    }

    /**
     * 找出第一个可见的Item的位置
     *
     * @param layoutManager
     * @return
     */
    private int findFirstVisiblePosition(RecyclerView.LayoutManager layoutManager) {
        int firstVisiblePosition = 0;
        if (layoutManager instanceof GridLayoutManager) {
            firstVisiblePosition = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
        } else if (layoutManager instanceof LinearLayoutManager) {
            firstVisiblePosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
            ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(into);
            firstVisiblePosition = Integer.MAX_VALUE;
            for (int pos : into) {
                firstVisiblePosition = Math.min(pos, firstVisiblePosition);
            }
        }
        return firstVisiblePosition;
    }

    @Override
    protected String getId(int position, int section, int row) {
        final String cacheId = mIdCache != null ? mIdCache.get(position) : null;
        if (cacheId != null) {
            if (mIsItemViewPinnedList.get(position).booleanValue()) {
                // 获取CellId的时候,要用lua定义的真正的Id
                return cacheId.split("\\.PINNED")[0];
            }
            return cacheId;
        } else {
            String id = null;
            Varargs args = LuaUtil.invokeFunction(mCellDelegate.get("Id"), LuaUtil.toLuaInt(section), LuaUtil.toLuaInt(row));
            if (args != null) {
                if (args.narg() > 1) {
                    if (args.arg(2).toint() == UDPinned.PINNED_YES) {
                        mHasPinnedItemView = true;
                        if (position < mIsItemViewPinnedList.size()) {
                            mIsItemViewPinnedList.set(position, true);
                        } else {
                            mIsItemViewPinnedList.add(position, true);
                        }
                        id = args.arg(1).optjstring("");
                        // 构造唯一的id加到mIdCache中,这样才能使得在lua用同一种Cell作为多个position的PinnedCell时,也会有不同的viewType,见getItemViewType(int)函数
                        id = new StringBuffer(id).append(".PINNED").append(position).toString();
                    } else {
                        if (position < mIsItemViewPinnedList.size()) {
                            mIsItemViewPinnedList.set(position, false);
                        } else {
                            mIsItemViewPinnedList.add(position, false);
                        }
                        id = args.arg(1).optjstring("");
                    }
                } else { // 兼容旧版本的写法,只有一个String参数的情况
                    id = ((LuaValue)args).optjstring("");
                    if (position < mIsItemViewPinnedList.size()) {
                        mIsItemViewPinnedList.set(position, false);
                    } else {
                        mIsItemViewPinnedList.add(position, false);
                    }
                }
            }

            if (mIdCache != null) {
                mIdCache.put(position, id);
            }

            return id;
        }
    }

    /**
     * 根据位置获取 item 的type id
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        int viewType = 0;
        String viewTypeName = mIdCache.get(position);//得到坑位类型名称
        if (viewTypeName == null) {
            viewTypeName = getId(position);
        }
        if (this.mViewTypeMap != null) {
            if (!this.mViewTypeMap.containsKey(viewTypeName)) {
                final int index = this.mViewTypeMap.size();
                this.mViewTypeMap.put(viewTypeName, index);
                this.mViewTypeNameMap.put(index, viewTypeName);
                viewType = index;
            } else {
                viewType = this.mViewTypeMap.get(viewTypeName);
            }
        }
        if (this.mIsItemViewPinnedList.get(position).booleanValue()) {
            if (!this.mPinnedViewTypePositionMaps.containsKey(viewType)) {
                this.mPinnedViewTypePositionMaps.put(viewType, position);
            }
        }
        return viewType;
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
            if (mPinnedViewTypePositionMaps.containsKey(viewType)) {
                // 获取CellId的时候,要用lua定义的真正的Id
                return hasCellFunction(id.split("\\.PINNED")[0], "Size");
            }
            return hasCellFunction(id, "Size");
        }
        return false;
    }
}