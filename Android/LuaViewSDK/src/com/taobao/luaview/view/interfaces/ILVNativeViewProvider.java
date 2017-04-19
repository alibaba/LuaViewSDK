/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.view.interfaces;


import android.view.View;

/**
 * Native View Provider
 *
 * @author song
 * @date 15/8/20
 */
public interface ILVNativeViewProvider {

    /**
     * get Native View from some Object
     *
     * @return
     */
    View getNativeView();
}
