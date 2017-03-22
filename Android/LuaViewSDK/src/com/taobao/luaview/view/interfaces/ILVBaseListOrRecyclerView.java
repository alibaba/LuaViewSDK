/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

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
