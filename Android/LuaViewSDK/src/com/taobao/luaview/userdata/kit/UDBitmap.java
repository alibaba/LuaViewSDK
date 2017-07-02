/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.kit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.webkit.URLUtil;

import com.taobao.luaview.global.LuaResourceFinder;
import com.taobao.luaview.global.LuaView;
import com.taobao.luaview.provider.ImageProvider;
import com.taobao.luaview.scriptbundle.asynctask.SimpleTask1;
import com.taobao.luaview.scriptbundle.asynctask.SimpleTask2;
import com.taobao.luaview.userdata.base.BaseUserdata;
import com.taobao.luaview.util.LogUtil;
import com.taobao.luaview.util.LuaUtil;
import com.taobao.luaview.view.imageview.DrawableLoadCallback;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * Bitmap 用户数据封装，二进制数据
 *
 * @author song
 * @date 15/9/6
 */
public class UDBitmap extends BaseUserdata {
    private String mUrl;
    private LuaValue mCallback = null;
    private Bitmap mBitmap;

    public UDBitmap(Globals globals, LuaValue metatable, Varargs varargs) {
        super(globals, metatable, varargs);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        if (initParams != null) {
            LuaValue data = null;
            if (initParams.isuserdata(1)) {
                data = LuaUtil.getUserdata(initParams, 1);
            } else {
                mUrl = LuaUtil.getString(initParams, 1);
            }
            mCallback = LuaUtil.getFunction(initParams, 2);

            if (data != null) {
                fetchBitmapFromData(data);
            } else {
                fetchBitmapFromUrl(mUrl);
            }
        }
    }

    /**
     * fetch bitmap from data
     *
     * @param data
     */
    private void fetchBitmapFromData(LuaValue data) {
        if (data instanceof UDData) {
            new SimpleTask2<byte[], Bitmap>() {
                @Override
                protected Bitmap doInBackground(byte[]... params) {
                    byte[] bmpData = params != null && params.length > 0 ? params[0] : null;
                    if (bmpData != null) {
                        try {
                            return BitmapFactory.decodeByteArray(bmpData, 0, bmpData.length);
                        } catch (Throwable e){
                            e.printStackTrace();
                            LogUtil.e("[LuaView-Error] Bitmap.fetchBitmapFromData OutOfMemoryError!");
                        }
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    mBitmap = bitmap;
                    LuaUtil.callFunction(mCallback, bitmap != null ? LuaValue.TRUE : LuaValue.FALSE);
                }
            }.executeInPool(((UDData) data).bytes());
        }
    }

    /**
     * fetch bitmap from url or local file
     *
     * @param urlOrName
     */
    private void fetchBitmapFromUrl(String urlOrName) {
        if (!TextUtils.isEmpty(urlOrName)) {
            if (URLUtil.isNetworkUrl(urlOrName)) {//network
                final ImageProvider provider = LuaView.getImageProvider();
                if (provider != null) {
                    provider.preload(getContext(), urlOrName, new DrawableLoadCallback() {
                        @Override
                        public void onLoadResult(Drawable result) {
                            mBitmap = result instanceof BitmapDrawable ? ((BitmapDrawable) result).getBitmap() : null;
                            callCallback(mBitmap);

                            if(mBitmap == null){
                                LogUtil.i("[LuaView-Info] UDBitmap load image failed");
                            }
                        }
                    });
                } else {
                    callCallback(null);
                }
            } else {
                if (getLuaResourceFinder() != null) {//异步加载图片
                    getLuaResourceFinder().findDrawable(urlOrName, new LuaResourceFinder.DrawableFindCallback() {
                        @Override
                        public void onStart(String urlOrPath) {
                        }

                        @Override
                        public void onFinish(Drawable drawable) {
                            mBitmap = drawable instanceof BitmapDrawable ? ((BitmapDrawable) drawable).getBitmap() : null;
                            callCallback(drawable);

                            if(mBitmap == null){
                                LogUtil.i("[LuaView-Info] UDBitmap load image failed");
                            }
                        }
                    });
                } else {
                    callCallback(null);
                }
            }
        } else {//设置null
            callCallback(true);
        }
    }

    /**
     * call callback
     *
     * @param result
     */
    private void callCallback(Object result) {
        if (mCallback != null) {//异步回调，需要checktag
            LuaUtil.callFunction(mCallback, result != null ? LuaValue.TRUE : LuaValue.FALSE);
        }
    }

    /**
     * 创建一个切片
     *
     * @param x
     * @param y
     * @param width
     * @param height
     * @param callback
     * @return
     */
    public LuaValue sprite(final int x, final int y, final int width, final int height, final LuaFunction callback) {
        if (callback != null) {
            new SimpleTask1<LuaValue>() {
                @Override
                protected LuaValue doInBackground(Object... params) {
                    return createSprite(x, y, width, height);
                }

                @Override
                protected void onPostExecute(LuaValue luaValue) {
                    LuaUtil.callFunction(callback, luaValue);
                }
            }.executeInPool();
            return LuaValue.NIL;
        } else {
            return createSprite(x, y, width, height);
        }
    }

    private LuaValue createSprite(final int x, final int y, final int width, final int height) {
        if (mBitmap != null) {
            UDBitmap bitmap = new UDBitmap(getGlobals(), getmetatable(), null);
            try {
                bitmap.mBitmap = Bitmap.createBitmap(mBitmap, x, y, width, height);
                return bitmap;
            } catch (Throwable e){
                e.printStackTrace();
                LogUtil.e("[LuaView-Error] Bitmap.createSprite failed", x, y, width, height, "Error!");
            }
        }
        return LuaValue.NIL;
    }

    public int getWidth() {
        return mBitmap != null ? mBitmap.getWidth() : 0;
    }

    public int getHeight() {
        return mBitmap != null ? mBitmap.getHeight() : 0;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public Drawable createDrawable() {
        if (getContext() != null) {
            return new BitmapDrawable(getContext().getResources(), mBitmap);
        }
        return null;
    }

}
