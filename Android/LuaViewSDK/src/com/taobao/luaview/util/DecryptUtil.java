/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.util;

import android.content.Context;

import com.taobao.luaview.cache.AppCache;
import com.taobao.luaview.global.Constants;

import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 解密类
 *
 * @author song
 * @date 15/11/10
 */
public class DecryptUtil {
    public static final String ALGORITHM_AES = "AES/CBC/PKCS5Padding";
    private static final String CACHE_PUBLIC_KEY = AppCache.CACHE_PUBLIC_KEY;
    public static final byte[] cIv = new byte[16];

    static {
        Arrays.fill(cIv, (byte) 0);
    }

    /**
     * 使用aes256进行解密
     *
     * @param encrypted
     * @return
     */
    public static byte[] aes(final Context context, final byte[] encrypted) {
        try {
            byte[] md5 = AppCache.getCache(CACHE_PUBLIC_KEY).get(Constants.PUBLIC_KEY_PATH_MD5);//get md5
            if (md5 == null) {
                byte[] keys = AppCache.getCache(CACHE_PUBLIC_KEY).get(Constants.PUBLIC_KEY_PATH);//get keys
                if (keys == null) {
                    final InputStream inputStream = context.getAssets().open(Constants.PUBLIC_KEY_PATH);
                    keys = IOUtil.toBytes(inputStream);
                    AppCache.getCache(CACHE_PUBLIC_KEY).put(Constants.PUBLIC_KEY_PATH, keys);//cache keys
                }
                md5 = EncryptUtil.md5(keys);
                AppCache.getCache(CACHE_PUBLIC_KEY).put(Constants.PUBLIC_KEY_PATH_MD5, md5);//cache md5
            }
            return aes(md5, encrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 使用aes256进行解密
     *
     * @param encrypted
     * @return
     */
    public static byte[] aes(final byte[] keys, final byte[] encrypted) {
        try {
            Cipher cipher = AppCache.getCache(CACHE_PUBLIC_KEY).get(Constants.PUBLIC_KEY_PATH_CIPHER);//get cipher
            if (cipher == null) {
                final SecretKeySpec skeySpec = new SecretKeySpec(keys, ALGORITHM_AES);
                final IvParameterSpec ivParameterSpec = new IvParameterSpec(cIv);
                cipher = Cipher.getInstance(ALGORITHM_AES);
                cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivParameterSpec);
                AppCache.getCache(CACHE_PUBLIC_KEY).put(Constants.PUBLIC_KEY_PATH_CIPHER, cipher);//cache cipher
            }
            return cipher.doFinal(encrypted);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
