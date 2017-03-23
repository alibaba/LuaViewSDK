/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.view.interfaces;


import com.taobao.luaview.userdata.ui.UDView;

import java.util.ArrayList;

/**
 * ViewGroup interface
 *
 * @author song
 * @date 15/8/20
 */
public interface ILVViewGroup extends ILVView {
    void setChildNodeViews(ArrayList<UDView> childNodeViews);
}
