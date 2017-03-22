/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.view.foreground;

import android.graphics.drawable.Drawable;

/**
 * 前景色
 *
 * @author song
 * @date 16/8/2
 * 主要功能描述
 * 修改描述
 * 下午11:36 song XXX
 */
public interface IForeground {

    boolean hasForeground();

    void setForeground(Drawable drawable);

    void clearForeground();
}
