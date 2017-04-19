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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * 加解密验证
 *
 * @author song
 * @date 15/11/10
 */
public class VerifyUtil {
    private static final String TAG = "VerifyUtil";
    private static final String CACHE_PUBLIC_KEY = AppCache.CACHE_PUBLIC_KEY;

    public static final String ALGORITHM_RSA = "RSA";
    public static final String SIGNATURE_ALGORITHM = "SHA1WithRSA";// "MD5withRSA";
    private static final String DER_CERT_509 = "X.509";

    /**
     * 验证rsa
     *
     * @param content
     * @param sign
     * @return
     */
    public static boolean rsa(Context context, byte[] content, byte[] sign) {
        try {
            byte[] publicKeyFileData = AppCache.getCache(CACHE_PUBLIC_KEY).get(Constants.PUBLIC_KEY_PATH);
            if(publicKeyFileData == null) {
                final InputStream inputStream;inputStream = context.getAssets().open(Constants.PUBLIC_KEY_PATH);
                publicKeyFileData = IOUtil.toBytes(inputStream);
                AppCache.getCache(TAG).put(Constants.PUBLIC_KEY_PATH, publicKeyFileData);
            }
            return rsa(content, publicKeyFileData, sign);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 验证rsa
     *
     * @param content
     * @param publicKey
     * @param sign
     * @return
     */
    public static boolean rsa(byte[] content, byte[] publicKey, byte[] sign) {
        InputStream inputStream = null;
        try {
            PublicKey pk = AppCache.getCache(CACHE_PUBLIC_KEY).get(Constants.PUBLIC_KEY_PATH_PK);//get public key
            if(pk == null) {
                inputStream = new ByteArrayInputStream(publicKey);
                final CertificateFactory certFactory = CertificateFactory.getInstance(DER_CERT_509);
                final Certificate cert = certFactory.generateCertificate(inputStream);
                pk = cert.getPublicKey();
                AppCache.getCache(CACHE_PUBLIC_KEY).put(Constants.PUBLIC_KEY_PATH_PK, pk);//cache public key
            }
            final Signature sig = Signature.getInstance(SIGNATURE_ALGORITHM);
            sig.initVerify(pk);
            sig.update(content);
            if (sig.verify(sign)) {
                return true;
            } else {
                LogUtil.d(TAG, "Verification Error");
                return false;
            }
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
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
        return false;
    }

    /**
     * read private key
     *
     * @param filename
     * @return
     */
    public static PrivateKey generatePrivateKey(final String filename) {
        try {
            File f = new File(filename);
            FileInputStream fis = new FileInputStream(f);
            DataInputStream dis = new DataInputStream(fis);
            byte[] keyBytes = new byte[(int) f.length()];
            dis.readFully(keyBytes);
            dis.close();
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance(ALGORITHM_RSA);
            return kf.generatePrivate(spec);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * read public key
     *
     * @param filename
     * @return
     */
    private static PublicKey generatePublicKey(final String filename) {
        try {
            File f = new File(filename);
            FileInputStream fis = new FileInputStream(f);
            DataInputStream dis = new DataInputStream(fis);
            byte[] keyBytes = new byte[(int) f.length()];
            dis.readFully(keyBytes);
            dis.close();

            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance(ALGORITHM_RSA);
            return kf.generatePublic(spec);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 16 进制 to bytes
     *
     * @param s
     * @return
     */
    private static byte[] hexToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
