package com.taobao.luaview.view.imageview;

import android.content.Context;

import com.taobao.luaview.global.LuaView;
import com.taobao.luaview.provider.ImageProvider;

/**
 * ImageView Impl
 *
 * @author song
 * @date 16/3/9
 */
public class LVBaseImageView extends BaseImageView {
    private String mUrl;

    public LVBaseImageView(Context context) {
        super(context);
    }

    @Override
    public void loadUrl(final String url, final LoadCallback callback) {
        this.mUrl = url;
        final ImageProvider provider = LuaView.getImageProvider();
        if (provider != null) {
            provider.load(this, url, callback);
        }
    }

    @Override
    public String getUrl() {
        return mUrl;
    }


}
