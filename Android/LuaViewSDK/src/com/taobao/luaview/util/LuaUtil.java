/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.util;

import com.taobao.luaview.userdata.kit.UDUnicode;
import com.taobao.luaview.userdata.ui.UDSpannableString;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaNumber;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lua脚本工具类
 *
 * @author song
 * @date 15/9/14
 */
public class LuaUtil {


    //-------------------------------------get value------------------------------------------------

    /**
     * 获取alpha
     *
     * @param varargs
     * @param poslist
     * @return
     */
    public static Integer getAlphaInt(final Varargs varargs, int... poslist) {
        return getAlphaInt(varargs, null, poslist);
    }

    public static Integer getAlphaInt(final Varargs varargs, Double defaultAlpha, int... poslist) {
        final Double alphaDouble = getDouble(varargs, poslist);
        if (alphaDouble != null) {
            return (int) (alphaDouble * 0xFF);
        } else if (defaultAlpha != null) {
            return (int) (defaultAlpha * 0xFF);
        } else {
            return null;
        }
    }

    /**
     * convert a LuaValue to Alpha int
     * @param value
     * @return
     */
    public static Integer toAlphaInt(final LuaValue value) {
        if (isNumber(value)) {
            final double dValue = value.optdouble(-1);
            if (dValue >= 0) {
                return (int) (dValue * 0xFF);
            }
        }
        return null;
    }

    /**
     * 获取时间，入参为秒(可以是小数)，输出为毫秒
     *
     * @param varargs
     * @param poslist
     * @return
     */
    public static Long getTimeLong(final Varargs varargs, int... poslist) {
        return getTimeLong(varargs, null, poslist);
    }

    /**
     * 获取时间，入参为秒(可以是小数)，输出为毫秒
     *
     * @param varargs
     * @param defaultSeconds
     * @param poslist
     * @return
     */
    public static Long getTimeLong(final Varargs varargs, Float defaultSeconds, int... poslist) {
        final Float timeOfFloat = getFloat(varargs, poslist);
        if (timeOfFloat != null) {
            return (long) (timeOfFloat * 1000);
        } else if (defaultSeconds != null) {
            return (long) (defaultSeconds * 1000);
        } else {
            return null;
        }
    }


    /**
     * 获取boolean
     *
     * @param varargs
     * @param poslist
     * @return
     */
    public static Boolean getBoolean(final Varargs varargs, int... poslist) {
        return (Boolean) getValue(LuaValue.TBOOLEAN, varargs, poslist);
    }

    public static Boolean getBoolean(final Varargs varargs, Boolean defaultValue, int... poslist) {
        return (Boolean) getValue(LuaValue.TBOOLEAN, varargs, defaultValue, poslist);
    }

    /**
     * 获取int
     *
     * @param varargs
     * @param poslist
     * @return
     */
    public static Integer getInt(final Varargs varargs, int... poslist) {
        final LuaNumber number = (LuaNumber) getValue(LuaValue.TNUMBER, varargs, poslist);
        return number != null ? number.checkint() : null;
    }

    /**
     * 获取long
     *
     * @param varargs
     * @param poslist
     * @return
     */
    public static Long getLong(final Varargs varargs, int... poslist) {
        final LuaNumber number = (LuaNumber) getValue(LuaValue.TNUMBER, varargs, poslist);
        return number != null ? number.checklong() : null;
    }

    public static Long getLong(final Varargs varargs, Long defaultValue, int... poslist) {
        final LuaNumber number = (LuaNumber) getValue(LuaValue.TNUMBER, varargs, defaultValue, poslist);
        return number != null ? number.checklong() : null;
    }

    /**
     * 获取double
     *
     * @param varargs
     * @param poslist
     * @return
     */
    public static Double getDouble(final Varargs varargs, int... poslist) {
        final LuaNumber number = (LuaNumber) getValue(LuaValue.TNUMBER, varargs, poslist);
        return number != null ? number.checkdouble() : null;
    }

    /**
     * 获取double
     *
     * @param varargs
     * @param poslist
     * @return
     */
    public static Double getDouble(final Varargs varargs, Double defaultValue, int... poslist) {
        final LuaNumber number = (LuaNumber) getValue(LuaValue.TNUMBER, varargs, poslist);
        return number != null ? number.checkdouble() : defaultValue;
    }

    /**
     * 获取float
     *
     * @param varargs
     * @param poslist
     * @return
     */
    public static Float getFloat(final Varargs varargs, int... poslist) {
        final LuaNumber number = (LuaNumber) getValue(LuaValue.TNUMBER, varargs, poslist);
        return number != null ? (float) number.checkdouble() : null;
    }

    /**
     * 获取float
     *
     * @param varargs
     * @param poslist
     * @return
     */
    public static Float getFloat(final Varargs varargs, Float defaultValue, int... poslist) {
        final LuaNumber number = (LuaNumber) getValue(LuaValue.TNUMBER, varargs, defaultValue, poslist);
        return number != null ? (float) number.checkdouble() : defaultValue;
    }


    /**
     * 获取string
     *
     * @param varargs
     * @param poslist
     * @return
     */
    public static String getString(final Varargs varargs, int... poslist) {
        return (String) getValue(LuaValue.TSTRING, varargs, poslist);
    }

    public static String getString(final LuaValue valueList, String... poslist) {
        return (String) getValueFromTable(LuaValue.TSTRING, valueList, poslist);
    }

    /**
     * 获取char sequence
     *
     * @param varargs
     * @param poslist
     * @return
     */
    public static CharSequence getText(final Varargs varargs, int... poslist) {
        LuaValue value = getValue(varargs, poslist);
        return toText(value);
    }

    /**
     * 获取char sequence
     *
     * @param values
     * @param poslist
     * @return
     */
    public static CharSequence getText(final LuaValue values, String... poslist) {
        LuaValue value = (LuaValue) getValueFromTable(LuaValue.TVALUE, values, poslist);
        return toText(value);
    }

    /**
     * 获取table
     *
     * @param varargs
     * @param poslist
     * @return
     */
    public static LuaTable getTable(final Varargs varargs, int... poslist) {
        return (LuaTable) getValue(LuaValue.TTABLE, varargs, poslist);
    }

    public static LuaTable getTable(final LuaValue valuelist, String... poslist) {
        return (LuaTable) getValueFromTable(LuaValue.TTABLE, valuelist, poslist);
    }

    /**
     * 获取function
     *
     * @param varargs
     * @param poslist
     * @return
     */
    public static LuaFunction getFunction(final Varargs varargs, int... poslist) {
        return (LuaFunction) getValue(LuaValue.TFUNCTION, varargs, poslist);
    }

    public static LuaFunction getFunction(final LuaValue valueList, String... keylist) {
        return (LuaFunction) getValueFromTable(LuaValue.TFUNCTION, valueList, keylist);
    }


    /**
     * 获取LuaValue
     *
     * @param varargs
     * @param poslist
     * @return
     */
    public static LuaValue getValue(final Varargs varargs, int... poslist) {
        return (LuaValue) getValue(LuaValue.TVALUE, varargs, poslist);
    }

    public static LuaValue getValue(final Varargs varargs, String... poslist) {
        return (LuaValue) getValueFromTable(LuaValue.TVALUE, varargs, poslist);
    }

    public static LuaValue getValue(final Varargs varargs, Varargs defaultValue, String... poslist) {
        return (LuaValue) getValueFromTable(LuaValue.TVALUE, varargs, defaultValue, poslist);
    }

    /**
     * 获取LuaValue by poslist name
     *
     * @param valuelist
     * @param poslist
     * @return
     */
    public static LuaValue getValue(final LuaValue valuelist, String... poslist) {
        return (LuaValue) getValueFromTable(LuaValue.TVALUE, valuelist, poslist);
    }

    public static LuaValue getValue(final LuaValue valuelist, LuaValue defaultValue, String... poslist) {
        return (LuaValue) getValueFromTable(LuaValue.TVALUE, defaultValue, valuelist, poslist);
    }

    /**
     * 获取userdata
     *
     * @param varargs
     * @param poslist
     * @return
     */
    public static LuaUserdata getUserdata(final Varargs varargs, int... poslist) {
        return (LuaUserdata) getValue(LuaValue.TUSERDATA, varargs, poslist);
    }

    /**
     * get value of given type, from varargs in position [poslist]
     *
     * @param type
     * @param varargs
     * @param poslist
     * @return
     */
    private static Object getValue(final int type, final Varargs varargs, int... poslist) {
        return getValue(type, varargs, null, poslist);
    }

    /**
     * get value of given type, from varargs in position [poslist]
     *
     * @param type
     * @param varargs
     * @param poslist
     * @return
     */
    private static Object getValue(final int type, final Varargs varargs, Object defaultValue, int... poslist) {
        Object result = null;
        if (varargs != null) {
            if (poslist != null && poslist.length > 0) {
                LuaValue value = null;
                for (int i = 0; i < poslist.length; i++) {
                    if (varargs.narg() >= poslist[i]) {
                        value = varargs.arg(poslist[i]);
                        result = parseValue(type, value);
                    }
                    if (result != null) {
                        break;
                    }
                }
            }
        }
        return result != null ? result : defaultValue;
    }


    /**
     * get value from table
     *
     * @param type
     * @param valueList
     * @param keylist
     * @return
     */
    private static Object getValueFromTable(final int type, final Varargs valueList, String... keylist) {
        return getValueFromTable(type, valueList, null, keylist);
    }

    /**
     * get value of given type, from varargs of key [poslist]
     *
     * @param type
     * @param varargs
     * @param keylist
     * @return
     */
    private static Object getValueFromTable(final int type, final Varargs varargs, Object defaultValue, String... keylist) {
        Object result = null;
        if (varargs instanceof LuaTable) {
            LuaTable varlist = ((LuaTable) varargs);
            if (keylist != null && keylist.length > 0) {
                for (int i = 0; i < keylist.length; i++) {
                    result = parseValue(type, varlist.get(keylist[i]));
                    if (result != null) {
                        break;
                    }
                }
            }
        }
        return result != null ? result : defaultValue;
    }

    /**
     * parse a value to given type
     *
     * @param type
     * @param value
     * @return
     */
    private static Object parseValue(int type, LuaValue value) {
        switch (type) {
            case LuaValue.TBOOLEAN:
                if (isBoolean(value)) return value.checkboolean();
                break;
            case LuaValue.TNUMBER:
                if (isNumber(value)) return value.checknumber();
                break;
            case LuaValue.TSTRING:
                if (isString(value)) return value.checkjstring();
                break;
            case LuaValue.TTABLE:
                if (isTable(value)) return value.checktable();
                break;
            case LuaValue.TFUNCTION:
                if (isFunction(value)) return value.checkfunction();
                break;
            case LuaValue.TUSERDATA:
                if (isUserdata(value)) return value.checkuserdata();
                break;
            case LuaValue.TVALUE:
                return value;
        }
        return null;
    }
    //---------------------------------type of value------------------------------------------------

    /**
     * is string
     *
     * @param target
     * @return
     */
    public static boolean isString(final LuaValue target) {
        return target != null && target.type() == LuaValue.TSTRING;
    }

    /**
     * is number
     *
     * @param target
     * @return
     */
    public static boolean isNumber(final LuaValue target) {
        return target != null && target.type() == LuaValue.TNUMBER;
    }

    /**
     * is boolean
     *
     * @param target
     * @return
     */
    public static boolean isBoolean(final LuaValue target) {
        return target != null && target.type() == LuaValue.TBOOLEAN;
    }

    /**
     * is function
     *
     * @param target
     * @return
     */
    public static boolean isFunction(final LuaValue target) {
        return target != null && target.type() == LuaValue.TFUNCTION;
    }

    /**
     * is table
     *
     * @param target
     * @return
     */
    public static boolean isTable(final LuaValue target) {
        return target != null && target.type() == LuaValue.TTABLE;
    }

    /**
     * is userdata
     *
     * @param target
     * @return
     */
    public static boolean isUserdata(final LuaValue target) {
        return target != null && target.type() == LuaValue.TUSERDATA;
    }

    /**
     * is nil
     *
     * @param target
     * @return
     */
    public static boolean isNil(final LuaValue target) {
        return target != null && target.type() == LuaValue.TNIL;
    }

    /**
     * is none
     *
     * @param target
     * @return
     */
    public static boolean isNone(final LuaValue target) {
        return target != null && target.type() == LuaValue.TNONE;
    }

    /**
     * 判断是否空或者nil
     *
     * @param target
     * @return
     */
    public static boolean isValid(final LuaValue target) {
        return target != null && target.type() != LuaValue.TNIL;
    }

    //------------------------------------function call---------------------------------------------

    /**
     * call lua function
     *
     * @param target
     * @return
     */
    public static LuaValue callFunction(LuaValue target) {
        try {
            return (target != null && target.isfunction()) ? target.call() : LuaValue.NIL;
        } catch (Exception e) {
            e.printStackTrace();
            return LuaValue.NIL;
        }
    }

    public static LuaValue callFunction(LuaValue target, LuaValue arg1) {
        try {
            return (target != null && target.isfunction()) ? target.call(arg1) : LuaValue.NIL;
        } catch (Exception e) {
            e.printStackTrace();
            return LuaValue.NIL;
        }
    }

    public static LuaValue callFunction(LuaValue target, LuaValue arg1, LuaValue arg2) {
        try {
            return (target != null && target.isfunction()) ? target.call(arg1, arg2) : LuaValue.NIL;
        } catch (Exception e) {
            e.printStackTrace();
            return LuaValue.NIL;
        }
    }

    public static LuaValue callFunction(LuaValue target, LuaValue arg1, LuaValue arg2, LuaValue arg3) {
        try {
            return (target != null && target.isfunction()) ? target.call(arg1, arg2, arg3) : LuaValue.NIL;
        } catch (Exception e) {
            e.printStackTrace();
            return LuaValue.NIL;
        }
    }

    public static Varargs callFunction(LuaValue target, Object... objs) {
        if (target != null && target.isfunction()) {
            LuaValue[] args = null;
            if (objs != null && objs.length > 0) {
                args = new LuaValue[objs.length];

                for (int i = 0; i < objs.length; i++) {
                    args[i] = CoerceJavaToLua.coerce(objs[i]);
                }
            }
            if (args != null) {
                return target.invoke(LuaValue.varargsOf(args));
            } else {
                return target.call();
            }
        }
        return LuaValue.NIL;
    }

    /**
     * call lua function ( return multiple values)
     *
     * @param target
     * @return
     */
    public static Varargs invokeFunction(LuaValue target) {
        try {
            return (target != null && target.isfunction()) ? target.invoke() : LuaValue.NIL;
        } catch (Exception e) {
            e.printStackTrace();
            return LuaValue.NIL;
        }
    }

    public static Varargs invokeFunction(LuaValue target, LuaValue arg1) {
        try {
            return (target != null && target.isfunction()) ? target.invoke(arg1) : LuaValue.NIL;
        } catch (Exception e) {
            e.printStackTrace();
            return LuaValue.NIL;
        }
    }

    public static Varargs invokeFunction(LuaValue target, LuaValue arg1, LuaValue arg2) {
        try {
            return (target != null && target.isfunction()) ? target.invoke(arg1, arg2) : LuaValue.NIL;
        } catch (Exception e) {
            e.printStackTrace();
            return LuaValue.NIL;
        }
    }

    public static Varargs invokeFunction(LuaValue target, LuaValue arg1, LuaValue arg2, LuaValue arg3) {
        try {
            return (target != null && target.isfunction()) ? target.invoke(new LuaValue[]{arg1, arg2, arg3}) : LuaValue.NIL;
        } catch (Exception e) {
            e.printStackTrace();
            return LuaValue.NIL;
        }
    }

    /**
     * 调用方法或者直接取数据
     *
     * @param target
     * @return
     */
    public static LuaValue getOrCallFunction(LuaValue target) {
        try {
            return (target != null && target.isfunction()) ? target.call() : target;
        } catch (Exception e) {
            e.printStackTrace();
            return LuaValue.NIL;
        }
    }

    public static LuaValue getOrCallFunction(LuaValue target, LuaValue arg1) {
        try {
            return (target != null && target.isfunction()) ? target.call(arg1) : target;
        } catch (Exception e) {
            e.printStackTrace();
            return LuaValue.NIL;
        }
    }

    public static LuaValue getOrCallFunction(LuaValue target, LuaValue arg1, LuaValue arg2) {
        try {
            return (target != null && target.isfunction()) ? target.call(arg1, arg2) : target;
        } catch (Exception e) {
            e.printStackTrace();
            return LuaValue.NIL;
        }
    }

    public static LuaValue getOrCallFunction(LuaValue target, LuaValue arg1, LuaValue arg2, LuaValue arg3) {
        try {
            return (target != null && target.isfunction()) ? target.call(arg1, arg2, arg3) : target;
        } catch (Exception e) {
            e.printStackTrace();
            return LuaValue.NIL;
        }
    }

    /**
     * lua 都是从1开始
     *
     * @param pos
     * @return
     */
    public static LuaValue toLuaInt(Integer pos) {
        return pos != null ? LuaValue.valueOf(pos + 1) : LuaValue.NIL;
    }

    public static LuaValue toLuaBoolean(boolean value) {
        return LuaValue.valueOf(value);
    }

    /**
     * java 从 0 开始
     *
     * @param pos
     * @return
     */
    public static int toJavaInt(LuaValue pos) {
        return pos.optint(1) - 1;
    }

    /**
     * convert a lua Int to java Int
     *
     * @param luaInt
     * @return
     */
    public static Integer toJavaInt(Integer luaInt) {
        return luaInt != null ? luaInt - 1 : null;
    }


    /**
     * convert params to LuaTable
     *
     * @param params
     * @return
     */
    public static LuaValue toTable(List<?> params) {
        if (params != null) {
            final LuaTable result = new LuaTable();
            if (params.size() > 0) {
                Object value = null;
                for (int i = 0; i < params.size(); i++) {
                    value = params.get(i);
                    result.set(i + 1, toLuaValue(value));
                }
            }
            return result;
        }
        return LuaValue.NIL;
    }

    /**
     * convert map to LuaTable
     *
     * @param params
     * @return
     */
    public static LuaValue toTable(Map<?, ?> params) {
        if (params != null) {
            final LuaTable result = new LuaTable();
            if (params.size() > 0) {
                Object valueObj = null;
                LuaValue key = null;
                for (final Object keyObj : params.keySet()) {
                    valueObj = params.get(keyObj);
                    key = toLuaValue(keyObj);
                    if (key != LuaValue.NIL) {
                        result.set(key, toLuaValue(valueObj));
                    }
                }
            }
            return result;
        }
        return LuaValue.NIL;
    }

    /**
     * convert luavalue to charsequence
     *
     * @param luaValue
     * @return
     */
    public static CharSequence toText(LuaValue luaValue) {
        if (LuaUtil.isString(luaValue)) {
            return luaValue.optjstring(null);
        } else if (LuaUtil.isUserdata(luaValue)) {
            if (luaValue instanceof UDSpannableString) {
                return ((UDSpannableString) luaValue).getSpannableStringBuilder();
            } else if (luaValue instanceof UDUnicode) {
                return ((UDUnicode) luaValue).toString();
            }
        }
        return null;
    }

    /**
     * convert object to LuaValue
     *
     * @param value
     * @return
     */
    public static LuaValue toLuaValue(Object value) {
        try {
            if (value instanceof Integer) {
                return LuaValue.valueOf((Integer) value);
            } else if (value instanceof Long) {
                return LuaValue.valueOf((Long) value);
            } else if (value instanceof Double) {
                return LuaValue.valueOf((Double) value);
            } else if (value instanceof String) {
                return LuaValue.valueOf((String) value);
            } else if (value instanceof Boolean) {
                return LuaValue.valueOf((Boolean) value);
            } else if (value instanceof byte[]) {
                return LuaValue.valueOf((byte[]) value);
            } else if (value instanceof List) {
                return toTable((List) value);
            } else if (value instanceof Map) {
                return toTable((Map) value);
            } else {
                return CoerceJavaToLua.coerce(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return LuaValue.NIL;
        }
    }

    /**
     * convert a table to map
     *
     * @param table
     * @return
     */
    public static Map<String, String> toMap(LuaTable table) {
        if (table != null) {
            final Map<String, String> result = new HashMap<String, String>();
            final LuaValue[] keys = table.keys();
            LuaValue value = null;
            for (LuaValue key : keys) {
                value = table.get(key);
                result.put(key.optjstring(null), value.optjstring(null));
            }
        }
        return null;
    }
}
