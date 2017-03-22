/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.List;


public final class ChannelTools {

    /**
     * read to
     *
     * @param buffer
     * @param offset
     * @param size
     * @return
     */
    public static byte[] toBytes(MappedByteBuffer buffer, int offset, int size) {
        if (buffer != null && offset >= 0 && size > 0) {
            byte[] result = new byte[size];
            buffer.get(result);
            return result;
        }
        return null;
    }

    /**
     * file path to
     *
     * @param filepath
     * @param sizes
     * @return
     */
    public static List<byte[]> toBytes(String filepath, int[] sizes) {
        List<byte[]> result = new ArrayList<byte[]>();
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(filepath, "r");
            MappedByteBuffer buffer = randomAccessFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, randomAccessFile.length());

            if (sizes != null && sizes.length > 0) {
                for (int size : sizes) {
                    byte[] r = new byte[size];
                    buffer.get(r);//fill buffer
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * copy a input stream to a byte[]
     *
     * @param inputStream
     * @return
     */
    public static byte[] toBytes(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            fastCopy(inputStream, outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * fast copy
     *
     * @param inputStream
     * @param outputStream
     * @throws IOException
     */
    public static void fastCopy(InputStream inputStream, OutputStream outputStream) throws IOException {
        final ReadableByteChannel input = Channels.newChannel(inputStream);
        final WritableByteChannel output = Channels.newChannel(outputStream);
        fastCopy(input, output);
    }

    /**
     * copy
     *
     * @param src
     * @param dest
     * @throws IOException
     */
    public static void fastCopy(final ReadableByteChannel src, final WritableByteChannel dest) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(8 * 1024);
        int count = 0;

        while ((count = src.read(buffer)) != -1) {
//            LogUtil.d("luaviewp-fastCopy", count, buffer.capacity(), buffer.remaining(), buffer.array().length);
            // prepare the buffer to be drained
            buffer.flip();
            // write to the channel, may block
            dest.write(buffer);
            // If partial transfer, shift remainder down
            // If buffer is empty, same as doing clear()
            buffer.compact();
        }
        // EOF will leave buffer in fill state
        buffer.flip();
        // make sure the buffer is fully drained.
        while (buffer.hasRemaining()) {
            dest.write(buffer);
        }
    }
}