/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.list;

import android.graphics.Point;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.taobao.luaview.cache.SparseCache;
import com.taobao.luaview.global.LuaView;
import com.taobao.luaview.provider.ImageProvider;
import com.taobao.luaview.userdata.ui.UDViewGroup;
import com.taobao.luaview.util.AndroidUtil;
import com.taobao.luaview.util.DimenUtil;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * UDBaseListView or RecyclerView
 *
 * @author song
 * @date 15/10/23
 */
public abstract class UDBaseListOrRecyclerView<T extends ViewGroup> extends UDViewGroup<T> {
    //初始化数据
    private LuaValue mSectionDelegate;
    protected LuaValue mCellDelegate;

    //view type map
    private AtomicBoolean isSectionLengthInit = new AtomicBoolean(false);
    private Point[] mSectionLength;//长度数组，每个point的x为开始位置(包含)，y为结束位置(不包含)，(y-x)为该组长度
    protected HashMap<String, Integer> mViewTypeMap = new HashMap<String, Integer>();//view类型集合，从0开始，不能放到init中初始化
    protected HashMap<Integer, String> mViewTypeNameMap = new HashMap<Integer, String>();//View类型名称集合，不能放到init中初始化

    //id
    protected SparseCache<String> mIdCache;

    //延迟加载
    protected boolean mLazyLoad = true;

    public UDBaseListOrRecyclerView(T view, Globals globals, LuaValue metaTable, Varargs initParams) {
        super(view, globals, metaTable, initParams);
        init();
    }

    /**
     * 初始化数据
     */
    public void init() {
        if (this.initParams != null) {
            this.mIdCache = new SparseCache<String>();

            this.mSectionDelegate = LuaUtil.getValue(initParams, "Section");
            this.mCellDelegate = LuaUtil.getValue(initParams, "Cell");
            this.mCallback = LuaUtil.getValue(initParams, "Callback");
            initSectionLength();
        }
    }

    /**
     * 初始化section的长度
     */
    private void initSectionLength() {
        isSectionLengthInit.set(false);
        final int numOfSections = getSectionCount();
        if (numOfSections > 0) {
            mSectionLength = new Point[numOfSections];
            int left = 0, right = 0;
            for (int i = 0; i < numOfSections; i++) {
                left = right;
                right = right + getRowCount(i);
                mSectionLength[i] = new Point(left, right);
            }
        }
        isSectionLengthInit.set(true);
    }

    //-----------------------------------------section----------------------------------------------

    /**
     * 获取新的总数跟老的总数差别
     *
     * @return
     */
    public int getDiffTotalCount() {
        int oldCount = (isSectionLengthInit.get() && mSectionLength != null) ? mSectionLength[mSectionLength.length - 1].y : 0;
        int newCount = getRawTotalCount();
        return newCount - oldCount;
    }

    /**
     * 获取总的个数
     *
     * @return
     */
    public int getTotalCount() {
        if (isSectionLengthInit.get()) {
            return mSectionLength != null ? mSectionLength[mSectionLength.length - 1].y : 0;
        } else {
            final int totalSections = getSectionCount();
            int totalCount = 0;
            for (int i = 0; i < totalSections; i++) {
                totalCount += getRowCount(i);
            }
            return totalCount;
        }
    }

    /**
     * 获取原始总个数
     *
     * @return
     */
    protected int getRawTotalCount() {
        final int totalSections = getRawSectionCount();
        int totalCount = 0;
        for (int i = 0; i < totalSections; i++) {
            totalCount += getRawRowCount(i);
        }
        return totalCount;
    }

    /**
     * 返回新count 跟 老count的差距
     *
     * @return
     */
    protected int getDiffSectionCount() {
        int oldCount = (isSectionLengthInit.get() && mSectionLength != null) ? mSectionLength.length : 0;
        int newCount = getRawSectionCount();
        return newCount - oldCount;
    }

    /**
     * 分区个数，android的totalCount=分区个数*每个分区的数目
     *
     * @return
     */
    protected int getSectionCount() {
        if (isSectionLengthInit.get()) {
            return mSectionLength != null ? mSectionLength.length : 0;
        } else {
            return getRawSectionCount();
        }
    }

    /**
     * 获取原始的SectionCount
     *
     * @return
     */
    protected int getRawSectionCount() {
        if (this.mSectionDelegate == null) {
            return 0;
        }

        return LuaUtil.getOrCallFunction(this.mSectionDelegate.get("SectionCount")).optint(1);
    }

    /**
     * 获取diff of new RowCount and old RowCount of Section
     *
     * @param section
     * @return
     */
    protected int diffRowCount(int section) {
        int oldCount = (isSectionLengthInit.get() && mSectionLength != null) ? (this.mSectionLength[section].y - this.mSectionLength[section].x) : 0;
        int newCount = getRawRowCount(section);
        return newCount - oldCount;
    }

    /**
     * 每个分区的行数，section从0开始
     *
     * @param section
     * @return
     */
    protected int getRowCount(int section) {
        if (isSectionLengthInit.get()) {
            return mSectionLength != null ? (this.mSectionLength[section].y - this.mSectionLength[section].x) : 0;
        } else {
            return getRawRowCount(section);
        }
    }

    /**
     * 读取RowCount，获取原始count
     *
     * @param section
     * @return
     */
    protected int getRawRowCount(int section) {
        return LuaUtil.callFunction(this.mSectionDelegate.get("RowCount"), LuaUtil.toLuaInt(section)).optint(0);
    }
    //------------------------------------------cell------------------------------------------------

    /**
     * get position by section and rowInSection
     *
     * @param section
     * @param rowInSection
     * @return
     */
    protected int getPositionBySectionAndRow(int section, int rowInSection) {
        if (mSectionLength != null) {
            if (section >= 0 && section < mSectionLength.length) {
                return mSectionLength[section].x + rowInSection;
            } else {
                return 0;
            }
        }
        return rowInSection;
    }

    /**
     * 根据position来判断该位置属于哪个position
     *
     * @param position
     * @return
     */
    protected int getSectionByPosition(int position) {
        if (mSectionLength != null) {
            for (int i = 0; i < mSectionLength.length; i++) {
                if (position >= mSectionLength[i].x && position < mSectionLength[i].y) {
                    return i;
                }
            }
        }
        return 0;
    }

    /**
     * 根据position来判断该位置位于section的第几个位置
     *
     * @param position
     * @return
     */
    protected int getRowInSectionByPosition(int position) {
        if (mSectionLength != null) {
            final int section = getSectionByPosition(position);
            return position - mSectionLength[section].x;
        } else {
            return 0;
        }
    }

    /**
     * 根据position获取某个cell的类型
     *
     * @param position
     * @return
     */
    public String getId(int position) {
        return getId(position, getSectionByPosition(position), getRowInSectionByPosition(position));
    }

    /**
     * 根据section, row获取cell的Id
     *
     * @param position
     * @param section
     * @param row
     * @return
     */
    protected String getId(int position, int section, int row) {
        final String cacheId = mIdCache != null ? mIdCache.get(position) : null;
        if (cacheId != null) {
            return cacheId;
        } else {
            final String id = LuaUtil.callFunction(mCellDelegate.get("Id"), LuaUtil.toLuaInt(section), LuaUtil.toLuaInt(row)).optjstring("");
            if (mIdCache != null) {
                mIdCache.put(position, id);
            }
            return id;
        }
    }


    /**
     * 根据id获取某个cell
     *
     * @param id
     * @return
     */
    public LuaValue getCell(String id) {
        return mCellDelegate.get(id);
    }


    /**
     * 是否有size的定义
     *
     * @param position
     * @return
     */
    public boolean hasCellSize(int position) {
        return hasCellFunction(position, "Size");
    }

    /**
     * return width, height
     *
     * @param cell
     * @param position
     * @return
     */
    public int[] callCellSize(LuaValue cell, int position, int... defaultSize) {
        final Varargs size = invokeCellSize(cell, position);
        int defaultWidth = (defaultSize != null && defaultSize.length > 1) ? defaultSize[0] : AndroidUtil.getScreenWidth(getContext());
        int width = 0, height = 0;
        if (size != null) {
            if (size.narg() > 1) {//width & height
                width = DimenUtil.dpiToPx(size.arg(1), defaultWidth);
                height = DimenUtil.dpiToPx(size.arg(2));
            } else {
                width = defaultWidth;
                height = DimenUtil.dpiToPx(size.arg(1));
            }
        }
        return new int[]{width, height};
    }

    /**
     * 调用 Size 方法 （返回多个值)
     *
     * @param cell
     * @param position
     * @return
     */
    public Varargs invokeCellSize(LuaValue cell, int position) {
//        return invokeCellFunction("Size", cell, position);
        return invokeCellFunction("Size", position);//不带cell
    }

    /**
     * 调用 Init 方法
     *
     * @param cell
     * @param position
     * @return
     */
    public LuaValue callCellInit(LuaValue cell, int position) {
        return callCellFunction("Init", cell, position);//Lua从1开始
    }

    /**
     * 调用 Layout 方法
     *
     * @param cell
     * @param position
     * @return
     */
    public LuaValue callCellLayout(LuaValue cell, int position) {
        return callCellFunction("Layout", cell, position);
    }

    /**
     * 调用 Callback 方法
     *
     * @param cell
     * @param position
     * @return
     */
    public LuaValue onCellClicked(LuaValue cell, int position) {
        final int section = getSectionByPosition(position);
        final int row = getRowInSectionByPosition(position);
        final String id = getId(position, section, row);
        final LuaValue cellData = getCell(id);
        if (cellData != null) {
            final LuaValue callback = cellData.get("Callback");
            if (callback.isfunction()) {
                return LuaUtil.callFunction(callback, cell, LuaUtil.toLuaInt(section), LuaUtil.toLuaInt(row));
            } else if (callback.istable()) {
                return LuaUtil.callFunction(LuaUtil.getFunction(callback, "Click", "click"), cell, LuaUtil.toLuaInt(section), LuaUtil.toLuaInt(row));
            }
        }
        return NIL;
    }

    /**
     * 调用 Callback 方法
     *
     * @param cell
     * @param position
     * @return
     */
    public boolean onCellLongClicked(LuaValue cell, int position) {
        final int section = getSectionByPosition(position);
        final int row = getRowInSectionByPosition(position);
        final String id = getId(position, section, row);
        final LuaValue cellData = getCell(id);
        if (cellData != null) {
            final LuaValue callback = cellData.get("Callback");
            if (callback != null && callback.istable()) {
                return LuaUtil.callFunction(LuaUtil.getFunction(callback, "LongClick", "longClick"), cell, LuaUtil.toLuaInt(section), LuaUtil.toLuaInt(row)).optboolean(false);
            }
        }
        return false;
    }

    /**
     * 调用cell的某个方法
     *
     * @param method
     * @param cell
     * @param position
     * @return
     */
    private LuaValue callCellFunction(String method, LuaValue cell, int position) {
        final int section = getSectionByPosition(position);
        final int row = getRowInSectionByPosition(position);
        return callCellFunction(getId(position, section, row), method, cell, section, row);
    }

    /**
     * 调用cell的某个方法，返回多个值
     *
     * @param method
     * @param cell
     * @param position
     * @return
     */
    private Varargs invokeCellFunction(String method, LuaValue cell, int position) {
        final int section = getSectionByPosition(position);
        final int row = getRowInSectionByPosition(position);
        return invokeCellFunction(getId(position, section, row), method, cell, section, row);
    }

    /**
     * 调用cell的某个方法，返回多个值
     *
     * @param method
     * @param position
     * @return
     */
    private Varargs invokeCellFunction(String method, int position) {
        final int section = getSectionByPosition(position);
        final int row = getRowInSectionByPosition(position);
        return invokeCellFunction(getId(position, section, row), method, section, row);
    }

    /**
     * 调用cell的某个方法
     *
     * @param id
     * @param method
     * @param cell
     * @param section
     * @param row
     * @return
     */
    private LuaValue callCellFunction(String id, String method, LuaValue cell, int section, int row) {
        final LuaValue methodData = getCell(id);
        if (!methodData.isnil()) {
            return LuaUtil.callFunction(methodData.get(method), cell, LuaUtil.toLuaInt(section), LuaUtil.toLuaInt(row));
        }
        return NIL;
    }

    /**
     * 调用cell的某个方法 (返回多个值)
     *
     * @param id
     * @param method
     * @param cell
     * @param section
     * @param row
     * @return
     */
    private Varargs invokeCellFunction(String id, String method, LuaValue cell, int section, int row) {
        final LuaValue methodData = getCell(id);
        if (!methodData.isnil()) {
            return LuaUtil.invokeFunction(methodData.get(method), cell, LuaUtil.toLuaInt(section), LuaUtil.toLuaInt(row));
        }
        return NIL;
    }

    /**
     * 调用cell的某个方法 (返回多个值)
     *
     * @param id
     * @param method
     * @param section
     * @param row
     * @return
     */
    private Varargs invokeCellFunction(String id, String method, int section, int row) {
        final LuaValue methodData = getCell(id);
        if (!methodData.isnil()) {
            return LuaUtil.invokeFunction(methodData.get(method), LuaUtil.toLuaInt(section), LuaUtil.toLuaInt(row));
        }
        return NIL;
    }

    /**
     * 是否有某个函数
     *
     * @param method
     * @param position
     * @return
     */
    private boolean hasCellFunction(int position, String method) {
        final int section = getSectionByPosition(position);
        final int row = getRowInSectionByPosition(position);
        final String id = getId(position, section, row);
        return hasCellFunction(id, method);
    }

    /**
     * 是否有某个函数
     *
     * @param cellId
     * @param method
     * @return
     */
    public boolean hasCellFunction(String cellId, String method) {
        final LuaValue methodData = getCell(cellId);
        return !methodData.isnil() && methodData.get(method) != null && methodData.get(method).isfunction();
    }

    /**
     * 获取key map
     *
     * @return
     */
    public HashMap<String, Integer> getCellKeyMap() {
        HashMap<String, Integer> result = new HashMap<String, Integer>();
        if (mCellDelegate != null) {
            LuaValue[] keys = this.mCellDelegate.checktable().keys();
            if (keys != null) {
                for (int i = 0; i < keys.length; i++) {
                    result.put(keys[i].toString(), i);
                }
            }
        }
        return result;
    }

    /**
     * 根据位置获取 item 的type id
     *
     * @param position
     * @return
     */
    public int getItemViewType(int position) {
        final String viewTypeName = getId(position);//得到坑位类型名称
        if (this.mViewTypeMap != null) {
            if (!this.mViewTypeMap.containsKey(viewTypeName)) {
                final int index = this.mViewTypeMap.size();
                this.mViewTypeMap.put(viewTypeName, index);
                this.mViewTypeNameMap.put(index, viewTypeName);
                return index;
            }
            return this.mViewTypeMap.get(viewTypeName);
        }
        return 0;
    }

    /**
     * 获取ViewType的name
     *
     * @param viewType
     * @return
     */
    public String getItemViewTypeName(int viewType) {
        if (this.mViewTypeNameMap != null) {
            return this.mViewTypeNameMap.get(viewType);
        }
        return null;
    }

    /**
     * 这里不是真实的viewType的数字，只是获取Cell中的属性数目（该属性数目一定比cell种数多)
     * TODO 这里通过读取ID的方式获取所有的count
     *
     * @return
     */
    public int getViewTypeCount() {
        return getCellKeyMap().size();
    }


    public UDBaseListOrRecyclerView setLazyLoad(boolean mLazyLoad) {
        this.mLazyLoad = mLazyLoad;
        return this;
    }

    //-----------------------------------------lazy load--------------------------------------------

    /**
     * 更新所有子view的ScrollState，用于延迟加载
     *
     * @param view
     * @param scrollState
     */
    protected void updateAllChildScrollState(ViewGroup view, int scrollState) {
        if (this.mLazyLoad && view != null) {
            if (scrollState >= AbsListView.OnScrollListener.SCROLL_STATE_FLING) {//手滑动的时候
                final ImageProvider provider = LuaView.getImageProvider();
                if (provider != null) {
                    provider.pauseRequests(view, getContext());
                }
            } else {
                final ImageProvider provider = LuaView.getImageProvider();
                if (provider != null) {
                    provider.resumeRequests(view, getContext());
                }
            }
        }
    }

    //------------------------------------------Scroll----------------------------------------------

    /**
     * 初始化滚动回调
     */
    public abstract void initOnScrollCallback(final T view);

    //------------------------------------------功能------------------------------------------------

    /**
     * 重新加载
     *
     * @return
     */
    public abstract UDBaseListOrRecyclerView reload(Integer section, Integer row);

    /**
     * listview滚动到顶部
     *
     * @param animate 是否动画
     * @return
     */
    public abstract UDBaseListOrRecyclerView scrollToTop(final int offset, final boolean animate);

    /**
     * listview滚动到某个位置
     *
     * @param section
     * @param rowInSection
     * @param offset
     * @return
     */
    public abstract UDBaseListOrRecyclerView scrollToItem(final int section, final int rowInSection, final int offset, final boolean animate);


    /**
     * 设置item最小间隔
     *
     * @param spacing
     * @return
     */
    public abstract UDBaseListOrRecyclerView setMiniSpacing(final int spacing);

    /**
     * 获取item最小间隔
     *
     * @return
     */
    public abstract int getMiniSpacing();
}