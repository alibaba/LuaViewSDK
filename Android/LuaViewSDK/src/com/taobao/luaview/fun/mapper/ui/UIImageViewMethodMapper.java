package com.taobao.luaview.fun.mapper.ui;

import android.widget.ImageView;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.constants.UDImageScaleType;
import com.taobao.luaview.userdata.ui.UDImageView;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * Image 接口封装
 * @author song
 * @param <U>
 */
@LuaViewLib
public class UIImageViewMethodMapper<U extends UDImageView> extends UIViewMethodMapper<U> {

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
        final String url = varargs.optjstring(2, null);
        final LuaFunction callback = varargs.optfunction(3, null);
        return view.setImageUrl(url, callback);
    }

    public LuaValue getImage(U view, Varargs varargs) {
        return valueOf(view.getImageUrl());
    }


    /**
     * 设置图片的缩放模式
     *
     * @param view
     * @param varargs
     * @return
     */
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
    public LuaValue startAnimationImages(U view, Varargs varargs) {
        final LuaTable imagesTable = varargs.opttable(2, null);
        final double duration = varargs.optdouble(3, -1.0);
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
    public LuaValue isAnimationImages(U view, Varargs varargs) {
        return valueOf(view.isAnimationImages());
    }
}