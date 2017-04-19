/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.kit;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.scriptbundle.asynctask.SimpleTask1;
import com.taobao.luaview.userdata.base.BaseLuaTable;
import com.taobao.luaview.util.JsonUtil;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

/**
 * Json 用户数据封装
 *
 * @author song
 * @date 15/9/6
 */
@LuaViewLib(revisions = {"20170306已对标", "iOS有toJson方法"})
public class UDJson extends BaseLuaTable {

    public UDJson(Globals globals, LuaValue metatable) {
        super(globals, metatable);
        init();
    }

    private void init() {
        set("toTable", new toTable());
        set("isValid", new isValid());
//        set("parse", new parse());
    }

    //is vaild
    class isValid extends VarArgFunction {

        @Override
        public LuaValue invoke(Varargs args) {
            final LuaValue target = args.arg(2);
            final LuaValue callback = LuaUtil.getFunction(args, 3);
            if(callback != null){//通过callback来处理
                new SimpleTask1<LuaValue>() {
                    @Override
                    protected LuaValue doInBackground(Object... params) {
                        return isValid(target);
                    }

                    @Override
                    protected void onPostExecute(LuaValue result) {
                        LuaUtil.callFunction(callback, result);
                    }
                }.executeInPool();
                return LuaValue.NIL;
            } else {
                return isValid(target);
            }
        }

        private LuaValue isValid(LuaValue target){
            if (target instanceof UDData) {
                return valueOf(JsonUtil.isJson(((UDData) target).toJson(UDData.DEFAULT_ENCODE)));
            } else if (LuaUtil.isString(target)) {
                return valueOf(JsonUtil.isJson(target.optjstring(null)));
            }
            return LuaValue.FALSE;
        }
    }


    //to table
    class toTable extends VarArgFunction {

        @Override
        public Varargs invoke(Varargs args) {
            final LuaValue target = args.arg(2);
            final LuaValue callback = LuaUtil.getFunction(args, 3);
            if (callback != null) {//通过callback来处理toTable
                new SimpleTask1<LuaValue>() {
                    @Override
                    protected LuaValue doInBackground(Object... params) {
                        return toTable(target);
                    }

                    @Override
                    protected void onPostExecute(LuaValue result) {
                        LuaUtil.callFunction(callback, result);
                    }
                }.executeInPool();
                return NIL;
            } else {
                return toTable(target);
            }
        }

        private LuaValue toTable(LuaValue target) {
            if (target instanceof UDData) {
                return ((UDData) target).toTable(UDData.DEFAULT_ENCODE);
            } else if (LuaUtil.isString(target)) {
                return JsonUtil.toLuaTable(target.optjstring(null));
            } else if (LuaUtil.isTable(target)) {
                return target;
            }
            return NIL;
        }
    }


}
