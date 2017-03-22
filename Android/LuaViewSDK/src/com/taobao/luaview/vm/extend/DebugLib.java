/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.vm.extend;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

/**
 * LuaJ DebugLib的扩展功能
 *
 * @author song
 * @date 16/2/22
 */
public class DebugLib {

    org.luaj.vm2.lib.DebugLib mDebugLib;
    Globals globals;

    public DebugLib(org.luaj.vm2.lib.DebugLib debugLib, Globals globals){
        this.mDebugLib = debugLib;
        this.globals = globals;
    }

    public void extend(LuaTable debug){
        debug.set("readCmd", new readCmd());
        debug.set("sleep", new sleep());
        debug.set("printToServer", new printToServer());
        debug.set("runningLine", new runningLine());
        debug.set("get_file_line", new get_file_line());
    }


    //----------------------------------------------------------------------------------------------

    final class readCmd extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            if (globals.debugConnection != null) {
                String cmd = globals.debugConnection.reciveCMD();
                return cmd != null ? valueOf(cmd) : NIL;
            } else {
                return NIL;
            }
        }
    }

    final class sleep extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            try {
                Thread.sleep(arg.checklong());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return NIL;
        }
    }

    final class printToServer extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            if (globals.debugConnection != null) {
                globals.debugConnection.sendingEnabled = arg.optboolean(false);
            }
            return NIL;
        }
    }

    final class runningLine extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            String fileName = arg1.checkjstring(1);
            String lineNumber = String.valueOf(arg2.checkint());

            if (globals.debugConnection != null) {
                globals.debugConnection.sendCmd("running", fileName, lineNumber.getBytes());
            }

            return NIL;
        }
    }

    final class get_file_line extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            return NIL;
        }
    }

    //----------------------------------------------------------------------------------------------

}
