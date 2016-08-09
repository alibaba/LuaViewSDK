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
    private static final int BUFFER_SIZE = 1024 * 8; //8Byte

    /**
     * convert a inputstream to bytes
     * TODO 这里会改变input stream，应该不能改变input
     *
     * @param input
     * @return
     */
    public static byte[] toBytes(final InputStream input) {
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
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static byte[] toBytes(final InputStream input, final String inputEncoding) {
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;

        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            byte[] result = output.toByteArray();
            if (inputEncoding != null && !UDData.DEFAULT_ENCODE.equalsIgnoreCase(inputEncoding)) {
                final String resultStr = new String(result, inputEncoding);
                return resultStr.getBytes(UDData.DEFAULT_ENCODE);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
