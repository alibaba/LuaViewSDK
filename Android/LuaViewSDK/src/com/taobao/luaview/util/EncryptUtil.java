/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * hash manager
 *
 * @author song
 */
public class EncryptUtil {

    /**
     * sha256 encrypteion
     * @param s
     * @return
     */
    public static String sha256(String s) {
        return encrypt(s, "SHA-256");
    }

    /**
     * sha256 encrypteion
     * @param s
     * @return
     */
    public static byte[] sha256(byte[] s) {
        return encrypt(s, "SHA-256");
    }

    /**
     * rsa encryption
     *
     * @param s
     * @return
     */
    public static String rsa(String s) {
        return encrypt(s, "rsa");
    }

    /**
     * aes 256 encryption
     *
     * @param s
     * @return
     */
    public static String aes256(String s) {
        return encrypt(s, "AES");
    }


    /**
     * md5
     *
     * @param s
     * @return
     */
    public static byte[] md5(byte[] s) {
        return encrypt(s, "MD5");
    }

    /**
     * md5
     *
     * @param s
     * @return
     */
    public static String md5(String s) {
        return encrypt(s, "MD5");
    }

    /**
     * md5
     *
     * @param s
     * @return
     */
    public static String md5Hex(String s) {
        return toHexString(encrypt(s, "MD5").getBytes());
    }


    /**
     * SHA1 algorithm
     */
    public static String sha1(String s) {
        return encrypt(s, "SHA-1");
    }

    /**
     * encrypt method
     *
     * @param s
     * @param method encrypt type
     * @return
     */
    private static String encrypt(String s, String method) {
        try {
            MessageDigest digest = MessageDigest.getInstance(method);
            digest.update(s.getBytes());
            return new String(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * encrypt method
     *
     * @param s
     * @param method encrypt type
     * @return
     */
    private static byte[] encrypt(byte[] s, String method) {
        try {
            MessageDigest digest = MessageDigest.getInstance(method);
            digest.update(s);
            return digest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * to hex string
     *
     * @param keyData
     * @return
     */
    private static String toHexString(byte[] keyData) {
        if (keyData == null) {
            return null;
        }
        int expectedStringLen = keyData.length * 2;
        StringBuilder sb = new StringBuilder(expectedStringLen);
        for (int i = 0; i < keyData.length; i++) {
            String hexStr = Integer.toString(keyData[i] & 0x00FF, 16);
            if (hexStr.length() == 1) {
                hexStr = "0" + hexStr;
            }
            sb.append(hexStr);
        }
        return sb.toString();

    }
}
