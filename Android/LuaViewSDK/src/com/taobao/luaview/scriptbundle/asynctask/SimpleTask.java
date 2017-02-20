package com.taobao.luaview.scriptbundle.asynctask;

/**
 * 简单任务封装
 *
 * @author song
 */
public abstract class SimpleTask<Params> extends BaseAsyncTask<Params, Object, Object> {

    @Override
    protected Object doInBackground(Params... params) {
        doTask(params);
        return null;
    }

    public abstract void doTask(Params... params);
}