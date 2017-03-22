/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.ui;


import com.taobao.luaview.userdata.base.BaseUserdata;
import com.taobao.luaview.view.LVLoadingDialog;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class UDLoadingDialog extends BaseUserdata {

    public UDLoadingDialog(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals, metaTable, varargs);
    }

    /**
     * 开始动画
     *
     * @return
     */
    public UDLoadingDialog startAnimating() {
        LVLoadingDialog.startAnimating(getContext());
        return this;
    }

    /**
     * 停止动画
     *
     * @return
     */
    public UDLoadingDialog stopAnimating() {
        LVLoadingDialog.stopAnimating();
        return this;
    }

    /**
     * 是否动画
     *
     * @return
     */
    public boolean isAnimating() {
        return LVLoadingDialog.isAnimating();
    }

    /**
     * 设置颜色跟透明度
     *
     * @param color
     * @param alpha
     * @return
     */
    public UDLoadingDialog setColorAndAlpha(Integer color, Double alpha) {
        //TODO
        return this;
    }

    /**
     * 获取颜色
     *
     * @return
     */
    public int getColor() {
        //TODO
        return 0;
    }

    /**
     * 获取alpha
     *
     * @return
     */
    public int getAlpha() {
        //TODO
        return 0;
    }

}
