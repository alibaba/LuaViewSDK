package com.taobao.luaview.scriptbundle.asynctask;

import android.os.AsyncTask;

/**
 * 简单任务封装
 * @author song
 */
public abstract class SimpleTask extends AsyncTask<Object, Object, Object> {

    @Override
    protected Object doInBackground(Object... params) {
        doInBackground();
        return null;
    }

    public abstract void doInBackground();
}