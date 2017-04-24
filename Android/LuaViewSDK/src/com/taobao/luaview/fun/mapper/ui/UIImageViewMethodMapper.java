/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.fun.mapper.ui;

import android.widget.ImageView;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.constants.UDImageScaleType;
import com.taobao.luaview.userdata.kit.UDBitmap;
import com.taobao.luaview.userdata.kit.UDData;
import com.taobao.luaview.userdata.ui.UDImageView;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.List;

/**
 * Image 接口封装
 *
 * @param <U>
 * @author song
 */
@LuaViewLib(revisions = {"20170306已对标"})
public class UIImageViewMethodMapper<U extends UDImageView> extends UIViewMethodMapper<U> {

    private static final String TAG = "UIImageViewMethodMapper";
    private static final String[] sMethods = new String[]{
            "image",//0
            "contentMode",//1
            "scaleType",//2
            "startAnimationImages",//3
            "stopAnimationImages",//4
            "isAnimationImages"//5
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
                return image(target, varargs);
            case 1:
                return contentMode(target, varargs);
            case 2:
                return scaleType(target, varargs);
            case 3:
                return startAnimationImages(target, varargs);
            case 4:
                return stopAnimationImages(target, varargs);
            case 5:
                return isAnimationImages(target, varargs);
        }
        return super.invoke(code, target, varargs);
    }

    //--------------------------------------- API --------------------------------------------------


    /**
     * 设置图片url
     *
     * @param view    UDImageView
     * @param varargs Varargs
     * @return LuaValue
     */
    public LuaValue image(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setImage(view, varargs);
        } else {
            return getImage(view, varargs);
        }
    }

    public LuaValue setImage(U view, Varargs varargs) {
        if (varargs.isstring(2)) {
            final String url = varargs.optjstring(2, null);
            final LuaFunction callback = varargs.optfunction(3, null);
            return view.setImageUrl(url, callback);
        } else if (varargs.arg(2) instanceof UDData) {//data
            final UDData data = (UDData) varargs.arg(2);
            return view.setImageBytes(data != null ? data.bytes() : null);
        } else if (varargs.arg(2) instanceof UDBitmap){//bitmap
            final UDBitmap bitmap = (UDBitmap) varargs.arg(2);
            return view.setImageBitmap(bitmap);
        }
        return view;
    }

    public LuaValue getImage(U view, Varargs varargs) {
        String imageUrl = view.getImageUrl();
        return imageUrl != null ? valueOf(imageUrl) : LuaValue.NIL;
    }


    /**
     * 设置图片的缩放模式
     *
     * @param view
     * @param varargs
     * @return
     */
    @Deprecated
    public LuaValue contentMode(U view, Varargs varargs) {
        return scaleType(view, varargs);
    }

    public LuaValue setContentMode(U view, Varargs varargs) {
        return setScaleType(view, varargs);
    }

    public LuaValue getContentMode(U view, Varargs varargs) {
        return getScaleType(view, varargs);
    }


    /**
     * 设置图片的缩放模式
     * TODO 跟iOS统一常
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue scaleType(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setScaleType(view, varargs);
        } else {
            return getScaleType(view, varargs);
        }
    }

    public LuaValue setScaleType(U view, Varargs varargs) {
        final String scaleTypeName = varargs.optjstring(2, ImageView.ScaleType.FIT_XY.name());//默认FIT_XY
        final ImageView.ScaleType scaleType = UDImageScaleType.parse(scaleTypeName);
        return view.setScaleType(scaleType);
    }

    public LuaValue getScaleType(U view, Varargs varargs) {
        return valueOf(view.getScaleType());
    }

    /**
     * 开始帧动画
     *
     * @param view
     * @param varargs 时间是秒而不是毫秒
     * @return
     */
    @Deprecated
    public LuaValue startAnimationImages(U view, Varargs varargs) {//TODO 支持UDImageView和UDBitmap
        final LuaTable imagesTable = varargs.opttable(2, null);
        final double duration = varargs.optdouble(3, 1f);
        boolean repeat = false;
        if (varargs.isnumber(4)) {
            repeat = varargs.optint(4, -1) > 0;
        } else {
            repeat = varargs.optboolean(4, false);
        }
        if (imagesTable != null && imagesTable.length() > 0) {
            final String[] images = new String[imagesTable.length()];
            int i = 0;
            for (LuaValue key : imagesTable.keys()) {
                images[i++] = imagesTable.get(key).optjstring(null);
            }
            return view.startAnimationImages(images, (int) duration * 1000, repeat);
        }
        return view;
    }

    /**
     * 停止帧动画
     *
     * @param view
     * @param varargs
     * @return
     */
    @Deprecated
    public LuaValue stopAnimationImages(U view, Varargs varargs) {
        return view.stopAnimationImages();
    }

    /**
     * 是否在动画中
     *
     * @param view
     * @param varargs
     * @return
     */
    @Deprecated
    public LuaValue isAnimationImages(U view, Varargs varargs) {
        return valueOf(view.isAnimationImages());
    }
}