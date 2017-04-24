/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.fun.mapper.kit;

import com.taobao.luaview.fun.base.BaseMethodMapper;
import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.kit.UDBitmap;
import com.taobao.luaview.util.DimenUtil;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.List;

/**
 * Bitmap 接口封装，二进制数据处理
 *
 * @author song
 * @date 15/8/21
 */
@LuaViewLib(revisions = {"20170306已对标"})
public class BitmapMethodMapper<U extends UDBitmap> extends BaseMethodMapper<U> {
    private static final String TAG = "BitmapMethodMapper";

    private static final String[] sMethods = new String[]{
            "sprite",//0
            "size",//1
    };

    @Override
    public List<String> getAllFunctionNames() {
        return mergeFunctionNames(TAG, super.getAllFunctionNames(), sMethods);
    }

    @Override
    public Varargs invoke(int code, U target, Varargs varargs) {
        final int optcode = code - super.getAllFunctionNames().size();
        switch (optcode) {
            case 0:
                return sprite(target, varargs);
            case 1:
                return size(target, varargs);
        }
        return super.invoke(code, target, varargs);
    }

    //--------------------------------------- API --------------------------------------------------

    /**
     * 切割bitmap
     *
     * @param bitmap
     * @param varargs
     * @return
     */
    public LuaValue sprite(U bitmap, Varargs varargs) {
        final int x = DimenUtil.dpiToPx(varargs.arg(2));
        final int y = DimenUtil.dpiToPx(varargs.arg(3));
        final int width = DimenUtil.dpiToPx(varargs.arg(4));
        final int height = DimenUtil.dpiToPx(varargs.arg(5));
        final LuaFunction callback = LuaUtil.getFunction(varargs, 6);
        return bitmap.sprite(x, y, width, height, callback);
    }

    /**
     * 获取bitmap size
     *
     * @param bitmap
     * @param varargs
     * @return
     */
    public Varargs size(U bitmap, Varargs varargs) {
        return varargsOf(new LuaValue[]{valueOf(DimenUtil.pxToDpi(bitmap.getWidth())), valueOf(DimenUtil.pxToDpi(bitmap.getHeight()))});
    }
}
