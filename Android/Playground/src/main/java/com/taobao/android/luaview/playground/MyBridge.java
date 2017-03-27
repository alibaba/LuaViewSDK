package com.taobao.android.luaview.playground;

import android.app.Activity;
import android.content.Intent;

import com.taobao.luaview.global.Constants;

/**
 * Created by tuoli on 17/3/27.
 */

public class MyBridge {

    private Activity mActivity;

    public MyBridge(Activity activity) {
        this.mActivity = activity;
    }

    public void jumpTo(String pageUri) {
        Intent intent = new Intent(mActivity, CommonActivity.class);
        intent.putExtra(Constants.PARAM_URI, pageUri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mActivity.startActivity(intent);
        mActivity.overridePendingTransition(R.anim.zoomin,R.anim.zoomout);
    }
}

