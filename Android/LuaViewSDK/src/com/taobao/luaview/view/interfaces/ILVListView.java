/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.view.interfaces;


import android.view.View;
import android.widget.BaseAdapter;

/**
 * ListView interface
 *
 * @author song
 * @date 15/8/20
 */
public interface ILVListView extends ILVBaseListOrRecyclerView<BaseAdapter> {


    /**
     * 新增 header view
     *
     * @param header
     */
    void addHeader(View header);

    void removeHeader();

    /**
     * 新增 footer view
     *
     * @param footer
     */
    void addFooter(View footer);

    void removeFooter();

}
