package com.taobao.luaview.view.interfaces;


/**
 * 统一List跟Recycler的操作
 *
 * @author song
 * @date 15/11/30
 */
public interface ILVBaseListOrRecyclerView<T> extends ILVViewGroup {

    /**
     * 获取adapter
     *
     * @return
     */
    T getLVAdapter();

}
