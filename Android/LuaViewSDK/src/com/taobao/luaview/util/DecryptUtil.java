package com.taobao.luaview.util;

import android.content.Context;

import com.taobao.luaview.global.Constants;

import java.io.IOException;
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
    public static final byte[] cIv = new byte[16];

    static {
        Arrays.fill(cIv, (byte)0);
    }

    /**
     * 使用aes256进行解密
     *
     * @param encrypted
     * @return
     */
    public static byte[] aes(final Context context, final byte[] encrypted) {
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open(Constants.PUBLIC_KEY_PATH);
            byte[] keys = IOUtil.toBytes(inputStream);
            return aes(EncryptUtil.md5(keys), encrypted);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            final SecretKeySpec skeySpec = new SecretKeySpec(keys, ALGORITHM_AES);
            final IvParameterSpec ivParameterSpec = new IvParameterSpec(cIv);
            final Cipher cipher = Cipher.getInstance(ALGORITHM_AES);

            cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivParameterSpec);

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
