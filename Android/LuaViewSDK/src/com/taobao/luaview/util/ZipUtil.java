/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Zip操作
 *
 * @author song
 * @date 15/11/9
 */
public class ZipUtil {
    private static final int BUFFER_SIZE = 1024 * 32;

    /**
     * 将原始数据压缩到zip中
     *
     * @param rawData
     * @return
     */
    public static byte[] zip(final byte[] rawData) {
        ByteArrayOutputStream os = null;
        GZIPOutputStream gos = null;
        try {
            os = new ByteArrayOutputStream(rawData.length);
            gos = new GZIPOutputStream(os);
            gos.write(rawData);
            byte[] compressed = os.toByteArray();
            return compressed;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (gos != null) {
                    gos.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将zip过的数据解压缩出来
     *
     * @param compressed
     * @return
     */
    public static byte[] unzip(byte[] compressed) {
        ByteArrayInputStream is = null;
        GZIPInputStream gis = null;
        ByteArrayOutputStream out = null;
        try {
            is = new ByteArrayInputStream(compressed);
            gis = new GZIPInputStream(is, BUFFER_SIZE);
            out = new ByteArrayOutputStream();
            byte[] data = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = gis.read(data)) != -1) {
                out.write(data, 0, bytesRead);
            }
            out.flush();
            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (gis != null) {
                    gis.close();
                }
                if (is != null) {
                    is.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
