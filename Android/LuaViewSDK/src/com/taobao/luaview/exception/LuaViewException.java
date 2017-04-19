/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.exception;

/**
 * LuaView错误
 *
 * @author song
 * @date 15/11/15
 */
public class LuaViewException extends Exception {
    private int mCode = 0;
    private String mMessage = null;

    public LuaViewException() {
        super();
    }

    public LuaViewException(Throwable throwable) {
        super(throwable);
    }

    public LuaViewException(int code) {
        super();
        this.mCode = code;
    }

    public LuaViewException(String message) {
        super(message);
    }

    public LuaViewException(int code, String message) {
        super(message);
        this.mCode = code;
    }
}
