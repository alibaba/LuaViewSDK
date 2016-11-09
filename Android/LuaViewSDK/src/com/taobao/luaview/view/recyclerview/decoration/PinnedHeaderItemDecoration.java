package com.taobao.luaview.view.recyclerview.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Region;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.taobao.luaview.util.IdUtil;
import com.taobao.luaview.util.LogUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by tuoli on 11/2/16.
 *
 * <p>porting from https://github.com/oubowu/PinnedSectionItemDecoration</p>
 *
 * 主要改进点
 * 1. 当多个Pinned Header的可点击区域不一样的时候,要清除上一个的,只保留当前的
 * 2. 给Pinned Header里的节点设置自动生成的视图Id,而非在外部通过R文件设置
 * 3. 优化不同的Pinned Header切换时,反复调用createViewHolder的问题
 * 4. 去除分割线的绘制,由另一个Decoration绘制,RecyclerView允许设置多个Decoration
 * 5. 修复reload的时候,Pinned Header被绘制的bug
 *
 */

public class PinnedHeaderItemDecoration extends RecyclerView.ItemDecoration {

    private OnHeaderClickListener mHeaderClickListener;

    private boolean mDisableHeaderClick;

    private Map<Integer, Boolean> mPinnedViewTypes = new HashMap<Integer, Boolean>();

    // 缓存自动生成的Id
    private List<Integer> mClickIdList = new ArrayList<Integer>();

    // 哈希被Pinned标记的position与ViewHolder
    private HashMap<Integer, RecyclerView.ViewHolder> mViewHolderMaps = new HashMap<Integer, RecyclerView.ViewHolder>();

    private RecyclerView.Adapter mAdapter;

    // 缓存的标签
    private View mPinnedHeaderView;

    // 缓存的标签位置
    private int mPinnedHeaderPosition = -1;

    // 顶部标签的Y轴偏移值
    private int mPinnedHeaderOffset;

    // 用于锁定画布绘制范围
    private Rect mClipBounds;

    // 父布局的左间距
    private int mRecyclerViewPaddingLeft;
    // 父布局的顶间距
    private int mRecyclerViewPaddingTop;

    private int mHeaderLeftMargin;
    private int mHeaderTopMargin;
    private int mHeaderRightMargin;
    private int mHeaderBottomMargin;

    // 用于处理头部点击事件屏蔽与响应
    private OnItemTouchListener mItemTouchListener;

    private int mLeft;
    private int mTop;
    private int mRight;
    private int mBottom;

    private int mFirstVisiblePosition;

    private int mDataPositionOffset;

    private boolean mDisableDrawHeader;

    private RecyclerView mParent;

    // 当我们调用mRecyclerView.addItemDecoration()方法添加decoration的时候，RecyclerView在绘制的时候，去会绘制decorator，即调用该类的onDraw和onDrawOver方法，
    // 1.onDraw方法先于drawChildren
    // 2.onDrawOver在drawChildren之后，一般我们选择复写其中一个即可。
    // 3.getItemOffsets 可以通过outRect.set()为每个Item设置一定的偏移量，主要用于绘制Decorator。
    public PinnedHeaderItemDecoration() {
    }

    public void setHeaderClickListener(OnHeaderClickListener listener) {
        this.mHeaderClickListener = listener;
    }

    @Override
    public void getItemOffsets(final Rect outRect, final View view, final RecyclerView parent, RecyclerView.State state) {
        checkCache(parent);
    }


    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

        // 检测到标签存在的时候，将标签强制固定在顶部
        createPinnedHeader(parent);

        if (!mDisableDrawHeader && mPinnedHeaderView != null && mFirstVisiblePosition >= mPinnedHeaderPosition) {

            mClipBounds = c.getClipBounds();
            // getTop拿到的是它的原点(它自身的padding值包含在内)相对parent的顶部距离，加上它的高度后就是它的底部所处的位置
            final int headEnd = mPinnedHeaderView.getTop() + mPinnedHeaderView.getHeight();
            // 根据坐标查找view，headEnd + 1找到的就是mPinnedHeaderView底部下面的view
            final View belowView = parent.findChildViewUnder(c.getWidth() / 2, headEnd + 1);
            if (isPinnedHeader(parent, belowView)) {
                // 如果是标签的话，缓存的标签就要同步跟此标签移动
                // 根据belowView相对顶部距离计算出缓存标签的位移
                mPinnedHeaderOffset = belowView.getTop() - (mRecyclerViewPaddingTop + mPinnedHeaderView.getHeight() + mHeaderTopMargin);
                // 锁定的矩形顶部为v.getTop(趋势是mPinnedHeaderView.getHeight()->0)
                mClipBounds.top = belowView.getTop();
            } else {
                mPinnedHeaderOffset = 0;
                mClipBounds.top = mRecyclerViewPaddingTop + mPinnedHeaderView.getHeight();
            }
            // 锁定画布绘制范围，记为A
            c.clipRect(mClipBounds);
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {

        if (!mDisableDrawHeader && mPinnedHeaderView != null && mFirstVisiblePosition >= mPinnedHeaderPosition) {
            c.save();

            mItemTouchListener.invalidTopAndBottom(mPinnedHeaderOffset);

            mClipBounds.top = mRecyclerViewPaddingTop + mHeaderTopMargin;
            // 锁定画布绘制范围，记为B
            // REVERSE_DIFFERENCE，实际上就是求得的B和A的差集范围，即B－A，只有在此范围内的绘制内容才会被显示
            // 因此,只绘制(0,0,parent.getWidth(),belowView.getTop())这个范围，然后画布移动了mPinnedHeaderTop，所以刚好是绘制顶部标签移动的范围
            // 低版本不行，换回Region.Op.UNION并集
            c.clipRect(mClipBounds, Region.Op.UNION);
            c.translate(mRecyclerViewPaddingLeft + mHeaderLeftMargin, mPinnedHeaderOffset + mRecyclerViewPaddingTop + mHeaderTopMargin);
            mPinnedHeaderView.draw(c);

            c.restore();

        } else if (mItemTouchListener != null) {
            // 不绘制的时候，把头部的偏移值偏移用户点击不到的程度
            mItemTouchListener.invalidTopAndBottom(-1000);
        }
    }

    /**
     * 查找到view对应的位置从而判断出是否标签类型
     *
     * @param parent
     * @param view
     * @return
     */
    private boolean isPinnedHeader(RecyclerView parent, View view) {
        final int position = parent.getChildAdapterPosition(view);
        if (position == RecyclerView.NO_POSITION) {
            return false;
        }
        final int type = mAdapter.getItemViewType(position);
        return isPinnedHeaderType(type);
    }

    /**
     * 创建标签强制固定在顶部
     *
     * @param parent
     */
    @SuppressWarnings("unchecked")
    private void createPinnedHeader(final RecyclerView parent) {

        if (mAdapter == null) {
            // checkCache的话RecyclerView未设置之前mAdapter为空
            return;
        }

        final RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();

        // 获取第一个可见的item位置
        mFirstVisiblePosition = findFirstVisiblePosition(layoutManager);

        // 获取标签的位置，
        int pinnedHeaderPosition = findPinnedHeaderPosition(mFirstVisiblePosition);
        if (pinnedHeaderPosition >= 0 && mPinnedHeaderPosition != pinnedHeaderPosition) {

            // 标签位置有效并且和缓存的位置不同
            mPinnedHeaderPosition = pinnedHeaderPosition;

            if (mViewHolderMaps.containsKey(mPinnedHeaderPosition)) {
                RecyclerView.ViewHolder holder = mViewHolderMaps.get(mPinnedHeaderPosition);
                mAdapter.bindViewHolder(holder, mPinnedHeaderPosition);
                mPinnedHeaderView = holder.itemView;
            } else {
                // 获取标签的type
                final int type = mAdapter.getItemViewType(mPinnedHeaderPosition);
                // 手动调用创建标签
                final RecyclerView.ViewHolder holder = mAdapter.createViewHolder(parent, type);
                mAdapter.bindViewHolder(holder, mPinnedHeaderPosition);
                // 缓存标签
                mPinnedHeaderView = holder.itemView;
                mViewHolderMaps.put(mPinnedHeaderPosition, holder);

                // 遍历所有的叶子节点,并设置Id
                if(mPinnedHeaderView instanceof ViewGroup) {
                    ViewGroup viewGroup = (ViewGroup) mPinnedHeaderView;
                    LinkedList<ViewGroup> queue = new LinkedList<ViewGroup>();
                    queue.add(viewGroup);
                    while(!queue.isEmpty()) {
                        ViewGroup current = queue.removeFirst();
                        for(int i = 0; i < current.getChildCount(); i ++) {
                            View view = current.getChildAt(i);
                            if (view.isClickable()) {
                                int id = IdUtil.generateViewId();
                                view.setId(id);
                                mClickIdList.add(id);
                            }

                            if(view instanceof ViewGroup) {
                                queue.addLast((ViewGroup) view);
                            }
                        }
                    }
                }
            }

            ViewGroup.LayoutParams lp = mPinnedHeaderView.getLayoutParams();
            if (lp == null) {
                // 标签默认宽度占满parent
                lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                mPinnedHeaderView.setLayoutParams(lp);
            }

            // 对高度进行处理
            int heightMode = View.MeasureSpec.getMode(lp.height);
            int heightSize = View.MeasureSpec.getSize(lp.height);

            if (heightMode == View.MeasureSpec.UNSPECIFIED) {
                heightMode = View.MeasureSpec.EXACTLY;
            }

            mRecyclerViewPaddingLeft = parent.getPaddingLeft();
            int recyclerViewPaddingRight = parent.getPaddingRight();
            mRecyclerViewPaddingTop = parent.getPaddingTop();
            int recyclerViewPaddingBottom = parent.getPaddingBottom();

            if (lp instanceof ViewGroup.MarginLayoutParams) {
                final ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) lp;
                mHeaderLeftMargin = mlp.leftMargin;
                mHeaderTopMargin = mlp.topMargin;
                mHeaderRightMargin = mlp.rightMargin;
                mHeaderBottomMargin = mlp.bottomMargin;
            }

            // 最大高度为RecyclerView的高度减去padding
            final int maxHeight = parent.getHeight() - mRecyclerViewPaddingTop - recyclerViewPaddingBottom;
            // 不能超过maxHeight
            heightSize = Math.min(heightSize, maxHeight);

            // 因为标签默认宽度占满parent，所以宽度强制为RecyclerView的宽度减去padding
            final int widthSpec = View.MeasureSpec
                    .makeMeasureSpec(parent.getWidth() - mRecyclerViewPaddingLeft - recyclerViewPaddingRight - mHeaderLeftMargin - mHeaderRightMargin,
                            View.MeasureSpec.EXACTLY);
            final int heightSpec = View.MeasureSpec.makeMeasureSpec(heightSize, heightMode);
            // 强制测量
            mPinnedHeaderView.measure(widthSpec, heightSpec);

            mLeft = mRecyclerViewPaddingLeft + mHeaderLeftMargin;
            mTop = mRecyclerViewPaddingTop + mHeaderTopMargin;
            mRight = mPinnedHeaderView.getMeasuredWidth() + mRecyclerViewPaddingLeft + mHeaderLeftMargin + mHeaderRightMargin;
            mBottom = mPinnedHeaderView.getMeasuredHeight() + mRecyclerViewPaddingTop + mHeaderTopMargin + mHeaderBottomMargin;

            // 位置强制布局在顶部
            mPinnedHeaderView.layout(mLeft, mTop, mRight - mHeaderRightMargin, mBottom - mHeaderBottomMargin);

            if (mItemTouchListener == null) {
                mItemTouchListener = new OnItemTouchListener(parent.getContext());
                try {
                    final Field field = parent.getClass().getDeclaredField("mOnItemTouchListeners");
                    field.setAccessible(true);
                    final ArrayList<OnItemTouchListener> touchListeners = (ArrayList<OnItemTouchListener>) field.get(parent);
                    touchListeners.add(0, mItemTouchListener);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                    parent.addOnItemTouchListener(mItemTouchListener);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    parent.addOnItemTouchListener(mItemTouchListener);
                }
                if (mHeaderClickListener != null) {
                    mItemTouchListener.setHeaderClickListener(mHeaderClickListener);
                    mItemTouchListener.disableHeaderClick(mDisableHeaderClick);
                }
                mItemTouchListener.setClickBounds(OnItemTouchListener.HEADER_ID, new ClickBounds(mPinnedHeaderView, mLeft, mTop, mRight, mBottom));
            }

            if (mHeaderClickListener != null) {
                // 清除上一个mPinnedHeaderView的可点击区域,再设置当前的mPinnedHeaderView的可点击区域,否则前后两个点击区域不一样时会有错乱
                mItemTouchListener.clearClickBounds();

                // OnItemTouchListener.HEADER_ID代表是标签的Id
                mItemTouchListener.setClickBounds(OnItemTouchListener.HEADER_ID, new ClickBounds(mPinnedHeaderView, mLeft, mTop, mRight, mBottom));
                if (mHeaderClickListener != null && mClickIdList != null && mClickIdList.size() > 0) {
                    for (int mClickId : mClickIdList) {
                        final View view = mPinnedHeaderView.findViewById(mClickId);
                        if (view != null) {
                            mItemTouchListener.setClickBounds(mClickId, new ClickBounds(view, view.getLeft(), view.getTop(), view.getLeft() + view.getMeasuredWidth(),
                                    view.getTop() + view.getMeasuredHeight()));
                        }
                    }
                }
                mItemTouchListener.setClickHeaderInfo(mPinnedHeaderPosition - mDataPositionOffset);
            }

        }

    }

    public int getDataPositionOffset() {
        return mDataPositionOffset;
    }

    public void setDataPositionOffset(int offset) {
        mDataPositionOffset = offset;
    }

    /**
     * 从传入位置递减找出标签的位置
     *
     * @param formPosition
     * @return
     */
    private int findPinnedHeaderPosition(int formPosition) {

        for (int position = formPosition; position >= 0; position--) {
            // 位置递减，只要查到位置是标签，立即返回此位置
            final int type = mAdapter.getItemViewType(position);
            if (isPinnedHeaderType(type)) {
                return position;
            }
        }

        return -1;
    }

    /**
     * 通过适配器告知类型是否为标签
     *
     * @param viewType
     * @return
     */
    private boolean isPinnedHeaderType(int viewType) {
        if (!mPinnedViewTypes.containsKey(viewType)) {
            mPinnedViewTypes.put(viewType, ((PinnedHeaderAdapter) mAdapter).isPinnedViewType(viewType));
        }

        return mPinnedViewTypes.get(viewType);
    }

    // interface
    public interface PinnedHeaderAdapter {
        boolean isPinnedViewType(int viewType);
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
     * 检查缓存
     *
     * @param parent
     */
    private void checkCache(final RecyclerView parent) {

        if (mParent != parent) {
            mParent = parent;
        }

        final RecyclerView.Adapter adapter = parent.getAdapter();
        if (mAdapter != adapter) {
            // 适配器为null或者不同，清空缓存
            mPinnedHeaderView = null;
            mPinnedHeaderPosition = -1;
            mAdapter = adapter;
//            mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//                @Override
//                public void onChanged() {
//                    super.onChanged();
//                    mPinnedHeaderPosition = -1;
//                }
//
//                @Override
//                public void onItemRangeChanged(int positionStart, int itemCount) {
//                    super.onItemRangeChanged(positionStart, itemCount);
//                    mPinnedHeaderPosition = -1;
//                }
//
//                @Override
//                public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
//                    super.onItemRangeChanged(positionStart, itemCount, payload);
//                    mPinnedHeaderPosition = -1;
//                }
//
//                @Override
//                public void onItemRangeInserted(int positionStart, int itemCount) {
//                    super.onItemRangeInserted(positionStart, itemCount);
//                    mPinnedHeaderPosition = -1;
//                }
//
//                @Override
//                public void onItemRangeRemoved(int positionStart, int itemCount) {
//                    super.onItemRangeRemoved(positionStart, itemCount);
//                    mPinnedHeaderPosition = -1;
//                }
//
//                @Override
//                public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
//                    super.onItemRangeMoved(fromPosition, toPosition, itemCount);
//                    mPinnedHeaderPosition = -1;
//                }
//            });
        }
    }

    public View getPinnedHeaderView() {
        return mPinnedHeaderView;
    }

    public int getPinnedHeaderPosition() {
        return mPinnedHeaderPosition;
    }

    /**
     * 是否禁止绘制粘性头部
     *
     * @return true的话不绘制头部
     */
    public boolean isDisableDrawHeader() {
        return mDisableDrawHeader;
    }

    /**
     * 禁止绘制粘性头部
     *
     * @param disableDrawHeader true的话不绘制头部，默认false绘制头部
     */
    public void disableDrawHeader(boolean disableDrawHeader) {
        mDisableDrawHeader = disableDrawHeader;
        if (mParent != null) {
            mParent.invalidateItemDecorations();
        }
    }

    public interface OnHeaderClickListener {

        void onHeaderClick(View view, int id, int position);

        void onHeaderLongClick(View view, int id, int position);

        void onHeaderDoubleClick(View view, int id, int position);

    }

    public static class OnHeaderClickAdapter implements OnHeaderClickListener {


        @Override
        public void onHeaderClick(View view, int id, int position) {

        }

        @Override
        public void onHeaderLongClick(View view, int id, int position) {

        }

        @Override
        public void onHeaderDoubleClick(View view, int id, int position) {

        }
    }

    public class OnItemTouchListener implements RecyclerView.OnItemTouchListener {

        /**
         * 代表的是标签的Id
         */
        public static final int HEADER_ID = -1;

        private ClickBounds mTmpBounds;

        private View mTmpView;

        private int mTmpClickId;

        private GestureDetector mGestureDetector;

        private SparseArray<ClickBounds> mBoundsArray;

        private boolean mIntercept;

        private PinnedHeaderItemDecoration.OnHeaderClickListener mHeaderClickListener;

        private int mPosition;

        private boolean mDisableHeaderClick;
        private boolean mDownInside;
        private RecyclerView.Adapter mAdapter;

        public OnItemTouchListener(Context context) {

            mBoundsArray = new SparseArray<ClickBounds>();

            GestureListener gestureListener = new GestureListener();
            mGestureDetector = new GestureDetector(context, gestureListener);
        }

        /**
         * 设置对应的View的点击范围
         *
         * @param id     View的ID
         * @param bounds 点击范围
         */
        public void setClickBounds(int id, ClickBounds bounds) {
            mBoundsArray.put(id, bounds);
        }

        public void clearClickBounds() {
            mBoundsArray.clear();
        }

        /**
         * 更新点击范围的顶部和底部
         *
         * @param offset 偏差
         */
        public void invalidTopAndBottom(int offset) {
            for (int i = 0; i < mBoundsArray.size(); i++) {
                final ClickBounds bounds = mBoundsArray.valueAt(i);
                bounds.setTop(bounds.getFirstTop() + offset);
                bounds.setBottom(bounds.getFirstBottom() + offset);
            }
        }

        @Override
        public boolean onInterceptTouchEvent(final RecyclerView rv, MotionEvent event) {

            if (mAdapter != rv.getAdapter()) {
                mAdapter = rv.getAdapter();
            }

            // 这里处理触摸事件来决定是否自己处理事件
            mGestureDetector.setIsLongpressEnabled(true);
            mGestureDetector.onTouchEvent(event);

            if (event.getAction() == MotionEvent.ACTION_UP && !mIntercept && mDownInside) {
                // 针对在头部滑动然后抬起手指的情况，如果在头部范围内需要拦截
                float downX = event.getX();
                float downY = event.getY();
                final ClickBounds bounds = mBoundsArray.valueAt(0);
                return downX >= bounds.getLeft() && downX <= bounds.getRight() && downY >= bounds.getTop() && downY <= bounds.getBottom();
            }

            return mIntercept;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }

        public void setClickHeaderInfo(int position) {
            mPosition = position;
        }

        public void setHeaderClickListener(PinnedHeaderItemDecoration.OnHeaderClickListener headerClickListener) {
            mHeaderClickListener = headerClickListener;
        }

        public void disableHeaderClick(boolean disableHeaderClick) {
            mDisableHeaderClick = disableHeaderClick;
        }

        private void shouldIntercept(MotionEvent e) {
            float downX = e.getX();
            float downY = e.getY();

            // 如果坐标在标签的范围内的话就屏蔽事件，自己处理
            //  mIntercept = downX >= mLeft && downX <= mRight && downY >= mTop && downY <= mBottom;

            for (int i = 0; i < mBoundsArray.size(); i++) {
                // 逐个View拿出，判断坐标是否落在View的范围里面
                final ClickBounds bounds = mBoundsArray.valueAt(i);
                boolean inside = downX >= bounds.getLeft() && downX <= bounds.getRight() && downY >= bounds.getTop() && downY <= bounds.getBottom();
                if (inside) {
                    // 拦截事件成立
                    mIntercept = true;
                    // 点击范围内
                    if (mTmpBounds == null) {
                        mTmpBounds = bounds;
                    } else if (bounds.getLeft() >= mTmpBounds.getLeft() && bounds.getRight() <= mTmpBounds.getRight() && bounds.getTop() >= mTmpBounds.getTop() && bounds
                            .getBottom() <= mTmpBounds.getBottom()) {
                        // 与缓存的在点击范围的进行比较，若其点击范围比缓存的更小，它点击响应优先级更高
                        mTmpBounds = bounds;
                    }
                }
            }

            if (mIntercept) {
                // 有点击中的，取出其id并清空mTmpBounds
                mTmpClickId = mBoundsArray.keyAt(mBoundsArray.indexOfValue(mTmpBounds));
                mTmpView = mTmpBounds.getView();
                mTmpBounds = null;
            }

        }

        private class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private boolean mDoubleTap;

            @Override
            public boolean onDown(MotionEvent e) {
                // 记录手指触碰，是否在头部范围内
                float downX = e.getX();
                float downY = e.getY();
                final ClickBounds bounds = mBoundsArray.valueAt(0);
                mDownInside = downX >= bounds.getLeft() && downX <= bounds.getRight() && downY >= bounds.getTop() && downY <= bounds.getBottom();

                if (!mDoubleTap) {
                    mIntercept = false;
                } else {
                    // 因为双击会在onDoubleTap后再调用onDown，所以这里要忽略第二次防止mIntercept被影响
                    mDoubleTap = false;
                }
                return super.onDown(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                shouldIntercept(e);

                if (!mDisableHeaderClick && mIntercept && mHeaderClickListener != null && mAdapter != null && mPosition <= mAdapter.getItemCount() - 1) {
                    // 自己处理点击标签事件
                    try {
                        mHeaderClickListener.onHeaderLongClick(mTmpView, mTmpClickId, mPosition);
                    } catch (IndexOutOfBoundsException e1) {
                        e1.printStackTrace();
                    }
                }

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                shouldIntercept(e);

                return mIntercept;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (!mDisableHeaderClick && mIntercept && mHeaderClickListener != null && mAdapter != null && mPosition <= mAdapter.getItemCount() - 1) {
                    // 自己处理点击标签事件
                    try {
                        mHeaderClickListener.onHeaderClick(mTmpView, mTmpClickId, mPosition);
                    } catch (IndexOutOfBoundsException e1) {
                        e1.printStackTrace();
                    }
                }

                return super.onSingleTapConfirmed(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                mDoubleTap = true;
                shouldIntercept(e);

                if (!mDisableHeaderClick && mIntercept && mHeaderClickListener != null && mAdapter != null && mPosition <= mAdapter.getItemCount() - 1) {
                    // 自己处理点击标签事件
                    try {
                        mHeaderClickListener.onHeaderDoubleClick(mTmpView, mTmpClickId, mPosition);
                    } catch (IndexOutOfBoundsException e1) {
                        e1.printStackTrace();
                    }
                }

                // 有机型在调用onDoubleTap后会接着调用onLongPress，这里这样处理
                mGestureDetector.setIsLongpressEnabled(false);

                return mIntercept;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (e1 != null)
                    LogUtil.i("tuoli", "onScroll e1", e1.getX(), e1.getY());
                if (e2 != null)
                    LogUtil.i("tuoli", "onScroll e2", e2.getX(), e2.getY());
                LogUtil.i("tuoli", "onScroll", distanceX, distanceY);

                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1 != null)
                    LogUtil.i("tuoli", "onFling e1", e1.getX(), e1.getY());
                if (e2 != null)
                    LogUtil.i("tuoli", "onFling e2", e2.getX(), e2.getY());
                LogUtil.i("tuoli", "onFling", velocityX, velocityY);

                return super.onFling(e1, e2, velocityX, velocityY);
            }
        }

    }
}
