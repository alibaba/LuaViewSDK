package com.taobao.luaview.scriptbundle.asynctask;

import android.os.AsyncTask;

/**
 * 带输入输出参数的简单任务，带输入&输出的简单封装
 * @author song
 */
public abstract class SimpleTask2<P extends Object, R extends Object> extends AsyncTask<P, Integer, R> {

    @Override
    protected void onPostExecute(R result) {
    }

}