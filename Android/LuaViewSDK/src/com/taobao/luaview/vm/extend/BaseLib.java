/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.vm.extend;

import com.taobao.luaview.userdata.base.BaseLuaTable;
import com.taobao.luaview.userdata.base.BaseUserdata;
import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.util.LogUtil;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

/**
 * Base Lib extend for LuaJ
 * @author song
 * @date 16/2/22
 */
public class BaseLib {
    org.luaj.vm2.lib.BaseLib baseLib;
    Globals globals;

    public BaseLib(org.luaj.vm2.lib.BaseLib baseLib, Globals globals) {
        this.baseLib = baseLib;
        this.globals = globals;
    }

    public void extend(LuaValue env) {
        env.set("printLV", new printLV(baseLib));
		env.set("printLog", new printLog(baseLib));
    }

    // "print", // (...) -> void
    final class printLV extends VarArgFunction {
        final org.luaj.vm2.lib.BaseLib baselib;

        printLV(org.luaj.vm2.lib.BaseLib baselib) {
            this.baselib = baselib;
        }

        public Varargs invoke(Varargs args) {
            StringBuffer sb = new StringBuffer();
            for (int i = 1, n = args.narg(); i <= n; i++) {
                if (i > 1) {
                    sb.append('\t');
                }

                if (args.arg(i) instanceof UDView) {//基础的View对象

                } else if (args.arg(i) instanceof BaseUserdata) {//基础用户数据扩展

                } else if (args.arg(i) instanceof BaseLuaTable) {//基础Table表的扩展

                }

            }
            LogUtil.i(sb);
            return NONE;
        }
    }
	
	// "printLog(fileName, messsage)", send log info to LuaViewDebugger
	final class printLog extends TwoArgFunction {
		final org.luaj.vm2.lib.BaseLib baseLib;

		public log(org.luaj.vm2.lib.BaseLib baseLib) {
			this.baseLib = baseLib;
		}

		@Override public LuaValue call(LuaValue arg1, LuaValue arg2) {
			String fileName = arg1.checkjstring();
			if (globals.debugConnection != null) {
				globals.debugConnection.sendCmd("log", fileName, arg2.checkjstring());
			}
			LogUtil.i(arg2);
			return NONE;
		}
	}
}
