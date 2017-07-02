/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.view.imageview;

import android.graphics.drawable.Drawable;

/**
 * 图片加载回调
 */
public interface DrawableLoadCallback {
    //drawable = null 表示失败，drawable != null表示成功
    void onLoadResult(Drawable drawable);
}