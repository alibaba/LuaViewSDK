package com.taobao.luaview.util;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

/**
 * Asset 工具类
 * @author song
 * @date 15/11/17
 */
public class AssetUtil {

    /**
     * check asset exists
     *
     * @param context
     * @param assetFilePath
     * @return
     */
    public static boolean exists(final Context context, final String assetFilePath) {
        boolean bAssetOk = false;
        try {
            InputStream stream = context.getAssets().open(assetFilePath);
            stream.close();
            bAssetOk = true;
        } catch (Exception e) {
        }
        return bAssetOk;
    }

    /**
     * opean a file in assets path
     *
     * @param context
     * @param assetFilePath
     * @return
     */
    public static InputStream open(final Context context, final String assetFilePath) {
        try {
            return context.getAssets().open(assetFilePath);
        } catch (IOException e) {
            return null;
        }
    }

}
