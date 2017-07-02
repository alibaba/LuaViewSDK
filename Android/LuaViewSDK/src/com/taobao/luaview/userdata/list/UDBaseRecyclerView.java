/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.list;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.taobao.luaview.global.Constants;
import com.taobao.luaview.userdata.constants.UDPinned;
import com.taobao.luaview.util.LuaUtil;
import com.taobao.luaview.view.LVRecyclerView;
import com.taobao.luaview.view.LVRefreshRecyclerView;
import com.taobao.luaview.view.recyclerview.LVRecyclerViewHolder;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * UDBaseRecyclerView 封装
 *
 * @author song
 * @date 15/10/23
 */
public abstract class UDBaseRecyclerView<T extends ViewGroup> extends UDBaseListOrRecyclerView<T> {

    private static final int DEFAULT_MAX_SPAN = 1;

    private SparseIntArray mSpanSize;

    // 用于添加吸顶视图的容器
    private ViewGroup mPinnedContainer;

    private View mCurrentPinnedView;

    private int mCurrentPinnedPosition = -1;

    private boolean mHasPinnedCell = false;

    // 缓存真实的Pinned Cell Name
    private SparseArray<String> mPinnedPositionCellId = new SparseArray<String>();

    // 缓存被Pinned标记的position
    public SparseBooleanArray mIsPinnedSparseArray = new SparseBooleanArray();
    // 缓存被pinned标记的viewType, position
    public SparseIntArray mPinnedViewTypePosition = new SparseIntArray();
    // 缓存被pinned标记的position,Holder
    public SparseArray<LVRecyclerViewHolder> mPinnedPositionHolder = new SparseArray<LVRecyclerViewHolder>();

    public UDBaseRecyclerView(T view, Globals globals, LuaValue metaTable, Varargs initParams) {
        super(view, globals, metaTable, initParams);
    }

    public abstract LVRecyclerView getLVRecyclerView();

    /**
     * mPinnedViewTypePosition是onCreateViewHolder时缓存的,不能清除
     */
    private void restore() {
        mHasPinnedCell = false;
        mIsPinnedSparseArray.clear();
        mPinnedPositionCellId.clear();
        mPinnedViewTypePosition.clear();
    }

    /**
     * notify data changed (section, row) in java
     *
     * @param section
     * @param row
     * @return
     */
    @Override
    public UDBaseRecyclerView reload(Integer section, Integer row) {
        restore();

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

                        pinned(lvRecyclerView);
                    }
                });
            }
        }
    }

    // TODO: 11/15/16 处理itemView之前有spacing的情况
    private void pinned(LVRecyclerView lvRecyclerView) {
        if (!mHasPinnedCell)
            return;

        if (mPinnedContainer == null) {
            mPinnedContainer = new FrameLayout(lvRecyclerView.getContext());
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            ViewGroup parent = (ViewGroup) lvRecyclerView.getParent();
            if (parent instanceof LVRefreshRecyclerView) {
                // RefreshCollectionView
                params.leftMargin = (int) parent.getX();
                params.topMargin = (int) parent.getY();
                ((ViewGroup) parent.getParent()).addView(mPinnedContainer, params);
            } else {
                // CollectionView
                params.leftMargin = (int) lvRecyclerView.getX();
                params.topMargin = (int) lvRecyclerView.getY();
                parent.addView(mPinnedContainer, params);
            }
        }

        int firstVisiblePosition = findFirstVisiblePosition(lvRecyclerView.getLayoutManager());
        // 从firstVisiblePosition位置开始递减查找上一个pinned position
        int pinnedViewPosition = findPinnedViewPositionDecrease(firstVisiblePosition);
        if (pinnedViewPosition >= 0 && mCurrentPinnedPosition != pinnedViewPosition) {
            if (mPinnedPositionHolder.get(pinnedViewPosition) != null) {
                ViewGroup itemView = (ViewGroup) mPinnedPositionHolder.get(pinnedViewPosition).itemView;
                View child = itemView.getChildAt(0);
                if (child != null) {
                    // 从itemView移除child之前,先设置其与child一样的宽高占位。
                    itemView.getLayoutParams().width = child.getLayoutParams().width;
                    itemView.getLayoutParams().height = child.getLayoutParams().height;
                    itemView.removeView(child);
                    mPinnedContainer.addView(child);
                    if (mCurrentPinnedView != null) {
                        mCurrentPinnedView.setVisibility(View.GONE);
                    }
                    mCurrentPinnedView = child;
                } else {
                    // 从(pinnedViewPosition + 1)位置开始递增查找下一个pinned position
                    int nextPinnedPosition = findPinnedViewPositionIncrease(pinnedViewPosition + 1);
                    ViewGroup parentItemView = (ViewGroup) mPinnedPositionHolder.get(nextPinnedPosition).itemView;
                    View pinnedView = mPinnedContainer.getChildAt(mPinnedContainer.getChildCount() - 1);
                    mPinnedContainer.removeView(pinnedView);
                    parentItemView.addView(pinnedView);
                    mCurrentPinnedView = mPinnedContainer.getChildAt(mPinnedContainer.getChildCount() - 1);
                    if (mCurrentPinnedView != null) {
                        mCurrentPinnedView.setVisibility(View.VISIBLE);
                    }
                }

                mCurrentPinnedPosition = pinnedViewPosition;
            }
        }

        // 第一个吸顶视图被移除的情况,亦即列表恢复没有吸顶视图的状态。
        if (pinnedViewPosition == -1 && mCurrentPinnedPosition != -1) {
            View subview = mPinnedContainer.getChildAt(mPinnedContainer.getChildCount() - 1);
            mPinnedContainer.removeView(subview);
            // 从position 0开始找第一个pinned标记的itemView,并把最后一个吸顶视图添加回到它的原本位置
            int firstPinnedPosition = findPinnedViewPositionIncrease(0);
            ViewGroup parentItemView = (ViewGroup) mPinnedPositionHolder.get(firstPinnedPosition).itemView;
            parentItemView.addView(subview);
            // 列表恢复没有吸顶视图的状态
            mCurrentPinnedPosition = -1;
            mCurrentPinnedView = null;
        }

        // 处理吸顶视图切换时的位移效果
        if (mPinnedContainer != null && mCurrentPinnedPosition != -1) {
            View targetView = lvRecyclerView.findChildViewUnder(mPinnedContainer.getMeasuredWidth() / 2, mPinnedContainer.getMeasuredHeight() + 1);
            if (targetView != null) {
                boolean isPinned = ((Boolean) targetView.getTag(Constants.RES_LV_TAG_PINNED)).booleanValue();
                if (isPinned && targetView.getTop() > 0) {
                    if (pinnedViewPosition != -1) {
                        int deltaY = targetView.getTop() - mPinnedContainer.getMeasuredHeight();
                        if (deltaY < (lvRecyclerView.getMiniSpacing() - mPinnedContainer.getMeasuredHeight())) {
                            // 防止设置了spacing的时候,在这个范围内mPinnedContainer被位移到top之上,而itemView是空白的现象
                            mPinnedContainer.setTranslationY(0);
                        } else {
                            mPinnedContainer.setTranslationY(deltaY);
                        }
                    }
                } else {
                    mPinnedContainer.setTranslationY(0);
                }
            } else {
                mPinnedContainer.setTranslationY(0);
            }
        }

        // Fix bug: 解决调用scrollToPositionWithOffset()方法时,吸顶容器没有被绘制的问题。
        if (mPinnedContainer != null) {
            mPinnedContainer.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPinnedContainer.requestLayout();
                }
            }, 1);
        }
    }

    /**
     * 从传入位置递增找出Pinned的位置
     *
     * @param fromPosition
     * @return int
     */
    private int findPinnedViewPositionIncrease(int fromPosition) {
        for (int position = fromPosition; position < this.getTotalCount(); position++) {
            // 位置递减，只要查到位置是Pinned标示，立即返回此位置
            if (this.mIsPinnedSparseArray.get(position)) {
                return position;
            }
        }

        return -1;
    }

    /**
     * 从传入位置递减找出Pinned的位置
     *
     * @param fromPosition
     * @return int
     */
    private int findPinnedViewPositionDecrease(int fromPosition) {
        for (int position = fromPosition; position >= 0; position--) {
            // 位置递减，只要查到位置是Pinned标示，立即返回此位置
            if (this.mIsPinnedSparseArray.get(position)) {
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

    /**
     * 由于该函数的特殊性,有则获取无则生成。
     * 对于生成,有Pinned.YES标记的Id,会先加后缀(".PINNED"+position),再存放到mIdCache中;
     * 对于获取,用mPinnedPositionCellId缓存的Lua定义的真实的Id。
     *
     * @param position
     * @param section
     * @param row
     * @return
     */
    @Override
    protected String getId(int position, int section, int row) {
        final String cacheId = mIdCache != null ? mIdCache.get(position) : null;
        if (cacheId != null) {
            if (this.mIsPinnedSparseArray.get(position)) {
                // 获取CellId的时候,要用lua定义的真正的Id
                return mPinnedPositionCellId.get(position);
            }
            return cacheId;
        } else {
            String id = null;
            Varargs args = LuaUtil.invokeFunction(mCellDelegate.get("Id"), LuaUtil.toLuaInt(section), LuaUtil.toLuaInt(row));
            if (args != null) {
                if (args.narg() > 1) {
                    if (args.arg(2).toint() == UDPinned.PINNED_YES) {
                        mHasPinnedCell = true;
                        mIsPinnedSparseArray.put(position, true);
                        id = args.arg(1).optjstring("");
                        /**
                         * 构造唯一的id,使得在lua用同一种Cell作为多个position的PinnedCell时,也会有不同的viewType.
                         * 见 {@link UDBaseRecyclerView#getItemViewType(int)}
                         */
                        mPinnedPositionCellId.put(position, id);
                        id = new StringBuffer(id).append(".PINNED").append(position).toString();
                    } else {
                        id = args.arg(1).optjstring("");
                    }
                } else { // 兼容旧版本的写法,只有一个String参数的情况
                    id = ((LuaValue) args).optjstring("");
                }
            }

            if (mIdCache != null) {
                mIdCache.put(position, id);
            }

            return id;
        }
    }

    /**
     * 根据位置获取item的viewType
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        int viewType = 0;
        String viewTypeName = mIdCache.get(position);//得到坑位类型名称
        if (viewTypeName == null) {
            // 已经有该position的viewTypeName则直接用,没有则调用getId函数生成并存入mIdCache,并返回。
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

        if (this.mIsPinnedSparseArray.get(position)) {
            this.mPinnedViewTypePosition.put(viewType, position);
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
            if (mHasPinnedCell && this.mPinnedViewTypePosition.get(viewType, -1) != -1) {
                // 获取CellId的时候,要用Lua层定义的正确的Id
                return hasCellFunction(mPinnedPositionCellId.get(mPinnedViewTypePosition.get(viewType)), "Size");
            }

            return hasCellFunction(id, "Size");
        }
        return false;
    }
}