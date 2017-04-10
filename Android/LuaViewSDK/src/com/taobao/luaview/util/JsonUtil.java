/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.Iterator;

/**
 * Json 处理
 *
 * @author song
 * @date 15/9/6
 */
public class JsonUtil {

    /**
     * convert a lua table to data string
     *
     * @param table
     * @return
     */
    public static String toStringPlain(LuaTable table) {
        JSONObject obj = toJSONObject(table);
        return obj.toString();
    }

    /**
     * convert a lua table to data string
     *
     * @param table
     * @return
     */
    public static String toString(LuaTable table) {
        JSONObject obj = toJSONObject(table);
        try {
            return obj.toString(2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj.toString();
    }

    public static String toString(Object object) {
        if (object instanceof LuaTable) {
            return toString((LuaTable) object);
        }
        return LuaValue.NIL.toString();
    }

    public static JSONObject toJSONObject(LuaTable table) {
        JSONObject obj = new JSONObject();
        if (table != null) {
            LuaValue[] keys = table.keys();
            if (keys != null && keys.length > 0) {
                try {
                    for (int i = 0; i < keys.length; i++) {
                        String key = keys[i].optjstring("");
                        LuaValue value = table.get(keys[i]);
                        if (value instanceof LuaTable) {
                            obj.put(key, toJSONObject((LuaTable) value));
                        } else {
                            obj.put(key, value);
                        }
                    }
                } catch (JSONException e) {
                    LogUtil.e("[LuaView Error-toJSONObject]-Json Parse Failed, Reason: Invalid Format!", e);
                }
            }
        }
        return obj;
    }

    /**
     * 将JSONObject转成LuaTable
     *
     * @param obj
     * @return
     */
    public static LuaValue toLuaTable(JSONObject obj) {
        LuaValue result = LuaValue.NIL;

        if (obj != null) {
            result = new LuaTable();
            if (obj.length() > 0) {//只要不空，就创建一个table
                Iterator<String> iter = obj.keys();
                while (iter.hasNext()) {
                    final String key = iter.next();
                    final Object value = obj.opt(key);
                    result.set(key, toLuaValue(value));
                }
            }
        }
        return result;
    }

    /**
     * 将JSONObject转成LuaTable
     *
     * @param jsonString
     * @return
     */
    public static LuaValue toLuaTable(String jsonString) {
        LuaValue luaTable = LuaValue.NIL;
        try {
            luaTable = toLuaTable(new JSONObject(jsonString));
        } catch (Exception e) {
            try {
                luaTable = toLuaTable(new JSONArray(jsonString));
            } catch (JSONException ex1) {
                LogUtil.e("[LuaView Error-toLuaTable]-Json Parse Failed, Reason: Invalid Format!", ex1);
            }
        }
        return luaTable;
    }

    /**
     * 判断是否可以转成json
     *
     * @param jsonString
     * @return
     */
    public static boolean isJson(String jsonString) {
        try {
            new JSONObject(jsonString);
        } catch (JSONException ex) {
            try {
                new JSONArray(jsonString);
            } catch (JSONException ex1) {
                LogUtil.e("[LuaView Error-isJson]-Json Parse Failed, Reason: Invalid Format!", ex1);
                return false;
            }
        }
        return true;
    }

    /**
     * 将JSONObject转成LuaTable
     *
     * @param obj
     * @return
     */
    public static LuaValue toLuaTable(JSONArray obj) {
        LuaValue result = LuaValue.NIL;

        if (obj != null) {
            result = new LuaTable();//只要不空，就创建一个table
            if (obj.length() > 0) {
                for (int i = 0; i < obj.length(); i++) {
                    final int key = i + 1;
                    final Object value = obj.opt(i);
                    result.set(key, toLuaValue(value));
                }
            }
        }
        return result;
    }

    /**
     * convert a object to LuaValue
     *
     * @param value
     * @return
     */
    private static LuaValue toLuaValue(Object value) {
        if (value instanceof String) {
            return LuaValue.valueOf((String) value);
        } else if (value instanceof Integer) {
            return LuaValue.valueOf((Integer) value);
        } else if (value instanceof Long) {
            return LuaValue.valueOf((Long) value);
        } else if (value instanceof Double) {
            return LuaValue.valueOf((Double) value);
        } else if (value instanceof Boolean) {
            return LuaValue.valueOf((Boolean) value);
        } else if (value instanceof JSONObject) {
            return toLuaTable((JSONObject) value);
        } else if (value instanceof JSONArray) {
            return toLuaTable((JSONArray) value);
        } else {
            //TODO 不支持的类型
            return LuaValue.NIL;
        }
    }
}
