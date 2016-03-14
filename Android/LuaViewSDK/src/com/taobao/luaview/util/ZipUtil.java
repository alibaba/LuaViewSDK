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
    private static final int BUFFER_SIZE = 1024 * 8;//8k

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
        try {
            is = new ByteArrayInputStream(compressed);
            gis = new GZIPInputStream(is, BUFFER_SIZE);
            StringBuilder sb = new StringBuilder();
            byte[] data = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = gis.read(data)) != -1) {
                sb.append(new String(data, 0, bytesRead));
            }
            return sb.toString().getBytes();
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
