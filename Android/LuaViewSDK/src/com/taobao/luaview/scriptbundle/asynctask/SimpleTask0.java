package com.taobao.luaview.scriptbundle.asynctask;

/**
 * 简单任务封装
 *
 * @author song
 */
public abstract class SimpleTask0 extends BaseAsyncTask<Object, Object, Object> {

    @Override
    protected Object doInBackground(Object... params) {
        doTask();
        return null;
    }

    public abstract void doTask();
}