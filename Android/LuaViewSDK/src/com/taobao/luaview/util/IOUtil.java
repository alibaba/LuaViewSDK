/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.util;

import com.taobao.luaview.userdata.kit.UDData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * IO 处理
 *
 * @author song
 * @date 15/11/10
 */
public class IOUtil {
    public static final int BUFFER_SIZE = 8 * 1024; //8k

    /**
     * convert a inputstream to bytes
     * TODO 这里会改变input stream，应该不能改变input
     *
     * @param input
     * @return
     */
    public static byte[] toBytes(final InputStream input) {
//        DebugUtil.tsi("luaviewp-toBytes");
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
//            DebugUtil.tei("luaviewp-toBytes");
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * convert input stream of inputEncoding to stream of DEFAULT_ENCODE
     *
     * @param input
     * @param inputEncoding
     * @return
     */
    public static byte[] toBytes(final InputStream input, final String inputEncoding) {
        byte[] result = toBytes(input);
        if (result != null && inputEncoding != null && !UDData.DEFAULT_ENCODE.equalsIgnoreCase(inputEncoding)) {
            try {
                return new String(result, inputEncoding).getBytes(UDData.DEFAULT_ENCODE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * read input stream to bytes
     *
     * @param input
     * @param length
     * @return
     */
    public static byte[] toBytes(final InputStream input, int length) {
        if (length > 0) {
            byte[] bytes = new byte[length];
            int count;
            int pos = 0;
            try {
                while (pos < length && ((count = input.read(bytes, pos, length - pos)) != -1)) {
                    pos += count;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (pos != length) {
                return null;
            }
            return bytes;
        } else {
            return toBytes(input);
        }
    }

    /**
     * read input stream to bytes
     *
     * @param input
     * @param length
     * @param inputEncoding
     * @return
     */
    public static byte[] toBytes(final InputStream input, int length, final String inputEncoding) {
        byte[] result = toBytes(input, length);
        if (result != null && inputEncoding != null && !UDData.DEFAULT_ENCODE.equalsIgnoreCase(inputEncoding)) {
            try {
                return new String(result, inputEncoding).getBytes(UDData.DEFAULT_ENCODE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
