/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.kit;

import android.app.ActionBar;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.global.Constants;
import com.taobao.luaview.userdata.base.BaseLuaTable;
import com.taobao.luaview.userdata.ui.UDImageView;
import com.taobao.luaview.userdata.ui.UDSpannableString;
import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.util.ImageUtil;
import com.taobao.luaview.util.LuaViewUtil;
import com.taobao.luaview.view.imageview.BaseImageView;
import com.taobao.luaview.view.imageview.DrawableLoadCallback;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

/**
 * ActionBar 用户数据封装
 *
 * @author song
 * @date 15/9/6
 */
@LuaViewLib(revisions = {"20170306已对标"})
public class UDActionBar extends BaseLuaTable {

    public UDActionBar(Globals globals, LuaValue metatable) {
        super(globals, metatable);
        init();
    }

    private void init() {
        set("title", new title());
        set("setTitle", new setTitle());
        set("getTitle", new getTitle());
        set("background", new background());
        set("setBackground", new setBackground());
        set("getBackground", new getBackground());
        set("left", new left());
        set("leftBarButton", new left());//@Deprecated
        set("right", new right());
        set("rightBarButton", new right());//@Deprecated
    }

    /**
     * 系统中间View
     */
    class title extends VarArgFunction {

        @Override
        public Varargs invoke(Varargs args) {
            if (args.narg() > 1) {
                return new setTitle().invoke(args);
            } else {
                return new getTitle().invoke(args);
            }
        }
    }

    @Deprecated
    class setTitle extends VarArgFunction {

        @Override
        public Varargs invoke(Varargs args) {
            if (args.isstring(2) || args.optvalue(2, NIL) instanceof UDSpannableString) {//title
                final CharSequence title = LuaViewUtil.getText(args.optvalue(2, NIL));
                if (title != null) {
                    final ActionBar actionBar = LuaViewUtil.getActionBar(getGlobals());
                    if (actionBar != null) {
                        actionBar.setTitle(title);
                    } else {
//                        final android.support.v7.app.ActionBar supportActionBar = LuaViewUtil.getSupportActionBar(getGlobals());
//                        if (supportActionBar != null) {
//                            supportActionBar.setTitle(title);
//                        }
                    }
                }
            } else if (args.isuserdata(2)) {//view
                final LuaValue titleViewValue = args.optvalue(2, null);
                if (titleViewValue instanceof UDView) {
                    final ActionBar actionBar = LuaViewUtil.getActionBar(getGlobals());
                    if (actionBar != null) {
                        final View view = ((UDView) titleViewValue).getView();
                        if (view != null) {
                            view.setTag(Constants.RES_LV_TAG, titleViewValue);
                        }
                        actionBar.setDisplayShowCustomEnabled(true);
                        actionBar.setCustomView(LuaViewUtil.removeFromParent(view));

                    } else {
//                        final android.support.v7.app.ActionBar supportActionBar = LuaViewUtil.getSupportActionBar(getGlobals());
//                        if (supportActionBar != null) {
//                            final View view = ((UDView) titleViewValue).getView();
//                            if (view != null) {
//                                view.setTag(Constants.RES_LV_TAG, titleViewValue);
//                            }
//                            supportActionBar.setDisplayShowCustomEnabled(true);
//                            supportActionBar.setCustomView(LuaViewUtil.removeFromParent(view));
//                        }
                    }
                }
            }
            return UDActionBar.this;
        }
    }

    @Deprecated
    class getTitle extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            final ActionBar actionBar = LuaViewUtil.getActionBar(getGlobals());
            if (actionBar != null) {
                final CharSequence title = actionBar.getTitle();
                if (!TextUtils.isEmpty(title)) {
                    return valueOf(title.toString());
                } else {
                    final View view = actionBar.getCustomView();
                    if (view != null) {
                        final Object tag = view.getTag(Constants.RES_LV_TAG);
                        return tag instanceof LuaValue ? (LuaValue) tag : NIL;
                    }
                }
            }
            return NIL;
        }
    }


    /**
     * 背景
     */
    class background extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            if (args.narg() > 1) {
                return new setBackground().invoke(args);
            } else {
                return new getBackground().invoke(args);
            }
        }
    }

    @Deprecated
    class setBackground extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            if (args.isstring(2)) {
                ImageUtil.fetch(getContext(), getLuaResourceFinder(), args.optjstring(2, null), new DrawableLoadCallback() {
                    @Override
                    public void onLoadResult(Drawable drawable) {
                        setupActionBarDrawable(drawable);
                    }
                });
            } else if (args.isuserdata(2)) {//view
                final LuaValue data = args.optvalue(2, null);
                if (data instanceof UDImageView) {
                    final ImageView imageView = (ImageView) LuaViewUtil.removeFromParent(((UDImageView) data).getView());
                    if (imageView instanceof BaseImageView) {//TODO ActionBar支持gif
                        ImageUtil.fetch(getContext(), getLuaResourceFinder(), ((BaseImageView) imageView).getUrl(), new DrawableLoadCallback() {
                            @Override
                            public void onLoadResult(Drawable drawable) {
                                setupActionBarDrawable(drawable);
                            }
                        });
                    }
                } else if (data instanceof UDBitmap) {
                    setupActionBarDrawable(((UDBitmap) data).createDrawable());
                }
            }
            return UDActionBar.this;
        }

        private void setupActionBarDrawable(Drawable drawable) {
            if (drawable != null) {
                final ActionBar actionBar = LuaViewUtil.getActionBar(getGlobals());
                if (actionBar != null) {
                    actionBar.setBackgroundDrawable(drawable);
                }
            }
        }
    }

    @Deprecated
    class getBackground extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            //TODO
            return UDActionBar.this;
        }
    }

    /**
     * 左按钮
     */
    class left extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            if (args.narg() > 1) {
                return new setLeft().invoke(args);
            } else {
                return new getLeft().invoke(args);
            }
        }
    }

    class setLeft extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            final ActionBar actionBar = LuaViewUtil.getActionBar(getGlobals());
            if (actionBar != null) {
                final boolean showBack = args.optboolean(2, true);
                actionBar.setDisplayHomeAsUpEnabled(showBack);
            }
            return UDActionBar.this;
        }
    }

    class getLeft extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            return UDActionBar.this;
        }
    }

    /**
     * 右按钮
     */
    class right extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            if (args.narg() > 1) {
                return new setRight().invoke(args);
            } else {
                return new getRight().invoke(args);
            }
        }
    }

    class setRight extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            final ActionBar actionBar = LuaViewUtil.getActionBar(getGlobals());
            if (actionBar != null) {
            }
            return UDActionBar.this;
        }
    }

    class getRight extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            return UDActionBar.this;
        }
    }
}
