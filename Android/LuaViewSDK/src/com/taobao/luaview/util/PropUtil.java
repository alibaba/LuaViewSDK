/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.text.TextUtils;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * Properties util
 * @author song
 */
public class PropUtil {
    public PropUtil() {
    }

    @TargetApi(9)
    public static Map<String, String> toMap(Properties properties) {
        HashMap result = new HashMap();
        if (properties != null && properties.size() > 0) {
            Iterator i$ = properties.stringPropertyNames().iterator();

            while (i$.hasNext()) {
                String name = (String) i$.next();
                result.put(name, properties.getProperty(name));
            }
        }

        return result;
    }

    @TargetApi(9)
    public static String toArray(Properties properties) {
        if (properties != null && properties.size() > 0) {
            String[] result = new String[properties.size()];
            int index = 0;
            properties.toString();

            String key;
            for (Iterator i$ = properties.stringPropertyNames().iterator(); i$.hasNext(); result[index++] = key + properties.getProperty(key)) {
                key = (String) i$.next();
            }
        }

        return null;
    }

    public static Properties buildProps(String[] types, String[] values) {
        Properties prop = new Properties();
        if (types != null && values != null) {
            int len1 = types.length;
            int len2 = values.length;
            int maxLen = Math.max(len1, len2);

            for (int i = 0; i < maxLen; ++i) {
                if (!TextUtils.isEmpty(types[i]) && !TextUtils.isEmpty(values[i])) {
                    prop.put(types[i], values[i]);
                }
            }
        }

        return prop;
    }

    @TargetApi(9)
    public static Properties loadConfig(Context context, InputStream input) {
        Properties properties = new Properties();

        try {
            properties.load(new InputStreamReader(input, "UTF-8"));
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return properties;
    }

    public static void saveConfig(Context context, String file, Properties properties) {
        try {
            FileOutputStream e = new FileOutputStream(file, false);
            properties.store(e, "");
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }
}
