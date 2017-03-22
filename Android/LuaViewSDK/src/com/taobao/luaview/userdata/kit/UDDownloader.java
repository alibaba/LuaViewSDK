/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.kit;

import android.text.TextUtils;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.base.BaseLuaTable;
import com.taobao.luaview.util.DateUtil;
import com.taobao.luaview.util.EncryptUtil;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

/**
 * 下载器 TODO
 *
 * @author song
 * @date 15/9/6
 */
@LuaViewLib(revisions = {"20170306已对标"})
@Deprecated
public class UDDownloader extends BaseLuaTable {

    public UDDownloader(Globals globals, LuaValue metatable) {
        super(globals, metatable);
        init();
    }

    private void init() {
        set("fetch", new fetch());
    }

    private String newFileName(String url) {
        return EncryptUtil.md5(url) + DateUtil.getCurrent(DateUtil.DATE_FORMAT_CN_24_SHORT_ENG);
    }

    /**
     * 下载内容
     */
    class fetch extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            final String url = args.optjstring(1, null);
            if (!TextUtils.isEmpty(url)) {
                final String fileName = args.optjstring(2, newFileName(url));
                final LuaFunction callback = args.optfunction(3, null);
                //TODO 下载器下载并保存
            }
            return UDDownloader.this;
        }
    }


}
