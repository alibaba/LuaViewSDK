package com.taobao.luaview.scriptbundle.asynctask;

import android.os.AsyncTask;

/**
 * 简单任务封装
 *
 * @author song
 */
public abstract class BaseAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    public void executeInPool(Params... values) {
        super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, values);
    }

    public void executeSerial(Params...values){
        super.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, values);
    }

}