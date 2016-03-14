package com.taobao.luaview.util;

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
    private static final int BUFFER_SIZE = 1024 * 8; //8k

    /**
     * convert a inputstream to bytes
     * TODO 这里会改变input stream，应该不能改变input
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
}
